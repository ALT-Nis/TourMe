package com.example.tourme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tourme.Adapters.OglasAdapter;
import com.example.tourme.Model.Gradovi;
import com.example.tourme.Model.Oglas;
import com.example.tourme.Model.StaticVars;
import com.example.tourme.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Account extends AppCompatActivity {

    ImageView imageView;
    TextView username, ime, prezime, opis, godine, average;
    RatingBar averageBar;
    RecyclerView recyclerView;

    DatabaseReference reference;
    FirebaseUser firebaseUser;

    View viewNoInternet, viewThis;
    ProgressBar progressBar;
    Button tryAgainButton;
    Handler h = new Handler();
    int reasonForBadConnection = 1;

    OglasAdapter oglasAdapter;

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

    public boolean tryToStart(){
        if(IsConnectedToInternet()){
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String userid1 = getIntent().getStringExtra("userid");
            reference = FirebaseDatabase.getInstance().getReference("users").child(userid1);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    username.setText(user.getUsername());
                    ime.setText(user.getIme());
                    prezime.setText(user.getPrezime());
                    opis.setText(user.getOpis());
                    average.setText(String.valueOf(user.getUkupnaProsecnaOcena()));
                    averageBar.setRating((float) user.getUkupnaProsecnaOcena());
                    //treba da stoji i ukupan broj ocens
                    String d1 = user.getDan();
                    String m1 = user.getMesec();
                    String g1 = user.getGodina();
                    String d2 = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());
                    String m2 = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());
                    String g2 = new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date());
                    godine.setText(String.valueOf(StaticVars.numberOfYears(d1, StaticVars.convertMonth(m1), g1, d2, m2, g2)));
                    if(ime.getText().toString().trim().equals("") && prezime.getText().toString().trim().equals(""))
                        if(d1.equals("01") && m1.equals("Januar") && g1.equals("1900"))
                            godine.setText("");
                    if(user.getImageurl().equals("default")){
                        imageView.setImageResource(R.mipmap.ic_launcher);
                    }
                    else{
                        Glide.with(getApplicationContext()).load(user.getImageurl()).into(imageView);
                    }



                    showOglas(user.getId());

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
        setContentView(R.layout.activity_account);

        StaticVars.listOfFragments.add(5);

        imageView = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        ime = findViewById(R.id.ime);
        prezime = findViewById(R.id.prezime);
        opis = findViewById(R.id.kratakOpis);
        godine = findViewById(R.id.godine);
        average = findViewById(R.id.ocena);
        averageBar = findViewById(R.id.averageRatubgBar);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayout linearLayout = new LinearLayout(getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(Account.this));

        viewThis = findViewById(R.id.accountActivity);
        viewNoInternet = (View) findViewById(R.id.nemaInternet);
        progressBar = viewNoInternet.findViewById(R.id.progressBar);


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

        tryToStart();
    }
    private void showOglas(String userid){

        reference = FirebaseDatabase.getInstance().getReference("oglasi");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Oglas> mOglas = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Oglas oglas = dataSnapshot.getValue(Oglas.class);
                    if(oglas.getUserId().equals(userid)){
                        mOglas.add(oglas);
                    }
                    oglasAdapter = new OglasAdapter(getApplicationContext(), mOglas);
                    recyclerView.setAdapter(oglasAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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