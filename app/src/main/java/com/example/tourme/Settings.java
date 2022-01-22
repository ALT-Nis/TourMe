package com.example.tourme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.tourme.Model.StaticVars;
import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity {

    Switch obavestenja;
    Button izmeniNalog;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        obavestenja = findViewById(R.id.switchObavestenja);
        izmeniNalog = findViewById(R.id.izmeniNalog);
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        Boolean isObavestenja = prefs.getBoolean("isObavestenja",true);
        obavestenja.setChecked(isObavestenja);
        obavestenja.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
                editor.putBoolean("isObavestenja", isChecked);
                editor.apply();
            }
        });

            if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                izmeniNalog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(Settings.this, IzmeniAccountActivity.class);
                        startActivity(i);
                    }
                });
            }
            else{
                izmeniNalog.setVisibility(View.GONE);
            }
    }
}