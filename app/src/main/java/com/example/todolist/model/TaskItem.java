package com.example.todolist.model;

public class TaskItem {

    private String mDescription;
    private int position;

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return String.format("%s, %s", getPosition(), getDescription());
    }
}
