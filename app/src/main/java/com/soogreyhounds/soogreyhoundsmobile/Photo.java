package com.soogreyhounds.soogreyhoundsmobile;

public class Photo {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    int id;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    String mTitle;

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    String uuid;

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    String url;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

     private String note;
    private String mPerson;


    public String getPerson() {
        return mPerson;
    }

    public void setPerson(String person) {
        mPerson = person;
    }
    }

