package com.example.msalad.threads;

import android.support.annotation.NonNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cci-loaner on 10/23/17.
 */

public class Topics implements Comparable{

    int position,replies, timeStamp;
    String topicTitle,parent,hostUID,topicInvite;
    Map anonCode = new HashMap<String,String>();
    ArrayList<String> upvoters,notifyList;
    ArrayList<Message> messages;

    public ArrayList<String> getNotifyList() {
        return notifyList;
    }

    public void setNotifyList(ArrayList<String> notifyList) {
        this.notifyList = notifyList;
    }

    public Topics() {
    }

    public String getTopicInvite() {
        return topicInvite;
    }

    public void setTopicInvite(String topicInvite) {
        this.topicInvite = topicInvite;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Topics copy = (Topics) o;
        if(timeStamp > copy.timeStamp){
        return -1;
        }
        else if(timeStamp < copy.timeStamp){
            return 1;
        }
        else{
            return 0;
        }
    }
}
