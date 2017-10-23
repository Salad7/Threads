package com.example.msalad.threads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cci-loaner on 10/23/17.
 */

public class Message {

    int position,upvotes,replies, timeStamp;
    String topicTitle,parent,hostUID,msg;
    Map anonCode = new HashMap<String,String>();
    ArrayList<String> upvoters;

    public Message() {
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getReplies() {
        return replies;
    }

    public void setReplies(int replies) {
        this.replies = replies;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getHostUID() {
        return hostUID;
    }

    public void setHostUID(String hostUID) {
        this.hostUID = hostUID;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map getAnonCode() {
        return anonCode;
    }

    public void setAnonCode(Map anonCode) {
        this.anonCode = anonCode;
    }

    public ArrayList<String> getUpvoters() {
        return upvoters;
    }

    public void setUpvoters(ArrayList<String> upvoters) {
        this.upvoters = upvoters;
    }
}
