package com.vasilyev.calendarnotifier.loader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;

import com.vasilyev.calendarnotifier.db.DBProvider;
import com.vasilyev.calendarnotifier.entity.User;

/**
 * Created by Igor on 17.06.2015.
 */
public class DataCollector extends AsyncTask<Context, Void, Void> {

    @Override
    protected Void doInBackground(Context... params) {
        Context ctx = params[0];
        insertContactsBirthdays(ctx);
        return null;
    }

    private void insertContactsBirthdays(Context context) {
        String displayNameField = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY : ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

        String where = ContactsContract.Data.MIMETYPE + "= ? AND " +
                ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;

        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[] {ContactsContract.CommonDataKinds.Phone.CONTACT_ID, displayNameField,
                ContactsContract.CommonDataKinds.Event.START_DATE}, where,
                new String[] {ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
        int i = 0;
        ContentValues contentValues []  = new ContentValues [cursor.getCount()] ;
        while(cursor.moveToNext()) {
            ContentValues cv = new ContentValues();
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
            String displayName = cursor.getString(cursor.getColumnIndex(displayNameField));
            String birthday = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
            cv.put(User.DB_CONTACT_ID, contactId);
            cv.put(User.DB_FULLNAME, displayName);
            cv.put(User.DB_BIRTHDAY, birthday);
            contentValues[i++] = cv;
        }
        context.getContentResolver().bulkInsert(DBProvider.USER_CONTENT_URI, contentValues);
    }
}
