package geoapp;

import java.util.ArrayList;

/**
 * A composite route is a route comprising of other routes,
 * with the constraint that the end and start of consecutive subroutes
 * will be within a certain distance of each other.
 */
public class CompositeRoute implements Route
{
	// Set to true to enable verbose parsing
	private ArrayList<Route> routes = new ArrayList<Route>();
	private int currentRoute = -1;
	private final String name;
	private String description;
	
	public CompositeRoute(String name, String description)
	{
		this.name = name;
		this.description = description;
	}
	
	/**
	 * Returns true if the route was added, false if it could not be connected.
	 */
	public void addRoute(Route r) throws RouteConnectionException
	{
		Waypoint newStart = r.getStart().getStartWaypoint();
		Segment end = getEnd();
		if(end != null)
		{
			Waypoint oldEnd = getEnd().getEndWaypoint();
			double distanceDelta = GeoUtils.calcMetresDistance(
						oldEnd.getLatitude(), oldEnd.getLongitude(), 
						newStart.getLatitude(), newStart.getLongitude()
					);
			double heightDelta = Math.abs(newStart.getAltitude() - oldEnd.getAltitude());
			
			if(distanceDelta > 10d || heightDelta > 2d)
			{
				throw new RouteConnectionException();
			}
		}
		routes.add(r);
	}
	
	public ArrayList<Waypoint> getWaypoints()
	{
		ArrayList<Waypoint> result = new ArrayList<Waypoint>();
		boolean firstRoute = true;
		for(Route route : routes)
		{
			boolean firstPoint = true;
			for(Waypoint point : route.getWaypoints())
			{
				// If this is not the first route, and this is the first point of this route,
				// then do not add the waypoint to the collection (it would be counted twice)
				if(!firstRoute && firstPoint)
				{
					// Do not add
				}else
				{
					// Add to the collection
					result.add(point);
				}
				firstPoint = false;
			}
			firstRoute = false;
		}
		return result;
	}
	
	@Override
	public int getNumWaypoints()
	{
		return getWaypoints().size();
	}
	
	@Override
	public double getHorizontalDistance()
	{
		double distance = 0d;
		for(Route r : routes)
		{
			distance += r.getHorizontalDistance();
		}
		return distance;
	}
	
	@Override
	public double getAscension() {
		double distance = 0d;
		for(Route r : routes)
		{
			distance += r.getAscension();
		}
		return distance;
	}

	@Override
	public double getDescension() {
		double distance = 0d;
		for(Route r : routes)
		{
			distance += r.getDescension();
		}
		return distance;
	}
	
	public Route getSubroute(int index) throws ArrayIndexOutOfBoundsException
	{
		if(index < 0 || index >= routes.size())
		{
			throw new ArrayIndexOutOfBoundsException();
		}
		
		return routes.get(index);
	}

	@Override
	public Segment getStart() {
		if(routes.size() <= 0)
		{
			return null;
		}
		return routes.get(0).getStart();
	}

	@Override
	public Segment getEnd() {
		if(routes.size() <= 0)
		{
			return null;
		}
		return routes.get(routes.size() - 1).getEnd();
	}

	@Override
	public boolean hasNext() {
		if(routes.size() <= 0)
		{
			return false;
		}
		if(routes.get(currentRoute).hasNext()
		|| routes.size() > currentRoute + 1)
		{
			return true;
		}
		return false;
	}

	@Override
	public Segment next() {
		if(!hasNext())
		{
			return null;
		}
		
		Route current = routes.get(currentRoute);
		if(current.hasNext())
		{
			return current.next();
		}else
		{
			// Go to the next subroute
			currentRoute++;
			// reset the iterator of the next subroute
			routes.get(currentRoute).resetIterator();
			return next();
		}
	}

