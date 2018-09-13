//package com.example.gailsemilladucao.capstone.database;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//
//import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
//
//public class DatabaseOpenHelper extends SQLiteAssetHelper {
//
//    private static final String DATABASE_NAME = "BisTalk.db";
//    private static final int DATABASE_VERSION = 1;
//
//    public DatabaseOpenHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//}

package com.example.gailsemilladucao.capstone.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BisTalk_NEw.db";
    private static final String TABLE_NAME = "Words";

    private static final String COL_1 = "English_text";
    private static final String COL_2 = "Cebuano_text";
    private static final String COL_3 = "Word_id";
    private static final String COL_4 = "Image_file";
    private static final String COL_5 = "Audio_text";
    private static final String COL_6 = "Effect_file";
    private static final String COL_7 = "Pronunciation";
    private static final String COL_8 = "Category";
    private static final String COL_9 = "Part_of_Speech";
    private static final int DATABASE_VERSION = 1;

    SQLiteDatabase db = this.getWritableDatabase();

    public DatabaseOpenHelper(Context context){super(context,DATABASE_NAME,null,1);}

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String query = "CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_2 + " TEXT, " +
//                COL_3 + " TEXT)";
        String query = "CREATE TABLE "+ TABLE_NAME +" ( "+ COL_1 +" TEXT, " +COL_2+ " TEXT, "+COL_3+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+COL_4+" TEXT, "+COL_5+" TEXT, "+COL_6+" TEXT, "+COL_7+" TEXT, "+COL_8+" TEXT, "+COL_9+ " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion){
            db.execSQL("DROP TABLE IF EXISTS " +  TABLE_NAME);
            onCreate(db);
        }
    }


        public String getAddress(String word){

        Cursor c = null;
        c = db.rawQuery("SELECT Cebuano_text FROM Words where English_text = '"+word+"'", new String[]{});
        StringBuffer buffer = new StringBuffer();
        while(c.moveToNext()){
            String ceb = c.getString(0);
            buffer.append("" + ceb);
        }
        return buffer.toString();
    }

        public void addWord(String english, String cebuano){


        boolean val;
       // String query = "Insert Into Words (English_text ,Cebuano_text ,Word_id) Values("+english+","+cebuano+",NULL);";
       // String query = "Insert Into Words (English_text ,Cebuano_text ,Word_id) Values('MEOW','IRINGG',NULL);";
       // c = db.rawQuery("Insert Into Words (English_text,Cebuano_text) Values('"+english+"','"+cebuano+"')",new String[]{});
        db.execSQL("Insert Into Words (English_text ,Cebuano_text ,Word_id) Values('MEOs','IRINGs',NULL)");

    }

}

