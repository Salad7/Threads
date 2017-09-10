# Threads
Anonymous group messaging app



public class driver {
	
	//Distance away from center to calculate
	final static double latDistFromCenter = 0.000100;
	//Distance away from center to calculate
	final static double lonDistFromCenter = 0.000100;

	public static void main(String[] args){
		double lat = 35.281412;
		double lon = -80.770331;
		//double lat = 5.000000;
		//double lon = -5.000000;
		//quad2 bounds
		//double latUpperBound = lat + latDistFromCenter;
		//double lonLowerBound = lon - lonDistFromCenter;
		
		//System.out.println("Quad 1" + " Latitude " +mnp(lat+latDistFromCenter)  + " Longitude "+ mnp(lon+lonDistFromCenter));
		//System.out.println("Quad 2" + " Latitude " +mnp(lat+latDistFromCenter)  + " Longitude "+ mnp(lon-lonDistFromCenter));
		//System.out.println("Quad 3" + " Latitude " +mnp(lat-latDistFromCenter)  + " Longitude "+ mnp(lon-lonDistFromCenter));
		//System.out.println("Quad 4" + " Latitude " +mnp(lat-latDistFromCenter)  + " Longitude "+ mnp(lon+lonDistFromCenter));
		
	runSimulationQuad2(mnp(lat-0.000100),mnp(lon-0.000100));
	}
	
	public static void runSimulationQuad2(double la, double lo){
		
		double resetVal = la;
		//System.out.println("Lat Starting at  " + mnp(la-latDistFromCenter) + " ending at " + mnp(la+latDistFromCenter));
		//System.out.println("Lon Starting at  " + mnp(lo-lonDistFromCenter) + " ending at " + mnp(lo+lonDistFromCenter));
		System.out.println("Lat start " + la);
		System.out.println("Lon start " + lo);
		for(double lonPos = 0; lonPos < 200; lonPos+=1){	
			lo = mnp(lo+0.000001);
			for(double latPos = 0; latPos < 200; latPos+=1){
				la = mnp(la+0.000001);
				System.out.println(" Lat " + mnp(la) + " Lon " + mnp(lo));
				//System.out.println("La now "+mnp(la)+" run throughs "+mnp(latPos));
			}
			
			la = resetVal;
//			System.out.println("Lo now "+mnp(lo)+" run throughs "+mnp(lonPos));
		}
		//System.out.println("Simulation executed  " + 200*200 + " times");

	}
	
	public static Double mnp(double v){
		return (double)Math.round(v * 1000000d) / 1000000d;
	}

}
