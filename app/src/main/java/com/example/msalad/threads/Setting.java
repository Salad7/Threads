package com.example.msalad.threads;

/**
 * Created by cci-loaner on 10/23/17.
 */

public class Setting {
    String name,threadCode;
    int timeStamp;

    public Setting() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThreadCode() {
        return threadCode;
    }

    public void setThreadCode(String threadCode) {
        this.threadCode = threadCode;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }
}
