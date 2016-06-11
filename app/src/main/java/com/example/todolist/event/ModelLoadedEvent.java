package com.example.todolist.event;

import android.database.Cursor;

public class ModelLoadedEvent extends BaseEvent{

    private final Cursor mModel;

    public ModelLoadedEvent(Cursor model) {
        mModel = model;
    }

    public Cursor getModel() {
        return mModel;
    }
}
