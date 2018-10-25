package com.example.gailsemilladucao.capstone.view;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gailsemilladucao.capstone.MainActivity;
import com.example.gailsemilladucao.capstone.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class ShowData extends AppCompatActivity {

    public TextView cebtxt,pru, back_btn;
    public Button query_button;
    public ImageButton imgfx;
    public Button audio;

    String imgName=null,audname=null,cebword=null,fxname = null,pruword=null;

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
        pru = findViewById(R.id.pru);
        back_btn = findViewById(R.id.back);



        try {
            instance = new JSONObject(getIntent().getStringExtra("Val"));
            cebword = instance.getString("Cebuano");
            pruword = instance.getString("Pronunciation");
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

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowData.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //Uri uri = Uri.parse(getFilesDir()+"/images/"+imgName);

        cebtxt.setText(cebword);
        pru.setText("["+pruword+"]");
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

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MediaPlayer player = new MediaPlayer();
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.setDataSource(getFilesDir()+"/audio/"+audname);
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
