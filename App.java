package geoapp;
import geoapp.ui.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The main class for interfacing between the View and Controller.
 * All of the events within the application pass through this class.
 * Operates similarly to a singleton, but no instance exists and all 
 * fields and methods operate within a static context.
 */
public class App 
{
	
	private static ArrayList<CompositeRoute> routes;
	private static RouteNavigator navigator;
	private static UIController ui;
	
	// If true, routes will be downloaded from a central server on startup.
	private static boolean downloadRoutes = true;
	
	public static void init(UIController ui)
	{
		App.ui = ui;
		routes = new ArrayList<CompositeRoute>();
		
		// Download routes on initialisation:
		if(downloadRoutes)
		{
			ArrayList<CompositeRoute> newRoutes = readRouteData();
			if(newRoutes != null)
			{
				routes = newRoutes;
				ui.routesUpdated(routes);
			}
		}
	}
	
	/**
	 * Called by the navigator when the gps position recieves an update event.
	 */
	public static void updatePosition()
	{
		ui.positionUpdated(navigator.getCurrentPosition());
		
		// Check if we've reached the next waypoint
		if(navigator.distanceToNextWaypoint() < 10d)
		{
			navigator.moveNext();
			ui.targetWaypointUpdated(navigator.getNextWaypoint());
		}
	}
	
	public static void setRoute(CompositeRoute route)
	{
		navigator.setRoute(route);
	}
	
	public static Iterable<CompositeRoute> getRoutes()
	{
		return routes;
	}
	
	public static void setRouteDownload(boolean download)
	{
		downloadRoutes = download;
	}
	
	static ArrayList<CompositeRoute> readRouteData()
	{
		ArrayList<CompositeRoute> result = null;
		try
		{
			String routeData = GeoUtils.retrieveRouteData();
			result = CompositeRoute.parse(routeData);
		}catch(IOException e)
		{
			ui.routeDownloadFailed();
		}
		return result;
	}
}
