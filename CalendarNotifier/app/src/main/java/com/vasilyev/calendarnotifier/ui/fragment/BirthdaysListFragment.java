package com.vasilyev.calendarnotifier.ui.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vasilyev.calendarnotifier.R;
import com.vasilyev.calendarnotifier.db.DBProvider;
import com.vasilyev.calendarnotifier.entity.User;
import com.vasilyev.calendarnotifier.loader.NotificationTask;
import com.vasilyev.calendarnotifier.utils.ActivityStarter;

/**
 * Created by Igor on 17.06.2015.
 */
public class BirthdaysListFragment extends ListFragment{

    private SimpleCursorAdapter adapter;

    private String[] listFrom = new String[] {User.DB_FULLNAME, User.DB_BIRTHDAY};
    private int[] listTo = new int[] {R.id.tvUserName, R.id.tvUserBirthday};

    private OnUserClickListener userCLickListener;

    private Menu menu;
    private boolean dataWasLoad = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        userCLickListener = (OnUserClickListener) activity;
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item_user, null, listFrom, listTo, 0);
        setListAdapter(adapter);

        getLoaderManager().initLoader(ActivityStarter.ID_LOADER_USERS, null, new UsersLoaderCallback()).forceLoad();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.menu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
            case R.id.set_notifiactions:
                new NotificationTask().execute(getActivity().getBaseContext());
                break;
        }
        return (super.onOptionsItemSelected(menuItem));
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        adapter.getCursor().moveToPosition(position);
        userCLickListener.onUserCLick(adapter.getCursor().getInt(adapter.getCursor().getColumnIndex(BaseColumns._ID)));
    }

    private class UsersLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if(id == ActivityStarter.ID_LOADER_USERS) {
                dataWasLoad = false;
                return new CursorLoader(getActivity(), DBProvider.USER_CONTENT_URI, null, null, null, null);
            } else {
                return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            dataWasLoad = true;
            if(menu != null) {
                menu.findItem(R.id.set_notifiactions).setVisible(true);
            }
            adapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            dataWasLoad = false;
            if(menu != null) {
                menu.findItem(R.id.set_notifiactions).setVisible(false);
            }
            adapter.swapCursor(null);
        }
    }

    public interface OnUserClickListener {
        public void onUserCLick(int userId);
    }
}
