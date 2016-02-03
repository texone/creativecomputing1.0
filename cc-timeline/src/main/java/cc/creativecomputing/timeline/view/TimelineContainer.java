package cc.creativecomputing.timeline.view;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.timeline.CCTimedEventListener;
import cc.creativecomputing.timeline.controller.CurveTrackController;
import cc.creativecomputing.timeline.controller.EventTrackController;
import cc.creativecomputing.timeline.controller.FileManager;
import cc.creativecomputing.timeline.controller.FileManager.FileManagerListener;
import cc.creativecomputing.timeline.controller.GroupTrackController;
import cc.creativecomputing.timeline.controller.TimelineController;
import cc.creativecomputing.timeline.controller.TransportStateListener;
import cc.creativecomputing.timeline.controller.TransportTimeListener;
import cc.creativecomputing.timeline.model.GroupTrack;
import cc.creativecomputing.timeline.model.Track;
import cc.creativecomputing.timeline.model.TrackType;
import cc.creativecomputing.timeline.model.UndoHistory;
import cc.creativecomputing.timeline.model.UndoHistory.HistoryListener;
import cc.creativecomputing.timeline.model.communication.MarkerListener;
import cc.creativecomputing.timeline.model.communication.TimelineListener;


public class TimelineContainer implements FileManagerListener, HistoryListener{

	private Map<String, Color> _myColorMap = new HashMap<String, Color>();
	
	protected TimelineController _myTimelineController;
	protected FileManager _myFileManager;

	public TimelineContainer(){
		_myTimelineController = new TimelineController();
		_myFileManager = new FileManager(_myTimelineController);
		_myFileManager.events().add(this);
		
		UndoHistory.instance().events().add(this);
	}
	
	public TimelineController timelineController() {
		return _myTimelineController;
	}
	
	public void extension(String theExtension, String theDescription) {
		_myFileManager.extension(theExtension, theDescription);
	}
	
	public void loadFile(File theFile){
		_myFileManager.loadFile(theFile);
	}
	
	public void loadFile(String theFile){
		_myFileManager.loadFile(new File(theFile));
	}
	
	public void reloadFile(String theFile){
		_myFileManager.reloadExistingTracks(new File(theFile));
	}
	
	public void addFileManagerListener(FileManagerListener theListener) {
		_myFileManager.events().add(theListener);
	}
	
	public void setSize(int theWidth, int theHeight){
	}
	
	public void play(){
		_myTimelineController.transportController().play();
	}
	
	public void stop(){
		_myTimelineController.transportController().stop();
	}
	
	public void update(double theDeltaTime){
		_myTimelineController.transportController().update(theDeltaTime);
	}
	
	public double loopStart(){
		return _myTimelineController.transportController().loopStart();
	}
	
	public double loopEnd(){
		return _myTimelineController.transportController().loopEnd();
	}
	
	public void loop(double theStartTime, double theEndTime){
		 _myTimelineController.transportController().loop(theStartTime, theEndTime);
	}
	
	public void raster(int theRaster) {
		_myTimelineController.raster(theRaster);
	}
	
	public void time(final double theTime){
		_myTimelineController.transportController().time(theTime);
	}
	
	public double time(){
		return _myTimelineController.transportController().time();
	}
	
	public void minZoomRange(double theMinZoomRange) {
		_myTimelineController.zoomController().minRange(theMinZoomRange);
	}
	
	public void maxZoomRange(double theMaxZoomRange) {
		_myTimelineController.zoomController().maxRange(theMaxZoomRange);
	}
	
	public void viewRange(double theStart, double theEnd) {
		_myTimelineController.zoomController().setRange(theStart, theEnd);
	}
	
	public void doLoop(final boolean theDoLoop){
		_myTimelineController.transportController().doLoop(theDoLoop);
	}
	
	public double speed(){
		return _myTimelineController.transportController().speed();
	}
	
	public void speed(double theSpeed){
		_myTimelineController.transportController().speed(theSpeed);
	}
	
