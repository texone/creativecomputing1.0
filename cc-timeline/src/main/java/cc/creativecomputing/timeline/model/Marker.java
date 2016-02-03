package cc.creativecomputing.timeline.model;

public class Marker {
	private String _myName;
	private double _myTime;

	public Marker(final String theName, final double theTime) {
		_myName = theName;
		_myTime = theTime;
	}

	public String getName() {
		return _myName;
	}

	public double getTime() {
		return _myTime;
	}
}