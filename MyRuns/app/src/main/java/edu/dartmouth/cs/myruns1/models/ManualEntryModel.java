package edu.dartmouth.cs.myruns1.models;

public class ManualEntryModel {

    private String title, data;

    public ManualEntryModel(String title, String data) {
        this.title = title;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


}
