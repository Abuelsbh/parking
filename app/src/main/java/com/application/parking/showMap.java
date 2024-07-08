package com.application.parking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class showMap extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    double Long, Lat;
    String Title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        Long = getIntent().getExtras().getDouble("Long");
        Lat = getIntent().getExtras().getDouble("Lat");
        Title = getIntent().getExtras().getString("Title");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Lat, Long);
        mMap.addMarker(new MarkerOptions().position(sydney).title(Title));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(Lat, Long));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(5);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }
}