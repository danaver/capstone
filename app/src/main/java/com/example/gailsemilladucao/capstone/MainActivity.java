package com.example.gailsemilladucao.capstone;

// =========== API =========== //
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gailsemilladucao.capstone.backend.AddData;
import com.example.gailsemilladucao.capstone.database.DatabaseOpenHelper;

import java.util.ArrayList;
import java.util.Locale;

// =========== PACKAGES =========== //

public class MainActivity extends AppCompatActivity {

    public TextView txvResult;
    public String message ="Hello";
    DatabaseOpenHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txvResult = findViewById(R.id.txvResult);
        db = new DatabaseOpenHelper(this);
    }

    //called in xml
    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);

        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    message = result.get(0);
                    txvResult.setText(message);

                    //covert first letter to uppercase since database set it on uppercase
                    message = message.substring(0,1).toUpperCase() + message.substring(1).toLowerCase();
                    //access the db

                    //Checking if the word is naa in the database
                    String cebWord = db.getAddress(message);
                    if(cebWord != ""){
//                        Intent lol = new Intent(MainActivity.this,ShowData.class);
//                        lol.putExtra("Val", message);
//                        startActivity(lol);
                        txvResult.setText(cebWord);
                    }else{
                        Toast.makeText(this, "Word is not found", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
        }
    }

    //called in xml
    public void addWord(View view) {
        Intent intent = new Intent(MainActivity.this, AddData.class);
        startActivity(intent);
    }

}
