package com.davidpaul.getsetgoo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David Paul on 8/21/2016.
 */
public class DBhelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "reached";
    private static final String TABLE_LocationHistory = "LocationHistory";
    //    private static final String KEY_ID = "id";
//    private static final String KEY_NAME = "name";
    private static final String KEY_ADDRESS = "Address";
    private static final String KEY_LATITUDE = "Latitude";
    private static final String KEY_LONGITUDE = "Longitude";
    Context context;
    private static final String KEY_TIME_STAMP = "TimeStamp";

    public DBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_LocationHistory + "("
                + KEY_ADDRESS + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT,"
                + KEY_TIME_STAMP + " TEXT"
                + ");";
        Log.e("CONTACTS_TABLE-->", CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LocationHistory);

        Log.e("DROP_CONTACTS_TABLE-->", "Called");
        // Create tables again
        onCreate(db);
    }

    void clearDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LocationHistory);
        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    void addAddressToDB(String address, String latitude, String longitude, String timeStamp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS, address);
        values.put(KEY_LATITUDE, latitude);
        values.put(KEY_LONGITUDE, longitude);
        values.put(KEY_TIME_STAMP, timeStamp);

        // Inserting Row
        db.insert(TABLE_LocationHistory, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public Cursor executeQuery(String sql) throws SQLException {
        SQLiteDatabase myDataBase = this.getWritableDatabase();
        myDataBase.beginTransaction();
        Cursor c = null;
        try {
            c = myDataBase.rawQuery(sql, null);
            myDataBase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            myDataBase.endTransaction();
        }
        return c;
    }

    public List<String> getAllSavedLocation() {
        List<String> historyList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_LocationHistory;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String address = cursor.getString(0);
                // Adding contact to list
                historyList.add(address);
            } while (cursor.moveToNext());
        } else {
            historyList.add("No history of locations");
        }

        // return history list
        return historyList;
    }

    public ArrayList<LatLng> getLATLANGlist(String startTime, String stopTime) {
        ArrayList<LatLng> latlongList = new ArrayList<LatLng>();
        // Select All Query
        String selectQuery = "SELECT " + KEY_LATITUDE + "," + KEY_LONGITUDE + " FROM " + TABLE_LocationHistory
                + " WHERE " + KEY_TIME_STAMP + " BETWEEN '" + startTime + "' AND '" + stopTime + "'";
        Log.e("get lat lang based on Timestamp-->", selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                double latitude = Double.parseDouble(cursor.getString(0));
                double longitude = Double.parseDouble(cursor.getString(1));
                latlongList.add(new LatLng(latitude, longitude));
                Log.e("lat_lang_Timestamp-->", "latitude--" + latitude + " longitude--" + longitude);
            } while (cursor.moveToNext());
        } else {
            latlongList.add(new LatLng(0, 0));
        }
        // return history list
        return latlongList;
    }
}