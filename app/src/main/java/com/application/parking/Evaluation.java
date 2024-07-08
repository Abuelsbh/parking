package com.application.parking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Evaluation extends AppCompatActivity {

    String KeyOfParking;
    DatabaseReference database;
    String temp_key;
    EditText Comment;
    RatingBar ratingBar;
    Button btn;
    boolean done = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        Comment = findViewById(R.id.Comment);
        ratingBar = findViewById(R.id.ratingBar);
        btn = findViewById(R.id.Rating);

        KeyOfParking = getIntent().getExtras().getString("KeyOfParking");
        database = FirebaseDatabase.getInstance().getReference().child("Parking").child(KeyOfParking).child("Comments");


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.orderByChild("User").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            dataSnapshot.getRef().child("Comment").setValue(Comment.getText().toString());
                            dataSnapshot.getRef().child("Rate").setValue(ratingBar.getRating());
                            done = true;
                            Toast.makeText(Evaluation.this, "Comment added", Toast.LENGTH_SHORT).show();
                            Toast.makeText(Evaluation.this, "Evaluation added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Evaluation.this, Home.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.selectpage,R.anim.selectpagee);
                            break;
                        }
                        if (!done) {
                            java.util.Map<String, Object> map1 = new HashMap<>();
                            temp_key = database.push().getKey();
                            database.updateChildren(map1);
                            java.util.Map<String, Object> map = new HashMap<>();
                            temp_key = database.push().getKey();
                            database.updateChildren(map);

                            DatabaseReference user = database.child(temp_key);
                            Map<String, Object> map2 = new HashMap<>();
                            map2.put("User", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            map2.put("Comment", Comment.getText().toString());
                            map2.put("Rate", ratingBar.getRating());

                            user.updateChildren(map2);

                            Toast.makeText(Evaluation.this, "Comment added", Toast.LENGTH_SHORT).show();
                            Toast.makeText(Evaluation.this, "Evaluation added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Evaluation.this, Home.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.selectpage,R.anim.selectpagee);

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