package com.example.osm.Uploads;

import com.google.firebase.database.Exclude;

public class UploadNotes {
    String folderType,type, name, url, mKey;

    public UploadNotes() {
    }

    public UploadNotes(String folderType,String type, String name, String url, String mKey) {
        this.folderType = folderType;
        this.type = type;
        this.name = name;
        this.url = url;
        this.mKey = mKey;
    }

    public String getFolderType() {
        return folderType;
    }

    public void setFolderType(String folderType) {
        this.folderType = folderType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }
}
