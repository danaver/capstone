package com.example.gailsemilladucao.capstone.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.gailsemilladucao.capstone.MainActivity;
import com.example.gailsemilladucao.capstone.R;
import com.example.gailsemilladucao.capstone.backend.AddData;
import com.example.gailsemilladucao.capstone.model.Bistalk;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DownloadCateg extends AppCompatActivity {

    Button noun, verb, adj, main, deln, dela, delv, viewnoun, viewverb, viewadj;
    String jsonfile;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    Bistalk list;

    // Navigation Drawer
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;

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
        gayson();

        jsonfile = readFromFile();
        list = JsontoGson();

        dl = findViewById(R.id.dl);

        // Navigation Drawer
        abdt = new ActionBarDrawerToggle(DownloadCateg.this, dl, R.string.open, R.string.close);
        dl.addDrawerListener(abdt);
        abdt.syncState();

        NavigationView nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if(id == R.id.add_word){
                    Intent intent = new Intent(DownloadCateg.this,AddData.class);
                    startActivity(intent);
                }else if(id == R.id.download_package){
                    Intent intent = new Intent(DownloadCateg.this, DownloadCateg.class);
                    startActivity(intent);
                }else if(id == R.id.home){
                    Intent intent = new Intent(DownloadCateg.this, DownloadCateg.class);
                    startActivity(intent);
                }
                return true;
            }
        });


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
        for (int i = 0; i < list.getWordbankList().size(); i++) {


            if(list.getWordbankList().get(i).getPos().equals(categ)){
                imgpath = getFilesDir() + "/images/" + list.getWordbankList().get(i).getPicture();
                File imgfile = new File(imgpath);
                imgfile.delete();

                fxpath = getFilesDir() + "/effects/" + list.getWordbankList().get(i).getEffect();
                File fxfile = new File(fxpath);
                fxfile.delete();


                audiopath = getFilesDir() + "/audio/" + list.getWordbankList().get(i).getAudio();
                File audiofile = new File(audiopath);
                audiofile.delete();

                list.getWordbankList().get(i).setStatus(0);
            }
        }

        GsontoJson(list);

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

        for (int i = 0; i < list.getWordbankList().size(); i++){

            //images
            if(list.getWordbankList().get(i).getPos().equals(categ)) {
                final StorageReference pictureReference = storage.getReferenceFromUrl("gs://bistalk-7833f.appspot.com").child("pictures/"+categ+"/" + list.getWordbankList().get(i).getPicture());
                final File imageFile = new File(getFilesDir(), "images/" + list.getWordbankList().get(i).getPicture());

                list.getWordbankList().get(i).setStatus(1);
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

                //effects
                final StorageReference audioFxReference = storage.getReferenceFromUrl("gs://bistalk-7833f.appspot.com").child("effects/"+categ+"/"+ list.getWordbankList().get(i).getEffect());
                final File audioFile = new File(getFilesDir(),"effects/"+ list.getWordbankList().get(i).getEffect());

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



            }

            //audio file
            final StorageReference audioReference = storage.getReferenceFromUrl("gs://bistalk-7833f.appspot.com").child("audio/"+categ+"/"+list.getWordbankList().get(i).getAudio());
            final File audioFile = new File(getFilesDir(),"audio/"+list.getWordbankList().get(i).getAudio());

            audioReference.getFile(audioFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //Toast.makeText(MainActivity.this, "Audio is not downloaded", Toast.LENGTH_SHORT).show();
                }
            });


        }

        GsontoJson(list);

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
                Toast.makeText(DownloadCateg.this, myFile.getName(), Toast.LENGTH_SHORT).show();
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

    private Bistalk JsontoGson(){
        Bistalk bistalk = new com.google.gson.Gson().fromJson(jsonfile, Bistalk.class);
        return bistalk;
    }

    public void GsontoJson(Bistalk besh) throws JSONException {


        com.google.gson.Gson gson = new com.google.gson.Gson();

        Bistalk bistalk = new Bistalk(besh.getUserList(),besh.getWordbankList());
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