package com.example.tourme;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.example.tourme.Model.Gradovi;
import com.example.tourme.Model.Oglas;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.tourme.databinding.ActivityMapaGradoviBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class mapaGradovi extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapaGradoviBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapaGradoviBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final List<Gradovi.grad> lokacija  = Arrays.asList(
        new Gradovi.grad("Beograd", new LatLng(44.8167, 20.4667)),
        new Gradovi.grad("Novi Sad", new LatLng(45.2644, 19.8317)),
        new Gradovi.grad("Niš", new LatLng(43.3192, 21.8961)),
        new Gradovi.grad("Kragujevac", new LatLng(44.0142, 20.9394)),
        new Gradovi.grad("Priština", new LatLng(42.667542, 21.166191)),
        new Gradovi.grad("Subotica", new LatLng(46.0983, 19.6700)),
        new Gradovi.grad("Zrenjanin", new LatLng(45.3778, 20.3861)),
        new Gradovi.grad("Pančevo", new LatLng(44.8739, 20.6519)),
        new Gradovi.grad("Čačak", new LatLng(43.8914, 20.3497)),
        new Gradovi.grad("Kruševac", new LatLng(43.5833, 21.3267)),
        new Gradovi.grad("Kraljevo", new LatLng(43.7234, 20.6870)),
        new Gradovi.grad("Novi Pazar", new LatLng(43.1500, 20.5167)),
        new Gradovi.grad("Smederevo", new LatLng(44.66278, 20.93)),
        new Gradovi.grad("Leskovac", new LatLng(42.9981, 21.9461)),
        new Gradovi.grad("Užice", new LatLng(43.8500, 19.8500)),
        new Gradovi.grad("Vranje", new LatLng(42.5542, 21.8972)),
        new Gradovi.grad("Valjevo", new LatLng(44.2667, 19.8833)),
        new Gradovi.grad("Šabac", new LatLng(44.748861, 19.690788)),
        new Gradovi.grad("Sombor", new LatLng(45.7800, 19.1200)),
        new Gradovi.grad("Požarevac", new LatLng(44.62133, 21.18782)),
        new Gradovi.grad("Pirot", new LatLng(43.15306, 22.58611)),
        new Gradovi.grad("Zaječar", new LatLng(43.90358, 22.26405)),
        new Gradovi.grad("Kikinda", new LatLng(45.8244, 20.4592)),
        new Gradovi.grad("Sremska Mitrovica", new LatLng(44.8167, 20.4667)), //fali
        new Gradovi.grad("Vršac", new LatLng(45.1206, 21.2986)),
        new Gradovi.grad("Bor", new LatLng(44.07488, 22.09591)),
        new Gradovi.grad("Prokuplje", new LatLng(43.23417, 21.58806)),
        new Gradovi.grad("Loznica", new LatLng(44.5333, 19.2258)));

        FirebaseDatabase.getInstance().getReference("oglasi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> zaPrikaz = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Oglas oglas = dataSnapshot.getValue(Oglas.class);
                    String grad = oglas.getGrad();
                    zaPrikaz.add(grad);
                }
                for(int i=0;i<lokacija.size();++i){
                    if(zaPrikaz.contains(lokacija.get(i).getIme())){
                        mMap.addMarker(new MarkerOptions().position(lokacija.get(i).getLokacija()).title(lokacija.get(i).getIme()));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(lokacija.get(i).getLokacija()));
                    }

                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(44.010013,20.490270), 6.8f));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}