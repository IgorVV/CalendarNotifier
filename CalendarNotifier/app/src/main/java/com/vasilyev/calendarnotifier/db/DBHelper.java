package com.vasilyev.calendarnotifier.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.vasilyev.calendarnotifier.entity.User;

/**
 * Created by Igor on 17.06.2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "calendar_notifier";
    public static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS " + User.DB_TABLE_NAME
            + "(" + BaseColumns._ID +" INTEGER PRIMARY KEY,"
            + User.DB_CONTACT_ID + " TEXT,"
            + User.DB_FULLNAME + " TEXT, "
            + User.DB_BIRTHDAY + " TEXT, "
            + User.DB_NOTIFICATION_TIME + " TEXT,"
            + User.DB_REMIND_BEFORE + " INTEGER DEFAULT -1, unique(" +User.DB_CONTACT_ID + "));";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
