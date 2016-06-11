package com.example.todolist.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.SimpleCursorAdapter;

import com.example.todolist.R;
import com.example.todolist.common.Constants;
import com.example.todolist.common.ContractFragment;
import com.example.todolist.event.ModelLoadedEvent;

import de.greenrobot.event.EventBus;


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
