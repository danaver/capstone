package com.example.gailsemilladucao.capstone;

// =========== API =========== //
import android.Manifest;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


// =========== PACKAGES =========== //
import com.example.gailsemilladucao.capstone.backend.*;
import com.example.gailsemilladucao.capstone.view.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public TextView txvResult;
    public String message ="Hello";

    String jsonString;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txvResult = findViewById(R.id.txvResult);

        //create folders
        createFolder();

        //download json
        gayson();

        //part na na parse na and placed sa string
        jsonString = readFromFile();

        try {
            //accessing the json file
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("wordbank");
            //for (int i = 0; i < jsonArray.length(); i++) {
            //accessing 1 instance
            JSONObject oneWord = jsonArray.getJSONObject(4);
            JSONObject twoWord = jsonArray.getJSONObject(4);

            //download files
            downloadImage(oneWord);
            downloadEffect(twoWord);

//                temp = new WordBank();
//                temp.english = word.getString("English");
//                temp.cebuano = word.getString("Cebuano");
//                temp.pos = word.getString("POS");
//                temp.picture = word.getString("Picture");
//                temp.audio = word.getString("Audio");
//                wordbank.add(temp);
//
            //json.setText(oneWord.getString("English") + " "+oneWord.getString("Cebuano") + " "+ oneWord.getString("Effect"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // downloadImages(jsonString);
        // new JSONTask();


        //json.setText(here);

//        try {
//            //accessing the json file
//            JSONObject jsonObject = new JSONObject(jsonString);
//            JSONArray jsonArray = jsonObject.getJSONArray("wordbank");
//            for (int i = 0 ; i < jsonArray.length() ;i++){
//                //accessing 1 instance
//                JSONObject word = jsonArray.getJSONObject(1);
//                temp = new WordBank();
//                temp.english = word.getString("English");
//                temp.cebuano = word.getString("Cebuano");
//                temp.pos = word.getString("POS");
//                temp.picture = word.getString("Picture");
//                temp.audio = word.getString("Audio");
//                wordbank.add(temp);
//
//                //json.setText(word.getString("English") + word.getString("Cebuano"));
//            }
//            Toast.makeText(MainActivity.this, "Added!", Toast.LENGTH_SHORT).show();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

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

    private void downloadImage(JSONObject oneWord) {
        try {
            final StorageReference pictureReference = storage.getReferenceFromUrl("gs://bistalk-7833f.appspot.com").child("pictures/" + oneWord.getString("Picture"));
            final File imageFile = new File(getFilesDir(), "images/" + oneWord.getString("Picture").toString());

            pictureReference.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
//                    img.setImageBitmap(bitmap);
                    Toast.makeText(MainActivity.this, "Image Successfully Downloaded", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(MainActivity.this, "Image Not Downloaded", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void downloadEffect(JSONObject twoWord) {
        try {
            final StorageReference audioReference = storage.getReferenceFromUrl("gs://bistalk-7833f.appspot.com").child("effects/"+twoWord.getString("Effect"));
            final File audioFile = new File(getFilesDir(),"effects/"+twoWord.getString("Effect").toString());

            audioReference.getFile(audioFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this, "Audio is downloaded", Toast.LENGTH_SHORT).show();

                    // Play MP3
                    try {
                        MediaPlayer player = new MediaPlayer();
                        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        player.setDataSource("https://firebasestorage.googleapis.com/v0/b/bistalk-7833f.appspot.com/o/effects%2Fairplane.mp3?alt=media&token=0bfc07f6-5879-4d2e-8d48-c44e08786a16");
                        player.prepare();
                        player.start();
                        Toast.makeText(MainActivity.this, "Audio is downloaded", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(MainActivity.this, "Audio is not downloaded", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void gayson() {

        String storageUrl = "https://firebasestorage.googleapis.com/v0/b/bistalk-7833f.appspot.com/o/wordbank.json?alt=media&token=21f68d7f-7a1c-4b1d-aab1-0790bbe5644c";
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = firebaseStorage.getReferenceFromUrl(storageUrl);

        final File myFile = new File(getFilesDir(),"wordbank.json");
        reference.getFile(myFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                //  Toast.makeText(MainActivity.this, myFile.getName(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(MainActivity.this, "Download was unsuccessful", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getBytesTransferred()
                // taskSnapshot.getTotalByteCount();
            }
        });
    }

    private void createFolder() {
        final File f = new File(getFilesDir(), "images");
        final File f1 = new File(getFilesDir(), "effects");

        if (!f.exists() || !f1.exists()){
            if(!f.mkdir() || !f1.mkdir()){
                f1.mkdir();
                f.mkdir();
                Toast.makeText(this, f.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                // path.setText(f.getAbsolutePath());
            }else{
                //Toast.makeText(MainActivity.this, "Already Exist", Toast.LENGTH_SHORT).show();
            }
        }else{
            //path.setText(f.getAbsolutePath() + f1.getAbsolutePath());
            //Toast.makeText(this, "folder is already cretaed", Toast.LENGTH_SHORT).show();
        }
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
//                    String cebWord = message;
//                    if(cebWord != ""){
//                        Intent lol = new Intent(MainActivity.this,ShowData.class);
//                        lol.putExtra("Val", message);
//                        startActivity(lol);
//                    }else{
//                        Toast.makeText(this, "Word is not found", Toast.LENGTH_SHORT).show();
//                    }

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
