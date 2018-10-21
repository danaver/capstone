package com.example.gailsemilladucao.capstone;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.gailsemilladucao.capstone.backend.AddData;
import com.example.gailsemilladucao.capstone.view.viewCateg;
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

public class DownloadCateg extends AppCompatActivity {

    Button noun,verb,adj,main,deln,dela,delv,viewnoun,viewverb,viewadj;
    String jsonfile;
    JSONArray jsonArray;
    FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_categ);

        noun = findViewById(R.id.noun);
        verb = findViewById(R.id.verb);
        adj = findViewById(R.id.adjective);
        main = findViewById(R.id.main);
        deln = findViewById(R.id.delnoun);
        dela = findViewById(R.id.deladjective);
        delv = findViewById(R.id.delverb);
        viewnoun = findViewById(R.id.viewnoun);
        viewverb = findViewById(R.id.viewverb);
        viewadj = findViewById(R.id.viewadj);

        createFolder();
        //gayson();

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
                try {
                    downloadCateg("noun");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        verb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    downloadCateg("verb");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        adj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    downloadCateg("adjective");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        deln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteCateg("noun");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        delv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteCateg("verb");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        dela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteCateg("adjective");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        viewnoun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lol = new Intent(DownloadCateg.this,viewCateg.class);
                lol.putExtra("Val","noun");
                startActivity(lol);
            }
        });

        viewverb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lol = new Intent(DownloadCateg.this,viewCateg.class);
                lol.putExtra("Val","verb");
                startActivity(lol);
            }
        });
        viewadj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lol = new Intent(DownloadCateg.this,viewCateg.class);
                lol.putExtra("Val","adjective");
                startActivity(lol);
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

    //returns true if connected
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void deleteCateg(String categ) throws JSONException {

        String imgpath;
        String audiopath;
        String fxpath;
        for (int i = 0; i <jsonArray.length();i++) {
            JSONObject word = jsonArray.getJSONObject(i);

            if(word.getString("POS").equals(categ)){
                if(word.has("Picture")){
                    imgpath = getFilesDir() + "/images/" + word.getString("Picture");
                    File imgfile = new File(imgpath);
                    imgfile.delete();
                }

                if(word.has("Effect")){
                    fxpath = getFilesDir() + "/effects/" + word.getString("Effect");
                    File fxfile = new File(fxpath);
                    fxfile.delete();
                }

                if(word.has("Audio")){
                    audiopath = getFilesDir() + "/audio/" + word.getString("Audio");
                    File audiofile = new File(audiopath);
                    audiofile.delete();
                }
            }
        }

        Toast.makeText(this, "All Data in "+categ+" has been deleted", Toast.LENGTH_SHORT).show();
    }


    private void downloadCateg(String categ) throws JSONException {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading Files");

        if (isNetworkConnected() == true) {
            progressDialog.show();
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

        for (int i = 0; i <jsonArray.length();i++){

            JSONObject word = jsonArray.getJSONObject(i);

            //images
            try {
                final StorageReference pictureReference = storage.getReferenceFromUrl("gs://bistalk-7833f.appspot.com").child("pictures/"+categ+"/" + word.getString("Picture"));
                final File imageFile = new File(getFilesDir(), "images/" + word.getString("Picture"));

                pictureReference.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //effects
            try {
                final StorageReference audioFxReference = storage.getReferenceFromUrl("gs://bistalk-7833f.appspot.com").child("effects/"+categ+"/"+word.getString("Effect"));
                final File audioFile = new File(getFilesDir(),"effects/"+word.getString("Effect").toString());

                audioFxReference.getFile(audioFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //audio file
//            try {
//                final StorageReference audioReference = storage.getReferenceFromUrl("gs://bistalk-7833f.appspot.com").child("audio/"+categ+"/"+word.getString("Audio"));
//                final File audioFile = new File(getFilesDir(),"audio/"+word.getString("Effect").toString());
//
//                audioReference.getFile(audioFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
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



        }
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
                Toast.makeText(DownloadCateg.this, "Download was unsuccessful", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }


}
