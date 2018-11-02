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

    // View buttons
    Button viewadj, viewanimal, viewbody, viewevent, viewfood, viewnumber, viewperson, viewplace, viewthing, viewverb;

    // Delete buttons
    Button deladj, delanimal, delbody, delevent, delfood, delnumber, delperson, delplace, delthing, delverb;

    // Download buttons
    Button adj, animal, body, event, food, number, person, place, thing, verb;

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

        adj = findViewById(R.id.adjective);
        animal = findViewById(R.id.animal);
        body = findViewById(R.id.body);
        event = findViewById(R.id.event);
        food = findViewById(R.id.food);
        number = findViewById(R.id.number);
        person = findViewById(R.id.person);
        place = findViewById(R.id.place);
        thing = findViewById(R.id.thing);
        verb = findViewById(R.id.verb);

        deladj = findViewById(R.id.deladjective);
        delanimal = findViewById(R.id.delanimal);
        delbody = findViewById(R.id.delbody);
        delevent = findViewById(R.id.delevent);
        delfood = findViewById(R.id.delfood);
        delnumber = findViewById(R.id.delnumber);
        delperson = findViewById(R.id.delperson);
        delplace = findViewById(R.id.delplace);
        delthing = findViewById(R.id.delthing);
        delverb = findViewById(R.id.delverb);

        viewadj = findViewById(R.id.viewadj);
        viewanimal = findViewById(R.id.viewanimal);
        viewbody = findViewById(R.id.viewbody);
        viewevent = findViewById(R.id.viewevent);
        viewfood = findViewById(R.id.viewfood);
        viewnumber = findViewById(R.id.viewnumber);
        viewperson = findViewById(R.id.viewperson);
        viewplace = findViewById(R.id.viewplace);
        viewthing = findViewById(R.id.viewthing);
        viewverb = findViewById(R.id.viewverb);

        createFolder();
       // gayson();

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
                }else if(id == R.id.home){
                    Intent intent = new Intent(DownloadCateg.this, MainActivity.class);
                    startActivity(intent);
                }
                return true;
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

        animal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    downloadCateg("animal");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        body.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    downloadCateg("body part");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    downloadCateg("event");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    downloadCateg("food");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    downloadCateg("number");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    downloadCateg("person");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    downloadCateg("place");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        thing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    downloadCateg("thing");
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

        deladj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteCateg("adjective");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        delanimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteCateg("animal");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        delbody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteCateg("body part");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        delevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteCateg("event");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        delfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteCateg("food");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        delnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteCateg("number");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        delperson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteCateg("person");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        delplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteCateg("place");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        delthing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteCateg("thing");
                }catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        delverb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deleteCateg("verb");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

        viewanimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lol = new Intent(DownloadCateg.this,viewCateg.class);
                lol.putExtra("Val","animal");
                startActivity(lol);
            }
        });

        viewbody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lol = new Intent(DownloadCateg.this,viewCateg.class);
                lol.putExtra("Val","body part");
                startActivity(lol);
            }
        });

        viewevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lol = new Intent(DownloadCateg.this,viewCateg.class);
                lol.putExtra("Val","event");
                startActivity(lol);
            }
        });

        viewfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lol = new Intent(DownloadCateg.this,viewCateg.class);
                lol.putExtra("Val","food");
                startActivity(lol);
            }
        });

        viewnumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lol = new Intent(DownloadCateg.this,viewCateg.class);
                lol.putExtra("Val","number");
                startActivity(lol);
            }
        });

        viewperson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lol = new Intent(DownloadCateg.this,viewCateg.class);
                lol.putExtra("Val","person");
                startActivity(lol);
            }
        });

        viewplace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lol = new Intent(DownloadCateg.this,viewCateg.class);
                lol.putExtra("Val","place");
                startActivity(lol);
            }
        });

        viewthing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lol = new Intent(DownloadCateg.this,viewCateg.class);
                lol.putExtra("Val","thing");
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


    private void downloadCateg(String categ) throws JSONException {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Downloading Files");

        if (isNetworkConnected() == true) {
            progressDialog.show();
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }

        for (int i = 0; i < list.getWordbankList().size(); i++){


            if(list.getWordbankList().get(i).getCategory().equals(categ)) {

                //images
                final StorageReference pictureReference = storage.getReferenceFromUrl("gs://bistalk-7833f.appspot.com").child("pictures/"+categ+"/" + list.getWordbankList().get(i).getPicture());
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
                final StorageReference audioFxReference = storage.getReferenceFromUrl("gs://bistalk-7833f.appspot.com").child("effects/"+categ+"/"+ list.getWordbankList().get(i).getEffect());
                final File audioFxFile = new File(getFilesDir(),"effects/"+ list.getWordbankList().get(i).getEffect());

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

                list.getWordbankList().get(i).setStatus(1);
            }
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