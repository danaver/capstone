package com.example.gailsemilladucao.capstone;

// =========== API =========== //

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gailsemilladucao.capstone.backend.AddData;
import com.example.gailsemilladucao.capstone.model.Bistalk;
import com.example.gailsemilladucao.capstone.model.wordbanks;
import com.example.gailsemilladucao.capstone.view.DownloadCateg;
import com.example.gailsemilladucao.capstone.view.ShowData;
import com.example.gailsemilladucao.capstone.view.Tips;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

// =========== PACKAGES =========== //

public class MainActivity extends AppCompatActivity {

    public TextView txvResult;
    public String message ="";
    Button searchy, logout, login;
    EditText wordie;
    Bistalk bistalk;

    // Navigation Drawer
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;

    // Session
    private static final String PREF_NAME = "MyPrefs";
    private static final String IS_FREE = "isFree";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    SharedPreferences pref;
    Editor editor;

    String jsonString;
    File gaysonFile,downsonFile ;
    DatabaseReference databaseRef;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txvResult = findViewById(R.id.txvResult);
        searchy = findViewById(R.id.searching);
        wordie = findViewById(R.id.wordie);
        dl = findViewById(R.id.dl);
        logout = findViewById(R.id.logout);
        login = findViewById(R.id.action_login);

        // Session
        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();

        //initializing
        gaysonFile = new File(getFilesDir(),"wordbank.json");
        downsonFile =new File(getFilesDir(),"update.json");

        // Create a storage reference from our app
        storageRef = FirebaseStorage.getInstance().getReference();

        // Database Reference
        databaseRef = FirebaseDatabase.getInstance().getReference("updates");

        if(!gaysonFile.exists()){
            gayson();
        }

        if(!downsonFile.exists()){
            upson();
        }



