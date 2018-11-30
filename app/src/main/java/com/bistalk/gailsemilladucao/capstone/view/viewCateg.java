package com.bistalk.gailsemilladucao.capstone.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.bistalk.gailsemilladucao.capstone.R;
import com.bistalk.gailsemilladucao.capstone.model.Bistalk;
import com.bistalk.gailsemilladucao.capstone.model.wordAdapter;
import com.bistalk.gailsemilladucao.capstone.model.wordbanks;

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
    Bistalk bistalk;

    // Session
    private static final String PREF_NAME = "MyPrefs";
    private static final String IS_FREE = "isFree";
    SharedPreferences pref;

    int status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_categ);

        listView = findViewById(R.id.listview);

        wordlist = new ArrayList<wordbanks>();


        categ = getIntent().getStringExtra("Val");
        jsonfile = readFromFile();
        bistalk = JsontoGson();

        // Session
        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        if(pref.getBoolean(IS_FREE, true)) {
            status = 1;
        }else if(!pref.getBoolean(IS_FREE, true)) {
            status = 2;
        }

        for (int i =0; i < bistalk.getWordbankList().size();i++) {
            if (bistalk.getWordbankList().get(i).getCategory().equals(categ)){
                String eng = bistalk.getWordbankList().get(i).getEnglish();
                String ceb = bistalk.getWordbankList().get(i).getCebuano();
                String aud = bistalk.getWordbankList().get(i).getAudio();
                String img = bistalk.getWordbankList().get(i).getPicture();
                String pru = bistalk.getWordbankList().get(i).getPronunciation();
                String cat = bistalk.getWordbankList().get(i).getCategory();
                String fx = bistalk.getWordbankList().get(i).getEffect();
                int stat = bistalk.getWordbankList().get(i).getStatus();

                wordlist.add(new wordbanks(eng, ceb,pru, cat,aud, img, fx,stat));
            }
        }
        wordAdapter adapter = new wordAdapter(this, R.layout.row, wordlist, status);
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

    private Bistalk JsontoGson(){
        Bistalk bistalk = new com.google.gson.Gson().fromJson(jsonfile, Bistalk.class);
        return bistalk;
    }
}
