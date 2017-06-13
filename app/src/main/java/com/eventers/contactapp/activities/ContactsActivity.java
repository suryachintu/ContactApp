package com.eventers.contactapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.eventers.contactapp.R;
import com.eventers.contactapp.fragments.SelectedConatcsFragment;

public class ContactsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TWO_PANE = "two_pane";
    boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        mTwoPane = findViewById(R.id.detail_container) != null;

        if (mTwoPane){

            if (savedInstanceState == null)
                getSupportFragmentManager().beginTransaction().replace(R.id.detail_container,new SelectedConatcsFragment()).commit();

        }

        Button finishBtn = (Button)findViewById(R.id.finish_btn);

        finishBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (mTwoPane){

            SelectedConatcsFragment fragment = new SelectedConatcsFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(TWO_PANE,true);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container,fragment).commit();

        }else{
            startActivity(new Intent(ContactsActivity.this,SelectedConatcs.class));
        }

    }
}
