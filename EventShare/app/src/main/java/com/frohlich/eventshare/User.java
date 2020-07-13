package com.frohlich.eventshare;

import java.util.ArrayList;

public class User {
    private String name;
    private String email;
    private String uri;
    private String uid;
    private ArrayList<String> groupList;

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUri() {
        return uri;
    }

    public String getUid() {
        return uid;
    }

    public ArrayList<String> getGroupList() {
        return groupList;
    }



    public User(){
        name = "DEFAULT";
        email = "DEFAULT";
        uri = "DEFAULT";
        uid = "DEFAULT";
        groupList = null;
    }

    public User( String name, String email, String uri, String uid, ArrayList<String> groupList ) {
        this.name = name;
        this.email = email;
        this.uri = uri;
        this.uid = uid;
        this.groupList = groupList;
    }






}
