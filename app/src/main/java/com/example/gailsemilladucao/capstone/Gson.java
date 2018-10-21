package com.example.gailsemilladucao.capstone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.gailsemilladucao.capstone.model.Bistalk;
import com.example.gailsemilladucao.capstone.model.users;
import com.example.gailsemilladucao.capstone.model.wordbanks;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Gson extends AppCompatActivity {

    String jsonstring = null;
    Bistalk bistalk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gson);

        //get the jsonstring
        jsonstring = readFromFile();

        //this will return the array version of the json
        //now pwede naka mo do og stuff such as editing and deleting and appending through bistalk
        bistalk=JsontoGson();

        //add a function here that will edit it
        bistalk = changeStatus(bistalk);

        //once mana kag edit it shiz pwede na nimo ipa jsonfile balik
        //if you notice naa syay try catch. its because of the writeFile within that function
        try {
            GsontoJson(bistalk);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Bistalk changeStatus(Bistalk bistalk) {

        //access the data
        bistalk.getWordbankList().get(1).setCebuano("999");

        return bistalk;
    }


    //once you done editing and adding stuff to the array
    //this will convert it back to json

    public void GsontoJson(Bistalk besh) throws JSONException {

        //Gson gson = new Gson(); raman ta na idk nganong mo error haahaha
        com.google.gson.Gson gson = new com.google.gson.Gson();

        //initialize a wordbanks to add new word, since you passed besh(ang gson array) you can append it der
        //just comment this out if ever
        //if naay kuwang sa constructor just add sa wordbanks og lain constructor
       // wordbanks wordo = new wordbanks("meow", "meow", "meow", "meow", "meow");

        //you have added to the array
        //to append it you have to access the "WordbankList" sooo besh.getWordbankList.add()
       // besh.getWordbankList().add(wordo);

        Bistalk bistalk = new Bistalk(besh.getUserList(),besh.getWordbankList());
        String json = gson.toJson(bistalk);

        // this will overwrite the jsonfile
        try {
            writeFile(json);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //returns an array version
    //this is from json
    private Bistalk JsontoGson(){
        //this is the portion na gi convert ang json to array
        //.fromJson(<the json string version ni wordbank>,<ang data type ni json file>)
        Bistalk bistalk = new com.google.gson.Gson().fromJson(jsonstring, Bistalk.class);

        //here is an example of accessing the data
        //bistalk -> user and wordbank -> wordbank index -> access an element
        String word = bistalk.getWordbankList().get(1).getEnglish();
        Toast.makeText(this, word, Toast.LENGTH_SHORT).show();


        return bistalk;
    }

    //this will overwrite the jsonfile that exist hence it will update
    void writeFile(String data) throws IOException {
        File outFile = new File(getFilesDir(), "wordbank.json");
        FileOutputStream out = new FileOutputStream(outFile, false);
        byte[] contents = data.getBytes();
        out.write(contents);
        out.flush();
        out.close();
    }

    //returns the string version of the wordbank.json
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
