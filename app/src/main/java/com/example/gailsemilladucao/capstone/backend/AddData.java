package com.example.gailsemilladucao.capstone.backend;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AddData extends AppCompatActivity {

    ImageView mimage;
    Button recstart,recstop,play,pause,attach,addData, attach_fx;
    String savepath = "",fxpath = "",srcPath =null,jsonstring;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    TextView info,info_effect;
    EditText engText,cebText;
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
        initControls();

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


        //spinner
        String pos [] =  getResources().getStringArray(R.array.pos);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddData.this,R.layout.spinner,pos);
        // adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // drop.setAdapter(adapter);

        //json
        jsonstring = readFromFile();

        bistalk = JsontoGson();



        // Upload Image to Firebase
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
                //localAdd(bistalk);
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

        recstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                recstop.setEnabled(false);
                play.setEnabled(true);
                recstart.setEnabled(true);
                pause.setEnabled(false);
            }
        });

        //The Play Button is an event that plays the record
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(audioFileUri != null || savepath != "") {
                    pause.setEnabled(true);
                    recstop.setEnabled(false);
                    recstart.setEnabled(false);
                    play.setEnabled(false);

                    mediaPlayer = new MediaPlayer();

                    try {
                        //It checks wether the TextView->Where the file path of the attach file is shown, is empty or not
                        //if it is empty, then it will assume that it will play on the recorded audio
                        if (savepath != "" && info.getText().toString() == savepath) {
                            mediaPlayer.setDataSource(savepath);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            Toast.makeText(AddData.this, "Playing Recorded Audio...", Toast.LENGTH_SHORT).show();
                        }
                        //else, it will read the filepath inn the textview and play the audio
                        else {
                            //code here is needed to play the attach audio file
                            savepath = info.getText().toString();//this portion gets the text that was in the TextView
                            fxpath = info_effect.getText().toString(); // this portion gets the effect text.
                            mediaPlayer.setDataSource(AddData.this, audioFileUri);//will read the path
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            Toast.makeText(AddData.this, "Playing Attached Audio...", Toast.LENGTH_SHORT).show();

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(AddData.this, "File attach is not found", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AddData.this, "Please Record or Attach a Files ", Toast.LENGTH_SHORT).show();
                }
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

    // Adjust Volume Settings
    private void initControls() {
        try
        {
            //binding
            volumeSeekbar = (SeekBar)findViewById(R.id.seekBar);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onStopTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(savepath);
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

        boolean internet = false;
        wordbanks word = new wordbanks();

        word.setEnglish(engText.getText().toString());
        word.setCebuano(cebText.getText().toString());

        //image to json and copy to internal
        File imgname =new File(imageFileUri.getPath());
        String imgextension = imgname.getAbsolutePath().substring(imgname.getAbsolutePath().lastIndexOf("."));
        String imgFile = imgname.getName() + imgextension;
        File img = new File(getFilesDir(),"images/"+imgFile);
        word.setPicture(imgFile);

        //audio to json and copy to internal
        File audioname = new File(audioFileUri.getPath());
        String audextension = audioname.getAbsolutePath().substring(audioname.getAbsolutePath().lastIndexOf("."));
        String audFile = audioname.getName()+audextension;
        File aud = new File(getFilesDir(),"audio/"+audFile);
        word.setAudio(audFile);

        File fxname = new File(audioFileUri.getPath());
        String fxextension = fxname.getAbsolutePath().substring(fxname.getAbsolutePath().lastIndexOf("."));
        String fxFile = fxname.getName()+fxextension;
        File fx = new File(getFilesDir(),"effects/"+fxFile);
        word.setAudio(fxFile);

        word.setPronunciation("null");
        word.setPos("null");

        internet = isNetworkConnected();
        if(internet == false){
            word.setStatus(2);
        }else{
            word.setStatus(3);
        }

        bistalk.getWordbankList().add(word);

        try {
            GsontoJson(bistalk);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

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













