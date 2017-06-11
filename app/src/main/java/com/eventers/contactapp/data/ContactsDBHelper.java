package com.eventers.contactapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by surya on 12/6/17.
 */

public class ContactsDBHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "mysavedcontacts";
    private static final int DATABASE_VERSION = 1;

    public ContactsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + DBContract.ContactEntry.TABLE_NAME + "(" +
                                DBContract.ContactEntry._ID + " INTEGER PRIMARY KEY," + DBContract.ContactEntry.KEY_CONTACT_ID + " TEXT NOT NULL,"
                                + DBContract.ContactEntry.KEY_NAME + " TEXT NOT NULL," + DBContract.ContactEntry.KEY_PHONE + " TEXT NOT NULL" + ")";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBContract.ContactEntry.TABLE_NAME);
        onCreate(db);
    }
}
