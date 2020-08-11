package com.example.profiler.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.profiler.models.Record;

import java.util.ArrayList;

public class MyProfileRecordDAO extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "my_profile_records.db";
    public static final String RECORD_TABLE_NAME = "record";
    public static final String RECORD_ID = "id";
    public static final String RECORD_TITLE = "title";
    public static final String RECORD_DESCRIPTION = "description";
    public static final String RECORD_IMAGE = "image";
    public static final String RECORD_TIME = "time";
    public static final String RECORD_PROFILE_ID = "profile_id";

    public MyProfileRecordDAO(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table "+ RECORD_TABLE_NAME +
                        " ("+ RECORD_ID +" integer primary key, " +
                        RECORD_TITLE +" text, "+
                        RECORD_DESCRIPTION +" text, "+
                        RECORD_IMAGE +" text, "+
                        RECORD_TIME +" text, "+
                        RECORD_PROFILE_ID +" int)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+ RECORD_TABLE_NAME);
        onCreate(db);
    }

    public boolean updateRecord(int id, Record record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RECORD_TITLE, record.getTitle());
        contentValues.put(RECORD_DESCRIPTION, record.getDescription());
        contentValues.put(RECORD_IMAGE, record.getImage());
        contentValues.put(RECORD_TIME, record.getTime());
        db.update(RECORD_TABLE_NAME, contentValues, RECORD_ID +" = ? ",
                new String[] { Integer.toString(id) } );
        return true;
    }

    public boolean insertRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RECORD_TITLE, record.getTitle());
        contentValues.put(RECORD_DESCRIPTION, record.getDescription());
        contentValues.put(RECORD_IMAGE, record.getImage());
        contentValues.put(RECORD_TIME, record.getTime());
        contentValues.put(RECORD_PROFILE_ID, record.getProfileID());

        db.insert(RECORD_TABLE_NAME, null, contentValues);
        return true;
    }
    public Record getRecord(int recordID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+ RECORD_TABLE_NAME +
                " where " + RECORD_ID + "=" +recordID, null );
        cursor.moveToFirst();
        Record record = new Record(
                cursor.getInt(cursor.getColumnIndex(RECORD_ID)),
                cursor.getString(cursor.getColumnIndex(RECORD_TITLE)),
                cursor.getString(cursor.getColumnIndex(RECORD_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(RECORD_IMAGE)),
                cursor.getString(cursor.getColumnIndex(RECORD_TIME)),
                cursor.getInt(cursor.getColumnIndex(RECORD_PROFILE_ID))
                );
        cursor.close();
        return record;
    }

    public void deleteRecord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(RECORD_TABLE_NAME,
                RECORD_ID +" = ? ",
                new String[] { Integer.toString(id) });
        ContentValues contentValues = new ContentValues();
        for(int i=id; i<=numberOfRows(); i++){
            contentValues.put(RECORD_ID, i);
            db.update(RECORD_TABLE_NAME, contentValues, RECORD_ID +" = ? ",
                    new String[] { Integer.toString(i+1) } );
        }
    }

    public ArrayList<Record> getProfileRecords(int profileID) {
        ArrayList<Record> recordList = new ArrayList<>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+ RECORD_TABLE_NAME +
                        " where "+ RECORD_PROFILE_ID +" = "+profileID,
                null );
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            Record record = new Record();
            record.setId(cursor.getInt(cursor.getColumnIndex(RECORD_ID)));
            record.setTitle(cursor.getString(cursor.getColumnIndex(RECORD_TITLE)));
            record.setDescription(cursor.getString(cursor.getColumnIndex(RECORD_DESCRIPTION)));
            record.setImage(cursor.getString(cursor.getColumnIndex(RECORD_IMAGE)));
            record.setTime(cursor.getString(cursor.getColumnIndex(RECORD_TIME)));
            record.setProfileID(profileID);
            recordList.add(record);
            cursor.moveToNext();
        }
        return recordList;
    }
    public ArrayList<Record> getProfileRecordsReversed(int profileID) {
        ArrayList<Record> recordList = new ArrayList<>();


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+ RECORD_TABLE_NAME +
                        " where "+ RECORD_PROFILE_ID +" = "+profileID,
                null );
        cursor.moveToLast();

        while(!cursor.isBeforeFirst()){
            Record record = new Record();
            record.setId(cursor.getInt(cursor.getColumnIndex(RECORD_ID)));
            record.setTitle(cursor.getString(cursor.getColumnIndex(RECORD_TITLE)));
            record.setDescription(cursor.getString(cursor.getColumnIndex(RECORD_DESCRIPTION)));
            record.setImage(cursor.getString(cursor.getColumnIndex(RECORD_IMAGE)));
            record.setTime(cursor.getString(cursor.getColumnIndex(RECORD_TIME)));
            record.setProfileID(profileID);
            recordList.add(record);
            cursor.moveToPrevious();
        }
        return recordList;
    }
    public void deleteProfileRecords(int profileID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(RECORD_TABLE_NAME,
                RECORD_PROFILE_ID +" = ? ",
                new String[] { Integer.toString(profileID) });

        ContentValues contentValues = new ContentValues();
        for(int i=profileID; i<=numberOfRows(); i++) {
            contentValues.put(RECORD_PROFILE_ID, i);
            db.update(RECORD_TABLE_NAME, contentValues, RECORD_PROFILE_ID + " = ? ",
                    new String[]{Integer.toString(i + 1)});
        }
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, RECORD_TABLE_NAME);
        return numRows;
    }

    public ArrayList<Record> getAllRecords() {
        ArrayList<Record> recordList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+RECORD_TABLE_NAME,
                null );
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Record record = new Record(
                    cursor.getInt(cursor.getColumnIndex(RECORD_ID)),
                    cursor.getString(cursor.getColumnIndex(RECORD_TITLE)),
                    cursor.getString(cursor.getColumnIndex(RECORD_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(RECORD_IMAGE)),
                    cursor.getString(cursor.getColumnIndex(RECORD_TIME)),
                    cursor.getInt(cursor.getColumnIndex(RECORD_PROFILE_ID))
            );
            recordList.add(record);
            cursor.moveToNext();
        }
        return recordList;
    }
    public ArrayList<Record> getAllRecordsReversed() {
        ArrayList<Record> recordList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+RECORD_TABLE_NAME,
                null );
        cursor.moveToLast();
        while(!cursor.isBeforeFirst()){
            Record record = new Record(
                    cursor.getInt(cursor.getColumnIndex(RECORD_ID)),
                    cursor.getString(cursor.getColumnIndex(RECORD_TITLE)),
                    cursor.getString(cursor.getColumnIndex(RECORD_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(RECORD_IMAGE)),
                    cursor.getString(cursor.getColumnIndex(RECORD_TIME)),
                    cursor.getInt(cursor.getColumnIndex(RECORD_PROFILE_ID))
            );
            recordList.add(record);
            cursor.moveToPrevious();
        }
        return recordList;
    }
}
