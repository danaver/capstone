package com.example.gailsemilladucao.capstone.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Blob;
import java.util.ArrayList;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    Cursor c = null;

    ArrayList<String> arrayList = new ArrayList<>();


    public DatabaseAccess (Context context){
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context){
        if(instance == null){
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    // OPEN DATABASE

    public void open(){
        this.db = openHelper.getWritableDatabase();
    }

    // CLOSE DATABASE

    public void close(){
        if(db != null){
            this.db.close();
        }
    }

    // CREATE METHOD TO QUERY AND RETURN THE RESULT FROM DATABASE

    public void populate() {
        
    }

    public String getAddress(String word){
        c = db.rawQuery("SELECT Cebuano_text FROM Words where English_text = '"+word+"'", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while(c.moveToNext()){
            String ceb = c.getString(0);
            buffer.append("" + ceb);
        }
        return buffer.toString();
    }

    public String getEnglish(String english){
        c = db.rawQuery("SELECT English_text FROM Words where English_text = '"+english+"'", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while(c.moveToNext()){
            String eng = c.getString(0);
            buffer.append("" + eng);
        }
        return buffer.toString();
    }

    public void addWord(String english, String cebuano){
        ContentValues content = new ContentValues();

        

    }
}
