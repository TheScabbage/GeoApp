package geoapp.ui;

import java.util.ArrayList;

import geoapp.*;

/**
 * Interface for connecting the app with the UI. 
 * Implementing this allows a class to be injected during application initialisation.
 */
public interface UIController
{
	public void positionUpdated(GeoPosition position);
	public void routesUpdated(ArrayList<CompositeRoute> routes);
	public void targetWaypointUpdated(Waypoint newTarget);
	public void routeDownloadFailed();
}