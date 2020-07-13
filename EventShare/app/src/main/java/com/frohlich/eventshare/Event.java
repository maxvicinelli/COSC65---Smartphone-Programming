package com.frohlich.eventshare;

import java.util.ArrayList;

public class Event {
    public String location;
    public String title;
    public String openToAll;
    public String description;
    public String date;
    public String time;
    public String groupID;
    public String hostID;
    public ArrayList<String> upVotes;
    public String id;


    public String hostName;

    public Event(){
        location = "DEMO";
        title = "DEMO";
        openToAll = "DEMO";
        description = "DEMO";
        date = "DEMO";
        time = "DEMO";
        groupID = "DEMO";
        hostID = "DEMO";
        hostName = "DEMO";
        upVotes = null;
        id = "0";
    }

    public Event(String date, String openToAll, String groupID, String description, String hostID, String location, String time, String title, String hostname, ArrayList<String> upVotes, String id){
        this.location = location;
        this.title = title;
        this.openToAll = openToAll;
        this.description = description;
        this.date = date;
        this.time = time;
        this.groupID = groupID;
        this.hostID = hostID;
        this.hostName = hostname;
        this.upVotes = upVotes;
        this.id = id;
    }

    public String getLocation(){
        return location;
    }

    public String getTitle() {
        return title;
    }

    public String getOpenToAll() {
        return openToAll;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getHostID() {
        return hostID;
    }

    public String getHostName() {
        return hostName;
    }

    public ArrayList<String> getUpVotes() {
        if (upVotes == null){
            return new ArrayList<String>();
        }
        return upVotes;
    }

    public String getId() {
        return id;
    }

}
