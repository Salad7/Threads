package com.example.msalad.threads;

import java.util.ArrayList;

/**
 * Created by cci-loaner on 10/23/17.
 */

public class Thread {
    String threadTitle = "";
    ArrayList<String> anons;
    ArrayList<Topics> topics;
    int timeStamp;

    public Thread() {
    }

    public String getThreadTitle() {
        return threadTitle;
    }

    public void setThreadTitle(String threadTitle) {
        this.threadTitle = threadTitle;
    }

    public ArrayList<String> getAnons() {
        return anons;
    }

    public void setAnons(ArrayList<String> anons) {
        this.anons = anons;
    }

    public ArrayList<Topics> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<Topics> topics) {
        this.topics = topics;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }
}
