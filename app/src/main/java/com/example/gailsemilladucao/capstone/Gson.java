package com.example.gailsemilladucao.capstone;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gailsemilladucao.capstone.backend.AddData;
import com.example.gailsemilladucao.capstone.model.Bistalk;
import com.example.gailsemilladucao.capstone.model.users;
import com.example.gailsemilladucao.capstone.model.wordbanks;
import com.google.android.gms.common.util.IOUtils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Gson extends AppCompatActivity {

    String jsonstring = null;
    Bistalk bistalk;

    ImageView imgs;
    Button add, attach_fx;
    Uri audioFileUri;
    Uri audioFxUri;
    Uri imageFileUri;
    Uri uri;

    Context context;
    final int REQUEST_PERMISSION_CODE = 1000;
    final int REQUEST_PERMISSION_GALLERY = 999;
    final static int RQS_OPEN_AUDIO_FX = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gson);

//        //get the jsonstring
//        jsonstring = readFromFile();
//        //this will return the array version of the json
//        //now pwede naka mo do og stuff such as editing and deleting and appending through bistalk
//        bistalk=JsontoGson();
//        //add a function here that will edit it
//        bistalk = changeStatus(bistalk);
//        //once mana kag edit it shiz pwede na nimo ipa jsonfile balik
//        //if you notice naa syay try catch. its because of the writeFile within that function
//        try {
//            GsontoJson(bistalk);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        imgs = findViewById(R.id.imgs);
        add = findViewById(R.id.insert);
        attach_fx = findViewById(R.id.attach_effect);

        imgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //external storage permission
                //runtime permision for android 6.0 and above
                ActivityCompat.requestPermissions(
                        Gson.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSION_GALLERY
                );
            }
        });

        attach_fx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Open Audio Effect (mp3) file"), RQS_OPEN_AUDIO_FX);
                //fxpath = info_effect.getText().toString();
            }
        });

        //place the code here KAREN
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this one na commented out is image insert which is okay na

                //external storage permission
                //runtime permision for android 6.0 and above
//                ActivityCompat.requestPermissions(
//                        Gson.this,
//                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                        REQUEST_PERMISSION_GALLERY
//                );

//                Bitmap bitmap = null;
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageFileUri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                ContextWrapper wrapper = null;
//                try{
//
//                    //image to local
//                    ParcelFileDescriptor parcelFileDescriptorImage =
//                            getContentResolver().openFileDescriptor(imageFileUri, "r");
//                    FileDescriptor imagefileDescriptor = parcelFileDescriptorImage.getFileDescriptor();
//                    Bitmap bitmap = BitmapFactory.decodeFileDescriptor(imagefileDescriptor);
//
//                    wrapper = new ContextWrapper(getApplicationContext());
//
//                    //change the name to proper name
//                    File imgFile = new File(wrapper.getFilesDir() + "/images","AAA"+".png");
//
//                    OutputStream istream = null;
//                    istream = new FileOutputStream(imgFile);
//                    bitmap.compress(Bitmap.CompressFormat.PNG,100,istream);
//                    istream.flush();
//                    istream.close();
//
//                    Toast.makeText(Gson.this, imageFileUri.toString(), Toast.LENGTH_SHORT).show();
//
//                    //effects to local
//
//
//                }catch(IOException e) {
//                    e.printStackTrace();
//                }

                //audiooooooo



                ////////////////////////////////////////////////////////////////////////
                File targetLocation= new File (getFilesDir()+"/audio","AA2.mp3");

                ContentResolver contentResolver = getContentResolver();
                InputStream in =null ;
                try {
                    in = contentResolver.openInputStream(audioFxUri);
                    OutputStream out = new FileOutputStream(targetLocation);
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
        });
    }


    public String getRealPathFromURI(Uri contentUri)
    {
        String res;
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        res = cursor.getString(idx);
        cursor.close();
        return res;
    }

    public byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;
        byte[] buffer = new byte[1024];
        while (read != -1) {
            read = in.read(buffer);
            if (read != -1)
                out.write(buffer,0,read);
        }
        out.close();
        return out.toByteArray();
    }

    public byte[] convert(String path) throws IOException {

        FileInputStream fis = new FileInputStream(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];

        for (int readNum; (readNum = fis.read(b)) != -1;) {
            bos.write(b, 0, readNum);
        }

        byte[] bytes = bos.toByteArray();

        return bytes;
    }



    public static boolean copyFiles(String from, String to) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(from);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(from);
                FileOutputStream fs = new FileOutputStream(to);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
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
                imgs.setImageURI(imageFileUri);
            }else if( resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
            }
        }

        // AUDIO FX
        if (resultCode == RESULT_OK) {
            if (requestCode == RQS_OPEN_AUDIO_FX) {
                audioFxUri = data.getData();
            }
        }
    }

    public Uri copyFile(Uri uri){

        if (uri == null) {
            return null;
        }

        String extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());

        String fileName = "some_name" + "." + extension;

        File tempFile = new File(getApplicationContext().getFilesDir().getAbsolutePath(), fileName);

        try {

            boolean fileCreated = tempFile.createNewFile();

            if(!fileCreated){
                //Log.e(LOG_TAG,"error creating file");
            }

            InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(uri);

            if(inputStream != null){
                // IOUtils.copy(inputStream, new FileOutputStream(tempFile));
            }
        } catch (IOException | NullPointerException ex) {
            //  Log.d(LOG_TAG, "Exception caught: " + ex.getMessage());
        }

        return Uri.fromFile(tempFile);
    }














    private Bistalk changeStatus(Bistalk bistalk) {

        //access the data
        bistalk.getWordbankList().get(1).setCebuano("999");

        return bistalk;
    }


    //once you done editing and adding stuff to the array
    //this will convert it back to json

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

        Bistalk bistalk = new Bistalk(besh.getUpdate(), besh.getUserList(),besh.getWordbankList());
        String json = gson.toJson(bistalk);

        // this will overwrite the jsonfile
        try {
            writeFile(json);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //returns an array version
    //this is from json
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

    //this will overwrite the jsonfile that exist hence it will update
    void writeFile(String data) throws IOException {
        File outFile = new File(getFilesDir(), "wordbank.json");
        FileOutputStream out = new FileOutputStream(outFile, false);
        byte[] contents = data.getBytes();
        out.write(contents);
        out.flush();
        out.close();
    }

    //returns the string version of the wordbank.json
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

}
