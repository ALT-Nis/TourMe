package com.example.tourme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.tourme.Model.StaticVars;

public class Settings extends AppCompatActivity {

    Switch obavestenja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        obavestenja = findViewById(R.id.switchObavestenja);
        obavestenja.setChecked(StaticVars.isObavestenja);
        obavestenja.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                StaticVars.isObavestenja = isChecked;
            }
        });
    }
}