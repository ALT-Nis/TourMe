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
import android.widget.TextView;
import android.widget.Toast;

import com.example.tourme.Model.Oglas;
import com.example.tourme.Model.Rating;
import com.example.tourme.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class pregled_jednog_oglasa extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView textView;

    Spinner rating;

    String newRating, newRatingText;
    String IDOglasa;

    Button buttonAddRating;

    EditText textForRating;

    boolean isGood = true;

    private DatabaseReference mDatabase;

    void setRatingTextError(String errorText){
        textForRating.setError(errorText);
        isGood = false;
    }

    public void startAddingRating(){
        mDatabase.child("oglasi").child(IDOglasa).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Firebase", "Error getting data", task.getException());
                }
                else {
                    Oglas oglas = task.getResult().getValue(Oglas.class);

                    if(oglas != null){
                        Integer brojOcena = oglas.getBrojOcena() + 1;
                        double ocena = oglas.getOcena();
                        double doubleNewRating = (double)(Integer.parseInt(newRating));

                        ocena = (((double)brojOcena - 1) * ocena + doubleNewRating) / ((double)brojOcena);
                        ocena = Math.round(ocena * 100.0) / 100.0;
                        Rating r = new Rating(doubleNewRating, newRatingText);

                        mDatabase.child("oglasi").child(IDOglasa).child("brojOcena").setValue(brojOcena);
                        mDatabase.child("oglasi").child(IDOglasa).child("ocena").setValue(ocena);
                        mDatabase.child("oglasi").child(IDOglasa).child("oceneOglasa").child(brojOcena.toString()).setValue(r);
                    }else{
                        Toast.makeText(pregled_jednog_oglasa.this, "ne postoji ovakav oglas", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregled_jednog_oglasa);

        IDOglasa = getIntent().getStringExtra("IDOglasa");
        textView = findViewById(R.id.idForOglas);
        textView.setText(IDOglasa);

        rating = findViewById(R.id.ratingForOglas);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.rating, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rating.setAdapter(adapter);
        rating.setOnItemSelectedListener(this);

        textForRating = findViewById(R.id.textForRatingForOglas);

        mDatabase = FirebaseDatabase.getInstance().getReference();

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
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        newRating = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}