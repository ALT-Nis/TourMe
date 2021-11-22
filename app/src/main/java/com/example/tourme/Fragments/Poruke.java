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

import com.example.tourme.Model.User;
import com.example.tourme.R;
import com.example.tourme.Adapters.UserAdapater;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_poruke, container,false);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            recyclerView = view.findViewById(R.id.recycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            mUsers = new ArrayList<>();

            String userid = "OGO1vougHZNWZQtZSSmMIhLDgsF2";
        /*
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("id",userid);
            hashMap.put("username","yutopk");
            hashMap.put("imageurl","default");

            FirebaseDatabase.getInstance().getReference("users").child(userid).setValue(hashMap);
        */

        /*
        userAdapater = new UserAdapater( getContext(), mUsers);
        Log.e("VELICINA", "Velicina je: " + userAdapater.getItemCount());
        recyclerView.setAdapter(userAdapater);

        User user = new User(userid,"yutopk","default");
        mUsers.add(user);
        userAdapater.notifyItemInserted(mUsers.size() - 1);

         */

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mUsers.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);

                        assert user != null;
                        assert firebaseUser != null;
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            mUsers.add(user);
                            Log.e("KORISNIK", "Username je: " + user.getUsername());
                        }
                    }

                    userAdapater = new UserAdapater(getContext(), mUsers);
                    Log.e("VELICINA", "Velicina je: " + userAdapater.getItemCount());
                    recyclerView.setAdapter(userAdapater);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return view;
    }

}