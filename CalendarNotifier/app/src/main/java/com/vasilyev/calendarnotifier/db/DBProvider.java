package com.vasilyev.calendarnotifier.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.vasilyev.calendarnotifier.entity.User;

/**
 * Created by Igor on 17.06.2015.
 */
public class DBProvider extends ContentProvider {

    private static final String AUTHORITY = "com.vasilyev.calendarnotifier.providers";

    private static final String USER_PATH = "users";

    public static final Uri USER_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + USER_PATH);

    public static final String USER_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + USER_PATH;
    public static final String USER_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + USER_PATH;

    private static final int URI_USERS = 1;
    private static final int URI_USERS_ID = 2;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, USER_PATH, URI_USERS);
        uriMatcher.addURI(AUTHORITY, USER_PATH + "/#", URI_USERS_ID);
    }

    private static final String TABLE_FROM = "(SELECT "
            + BaseColumns._ID + ", " + User.DB_FULLNAME + ", "
            + User.DB_BIRTHDAY +", " + User.DB_CONTACT_ID + ", "
            + User.DB_REMIND_BEFORE + ", " + User.DB_NOTIFICATION_TIME
            + ", CASE WHEN length(" + User.DB_BIRTHDAY + ")  >= 10"
            + " THEN " + User.DB_BIRTHDAY
            + " ELSE '0001' || substr(" + User.DB_BIRTHDAY + ", 2,  length(" + User.DB_BIRTHDAY + "))"
            + " END as fbirthday FROM " + User.DB_TABLE_NAME + ") u";

    private static final String DATE_SORT = "CASE WHEN strftime"
            + "('%m', fbirthday) - strftime('%m', 'now') >= 0"
            + " THEN strftime('%m', fbirthday) - strftime('%m', 'now')"
            + " ELSE 12 + strftime('%m', fbirthday)"
            + " END as difference";

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private static final Handler handler = new Handler();

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case URI_USERS: {
                if (TextUtils.isEmpty(sortOrder)) {
//					Sort by month and day from current date
                    if (projection != null) {
                        String[] tmpProjection = new String[projection.length + 1];
                        System.arraycopy(projection, 0, tmpProjection, 0, projection.length);
                        tmpProjection[tmpProjection.length - 1] = DATE_SORT;
                        projection = tmpProjection;
                    } else {
                        projection = new String[2];
                        projection[0] = "*";
                        projection[1] = DATE_SORT;
                        sortOrder = "difference, strftime('%d', " + User.DB_BIRTHDAY +")";
                    }
                }
                break;
            }
            case URI_USERS_ID: {
                String id = uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection)) {
                    selection = BaseColumns._ID + " = " + id;
                } else {
                    selection = selection + " AND " + BaseColumns._ID + " = " + id;
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Wrong uri : " + uri);
            }
        }
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FROM, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        if (uriMatcher.match(uri) != URI_USERS) {
            throw new IllegalArgumentException("Wrong URI : " + uri);
        }

        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        int result = 0;
        for(ContentValues cv : values) {
            db.insertWithOnConflict(User.DB_TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
            result++;
        }
        db.setTransactionSuccessful();
        db.endTransaction();


        getContext().getContentResolver().notifyChange(USER_CONTENT_URI, null);
        return result;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_USERS:
                return USER_CONTENT_TYPE;
            case  URI_USERS_ID:
                return USER_CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if(uriMatcher.match(uri) != URI_USERS) {
            throw new IllegalArgumentException("Wrong URI : " + uri);
        }

        db = dbHelper.getWritableDatabase();
        long rowID = db.insertWithOnConflict(User.DB_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        Uri resultUri = ContentUris.withAppendedId(USER_CONTENT_URI, rowID);

        getContext().getContentResolver().notifyChange(uri, null);

        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_USERS:
                break;
            case URI_USERS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = BaseColumns._ID + " = " + id;
                } else {
                    selection = selection + " AND " + BaseColumns._ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI : " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(User.DB_TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_USERS:
                break;
            case URI_USERS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = BaseColumns._ID + " = " + id;
                } else {
                    selection = selection + " AND " + BaseColumns._ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI : " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(User.DB_TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }
}
