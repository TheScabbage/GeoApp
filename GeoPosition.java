package geoapp;

import java.text.DecimalFormat;

public class GeoPosition
{
	public final double latitude, longitude, altitude;
	
	/**
	 * Creates a position from the given latitude, longitude and altitude.
	 * Values out of range are wrapped around to be within bounds
	 * (eg. a latitude of 95 degrees will result in 85 degrees)
	 * @param latitude
	 * @param longitude
	 * @param altitude
	 */
	public GeoPosition(double latitude, double longitude, double altitude)
	{
		this.latitude = wrapLatitude(latitude);
		this.longitude = wrapLongitude(longitude);
		this.altitude = altitude;
	}
	
	/**
	 * Wraps the input value to a valid latitude using modulo arithmetic.
	 * @param latitude
	 * @return
	 */
	public static double wrapLatitude(double latitude)
	{
		if(latitude < 90d && latitude > -90d)
		{
			return latitude;
		}
		
		// offset so 0 is at the south pole (all values now positive)
		latitude += 90d;
		// this now has the property that it can be inverted and still be the same latitude:
		if(latitude < 0d)
		{
			latitude = -latitude;
		}
		
		latitude = latitude % 360d;
		if(latitude > 180d)
		{
			latitude = 360d - latitude;
		}
		// offset back to equator = 0 degrees
		latitude -= 90d;
		return latitude;
	}
	
	public static double wrapLongitude(double longitude)
	{
		if(longitude > -180d && longitude <= 180d)
		{
			return longitude;
		}
		
		longitude += 180d;
		longitude = longitude % 360d;
		longitude -= 180d;
		return longitude;
	}
	
	@Override
	public String toString()
	{
		DecimalFormat formatter = new DecimalFormat("###.###");
		return "(" + formatter.format(latitude) + 
				", " + formatter.format(longitude) + 
				", " + formatter.format(altitude) + ")";
	}
}
