package com.example.tourme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tourme.Adapters.OglasAdapter;
import com.example.tourme.Model.Gradovi;
import com.example.tourme.Model.Oglas;
import com.example.tourme.Model.StaticVars;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class pregledOglasaGrad extends AppCompatActivity {

    RecyclerView recyclerView;

    DatabaseReference reference;

    OglasAdapter oglasAdapter;

    AutoCompleteTextView searchBar;
    Button searchButton;
    Gradovi g;
    List<String> items;

    View viewNoInternet, viewThis;
    ProgressBar progressBar;
    Button tryAgainButton;
    Handler h = new Handler();
    int reasonForBadConnection = 1;

    Spinner spinnerForSorting;
    ArrayAdapter<CharSequence> adapter;
    int sortingVariable = 0;

    View viewNoOglas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregled_oglasa_grad);

        StaticVars.listOfFragments.add(13);

        String grad = getIntent().getStringExtra("grad");

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("oglasi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Oglas> mOglas = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Oglas oglas = dataSnapshot.getValue(Oglas.class);
                    if(oglas.getGrad().equals(grad)) {
                        mOglas.add(oglas);
                    }
                }

                oglasAdapter = new OglasAdapter(pregledOglasaGrad.this, mOglas);
                recyclerView.setAdapter(oglasAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



}