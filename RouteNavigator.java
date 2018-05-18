package geoapp;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Handles events from the GpsLocator and traverses the current route.
 *
 */
public class RouteNavigator extends GpsLocator
{
	private CompositeRoute route;
	private Segment currentSegment;
	private GeoPosition currentPosition;
	private ReentrantLock positionLock = new ReentrantLock();
	
	
	
	public void setRoute(CompositeRoute route)
	{
		this.route = route;
		reset();
	}
	
	public void reset()
	{
		if(route != null)
		{
			route.resetIterator();
			if(route.hasNext())
			{
				currentSegment = route.next();
			}else
			{
				currentSegment = null;
			}
		}else
		{
			currentSegment = null;
		}
	}
	
	/**
	 * Returns the most recent position returned from GpsLocator.
	 * @return
	 */
	public GeoPosition getCurrentPosition()
	{
		return currentPosition;
	}
	
	public double distanceToNextWaypoint()
	{
		GeoPosition nextPosition = getNextWaypoint().getPosition();
		return GeoUtils.calcMetresDistance(currentPosition.latitude, currentPosition.longitude, nextPosition.latitude, nextPosition.longitude);
	}
	
	public Waypoint getNextWaypoint()
	{
		return currentSegment == null ? null : currentSegment.getEndWaypoint();
	}
	
	public Segment getCurrentSegment()
	{
		return currentSegment;
	}
	
	public void moveNext()
	{
		currentSegment = route.next();
	}

	/**
	 * Called when a new location is received from the GpsLocator super class.
	 */
	@Override
	public void locationReceived(double latitude, double longitude, double altitude)
	{
		positionLock.lock();
		currentPosition = new GeoPosition(latitude, longitude, altitude);
		positionLock.unlock();
		App.updatePosition();
	}
}
