package com.example.tourme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tourme.Adapters.MessageAdapter;
import com.example.tourme.Adapters.OglasAdapter;
import com.example.tourme.Model.Chat;
import com.example.tourme.Model.Oglas;
import com.example.tourme.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Account extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    RecyclerView recyclerView;

    DatabaseReference reference;
    FirebaseUser firebaseUser;

    OglasAdapter oglasAdapter;
    List<Oglas> mOglas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        imageView = findViewById(R.id.profile_image);
        textView = findViewById(R.id.username);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(Account.this));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                textView.setText(user.getUsername());
                if(user.getImageurl().equals("default")){
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }
                else{
                    Glide.with(Account.this).load(user.getImageurl()).into(imageView);
                }

                showOglas(user.getId());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showOglas(String userid){
        mOglas = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("oglasi");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mOglas.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Oglas oglas = dataSnapshot.getValue(Oglas.class);
                    if(oglas.getUserId().equals(userid)){
                        mOglas.add(oglas);
                    }
                    oglasAdapter = new OglasAdapter(Account.this, mOglas);
                    recyclerView.setAdapter(oglasAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}