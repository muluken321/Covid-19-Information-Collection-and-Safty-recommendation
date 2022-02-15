package com.muler.covid19;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "covid19";
    public static final String TABLE_NAME = "user_info";
    public static final String COL_1 = "fullname";
    public static final String COL_2 = "nikname";
    public static final String COL_3 = "address";
    public static final String COL_4 = "travel_history";

    /***TRAVEL HISTORIES
     *
     * @param context
     */
    public static final String TABLE2_NAME = "t_history";
    public static final String t2COL_1 = "date";
    public static final String t2COL_2 = "location";
    public static final String t2COL_3 = "details";
    public static final String t2COL_4 = "remark";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
db.execSQL("create table "+TABLE_NAME+" (fullname TEXT, nikname TEXT, address TEXT, travel_history TEXT)");
db.execSQL("create table "+TABLE2_NAME+" ("+t2COL_1+" TEXT, "+t2COL_2+" TEXT, "+t2COL_3+" TEXT, "+t2COL_4+" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
db.execSQL("drop table if exists "+TABLE_NAME);
db.execSQL("drop table if exists "+TABLE2_NAME);
onCreate(db);
    }

    public boolean insertData(String fullname, String nikname, String address, String travel_history){
SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put(COL_1,fullname);
        contentvalues.put(COL_2,nikname);
        contentvalues.put(COL_3,address);
        contentvalues.put(COL_4,travel_history);
       long result = db.insert(TABLE_NAME,null,contentvalues);
if(result == -1){
    return false;
}
else{
    return true;
}
    }
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);
        return res;
    }
}
