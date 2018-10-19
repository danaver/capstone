package com.example.gailsemilladucao.capstone;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.gailsemilladucao.capstone.backend.AddData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
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

public class DownloadCateg extends AppCompatActivity {

    public static final String LOG_TAG = "Android Downloader";

    Button noun,verb,adj,main;
    String jsonfile;
    JSONArray jsonArray;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public String category;

    //progress bar
    private ProgressDialog mProgressDialog;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_categ);

        noun = findViewById(R.id.noun);
        verb = findViewById(R.id.verb);
        adj = findViewById(R.id.adjective);
        main = findViewById(R.id.main);

        createFolder();

        jsonfile = readFromFile();
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(jsonfile);
            jsonArray = jsonObject.getJSONArray("wordbank");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        noun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "noun";
                new DownloadFileAsync().execute(category);
            }
        });

        verb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "verb";
                new DownloadFileAsync().execute(category);
            }
        });

        adj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category = "adjective";
                new DownloadFileAsync().execute(category);
            }
        });

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DownloadCateg.this, AddData.class);
                startActivity(intent);
            }
        });

    }

    //progress
    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {

            for (int i = 0; i <jsonArray.length();i++) {

                //images
                try {
                    JSONObject word = jsonArray.getJSONObject(i);
                    final StorageReference pictureReference = storage.getReferenceFromUrl("gs://bistalk-7833f.appspot.com").child("pictures/" + category + "/" + word.getString("Picture"));
                    final File imageFile = new File(getFilesDir(), "images/" + word.getString("Picture"));

                    pictureReference.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //effects
                try {
                    JSONObject word = jsonArray.getJSONObject(i);
                    final StorageReference audioFxReference = storage.getReferenceFromUrl("gs://bistalk-7833f.appspot.com").child("effects/" + category + "/" + word.getString("Effect"));
                    final File audioFile = new File(getFilesDir(), "effects/" + word.getString("Effect").toString());

                    audioFxReference.getFile(audioFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            //Toast.makeText(DownloadCateg.this, "AudioFx is downloaded", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //Toast.makeText(MainActivity.this, "Audio is not downloaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d(LOG_TAG,progress[0]);
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(String unused) {
            //dismiss the dialog after the file was downloaded
            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
        }
    }

    //our progress bar settings
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS: //we set this to 0
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Downloading File");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();

                return mProgressDialog;
            default:
                return null;
        }
    }



//    private void downloadCateg(String categ) throws JSONException {
//
//        for (int i = 0; i <jsonArray.length();i++){
//
//            JSONObject word = jsonArray.getJSONObject(i);
//
//            //images
//            try {
//                final StorageReference pictureReference = storage.getReferenceFromUrl("gs://bistalk-7833f.appspot.com").child("pictures/"+categ+"/" + word.getString("Picture"));
//                final File imageFile = new File(getFilesDir(), "images/" + word.getString("Picture"));
//
//                pictureReference.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//
//                    }
//                });
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            //effects
//            try {
//                final StorageReference audioFxReference = storage.getReferenceFromUrl("gs://bistalk-7833f.appspot.com").child("effects/"+categ+"/"+word.getString("Effect"));
//                final File audioFile = new File(getFilesDir(),"effects/"+word.getString("Effect").toString());
//
//                audioFxReference.getFile(audioFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                        Toast.makeText(DownloadCateg.this, "AudioFx is downloaded", Toast.LENGTH_SHORT).show();
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        //Toast.makeText(MainActivity.this, "Audio is not downloaded", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            //audio file
////            try {
////                final StorageReference audioReference = storage.getReferenceFromUrl("gs://bistalk-7833f.appspot.com").child("audio/"+categ+"/"+word.getString("Audio"));
////                final File audioFile = new File(getFilesDir(),"audio/"+word.getString("Effect").toString());
////
////                audioReference.getFile(audioFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
////                    @Override
////                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
////
////                    }
////                }).addOnFailureListener(new OnFailureListener() {
////                    @Override
////                    public void onFailure(@NonNull Exception exception) {
////                        //Toast.makeText(MainActivity.this, "Audio is not downloaded", Toast.LENGTH_SHORT).show();
////                    }
////                });
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
//
//
//
//        }
//    }

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

    private void createFolder() {
        final File image = new File(getFilesDir(), "images");
        final File effect = new File(getFilesDir(), "effects");
        final File audio = new File(getFilesDir(), "audio");

        if (!image.exists() || !effect.exists()|| !audio.exists()) {
            if (!image.mkdir() || !effect.mkdir() || !audio.exists()) {
                effect.mkdir();
                image.mkdir();
                audio.mkdir();
            }
        }
    }


}
