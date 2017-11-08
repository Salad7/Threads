package com.example.msalad.threads;

/**
 * Created by cci-loaner on 10/23/17.
 */

// Threads
  //      Anonymous group messaging app

        import java.nio.channels.CompletionHandler;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.Map;

public class ThreadFinder{

    //Distance away from center to calculate
    final static double latDistFromCenter = 0.000100;
    //Distance away from center to calculate
    final static double lonDistFromCenter = 0.000100;
    final static int MAX_SETTINGS_THREAD = 100;
    final static int MAX_MESSAGES = 10000;
    final static int MAX_TOPICS = 1000;
    final static int MAX_NOTIFICATIONS = 10000;

   // public static void main(String[] args){
     //   double lat = 35.281412;
       // double lon = -80.770331;
//		double lat = -5.000000;
//		double lon = -5.000000;
//		double lat = 35.307330;
//		double lon = -80.735730;
        //quad2 bounds
        //double latUpperBound = lat + latDistFromCenter;
        //double lonLowerBound = lon - lonDistFromCenter;

        //System.out.println("Quad 1" + " Latitude " +mnp(lat+latDistFromCenter)  + " Longitude "+ mnp(lon+lonDistFromCenter));
        //System.out.println("Quad 2" + " Latitude " +mnp(lat+latDistFromCenter)  + " Longitude "+ mnp(lon-lonDistFromCenter));
        //System.out.println("Quad 3" + " Latitude " +mnp(lat-latDistFromCenter)  + " Longitude "+ mnp(lon-lonDistFromCenter));
        //System.out.println("Quad 4" + " Latitude " +mnp(lat-latDistFromCenter)  + " Longitude "+ mnp(lon+lonDistFromCenter));

       // System.out.println(runSimulationQuad2(mnp(lat-0.000100),mnp(lon-0.000100)).size() + " Coordinates found!");
    //}

    public static ArrayList<LatLon> runSimulationQuad2(double la, double lo, MainActivity mainActivity){
        ArrayList<LatLon> ll = new ArrayList<>();
        double resetVal = la;
        System.out.println("Lat start " + la);
        System.out.println("Lon start " + lo);
        for(double lonPos = 0; lonPos <= 200; lonPos+=1){
            lo = mnp(lo+0.0001);
            for(double latPos = 0; latPos <= 200; latPos+=1){
                la = mnp(la+0.0001);
                //System.out.println(" Lat " + mnp(la) + " Lon " + mnp(lo));
                ll.add(new LatLon(mnp(la),mnp(lo)));
                //System.out.println("La now "+mnp(la)+" run throughs "+mnp(latPos));
            }
            la = resetVal;
        }
        mainActivity.coors_near_me = ll;
        mainActivity.new ThreadAsyncTask().execute();
        return ll;
    }

    public static Double mnp(double v){
        return (double)Math.round(v * 1000000d) / 1000000d;
    }

    public static int getLastDayInCalender(int month){
        if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12){
            return 31;
        }
        if(month == 2){
            return 28;
        }
        return 30;
    }


public static String getElapsedTime(int userTS){
    Date dt = new Date();
    //let calendar = new Calendar();
    int secondsCap = 60;
    int minutesCap = 3600;
    int hoursCap = 216000;
    int daysCap = 51840000;
    int month = dt.getMonth();
    int monthsCamp = getLastDayInCalender(month);
    int timestamp = getTimeStamp();
    int difference = timestamp - userTS;
    int getSeconds = difference;
    //Limit seconds in a minute
    if(getSeconds < 60){
        if(getSeconds == 1){
            return getSeconds+" second ago";
        }
        return getSeconds+" seconds ago";
    }
    //Limit seconds in an hour
    else if (getSeconds > 60 && getSeconds < 3600){
        if(getSeconds/60 == 1){
            return getSeconds/60+" minute ago";

        }
        return getSeconds/60+" minutes ago";

    }
    //Limit seconds in a day
    else if(getSeconds > 3600 && getSeconds < 86400){
        if(getSeconds/3600 == 1){
            return getSeconds/3600+" hour ago";

        }
        return getSeconds/3600+" hours ago";

    }
    //limit seconds in a week
    else if(getSeconds > 86400 && getSeconds < 604800){
        if(getSeconds/86400 == 1){
            return getSeconds/86400+" day ago";
        }
        return getSeconds/86400+" days ago";
    }
    //limit seconds in a month
    else if(getSeconds > 604800 && getSeconds < monthsCamp*864000){
        if(getSeconds/604800 == 1){
            return 1+" week ago";

        }else{
            return getSeconds/604800+" weeks ago";

        }
    }
    //limit seconds in a year
    else if(getSeconds > monthsCamp*864000 && getSeconds < monthsCamp*864000*12){
        if(getSeconds/(monthsCamp*864000) == 1){
            return getSeconds/(monthsCamp*864000)+" month ago";
        }
        return getSeconds/(monthsCamp*864000)+" months ago";
    }

    else{
        return " over a year ago";
    }
}

public static int getTimeStamp(){
    Date d = new Date();
    int time = (int) (d.getTime());
    return time/1000;
}


}




