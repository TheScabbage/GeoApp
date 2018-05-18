package geoapp;

import java.util.Iterator;

/**
 * A route is an enumerable set of segments, with the constraint that each
 * consecutive segment will start within a set distance horizontally and vertically
 * of the last segment.
 */
public interface Route extends Iterator<Segment>
{
	public Segment getStart();
	public Segment getEnd();
	public void resetIterator();
	public double getHorizontalDistance();
	public double getAscension();
	public double getDescension();
	public int getNumWaypoints();
	public Iterable<Waypoint> getWaypoints();
}
