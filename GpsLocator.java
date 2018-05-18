package geoapp;

public abstract class GpsLocator
{
	public abstract void locationReceived(double latitude, double longitude, double altitude);
}