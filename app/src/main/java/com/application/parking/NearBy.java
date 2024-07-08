package com.application.parking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NearBy extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menu;
    Button btn;
    private final static int LOCATION_REQUEST_CODE = 23;
    ListView listView;
    DatabaseReference database;
    ArrayList<Parking> parking = new ArrayList<>();
    ListViewAdaptor listViewAdaptor = new ListViewAdaptor();
    ArrayList<Comment> Comments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_by);


        drawerLayout = findViewById(R.id.drawer);

        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);
        menu = findViewById(R.id.menu);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                int id = item.getItemId();
                switch (id) {
                    case R.id.home:intent = new Intent(NearBy.this, Home.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.selectpage, R.anim.selectpagee);
                        finish();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.NearBy:
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.add:
                        intent = new Intent(NearBy.this, Add.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.selectpage, R.anim.selectpagee);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.profile:
                        intent = new Intent(NearBy.this, MyProfile.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.selectpage, R.anim.selectpagee);
                        finish();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        Intent intent2 = new Intent(NearBy.this, MainActivity.class);
                        startActivity(intent2);
                        overridePendingTransition(R.anim.selectpage, R.anim.selectpagee);
                        finish();
                        drawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });



        database = FirebaseDatabase.getInstance().getReference().child("Parking");
        listView = findViewById(R.id.listView);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange(Location location) {
                            LatLng ltlng = new LatLng(location.getLatitude(), location.getLongitude());
                            database.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    listViewAdaptor = new ListViewAdaptor();
                                    parking = new ArrayList<>();
                                    for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                                        Comments = new ArrayList<>();
                                        for (DataSnapshot dataSnapshot1:dataSnapshot.child("Comments").getChildren()) {
                                            Comments.add(new Comment(dataSnapshot1.child("Comment").getValue(String.class),
                                                    dataSnapshot1.child("Rate").getValue(Float.class),
                                                    dataSnapshot1.child("User").getValue(String.class)));
                                        }
                                        parking.add(new Parking(dataSnapshot.child("Owner").getValue(String.class), dataSnapshot.child("Image").getValue(String.class),
                                                dataSnapshot.child("Description").getValue(String.class), dataSnapshot.child("Location").getValue(String.class),
                                                dataSnapshot.child("Title").getValue(String.class), (Double) dataSnapshot.child("Lat").getValue(),
                                                (Double) dataSnapshot.child("Long").getValue(), dataSnapshot.child("NumOfParking").getValue(int.class),
                                                dataSnapshot.child("Price").getValue(int.class), dataSnapshot.getKey(), Comments));
                                    }
                                    listViewAdaptor.notifyDataSetChanged();
                                    listView.setSelection(parking.size());
                                    listView.setAdapter(listViewAdaptor);
                                    getLocations();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                                    ltlng, 16f);
                            mMap.animateCamera(cameraUpdate);
                        }
                    });
                    Location location = mMap.getMyLocation();

                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);

                            mMap.clear();
                            CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                                    latLng, 15);
                            mMap.animateCamera(location);
                            mMap.addMarker(markerOptions);
                        }
                    });


                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void getLocations() {
        for (int i=0; i<parking.size(); i++) {
            LatLng sydney = new LatLng(parking.get(i).getLat(), parking.get(i).getLong());
            mMap.addMarker(new MarkerOptions().position(sydney).title(parking.get(i).getTitle()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(parking.get(i).getLat(), parking.get(i).getLong()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(5);

            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        dist = dist * 1000;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


    ArrayList<String> comments = new ArrayList<>();
    ArrayList<String> users = new ArrayList<>();
    ArrayList<String> rates = new ArrayList<>();


    public class ListViewAdaptor extends BaseAdapter {

        @Override
        public int getCount() {
            return parking.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {


            View view1 = getLayoutInflater().inflate(R.layout.listview, null);
            RoundedImageView imageView = view1.findViewById(R.id.ProfilePicture);
            TextView title = view1.findViewById(R.id.title);
            TextView Location = view1.findViewById(R.id.Location);
            LinearLayout linear = view1.findViewById(R.id.linear);


            Glide.with(NearBy.this).load(parking.get(i).getImage()).into(imageView);
            //Picasso.get().load(Uri.parse(Services.get(i).getImage())).into(imageView);
            //Picasso.with(my_profile.this).load(Uri.parse(Services.get(i).getImage())).into(imageView);
            title.setText(parking.get(i).getTitle());
            Location.setText(parking.get(i).getLocation());


            linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    for(int g=0; g<parking.get(i).getComments().size(); g++) {
                        users.add(parking.get(i).getComments().get(g).getUser());
                        comments.add(parking.get(i).getComments().get(g).getComment());
                        rates.add(String.valueOf(parking.get(i).getComments().get(g).getRate()));
                    }

                    Intent intent = new Intent(NearBy.this,InfoOfParking.class);
                    intent.putExtra("Owner", parking.get(i).getOwner());
                    intent.putExtra("Title",parking.get(i).getTitle());
                    intent.putExtra("Image",parking.get(i).getImage());
                    intent.putExtra("NumOfParking",parking.get(i).getNumOfParking());
                    intent.putExtra("Price",parking.get(i).getPrice());
                    intent.putExtra("Location",parking.get(i).getLocation());
                    intent.putExtra("Description",parking.get(i).getDescription());
                    intent.putExtra("Long",parking.get(i).getLong());
                    intent.putExtra("Lat",parking.get(i).getLat());
                    intent.putExtra("Key",parking.get(i).getKey());
                    intent.putExtra("Comments",comments);
                    intent.putExtra("Rates",rates);
                    intent.putExtra("Users",users);
                    startActivity(intent);
                    overridePendingTransition(R.anim.selectpage, R.anim.selectpagee);
                }
            });
            return view1;
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.selectpage,R.anim.selectpagee);
    }

}