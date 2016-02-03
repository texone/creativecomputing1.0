package cc.creativecomputing.timeline.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.timeline.model.AbstractTrack;
import cc.creativecomputing.timeline.model.GroupTrack;
import cc.creativecomputing.timeline.model.Track;
import cc.creativecomputing.timeline.model.TrackData;
import cc.creativecomputing.timeline.model.UndoHistory;
import cc.creativecomputing.util.logging.CCLog;
import cc.creativecomputing.xml.CCXMLElement;
import cc.creativecomputing.xml.CCXMLIO;


public class FileManager {
	
	public static interface FileManagerListener{
		public void onLoad(File theFile);

		public void onSave(File theFile);

		public void onNew(File theFile);
		
	}
	
	private class XMLSerializer{
	
		private static final String TIMELINE_ELEMENT = "Timeline";
		private static final String TRACKS_ELEMENT = "Tracks";
		
		private static final String TRANSPORT_ELEMENT = "Transport";
		private static final String PLAYBACK_SPEED_ATTRIBUTE = "speed";
		private static final String LOOP_START_ATTRIBUTE = "loop_start";
		private static final String LOOP_END_ATTRIBUTE = "loop_end";
		private static final String LOOP_ACTIVE_ATTRIBUTE = "loop_active";
		
		private static final String LOWER_BOUND_ATTRIBUTE = "lower_bound";
		private static final String UPPER_BOUND_ATTRIBUTE = "upper_bound";
		
		public XMLSerializer() {
		}
		
		////////////////////////////////////
		//
		// LOADING
		//
		////////////////////////////////////
		private void loadTransport(final CCXMLElement theTransportXML, final TransportController theTransportController) {
			
			if (theTransportXML.hasAttribute(PLAYBACK_SPEED_ATTRIBUTE)) {
				theTransportController.speed(
					theTransportXML.doubleAttribute(PLAYBACK_SPEED_ATTRIBUTE)
				);
			}
			
			if (theTransportXML.hasAttribute(LOOP_START_ATTRIBUTE) && theTransportXML.hasAttribute(LOOP_END_ATTRIBUTE)) {
				theTransportController.loop(
					theTransportXML.doubleAttribute(LOOP_START_ATTRIBUTE),
					theTransportXML.doubleAttribute(LOOP_END_ATTRIBUTE)
				);
			}
			
			if (theTransportXML.hasAttribute(LOOP_ACTIVE_ATTRIBUTE)) {
				theTransportController.doLoop(theTransportXML.booleanAttribute(LOOP_ACTIVE_ATTRIBUTE));
			}
			
			CCXMLElement myTrackDataXML = theTransportXML.child(TrackData.TRACKDATA_ELEMENT);
			if(myTrackDataXML != null) {
				TrackData myTrackData = new TrackData(null);
				myTrackData.fromXML(myTrackDataXML);
				theTransportController.trackData(myTrackData);
			}
			
		}
		
		private List<AbstractTrack> loadTracks(CCXMLElement theTimelineXML){
			CCXMLElement myTracksXML = theTimelineXML.child(TRACKS_ELEMENT);
			ArrayList<AbstractTrack> myTracks = new ArrayList<AbstractTrack>();
			for (CCXMLElement myTrackXML:myTracksXML) {
				if(myTrackXML.name().equals(GroupTrack.GROUP_TRACK_ELEMENT)) {
					GroupTrack myGroupTrack = new GroupTrack();
					myGroupTrack.fromXML(myTrackXML);
					myTracks.add(myGroupTrack);
				}else {
					Track myTrack = new Track();
					myTrack.fromXML(myTrackXML);
					myTracks.add(myTrack);
				}
			}
			return myTracks;
		}
		
		private void reloadTracks(TimelineController theTimeline, CCXMLElement theTimelineXML){
			CCXMLElement myTracksXML = theTimelineXML.child(TRACKS_ELEMENT);
			for (CCXMLElement myTrackXML:myTracksXML) {
				if(myTrackXML.name().equals(GroupTrack.GROUP_TRACK_ELEMENT)) {
					String myAddress = myTrackXML.attribute(GroupTrack.GROUP_NAME_ATTRIBUTE);
					GroupTrackController myGroupTrackController = theTimeline.group(myAddress);
					if(myGroupTrackController != null) {
						myGroupTrackController.groupTrack().fromXML(myTrackXML);
					}
				}else {
					String myAddress = myTrackXML.attribute(Track.ADDRESS_ATTRIBUTE);
					TrackController myTrackController = theTimeline.track(myAddress);
					if(myTrackController != null) {
						myTrackController.track().fromXML(myTrackXML);
					}
				}
			}
			theTimeline.render();
		}
		
