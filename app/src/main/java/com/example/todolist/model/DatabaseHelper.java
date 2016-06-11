package com.example.todolist.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todolist.common.Constants;

import timber.log.Timber;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "tasks_database.db";
    private static final int SCHEMA = 1;
    private static volatile DatabaseHelper sDatabaseHelper = null;
    private SQLiteDatabase mDatabase = null;

    public synchronized static DatabaseHelper getInstance(Context context){
        if(sDatabaseHelper == null) {
            sDatabaseHelper = new DatabaseHelper(context.getApplicationContext());
        }
        return sDatabaseHelper;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create table, defining table & column headings
        db.execSQL("CREATE TABLE tasks ("+
                "_id INTEGER PRIMARY KEY," +
                " description TEXT," +
                " position INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new RuntimeException("onUpgrade not setup"); // FIXME
    }

    public void insertTaskItem(Context context, ContentValues value) {
        Timber.i("%s: inserting item into dbase", Constants.LOG_TAG);
        SQLiteDatabase db = getDb(context); // writable dbase instance
//        ContentValues cv = new ContentValues();
//        cv.put(TASK_POSITION, item.getPosition());
//        cv.put(TASK_DESCRIPTION, item.getPosition());
        db .insert(Constants.TABLE, Constants.TASK_POSITION, value);
    }

    public Cursor loadTaskItems(Context context) {
        Timber.i("%s: loading items from the dbase", Constants.LOG_TAG);
        SQLiteDatabase db = getDb(context);
        return (db.rawQuery("SELECT * FROM tasks ORDER BY position DESC", null));
    }

    public Cursor loadTaskItem(Context context, int position) {
        Timber.i("%s: loading items from the dbase", Constants.LOG_TAG);
        SQLiteDatabase db = getDb(context);
        return (db.rawQuery("SELECT * FROM tasks where position='" + position + "'", null));
    }

    public void deleteTaskItem(Context context, int position) {
        Timber.i("%s: deleting item from the dbase", Constants.LOG_TAG);
        SQLiteDatabase db = getDb(context);
        String deleteQuery = "DELETE FROM tasks where position='" + position + "'";
        db.execSQL(deleteQuery); // FIXME ?? sql injection risk
    }

    public void updateTaskItem(Context context, TaskItem item){
        Timber.i("%s: updating item in the dbase", Constants.LOG_TAG);
        SQLiteDatabase db = getDb(context);
        String[] args = {String.valueOf(item.getPosition()), item.getDescription()};
        db.execSQL("INSERT OR REPLACE INTO tasks (position, description) VALUES (?, ?)", args);
    }

    private SQLiteDatabase getDb(Context context) {
        if(mDatabase == null) {
            mDatabase = getInstance(context).getWritableDatabase();
        }
        return mDatabase;
    }



}
