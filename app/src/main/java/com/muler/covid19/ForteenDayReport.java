package com.muler.covid19;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;


public  class ForteenDayReport {
    public static String HistoryEvent = "";

    public static void RegisterEvents(String location, String Details, String remark) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String newDateStr = formatter.format(date);


        SQLiteDatabase db = GSignIn.dbhelper.getWritableDatabase();
        ContentValues contentvalues = new ContentValues();
        contentvalues.put("date",newDateStr);
        contentvalues.put("location",location);
        contentvalues.put("details",Details);
        contentvalues.put("remark",remark);
        long result = db.insert("t_history",null,contentvalues);
        if(result == -1){
        }
        else {
        }
        }

       //not used for now
    public static void RetriveEvents() {

        try {
//Cursor res = dbhelper.getAllData();
            SQLiteDatabase db = GSignIn.dbhelper.getWritableDatabase();
            Cursor c = db.rawQuery("SELECT * FROM t_history", null);
            if (c.moveToFirst()){
                do {
                    // Passing values
                    String column1 = c.getString(0);
                    String column2 = c.getString(1);
                    String column3 = c.getString(2);
                    String column4 = c.getString(3);
                    // Do something Here with values
                    HistoryEvent = HistoryEvent+"\n"+column1+"\n"+column2+"-"+column3+"--"+column4;
                } while(c.moveToNext());
            }
        }
        catch (Exception ex){

        }
    }

}
