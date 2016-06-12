package com.example.todolist.ui.fragment;

import android.os.Bundle;
import android.os.Process;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.todolist.R;
import com.example.todolist.common.Constants;
import com.example.todolist.common.ContractFragment;
import com.example.todolist.common.Utils;
import com.example.todolist.event.ModelLoadedEvent;
import com.example.todolist.model.DatabaseHelper;

import de.greenrobot.event.EventBus;
import timber.log.Timber;


public class MainActivityFragment extends
        ContractFragment<MainActivityFragment.Contract> {


    interface Contract {
        // add methods
    }

    public MainActivityFragment() { }

    public static MainActivityFragment newInstance(){
        return new MainActivityFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            ListView listView = (ListView) view.findViewById(android.R.id.list);
            registerForContextMenu(listView);
        }
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

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


    // delete item
    class DeleteItemThread extends Thread {

        private int mPosition;

        public DeleteItemThread(int position) {
            mPosition = position;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            try {
                DatabaseHelper.getInstance(getActivity()).deleteTaskItem(getActivity(), mPosition);
            } catch (Exception e) {
                Timber.e("%s: error deleting item from database, %s", Constants.LOG_TAG, e.getMessage());
            }
            // trigger ui update
            Utils.queryAllItems(getActivity());
        }
    }



}
