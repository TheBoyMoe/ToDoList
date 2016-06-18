package com.example.todolist.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.todolist.R;
import com.example.todolist.common.Constants;
import com.example.todolist.common.ContractFragment;
import com.example.todolist.event.ModelLoadedEvent;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.SimpleDragSortCursorAdapter;

import de.greenrobot.event.EventBus;

public class MainActivityFragment extends
        ContractFragment<MainActivityFragment.Contract> {

    private DragSortListView mListView                                                                  ;
    private CustomAdapter mAdapter;

    public interface Contract {
        void deleteTaskItem(long taskId);
        void updateTaskItem(long taskId, String description);
    }

    public MainActivityFragment() { }

    public static MainActivityFragment newInstance(){
        return new MainActivityFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mListView =  (DragSortListView)view.findViewById(android.R.id.list);
        mListView.setEmptyView(view.findViewById(android.R.id.empty));

        // instantiate and bind the adapter
        mAdapter = new CustomAdapter(getActivity(),
                R.layout.list_item,                 // list item layout
                null,                               // cursor
                new String[]{Constants.TASK_TITLE}, // columns
                new int[]{R.id.title},              // views to bind the data to
                0);

        mListView.setAdapter(mAdapter);

        // enable contextual action bar
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

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

                        // delete the items from the database
                        //ListAdapter adapter = getListAdapter();
                        for (int i = mAdapter.getCount() - 1; i >= 0; i--) {

                            if (mListView.isItemChecked(i)) {
                                Cursor cursor = (Cursor) mAdapter.getItem(i);
                                if(cursor != null && cursor.getCount() >= i) {
                                    cursor.moveToPosition(i);
                                    long id = cursor.getLong(cursor.getColumnIndex(Constants.TASK_ID));
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

        // configure list item drag
        mListView.setDragScrollProfile(new DragSortListView.DragScrollProfile() {
            @Override
            public float getSpeed(float w, long t) {
                if (w > 0.8f) {
                    // Traverse all views in a millisecond
                    return ((float) mAdapter.getCount()) / 0.001f;
                } else {
                    return 10.0f * w;
                }
            }
        });


        // launch dialog to allow list item edit on item click
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                // Utils.showToast(getActivity(), "clicked on item " + position);
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);
                if(cursor != null){
                    String title = cursor.getString(cursor.getColumnIndex(Constants.TASK_TITLE));
                    long taskId = cursor.getLong(cursor.getColumnIndex(Constants.TASK_ID));
                    // forward the position of the item clicked on to the hosting activity
                    getContract().updateTaskItem(taskId, title);
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_persist, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_persist) {
            mAdapter.persistChanges();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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

    @SuppressWarnings("unused")
    public void onEventMainThread(ModelLoadedEvent event) {

//        Cursor cursor = event.getModel();
//        if (cursor.moveToFirst()) {
//            do {
//                Timber.i("%s: id: %s, description: %s" ,
//                        Constants.LOG_TAG, cursor.getString(cursor.getColumnIndex(Constants.TASK_ID)),
//                        cursor.getString(cursor.getColumnIndex(Constants.TASK_TITLE)));
//            } while (cursor.moveToNext());
//        }

        // pass the retrieved cursor to the adapter
        mAdapter.changeCursor(event.getModel());
    }


    private class CustomAdapter extends SimpleDragSortCursorAdapter {

        public CustomAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return super.getView(position, convertView, parent);
        }

        public void persistChanges() {
            Cursor cursor = getCursor();
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                int itemPosition = getListPosition(cursor.getPosition());
                // Timber.i("%s: item position %d", Constants.LOG_TAG, itemPosition);
                if (itemPosition == REMOVED) {
                    long taskID = cursor.getLong(cursor.getColumnIndex(Constants.TASK_ID));
                    // delete item from the database - forward the call to the hosting activity
                    getContract().deleteTaskItem(taskID);

                } else if (itemPosition != cursor.getPosition()) {
                    // TODO update item in the database

                }
            }
        }
    }



}
