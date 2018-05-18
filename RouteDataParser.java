package geoapp;

/**
 * Contains helper methods for parsing line of route data, obtained from a central server.
 *
 */
public class RouteDataParser
{
	public static String pointer(String line)
	{
		// Check if this line even contains an asterisk, return null 
		// if it doesnt as it can't possibly have a pointer
		if(!line.contains("*"))
		{
			return null;
		}
		
		// A pointer line cannot have a description, so early out if it does:
		if(description(line) != null)
		{
			return null;
		}
		
		// Similar to the logic of finding the description, but with a * preceeding the name of the pointer
		line = line.trim();
		
		int startIndex = line.indexOf('*');
		return line.substring(startIndex+1, line.length());
	}
	
	/**
	 * Returns the description string from a valid line
	 * from a route data string, or null if it does not exist.
	 * @param s
	 * @return
	 */
	public static String description(String s)
	{
		String description;
		int start;
		
		if(s.length() <= 0 || !s.endsWith("]"))
		{
			// string does not contain a description
			return null;
		}
		
		start = s.indexOf("[");
		// Get the description text
		description = s.substring(start+1, s.length() - 1);
		
		return description;
	}
	
	/**
	 * Returns the name of a route from a valid line from 
	 * a route data string, assuming the string is the beginning of a route.
	 * Returns null if the string is invalid.
	 * @param s
	 * @return
	 */
	public static String name(String s)
	{
		String name;
		int end;
		
		if(s.length() <= 0 || !s.endsWith("]"))
		{
			// string does not contain a description, and thus, no name.
			return null;
		}
		
		end = s.indexOf("[");
		// Get the name text
		name = s.substring(0, end);
		
		return name;
	}
}
