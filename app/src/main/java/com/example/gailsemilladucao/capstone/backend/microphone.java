package com.example.gailsemilladucao.capstone.backend;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gailsemilladucao.capstone.R;

public class microphone extends AppCompatActivity {

    Button btnAdd;
    EditText mtask;
    Spinner drop;
    String val;
    TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bunding
        mtask = findViewById(R.id.editText);

        //Bundle
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            String message = bundle.getString("textRecorded");
            mtask.setText(message);
        }
    }
}