		public List<AbstractTrack> loadFile(File theFile, TimelineController theTimelineController) {
			CCXMLElement myTimelineXML = CCXMLIO.createXMLElement(theFile.getAbsolutePath());
			if(myTimelineXML == null)throw new RuntimeException("the given timelinedocument:" + theFile.getAbsolutePath() +" does not exist");
			CCXMLElement myTransportXML = myTimelineXML.child(TRANSPORT_ELEMENT);
			loadTransport(myTransportXML, theTimelineController.transportController());
				
				
			if (myTimelineXML.hasAttribute(LOWER_BOUND_ATTRIBUTE)) {
				theTimelineController.zoomController().setLowerBound(myTimelineXML.doubleAttribute(LOWER_BOUND_ATTRIBUTE));
			}
			
			if (myTimelineXML.hasAttribute(UPPER_BOUND_ATTRIBUTE)) {
				theTimelineController.zoomController().setUpperBound(myTimelineXML.doubleAttribute(UPPER_BOUND_ATTRIBUTE));
			}
				
			return loadTracks(myTimelineXML);
		}
		
		public void reloadFile(File theFile, TimelineController theTimelineController) {
			CCXMLElement myTimelineXML = CCXMLIO.createXMLElement(theFile.getAbsolutePath());
			if(myTimelineXML == null)throw new RuntimeException("the given timelinedocument:" + theFile.getAbsolutePath() +" does not exist");
			CCXMLElement myTransportXML = myTimelineXML.child(TRANSPORT_ELEMENT);
			loadTransport(myTransportXML, theTimelineController.transportController());
				
				
			if (myTimelineXML.hasAttribute(LOWER_BOUND_ATTRIBUTE)) {
				theTimelineController.zoomController().setLowerBound(myTimelineXML.doubleAttribute(LOWER_BOUND_ATTRIBUTE));
			}
			
			if (myTimelineXML.hasAttribute(UPPER_BOUND_ATTRIBUTE)) {
				theTimelineController.zoomController().setUpperBound(myTimelineXML.doubleAttribute(UPPER_BOUND_ATTRIBUTE));
			}
				
			reloadTracks(theTimelineController, myTimelineXML);
		}
		
