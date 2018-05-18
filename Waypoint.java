package geoapp;

public class Waypoint 
{
	private GeoPosition position;
	private String description;
	
	public Waypoint(String description, GeoPosition position)
	{
		this.position = position;
		this.description = description;
	}
	
	public GeoPosition getPosition()
	{
		return position;
	}
	
	public double getLatitude()
	{
		return position.latitude;
	}
	
	public double getLongitude()
	{
		return position.longitude;
	}
	
	public double getAltitude()
	{
		return position.altitude;
	}
	
	/**
	 * Parses a waypoint from a route data string.
	 * Returns null if the string was invalid.
	 * Valid strings are of the form <latitude>,<longitude>,<altitude>,[description]
	 * @param s
	 * @return
	 */
	public static Waypoint parse(String s) throws IllegalArgumentException, WaypointDescriptionException
	{
		String[] components = s.split(",");
		// Early out if there are not exactly 4 components
		if(components.length != 4)
		{
			throw new IllegalArgumentException();
		}
		// Also ensure the description is correctly formatted:
		components[3] = components[3].trim();
		if(!components[3].startsWith("[") || !components[3].endsWith("]"))
		{
			throw new WaypointDescriptionException();
		}
		
		// Parse the lat, long, and altitude:
		double latitude, longitude, altitude;
		try
		{
			latitude = Double.parseDouble(components[0]);
			longitude = Double.parseDouble(components[1]);
			altitude = Double.parseDouble(components[2]);
		}catch(NumberFormatException e)
		{
			// One of the numerical arguments was invalid.
			return null;
		}
		String description = components[3].substring(1, components[3].length() - 1);
		
		GeoPosition position = new GeoPosition(latitude, longitude, altitude);
		
		return new Waypoint(description, position);
	}
	
	@Override
	public String toString()
	{
		return position.toString() + " " + description;
	}
}
