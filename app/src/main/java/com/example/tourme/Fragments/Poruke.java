package com.example.tourme.Fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tourme.Login;
import com.example.tourme.Model.Chat;
import com.example.tourme.Model.User;
import com.example.tourme.R;
import com.example.tourme.Adapters.UserAdapater;
import com.example.tourme.Register;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Poruke#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Poruke extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Poruke() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Poruke.
     */
    // TODO: Rename and change types and number of parameters
    public static Poruke newInstance(String param1, String param2) {
        Poruke fragment = new Poruke();
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
    private UserAdapater userAdapater;

    private List<User> mUsers;
    private List<String> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poruke, container, false);

        //ovde se proverava da li je korisnik povezan
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {            recyclerView = view.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            mUsers = new ArrayList<>();

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("chats");

            usersList = new ArrayList<>();

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    usersList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Chat chat = dataSnapshot.getValue(Chat.class);

                        if(chat.getSender().equals(firebaseUser.getUid())){
                            usersList.add(chat.getReceiver());
                        }
                        if(chat.getReceiver().equals(firebaseUser.getUid())){
                            usersList.add(chat.getSender());
                        }
                    }

                    readChats();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });



        }
        else{
            view = inflater.inflate(R.layout.not_logged_in, container, false);
            Button dugme_login = view.findViewById(R.id.dugme_login);
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

    private void readChats(){
        mUsers = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);

                    if(usersList.contains(user.getId())){
                        mUsers.add(user);
                    }

                }
                userAdapater = new UserAdapater(getContext(), mUsers, true);
                recyclerView.setAdapter(userAdapater);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*
        userAdapater = new UserAdapater(getContext(), mUsers, true);
        Log.e("VELICINA", "Velicina je: " + userAdapater.getItemCount());
        recyclerView.setAdapter(userAdapater);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        */

    }

}