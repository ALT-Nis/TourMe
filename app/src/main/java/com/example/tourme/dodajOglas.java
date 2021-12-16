package com.example.tourme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tourme.Model.Oglas;
import com.example.tourme.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class dodajOglas extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button addOglas;
    private DatabaseReference mDatabase;
    FirebaseAuth fAuth;

    Spinner city;

    EditText editTextForDescribe;

    boolean isGood = true;

    String textForDescribe;
    String cityText;


    void setDescribeError(String errorText){
        editTextForDescribe.setError(errorText);
        isGood = false;
    }

    public void finishAddingOglas(String userId, String imageurl, String username){

        mDatabase.child("users").child(userId).child("brojOglasa").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Firebase", "Error getting data", task.getException());
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
                        Log.e("greska", "neka greska ali ne bi trebala da postoji");
                    }
                }
            }
        });

    }

    public void continueAddingOglas(String userId, String imageurl, String username){
        mDatabase.child("users").child(userId).child("oglas").child(cityText).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Firebase", "Error getting data", task.getException());
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
    }

    public void startAddingOglas(){
        String userId = fAuth.getCurrentUser().getUid();

        mDatabase.child("users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Firebase", "Error getting data", task.getException());
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_oglas);

        city = findViewById(R.id.cityForOglas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adapter);
        city.setOnItemSelectedListener(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        addOglas = findViewById(R.id.addOglas);

        fAuth = FirebaseAuth.getInstance();

        editTextForDescribe = findViewById(R.id.describeOglas);

        addOglas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGood = true;

                if(fAuth.getCurrentUser()!=null) {
                    textForDescribe = editTextForDescribe.getText().toString().trim();
                    if(TextUtils.isEmpty(textForDescribe)){
                        setDescribeError("Unesite opis oglasa");
                    }
                    if(isGood)
                        startAddingOglas();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        cityText = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}