package com.example.profiler.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.profiler.models.Profile;

import java.util.ArrayList;

public class ProfileDAO extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "6.db";
    public static final String PROFILE_TABLE_NAME = "profiles";
    public static final String PROFILE_ID = "id";
    public static final String PROFILE_PHOTO = "photo";
    public static final String PROFILE_NAME = "name";
    public static final String PROFILE_BIO = "bio";
    public static final String PROFILE_PHONE = "phone";
    public static final String PROFILE_BIRTHDAY = "birthday";
    public static final String PROFILE_EMAIL = "email";
    public static final String PROFILE_ADDRESS = "address";
    public static final String PROFILE_INTERESTS = "interests";
    public static final String PROFILE_RELATIONSHIP_STATUS = "relationship_status";
    public static final String PROFILE_OCCUPATION = "occupation";

    public ProfileDAO(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table "+PROFILE_TABLE_NAME +
                        " ("+ PROFILE_ID +" integer primary key, " +
                        PROFILE_NAME+" text, "+
                        PROFILE_PHOTO+" text, "+
                        PROFILE_BIO+" text, "+
                        PROFILE_PHONE+" text, "+
                        PROFILE_BIRTHDAY+" text, "+
                        PROFILE_EMAIL+" text, "+
                        PROFILE_ADDRESS+" text, "+
                        PROFILE_INTERESTS+" text, "+
                        PROFILE_RELATIONSHIP_STATUS+" text, "+
                        PROFILE_OCCUPATION+" text)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+PROFILE_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertProfile (Profile profile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILE_NAME, profile.getName());
        contentValues.put(PROFILE_PHOTO, profile.getPhoto());
        contentValues.put(PROFILE_BIO, profile.getBio());
        contentValues.put(PROFILE_PHONE, profile.getPhone());
        contentValues.put(PROFILE_BIRTHDAY, profile.getBirthday());
        contentValues.put(PROFILE_EMAIL, profile.getEmail());
        contentValues.put(PROFILE_ADDRESS, profile.getAddress());
        contentValues.put(PROFILE_INTERESTS, profile.getInterests());
        contentValues.put(PROFILE_RELATIONSHIP_STATUS, profile.getRelationshipStatus());
        contentValues.put(PROFILE_OCCUPATION, profile.getOccupation());
        db.insert(PROFILE_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean updateProfile (int id, Profile profile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILE_NAME, profile.getName());
        contentValues.put(PROFILE_PHOTO, profile.getPhoto());
        contentValues.put(PROFILE_BIO, profile.getBio());
        contentValues.put(PROFILE_PHONE, profile.getPhone());
        contentValues.put(PROFILE_BIRTHDAY, profile.getBirthday());
        contentValues.put(PROFILE_EMAIL, profile.getEmail());
        contentValues.put(PROFILE_ADDRESS, profile.getAddress());
        contentValues.put(PROFILE_INTERESTS, profile.getInterests());
        contentValues.put(PROFILE_RELATIONSHIP_STATUS, profile.getRelationshipStatus());
        contentValues.put(PROFILE_OCCUPATION, profile.getOccupation());
        db.update(PROFILE_TABLE_NAME, contentValues, PROFILE_ID+" = ? ",
                new String[] { Integer.toString(id) } );
        return true;
    }

    public void deleteProfile(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(PROFILE_TABLE_NAME,
                PROFILE_ID+" = ? ",
                new String[] { Integer.toString(id) });
        ContentValues contentValues = new ContentValues();
        for(int i=id; i<=numberOfRows(); i++){
            contentValues.put(PROFILE_ID, i);
            db.update(PROFILE_TABLE_NAME, contentValues, PROFILE_ID+" = ? ",
                    new String[] { Integer.toString(i+1) } );
        }

    }

    public Profile getProfile(int profileID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+PROFILE_TABLE_NAME+
                " where " +PROFILE_ID+ "=" +profileID, null );
        cursor.moveToFirst();
        Profile profile = new Profile(
                cursor.getString(cursor.getColumnIndex(PROFILE_PHOTO)),
                cursor.getString(cursor.getColumnIndex(PROFILE_NAME)),
                cursor.getString(cursor.getColumnIndex(PROFILE_BIO)),
                cursor.getString(cursor.getColumnIndex(PROFILE_PHONE)),
                cursor.getString(cursor.getColumnIndex(PROFILE_BIRTHDAY)),
                cursor.getString(cursor.getColumnIndex(PROFILE_EMAIL)),
                cursor.getString(cursor.getColumnIndex(PROFILE_ADDRESS)),
                cursor.getString(cursor.getColumnIndex(PROFILE_INTERESTS)),
                cursor.getString(cursor.getColumnIndex(PROFILE_RELATIONSHIP_STATUS)),
                cursor.getString(cursor.getColumnIndex(PROFILE_OCCUPATION)));
        cursor.close();
        return profile;
    }

    public ArrayList<Profile> getAllProfiles() {
        ArrayList<Profile> profileList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+PROFILE_TABLE_NAME,
                null );
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Profile profile = new Profile(
                    cursor.getInt(cursor.getColumnIndex(PROFILE_ID)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_PHOTO)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_NAME)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_BIO)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_PHONE)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_BIRTHDAY)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_ADDRESS)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_INTERESTS)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_RELATIONSHIP_STATUS)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_OCCUPATION)));
            profileList.add(profile);
            cursor.moveToNext();
        }
        return profileList;
    }

    public ArrayList<Profile> getAllProfilesReversed() {
        ArrayList<Profile> profileList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select * from "+PROFILE_TABLE_NAME,
                null );
        cursor.moveToLast();
        while(!cursor.isBeforeFirst()){
            Profile profile = new Profile(
                    cursor.getInt(cursor.getColumnIndex(PROFILE_ID)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_PHOTO)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_NAME)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_BIO)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_PHONE)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_BIRTHDAY)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_ADDRESS)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_INTERESTS)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_RELATIONSHIP_STATUS)),
                    cursor.getString(cursor.getColumnIndex(PROFILE_OCCUPATION)));
            profileList.add(profile);
            cursor.moveToPrevious();
        }
        return profileList;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, PROFILE_TABLE_NAME);
        return numRows;
    }
}
