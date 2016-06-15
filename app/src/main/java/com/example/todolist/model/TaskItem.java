package com.example.todolist.model;

public class TaskItem {

    private String mDescription;
    private long mId;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    @Override
    public String toString() {
        return String.format("%s, %s", getId(), getDescription());
    }
}
