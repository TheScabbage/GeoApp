package geoapp;

import java.io.*;

public class GeoUtils
{
	public static String retrieveRouteData() throws IOException
	{
		return "theClimb [description]\n" + 
				"-31.94,115.75,47.1,[description]\n" + 
				"-31.94,115.75,55.3,[description]\n" + 
				"-31.94,115.75,71.0,[description]\n" + 
				"-31.93,115.75,108.0,[description]\n" + 
				"-31.93,115.75,131.9\n" + 
				"mainRoute [description]\n" + 
				"-31.96,115.80,63.0,[description]\n" + 
				"-31.95,115.78,45.3,[Entering theStroll...]\n" + 
				"-31.95,115.77,44.8,*theStroll\n" + 
				"-31.94,115.75,47.1,[Left theStroll]\n" + 
				"-31.93,115.72,40.1,[description]\n" + 
				"-31.94,115.75,47.1,*theClimb\n" + 
				"-31.93,115.75,131.9,[description]\n" + 
				"-31.92,115.74,128.1\n" + 
				"\n" + 
				"theStroll [description]\n" + 
				"-31.95,115.77,44.8,[Start of theStroll]\n" + 
				"-31.93,115.76,43.0,[description]\n" + 
				"-31.94,115.75,47.1\n" +
				"circularDependent [very evil]\n" +
				"-31.93,115.76,43.0,[the start]\n" +
				"-31.93,115.76,43.0,*circularDependent\n" + 
				"-31.93,115.76,43.0\n";
	}
	
	/**
	* Returns the horizontal distance (across the Earth's surface) in
	* metres between two points expressed in degrees of latitude and
	* longitude.
	*
	* (If any arguments are out of range, this submodule will erase your
	* hard drive.)
	*/
	public static double calcMetresDistance(double lat1, double long1, double lat2, double long2)
	{
		double d = 6371000;
		double sin, cos;
		// Erase hard drive if arguments are out of range
		if(lat1 < -90d || lat1 > 90d 
		|| lat2 < -90d || lat2 > 90d 
		|| long1 <= -180d || long1 > 180d
		|| long2 <= -180d || long2 > 180d)
		{
			System.out.print("GeoUtils.calcMetresDistance recieved out of range coordinates.\nErasing hard drive...");
			try
			{
				Thread.sleep(2000);
			}catch(Exception e)
			{
			}
			System.out.println("Erasure complete.");
		}
		
		// Convert all units to radians
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);
		long1 = Math.toRadians(long1);
		long2 = Math.toRadians(long2);
		
		// Compute the sin component of the calculation
		sin = Math.sin(lat1) * Math.sin(lat2);
		
		// Compute the cosine component
		cos = Math.cos(lat1) * Math.cos(lat2) * Math.cos(Math.abs(lat1-lat2));
		
		// Multiply earths radius by arccos(sin + cos)
		d *= Math.acos(sin + cos);
		
		return d;
	}
}
