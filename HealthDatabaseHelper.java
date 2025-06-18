package com.example.finaleapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Timestamp;

 class HealthDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "health.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "health_data";
    public static final String COL_ID = "id";
    public static final String COL_BPM = "bpm";
    public static final String COL_TEMPERATURE = "temperature";
    public static final String COL_SPO2 = "spo2";
    public static final String COL_RR = "rr";
    public static final String COL_HRV = "hrv";
    public static final String COL_TIME = "time";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_BPM + " INTEGER, " +
            COL_TEMPERATURE + " REAL, " +
            COL_SPO2 + " INTEGER, " +
            COL_RR + " INTEGER, " +
            COL_HRV + " INTEGER, " +
            COL_TIME + " TEXT)";

    public HealthDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(int bpm, float temperature, int spo2, int rr, int hrv, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BPM, bpm);
        values.put(COL_TEMPERATURE, temperature);
        values.put(COL_SPO2, spo2);
        values.put(COL_RR, rr);
        values.put(COL_HRV, hrv);
        values.put(COL_TIME, time);
        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_ID + " DESC", null);
    }
     public void clearDatabase() {
         SQLiteDatabase db = this.getWritableDatabase();
         db.delete(TABLE_NAME, null, null); // Deletes all rows
         db.close();
     }
}
