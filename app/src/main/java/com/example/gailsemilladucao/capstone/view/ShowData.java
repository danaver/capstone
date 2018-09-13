package com.example.gailsemilladucao.capstone.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gailsemilladucao.capstone.MainActivity;
import com.example.gailsemilladucao.capstone.R;
import com.example.gailsemilladucao.capstone.database.DatabaseOpenHelper;

public class ShowData extends AppCompatActivity {

    public EditText englishText;
    public Button query_button;
    public TextView result_cebuano;
    public MainActivity mainActivity;
    String Val;

    DatabaseOpenHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        englishText = findViewById(R.id.englishText);
        query_button = findViewById(R.id.query_button);
        result_cebuano = findViewById(R.id.result_text);

        Val = getIntent().getStringExtra("Val");

        englishText.setText(Val);

        // CREATE INSTANCE OF DB ACCESS CLASS AND DB CONNECTION
//        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
//        databaseAccess.open();

        // GETTING VALUE FROM EDIT TEXT
        String engWord = englishText.getText().toString();
        String cebWord = db.getAddress(engWord); // getAddress() method to get the english word

        // SETTING TEXT TO RESULT FIELD
        result_cebuano.setText(cebWord);


        //SETTING ONCLICK LISTENER


        query_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // CREATE INSTANCE OF DB ACCESS CLASS AND DB CONNECTION


                // GETTING VALUE FROM EDIT TEXT
                String engWord = englishText.getText().toString();
                String cebWord = db.getAddress(engWord); // getAddress() method to get the english word

                // SETTING TEXT TO RESULT FIELD
                result_cebuano.setText(cebWord);
                db.close();
            }
        });
    }
}
