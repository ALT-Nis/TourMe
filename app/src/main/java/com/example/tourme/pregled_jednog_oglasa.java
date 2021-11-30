package com.example.tourme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class pregled_jednog_oglasa extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregled_jednog_oglasa);

        String IDOglasa = getIntent().getStringExtra("IDOglasa");
        textView = findViewById(R.id.idForOglas);
        textView.setText(IDOglasa);
    }
}