package com.example.gailsemilladucao.capstone.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.gailsemilladucao.capstone.Login;
import com.example.gailsemilladucao.capstone.MainActivity;
import com.example.gailsemilladucao.capstone.R;
import com.example.gailsemilladucao.capstone.backend.AddData;
import com.example.gailsemilladucao.capstone.model.Bistalk;
import com.example.gailsemilladucao.capstone.model.categ;
import com.example.gailsemilladucao.capstone.model.categAdapter;
import com.example.gailsemilladucao.capstone.model.wordbanks;
import com.example.gailsemilladucao.capstone.signup;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
import java.util.Arrays;
import java.util.List;

public class DownloadCateg extends AppCompatActivity {

    List<categ> clist;
    List<wordbanks>  sample;
    String jsonfile,jsonupdate;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseDatabase db = FirebaseDatabase.getInstance();

    Bistalk list,upson;


    //Collec
    DatabaseReference wordbank;
    DatabaseReference databaseRef;
    StorageReference storageRef;

    // Navigation Drawer
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;

    // Session
    private static final String PREF_NAME = "MyPrefs";
    private static final String IS_FREE = "isFree";
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_categ);

        String[] categor = getResources().getStringArray(R.array.post);
        String[] name = getResources().getStringArray(R.array.categ);
        TypedArray imgs = getResources().obtainTypedArray(R.array.draw);
        dl = findViewById(R.id.dl);

        boolean update;

        createFolder();

        // Database Reference
        wordbank = FirebaseDatabase.getInstance().getReference("wordbank");
        databaseRef = FirebaseDatabase.getInstance().getReference("temp");



        jsonfile = readFromFile("wordbank.json");
        jsonupdate =readFromFile("update.json");
        list = JsontoGson(jsonfile);
        upson = JsontoGson(jsonupdate);

        if(isNetworkConnected()){
            updateson();
            //download updatejson
            update = checkUpdate(list,upson);
            if(update == true){
                try {
                    appendUpdate();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {
                delayUpload();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //display object



        // Session
        pref = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Navigation Drawer
        abdt = new ActionBarDrawerToggle(DownloadCateg.this, dl, R.string.open, R.string.close);
        dl.addDrawerListener(abdt);
        abdt.syncState();

        NavigationView nav_view = findViewById(R.id.nav_view);

        // Hiding
        if(pref.getBoolean(IS_FREE, true)){
            nav_view.getMenu().findItem(R.id.action_login).setVisible(true);
            nav_view.getMenu().findItem(R.id.logout).setVisible(false);
            nav_view.getMenu().findItem(R.id.action_premium).setVisible(true);
        }else if(!pref.getBoolean(IS_FREE, true)){
            nav_view.getMenu().findItem(R.id.action_login).setVisible(false);
            nav_view.getMenu().findItem(R.id.logout).setVisible(true);
            nav_view.getMenu().findItem(R.id.action_premium).setVisible(false);
        }

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if(id == R.id.add_word && pref.getBoolean(IS_FREE, true)) {
                    Toast.makeText(DownloadCateg.this, "Get Premium", Toast.LENGTH_SHORT).show();
                }else if(id == R.id.add_word && !pref.getBoolean(IS_FREE, true)) {
                    Intent intent = new Intent(DownloadCateg.this, AddData.class);
                    startActivity(intent);
                }else if(id == R.id.download_package){
                    Intent intent = new Intent(DownloadCateg.this, DownloadCateg.class);
                    startActivity(intent);
                }else if(id == R.id.action_login){
                    Intent intent = new Intent(DownloadCateg.this, Login.class);
                    startActivity(intent);
                }else if(id == R.id.tips){
                    Intent intent = new Intent(DownloadCateg.this, Tips.class);
                    startActivity(intent);
                }else if(id == R.id.action_premium){
                    Intent intent = new Intent(DownloadCateg.this, signup.class);
                    startActivity(intent);
                }else if(id == R.id.logout && !pref.getBoolean(IS_FREE, true)){
                    Intent intent = new Intent(DownloadCateg.this, Login.class);
                    startActivity(intent);
                }else if(id == R.id.home){
                    Intent intent = new Intent(DownloadCateg.this, MainActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        clist = new ArrayList<>();

        for (int i = 0; i< name.length;i++){
            clist.add(new categ(name[i],categor[i],imgs.getResourceId(i, -1)));
        }

        buildRV();

    }





    private void delayUpload() throws JSONException {
        for (int i = 0; i <list.getWordbankList().size();i++){
            if(list.getWordbankList().get(i).getStatus() == 2){
                //call uploadtoStorage function
                // same to ur uploadFile() but instead of from the form.
                //data will be retrive through bistalk.getWordbankList().get(i).getName and etc
                //call uploadtoRDBM function
                uploadFile(list.getWordbankList().get(i));
                list.getWordbankList().get(i).setStatus(3);
            }
        }

        GsontoJson(list);

    }

    public void openDialog(){
        dialog dlg = new dialog();
        dlg.show(getSupportFragmentManager(), "Update");
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
            public void onDeleteClick(final int i) {

                final int idx = i;

                AlertDialog.Builder alert = new AlertDialog.Builder(DownloadCateg.this);
                alert.setTitle("Delete Data in "+clist.get(idx).getName()+ "?");
                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String categ = clist.get(idx).getCateg();
                            deleteCateg(categ);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alert.show();
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
                if(list.getWordbankList().get(i).getStatus() == 1 ) {
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
                    if(list.getWordbankList().get(i).getStatus() == 0) {

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
            }

            GsontoJson(list);
        }

    }


    private String readFromFile(String json) {
        String ret = "";

        try {
            InputStream inputStream = openFileInput(json);

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

    private Bistalk JsontoGson(String jsonstring){
        Bistalk bistalk = new com.google.gson.Gson().fromJson(jsonstring, Bistalk.class);
        return bistalk;
    }

    public void GsontoJson(Bistalk besh) throws JSONException {

        com.google.gson.Gson gson = new com.google.gson.Gson();

        Bistalk bistalk = new Bistalk(besh.getUpdate(),besh.getWordbankList());
        String json = gson.toJson(bistalk);

        // this will overwrite the jsonfile
        try {
            writeFile(json);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void updateson() {

        if (isNetworkConnected() == false) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }else{
            String storageUrl = "https://firebasestorage.googleapis.com/v0/b/bistalk-7833f.appspot.com/o/update.json?alt=media&token=ffea4fb6-6673-4ecc-b3f1-b93110041931";
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference reference = firebaseStorage.getReferenceFromUrl(storageUrl);

            final File upson = new File(getFilesDir(),"update.json");
            reference.getFile(upson).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Toast.makeText(DownloadCateg.this, upson.getName(), Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //  Toast.makeText(DownloadCateg.this, "Download was unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {

                }
            });
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

    //it will add at the last of the wordbank
    private void appendUpdate() throws JSONException {
        for (int i = 0; i <upson.getWordbankList().size();i++){
            list.getWordbankList().add(upson.getWordbankList().get(i));

        }
        list.setUpdate(upson.getUpdate());
        GsontoJson(list);
        openDialog();
    }

    //if it returns true it means update has a new update
    private boolean checkUpdate(Bistalk wb,Bistalk up){
        boolean toUpdate = false;
        int updatewb,updateup;

        updatewb = wb.getUpdate();
        updateup =up.getUpdate();

        if(updatewb < updateup){
            toUpdate = true;
        }
        return toUpdate;
    }

    private void uploadFile(wordbanks word) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Files, Please wait...");
        progressDialog.show();

        Uri audioFileUri = Uri.parse(getFilesDir() + word.getCebuano());
        Uri imageFileUri = Uri.parse(getFilesDir() + word.getEnglish());
        Uri audioFxUri = Uri.parse(getFilesDir() + word.getEnglish());

        if (audioFileUri != null) {
            StorageReference imageReference = storageRef.child("photo").child(word.getEnglish().trim() + "");
            StorageReference audioRef = storageRef.child("audio").child(word.getCebuano().trim() + ""); // storage location to firebase.
            StorageReference fxRef = storageRef.child("effect").child(word.getEnglish().trim() + ""); // storage location to firebase

            // Upload attach audio file
            audioRef.putFile(audioFileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    //  Toast.makeText(getApplicationContext(), "Audio Uploaded!", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    //
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            // Upload for attach effects audio file
            if(audioFxUri != null){
                fxRef.putFile(audioFxUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { // If progress fails
                        // Toast.makeText(getApplicationContext(), "Audio Effect Failed! ", Toast.LENGTH_LONG).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) { // During upload progress
                        double progress = (100.0 * taskSnapshot.getBytesTransferred())/ taskSnapshot.getTotalByteCount();
                        System.out.println("Upload is " + progress + " % done");
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
            // Toast.makeText(getApplicationContext(), "No file selected. Audio File Required!", Toast.LENGTH_LONG).show();
        }

    }


}
