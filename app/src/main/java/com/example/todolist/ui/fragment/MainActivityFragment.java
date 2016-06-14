package com.example.todolist.ui.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.todolist.R;
import com.example.todolist.common.Constants;
import com.example.todolist.common.ContractFragment;
import com.example.todolist.event.ModelLoadedEvent;

import de.greenrobot.event.EventBus;


public class MainActivityFragment extends
        ContractFragment<MainActivityFragment.Contract> {

    public interface Contract {
        void deleteTaskItem(long position);
    }

    public MainActivityFragment() { }

    public static MainActivityFragment newInstance(){
        return new MainActivityFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        assert view != null;
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        // registerForContextMenu(listView); // enable context menu

        // enable contextual action bar
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_delete, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        ListAdapter adapter = getListAdapter();
                        for (int i = adapter.getCount() - 1; i >= 0; i--) {
                            if (getListView().isItemChecked(i)) {
                                Cursor cursor = (Cursor) adapter.getItem(i);
                                if(cursor != null && cursor.getCount() >= i) {
                                    cursor.moveToPosition(i);
                                    long id = cursor.getLong(cursor.getColumnIndex(Constants.TASK_POSITION));
                                    getContract().deleteTaskItem(id); // forward call to hosting activity
                                }
                            }
                        }
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // no-op
                return false;
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // no-op
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // no-op
            }

        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SimpleCursorAdapter adapter =
                new SimpleCursorAdapter(getActivity(),
                        R.layout.list_item,
                        null,
                        new String[] {Constants.TASK_DESCRIPTION},
                        new int[] {R.id.description},
                        0);
        setListAdapter(adapter);
    }

    //    public void addTask(){
//        Utils.showToast(getActivity(), "the fragment has spoken");
//    }

    // get the update to the model via a sticky post


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.menu_delete, menu);
    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo menuInfo =
//               (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        int position = menuInfo.position;
//        ListAdapter adapter = getListAdapter();
//        Cursor cursor = (Cursor) adapter.getItem(position);
//        if(cursor != null && cursor.getCount() >= position) {
//            cursor.moveToPosition(position);
//            long id = cursor.getLong(cursor.getColumnIndex(Constants.TASK_POSITION));
//            getContract().deleteTaskItem(id); // forward call to hosting activity
//        }
//
//        return super.onContextItemSelected(item);
//    }



    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().registerSticky(this);
    }


    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }


    public void onEventMainThread(ModelLoadedEvent event) {
        // returns a cursor
//        Cursor cursor = event.getModel();
//        if (cursor.moveToFirst()) {
//            do {
//                Timber.i("%s: id: %s, description: %s",
//                        Constants.LOG_TAG, cursor.getString(cursor.getColumnIndex(Constants.TASK_POSITION)),
//                        cursor.getString(cursor.getColumnIndex(Constants.TASK_DESCRIPTION)));
//            } while (cursor.moveToNext());
//        }
        ((SimpleCursorAdapter)getListAdapter()).changeCursor(event.getModel());
    }


}
