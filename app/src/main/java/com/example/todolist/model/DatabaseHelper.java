package com.example.todolist.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

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
        db.execSQL("CREATE TABLE " + Constants.TABLE + "("
                + "_id INTEGER PRIMARY KEY, "
                + Constants.TASK_ID + " INTEGER, "
                + Constants.TASK_TITLE + " TEXT, "
                + Constants.TASK_DESCRIPTION + " TEXT, "
                + Constants.TASK_POSITION + " INTEGER"
                +  ");");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new RuntimeException("onUpgrade not setup"); // FIXME
    }

    public void insertTaskItem(Context context, long taskId, String title) {
        int itemPosition = getMaxColumnData(context); // FIXME always 0
        Timber.i("%s item position %d", Constants.LOG_TAG, itemPosition);
        ContentValues cv = new ContentValues();
        cv.put(Constants.TASK_ID, taskId);
        cv.put(Constants.TASK_TITLE, title);
        cv.put(Constants.TASK_POSITION, itemPosition);

        SQLiteDatabase db = getDb(context); // writable dbase instance
        db.insert(Constants.TABLE, Constants.TASK_ID, cv);
    }

    public Cursor loadTaskItems(Context context) {
        SQLiteDatabase db = getDb(context);
        return  (db.rawQuery("SELECT * FROM tasks ORDER BY " + Constants.TASK_ID + " DESC", null));
//        Cursor result = db.query(
//                Constants.TABLE,
//                new String[] {"ROWID AS _ID", Constants.TASK_ID, Constants.TASK_DESCRIPTION},
//                null, null, null, null, Constants.TASK_ID);
//        result.getCount();
//
//        return result;
    }

    public Cursor loadTaskItem(Context context, long taskId) {
        SQLiteDatabase db = getDb(context);
        return (db.rawQuery("SELECT * FROM tasks where " + Constants.TASK_ID +"='" + taskId + "'", null));
    }

    public void deleteTaskItem(Context context, long taskId) {
        SQLiteDatabase db = getDb(context);
        // String deleteQuery = "DELETE FROM tasks where _ID='" + position + "'";
        //db.execSQL(deleteQuery); // FIXME ?? sql injection risk
        //String selection = "_id = ?";
        String selection = Constants.TASK_ID + " = ?";
        String[] args = {String.valueOf(taskId)};
        db.delete(Constants.TABLE, selection, args);
    }

    public void updateTaskItem(Context context, ContentValues values){
        Timber.i("%s: updating item in the dbase", Constants.LOG_TAG);
        SQLiteDatabase db = getDb(context);
        // String[] args = {values.getAsString(Constants.TASK_ID), values.getAsString(Constants.TASK_DESCRIPTION)};
        // db.execSQL("INSERT OR REPLACE INTO tasks (" + Constants.TASK_ID + ", " + Constants.TASK_DESCRIPTION + ") VALUES (?, ?)", args);
//        String update = "UPDATE " + Constants.TABLE + " SET " +
//                Constants.TASK_DESCRIPTION + " = " + values.getAsString(Constants.TASK_DESCRIPTION) +
//                ", WHERE " + Constants.TASK_ID + " = " + values.getAsString(Constants.TASK_ID);
        // Timber.i("%s: statement %s", Constants.LOG_TAG, str);
        //db.execSQL(update);
        db.update(Constants.TABLE, values, Constants.TASK_ID + " = " + values.getAsString(Constants.TASK_ID), null);
    }

    private SQLiteDatabase getDb(Context context) {
        if(mDatabase == null) {
            mDatabase = getInstance(context).getWritableDatabase();
        }
        return mDatabase;
    }

    private int getMaxColumnData(Context context) {
        SQLiteDatabase db = getDb(context);
        final SQLiteStatement statement =
                db.compileStatement("SELECT MAX(" + Constants.TASK_POSITION + ") FROM " + Constants.TABLE);

        return (int) statement.simpleQueryForLong();
    }

}
