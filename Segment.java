package geoapp;

import java.util.ArrayList;

public class Segment implements Route
{
	private Waypoint start, end;
	private boolean hasNext = true;
	
	public Segment(Waypoint start, Waypoint end)
	{
		this.start = start;
		this.end = end;
	}
	
	public Waypoint getStartWaypoint()
	{
		return start;
	}
	
	public Waypoint getEndWaypoint()
	{
		return end;
	}
	
	public Segment getEnd()
	{
		return this;
	}
	
	public Segment getStart() 
	{
		return this;
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public Segment next() {
		hasNext = false;
		return this;
	}

	@Override
	public void resetIterator() {
		hasNext = true;
	}
	
	@Override
	public int getNumWaypoints()
	{
		return 2;
	}
	
	@Override
	public Iterable<Waypoint> getWaypoints()
	{
		ArrayList<Waypoint> points = new ArrayList<Waypoint>();
		points.add(start);
		points.add(end);
		return points;
	}

	@Override
	public double getHorizontalDistance() {
		return GeoUtils.calcMetresDistance(start.getLatitude(), start.getLongitude(), end.getLatitude(), end.getLongitude());
	}

	@Override
	public double getAscension() {
		double heightDelta = end.getAltitude() - start.getAltitude();
		// Return the height delta if it is greater than or equal to 0 (ascension)
		// Otherwise return 0
		return heightDelta >= 0d ? heightDelta : 0;
	}

	@Override
	public double getDescension() {
		double heightDelta = end.getAltitude() - start.getAltitude();
		// Return abs(height delta) if it is less than 0 (descent)
		// Otherwise return 0
		return heightDelta < 0d ? -heightDelta : 0d;
	}
	
	public String toString()
	{
		return start + " -> " + end;
	}
	
}
