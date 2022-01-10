package com.example.tourme.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tourme.Adapters.NotificationAdapter;
import com.example.tourme.Adapters.UserAdapater;
import com.example.tourme.Login;
import com.example.tourme.Model.Gradovi;
import com.example.tourme.Model.Notification;
import com.example.tourme.Model.StaticVars;
import com.example.tourme.Model.User;
import com.example.tourme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Obavestenja#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Obavestenja extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Obavestenja() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Obavestenja.
     */
    // TODO: Rename and change types and number of parameters
    public static Obavestenja newInstance(String param1, String param2) {
        Obavestenja fragment = new Obavestenja();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_obavestenja, container, false);
        StaticVars.listOfFragments.add(3);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            recyclerView = view.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notifications");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<Notification> mNotification = new ArrayList<>();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Notification notification = dataSnapshot.getValue(Notification.class);

                        if(notification.getTo().equals(FirebaseAuth.getInstance().getUid())){
                            mNotification.add(notification);
                        }

                    }
                    Collections.reverse(mNotification);
                    notificationAdapter = new NotificationAdapter(getContext(), mNotification);
                    recyclerView.setAdapter(notificationAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        else{
            view = inflater.inflate(R.layout.not_logged_in, container, false);
            Button dugme_login = view.findViewById(R.id.goToLoginIfDidnt);
            dugme_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), Login.class);
                    startActivity(i);
                }
            });
        }
        return view;
    }
}