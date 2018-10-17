package com.example.gailsemilladucao.capstone.view;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gailsemilladucao.capstone.MainActivity;
import com.example.gailsemilladucao.capstone.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class ShowData extends AppCompatActivity {

    public EditText cebtxt;
    public Button query_button;
    public ImageButton imgfx;
    public Button audio;
    public TextView result_cebuano;
    public MainActivity mainActivity;

    String imgName=null,audname=null,cebword=null,fxname = null;

    JSONObject instance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_data);

        cebtxt = findViewById(R.id.cebTxt);
       // query_button = findViewById(R.id.query_button);
       // result_cebuano = findViewById(R.id.result_text);
        imgfx = findViewById(R.id.imgfx);
        audio =findViewById(R.id.audio);



        try {
            instance = new JSONObject(getIntent().getStringExtra("Val"));
            cebword = instance.getString("Cebuano");
            imgName = instance.getString("Picture");
            audname = instance.getString("Audio");
            try {
                fxname = instance.getString("Effect");
            }catch (Exception e){
                fxname = null;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Uri uri = Uri.parse(getFilesDir()+"/images/"+imgName);

        cebtxt.setText(cebword);
        imgfx.setImageURI(Uri.parse(getFilesDir()+"/images/"+imgName));

        imgfx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                MediaPlayer player = new MediaPlayer();
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.setDataSource(getFilesDir()+"/effects/"+fxname);
                    player.prepare();
                    player.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


//        query_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });
    }
}
