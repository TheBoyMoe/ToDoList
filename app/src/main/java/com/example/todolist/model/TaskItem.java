package com.example.todolist.model;

public class TaskItem {

    private long mId;
    private String mTitle;
    private String mDescription;
    private long mPosition;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public long getPosition() {
        return mPosition;
    }

    public void setPosition(long position) {
        mPosition = position;
    }

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
