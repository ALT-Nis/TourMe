package com.example.tourme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.tourme.Adapters.MessageAdapter;
import com.example.tourme.Model.Chat;
import com.example.tourme.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    ImageView profile_image;
    TextView username;

    FirebaseUser fUser;
    DatabaseReference reference;

    MessageAdapter messageAdapter;
    List<Chat> mChat;

    RecyclerView recyclerView;

    Intent intent;

    ImageButton button_send;
    EditText text_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        button_send = findViewById(R.id.button_send);
        text_send = findViewById(R.id.text_send);

        intent = getIntent();
        final String userid = intent.getStringExtra("userid");

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if(!msg.equals("")){
                    sendMessage(fUser.getUid(), userid, msg);
                }else{
                    Toast.makeText(MessageActivity.this, "Prazana poruka", Toast.LENGTH_LONG).show();
                }

                text_send.setText("");
            }
        });


        reference = FirebaseDatabase.getInstance().getReference("users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageurl().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }
                else{
                    Glide.with(MessageActivity.this).load(user.getImageurl()).into(profile_image);
                }

                readMessages(fUser.getUid(), userid, user.getImageurl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccount(userid);
            }
        });

    }

    private void openAccount(String userid){
        Intent intent = new Intent(MessageActivity.this, Account.class);
        intent.putExtra("userid",userid);
        startActivity(intent);
    }

    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);

        reference.child("chats").push().setValue(hashMap);
    }

    private void readMessages(String myid, String userid, String imageurl){
        mChat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    Log.e("CHAT", "R: " + chat.getReceiver());
                    Log.e("CHAT", "S: " + chat.getSender());
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mChat.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, mChat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                    recyclerView.scrollToPosition(mChat.size()-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(String status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }


}