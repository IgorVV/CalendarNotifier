package com.vasilyev.calendarnotifier.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.vasilyev.calendarnotifier.R;

/**
 * Created by Igor on 25.06.2015.
 */
public class BaseActivity  extends AppCompatActivity {

    protected Toolbar toolbar;


    protected void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
