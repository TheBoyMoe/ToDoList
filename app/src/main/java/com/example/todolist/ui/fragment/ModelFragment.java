package com.example.todolist.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todolist.common.Utils;
import com.example.todolist.model.TaskItem;


/**
 * Headless fragment which is responsible for interacting with the database
 */
public class ModelFragment extends Fragment{

    private Context mApp = null;

    public ModelFragment(){}

    public static ModelFragment newInstance(){
        return new ModelFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null; // headless fragment
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // EventBus.getDefault().register(this); // REQD ??

        if (mApp == null) {
            // use the application context so as to prevent a memory leak
            mApp = context.getApplicationContext();
            // query dbase on start
            new LoadItemsThread().start();
        }
    }

//    @Override
//    public void onDetach() {
//        EventBus.getDefault().unregister(this); // REQD ??
//        super.onDetach();
//    }




    // query multiple items
    class LoadItemsThread extends Thread {

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            // fetch all items from the database, posting the resulting cursor the the EventBus
            Utils.queryAllItems(mApp);
        }
    }






    // fetch item
    class LoadItemThread extends Thread {

        private int mPosition;

        public LoadItemThread(int position) {
            super();
            mPosition = position;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            // TODO query dbase
        }
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
            // TODO delete item
        }
    }

    // update item
    class UpdateItemThread extends Thread {

        private TaskItem mItem;

        public UpdateItemThread(TaskItem item) {
            mItem = item;
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            // TODO update item
        }
    }



}
