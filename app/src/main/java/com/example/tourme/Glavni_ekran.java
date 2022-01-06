package com.example.tourme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.tourme.Fragments.Obavestenja;
import com.example.tourme.Fragments.Pocetni;
import com.example.tourme.Fragments.Poruke;
import com.example.tourme.Model.StaticVars;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Set;

public class Glavni_ekran extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //noviOglas

        /*
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

        navigationView.getMenu().findItem(R.id.mape).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(Glavni_ekran.this, mapaGradovi.class);
                startActivity(i);
                return false;
            }
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
            else{
                Intent i = new Intent(Glavni_ekran.this, Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }

            return true;
        });

        navigationView.getMenu().findItem(R.id.podesavanja).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(Glavni_ekran.this, Settings.class);
                startActivity(i);
                return false;
            }
        });

    */

        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            navigationView.getMenu().findItem(R.id.logInOut).setTitle("Prijavite se");
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.poruke:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView3, new Poruke()).commit();
                drawerLayout.closeDrawer(Gravity.RIGHT);
                break;
            case R.id.obavestenja:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView3, new Obavestenja()).commit();
                drawerLayout.closeDrawer(Gravity.RIGHT);
                break;
            case R.id.pocetni:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView3, new Pocetni()).commit();
                drawerLayout.closeDrawer(Gravity.RIGHT);
                break;
            case R.id.podesavanja:
                drawerLayout.openDrawer(Gravity.RIGHT);
                int len = StaticVars.listOfFragments.size();
                if(len >= 1)
                    return false;
                break;
            case R.id.noviOglas:
                drawerLayout.closeDrawer(Gravity.RIGHT);
                Intent i = new Intent(Glavni_ekran.this, dodajOglas.class);
                startActivity(i);
                break;
            case R.id.mojNalog:
                i = new Intent(Glavni_ekran.this, MyAccount.class);
                startActivity(i);
                break;
            case R.id.mape:
                i = new Intent(Glavni_ekran.this, mapaGradovi.class);
                startActivity(i);
                break;
            case R.id.logInOut:
                drawerLayout.closeDrawer(Gravity.RIGHT);
                if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                    status("offline");
                    FirebaseAuth.getInstance().signOut();
                    item.setTitle("Prijavite se");
                    Toast.makeText(Glavni_ekran.this, "Logged out", Toast.LENGTH_LONG).show();
                    i = new Intent(Glavni_ekran.this, Login.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
                else{
                    item.setTitle("Odjavite se");
                    i = new Intent(Glavni_ekran.this, Login.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
                break;
            case R.id.settings:
                i = new Intent(Glavni_ekran.this, Settings.class);
                startActivity(i);
                break;


        }

        return true;
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

