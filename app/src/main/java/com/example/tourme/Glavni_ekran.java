package com.example.tourme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Glavni_ekran extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentContainerView3);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        NavigationView navigationView = findViewById(R.id.nav_view);
        NavHostFragment navHostFragment1 =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentContainerView3);
        NavController navController1 = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(navigationView, navController);

        //noviOglas

        navigationView.getMenu().findItem(R.id.noviOglas).setOnMenuItemClickListener(menuItem -> {
                Intent i = new Intent(Glavni_ekran.this, dodajOglas.class);
                startActivity(i);
            return true;
        });

        navigationView.getMenu().findItem(R.id.mojNalog).setOnMenuItemClickListener(menuItem -> {
                Intent i = new Intent(Glavni_ekran.this, MyAccount.class);
                startActivity(i);
            return true;
        });

        navigationView.getMenu().findItem(R.id.logInOut).setOnMenuItemClickListener(menuItem -> {
            if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                status("offline");
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(Glavni_ekran.this, "Logged out", Toast.LENGTH_LONG).show();
                Intent i = new Intent(Glavni_ekran.this, Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }

            return true;
        });


    }

    private void status(String status){
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("status", status);

            reference.updateChildren(hashMap);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

}