	public void addMarker(String theName, double theTime){
		_myTimelineController.transportController().addMarker(theName, theTime);
	}
	
	public void addMarkerListener(MarkerListener theListener){
		_myTimelineController.transportController().addMarkerListener(theListener);
	}
	
	public void addTransportTimeListener(TransportTimeListener theListener){
		_myTimelineController.transportController().addTimeListener(theListener);
	}
	
	public void addTransportStateListener(TransportStateListener theStateListener){
		_myTimelineController.transportController().addStateListener(theStateListener);
	}
	
	public void addTimelineListener(TimelineListener theTimelineListener) {
		_myTimelineController.addListener(theTimelineListener);
	}
	
	private float _myHue = 0;
	
	private Color getColor(String theGroup) {
		if(!_myColorMap.containsKey(theGroup)) {
			_myColorMap.put(theGroup, Color.getHSBColor(_myHue, 0.75f, 0.75f));
			_myHue += 0.1f;
		}
		return _myColorMap.get(theGroup);
	}
	
	public GroupTrackController addGroupTrack(String theGroup) {
		GroupTrackController myResult = _myTimelineController.group(theGroup);
		if(myResult == null) {
			GroupTrack myGroupTrack = new GroupTrack(theGroup);
			Map<String, String> myExtraMap = new HashMap<>();
			myExtraMap.put(TimelineContainer.EVENT_TYPES,"new");
			myGroupTrack.extras(myExtraMap);
			myGroupTrack.color(getColor(theGroup));
			myGroupTrack.setAddress(theGroup);
			return _myTimelineController.addGroupTrack(myGroupTrack);
		}
		return myResult;
	}
	
	private Track createTrack(String theGroup, String theName, TrackType theTrackType, float theMin, float theMax) {
		String myKey;
		
		if(theGroup != null && !theGroup.equals("")) {
			myKey= theGroup + "/" + theName;
			addGroupTrack(theGroup);
		}else {
			myKey= "/" + theName;
		}
		Track myTrack = new Track(myKey);
		myTrack.color(getColor(theGroup));
		myTrack.trackType(theTrackType);
		myTrack.valueRange(theMin, theMax);
		return myTrack;
	}
	
	public CurveTrackController addCurveTrack(
		String theGroup, String theName, 
		TrackType theTrackType, 
		float theMin, float theMax
	) {
		Track myTrack = createTrack(theGroup, theName, theTrackType, theMin, theMax);
		return _myTimelineController.addCurveTrack(myTrack, theGroup);
	}
	
	public static final String EVENT_TYPES = "eventTypes";
	
	public EventTrackController addTimedEventTrack(
		String theGroup, String theName, 
		CCTimedEventListener theListener,
		List<String> theEventTypes
	){
		Track myTrack = createTrack(theGroup, theName, TrackType.TIME, 0, 1);
		StringBuffer myList = new StringBuffer();
		for(String myType:theEventTypes) {
			myList.append(myType);
			myList.append(",");
		}
		myList.deleteCharAt(myList.length()-1);
		myTrack.addExtra(EVENT_TYPES, myList.toString());
		return _myTimelineController.addTimedEventTrack(myTrack, theGroup);
	}

//	public EventTrackController addTimedEventTrack(
//		String theName, 
//		CCTimedEventListener theListener
//	){
//		return addTimedEventTrack(null, theName, theListener, EnumSet.allOf(theEnumClass));
//	}
	
	@Override
	public void onLoad(File theFile) {
		
	}
	
	@Override
	public void onChange(UndoHistory theHistory) {
		
	} 
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.timeline.controller.FileManager.FileManagerListener#onSave(java.io.File)
	 */
	@Override
	public void onSave(File theFile) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.timeline.controller.FileManager.FileManagerListener#onNew(java.io.File)
	 */
	@Override
	public void onNew(File theFile) {
		// TODO Auto-generated method stub
		
	}
}
