package com.eventers.contactapp.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eventers.contactapp.R;
import com.eventers.contactapp.data.ContactsDBHelper;
import com.eventers.contactapp.data.DBContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class SelectedConatcsFragment extends Fragment {

    public SelectedConatcsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selected_conatcs, container, false);

        TextView totalContacts = (TextView)view.findViewById(R.id.total_contacts);

        TextView selectedContacts = (TextView)view.findViewById(R.id.selected_contacts);

        Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);//corresponds to contacts db

        if (cursor!= null) {
            totalContacts.setText(String.valueOf(cursor.getCount()));
            cursor.close();
        }else {
            totalContacts.setText("0");
        }

        ContactsDBHelper dbHelper = new ContactsDBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor localCursor = db.query(DBContract.ContactEntry.TABLE_NAME,null,null,null,null,null,null);//corresponds to local db

        if (localCursor!= null) {
            selectedContacts.setText(String.valueOf(localCursor.getCount()));
            localCursor.close();
        }else {
            totalContacts.setText("0");
        }

        return view;

    }
}
