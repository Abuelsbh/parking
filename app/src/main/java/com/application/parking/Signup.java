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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    Boolean visibility = false;
    ImageView showPassword;
    TextView login;
    EditText Username,Email,Password,Phone;
    Button btn;
    FirebaseAuth mAuth;
    ProgressDialog loadingBar;
    String temp_key;
    DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        root= FirebaseDatabase.getInstance().getReference().child("Users");
        loadingBar=new ProgressDialog(this);
        mAuth= FirebaseAuth.getInstance();
        login = findViewById(R.id.login);
        Username = findViewById(R.id.Username);
        Email = findViewById(R.id.Email);
        Password = findViewById(R.id.Password);
        Phone = findViewById(R.id.Phone);
        btn = findViewById(R.id.btn);
        showPassword = findViewById(R.id.showPassword);

        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (visibility) {
                    visibility = false;
                    Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPassword.setImageResource(R.drawable.eye_slash);
                } else {
                    visibility = true;
                    Password.setInputType(InputType.TYPE_CLASS_TEXT);
                    showPassword.setImageResource(R.drawable.eye);
                }
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Signup.this,MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.selectpage,R.anim.selectpagee);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Email.getText().toString().isEmpty() && !Password.getText().toString().isEmpty() && !Username.getText().toString().isEmpty() && !Phone.getText().toString().isEmpty() )
                {
                    loadingBar.setTitle("Creating New Account");
                    loadingBar.setMessage("Please Wait, While we are creating you new account...");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(true);
                    createnewaccount();
                }
                else
                {
                    Toast.makeText(Signup.this, "continue all information...", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void createnewaccount()
    {
        mAuth.createUserWithEmailAndPassword(Email.getText().toString(),Password.getText().toString()).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    sendemailnotificationmessage();
                    loadingBar.dismiss();
                }
                else
                {
                    String message=task.getException().getMessage();
                    Toast.makeText(Signup.this, "Error "+message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                }
            }
        });
    }

    private void sendemailnotificationmessage()
    {
        FirebaseUser user=mAuth.getCurrentUser();
        if(user !=null)
        {

            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {

                    if(task.isSuccessful())
                    {
                        Map<String, Object> map1 = new HashMap<>();
                        temp_key = root.push().getKey();
                        root.updateChildren(map1);

                        Map<String, Object> map = new HashMap<>();
                        temp_key = root.push().getKey();
                        root.updateChildren(map);

                        DatabaseReference user = root.child(temp_key);

                        Map<String, Object> map2 = new HashMap<>();
                        map2.put("Username", Username.getText().toString());
                        map2.put("Email",  Email.getText().toString());
                        map2.put("Phone",  Phone.getText().toString());


                        user.updateChildren(map2);


                        Toast.makeText(Signup.this, "Registration successful ,Please verify your account", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();
                        Intent loginintent=new Intent(Signup.this,MainActivity.class);
                        startActivity(loginintent);
                        overridePendingTransition(R.anim.selectpage,R.anim.selectpagee);
                        finish();
                        mAuth.signOut();
                    }
                    else
                    {
                        String error=task.getException().getMessage();
                        Toast.makeText(Signup.this, "Error: "+error, Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    }
                }
            });
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.selectpage,R.anim.selectpagee);
    }
}