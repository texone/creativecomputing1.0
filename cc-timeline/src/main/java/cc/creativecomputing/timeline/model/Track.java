package cc.creativecomputing.timeline.model;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.xml.CCXMLElement;

public class Track extends AbstractTrack{

	private TrackData _myTrackData;
	
    private double _myMinValue = 0;
    private double _myMaxValue = 1;
    
    private boolean _myAccumulateData = false;
	
	private Map<String, String> _myExtras;

	
	
	public Track(TrackType theType){
		super(theType);
		_myTrackData = new TrackData(this);
	}
	
	public Track() {
		this(TrackType.DOUBLE);
	}
	
	public Track(final String theAddress, TrackType theType) {
		super(theAddress, theType);
		setAddress(theAddress);
		_myTrackData = new TrackData(this);
	}
	
	public Track(final String theAddress) {
		this(theAddress, TrackType.DOUBLE);
	}
	
	public void extras(Map<String, String> theExtras) {
		_myExtras = theExtras;
	}
	
	public Map<String, String> extras() {
		return _myExtras;
	}
	
	public void addExtra(String theKey, String theValue) {
		if(_myExtras == null)_myExtras = new HashMap<String, String>();
		_myExtras.put(theKey, theValue);
	}
	
	public void accumulateData(final boolean theAccumulateData) {
		_myAccumulateData = theAccumulateData;
		
		if(_myAccumulateData && !(_myTrackData instanceof AccumulatedTrackData)) {
			_myTrackData = new AccumulatedTrackData(this);
		}
	}
	
	public boolean accumulateData() {
		return _myAccumulateData;
	}
	
    public void valueRange(double theMinValue, double theMaxValue) {
        _myMinValue = theMinValue;
        _myMaxValue = theMaxValue;
    }
    
    public double minValue() {
    	return _myMinValue;
    }
    
    public double maxValue() {
    	return _myMaxValue;
    }
	
	public TrackData trackData() {
		return _myTrackData;
	}
	
	public void trackData(TrackData theTrackData) {
		_myTrackData = theTrackData;
		_myDirtyFlag = false;
	}
	
	public boolean isDirty() {
		return _myDirtyFlag || _myTrackData.isDirty();
	}
	
	public void setDirty(boolean theFlag) {
		_myDirtyFlag = theFlag;
		_myTrackData.setDirty(theFlag);
	}
	
	private static final String TRACK_ELEMENT = "Track";
	private static final String TRACK_EXTRAS = "Extras";
	public static final String ADDRESS_ATTRIBUTE = "address";
	private static final String MUTE_ATTRIBUTE = "mute";
	private static final String ACCUMULATE_ATTRIBUTE = "accumulate";
	
	private static final String TYPE_ATTRIBUTE = "type";
	private static final String MIN_ATTRIBUTE = "min";
	private static final String MAX_ATTRIBUTE = "max";
	
	public CCXMLElement toXML(double theStart, double theEnd) {
		CCXMLElement myTrackXML = new CCXMLElement(TRACK_ELEMENT);
		myTrackXML.addAttribute(ADDRESS_ATTRIBUTE, address());
		myTrackXML.addAttribute(MUTE_ATTRIBUTE, mute());
		myTrackXML.addAttribute(ACCUMULATE_ATTRIBUTE, accumulateData());
			
		myTrackXML.addAttribute(TYPE_ATTRIBUTE, trackType().toString());
		myTrackXML.addAttribute(MIN_ATTRIBUTE, minValue());
		myTrackXML.addAttribute(MAX_ATTRIBUTE, maxValue());
			
		myTrackXML.addAttribute(COLOR_RED_ATTRIBUTE, color().getRed());
		myTrackXML.addAttribute(COLOR_GREEN_ATTRIBUTE, color().getGreen());
		myTrackXML.addAttribute(COLOR_BLUE_ATTRIBUTE, color().getBlue());
			
		myTrackXML.addChild(trackData().toXML(theStart, theEnd));
		
		if(_myExtras != null) {
			CCXMLElement myExtraXML = myTrackXML.createChild(TRACK_EXTRAS);
			for(String myKey:_myExtras.keySet()) {
				myExtraXML.createChild(myKey, _myExtras.get(myKey));
			}
		}
		return myTrackXML;
	}
	
	public void fromXML(CCXMLElement theTrackXML) {
		setAddress(theTrackXML.attribute(ADDRESS_ATTRIBUTE));
		mute(theTrackXML.booleanAttribute(MUTE_ATTRIBUTE));
		accumulateData(theTrackXML.booleanAttribute(ACCUMULATE_ATTRIBUTE, false));
		trackType(TrackType.valueOf(theTrackXML.attribute(TYPE_ATTRIBUTE)));
		valueRange(theTrackXML.doubleAttribute(MIN_ATTRIBUTE), theTrackXML.doubleAttribute(MAX_ATTRIBUTE));
		
		if(theTrackXML.hasAttribute(COLOR_RED_ATTRIBUTE)) {
			Color myColor = new Color(
				theTrackXML.intAttribute(COLOR_RED_ATTRIBUTE),
				theTrackXML.intAttribute(COLOR_GREEN_ATTRIBUTE),
				theTrackXML.intAttribute(COLOR_BLUE_ATTRIBUTE)
			);
			color(myColor);
		}
		
		CCXMLElement myTrackDataXML = theTrackXML.child(TrackData.TRACKDATA_ELEMENT);
//		TrackData myTrackData;
//		if(accumulateData()) {
//			myTrackData = new AccumulatedTrackData(this);
//		}else {
//			myTrackData = new TrackData(this);
//		}
//		myTrackData.fromXML(myTrackDataXML);
//		trackData(myTrackData);
		_myTrackData.clear();
		_myTrackData.fromXML(myTrackDataXML);
		
		CCXMLElement myExtrasXML = theTrackXML.child(TRACK_EXTRAS);
		if(myExtrasXML == null)return;
		_myExtras = new HashMap<String, String>();
		for(CCXMLElement myExtraXML:myExtrasXML) {
			_myExtras.put(myExtraXML.name(), myExtraXML.content());
		}
	}
}
