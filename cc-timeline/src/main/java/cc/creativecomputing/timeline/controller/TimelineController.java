/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.timeline.controller;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.timeline.controller.tools.ToolController;
import cc.creativecomputing.timeline.model.AbstractTrack;
import cc.creativecomputing.timeline.model.GroupTrack;
import cc.creativecomputing.timeline.model.TimeRange;
import cc.creativecomputing.timeline.model.Track;
import cc.creativecomputing.timeline.model.TrackData;
import cc.creativecomputing.timeline.model.communication.CurveEvent;
import cc.creativecomputing.timeline.model.communication.TimedEvent;
import cc.creativecomputing.timeline.model.communication.TimelineListener;
import cc.creativecomputing.timeline.model.points.ControlPoint;
import cc.creativecomputing.timeline.model.points.TimedEventPoint;
import cc.creativecomputing.timeline.view.GroupTrackView;
import cc.creativecomputing.timeline.view.TimelineView;
import cc.creativecomputing.timeline.view.TrackView;
import cc.creativecomputing.util.logging.CCLog;


/**
 * @author christianriekoff
 * 
 */
public class TimelineController extends TrackContext implements TransportTimeListener{

	private TransportController _myTransportController;

	private List<TimelineListener> _myTimelineListener = new ArrayList<TimelineListener>();
	
	private Map<String, GroupTrackController> _myGroupControllerTrackMap = new HashMap<String, GroupTrackController>();
	private List<String> _myGroupOrder = new ArrayList<>();
	private TimelineView _myView;
	
	private List<Object> _myObjectsToSave = new ArrayList<Object>();
	private Map<String, TrackController> _myTrackControllerMap;
	private List<EventTrackController> _myEventTrackController;
	private List<CurveTrackController> _myCurveTrackController;
	
	public TimelineController() {
		super();
		_myTransportController = new TransportController(this);
		_myTransportController.addTimeListener(this);
		_myTrackControllerMap = new HashMap<String, TrackController>();
		_myEventTrackController = new ArrayList<EventTrackController>();
		_myCurveTrackController = new ArrayList<CurveTrackController>();
	}
	
	public void addListener(TimelineListener theTimelineListener) {
		_myTimelineListener.add(theTimelineListener);
	}
	
	public void removeListener(TimelineListener theTimelineListener) {
		_myTimelineListener.remove(theTimelineListener);
	}
	
	public void view(TimelineView theView) {
		_myView = theView;
		_myTransportController.rulerView(_myView.transportRulerView());
		_myZoomController.addZoomable(_myTransportController);
	}
	
	public TimelineView view() {
		return _myView;
	}
	
	public CCZoomController zoomController() {
		return _myZoomController;
	}
	
	public TransportController transportController() {
		return _myTransportController;
	}
	