		public List<AbstractTrack> insertTracks(File theFile, TimelineController theTimelineController) {
			try {
				CCXMLElement myTimelineXML = CCXMLIO.createXMLElement(theFile.getAbsolutePath());
				
				CCXMLElement myTransportXML = myTimelineXML.child(TRANSPORT_ELEMENT);
				loadTransport(myTransportXML, theTimelineController.transportController());
				
				CCXMLElement myMarkerDataXML = myTransportXML.child(TrackData.TRACKDATA_ELEMENT);
				TrackData myMarkerData = new TrackData(null);
				if(myMarkerDataXML != null){
					myMarkerData.fromXML(myMarkerDataXML);
				}
				
				List<AbstractTrack> myTracks = loadTracks(myTimelineXML);

				_myTimelineController.insertTracks(myTracks, myMarkerData);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		////////////////////////////////////
		//
		// SAVING
		//
		////////////////////////////////////
		
		private CCXMLElement createTransportXML(TransportController theTransportController, double theStart, double theEnd) {
			CCXMLElement myTransportXML = new CCXMLElement(TRANSPORT_ELEMENT);
			myTransportXML.addAttribute(PLAYBACK_SPEED_ATTRIBUTE, theTransportController.speed());
			myTransportXML.addAttribute(LOOP_START_ATTRIBUTE, theTransportController.loopStart());
			myTransportXML.addAttribute(LOOP_END_ATTRIBUTE, theTransportController.loopEnd());
			myTransportXML.addAttribute(LOOP_ACTIVE_ATTRIBUTE, theTransportController.doLoop());
			
			CCXMLElement myMarkerXML = theTransportController.trackData().toXML(theStart, theEnd);
			myTransportXML.addChild(myMarkerXML);
			return myTransportXML;
		}
		
		private CCXMLElement createTimelineXML(TimelineController theTimelineController, boolean theSaveSelection) {
			CCXMLElement myTimelineXML = new CCXMLElement(TIMELINE_ELEMENT);
			myTimelineXML.addAttribute(LOWER_BOUND_ATTRIBUTE, theTimelineController.zoomController().lowerBound());
			myTimelineXML.addAttribute(UPPER_BOUND_ATTRIBUTE, theTimelineController.zoomController().upperBound());
			
			double myStart = 0;
			double myEnd = theTimelineController.maximumTime();
			
			if(theSaveSelection){
				myStart = theTimelineController.transportController().loopStart();
				myEnd = theTimelineController.transportController().loopEnd();
			}
			
			myTimelineXML.addChild(createTransportXML(theTimelineController.transportController(), myStart, myEnd));
			
			CCXMLElement myTracksXML = new CCXMLElement(TRACKS_ELEMENT);
			myTimelineXML.addChild(myTracksXML);
			
			for (AbstractTrack myObject : theTimelineController.tracks()) {
				if(myObject instanceof Track) {
					myTracksXML.addChild(((Track)myObject).toXML(myStart, myEnd));
				}else if(myObject instanceof GroupTrack) {
					myTracksXML.addChild(((GroupTrack)myObject).toXML(myStart, myEnd));
				}
			}
			
			return myTimelineXML;
		}
		
		public void saveFile(File theFile, TimelineController theTimelineController) {
			CCXMLElement myTimelineXML = createTimelineXML(theTimelineController, false);
			CCXMLIO.saveXMLElement(myTimelineXML, theFile.getAbsolutePath());
		}
		

		
		public void saveSelection(File theFile, TimelineController theTimelineController) {
			CCXMLElement myTimelineXML = createTimelineXML(theTimelineController, true);
			CCXMLIO.saveXMLElement(myTimelineXML, theFile.getAbsolutePath());
		}
	}
	
	private XMLSerializer _mySerializer;
	
	private TimelineController _myTimelineController;
	
	private CCListenerManager<FileManagerListener> _myEvents = CCListenerManager.create(FileManagerListener.class);
	
	private String _myExtension = "xml";
	private String _myDescription = "XML File";
	
	public FileManager(TimelineController theTimelineController) {
		_mySerializer = new XMLSerializer();
		_myTimelineController = theTimelineController;
	}
	
	public void extension(String theExtension, String theDescription) {
		_myExtension = theExtension;
		_myDescription = theDescription;
	}
	
	public String extension() {
		return _myExtension;
	}
	
	public String description() {
		return _myDescription;
	}
	
	public CCListenerManager<FileManagerListener> events() {
		return _myEvents;
	}
	
	public void loadFile(File theFile) {
		List<AbstractTrack> myTracks = _mySerializer.loadFile(theFile, _myTimelineController);
		_myTimelineController.reloadTracks(myTracks);
		_myEvents.proxy().onLoad(theFile);
		UndoHistory.instance().clear();
	}
	
	public void reloadExistingTracks(File theFile) {
		_mySerializer.reloadFile(theFile, _myTimelineController);
		_myEvents.proxy().onLoad(theFile);
		UndoHistory.instance().clear();
	}
	
	public void insertAtTime(File theFile) {
		_mySerializer.insertTracks(theFile, _myTimelineController);
		UndoHistory.instance().clear();
	}
	
	public void newFile() {
		_myTimelineController.resetTracks();
		UndoHistory.instance().clear();
		_myEvents.proxy().onNew(new File("New File"));
	}
	
	public void save(File theFile) {
		_mySerializer.saveFile(theFile, _myTimelineController);
		UndoHistory.instance().clear();
		_myEvents.proxy().onSave(theFile);
	}
	
	public void saveSelection(File theFile){
		_mySerializer.saveSelection(theFile, _myTimelineController);
		UndoHistory.instance().clear();
		_myEvents.proxy().onSave(theFile);
	}

}
