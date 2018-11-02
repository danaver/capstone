package com.example.gailsemilladucao.capstone.backend;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gailsemilladucao.capstone.Gson;
import com.example.gailsemilladucao.capstone.MainActivity;
import com.example.gailsemilladucao.capstone.R;
import com.example.gailsemilladucao.capstone.model.Bistalk;
import com.example.gailsemilladucao.capstone.model.wordbanks;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

public class AddData extends AppCompatActivity {

    ImageView mimage;
    Button recstop,play,attach,addData, attach_fx;
    String savepath = "",fxpath = "",srcPath =null,jsonstring;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    TextView info,info_effect;
    EditText engText,cebText,prunoun;
    Uri audioFileUri, audioFxUri, imageFileUri;
    Bistalk bistalk;
    Spinner drop;


    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null;
    private static final String LOG_TAG = "Audio Record Test";


    final int REQUEST_PERMISSION_CODE = 1000;
    final int REQUEST_PERMISSION_GALLERY = 999;
    final static int RQS_OPEN_AUDIO_MP3 = 1;
    final static int RQS_OPEN_AUDIO_FX = 2;

    //FIREBASE
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);


        gayson();


        // Create a storage reference from our app
        storageRef = FirebaseStorage.getInstance().getReference("temp");

        //request runtime permission
        if(!checkPermissionFromDevice())
            requestPermission();

        //binding
        info = findViewById(R.id.info);
        attach = findViewById(R.id.attach);
        mimage = findViewById(R.id.image);

        recstop =  findViewById(R.id.recstop);
        play = findViewById(R.id.play);

        addData = findViewById(R.id.addData);
        attach_fx = findViewById(R.id.attach_effect);
        info_effect = findViewById(R.id.info_effect);
        engText = findViewById(R.id.engtb);
        cebText = findViewById(R.id.bistb);
        drop = findViewById(R.id.pos);
        prunoun = findViewById(R.id.prunoun);


        //spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddData.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.post));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drop.setAdapter(adapter);

        //json
        jsonstring = readFromFile();

        bistalk = JsontoGson();



        // Upload Image to Firebase
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //uploadFile();
                localAdd(bistalk);
            }
        });
        //from android m, you need request runtime permission


        // Attach audio effects
        attach_fx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Open Audio Effect (mp3) file"), RQS_OPEN_AUDIO_FX);
                fxpath = info_effect.getText().toString();
            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lol = new Intent();
                lol.setType("audio/*");
                lol.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(
                        lol, "Open Audio (mp3) file"), RQS_OPEN_AUDIO_MP3);
            }
        });





        mimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //external storage permission
                //runtime permision for android 6.0 and above
                ActivityCompat.requestPermissions(
                        AddData.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_GALLERY
                );
            }
        });
    }





    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_PERMISSION_CODE);
    }

    //press CTRL+O


    //for audio
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            case REQUEST_PERMISSION_GALLERY:
            {
                if(grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //gallery intent
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, REQUEST_PERMISSION_GALLERY);
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }


    private boolean checkPermissionFromDevice() {
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    //for gallery

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        // Image File
        if(requestCode == REQUEST_PERMISSION_GALLERY && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON) //crop guide lines
                    .setAspectRatio(1, 1) //square croping
                    .start(this);
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                imageFileUri = result.getUri();
                //set image choosen from gallery to image view
                mimage.setImageURI(imageFileUri);
            }else if( resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }

        // AUDIO
        if (resultCode == RESULT_OK) {
            if (requestCode == RQS_OPEN_AUDIO_MP3) {
                audioFileUri = data.getData();
                // This lets you set the path sa TextView
                srcPath = audioFileUri.getPath();
                info.setText(audioFileUri.toString() + "\n" + srcPath);

            }
        }

        // AUDIO FX
        if (resultCode == RESULT_OK) {
            if (requestCode == RQS_OPEN_AUDIO_FX) {
                audioFxUri = data.getData();
                //vvv This lets you set the path sa TextView
                srcPath = audioFxUri.getPath();
                info_effect.setText(audioFxUri.toString() + "\n" + srcPath);

            }
        }
    }


    private void uploadFile() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Files, Please wait...");
        progressDialog.show();

        if (audioFileUri != null) {
            StorageReference imageReference = storageRef.child("images").child(engText.getText().toString().trim() + "");
            StorageReference audioRef = storageRef.child("audio").child(engText.getText().toString().trim() + ""); // storage location to firebase.
            StorageReference fxRef = storageRef.child("effects").child(engText.getText().toString().trim() + ""); // storage location to firebase

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
                        Toast.makeText(getApplicationContext(), "Audio Effect Uploaded! ", Toast.LENGTH_LONG).show();

                    }
                }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onPaused(UploadTask.TaskSnapshot taskSnapshot) { // When loading progress is paused
                        System.out.println("Upload is paused");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { // If progress fails
                        Toast.makeText(getApplicationContext(), "Audio Effect Failed! ", Toast.LENGTH_LONG).show();
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
        } else {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "No file selected. Audio File Required!", Toast.LENGTH_LONG).show();
        }
    }



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

    public void menu(View view) {
        Intent intent = new Intent(AddData.this, MainActivity.class);
        startActivity(intent);
    }

    public void localAdd(Bistalk bistalk){

        String jeng,jceb,jfx="null",jpru="";
        int jstat;

        boolean internet = false;
        wordbanks word = new wordbanks();

        //getiing the  text
        jeng = engText.getText().toString();
        jceb = cebText.getText().toString();
        jpru = prunoun.toString();





        if (jeng.equals("")&&jceb.equals("")){
            Toast.makeText(AddData.this, "Please fill all blanks", Toast.LENGTH_SHORT).show();
        }else{
            //image to json and copy to internal
            //image to local
            if(imageFileUri==null) {
                Toast.makeText(AddData.this, "Please Include an Image", Toast.LENGTH_SHORT).show();
            }else{
                try {
                    //image to local

                    ParcelFileDescriptor parcelFileDescriptorImage =
                            getContentResolver().openFileDescriptor(imageFileUri, "r");
                    FileDescriptor imagefileDescriptor = parcelFileDescriptorImage.getFileDescriptor();
                    Bitmap bitmap = BitmapFactory.decodeFileDescriptor(imagefileDescriptor);

                    ContextWrapper wrapper = new ContextWrapper(getApplicationContext());

                    //change the name to proper name
                    File imgFile = new File(wrapper.getFilesDir() + "/images", jeng + ".png");

                    OutputStream istream = null;
                    istream = new FileOutputStream(imgFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, istream);
                    istream.flush();
                    istream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //audiofx to json and copy to internal
                if(audioFxUri== null) {
                    jfx="null";
                }else {
                        File audfx = new File(getFilesDir() + "/effects", jeng + ".mp3");

                        ContentResolver contentResolver = getContentResolver();
                        InputStream in = null;
                        try {
                            in = contentResolver.openInputStream(audioFxUri);
                            OutputStream out = new FileOutputStream(audfx);
                            // Copy the bits from instream to outstream
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                            in.close();
                            out.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                //audio to internal
                if(audioFileUri==null) {
                    Toast.makeText(AddData.this, "Please attach an audio file", Toast.LENGTH_SHORT).show();

                }else{
                    File aud = new File(getFilesDir() + "/audio", jceb + ".mp3");

                    ContentResolver contentResolvers = getContentResolver();
                    InputStream ins = null;
                    try {
                        ins = contentResolvers.openInputStream(audioFileUri);
                        OutputStream out = new FileOutputStream(aud);
                        // Copy the bits from instream to outstream
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = ins.read(buf)) > 0) {
                            out.write(buf, 0, len);
                        }
                        ins.close();
                        out.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //INSERT WORDS HERE NA
                    word.setEnglish(jeng.toLowerCase());
                    word.setCebuano(jceb.toLowerCase());
                    word.setPronunciation(jpru);

                    for (int i = 0; bistalk.getWordbankList().size()>i;i++){
                        if(bistalk.getWordbankList().get(i).getAudio().equals(jceb+".mp3")){
                            jceb = jceb+"1";
                        }
                    }


                    word.setAudio(jceb+".mp3");
                    word.setPicture(jeng+".png");
                    word.setCategory(jpru);
                    word.setEffect(jfx);

                    if(audioFxUri != null){
                        word.setEffect(jfx);
                    }
                    internet = isNetworkConnected();
                    if(internet == false){
                        word.setStatus(2);
                    }else{
                        word.setStatus(3);
                    }


                    String val = wordExist(jeng);
                    if(val == null){
                        bistalk.getWordbankList().add(word);
                        try {
                            GsontoJson(bistalk);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent lol = new Intent(AddData.this,MainActivity.class);
                        startActivity(lol);
                    }else{
                        Toast.makeText(AddData.this, "Word is already exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String wordExist(String word){
        String match = null;
        for (int i = 0; bistalk.getWordbankList().size()>i;i++){
            if(bistalk.getWordbankList().get(i).getEnglish().equals(word.toLowerCase())){
               return match = bistalk.getWordbankList().get(i).getEnglish();
            }
        }
        return match;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void GsontoJson(Bistalk besh) throws JSONException {

        //Gson gson = new Gson(); raman ta na idk nganong mo error haahaha
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
                Toast.makeText(AddData.this, "Download was unsuccessful", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }
}













