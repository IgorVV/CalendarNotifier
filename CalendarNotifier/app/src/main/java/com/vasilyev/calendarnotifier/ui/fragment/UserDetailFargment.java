package com.vasilyev.calendarnotifier.ui.fragment;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.vasilyev.calendarnotifier.MainActivity;
import com.vasilyev.calendarnotifier.R;
import com.vasilyev.calendarnotifier.db.DBProvider;
import com.vasilyev.calendarnotifier.entity.User;
import com.vasilyev.calendarnotifier.loader.UpdateUserTask;
import com.vasilyev.calendarnotifier.utils.ActivityStarter;

import java.util.Calendar;


public class UserDetailFargment extends BaseFragment implements OnClickListener, OnCheckedChangeListener {
	
	private TextView tvUserName;
	private TextView tvUserBirthday;
	private TextView tvNotificationTime;
	private CheckBox cbRmindBefore;
	private Spinner spDays;
	
	private User user;
	
	public static UserDetailFargment newInstance(int userId) {
		Bundle args = new Bundle();
		args.putInt(ActivityStarter.USER_ID, userId);
		UserDetailFargment fragment = new UserDetailFargment();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_user_detail, container, false);
		
		tvUserName = (TextView) v.findViewById(R.id.tvUserName);
		tvUserBirthday = (TextView) v.findViewById(R.id.tvUserBirthday);
		tvNotificationTime = (TextView) v.findViewById(R.id.tvNotificationTime);
		cbRmindBefore = (CheckBox) v.findViewById(R.id.cbRmindBefore);
		spDays = (Spinner) v.findViewById(R.id.spDays);	
		
		tvNotificationTime.setOnClickListener(this);
		cbRmindBefore.setOnCheckedChangeListener(this);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.days_notifier));
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spDays.setAdapter(adapter);
		spDays.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (user != null) {
					user.setRemindBefore(position);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		getLoaderManager().initLoader(ActivityStarter.ID_LOADER_USER, null, new UserCallback()).forceLoad();

		return v;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
	    switch (menuItem.getItemId()) {
	        case android.R.id.home:
				if(user != null) {
					new UpdateUserTask(getActivity().getBaseContext()).execute(user);
				}
	            getActivity().onBackPressed();
	            break;
			case R.id.cancel:
				getActivity().onBackPressed();
				break;
	    }
	    return (super.onOptionsItemSelected(menuItem));
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK) {
			return;
		}
		if(requestCode == ActivityStarter.REQUEST_DATE) {
			int hour = data.getIntExtra(ActivityStarter.HOUR_OF_DAY, 0);
	        int minute = data.getIntExtra(ActivityStarter.MINUTE, 0);
			String time = pad(hour) + ":" + pad(minute);
			user.setNotificationTime(time);
	        tvNotificationTime.setText(time);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.tvNotificationTime: {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				int hour = user.getCalendarNotificationTime().get(Calendar.HOUR);
				int minute = user.getCalendarNotificationTime().get(Calendar.MINUTE);
				DialogFragment timeDialog = TimePickerFragment.newInstance(hour, minute);
				timeDialog.setTargetFragment(UserDetailFargment.this, ActivityStarter.REQUEST_DATE);
				timeDialog.show(fm, null);
				break;
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch(buttonView.getId()) {
			case R.id.cbRmindBefore: {
				if(isChecked) {
					spDays.setVisibility(View.VISIBLE);
					user.setRemindBefore(0);
				} else {
					spDays.setVisibility(View.INVISIBLE);
					user.setRemindBefore(-1);
				}
				break;
			}	
		}
	}

	private String pad(int c) {
		if (c >= 10) {
			return String.valueOf(c);
		} else {
			return "0" + c;
		}
	}

	private class UserCallback implements LoaderCallbacks<Cursor> {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
			if(id == ActivityStarter.ID_LOADER_USER) {
				return new CursorLoader(getActivity(), ContentUris.withAppendedId(DBProvider.USER_CONTENT_URI, getArguments().getInt(ActivityStarter.USER_ID)), null, null, null, null);
			} else {
				return null;
			}
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor result) {
			if(result.moveToFirst()) {
				user = User.getInstanceByCursor(result);
				tvUserName.setText(user.getFullname());
				tvUserBirthday.setText(user.getBirthday());
				tvNotificationTime.setText(user.getNotificationTime());
				if(user.getRemindBefore() > -1) {
					cbRmindBefore.setChecked(true);
					spDays.setSelection(user.getRemindBefore());
				} else {
					cbRmindBefore.setChecked(false);
				}
			} else {
				tvUserName.setText("");
				tvUserBirthday.setText("");
				tvNotificationTime.setText("");
				cbRmindBefore.setChecked(false);
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			tvUserName.setText("");
			tvUserBirthday.setText("");
			tvNotificationTime.setText("");
			cbRmindBefore.setChecked(false);
		}
		
	}

}
