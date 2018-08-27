package com.example.gailsemilladucao.capstone.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gailsemilladucao.capstone.R;
import com.example.gailsemilladucao.capstone.database.DatabaseAccess;

public class ShowData extends AppCompatActivity {

    public EditText englishText;
    public Button query_button;
    public TextView result_cebuano;
    public String mes;
    Bundle extras = getIntent().getExtras();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        if(extras != null) {
            mes = extras.getString("word");
        }

        englishText.setText(mes);

        englishText = findViewById(R.id.englishText);
        query_button = findViewById(R.id.query_button);
        result_cebuano = findViewById(R.id.result_text);

        //SETTING ONCLICK LISTENER

        query_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // CREATE INSTANCE OF DB ACCESS CLASS AND DB CONNECTION
                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open();

                // GETTING VALUE FROM EDIT TEXT

                String engWord = englishText.getText().toString();
                String cebWord = databaseAccess.getAddress(engWord); // getAddress() method to get the english word

                // SETTING TEXT TO RESULT FIELD
                result_cebuano.setText(cebWord);
                databaseAccess.close();
            }
        });
    }
}
