package com.vasilyev.calendarnotifier.utils;

import android.content.Context;
import android.content.Intent;

import com.vasilyev.calendarnotifier.ui.activity.UserDetailActivity;

public class ActivityStarter {
	
	public static final String USER_ID = "user_id";
	public static final String HOUR_OF_DAY = "hour_of_day";
	public static final String MINUTE = "minute";
	
	public static final int REQUEST_DATE = 0;

	public static final int ID_LOADER_USERS = 1;
	public static final int ID_LOADER_USER = 2;

	public static void startUserDetailActivity(Context ctx, int userId) {
		Intent intentDetailActivity = new Intent(ctx, UserDetailActivity.class);
		intentDetailActivity.putExtra(USER_ID, userId);
		ctx.startActivity(intentDetailActivity);
	}

}
