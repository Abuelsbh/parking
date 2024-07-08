package com.application.parking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menu;
    ArrayList<Parking> parking = new ArrayList<>();
    ListViewAdaptor listViewAdaptor = new ListViewAdaptor();
    ListView listView;
    DatabaseReference database;
    ArrayList<Comment> Comments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer);

        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
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
                    case R.id.home:
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.NearBy:
                        intent = new Intent(Home.this, NearBy.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.selectpage, R.anim.selectpagee);
                        finish();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.add:
                        intent = new Intent(Home.this, Add.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.selectpage, R.anim.selectpagee);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.profile:
                        intent = new Intent(Home.this, MyProfile.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.selectpage, R.anim.selectpagee);
                        finish();
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        Intent intent2 = new Intent(Home.this, MainActivity.class);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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


            Glide.with(Home.this).load(parking.get(i).getImage()).into(imageView);
            //Picasso.get().load(Uri.parse(Services.get(i).getImage())).into(imageView);
            //Picasso.with(my_profile.this).load(Uri.parse(Services.get(i).getImage())).into(imageView);
            title.setText(parking.get(i).getTitle());
            Location.setText(parking.get(i).getLocation());


            linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    users = new ArrayList<>();
                    comments = new ArrayList<>();
                    rates = new ArrayList<>();

                    for(int g=0; g<parking.get(i).getComments().size(); g++) {
                        users.add(parking.get(i).getComments().get(g).getUser());
                        comments.add(parking.get(i).getComments().get(g).getComment());
                        rates.add(String.valueOf(parking.get(i).getComments().get(g).getRate()));
                    }
                    Intent intent = new Intent(Home.this,InfoOfParking.class);
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