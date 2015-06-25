package com.vasilyev.calendarnotifier.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.vasilyev.calendarnotifier.utils.ActivityStarter;

public class TimePickerFragment extends DialogFragment implements OnTimeSetListener {
	
	public static TimePickerFragment newInstance(int hour, int minute) {
		Bundle args = new Bundle();
		args.putInt(ActivityStarter.HOUR_OF_DAY, hour);
		args.putInt(ActivityStarter.MINUTE, minute);
		TimePickerFragment fragment = new TimePickerFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int hour = getArguments().getInt(ActivityStarter.HOUR_OF_DAY);
        int minute = getArguments().getInt(ActivityStarter.MINUTE);
        
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }


	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if(getTargetFragment() == null) {
			return;
		}
		Intent intent = new Intent();
		intent.putExtra(ActivityStarter.HOUR_OF_DAY, hourOfDay);
		intent.putExtra(ActivityStarter.MINUTE, minute);
		
		getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
	}

	
	
}
