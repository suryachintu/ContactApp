package com.eventers.contactapp.data;

import android.provider.BaseColumns;

/**
 * Created by surya on 12/6/17.
 */

public class DBContract {

    public static final class ContactEntry implements BaseColumns{
        public static final String TABLE_NAME = "savedContacts";
        public static final String KEY_CONTACT_ID = "contact_id";
        public static final String KEY_NAME = "name";
        public static final String KEY_PHONE = "phoneNumber";
    }

}
