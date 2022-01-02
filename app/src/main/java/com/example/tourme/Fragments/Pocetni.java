package com.example.tourme.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.graphics.PathParser;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tourme.Adapters.OglasAdapter;
import com.example.tourme.Model.Gradovi;
import com.example.tourme.Model.Oglas;
import com.example.tourme.Model.User;
import com.example.tourme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

    OglasAdapter oglasAdapter;
    List<Oglas> mOglas;

    AutoCompleteTextView searchBar;
    Button searchButton;
    Gradovi g;
    List<String> items;


    View viewNoInternet, viewThis;
    ProgressBar progressBar;
    Button tryAgainButton;
    Handler h = new Handler();
    int reasonForBadConnection = 1;

    View viewNoOglas;

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
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    public void hideNoOglas(int len) {
        if(len == 0)
            viewNoOglas.setVisibility(View.VISIBLE);
        else
            viewNoOglas.setVisibility(View.GONE);
    }

    public void search(){
        String inputText = searchBar.getText().toString().trim();
        if(TextUtils.isEmpty(inputText)){
            searchBar.setError("Unesite text");
        }else{
            if(IsConnectedToInternet()){
                reference.child("oglasi").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        mOglas.clear();
                        List<String> newItems  = g.Search(inputText);
                        items = newItems;
                        Gradovi.lastSearch = inputText;

                        for(DataSnapshot dataSnapshot : task.getResult().getChildren()){
                            Oglas oglas = dataSnapshot.getValue(Oglas.class);
                            String gradOglasaa = oglas.getGrad();
                            if(newItems.contains(gradOglasaa.toLowerCase()))
                                mOglas.add(oglas);

                        }
                        hideNoOglas(mOglas.size());
                        oglasAdapter = new OglasAdapter(getContext(), mOglas);
                        recyclerView.setAdapter(oglasAdapter);
                    }
                });
            }else{
                HideWithReason(2);
            }
        }
    }

    public boolean tryToStart(){
        if(IsConnectedToInternet()){
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
                        }
                        hideNoOglas(mOglas.size());
                        oglasAdapter = new OglasAdapter(getContext(), mOglas);
                        recyclerView.setAdapter(oglasAdapter);
                    }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pocetni, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        viewThis = view.findViewById(R.id.pocetniFragment);
        viewNoInternet = (View) view.findViewById(R.id.nemaInternet);
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

        mOglas = new ArrayList<>();
        oglasAdapter = new OglasAdapter(getContext(), mOglas);
        recyclerView.setAdapter(oglasAdapter);

        viewNoOglas = (View) view.findViewById(R.id.nema_oglasa);

        g = new Gradovi();
        items = g.getAllCities();

        searchBar = view.findViewById(R.id.searchBar);
        searchBar.setError(null);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, g.getAllCitiesC());
        searchBar.setAdapter(adapter2);

        tryToStart();

        searchBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                search();
            }
        });

        if(!Gradovi.lastSearch.equals("")){
            String f = Gradovi.lastSearch;
            searchBar.setText(f);
            search();
        }

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