	public ToolController toolController() {
		return _myToolController;
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.timeline.controller.TrackContext#snapToRaster(cc.creativecomputing.timeline.model.points.ControlPoint)
	 */
	@Override
	public ControlPoint snapToRaster(ControlPoint thePoint) {
		if(_myRaster == 0) {
    		return thePoint;
    	}
        double myTime = _myTransportController.rulerInterval().quantize(thePoint.time(), _myRaster);
        thePoint.time(myTime);
        return thePoint;
	}
	
	public double snapToRaster(double theTime) {
		if(_myRaster == 0) {
    		return theTime;
    	}
        return _myTransportController.rulerInterval().quantize(theTime, _myRaster);
	}
	
	/**
	 * Sets the zoom range based on the selected range
	 */
	public void zoomToSelection() {
		// TODO check zoom to selection
		_myZoomController.setRange(_myToolController.selectionController().range());
	}
	
	/**
	 * Finds the last point in all tracks and zoom the time to be between
	 * 0 and the time of that point.
	 */
	public double maximumTime() {
		double myMaxValue = 0;
		for (TrackController myTrackController : _myTrackControllerMap.values()) {
			ControlPoint myLastPoint = myTrackController.trackData().getLastPoint();
			if(myLastPoint == null)continue;
			
			double myCurrentValue;
			if(myLastPoint instanceof TimedEventPoint) {
				myCurrentValue = ((TimedEventPoint)myLastPoint).endTime();
			}else {
				myCurrentValue = myLastPoint.time();
			}
			if (myCurrentValue > myMaxValue) {
				myMaxValue = myCurrentValue;
			}
		}
		return myMaxValue;
	}
	
	/**
	 * Finds the last point in all tracks and zoom the time to be between
	 * 0 and the time of that point.
	 */
	public void zoomToMaximum() {
		_myZoomController.setRange(new TimeRange(0, maximumTime()));
	}
	
	public List<AbstractTrack> tracks(){
		List<AbstractTrack> myResult = new ArrayList<AbstractTrack>();
		for(Object myTrackController:_myObjectsToSave) {
			if(myTrackController instanceof TrackController) {
				myResult.add(((TrackController)myTrackController).track());
			}
			if(myTrackController instanceof GroupTrackController) {
				myResult.add(((GroupTrackController)myTrackController).groupTrack());
			}
		}
		return myResult;
	}
	
	public void insertTime(){
		double myLowerBound = _myTransportController.loopStart();
		double myUpperBound = _myTransportController.loopEnd();
		double myRange = myUpperBound - myLowerBound;
		_myTransportController.trackData().insertTime(myLowerBound, myRange);
		for (TrackController myController : _myTrackControllerMap.values()) {
			myController.trackData().insertTime(myLowerBound, myRange);
			myController.view().render();
		}
	}
	
	public void insertTime(double theInsertTime, double theTime){
		for (TrackController myController : _myTrackControllerMap.values()) {
			myController.trackData().insertTime(theInsertTime, theTime);
			myController.view().render();
		}
	}
	
	public void removeTime(){
		double myLowerBound = _myTransportController.loopStart();
		double myUpperBound = _myTransportController.loopEnd();
		double myRange = myUpperBound - myLowerBound;

		_myTransportController.trackData().cutRangeAndTime(myLowerBound, myRange);
		for (TrackController myController : _myTrackControllerMap.values()) {
			myController.trackData().cutRangeAndTime(myLowerBound, myRange);
			myController.view().render();
		}
	}
	
	public void colorTrack(final Color theColor, final String theAddress) {
		TrackController myTrackController = _myTrackControllerMap.get(theAddress);
		if(myTrackController != null) {
			myTrackController.track().color(theColor);
			myTrackController.view().color(theColor);
		}
	}
	
	public void scaleTracks(double theOldLoopStart, double theOldLoopEnd, double theNewLoopStart, double theNewLoopEnd) {
		//TODO CHECK THIS
//		for(TrackController myController : _myTrackControllerMap.values()) {
//			myController.trackData().scaleRange(theOldLoopStart, theOldLoopEnd, theNewLoopStart, theNewLoopEnd);
//			myController.view().render();
//		}
	}
	
	public GroupTrackController group(String theGroupName) {
		return _myGroupControllerTrackMap.get(theGroupName);
	}
	
	public TrackController track(String theTrackName) {
		return _myTrackControllerMap.get(theTrackName);
	}
	
	public GroupTrackController addGroupTrack(GroupTrack theGroupTrack) {
		theGroupTrack.isOpen(true);
		GroupTrackController myGroupController = new GroupTrackController(this, _myToolController, theGroupTrack);

		_myGroupControllerTrackMap.put(theGroupTrack.address(), myGroupController);
		_myGroupOrder.add(theGroupTrack.address());
		_myObjectsToSave.add(myGroupController);
		
		openGroups();
		
		if(_myView != null){
			GroupTrackView myGroupView = _myView.addGroupTrack(_myTrackCount, myGroupController);
			myGroupView.color(theGroupTrack.color());
			myGroupView.address(theGroupTrack.address());
			
			myGroupController.view(myGroupView);
		}
		_myTrackCount++;
		return myGroupController;
	}
	
	private void addTrack(TrackController theTrackController, Track theTrack, String theGroup) {
		
		if(_myView != null){
			TrackView myTrackView = _myView.addTrack(_myTrackCount, theTrackController);
			theTrackController.view(myTrackView);
			theTrackController.view().address(theTrack.address().substring(theTrack.address().lastIndexOf("/") + 1));
			theTrackController.view().color(theTrack.color());
		}
		theTrackController.mute(theTrack.mute());
		
		_myZoomController.addZoomable(theTrackController);
		
		if(theGroup != null && _myGroupControllerTrackMap.containsKey(theGroup)) {
			_myGroupControllerTrackMap.get(theGroup).addTrack(theTrackController);
		}else {
			_myObjectsToSave.add(_myTrackControllerMap.size() - 1, theTrackController);
		}
	}
	
	/**
	 * Gets the Track Model and creates all needed Views and adds them to the needed listeners
	 * @param theIndex
	 * @param theTrack
	 */
	public CurveTrackController addCurveTrack(Track theTrack, String theGroup) {

		GroupTrackController myGroup = _myGroupControllerTrackMap.get(theGroup);
		CurveTrackController myTrackController = new CurveTrackController(this, _myCurveToolController, theTrack, myGroup);
		
		_myTrackControllerMap.put(theTrack.address(), myTrackController);
		_myCurveTrackController.add(myTrackController);
		addTrack(myTrackController, theTrack, theGroup);
		
		_myTrackCount++;
		
		return myTrackController;
	}
	
	public EventTrackController addTimedEventTrack(Track theTrack, String theGroup) {
		GroupTrackController myGroup = _myGroupControllerTrackMap.get(theGroup);
		EventTrackController myTrackController = new EventTrackController(this, _myToolController, theTrack, myGroup);

		_myTrackControllerMap.put(theTrack.address(), myTrackController);
		_myEventTrackController.add(myTrackController);
		addTrack(myTrackController, theTrack, theGroup);
		
		_myTrackCount++;
		
		return myTrackController;
	}
	
	private void removeTrack(Track theTrack) {
		TrackController myTrackController = _myTrackControllerMap.remove(theTrack.address());
		_myObjectsToSave.remove(myTrackController);
		_myZoomController.removeZoomable(myTrackController);
		
		if(_myView != null)_myView.removeTrack(theTrack.address());
	}
	
	private void removeGroupTrack(GroupTrack theGroupTrack) {
		_myGroupOrder.remove(theGroupTrack.address());
		GroupTrackController myGroupController = _myGroupControllerTrackMap.remove(theGroupTrack.address());
		_myObjectsToSave.remove(myGroupController);
		
		for(TrackController myTrackController:myGroupController.trackController()) {
			_myZoomController.removeZoomable(myTrackController);
			if(_myView != null)_myView.removeTrack(myTrackController.track().address());
		}
		
		if(_myView != null)_myView.removeTrack(theGroupTrack.address());
	}
	
	/**
	 * Removes all current tracks and creates new ones according to the loaded file
	 */
	public void reloadTracks(List<AbstractTrack> theTracks) {
		if(theTracks == null)return;
		
		for(TrackController myTrackController:new ArrayList<TrackController>(_myTrackControllerMap.values())) {
			removeTrack(myTrackController.track());
		}
		for(GroupTrackController myTrackController:new ArrayList<GroupTrackController>(_myGroupControllerTrackMap.values())) {
			removeGroupTrack(myTrackController.groupTrack());
		}
		System.gc();
		
		_myTrackCount = 0;
		for (AbstractTrack myTrack : theTracks) {
			switch (myTrack.trackType()) {
			case GROUP:
				for (Track myTrack1 : ((GroupTrack) myTrack).tracks()) {
					addCurveTrack(myTrack1, ((GroupTrack) myTrack).address());
				}
				break;
			case TIME:
				addTimedEventTrack((Track) myTrack, null);
				break;
			default:
				addCurveTrack((Track) myTrack, null);
			}
		}
		
		for(GroupTrackController myGroupController:_myGroupControllerTrackMap.values()){
			myGroupController.closeGroup();
		}
	}
	
	@Override
	public void render(){
		for (TrackController myController : _myTrackControllerMap.values()) {
			if(myController.view() != null)myController.view().render();
		}
	}
	
	private void reloadDataTrack(Track theTrack) {
		TrackController myTrackController = _myTrackControllerMap.get(theTrack.address());
		if(myTrackController != null) {
			myTrackController.trackData(theTrack);
		}
	}

	/**
	 * Keeps all tracks and only loads the data for the existing ones.
	 */
	public void reloadDataTracks(List<AbstractTrack> theTracks) {
		if(theTracks == null)return;
		
		for (AbstractTrack myAbstractTrack : theTracks) {

			if(myAbstractTrack instanceof GroupTrack) {
				GroupTrack myGroupTrack = (GroupTrack)myAbstractTrack;
				for(Track myTrack:myGroupTrack.tracks()) {
					reloadDataTrack(myTrack);
				}
			}else if(myAbstractTrack instanceof Track) {
				reloadDataTrack((Track)myAbstractTrack);
			}
			
		}
	}
	
	@SuppressWarnings("unused")
	private void insertDataTrack(Track theTrack, double theRange) {
		TrackController myTrackController = _myTrackControllerMap.get(theTrack.address());
		if(myTrackController != null) {
			myTrackController.trackData().insertAll(_myTransportController.time(), theRange, theTrack.trackData().rangeList(0, theRange));
		}
	}
	
	private double checkMaxTime(double theMaxTime, TrackData theTrackData){
		double myLastTime = theTrackData.getLastTime();
		if (myLastTime > theMaxTime) {
			return myLastTime;
		}
		return theMaxTime;
	}
	
	private double checkMaxTime(double theMaxTime, Track theTrack){
		return checkMaxTime(theMaxTime, theTrack.trackData());
	}
	
	private void insertTrackData(double theInsertTime, double theMaxTime, Track theTrack){
		TrackController myController = _myTrackControllerMap.get(theTrack.address());
		if(myController == null){
			CCLog.info("INSERT TIME:" + theTrack.address());
			return;
		}
		myController.trackData().insertAll(theInsertTime, theMaxTime, theTrack.trackData().rangeList(0));
	}
	
	public void insertTracks(List<AbstractTrack> theTracks, TrackData theMarkerTrack){
		if(theTracks == null)return;
		
		double myMaxTime = 0;
		for (AbstractTrack myAbstractTrack : theTracks) {
			if(myAbstractTrack instanceof Track) {
				myMaxTime = checkMaxTime(myMaxTime, (Track)myAbstractTrack);
			}else if(myAbstractTrack instanceof GroupTrack) {
				for(Track myTrack:((GroupTrack)myAbstractTrack).tracks()) {
					myMaxTime = checkMaxTime(myMaxTime, myTrack);
				}
			}
		}
		myMaxTime = checkMaxTime(myMaxTime, theMarkerTrack);
		
		double myInsertTime = _myTransportController.time();
		
		
		for (AbstractTrack myAbstractTrack : theTracks) {
			CCLog.info(myAbstractTrack);
			if(myAbstractTrack instanceof GroupTrack) {
				for(Track myTrack:((GroupTrack)myAbstractTrack).tracks()) {
					CCLog.info(myTrack.address());
					insertTrackData(myInsertTime, myMaxTime, myTrack);
				}
			}else if(myAbstractTrack instanceof Track) {
				Track myTrack = (Track)myAbstractTrack;
				insertTrackData(myInsertTime, myMaxTime, myTrack);
			}
		}
		
		_myTransportController.trackData().insertAll(myInsertTime, myMaxTime, theMarkerTrack.rangeList(0));
	}
	
	public void resetTracks() {
		for(TrackController myTrackController:_myTrackControllerMap.values()) {
			myTrackController.reset();
		}
	}
	
	public void closeGroups(){
		for(String myAddress:_myGroupOrder){
			CCLog.info(myAddress);
			GroupTrackController myGroupTrackController = _myGroupControllerTrackMap.get(myAddress);
			myGroupTrackController.closeGroup();
		}
	}
	
	public void openGroups(){
		for(GroupTrackController myGroupTrackController:_myGroupControllerTrackMap.values()){
			myGroupTrackController.openGroup();
		}
	}
	
	public void reverseTracks(){
		double myMaximumTime = maximumTime();
		for(TrackController myTrackController:_myTrackControllerMap.values()){
			myTrackController.track().trackData().reverse(0, myMaximumTime);
		}
		render();
	}
	
	public void hideUnusedTracks(boolean theHideUnusedTracks){
		if(theHideUnusedTracks)_myView.hideUnusedTracks();
		else _myView.showUnusedTracks();
	}
	
	private int _myTrackCount = 0;
	
	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	/**
	 * @param theIsMuted
	 */
	public void muteAll(boolean theIsMuted) {
		for(TrackController myTrackController:_myTrackControllerMap.values()) {
			myTrackController.mute(theIsMuted);
		}
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.timeline.controller.TransportTimeListener#time(double)
	 */
	@Override
	public void time(double theTime) {
		for(CurveTrackController myController:_myCurveTrackController) {
			if(myController.view() != null)myController.view().update();
			
			double myValue = myController.value(theTime);
//			if(myValue != _myLastValue){
				myController.viewValue(myValue);
				if(myController.trackData().size() == 0 && myController.view() != null)myController.view().render();
//			}

				
			if(myController.track().mute()) continue;
	    	if(myController.trackData().size() == 0)continue;

	    	for (TimelineListener myListener : _myTimelineListener) {
				CurveEvent myEvent = new CurveEvent(
					theTime, 
					myController.trackData().getAccumulatedValue(theTime), 
					myController.track().address(), myController.trackType()
				);
				myListener.onCurveEvent(myEvent);
			}
		}
		
		for(EventTrackController myController:_myEventTrackController) {
			if(myController.view() != null)myController.view().update();
			
			double myValue = myController.value(theTime);
			myController.viewValue(myValue);
			if(myController.trackData().size() == 0 && myController.view() != null)myController.view().render();
	
			if(myController.track().mute()) continue;
	    	if(myController.trackData().size() == 0)continue;
	    	
	    	TimedEventPoint myEventPoint = myController.pointAt(theTime);
	    	if(myEventPoint == null)continue;

	    	for (TimelineListener myListener : _myTimelineListener) {
				TimedEvent myEvent = new TimedEvent(
					myEventPoint,
					theTime, 
					myController.track().address(), 
					myController.trackType()
				);
				myListener.onTimedEvent(myEvent);
			}
		}
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.timeline.controller.TransportTimeListener#onChangeLoop(cc.creativecomputing.timeline.model.TimeRange, boolean)
	 */
	@Override
	public void onChangeLoop(TimeRange theRange, boolean theLoopIsActive) {
	}
}
