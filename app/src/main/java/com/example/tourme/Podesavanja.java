package com.example.tourme;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Podesavanja#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Podesavanja extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Podesavanja() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Podesavanja.
     */
    // TODO: Rename and change types and number of parameters
    public static Podesavanja newInstance(String param1, String param2) {
        Podesavanja fragment = new Podesavanja();
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
    View view;

    @Override
    public void onStart() {
        super.onStart();
        TextView textView = view.findViewById(R.id.loggedin_text);
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser!=null){
            textView.setText("Logged in as: " +  fUser.getEmail());
        }
        else{
            textView.setText("Not logged in");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_podesavanja, container, false);
        Button dugme_login = view.findViewById(R.id.dugme_login);
        dugme_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Login.class);
                startActivity(i);
            }
        });

        Button dugme_register = view.findViewById(R.id.dugme_register);
        dugme_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Register.class);
                startActivity(i);
            }
        });



        return view;
    }
}