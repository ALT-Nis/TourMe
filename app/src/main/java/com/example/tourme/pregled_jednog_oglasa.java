package com.example.tourme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tourme.Adapters.CommentAdapter;
import com.example.tourme.Adapters.MessageAdapter;
import com.example.tourme.Adapters.OglasAdapter;
import com.example.tourme.Adapters.UserAdapater;
import com.example.tourme.Model.Chat;
import com.example.tourme.Model.Comment;
import com.example.tourme.Model.Gradovi;
import com.example.tourme.Model.Oglas;
import com.example.tourme.Model.Rating;
import com.example.tourme.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class pregled_jednog_oglasa extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner rating;

    String newRating, newRatingText;
    String IDOglasa, nazivGrada;

    Button buttonAddRating;
    Button sendMessage;

    ImageView profile_image;
    TextView username, opis, grad;
    EditText textForRating;

    View viewNoInternet, viewThis;
    ProgressBar progressBar;
    Button tryAgainButton;
    Handler h = new Handler();

    RecyclerView recyclerView;
    CommentAdapter commentAdapter;

    int reasonForBadConnection = 1;

    boolean isGood = true;

    private DatabaseReference mDatabase;

    void setRatingTextError(String errorText){
        textForRating.setError(errorText);
        isGood = false;
    }

    void hideProgressShowButton(){
        progressBar.setVisibility(View.GONE);
        tryAgainButton.setVisibility(View.VISIBLE);
    }
    void hideButtonShowProgress(){
        progressBar.setVisibility(View.VISIBLE);
        tryAgainButton.setVisibility(View.GONE);
    }

    private void HideEverythingRecursion(View v) {
        ViewGroup viewgroup=(ViewGroup)v;
        for (int i = 0 ;i < viewgroup.getChildCount(); i++) {
            View v1 = viewgroup.getChildAt(i);
            if (v1 instanceof ViewGroup){
                if(v1 != viewNoInternet)
                    HideEverythingRecursion(v1);
            }else
                v1.setVisibility(View.GONE);
        }
    }

    void HideEverything(){
        HideEverythingRecursion(viewThis);
        viewNoInternet.setVisibility(View.VISIBLE);
    }

    void HideWithReason(int reason){
        HideEverything();
        reasonForBadConnection = reason;
    }

    private void ShowEverythingRecursion(View v) {
        ViewGroup viewgroup=(ViewGroup)v;
        for (int i = 0 ;i < viewgroup.getChildCount(); i++) {
            View v1 = viewgroup.getChildAt(i);
            if (v1 instanceof ViewGroup){
                if(v1 != viewNoInternet)
                    ShowEverythingRecursion(v1);
            }else
                v1.setVisibility(View.VISIBLE);
        }
    }

    void ShowEverything(){
        ShowEverythingRecursion(viewThis);
        viewNoInternet.setVisibility(View.GONE);
    }

    Boolean IsConnectedToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    public void startAddingRating(){
        if(IsConnectedToInternet()){
            mDatabase.child("oglasi").child(IDOglasa).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        HideWithReason(2);
                    }
                    else {
                        Oglas oglas = task.getResult().getValue(Oglas.class);

                        if(oglas != null){
                            Integer brojOcena = oglas.getBrojOcena() + 1;
                            double ocena = oglas.getOcena();
                            double doubleNewRating = (double)(Integer.parseInt(newRating));

                            ocena = (((double)brojOcena - 1) * ocena + doubleNewRating) / ((double)brojOcena);
                            ocena = Math.round(ocena * 100.0) / 100.0;
                            Comment comment = new Comment(doubleNewRating,newRatingText,FirebaseAuth.getInstance().getCurrentUser().getUid());

                            mDatabase.child("oglasi").child(IDOglasa).child("brojOcena").setValue(brojOcena);
                            mDatabase.child("oglasi").child(IDOglasa).child("ocena").setValue(ocena);
                            mDatabase.child("oglasi").child(IDOglasa).child("oceneOglasa").child(brojOcena.toString()).setValue(comment);
                        }else{
                            Toast.makeText(pregled_jednog_oglasa.this, "ne postoji ovakav oglas", Toast.LENGTH_LONG).show();
                        }

                    }
                }
            });
        }else{
            HideWithReason(2);
        }

    }

    public boolean tryToStart(){
        if(IsConnectedToInternet()) {
            FirebaseDatabase.getInstance().getReference("oglasi").child(IDOglasa).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Oglas oglas = snapshot.getValue(Oglas.class);
                    username.setText(oglas.getUsername());
                    username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openAccount(oglas.getUserId());
                        }
                    });

                    opis.setText(oglas.getOpis());
                    grad.setText(oglas.getGrad());

                    if (oglas.getImageurl().equals("default"))
                        profile_image.setImageResource(R.mipmap.ic_launcher);
                    else
                        Glide.with(getApplicationContext()).load(oglas.getImageurl()).into(profile_image);

                    profile_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openAccount(oglas.getUserId());
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("oglasi").child(IDOglasa).child("oceneOglasa");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Comment> mComment = new ArrayList<>();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Comment comment = dataSnapshot.getValue(Comment.class);
                        mComment.add(comment);
                    }
                    commentAdapter = new CommentAdapter(getApplicationContext(), mComment);
                    recyclerView.setAdapter(commentAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else{
            HideWithReason(1);
            return false;
        }
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregled_jednog_oglasa);

        Gradovi.listOfFragments.add(10);

        IDOglasa = getIntent().getStringExtra("IDOglasa");
        nazivGrada = getIntent().getStringExtra("NazivGrada");

        viewThis = findViewById(R.id.PregledJednogOglasaActivity);
        viewNoInternet = (View) findViewById(R.id.nemaInternet);
        progressBar = viewNoInternet.findViewById(R.id.progressBar);

        rating = findViewById(R.id.ratingForOglas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.rating, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rating.setAdapter(adapter);
        rating.setOnItemSelectedListener(this);

        textForRating = findViewById(R.id.textForRatingForOglas);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        opis = findViewById(R.id.opis);
        grad = findViewById(R.id.grad);

        buttonAddRating = findViewById(R.id.addRatingButton);
        buttonAddRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGood = true;
                newRatingText = textForRating.getText().toString().trim();
                if(TextUtils.isEmpty(newRatingText)){
                    setRatingTextError("Dodajte opis ocene za ovaj oglas");
                }
                if(isGood){
                    startAddingRating();
                }
            }
        });

        tryAgainButton = viewNoInternet.findViewById(R.id.TryAgainButton);
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideButtonShowProgress();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressShowButton();
                        if(reasonForBadConnection == 1) {
                            if (tryToStart()) ShowEverything();
                        }else ShowEverything();
                    }
                }, 1000);
            }
        });

        sendMessage = findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMessaging();
            }
        });


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        tryToStart();
    }

    private void openAccount(String userid){
        Intent intent = new Intent(getApplicationContext(), Account.class);
        intent.putExtra("userid",userid);
        startActivity(intent);
    }

    private void startMessaging() {
        if(IsConnectedToInternet()) {
            FirebaseDatabase.getInstance().getReference("oglasi").child(IDOglasa).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    Oglas oglas = task.getResult().getValue(Oglas.class);

                    Intent i = new Intent(pregled_jednog_oglasa.this, MessageActivity.class);
                    i.putExtra("userid", oglas.getUserId());
                    startActivity(i);
                }
            });
        }else{
            HideWithReason(2);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        newRating = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void status(String status){
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("status", status);

            reference.updateChildren(hashMap);
        }

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