        // Navigation Drawer
        abdt = new ActionBarDrawerToggle(MainActivity.this, dl, R.string.open, R.string.close);
        dl.addDrawerListener(abdt);
        abdt.syncState();

        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if(id == R.id.add_word && pref.getBoolean(IS_FREE, true)) {
                    Toast.makeText(MainActivity.this, "Get Premium", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.add_word && !pref.getBoolean(IS_FREE, true)) {
                    Intent intent = new Intent(MainActivity.this, AddData.class);
                    startActivity(intent);
                }else if(id == R.id.download_package){
                    Intent intent = new Intent(MainActivity.this, DownloadCateg.class);
                    startActivity(intent);
                }else if(id == R.id.action_login){
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                }else if(id == R.id.tips){
                    Intent intent = new Intent(MainActivity.this, Tips.class);
                    startActivity(intent);
                }else if(id == R.id.action_premium){
                    Intent intent = new Intent(MainActivity.this, signup.class);
                    startActivity(intent);
                }else if(id == R.id.logout){
                    editor.clear();
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                }
                return true;
            }
        });


        //download json
        //gayson();

        //part na na parse na and placed sa string
        //this also is used global in searchWord()
        jsonString = readFromFile();
        bistalk = JsontoGson();

        if(isNetworkConnected() ==  true){
            try {
                delayUpload();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        searchy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                message = wordie.getText().toString().toLowerCase();
                if (!message.equals("")) {
                    //JSONObject res = null;
                    wordbanks res = null;
                    res = searchWord(message);
                    if (res != null) {
                        Intent lol = new Intent(MainActivity.this, ShowData.class);
                        //converted the
//
                        if(res.getStatus() != 0){
                            lol.putExtra("res", res);
                            startActivity(lol);
                        }else{
                            Toast.makeText(MainActivity.this, "Please download the word", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Word is not in the dictionary", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Enter word", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setDefault() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(IS_FREE, true);
        editor.putString(KEY_PASSWORD, "");
        editor.putString(KEY_EMAIL, "");
        editor.commit();
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



    private void gayson() {
        if (isNetworkConnected() == false) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }else {
            String storageUrl = "https://firebasestorage.googleapis.com/v0/b/bistalk-7833f.appspot.com/o/wordbank.json?alt=media&token=21f68d7f-7a1c-4b1d-aab1-0790bbe5644c";
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference reference = firebaseStorage.getReferenceFromUrl(storageUrl);


            reference.getFile(gaysonFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
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

                }
            });
        }
    }

    private void upson() {

        if (isNetworkConnected() == false) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }else{
            String storageUrl = "https://firebasestorage.googleapis.com/v0/b/bistalk-7833f.appspot.com/o/update.json?alt=media&token=ffea4fb6-6673-4ecc-b3f1-b93110041931";
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference reference = firebaseStorage.getReferenceFromUrl(storageUrl);


            reference.getFile(downsonFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Toast.makeText(DownloadCateg.this, upson.getName(), Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(MainActivity.this, "Download was unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
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
                    message = message.substring(0,1).toLowerCase() + message.substring(1).toLowerCase();
                    //access the db
                        wordbanks res = null;
                        res = searchWord(message);
                        if (res != null) {
                            Intent lol = new Intent(MainActivity.this, ShowData.class);
                            //converted the
                            if(res.getStatus() != 0){
                                lol.putExtra("res", res);
                                startActivity(lol);
                            }else{
                                Toast.makeText(MainActivity.this, "Please download the word", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Word is not in the dictionary", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "Enter word", Toast.LENGTH_SHORT).show();
                    }

                break;
        }
    }

    //called in getSpeechInput and searchings the word
      public wordbanks searchWord(String msg) {
        wordbanks match = null;

        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();


            for (int i = 0; i < bistalk.getWordbankList().size(); i++) {
                if (msg.equals(bistalk.getWordbankList().get(i).getEnglish())) {
                    return match = bistalk.getWordbankList().get(i);
                }
            }

        return match;
    }

    private Bistalk JsontoGson(){
        Bistalk bistalk = new com.google.gson.Gson().fromJson(jsonString, Bistalk.class);
        return bistalk;
    }

    public void delayUpload() throws JSONException {
        for (int i = 0; i <bistalk.getWordbankList().size();i++){
            if(bistalk.getWordbankList().get(i).getStatus() == 2){
                //call uploadtoStorage function
                // same to ur uploadFile() but instead of from the form.
                //data will be retrive through bistalk.getWordbankList().get(i).getName and etc
                //call uploadtoRDBM function
                uploadFile(bistalk.getWordbankList().get(i));
                bistalk.getWordbankList().get(i).setStatus(3);
            }
        }

        GsontoJson(bistalk);

    }

    private void uploadFile(wordbanks word) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Files, Please wait...");
        progressDialog.show();

        Uri audioFileUri = Uri.parse(getFilesDir() + word.getCebuano());
        Uri imageFileUri = Uri.parse(getFilesDir() + word.getEnglish());
        Uri audioFxUri = Uri.parse(getFilesDir() + word.getEnglish());

        if (audioFileUri != null) {
            StorageReference imageReference = storageRef.child("updates_photo").child(word.getEnglish().trim() + "");
            StorageReference audioRef = storageRef.child("updates_audio").child(word.getCebuano().trim() + ""); // storage location to firebase.
            StorageReference fxRef = storageRef.child("updates_effect").child(word.getEnglish().trim() + ""); // storage location to firebase

            // Upload attach audio file
            audioRef.putFile(audioFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Audio Uploaded!", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            // Upload for attach effects audio file
            if(audioFxUri != null){
                fxRef.putFile(audioFxUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                       // Toast.makeText(getApplicationContext(), "Audio Effect Uploaded! ", Toast.LENGTH_LONG).show();

                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) { // When loading progress is paused
                       // System.out.println("Upload is paused");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { // If progress fails
                       // Toast.makeText(getApplicationContext(), "Audio Effect Failed! ", Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) { // During upload progress
//                        double progress = (100.0 * taskSnapshot.getBytesTransferred())/ taskSnapshot.getTotalByteCount();
//                        System.out.println("Upload is " + progress + " % done");
                    }
                });
            }

            // Upload for Image
            if(imageFileUri != null){
                imageReference.putFile(imageFileUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Image File Uploaded ", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();

                        //and displaying error message
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }


            // Upload editText value to RDBM
            DatabaseReference englishRef = databaseRef.child("381");

            String english = word.getEnglish();
            String cebuano = word.getCebuano();
            String pronunciation = word.getPronunciation();
            String audio = word.getAudio();
            String image = word.getPicture();
            String fx = word.getEffect();
            String category = word.getCategory();
            int status = 3;

            // push to the firabase database
            String id = englishRef.push().getKey();
            databaseRef.child(id).child("Audio").setValue(audio);
            databaseRef.child(id).child("Category").setValue(category);
            databaseRef.child(id).child("Cebuano").setValue(cebuano);
            databaseRef.child(id).child("Effect").setValue(fx);
            databaseRef.child(id).child("English").setValue(english);
            databaseRef.child(id).child("Picture").setValue(image);
            databaseRef.child(id).child("Pronunciation").setValue(pronunciation);
            databaseRef.child(id).child("Status").setValue(status);

        } else {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "No file selected. Audio File Required!", Toast.LENGTH_LONG).show();
        }

    }

    public void GsontoJson(Bistalk besh) throws JSONException {

        //Gson gson = new Gson(); raman ta na idk nganong mo error haahaha
        com.google.gson.Gson gson = new com.google.gson.Gson();

        Bistalk bistalk = new Bistalk(besh.getUpdate(),besh.getUserList(),besh.getWordbankList());
        String json = gson.toJson(bistalk);

        // this will overwrite the jsonfile
        try {
            writeFile(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void writeFile(String data) throws IOException {
        File outFile = new File(getFilesDir(), "wordbank.json");
        FileOutputStream out = new FileOutputStream(outFile, false);
        byte[] contents = data.getBytes();
        out.write(contents);
        out.flush();
        out.close();
    }

}
