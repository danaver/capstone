package com.example.gailsemilladucao.capstone.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.gailsemilladucao.capstone.R;
import com.example.gailsemilladucao.capstone.model.wordAdapter;
import com.example.gailsemilladucao.capstone.model.wordbanks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class viewCateg extends AppCompatActivity {

    String categ,jsonfile;
    ArrayList<wordbanks> wordlist;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_categ);

        listView = findViewById(R.id.listview);

        wordlist = new ArrayList<wordbanks>();

        categ = getIntent().getStringExtra("Val");
        jsonfile = readFromFile();

        String img=null,fx=null;
        try {
            JSONObject jsonObject = new JSONObject(jsonfile);
            JSONArray jsonArray = null;

            jsonArray = jsonObject.getJSONArray("wordbank");
            for (int i = 0; i <jsonArray.length();i++) {
                JSONObject word = jsonArray.getJSONObject(i);

                if (word.getString("POS").equals(categ)) {
                    String eng = word.getString("English");
                    String ceb = word.getString("Cebuano");
                    String aud = word.getString("Audio");
                    if (word.has("Picture")) {
                        img = word.getString("Picture");
                    }else{
                        img = null;
                    }
                    if (word.has("Effect")) {
                        fx = word.getString("Effect");
                    }else{
                        fx = null;
                    }

                    wordlist.add(new wordbanks(eng, ceb, aud, img, fx));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        wordAdapter adapter = new wordAdapter(this, R.layout.row, wordlist);
        listView.setAdapter(adapter);

        listView.invalidateViews();

    }


    private String readFromFile() {
        String ret = "";

        try {
            InputStream inputStream = openFileInput("wordbank.json");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
