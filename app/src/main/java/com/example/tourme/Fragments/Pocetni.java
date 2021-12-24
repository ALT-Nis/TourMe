package com.example.tourme.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.graphics.PathParser;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tourme.Adapters.OglasAdapter;
import com.example.tourme.Model.Gradovi;
import com.example.tourme.Model.Oglas;
import com.example.tourme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Pocetni extends Fragment {

    RecyclerView recyclerView;

    DatabaseReference reference;
    FirebaseUser firebaseUser;

    OglasAdapter oglasAdapter;
    List<Oglas> mOglas;

    AutoCompleteTextView searchBar;
    Button searchButton;
    Gradovi g;
    List<String> items;

    public void search(){
        String inputText = searchBar.getText().toString().trim();
        if(TextUtils.isEmpty(inputText)){
            searchBar.setError("Unesite text");
        }else{
            reference.child("oglasi").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    mOglas.clear();
                    List<String> newItems  = g.Search(inputText);
                    items = newItems;

                    for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                        Oglas oglas = dataSnapshot.getValue(Oglas.class);
                        String gradOglasaa = oglas.getGrad();
                        if(newItems.contains(gradOglasaa.toLowerCase()))
                            mOglas.add(oglas);

                    }
                    oglasAdapter = new OglasAdapter(getContext(), mOglas);
                    recyclerView.setAdapter(oglasAdapter);
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pocetni, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mOglas = new ArrayList<>();
        oglasAdapter = new OglasAdapter(getContext(), mOglas);
        recyclerView.setAdapter(oglasAdapter);

        g = new Gradovi();

        //        String pocetniGrad = "Nis"; //ovo je grad koji se dobija na pocetku preko lokacija stavio sam za sad finsko Nis
        items = g.getAllCities();


        reference = FirebaseDatabase.getInstance().getReference();
        reference.child("oglasi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mOglas.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Oglas oglas = dataSnapshot.getValue(Oglas.class);
                    String gradOglasaa = oglas.getGrad();
                    if(items.contains(gradOglasaa.toLowerCase())) {
                        mOglas.add(oglas);
                        oglasAdapter = new OglasAdapter(getContext(), mOglas);
                        recyclerView.setAdapter(oglasAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchBar = view.findViewById(R.id.searchBar);
        searchBar.setError(null);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, g.getAllCitiesC());
        searchBar.setAdapter(adapter2);

        searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                search();
            }
        });

        searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });

        return view;
    }


}