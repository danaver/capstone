package com.example.gailsemilladucao.capstone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {


    Button signUp;
    EditText email;
    EditText pass;
    TextView signIn;
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
        email =  findViewById(R.id.emailTxt);
        pass =  findViewById(R.id.passwordTxt);
        signIn = (TextView) findViewById(R.id.signinText);

        progressDialog = new ProgressDialog(this);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class ));
            }
        });
    }

    public void Register (){
        final String userEmail =  email.getText().toString();
        final String userPass =  pass.getText().toString();
        progressDialog.setMessage("Registering New User ... ");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(userEmail,userPass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    finish();

                    onAuthSuccess(task.getResult().getUser(),userEmail,userPass);
                    // onAuthSuccess(task.getResult().)
                    Toast.makeText(signup.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(signup.this, "Try Again!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onAuthSuccess(FirebaseUser user, String email, String pass){
        String userId = user.getUid();
        //db.child("User").child(userId).setValue(email).;
        db.child("User").child(userId).child("email").setValue(email);
        db.child("User").child(userId).child("password").setValue(pass);
        //        user = FirebaseAuth.getInstance().getCurrentUser();
//        db = fbdb.getReference("email");
//        String userId = user.getUid();
//        db.child("User").child(userId);
//       // db = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
//       // db.child("User").child(userId).setValue(email);
//
////        HashMap<String,String> userMap = new HashMap<>();
////        userMap.put("email",email);


    }
}
