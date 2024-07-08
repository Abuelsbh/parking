package com.application.parking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ProgressDialog loadingBar;
    Boolean emailAddressChecker,visibility = false;
    TextView Signup,reset;
    EditText email, password;
    Button btn;
    ImageView showPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingBar=new ProgressDialog(this);
        mAuth= FirebaseAuth.getInstance();
        Signup = findViewById(R.id.Signup);
        reset = findViewById(R.id.reset);
        email = findViewById(R.id.Email);
        password = findViewById(R.id.Password);
        showPassword = findViewById(R.id.showPassword);
        btn = findViewById(R.id.btn);

        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (visibility) {
                    visibility = false;
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPassword.setImageResource(R.drawable.eye_slash);
                } else {
                    visibility = true;
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    showPassword.setImageResource(R.drawable.eye);
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,resetPassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.selectpage,R.anim.selectpagee);
            }
        });

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Signup.class);
                startActivity(intent);
                overridePendingTransition(R.anim.selectpage,R.anim.selectpagee);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowingusertologin();
            }
        });
    }

    private void allowingusertologin()
    {
        String Email=email.getText().toString();
        String Password=password.getText().toString();

        if(Email.isEmpty())
        {
            Toast.makeText(this, "please write your email...", Toast.LENGTH_SHORT).show();
        }
        else if(Password.isEmpty())
        {
            Toast.makeText(this, "please write your password...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login");
            loadingBar.setMessage("please wait,while we are allowing you to log in into your email");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        verifyemailaddress();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        String message=task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error "+message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void verifyemailaddress()
    {
        FirebaseUser user=mAuth.getCurrentUser();
        emailAddressChecker=user.isEmailVerified();
        if(emailAddressChecker)
        {
            Intent loginintent=new Intent(MainActivity.this, Home.class);
            startActivity(loginintent);
            overridePendingTransition(R.anim.selectpage,R.anim.selectpagee);
            finish();
        }
        else
        {
            Toast.makeText(this, "please verify your account first...", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.selectpage,R.anim.selectpagee);
    }
}