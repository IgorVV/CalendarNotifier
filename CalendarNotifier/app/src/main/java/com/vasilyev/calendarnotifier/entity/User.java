package com.vasilyev.calendarnotifier.entity;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.vasilyev.calendarnotifier.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Igor on 17.06.2015.
 */
public class User {

    public static final String DB_TABLE_NAME = "users";

    public static final String DB_CONTACT_ID = "contact_id";
    public static final String DB_FULLNAME = "full_name";
    public static final String DB_BIRTHDAY = "birthday";
    public static final String DB_NOTIFICATION_TIME = "notification_time";
    public static final String DB_REMIND_BEFORE = "remind_before";

    public static final String[] DB_TABLE_COLUMNS = {BaseColumns._ID, DB_CONTACT_ID, DB_FULLNAME, DB_BIRTHDAY, DB_NOTIFICATION_TIME, DB_REMIND_BEFORE};

    private final static SimpleDateFormat FORMAT_BIRTHDAY = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat FORMAT_BIRTHDAY_2 = new SimpleDateFormat("--MM-dd");
    private final static SimpleDateFormat FORMAT_NOTIFICATION_TIME = new SimpleDateFormat("hh:mm");

    private String contactId = "";
    private String fullname = "";
    private String birthday = "";
    private String notificationTime = "";
    private int remindBefore = -1;

    private User() {
    }

    public User(String contactId, String fullname, String birthday) {
        this.contactId = contactId;
        this.fullname = fullname;
        this.birthday = birthday;
    }

    public static User getInstanceByCursor(Cursor cursor) {
        User user = new User();
        user.setContactId(cursor.getString(cursor.getColumnIndex(DB_CONTACT_ID)));
        user.setFullname(cursor.getString(cursor.getColumnIndex(DB_FULLNAME)));
        user.setBirthday(cursor.getString(cursor.getColumnIndex(DB_BIRTHDAY)));
        user.setNotificationTime(cursor.getString(cursor.getColumnIndex(DB_NOTIFICATION_TIME)));
        user.setRemindBefore(cursor.getInt(cursor.getColumnIndex(DB_REMIND_BEFORE)));
        return user;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getNotificationTime() {
        if(TextUtils.isEmpty(notificationTime)) {
            return Constants.DEFAULT_NOTIFICATION_TIME;
        } else {
            return notificationTime;
        }
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getRemindBefore() {
        return remindBefore;
    }

    public void setRemindBefore(int remindBefore) {
        this.remindBefore = remindBefore;
    }

    public Calendar getCalendarNotificationTime() {

        try {
            Calendar nt = Calendar.getInstance();
            nt.setTime(FORMAT_NOTIFICATION_TIME.parse(getNotificationTime()));
            return nt;
        }  catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid time = " + getNotificationTime());
        }
    }
}
