package com.example.tourme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class Glavni_ekran extends AppCompatActivity {

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
            if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                Intent i = new Intent(Glavni_ekran.this, pregledSvihSvojihOglasa.class);
                startActivity(i);
            }

            return true;
        });

        navigationView.getMenu().findItem(R.id.mojNalog).setOnMenuItemClickListener(menuItem -> {
            if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                Intent i = new Intent(Glavni_ekran.this, MyAccount.class);
                startActivity(i);
            }

            return true;
        });

        navigationView.getMenu().findItem(R.id.logInOut).setOnMenuItemClickListener(menuItem -> {
            if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(Glavni_ekran.this, "Logged out", Toast.LENGTH_LONG).show();
                Intent i = new Intent(Glavni_ekran.this, Login.class);
                startActivity(i);
            }

            return true;
        });


    }


}

