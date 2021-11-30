package com.example.tourme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class pregledSvihSvojihOglasa extends AppCompatActivity {

    Button goToCheckOglas;
    Button goToAddOglas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregled_svih_svojih_oglasa);

        goToCheckOglas = findViewById(R.id.goToCheckOglas);
        goToAddOglas = findViewById(R.id.goToAddOglas);

        goToAddOglas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(pregledSvihSvojihOglasa.this, dodajOglas.class);
                startActivity(i);
            }
        });

        goToCheckOglas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(pregledSvihSvojihOglasa.this, pregled_jednog_oglasa.class);
                i.putExtra("IDOglasa", "23");
                startActivity(i);
            }
        });
    }
}