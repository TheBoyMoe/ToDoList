package com.example.todolist.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.todolist.R;
import com.example.todolist.common.Utils;
import com.example.todolist.ui.fragment.MainActivityFragment;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private MainActivityFragment mMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Timber.i("toolbar: " + toolbar);
        setSupportActionBar(toolbar);

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
                            .inputRange(2, 20)
                            .input(null, null, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                    // fetch entered text and save
                                    Utils.showToast(MainActivity.this, input.toString());
                                }
                            })
                            .positiveText("Save")
                            .negativeText("Cancel")
                            .show();
                }
            });
        }

        // cache a reference to the fragment
        mMainFragment = (MainActivityFragment) getSupportFragmentManager()
                                                .findFragmentById(R.id.fragment_container);
        if(mMainFragment == null){
            mMainFragment = MainActivityFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mMainFragment)
                    .commit();
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

}
