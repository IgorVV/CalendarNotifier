package com.vasilyev.calendarnotifier.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.vasilyev.calendarnotifier.R;
import com.vasilyev.calendarnotifier.ui.fragment.UserDetailFargment;
import com.vasilyev.calendarnotifier.utils.ActivityStarter;

/**
 * Created by Igor on 25.06.2015.
 */
public class UserDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initToolbar();

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int userId = getIntent().getIntExtra(ActivityStarter.USER_ID, -1);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.container, UserDetailFargment.newInstance(userId));

        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
