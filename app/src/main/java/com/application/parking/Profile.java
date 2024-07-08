package com.application.parking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Profile extends AppCompatActivity {

    EditText Username,Phone,Email;
    Button btn;
    DatabaseReference database;
    StorageReference MReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        database = FirebaseDatabase.getInstance().getReference().child("Users");
        MReference = FirebaseStorage.getInstance().getReference().child("Photos");

        Username = findViewById(R.id.Username);
        Email = findViewById(R.id.Email);
        Phone = findViewById(R.id.Phone);
        btn = findViewById(R.id.btn);

        database.orderByChild("Email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Username.setText(dataSnapshot.child("Username").getValue(String.class));
                    Email.setText(dataSnapshot.child("Email").getValue(String.class));
                    Phone.setText(dataSnapshot.child("Phone").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database.orderByChild("Email").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                            dataSnapshot.getRef().child("Username").setValue(Username.getText().toString());
                            dataSnapshot.getRef().child("Email").setValue(Email.getText().toString());
                            dataSnapshot.getRef().child("Phone").setValue(Phone.getText().toString());

                            Toast.makeText(Profile.this, "تم...", Toast.LENGTH_SHORT).show();

                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.selectpage,R.anim.selectpagee);
    }

}