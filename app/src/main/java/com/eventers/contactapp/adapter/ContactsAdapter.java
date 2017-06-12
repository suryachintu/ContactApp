package com.eventers.contactapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.eventers.contactapp.R;
import com.eventers.contactapp.data.ContactsDBHelper;
import com.eventers.contactapp.data.DBContract;
import com.eventers.contactapp.utilities.CursorRecyclerViewAdapter;
import com.eventers.contactapp.utilities.Utils;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by surya on 11/6/17.
 */

public class ContactsAdapter extends CursorRecyclerViewAdapter<ContactsAdapter.ViewHolder> {

    private Context context;
    private ItemClickListener itemClickListener;
    private SQLiteDatabase mSqLiteDatabase;

    public interface ItemClickListener{
        public void onItemClick(String id,ViewHolder vh);
    }

    public ContactsAdapter(Context context, Cursor cursor,ItemClickListener itemClickListener) {
        super(context, cursor);
        this.context = context;
        this.itemClickListener = itemClickListener;
        ContactsDBHelper contactsDBHelper = new ContactsDBHelper(context);
        mSqLiteDatabase = contactsDBHelper.getReadableDatabase();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {

        String selection = DBContract.ContactEntry.KEY_CONTACT_ID + "=?";
        String[] selectionArgs = new String[]{cursor.getString(0)};
        Cursor localCursor = mSqLiteDatabase.query(DBContract.ContactEntry.TABLE_NAME,
                new String[]{DBContract.ContactEntry._ID},selection,selectionArgs,null,null,null,null);

        /* If localCursor is null the contactId is not present in the local DB*/
        if (localCursor.moveToFirst()) {
            holder.mCheckBox.setChecked(true);
            localCursor.close();
        } else {
            holder.mCheckBox.setChecked(false);
        }

        /*get the contact name*/
        String name = cursor.getString(1);

        if (name != null)
            holder.mContactName.setText(name);
        else
            holder.mContactName.setText(context.getString(R.string.unknown));

        if (cursor.getString(3) != null) {
            holder.mContactImage.setImageURI(Uri.parse(cursor.getString(3)));
            holder.mContactImage.setVisibility(View.VISIBLE);
            holder.mContactLetter.setVisibility(View.GONE);
        }else { /*If imageUri is null set the first letter of the contact name with random background*/
            if (name == null)
                return;
            char letter = name.toUpperCase().charAt(0);

            holder.mContactLetter.setText(String.valueOf(letter));

            GradientDrawable magnitudeCircle = (GradientDrawable) holder.mContactLetter.getBackground();

            magnitudeCircle.setColor(ResourcesCompat.getColor(context.getResources(), Utils.getRandomColor(),null));

            holder.mContactImage.setVisibility(View.GONE);
            holder.mContactLetter.setVisibility(View.VISIBLE);

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mContactLetter;
        CircleImageView mContactImage;
        TextView mContactName;
        public CheckBox mCheckBox;

        ViewHolder(final View itemView) {
            super(itemView);
            mContactImage = (CircleImageView) itemView.findViewById(R.id.contact_image);
            mContactLetter = (TextView) itemView.findViewById(R.id.contact_letter);
            mContactName = (TextView) itemView.findViewById(R.id.contact_name);
            mCheckBox = (CheckBox)itemView.findViewById(R.id.contact_checkbox);
            itemView.setOnClickListener(this);
            mCheckBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.contact_checkbox){
                if (mCheckBox.isChecked()) {
                    mCheckBox.setChecked(false);
                    handleAction();
                } else {
                    Utils.removeFromDatabase(context, getId());
                }
            }else {

                if (!mCheckBox.isChecked())
                    handleAction();
                else {
                    mCheckBox.setChecked(false);
                    Utils.removeFromDatabase(context, getId());
                }
            }
        }

        /*Helper method to show th dialog with list of phone numbers*/
        private void handleAction() {
            itemClickListener.onItemClick(getId(),this);
        }

        /*Helper method to get the contactId*/
        public String getId(){
            Cursor cursor = getCursor();
            cursor.moveToPosition(getAdapterPosition());
            return cursor.getString(0);
        }
    }
}
