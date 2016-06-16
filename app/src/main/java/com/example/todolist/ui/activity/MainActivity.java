package com.example.todolist.ui.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.todolist.R;
import com.example.todolist.common.Constants;
import com.example.todolist.common.Utils;
import com.example.todolist.model.DatabaseHelper;
import com.example.todolist.ui.fragment.MainActivityFragment;
import com.example.todolist.ui.fragment.ModelFragment;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Contract {

    private static final String MODEL_FRAGMENT = "model_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Timber.i("toolbar: " + toolbar);
        setSupportActionBar(toolbar);

        // cache a reference to the fragment
        MainActivityFragment taskListFragment = (MainActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        if(taskListFragment == null){
            taskListFragment = MainActivityFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, taskListFragment)
                    .commit();
        }

        // cache a reference to the model fragment
        Fragment modelFragment = getSupportFragmentManager().findFragmentByTag(MODEL_FRAGMENT);
        if (modelFragment == null) {
            modelFragment = ModelFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(modelFragment, MODEL_FRAGMENT)
                    .commit();
        }

        FloatingActionButton addTask = (FloatingActionButton) findViewById(R.id.fab);
        if (addTask != null) {
            addTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // forward any click events to the fragment
                    // mMainFragment.addTask();

                    // launch dialog to allow user to create task
                    new MaterialDialog.Builder(MainActivity.this)
                            .title("Define a task you wish to complete")
                            .inputType(InputType.TYPE_CLASS_TEXT)
                            .inputRange(2, 100)
                            .input(null, null, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                    // fetch entered text and save to dbase
                                    ContentValues cv = new ContentValues();
                                    cv.put(Constants.TASK_ID, Utils.generateCustomId());
                                    cv.put(Constants.TASK_TITLE, input.toString());
                                    new InsertItemThread(cv).start();
                                }
                            })
                            .positiveText("Save")
                            .negativeText("Cancel")
                            .show();
                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return  (item.getItemId() == R.id.action_settings)
                || super.onOptionsItemSelected(item);
    }

    @Override
    public void deleteTaskItem(long taskId) {
        new DeleteItemThread(taskId).start();
    }

    @Override
    public void updateTaskItem(final long taskId, String description) {

        // load description into a dialog to allow updating
        new MaterialDialog.Builder(MainActivity.this)
                .title("Amend the task and save ")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .inputRange(2, 100)
                .input(null, description, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        // fetch the updated text
                        ContentValues cv = new ContentValues();
                        cv.put(Constants.TASK_ID, taskId);
                        cv.put(Constants.TASK_TITLE, input.toString());

                        // TODO update item in database
                        new UpdateItemThread(cv).start();
                    }
                })
                .positiveText("Save")
                .negativeText("Cancel")
                .show();

        // save the item back to the database

    }


    // insert item into database via a bkgd thread
    class InsertItemThread extends Thread {
        private ContentValues mValue;

        public InsertItemThread(ContentValues value) {
            super();
            mValue = value;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            try {
                DatabaseHelper.getInstance(MainActivity.this).insertTaskItem(MainActivity.this, mValue);
            } catch (Exception e) {
                Timber.e("%s: error adding item to dbase, %s", Constants.LOG_TAG, e.getMessage());
            }
            // query the dbase so as to trigger an update of the ui
            Utils.queryAllItems(MainActivity.this);
        }
    }

    // delete item from database via a bkgd thread
    class DeleteItemThread extends Thread {

        private long mId;

        public DeleteItemThread(long taskId) {
            mId = taskId;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            try {
                DatabaseHelper.getInstance(MainActivity.this).deleteTaskItem(MainActivity.this, mId);
            } catch (Exception e) {
                Timber.e("%s: error deleting item from database, %s", Constants.LOG_TAG, e.getMessage());
            }
            // trigger ui update
            Utils.queryAllItems(MainActivity.this);
        }
    }

    // fetch item from database
    class FetchItemThread extends Thread {

        private long mId;

        public FetchItemThread(long taskId) {
            mId = taskId;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            try {
                Cursor cursor = DatabaseHelper.getInstance(MainActivity.this).loadTaskItem(MainActivity.this, mId);

            } catch (Exception e) {
                Timber.e("%s: error fetching item from database, %s", Constants.LOG_TAG, e.getMessage());
            }
        }
    }

    // update database item via bkgd thread
    class UpdateItemThread extends Thread {

        private ContentValues mValues;

        public UpdateItemThread(ContentValues values) {
            mValues = values;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            try {
                DatabaseHelper.getInstance(MainActivity.this).updateTaskItem(MainActivity.this, mValues);
            } catch (Exception e) {
                Timber.e("%s: error deleting item from database, %s", Constants.LOG_TAG, e.getMessage());
            }
            // trigger ui update
            Utils.queryAllItems(MainActivity.this);
        }
    }


}