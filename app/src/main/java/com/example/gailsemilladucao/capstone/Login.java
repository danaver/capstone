package com.example.gailsemilladucao.capstone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener {

    Button signinBtn,register;
    EditText emailtxt, passwordtxt;

    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
//
//        if (firebaseAuth.getCurrentUser() != null){
//            finish();
//
//            startActivity(new Intent(getApplicationContext(), HomeActivity.class) );
//        }

        signinBtn = (Button) findViewById(R.id.signinBtn); //if using signupBtn program wont crash
        emailtxt = (EditText) findViewById(R.id.emailView);
        passwordtxt = (EditText) findViewById(R.id.passwordView);
        register = (Button)findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, signup.class);
                startActivity(intent);
                finish();
            }
        });

        progressDialog = new ProgressDialog(this);

        signinBtn.setOnClickListener(this);
    }


    public void userLogin(){
        String email = emailtxt.getText().toString().trim();
        String password = passwordtxt.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_LONG).show();
            return;
        }

        progressDialog.setMessage("Signing in to your Account ... ");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if(task.isSuccessful()){
                    finish();
                    Toast.makeText(Login.this, " Successfully signed in to your Account", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else{
                    Toast.makeText(Login.this, "Sign In Unsuccessful", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    public void onClick(View view) {

        if(view == signinBtn){
            userLogin();
        }
    }
}
