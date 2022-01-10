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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tourme.Adapters.CommentAdapter;
import com.example.tourme.Model.Comment;
import com.example.tourme.Model.Oglas;
import com.example.tourme.Model.StaticVars;
import com.example.tourme.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class pregledJednogOglasa extends AppCompatActivity {

    //View
//    Spinner rating;
    Button sendMessage, buttonAddRating, tryAgainButton, editOglasButton, deleteOglasButton, backButton;
    ImageView profile_image;
    TextView ime, prezime, opis, grad, starost, cena, averageRatingText, numberOfRatingsText, username;
    EditText textForNewRating;
    RatingBar newRatingBar, averageRatingBar;
    View viewNoInternet, viewNoPage, viewThis, viewDodajOcenu;
    ProgressBar progressBar;

    //FireBase
    private DatabaseReference mDatabase;

    //Variables
    String newRatingText;
    String IDOglasa, nazivGrada, IDUser;
    String opisString, gradString, cenaString;
    Handler h = new Handler();
    RecyclerView recyclerView;
    CommentAdapter commentAdapter;
    int reasonForBadConnection = 1;
    boolean isGood = true;

    void setRatingTextError(String errorText){
        textForNewRating.setError(errorText);
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
                if(v1 != viewNoInternet && v1 != viewNoPage)
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
                if(v1 != viewNoInternet && v1 != viewNoPage)
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

    private void sendNotification1(String receiver){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap1 = new HashMap<>();
        hashMap1.put("to",receiver);
        hashMap1.put("title","Nova ocena");
        hashMap1.put("body","Dobili ste novu ocenu za oglas!");
        reference.child("notifications").push().setValue(hashMap1);

    }

    public void updateUser(){
        if(IsConnectedToInternet()){
            mDatabase.child("users").child(IDUser).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    User user = task.getResult().getValue(User.class);
                    Integer brojOcena = user.getBrojOcena() + 1;
                    double ocena = user.getUkupnaProsecnaOcena();
                    double doubleNewRating = (double)(newRatingBar.getRating());

                    ocena = (((double)brojOcena - 1) * ocena + doubleNewRating) / ((double)brojOcena);
                    ocena = Math.round(ocena * 10.0) / 10.0;
                    FirebaseDatabase.getInstance().getReference().child("users").child(IDUser).child("brojOcena").setValue(brojOcena);
                    FirebaseDatabase.getInstance().getReference().child("users").child(IDUser).child("ukupnaProsecnaOcena").setValue(ocena);

                    newRatingBar.setRating(0.0F);
                    textForNewRating.setText("");
                    Toast.makeText(pregledJednogOglasa.this, "Ocena je uspesno dodata", Toast.LENGTH_LONG).show();
                }
            });
        }else{
            HideWithReason(2);
        }
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
                            double doubleNewRating = (double)(newRatingBar.getRating());

                            ocena = (((double)brojOcena - 1) * ocena + doubleNewRating) / ((double)brojOcena);
                            ocena = Math.round(ocena * 10.0) / 10.0;
                            Comment comment = new Comment(doubleNewRating,newRatingText,FirebaseAuth.getInstance().getCurrentUser().getUid());

                            mDatabase.child("oglasi").child(IDOglasa).child("brojOcena").setValue(brojOcena);
                            mDatabase.child("oglasi").child(IDOglasa).child("ocena").setValue(ocena);
                            mDatabase.child("oglasi").child(IDOglasa).child("oceneOglasa").child(brojOcena.toString()).setValue(comment);
                            updateUser();
                            sendNotification1(oglas.getUserId());
                        }else{
                            Toast.makeText(pregledJednogOglasa.this, "ne postoji ovakav oglas", Toast.LENGTH_LONG).show();
                        }

                    }
                }
            });
        }else{
            HideWithReason(2);
        }

    }

    public void setupFireBase(){
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if(FirebaseAuth.getInstance().getUid().equals(IDUser)){
                deleteOglasButton.setVisibility(View.VISIBLE);
                editOglasButton.setVisibility(View.VISIBLE);
                sendMessage.setVisibility(View.GONE);
                viewDodajOcenu.setVisibility(View.GONE);
            }
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseDatabase.getInstance().getReference().child("users").child(IDUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user.getIme().equals("") || user.getPrezime().equals("")){
                    username.setVisibility(View.VISIBLE);
                    username.setText(user.getUsername());
                    ime.setVisibility(View.INVISIBLE);
                    prezime.setVisibility(View.INVISIBLE);
                }
                else{
                    ime.setText(user.getIme());
                    prezime.setText(user.getPrezime());
                    username.setVisibility(View.GONE);
                }
                String d1 = user.getDan();
                String m1 = user.getMesec();
                String g1 = user.getGodina();
                String d2 = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());
                String m2 = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());
                String g2 = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());

                if(StaticVars.numberOfYears(d1, StaticVars.convertMonth(m1), g1, d2, m2, g2) != 122) {
                    starost.setText(String.valueOf(StaticVars.numberOfYears(d1, StaticVars.convertMonth(m1), g1, d2, m2, g2)));
                }
                else{
                    starost.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("oglasi").child(IDOglasa).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Oglas oglas = snapshot.getValue(Oglas.class);
                FirebaseDatabase.getInstance().getReference("users").child(oglas.getUserId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        User user = snapshot.getValue(User.class);
                        if(oglas != null){
                            if (user.getImageurl().equals("default"))
                                profile_image.setImageResource(R.drawable.ic_profp);
                            else
                                Glide.with(getApplicationContext()).load(user.getImageurl()).into(profile_image);

                            profile_image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    openAccount(oglas.getUserId());
                                }
                            });

                            opisString = oglas.getOpis();
                            gradString = oglas.getGrad();
                            cenaString = String.valueOf(oglas.getCenaOglasa());

                            numberOfRatingsText.setText(String.valueOf(oglas.getBrojOcena()));
                            averageRatingText.setText(String.valueOf(oglas.getOcena()));
                            averageRatingBar.setRating((float) oglas.getOcena());

                            opis.setText(opisString);
                            grad.setText(gradString);
                            cena.setText(cenaString + "RSD");

                        }else{
                            HideEverything();
                            viewNoInternet.setVisibility(View.GONE);
                            viewNoPage.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

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
    }

    public boolean tryToStart(){
        if(IsConnectedToInternet()) {
            setupFireBase();
        }else{
            HideWithReason(1);
            return false;
        }
        return true;
    }

    public void setupView(){
        StaticVars.listOfFragments.add(10);

        IDOglasa = getIntent().getStringExtra("IDOglasa");
        nazivGrada = getIntent().getStringExtra("NazivGrada");
        IDUser = getIntent().getStringExtra("IDUser");

        viewThis = findViewById(R.id.PregledJednogOglasaActivity);
        viewNoInternet = (View) findViewById(R.id.nemaInternet);
        viewDodajOcenu = (View) findViewById(R.id.dodaj_ocenu);
        viewNoPage = (View) findViewById(R.id.nemaStranice);
        progressBar = viewNoInternet.findViewById(R.id.progressBar);

        editOglasButton = findViewById(R.id.editButton);
        editOglasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(pregledJednogOglasa.this, editOglas.class);
                i.putExtra("opis", opisString);
                i.putExtra("grad", gradString);
                i.putExtra("cena", cenaString);
                i.putExtra("id", IDOglasa);
                startActivity(i);
            }
        });

        backButton = viewNoPage.findViewById(R.id.backButttonNoPage);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        deleteOglasButton = findViewById(R.id.deleteThisButton);
        deleteOglasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(IsConnectedToInternet()){
                    mDatabase.child("users").child(IDUser).child("brojOglasa").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful())
                                HideWithReason(2);
                            else {
                                String numberOfOglas = String.valueOf(task.getResult().getValue());

                                if(!numberOfOglas.equals("null")){
                                    Integer intNumOfOglas = Integer.parseInt(numberOfOglas) - 1;
                                    String newNumberForOglas = intNumOfOglas.toString();

                                    mDatabase.child("users").child(IDUser).child("brojOglasa").setValue(newNumberForOglas);
                                    FirebaseDatabase.getInstance().getReference().child("oglasi").child(IDOglasa).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("users").child(IDUser).child("oglas").child(nazivGrada).removeValue();
                                    finish();

                                }else
                                    HideWithReason(2);
                            }
                        }
                    });
                }else{
                    HideWithReason(2);
                }
            }
        });


        profile_image = findViewById(R.id.profile_image);
        ime = findViewById(R.id.ime);
        prezime = findViewById(R.id.prezime);
        username = findViewById(R.id.username);
        opis = findViewById(R.id.opis);
        grad = findViewById(R.id.grad);
        cena = findViewById(R.id.cena2);
        textForNewRating = findViewById(R.id.textForRatingForOglas);
        starost = findViewById(R.id.godine);
        newRatingBar = findViewById(R.id.ratingForOglas);
        averageRatingBar = findViewById(R.id.ocena_bar);
        averageRatingText = findViewById(R.id.ocena);
        numberOfRatingsText = findViewById(R.id.ukupanBrojOcena);

        buttonAddRating = findViewById(R.id.addRatingButton);
        buttonAddRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGood = true;
                newRatingText = textForNewRating.getText().toString().trim();
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
        recyclerView.setLayoutManager(new LinearLayoutManager(pregledJednogOglasa.this));


        tryToStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregled_jednog_oglasa);

        setupView();
    }

    private void openAccount(String userid){
        Intent intent = new Intent(pregledJednogOglasa.this, Account.class);
        intent.putExtra("userid",userid);
        startActivity(intent);
    }

    private void startMessaging() {
        if(IsConnectedToInternet()) {
            FirebaseDatabase.getInstance().getReference("oglasi").child(IDOglasa).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    Oglas oglas = task.getResult().getValue(Oglas.class);

                    Intent i = new Intent(pregledJednogOglasa.this, MessageActivity.class);
                    i.putExtra("userid", oglas.getUserId());
                    i.putExtra("startedfrom","oglas");
                    startActivity(i);
                }
            });
        }else{
            HideWithReason(2);
        }

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