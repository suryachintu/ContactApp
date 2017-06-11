package com.eventers.contactapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

/**
 * Created by surya on 11/6/17.
 */

public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,ContactsAdapter.ItemClickListener{


    private static final String TAG = ContactsFragment.class.getSimpleName();
    private static final int ID = 1;

    RecyclerView mContactsList;

    // An adapter that binds the result Cursor to the recyclerview
    private ContactsAdapter mContactsAdapter;

    private String[] projection    = new String[] { ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY, ContactsContract.Contacts.LOOKUP_KEY, ContactsContract.Contacts.PHOTO_URI};
    private PhoneNumberUtil phoneUtil;


    public ContactsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_contacts,container,false);

        mContactsList = (RecyclerView)view.findViewById(R.id.contacts_list);

        mContactsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        mContactsAdapter = new ContactsAdapter(getActivity(),null,this);
        mContactsList.setAdapter(mContactsAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(ID, null, this);
        phoneUtil = PhoneNumberUtil.createInstance(getActivity());

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.e(TAG,"Oncreate loader");
        return new CursorLoader(getActivity(), ContactsContract.Contacts.CONTENT_URI, projection, null,null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        DatabaseUtils.dumpCursor(data);
        mContactsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e(TAG,"Loader reset");
    }

    @Override
    public void onItemClick(final String id, final ContactsAdapter.ViewHolder vh) {

        Cursor cursor = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                new String[]{id}, null);

        DatabaseUtils.dumpCursor(cursor);
        ArrayList<String> phoneNumbers = new ArrayList<>();
        if (cursor == null)
            return;

        String name = null;
        String imageUri = null;
        while (cursor.moveToNext()){

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
            imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID));

            System.out.println(name);

            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            try {
                Phonenumber.PhoneNumber x = phoneUtil.parse(number,"IN");
                if (phoneUtil.isValidNumber(x)) {
                    if (checkForDuplicates(phoneNumbers,number))
                        phoneNumbers.add(number);
                }

            } catch (NumberParseException e) {
                e.printStackTrace();
            }
        }
        cursor.close();


        if (phoneNumbers.size() == 1){
            vh.mCheckBox.setChecked(true);
            return;
        }else if (phoneNumbers.size() == 0)
            Toast.makeText(getActivity(), getString(R.string.no_number_available), Toast.LENGTH_SHORT).show();

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(true);

        if (dialog.getWindow() != null)
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        final RadioGroup radioGroup = (RadioGroup)dialog.findViewById(R.id.number_radio_group);
        TextView contactLetter = (TextView)dialog.findViewById(R.id.contact_letter);
        TextView contactName = (TextView)dialog.findViewById(R.id.contact_name);
        CircleImageView contactImage = (CircleImageView) dialog.findViewById(R.id.contact_image);
        Button cancelBtn = (Button)dialog.findViewById(R.id.cancel_btn);
        Button okBtn = (Button)dialog.findViewById(R.id.ok_btn);
        contactName.setText(name != null ? name : getString(R.string.unknown));

        System.out.println(imageUri + "*" + name);
        if (imageUri!= null) {
            contactImage.setImageURI(Uri.parse(imageUri));
            contactImage.setVisibility(View.VISIBLE);
            contactLetter.setVisibility(View.GONE);
        } else {
            if (name == null)
                return;
            char letter = name.toUpperCase().charAt(0);

            System.out.println(letter);
            contactLetter.setText(String.valueOf(letter));

            GradientDrawable magnitudeCircle = (GradientDrawable) contactLetter.getBackground();

            magnitudeCircle.setColor(ResourcesCompat.getColor(getResources(), Utils.getRandomColor(), null));
            contactImage.setVisibility(View.GONE);
            contactLetter.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < phoneNumbers.size(); i++) {
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setText(phoneNumbers.get(i));
            radioButton.setId(i);
            radioGroup.addView(radioButton,new RadioGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            if (i==0)
                radioButton.setChecked(true);
        }

        final String finalName = name;
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                    if (radioButton.isChecked()){
                        vh.mCheckBox.setChecked(true);

                        Utils.addToDatabase(getActivity(),id, finalName,radioButton.getText().toString());

                        break;
                    }
                }
                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                vh.mCheckBox.setChecked(false);
            }
        });

        dialog.show();

    }

    private boolean checkForDuplicates(ArrayList<String> phoneNumbers, String number) {

        if (phoneNumbers.size() == 0)
            return true;
        else {
            boolean flag = true;
            for (int i = 0; i < phoneNumbers.size(); i++) {

                PhoneNumberUtil.MatchType matchType = phoneUtil.isNumberMatch(phoneNumbers.get(i), number);

                if (matchType.equals(PhoneNumberUtil.MatchType.NSN_MATCH) || matchType.equals(PhoneNumberUtil.MatchType.EXACT_MATCH)) {
                    flag = false;
                    break;
                }

            }

            return flag;
        }
    }

}
