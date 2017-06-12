package com.eventers.contactapp.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;

import com.eventers.contactapp.R;
import com.eventers.contactapp.data.ContactsDBHelper;
import com.eventers.contactapp.data.DBContract;

/**
 * Created by surya on 12/6/17.
 */

public class Utils {

    public static int getRandomColor(){
        int n = (int)(Math.random()*9);
        switch (n){
            case 0: return R.color.colorA;
            case 1: return R.color.colorB;
            case 2: return R.color.colorC;
            case 3: return R.color.colorD;
            case 4: return R.color.colorE;
            case 5: return R.color.colorF;
            case 6: return R.color.colorG;
            case 7: return R.color.colorH;
            case 8: return R.color.colorI;
            case 9: return R.color.colorJ;
            default:return R.color.colorAccent;
        }
    }


    /*Helper method to add the contact to the database*/
    public static void addToDatabase(Context context, String id, String finalName, String number) {

        ContactsDBHelper dbHelper = new ContactsDBHelper(context);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBContract.ContactEntry.KEY_CONTACT_ID,id);
        values.put(DBContract.ContactEntry.KEY_NAME,finalName);
        values.put(DBContract.ContactEntry.KEY_PHONE,number);
        long rowId =db.insert(DBContract.ContactEntry.TABLE_NAME,null,values);
        db.close();
        System.out.println("Inserted :" + rowId);
    }

    /*Helper method to rempve the contact from the database*/
    public static void removeFromDatabase(Context context, String id) {

        ContactsDBHelper dbHelper = new ContactsDBHelper(context);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long rowId = db.delete(DBContract.ContactEntry.TABLE_NAME,DBContract.ContactEntry.KEY_CONTACT_ID+ " = ?",
                new String[] { id });
        System.out.println("Inserted :" + rowId);
    }

}
