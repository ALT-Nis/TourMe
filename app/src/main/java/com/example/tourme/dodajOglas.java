package com.example.tourme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class dodajOglas extends AppCompatActivity {

    Button addOglas;
    private DatabaseReference mDatabase;
    FirebaseAuth fAuth;
    User user;

    public void continueAddingOglas(String userId, String grad, double ocena){

        mDatabase.child("users").child(userId).child("brojOglasa").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Firebase", "Error getting data", task.getException());
                }
                else {
                    String numberOfOglas = String.valueOf(task.getResult().getValue());

                    if(!numberOfOglas.equals("null")){
                        Oglas oglas = new Oglas(grad, ocena,user);
                        DatabaseReference ref = mDatabase.child("oglasi").push();
                        String idOglasa = ref.getKey();

                        Integer intNumOfOglas = Integer.parseInt(numberOfOglas) + 1;
                        String newNumberForOglas = intNumOfOglas.toString();

                        mDatabase.child("oglasi").child(idOglasa).setValue(oglas);
                        mDatabase.child("users").child(userId).child("brojOglasa").setValue(newNumberForOglas);
                        mDatabase.child("users").child(userId).child("oglas").child(grad).setValue(idOglasa);
                    }else{
                        Log.e("greska", "neka greska ali ne bi trebala da postoji");
                    }
                }
            }
        });

    }

    public void startAddingOglas(){
        String userId = fAuth.getCurrentUser().getUid();
        String grad = "Nis";
        double ocena = 3.5;

        mDatabase.child("users").child(userId).child("oglas").child(grad).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Firebase", "Error getting data", task.getException());
                }
                else {
                    String existsOglas = String.valueOf(task.getResult().getValue());

                    if(existsOglas.equals("null")){
                        continueAddingOglas(userId, grad, ocena);
                    }else{
                        Toast.makeText(dodajOglas.this, "vec postoji oglas sa ovim gradom", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_oglas);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        addOglas = findViewById(R.id.addOglas);

        fAuth = FirebaseAuth.getInstance();

        addOglas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fAuth.getCurrentUser()!=null) {
                    startAddingOglas();
                }
            }
        });
    }
}