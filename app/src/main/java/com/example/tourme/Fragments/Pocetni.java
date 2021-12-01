package com.example.tourme.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tourme.Account;
import com.example.tourme.Adapters.OglasAdapter;
import com.example.tourme.Adapters.UserAdapater;
import com.example.tourme.Model.Oglas;
import com.example.tourme.Model.User;
import com.example.tourme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Pocetni extends Fragment {

    RecyclerView recyclerView;

    DatabaseReference reference;
    FirebaseUser firebaseUser;

    OglasAdapter oglasAdapter;
    List<Oglas> mOglas;

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

        reference = FirebaseDatabase.getInstance().getReference("oglasi");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mOglas.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Oglas oglas = dataSnapshot.getValue(Oglas.class);
                    mOglas.add(oglas);
                    oglasAdapter = new OglasAdapter(getContext(), mOglas);
                    recyclerView.setAdapter(oglasAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }


}