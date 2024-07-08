package com.application.parking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class InfoOfParking extends AppCompatActivity {
    String owner,title,Image,description,key,location;
    int numOfParking, price;
    double Long, Lat;
    TextView Title,Username,Description,Location,Price,NumOfParking,Phone;
    RoundedImageView ImageOfParking;
    Button Remove,book,showMap,Call;
    DatabaseReference database,database2;
    ArrayList<String> Comments = new ArrayList<>();
    ArrayList<String> Users = new ArrayList<>();
    ArrayList<String> Rates = new ArrayList<>();
    RatingBar ratingBar;
    ListView listView;
    ListViewAdaptor listViewAdaptor = new ListViewAdaptor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_of_parking);

        database = FirebaseDatabase.getInstance().getReference().child("Parking");
        database2 = FirebaseDatabase.getInstance().getReference().child("Users");

        Title = findViewById(R.id.title);
        ImageOfParking = findViewById(R.id.image);
        Description = findViewById(R.id.Description);
        Location = findViewById(R.id.Location);
        NumOfParking = findViewById(R.id.NumOfParking);
        Price = findViewById(R.id.Price);
        Phone = findViewById(R.id.Phone);
        Username = findViewById(R.id.Username);
        Remove = findViewById(R.id.btn);
        book = findViewById(R.id.btn2);
        showMap = findViewById(R.id.showMap);
        Call = findViewById(R.id.Call);
        ratingBar = findViewById(R.id.AllRating);
        listView = findViewById(R.id.listView);

        owner = getIntent().getExtras().getString("Owner");
        title = getIntent().getExtras().getString("Title");
        Image = getIntent().getExtras().getString("Image");
        numOfParking = getIntent().getExtras().getInt("NumOfParking");
        price = getIntent().getExtras().getInt("Price");
        location = getIntent().getExtras().getString("Location");
        Long = getIntent().getExtras().getDouble("Long");
        Lat = getIntent().getExtras().getDouble("Lat");
        description = getIntent().getExtras().getString("Description");
        key = getIntent().getExtras().getString("Key");
        Users = getIntent().getExtras().getStringArrayList("Users");
        Comments = getIntent().getExtras().getStringArrayList("Comments");
        Rates = getIntent().getExtras().getStringArrayList("Rates");


        listViewAdaptor.notifyDataSetChanged();
        listView.setSelection(Comments.size());
        listView.setAdapter(listViewAdaptor);

        float TotalRate = 0;
        for (int i=0; i<Rates.size(); i++) {
            TotalRate += Float.parseFloat(Rates.get(i));
        }
        TotalRate = TotalRate / Rates.size();
        ratingBar.setRating(TotalRate);

        Title.setText(title);
        Description.setText(description);
        Glide.with(InfoOfParking.this).load(Image).into(ImageOfParking);
        NumOfParking.setText(String.valueOf(numOfParking));
        Price.setText(String.valueOf(price));
        Location.setText(String.valueOf(location));
        if(owner.equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
            book.setVisibility(View.GONE);
        } else {
            Remove.setVisibility(View.GONE);
        }


        Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Phone.getText().toString()));
                startActivity(intent);
            }
        });

        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoOfParking.this,showMap.class);
                intent.putExtra("Long",Long);
                intent.putExtra("Lat",Lat);
                intent.putExtra("Title",title);
                startActivity(intent);
                overridePendingTransition(R.anim.selectpage, R.anim.selectpagee);
            }
        });

        database2.orderByChild("Email").equalTo(owner).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Username.setText(dataSnapshot.child("Username").getValue(String.class));
                    Phone.setText(dataSnapshot.child("Phone").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue();
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoOfParking.this, ShapeOfSlots.class);
                intent.putExtra("Key",key);
                intent.putExtra("numOfParking",numOfParking);
                intent.putExtra("Price",price);
                intent.putExtra("Username",Username.getText().toString());
                intent.putExtra("Phone",Phone.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.selectpage, R.anim.selectpagee);
            }
        });

    }

    public class ListViewAdaptor extends BaseAdapter {

        @Override
        public int getCount() {
            return Comments.size();
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
            View view1;
            view1 = getLayoutInflater().inflate(R.layout.evaluation, null);

            TextView User = view1.findViewById(R.id.User);
            RatingBar ratingBar = view1.findViewById(R.id.ratingBar);
            TextView Comment = view1.findViewById(R.id.Comment);

            User.setText(Users.get(i));
            ratingBar.setRating(Float.parseFloat(Rates.get(i)));
            Comment.setText(Comments.get(i));

            return view1;
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.selectpage,R.anim.selectpagee);
    }
}