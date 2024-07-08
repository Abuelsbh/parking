package com.application.parking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;

public class Splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Intent intent = new Intent(Splashscreen.this, Home.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.selectpage, R.anim.selectpagee);
                    finish();
                } else {
                    Intent intent = new Intent(Splashscreen.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.selectpage, R.anim.selectpagee);
                    finish();
                }
            }
        }, 500);
    }
}