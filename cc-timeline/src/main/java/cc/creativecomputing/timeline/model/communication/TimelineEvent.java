package cc.creativecomputing.timeline.model.communication;

import cc.creativecomputing.timeline.model.TrackType;

public class TimelineEvent {
	
	private double _myTime;
	private String _myAddress;
	private TrackType _myType;

	public TimelineEvent(double theTime, String theAddress, TrackType theTrackType) {
		_myTime = theTime;
		_myAddress = theAddress;
		_myType = theTrackType;
	}
	
	public TrackType type() {
		return _myType;
	}

	/**
	 * @return the time
	 */
	public double time() {
		return _myTime;
	}

	/**
	 * @return the address
	 */
	public String address() {
		return _myAddress;
	}
}
