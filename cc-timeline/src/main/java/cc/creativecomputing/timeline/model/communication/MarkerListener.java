package cc.creativecomputing.timeline.model.communication;

import cc.creativecomputing.timeline.model.points.MarkerPoint;

/**
 * Listener to react on marker events. These events happen when the time cursor goes over the marker.
 * @author artcom
 *
 */
public interface MarkerListener {
	
	/**
	 * Implement this method to define what happens when the time cursor goes over a marker
	 * @param thePoint marker the timeline cursor has moved over
	 */
	public void onMarker(MarkerPoint thePoint);
}
