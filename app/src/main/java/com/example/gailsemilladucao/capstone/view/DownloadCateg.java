package com.example.gailsemilladucao.capstone.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.gailsemilladucao.capstone.R;
import com.example.gailsemilladucao.capstone.model.Bistalk;
import com.example.gailsemilladucao.capstone.model.categ;
import com.example.gailsemilladucao.capstone.model.categAdapter;
import com.example.gailsemilladucao.capstone.view.viewCateg;
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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DownloadCateg extends AppCompatActivity {

    List<categ> clist;
    String jsonfile;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Bistalk list;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_categ);

        String[] categor = getResources().getStringArray(R.array.post);
        String[] name = getResources().getStringArray(R.array.categ);
        TypedArray imgs = getResources().obtainTypedArray(R.array.draw);

        createFolder();
        // gayson();

        jsonfile = readFromFile();
        list = JsontoGson();

        clist = new ArrayList<>();

        for (int i = 0; i< name.length;i++){
            clist.add(new categ(name[i],categor[i],imgs.getResourceId(i, -1)));
        }

        buildRV();

        Toast.makeText(DownloadCateg.this, "Scroll down for more categories", Toast.LENGTH_SHORT).show();



    }

    public void buildRV(){
        RecyclerView view = findViewById(R.id.rv);
        categAdapter adapter = new categAdapter(this, clist);
        view.setLayoutManager(new GridLayoutManager(this, 3 ));
        view.setAdapter(adapter);

        adapter.setOnItemClickListener(new categAdapter.OnItemClickListener() {
            @Override
            public void onDownloadClick(int i) {
                try {
                    String categ = clist.get(i).getCateg();
                    downloadCateg(categ);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onDeleteClick(int i) {
                try {
                    String categ = clist.get(i).getCateg();
                    deleteCateg(categ);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onViewClick(int i) {
                String categ = clist.get(i).getCateg();
                Intent lol = new Intent(DownloadCateg.this,viewCateg.class);
                lol.putExtra("Val",categ);
                startActivity(lol);
            }
        });

    }



    private void deleteCateg(String categ) throws JSONException {

        String imgpath;
        String audiopath;
        String fxpath;
        for (int i = 0; i < list.getWordbankList().size(); i++) {
            if(list.getWordbankList().get(i).getCategory().equals(categ)){
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void downloadCateg(String categ) throws JSONException {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading Files");

        if (isNetworkConnected() != true) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();


            for (int i = 0; i < list.getWordbankList().size(); i++) {


                if (list.getWordbankList().get(i).getCategory().equals(categ)) {

                    //images
                    final StorageReference pictureReference = storage.getReferenceFromUrl("gs://bistalk-7833f.appspot.com").child("pictures/" + categ + "/" + list.getWordbankList().get(i).getPicture());
                    final File imageFile = new File(getFilesDir(), "images/" + list.getWordbankList().get(i).getPicture());

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
                    final StorageReference audioFxReference = storage.getReferenceFromUrl("gs://bistalk-7833f.appspot.com").child("effects/" + categ + "/" + list.getWordbankList().get(i).getEffect());
                    final File audioFxFile = new File(getFilesDir(), "effects/" + list.getWordbankList().get(i).getEffect());

                    audioFxReference.getFile(audioFxFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
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


                    //audio file
                    final StorageReference audioReference = storage.getReferenceFromUrl("gs://bistalk-7833f.appspot.com").child("audio/" + categ + "/" + list.getWordbankList().get(i).getAudio());
                    final File audioFile = new File(getFilesDir(), "audio/" + list.getWordbankList().get(i).getAudio());

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

                    list.getWordbankList().get(i).setStatus(1);
                }
            }

            GsontoJson(list);
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

    private void gayson() {

        if (isNetworkConnected() == false) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

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

    void writeFile(String data) throws IOException {
        File outFile = new File(getFilesDir(), "wordbank.json");
        FileOutputStream out = new FileOutputStream(outFile, false);
        byte[] contents = data.getBytes();
        out.write(contents);
        out.flush();
        out.close();
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
