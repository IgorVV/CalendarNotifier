package com.vasilyev.calendarnotifier.loader;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.vasilyev.calendarnotifier.db.DBProvider;
import com.vasilyev.calendarnotifier.entity.User;

/**
 * Created by Igor on 17.06.2015.
 */
public class UpdateUserTask extends AsyncTask<User, Void, Void> {

    private Context ctx;

    public UpdateUserTask(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected Void doInBackground(User... params) {
        for(User user : params) {
            updateUser(user);
        }
        return null;
    }

    private void updateUser(User user) {
        ContentValues cv = new ContentValues();
        cv.put(User.DB_NOTIFICATION_TIME, user.getNotificationTime());
        cv.put(User.DB_REMIND_BEFORE, user.getRemindBefore());
        ctx.getContentResolver().update(DBProvider.USER_CONTENT_URI, cv, User.DB_CONTACT_ID + " = ?", new String[] {user.getContactId()});
    }
}
