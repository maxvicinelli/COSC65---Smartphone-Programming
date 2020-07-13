package com.frohlich.eventshare;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Group {

    private String groupName;
    private String ownerName;
    private String ownerUID;

    public String getGroupID() {
        return groupID;
    }

    private String groupID;

    private ArrayList<String> groupUsers;

    public Group(){
        this.groupName = "HAH";
        this.ownerName = "HAH";
        this.ownerUID = "HAH";
        this.groupUsers = null;
    }

    public Group(String groupName, String ownerName, String ownerUID, ArrayList<String> groupUsers, String groupID) {
        this.groupName = groupName;
        this.ownerName = ownerName;
        this.ownerUID = ownerUID;
        this.groupUsers = groupUsers;
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerUID() {
        return ownerUID;
    }

    public void setOwnerUID(String ownerUID) {
        this.ownerUID = ownerUID;
    }

    public ArrayList<String> getGroupUsers() {
        return groupUsers;
    }

    public void setGroupUsers(ArrayList<String> groupUsers) {
        this.groupUsers = groupUsers;
    }
}
