package com.example.tourme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tourme.Adapters.CommentAdapter;
import com.example.tourme.Model.Comment;
import com.example.tourme.Model.Oglas;
import com.example.tourme.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class dodajOglas extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button addOglas;
    private DatabaseReference mDatabase;
    FirebaseAuth fAuth;

    Spinner city;

    EditText editTextForDescribe;

    boolean isGood = true;

    String textForDescribe;
    String cityText;

    View viewNoInternet, viewThis;
    ProgressBar progressBar;
    Button tryAgainButton;
    Handler h = new Handler();
    int reasonForBadConnection = 1;

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
                if(v1 != viewNoInternet) {
                    ShowEverythingRecursion(v1);
                }
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

    void setDescribeError(String errorText){
        editTextForDescribe.setError(errorText);
        isGood = false;
    }

    public void finishAddingOglas(String userId, String imageurl, String username){
        if(IsConnectedToInternet()){
            mDatabase.child("users").child(userId).child("brojOglasa").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        HideWithReason(2);
                    }
                    else {
                        String numberOfOglas = String.valueOf(task.getResult().getValue());

                        if(!numberOfOglas.equals("null")){
                            DatabaseReference ref = mDatabase.child("oglasi").push();
                            String idOglasa = ref.getKey();
                            Oglas oglas = new Oglas(idOglasa, cityText, 0.0, 0, userId, textForDescribe, imageurl, username);

                            Integer intNumOfOglas = Integer.parseInt(numberOfOglas) + 1;
                            String newNumberForOglas = intNumOfOglas.toString();

                            mDatabase.child("oglasi").child(idOglasa).setValue(oglas);
                            mDatabase.child("users").child(userId).child("brojOglasa").setValue(newNumberForOglas);
                            mDatabase.child("users").child(userId).child("oglas").child(cityText).setValue(idOglasa);

                            Toast.makeText(dodajOglas.this, "Uspesno ste kreirali oglas", Toast.LENGTH_LONG).show();
                        }else{
                            HideWithReason(2);
                        }
                    }
                }
            });
        }else{
            HideWithReason(2);
        }

    }

    public void continueAddingOglas(String userId, String imageurl, String username){
        if(IsConnectedToInternet()){
            mDatabase.child("users").child(userId).child("oglas").child(cityText).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        HideWithReason(2);
                    }
                    else {
                        String existsOglas = String.valueOf(task.getResult().getValue());
                        if(existsOglas.equals("null")){
                            finishAddingOglas(userId, imageurl, username);
                        }else{
                            Toast.makeText(dodajOglas.this, "vec postoji oglas sa ovim gradom", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }else{
            HideWithReason(2);
        }
    }

    public void startAddingOglas(){
        if(IsConnectedToInternet()){
            String userId = fAuth.getCurrentUser().getUid();
            mDatabase.child("users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        HideWithReason(2);
                    }
                    else {
                        User user = task.getResult().getValue(User.class);

                        if(user != null){
                            String imageurl = user.getImageurl();
                            String username = user.getUsername();
                            continueAddingOglas(userId, imageurl, username);
                        }else{
                            Toast.makeText(dodajOglas.this, "ne postoji ovakav nalog", Toast.LENGTH_LONG).show();
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
            mDatabase = FirebaseDatabase.getInstance().getReference();
            fAuth = FirebaseAuth.getInstance();

        }else{
            HideWithReason(1);
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_oglas);

        viewThis = findViewById(R.id.dodajOglasActivity);
        viewNoInternet = (View) findViewById(R.id.nemaInternet);
        progressBar = viewNoInternet.findViewById(R.id.progressBar);

        city = findViewById(R.id.cityForOglas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adapter);
        city.setOnItemSelectedListener(this);

        addOglas = findViewById(R.id.addOglas);
        editTextForDescribe = findViewById(R.id.describeOglas);

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

        addOglas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(IsConnectedToInternet()){
                    isGood = true;

                    if(fAuth.getCurrentUser()!=null) {
                        textForDescribe = editTextForDescribe.getText().toString().trim();
                        if(TextUtils.isEmpty(textForDescribe)){
                            setDescribeError("Unesite opis oglasa");
                        }
                        if(isGood)
                            startAddingOglas();
                    }
                }else{
                    HideWithReason(2);
                }

            }
        });

        tryToStart();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        cityText = adapterView.getItemAtPosition(i).toString();
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