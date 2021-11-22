package com.example.tourme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.noviOglas){
                    Toast.makeText(Glavni_ekran.this,"Novi oglas",Toast.LENGTH_LONG).show();
                } else if(item.getItemId() == R.id.poruke){
                    Toast.makeText(Glavni_ekran.this,"Poruke",Toast.LENGTH_LONG).show();
                }else if(item.getItemId() == R.id.obavestenja){
                    Toast.makeText(Glavni_ekran.this,"Obavestenja",Toast.LENGTH_LONG).show();
                }else if(item.getItemId() == R.id.mojNalog){
                    Toast.makeText(Glavni_ekran.this,"Moj nalog",Toast.LENGTH_LONG).show();
                }else if(item.getItemId() == R.id.podesavanja){
                    Toast.makeText(Glavni_ekran.this,"Podesavanja",Toast.LENGTH_LONG).show();
                }else if(item.getItemId() == R.id.logInOut){
                    if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(Glavni_ekran.this, "Logged out", Toast.LENGTH_LONG).show();
                    }
                }
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawer(GravityCompat.END);
                return false;
            }
        });



    }
}