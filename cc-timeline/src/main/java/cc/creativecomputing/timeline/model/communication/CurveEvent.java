package cc.creativecomputing.timeline.model.communication;

import cc.creativecomputing.timeline.model.TrackType;

public class CurveEvent extends TimelineEvent{
	
	private double _myValue;

	public CurveEvent(double theTime, double theValue, String theAddress, TrackType theTrackType) {
		super(theTime, theAddress, theTrackType);
		_myValue = theValue;
	}

	/**
	 * @return the value
	 */
	public double value() {
		return _myValue;
	}

	/**
	 * @param theValue the value to set
	 */
	public void value(double theValue) {
		_myValue = theValue;
	}
}
