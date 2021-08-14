package com.example.osm.Uploads;

public class AddSubjectNew {
    String folderType, name;

    public AddSubjectNew() {
    }

    public AddSubjectNew(String folderType, String name) {
        this.folderType = folderType;
        this.name = name;
    }

    public String getFolderType() {
        return folderType;
    }

    public void setFolderType(String folderType) {
        this.folderType = folderType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
