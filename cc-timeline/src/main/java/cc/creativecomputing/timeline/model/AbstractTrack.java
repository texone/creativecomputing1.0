package cc.creativecomputing.timeline.model;

import java.awt.Color;

public abstract class AbstractTrack {

	private String _myAddress;
	
	private Color _myColor;
	
	private boolean _myMuteFlag;
	
	protected boolean _myDirtyFlag;
	
	private TrackType _myTrackType = TrackType.GROUP;

	public AbstractTrack(TrackType theTrackType) {
		_myMuteFlag = false;
		_myTrackType = theTrackType;
		_myColor = Color.lightGray;
	}
	
	public AbstractTrack(final String theAddress, TrackType theTrackType) {
		this(theTrackType);
		setAddress(theAddress);
	}
	
	public void trackType(TrackType theTrackType) {
		_myTrackType = theTrackType;
	}
	
	public TrackType trackType() {
		return _myTrackType;
	}
	
	public void setAddress( String theAddress ) {
		_myAddress = theAddress;
	}
	
	public String address() {
		return _myAddress;
	}
	
	public void mute( boolean theFlag ) {
		_myMuteFlag = theFlag;
	}
	
	public boolean mute() {
		return _myMuteFlag;
	}
	
	public Color color() {
		return _myColor;
	}
	
	public void color(Color theColor) {
		_myColor = theColor;
	}
	
	public static final String COLOR_RED_ATTRIBUTE = "red";
	public static final String COLOR_GREEN_ATTRIBUTE = "green";
	public static final String COLOR_BLUE_ATTRIBUTE = "blue";
}
