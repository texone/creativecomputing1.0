package cc.creativecomputing.timeline.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.util.logging.CCLog;
import cc.creativecomputing.xml.CCXMLElement;

public class GroupTrack extends Track{

	private List<Track> _myTracks;
	private boolean _myIsOpen;

	public GroupTrack() {
		super(TrackType.GROUP);
		_myTracks = new ArrayList<Track>();
		_myIsOpen = true;
	}
	
	public GroupTrack(final String theAddress) {
		super(theAddress, TrackType.GROUP);
		_myTracks = new ArrayList<Track>();
	}
	
	public List<Track> tracks(){
		return _myTracks;
	}
	
	public void addTrack(Track theTrack) {
		if(!_myTracks.contains(theTrack))_myTracks.add(theTrack);
	}
	
	public boolean isOpen(){
		return _myIsOpen;
	}
	
	public void isOpen(boolean theIsOpen){
		_myIsOpen = theIsOpen;
	}
	
	public static final String GROUP_TRACK_ELEMENT = "GroupTrack";
	public static final String GROUP_NAME_ATTRIBUTE = "name";
	private static final String GROUP_OPEN_ATTRIBUTE = "open";
	
	public CCXMLElement toXML(double theStart, double theEnd) {
		CCXMLElement myTrackXML = new CCXMLElement(GROUP_TRACK_ELEMENT);
		myTrackXML.addAttribute(GROUP_NAME_ATTRIBUTE, address());
		myTrackXML.addAttribute(GROUP_OPEN_ATTRIBUTE, isOpen());
			
		myTrackXML.addAttribute(COLOR_RED_ATTRIBUTE, color().getRed());
		myTrackXML.addAttribute(COLOR_GREEN_ATTRIBUTE, color().getGreen());
		myTrackXML.addAttribute(COLOR_BLUE_ATTRIBUTE, color().getBlue());
			
		for(Track myTrack:tracks()) {
			myTrackXML.addChild(myTrack.toXML(theStart, theEnd));
		}
			
		return myTrackXML;
	}
	
	public void fromXML(CCXMLElement theGroupTrackXML) {
		setAddress(theGroupTrackXML.attribute(GROUP_NAME_ATTRIBUTE));
//		isOpen(theGroupTrackXML.booleanAttribute(GROUP_OPEN_ATTRIBUTE, true));
		
		if(theGroupTrackXML.hasAttribute(COLOR_RED_ATTRIBUTE)) {
			Color myColor = new Color(
				theGroupTrackXML.intAttribute(COLOR_RED_ATTRIBUTE),
				theGroupTrackXML.intAttribute(COLOR_GREEN_ATTRIBUTE),
				theGroupTrackXML.intAttribute(COLOR_BLUE_ATTRIBUTE)
			);
			color(myColor);
		}
		
		for(CCXMLElement myTrackXML:theGroupTrackXML) {
			Track myTrack = null;
			String theAddress = myTrackXML.attribute(Track.ADDRESS_ATTRIBUTE);
			for(Track myCheckTrack:_myTracks) {
				if(myCheckTrack.address().equals(theAddress)) {
					myTrack = myCheckTrack;
					break;
				}
			}
			if(myTrack == null)myTrack = new Track();
			myTrack.fromXML(myTrackXML);
			addTrack(myTrack);
		}
	}
}
