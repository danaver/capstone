package com.example.gailsemilladucao.capstone.backend;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gailsemilladucao.capstone.MainActivity;
import com.example.gailsemilladucao.capstone.R;
import com.example.gailsemilladucao.capstone.model.Bistalk;
import com.example.gailsemilladucao.capstone.model.wordbanks;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class EditData extends AppCompatActivity {

    EditText eEng,eCeb,ePru;
    Button eAud,eFx,save;
    TextView infoaud,infofx;
    ImageView eImg;
    Spinner pus;

    Uri audioFileUri,audioFxUri,imageFileUri;
    String srcPath,jsonstring;
    Bistalk bistalk;
    int position;
    wordbanks val;


    final int REQUEST_PERMISSION_CODE = 1000;
    final int REQUEST_PERMISSION_GALLERY = 999;
    final static int RQS_OPEN_AUDIO_MP3 = 1;
    final static int RQS_OPEN_AUDIO_FX = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);
        //binding
        pus = findViewById(R.id.pos);
        eEng = findViewById(R.id.editEng);
        eCeb = findViewById(R.id.editCeb);
        ePru = findViewById(R.id.editPrunoun);
        eAud = findViewById(R.id.editAud);
        eFx = findViewById(R.id.editFx);
        infoaud = findViewById(R.id.info_audio);
        infofx = findViewById(R.id.info_effect);
        eImg = findViewById(R.id.editImg);
        save = findViewById(R.id.save);


        //spinner
        Intent i = getIntent();
        val = (wordbanks)i.getSerializableExtra("res");


        ArrayAdapter<String> adapter = new ArrayAdapter<>(EditData.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.post));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pus.setAdapter(adapter);





        //for localEdit
        jsonstring = readFromFile();
        bistalk =JsontoGson();


        //placing in fields
        pus.setSelection(adapter.getPosition(val.getCategory()));
        eEng.setText(val.getEnglish());
        eCeb.setText(val.getCebuano());
        ePru.setText(val.getPronunciation());
        infoaud.setText(val.getAudio());
        eImg.setImageURI(Uri.parse(getFilesDir()+"/images/"+val.getPicture()));

        if(!val.getEffect().equals("null")){
            infofx.setText(val.getEffect());
            audioFxUri = Uri.parse(getFilesDir()+"/effects/"+val.getEffect());
        }

        // get uri for LocalEdit
        audioFileUri = Uri.parse(getFilesDir()+"/audio/"+val.getAudio());
        imageFileUri = Uri.parse(getFilesDir()+"/images/"+val.getPicture());

        position = wordExist(val.getEnglish());

        //attaching new audio or so
        eImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //external storage permission
                //runtime permision for android 6.0 and above
                ActivityCompat.requestPermissions(
                        EditData.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_GALLERY
                );
            }
        });

        eFx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Open Audio Effect (mp3) file"), RQS_OPEN_AUDIO_FX);
            }
        });

        eAud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lol = new Intent();
                lol.setType("audio/*");
                lol.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(
                        lol, "Open Audio (mp3) file"), RQS_OPEN_AUDIO_MP3);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    localEdit(bistalk,position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void menu(View view) {
        Intent lol = new Intent(EditData.this,MainActivity.class);
        startActivity(lol);
    }

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
                eImg.setImageURI(imageFileUri);
                Toast.makeText(EditData.this, imageFileUri.getPath(), Toast.LENGTH_SHORT).show();
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
                infoaud.setText(srcPath);

            }
        }

        // AUDIO FX
        if (resultCode == RESULT_OK) {
            if (requestCode == RQS_OPEN_AUDIO_FX) {
                audioFxUri = data.getData();
                //vvv This lets you set the path sa TextView
                srcPath = audioFxUri.getPath();
                infofx.setText(srcPath);

            }
        }
    }

    public void localEdit(Bistalk bistalk,int position) throws JSONException {

        String jeng,jceb,jfx,jpru="",jcateg;
        int jstat;

        boolean internet = false;
        wordbanks word = new wordbanks();

        //getiing the  text
        jeng = eEng.getText().toString();
        jceb = eCeb.getText().toString();
        jpru = ePru.getText().toString();
        jcateg = pus.getSelectedItem().toString();
        jfx = jeng+".mp3";


        if (jeng.equals("")&&jceb.equals("")){
            Toast.makeText(EditData.this, "Please fill all blanks", Toast.LENGTH_SHORT).show();
        }else{
            //image to json and copy to internal
            //image to local
            if(imageFileUri==null) {
                Toast.makeText(EditData.this, "Please Include an Image", Toast.LENGTH_SHORT).show();
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
                    File directory = new File(getFilesDir()+"/images");
                    File from      = new File(directory,val.getPicture());
                    File to        = new File(directory, jeng + ".png");
                    if(from.exists()) {
                        from.renameTo(to);
                    }
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
                        File directory = new File(getFilesDir()+"/effects");
                        File from      = new File(directory,val.getEffect());
                        File to        = new File(directory, jeng + ".mp3");
                        if(from.exists()) {
                            from.renameTo(to);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //audio to internal
                if(audioFileUri==null) {
                    Toast.makeText(EditData.this, "Please attach an audio file", Toast.LENGTH_SHORT).show();

                }else{
                    //works with new attached file
                    File aud = new File(getFilesDir() + "/audio", jceb + ".mp3");
                    ContentResolver contentResolvers = getContentResolver();

                    InputStream ins = null;
                    try {
                        ins = contentResolvers.openInputStream(audioFileUri);
                        Toast.makeText(EditData.this, ins.toString(), Toast.LENGTH_SHORT).show();
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
                        File directory = new File(getFilesDir()+"/audio");
                        File from      = new File(directory,val.getAudio());
                        File to        = new File(directory, jceb + ".mp3");
                        if(from.exists()) {
                            from.renameTo(to);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    //INSERT WORDS HERE NA

                    word.setEnglish(jeng.toLowerCase());
                    word.setCebuano(jceb.toLowerCase());
                    word.setPronunciation(jpru);


                    word.setAudio(jceb+".mp3");
                    word.setPicture(jeng+".png");
                    word.setCategory(jcateg);
                    word.setEffect(jfx);

                    if(audioFxUri != null){
                        word.setEffect(jfx);
                    }

//                    internet = isNetworkConnected();
//                    if(internet == false){
//                        word.setStatus(2);
//                    }else{
//                        word.setStatus(3);
//                    }
                    word.setStatus(1);

                    bistalk.getWordbankList().set(position,word);
                    GsontoJson(bistalk);
                    Intent lol = new Intent(EditData.this,MainActivity.class);
                    startActivity(lol);

                }
            }
        }
    }

    private Bistalk JsontoGson(){
        Bistalk bistalk = new com.google.gson.Gson().fromJson(jsonstring, Bistalk.class);
        return bistalk;
    }


    public void GsontoJson(Bistalk besh) throws JSONException {

        //Gson gson = new Gson(); raman ta na idk nganong mo error haahaha
        com.google.gson.Gson gson = new com.google.gson.Gson();

        Bistalk bistalk = new Bistalk(besh.getUpdate(),besh.getcStats(),besh.getUserList(),besh.getWordbankList());
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private int wordExist(String word){
        int match = 0;
        for (int i = 0; bistalk.getWordbankList().size()>i;i++){
            if(bistalk.getWordbankList().get(i).getEnglish().equals(word.toLowerCase())){
                return match = i ;
            }
        }
        return match;
    }







}