	@Override
	public void resetIterator()
	{
		currentRoute = 0;
		if(routes.size() > 0)
		{
			routes.get(0).resetIterator();
		}
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * Takes a valid route data string and returns the CompositeRoute representations.
	 * @param routeString
	 * @return
	 */
	public static ArrayList<CompositeRoute> parse(String routeString)
	{
		String[] routeData = routeString.split("\n");
		
		// A set of unparsed routes.
		// Each comprises of an array list with the first element being the route
		// name, followed by the description, and any lines following
		ArrayList<ArrayList<String>> unparsedRoutes = new ArrayList<ArrayList<String>>();
		
		// The current route we're parsing (used in the first pass of the route data).
		ArrayList<String> currentRoute = new ArrayList<String>();
		for(int ii = 0; ii < routeData.length; ii++)
		{
			// remove whitespace at the beginning and end of lines
			routeData[ii] = routeData[ii].trim();
		}
		
		boolean searchingForRoute = true;
		
		// true if the current line has a description
		boolean hasDescription;
		
		// true if the current line has a 'pointer' to another route.
		boolean hasPointer; 
		
		// First pass of parser, bins all the lines in the route data
		// into lists of strings, discards any invalid data.
		for(int ii = 0; ii < routeData.length; ii++)
		{
			String description = RouteDataParser.description(routeData[ii]);
			String name = RouteDataParser.name(routeData[ii]);
			if(name != null)
				name = name.trim();
			
			String pointer = RouteDataParser.pointer(routeData[ii]);
			hasDescription = description != null;
			hasPointer = pointer != null;
			
			if(searchingForRoute)
			{
				// Start the route at the next line with a description
				if(hasDescription)
				{
					// This is the start of a new route, create it:
					
					currentRoute = new ArrayList<String>();
					currentRoute.add(name);
					currentRoute.add(description);
					
					searchingForRoute = false;
				}
			}else
			{
				// We are inside a route already. End it when we find
				// a line with no description or reference to another route.
				
				// this line is part of the current route, add it:
				currentRoute.add(routeData[ii]);
				
				if(!hasDescription && !hasPointer)
				{
					// This line has neither a pointer nor a description, it must be the end of the route.
					searchingForRoute = true;
					
					// Add the created route to the unparsed list
					unparsedRoutes.add(currentRoute);
					currentRoute = null;
				}
			}
		}
		
		
		// Second pass, actually creates the subroutes of each composite path.
		ArrayList<CompositeRoute> result = new ArrayList<CompositeRoute>();
		
		for(int ii = 0; ii < unparsedRoutes.size(); ii++)
		{
			ArrayList<String> inputLines = unparsedRoutes.get(ii);
			if(inputLines != null)
			{
				ArrayList<String> unparsedLines = getExpandedRoute(unparsedRoutes, ii);
				if(unparsedLines != null)
				{
					String name = inputLines.get(0);
					String description = inputLines.get(1);
					// Turn the expanded lines into a route
					
					CompositeRoute newRoute = new CompositeRoute(name, description);
					Waypoint current, last = null;
					String currentLine = "";
					for(int line = 0; newRoute != null && line < unparsedLines.size(); line++)
					{
						try
						{
							
							currentLine = unparsedLines.get(line);
							current = Waypoint.parse(currentLine);
							
							if(last != null)
							{
								Segment segment = new Segment(last, current);
								newRoute.addRoute(segment);
							}
							
							last = current;
						}catch(WaypointDescriptionException e)
						{
							// The waypoint string was invalid, and thus so is this route
							newRoute = null;
						}catch(IllegalArgumentException e)
						{
							// The description did not exist, this must be connected to the next route
						}catch(RouteConnectionException e)
						{
							// The added segment could not be connected to the route, making this route invalid:
							newRoute = null;
						}
					}
					// Add the final segment
					try
					{
						current = Waypoint.parse(currentLine + ",[]");
						
						if(last != null)
						{
							Segment segment = new Segment(last, current);
							newRoute.addRoute(segment);
						}
					}catch(Exception e)
					{
					}
					
					// Add the new route to the result
					if(newRoute != null)
						result.add(newRoute);
					
				}
			}
		}
		
		return result;
	}
	
	private static ArrayList<String> getExpandedRoute(ArrayList<ArrayList<String>> routes, int startRouteIndex)
	{
		
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> startRoute = routes.get(startRouteIndex);
		int routeLength = startRoute.size();
		String pointer;
		for(int ii = 2; ii < routeLength; ii++)
		{
			pointer = RouteDataParser.pointer(startRoute.get(ii));
			if(pointer == null)
			{
				// simply add this to the result
				result.add(startRoute.get(ii));
			}else
			{
				// This line is a pointer to another route, find the route and recursively parse it:
				int subRouteIndex = findUnparsedRouteIndex(routes, pointer);
				
				if(subRouteIndex >= 0)
				{
					ArrayList<String> subRoute = getExpandedRoute(routes, startRouteIndex, subRouteIndex);
					if(subRoute != null)
					{
						// The subroute was valid, add it:
						result.addAll(subRoute);
					}else
					{
						// The subroute was invalid, which invalidates this route too.
						return null;
					}
					
				}else
				{
					// The subroute could not be found. This route is invalid.
					return null;
				}
			}
		}
		return result;
	}
	
	private static ArrayList<String> getExpandedRoute(ArrayList<ArrayList<String>> routes, int startRouteIndex, int currentRouteIndex)
	{
		// If the current root is also the start route, then we have a circular dependency
		// and must early out:
		if(startRouteIndex == currentRouteIndex)
		{
			return null;
		}
		
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> currentRoute = routes.get(currentRouteIndex);
		int routeLength = currentRoute.size();
		String pointer;
		for(int ii = 2; ii < routeLength; ii++)
		{
			pointer = RouteDataParser.pointer(currentRoute.get(ii));
			if(pointer == null)
			{
				result.add(currentRoute.get(ii));
			}else
			{
				// This line is a pointer to another route, find the route and recursively parse it:
				int subRouteIndex = findUnparsedRouteIndex(routes, pointer);
				
				if(subRouteIndex >= 0)
				{
					ArrayList<String> subRoute = getExpandedRoute(routes, startRouteIndex, subRouteIndex);
					result.addAll(subRoute);
				}else
				{
					// The subroute could not be found. This route is invalid.
					return null;
				}
			}
		}
		return result;
	}
	
	/**
	 * Finds an unparsed route with the given name in a list of unparsed routes.
	 * Returns -1 if it could not be found.
	 * The search is case-sensitive.
	 * @param routes
	 * @return
	 */
	private static int findUnparsedRouteIndex(ArrayList<ArrayList<String>> routes, String name)
	{
		int result = -1;
		int index = 0;
		ArrayList<String> current;
		while(result == -1 && index < routes.size())
		{
			current = routes.get(index);
			
			if(current != null)
			{
				// Check the name
				if(current.size() > 0 && current.get(0).equals(name))
				{
					// The route was found, return the index
					result = index;
				}
			}
			index++;
		}
		return result;
	}
	
}
