package com.example.msalad.threads;

/**
 * Created by cci-loaner on 10/23/17.
 */

// Threads
  //      Anonymous group messaging app

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;

public class ThreadFinder {

    //Distance away from center to calculate
    final static double latDistFromCenter = 0.000100;
    //Distance away from center to calculate
    final static double lonDistFromCenter = 0.000100;

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

    public static ArrayList<LatLon> runSimulationQuad2(double la, double lo){
        ArrayList<LatLon> ll = new ArrayList<>();
        double resetVal = la;
        System.out.println("Lat start " + la);
        System.out.println("Lon start " + lo);
        for(double lonPos = 0; lonPos <= 10; lonPos+=1){
            lo = mnp(lo+0.000001);
            for(double latPos = 0; latPos <= 10; latPos+=1){
                la = mnp(la+0.000001);
                System.out.println(" Lat " + mnp(la) + " Lon " + mnp(lo));
                ll.add(new LatLon(mnp(la),mnp(lo)));
                //System.out.println("La now "+mnp(la)+" run throughs "+mnp(latPos));
            }
            la = resetVal;
        }
        return ll;
    }

    public static Double mnp(double v){
        return (double)Math.round(v * 1000000d) / 1000000d;
    }



}




