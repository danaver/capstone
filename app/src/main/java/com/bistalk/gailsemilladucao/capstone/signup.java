package com.bistalk.gailsemilladucao.capstone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {


    Button signUp;
    TextView logIn;
    ProgressDialog progressDialog;

    // FirebaseDatabase fbdb;
    FirebaseAuth firebaseAuth;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        signUp = findViewById(R.id.signupBtn);
        logIn = (TextView) findViewById(R.id.signinText);

        progressDialog = new ProgressDialog(this);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://goo.gl/forms/VAcr3twIBuFhNvXm1"));
                startActivity(browserIntent);
                signup.super.finish();

            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class ));
            }
        });
    }
}
