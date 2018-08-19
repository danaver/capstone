package com.example.gailsemilladucao.capstone;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    Cursor c = null;


    private DatabaseAccess (Context context){
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

    public String getAddress(String english){
        c = db.rawQuery("SELECT Cebuano_text FROM Words where English_text = '"+english+"'", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while(c.moveToNext()){
            String ceb = c.getString(0);
            buffer.append("" + ceb);
        }
        return buffer.toString();
    }
}
