package com.example.gailsemilladucao.capstone.backend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gailsemilladucao.capstone.R;
import com.example.gailsemilladucao.capstone.model.wordbanks;

public class EditData extends AppCompatActivity {


    Spinner pus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);

        pus = findViewById(R.id.pos);

        Intent i = getIntent();
        wordbanks val = (wordbanks)i.getSerializableExtra("res");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(EditData.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.post));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pus.setAdapter(adapter);

        //Toast.makeText(this, ""+val.getAudio(), Toast.LENGTH_SHORT).show();
    }
}
