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

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.timeline.model.TimeRange;
import cc.creativecomputing.timeline.model.TrackData;
import cc.creativecomputing.timeline.model.TrackType;
import cc.creativecomputing.timeline.model.communication.MarkerListener;
import cc.creativecomputing.timeline.model.points.ControlPoint;
import cc.creativecomputing.timeline.model.points.MarkerPoint;
import cc.creativecomputing.timeline.view.TransportRulerView;


/**
 * @author christianriekoff
 *
 */
public class TransportController extends TrackDataController implements Zoomable{
	
	public static class RulerInterval{
		private double _myMin;
		private double _myMax;
		
		private double _myInterval;
		
		public RulerInterval(float theMax, float theMin, float theInterval) {
			_myMin = theMin;
			_myMax = theMax;
			_myInterval = theInterval;
		}
		
		public boolean isInInterval(double theValue) {
			return theValue < _myMax && theValue >= _myMin;
		}
		
		public double quantize(double theValue) {
			return quantize(theValue, 1);
		}
		
		public double quantize(double theValue, int theRaster) {
			if(theRaster == 0)return theValue;
			double myFactor = _myInterval / theRaster;
			return myFactor * Math.round(theValue / myFactor);
		}
		
		public double interval() {
			return _myInterval;
		}
	}
	
	private List<RulerInterval> _myIntervals = new ArrayList<TransportController.RulerInterval>();
	
	public static enum TransportAction{
		PLAY, STOP, LOOP
	}
	
	private TrackData _myMarkerList;

	public TransportRulerView _myTransportRulerView;
	
	public TimeRangeController _myTimeRangeController;

	private boolean _myDefineLoop = false;
	private int _myStartClickX = 0;
	
	private double _myLowerBound = 0;
	private double _myUpperBound = 0;
	
	private double _myCurrentTime;
	private double _mySpeedFactor;
	
	private ArrayList<TransportTimeListener> _myTimeListener;
	private ArrayList<TransportStateListener> _myStateListener;
	
	private ArrayList<MarkerListener> _myMarkerListener;
	
	public static enum PlayMode {
		PLAYING, STOPPED
	}
	
	private PlayMode _myPlayMode;
	
	private boolean _myIsInLoop = false;
	
	private TimeRange _myLoop = new TimeRange();
	
	private TimelineController _myTimelineController;
	
	private RulerInterval _myInterval;
	
	public TransportController(TimelineController theTimelineController) {
		super(theTimelineController, TrackType.MARKER);
		_myTimelineController = theTimelineController;
		_myMarkerList = new TrackData(null);
		
		_myPlayMode = PlayMode.STOPPED;
		_myCurrentTime = 0;
		_mySpeedFactor = 1;
		_myTimeListener = new ArrayList<TransportTimeListener>();
		_myStateListener = new ArrayList<TransportStateListener>();
		_myMarkerListener = new ArrayList<MarkerListener>();
		
		_myInterval = new RulerInterval(Float.MAX_VALUE, 0f, 1f);
		createIntervals();
	}
	
	private RulerInterval currentInterval() {
		int myDevides = _myTransportRulerView.width() / MIN_SPACE;
		double myInterval = (_myUpperBound - _myLowerBound) / myDevides;
		
		for(RulerInterval myRulerInterval:_myIntervals) {
			if(myRulerInterval.isInInterval(myInterval)) {
				return myRulerInterval;
			}
		}
		return new RulerInterval(Float.MAX_VALUE, 0f, 1f);
	}
	
	private void createIntervals() {
		_myIntervals.add(new RulerInterval(18000, 9000, 18000));
		_myIntervals.add(new RulerInterval(9000, 3600, 7200));
		_myIntervals.add(new RulerInterval(3600, 1800, 3600));
		
		_myIntervals.add(new RulerInterval(1800, 1500, 1800));
		_myIntervals.add(new RulerInterval(1500, 600, 1200));
		_myIntervals.add(new RulerInterval(600, 300, 600));
		
		_myIntervals.add(new RulerInterval(300, 150, 300));
		_myIntervals.add(new RulerInterval(150, 60, 120));
		_myIntervals.add(new RulerInterval(60, 30, 60));
		
		_myIntervals.add(new RulerInterval(30, 20, 30));
		_myIntervals.add(new RulerInterval(20, 10, 20));
		
		_myIntervals.add(new RulerInterval(10, 5, 10));
		_myIntervals.add(new RulerInterval(5, 2, 5));
		_myIntervals.add(new RulerInterval(2, 1, 2));
		
		_myIntervals.add(new RulerInterval(1.0000f, 0.5000f, 1));
		_myIntervals.add(new RulerInterval(0.5000f, 0.2500f, 0.5f));
		_myIntervals.add(new RulerInterval(0.2500f, 0.1000f, 0.2f));

		_myIntervals.add(new RulerInterval(0.1000f, 0.0500f, 0.1f));
		_myIntervals.add(new RulerInterval(0.0500f, 0.0250f, 0.05f));
		_myIntervals.add(new RulerInterval(0.0250f, 0.0100f, 0.02f));

		_myIntervals.add(new RulerInterval(0.0100f, 0.0050f, 0.01f));
		_myIntervals.add(new RulerInterval(0.0050f, 0.0025f, 0.005f));
		_myIntervals.add(new RulerInterval(0.0025f, 0.0010f, 0.002f));
		
		_myIntervals.add(new RulerInterval(0.0010f, 0.0000f, 0.001f));
	}
	
	public RulerInterval rulerInterval() {
		return _myInterval;
	}
	
	public void addMarkerFromMouse(final String theName, final int theMouseX){
		if(_myTransportRulerView == null)return;
		double myClickedTime = _myTransportRulerView.viewXToTime(theMouseX);
		addMarker(theName, myClickedTime);
	}
	
	/**
	 * Adds a marker with the given name at the given time
	 * @param theName name of the marker
	 * @param theTime time of the marker
	 */
	public void addMarker(final String theName, final double theTime) {
		_myMarkerList.add(new MarkerPoint(theTime, theName));
		if(_myTransportRulerView != null)_myTransportRulerView.render();
	}
	
	/**
	 * Adds a listener to react on marker events
	 * @param theMarkerListener
	 */
	public void addMarkerListener(MarkerListener theMarkerListener){
		_myMarkerListener.add(theMarkerListener);
	}
	
	/**
	 * Removes the given listener
	 * @param theMarkerListener
	 */
	public void removeMarkerListener(MarkerListener theMarkerListener){
		_myMarkerListener.add(theMarkerListener);
	}
	
	public TrackData marker() {
		return _myMarkerList;
	}
	
	public TimeRange loopRange() {
		return _myLoop;
	}
	
	public void rulerView(TransportRulerView theRulerView) {
		super.trackDataView(theRulerView);
		_myTransportRulerView = theRulerView;
		_myTimeRangeController = new TimeRangeController(_myTimelineController, loopRange(), _myTransportRulerView);
		_myInterval = currentInterval();
	}
	
	private MarkerPoint _myLastMarker = null;
	
	public double time() {
		return _myCurrentTime;
	}
	
	public void speed(double theSpeed){
		_mySpeedFactor = theSpeed;
		if(_myTransportRulerView != null)_myTransportRulerView.speed(theSpeed);
	}
	
	public double speed() {
		return _mySpeedFactor;
	}
	
	public double loopStart() {
		return _myLoop.start();
	}
	
	public double loopEnd() {
		return _myLoop.end();
	}
	
	public void loop(final double theLoopStart, final double theLoopEnd) {
		_myLoop.range(theLoopStart, theLoopEnd);
		onChangeLoop();
	}
	
	public void doLoop(final boolean theIsInLoop) {
		_myIsInLoop = theIsInLoop;
		onChangeLoop();
	}
	
	public boolean doLoop() {
		return _myIsInLoop;
	}
	
	private int MIN_SPACE = 150;
	
	public void setRange( double theLowerBound, double theUpperBound ) {
		_myLowerBound = theLowerBound;
		_myUpperBound = theUpperBound;
		_myInterval = currentInterval();
		if(_myTransportRulerView != null)_myTransportRulerView.render();
	}

	public double lowerBound() {
		return _myLowerBound;
	}

	public double upperBound() {
		return _myUpperBound;
	}
	
	private void onChangeLoop() {
		for (TransportTimeListener myTransportable:_myTimeListener) {
			myTransportable.onChangeLoop(_myLoop, _myIsInLoop);
		}
	}
	
	private void moveTransport(final int theMouseX) {
		if(_myTransportRulerView == null)return;
		double myClickedTime = _myTransportRulerView.viewXToTime(theMouseX);
		time(myClickedTime);
	}
	
	@Override
	public TrackData trackData() {
		return _myMarkerList;
	}
	
	public void trackData(TrackData theTrackData) {
		_myMarkerList = theTrackData;
	}
	
	public TrackType trackType() {
		return TrackType.MARKER;
	}
    
    public double viewXToTime(int theViewX) {
        return _myTransportRulerView.viewXToTime(theViewX);
    }

    public int timeToViewX(double theTime) {
        return _myTransportRulerView.timeToViewX(theTime);
    }
	
	public Point2D curveToViewSpace(ControlPoint thePoint) {
        Point2D myResult = new Point2D.Double();
        int myX = timeToViewX(thePoint.time());
        int myY = 0; // reverse y axis
        myResult.setLocation(myX, myY);
        return myResult;
    }
    
    public ControlPoint viewToCurveSpace(Point2D thePoint) {
        ControlPoint myResult = new ControlPoint();
        
        myResult.time(viewXToTime((int) thePoint.getX()));
        myResult.value(0);
        
        return myResult;
    }
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.controller.TrackDataController#createPoint(de.artcom.timeline.model.points.ControlPoint)
	 */
	@Override
	public ControlPoint createPointImpl(ControlPoint theCurveCoords) {
		MarkerPoint myMarkerPoint = new MarkerPoint(theCurveCoords.time(), "");
		
		if(_myTransportRulerView != null)_myTransportRulerView.showMarkerDialog(myMarkerPoint);
		
		return myMarkerPoint;
	}
	
	/* (non-Javadoc)
     * @see de.artcom.timeline.controller.TrackDataController#dragPointImp(java.awt.geom.Point2D, boolean)
     */
    @Override
    public void dragPointImp(ControlPoint theDraggedPoint, Point2D myTargetPosition, boolean theIsPressedShift) {
    	ControlPoint myPoint = _myTrackContext.snapToRaster(viewToCurveSpace(myTargetPosition));
    	trackData().move(theDraggedPoint, myPoint);
    }
	
	private double _myLoopStart = 0;
	private double _myLoopEnd = 0;
	
	public void mousePressed(MouseEvent e) {
		if(_myTransportRulerView == null)return;
		boolean myPressedShift = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK;
		
		_myDefineLoop = e.getY() < _myTransportRulerView.height() / 2;
		
		if(!_myDefineLoop) {
			
			if(!myPressedShift) {
				moveTransport(e.getX());
			}else {
				super.mousePressed(e);
			}
		}else {
			_myTimeRangeController.mousePressed(e);
			_myLoopStart = _myTimeRangeController._myLoopStart;
			_myLoopEnd = _myTimeRangeController._myLoopEnd;
		}
		_myTransportRulerView.render();
		_myTimelineController.render();
	}
	
	public void mouseDragged(MouseEvent e) {
		boolean myPressedShift = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK;
		
		if(!_myDefineLoop) {
			if(!myPressedShift) {
				moveTransport(e.getX());
				_myTimelineController.render();
			}else {
				super.mouseDragged(e);
			}
			_myTimelineController.render();
		}else {
			_myTimeRangeController.mouseDragged(e);
			doLoop(true);
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		boolean myPressedShift = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK;
		if(_myDefineLoop) {
			if(e.getX() == _myStartClickX ) {
				doLoop(false);
			}else {

				if(myPressedShift) {
				
					_myTimelineController.scaleTracks(
						_myLoopStart, _myLoopEnd, 
						_myTimeRangeController._myLoopStart, _myTimeRangeController._myLoopEnd
					);
				}
			}
		}else {
			if(!myPressedShift) {
//				moveTransport(e.getX());
			}else {
				super.mouseReleased(e);
			}
		}
	}
	
	public boolean isPlaying() {
		return _myPlayMode == PlayMode.PLAYING;
	}
	
	public void play() {
		if (isPlaying()) {
			rewind();
		} else {
			_myPlayMode = PlayMode.PLAYING;
		}
		for(TransportStateListener myStateListener:_myStateListener){
			myStateListener.play(_myCurrentTime);
		}
	}
	
	public void stop() {
		if (!isPlaying()) {
			rewind();
		}
		_myPlayMode = PlayMode.STOPPED;
		for(TransportStateListener myStateListener:_myStateListener){
			myStateListener.stop(_myCurrentTime);
		}
	}
	
	public void rewind() {
		_myCurrentTime = 0;
		for(TransportTimeListener myTransportable:_myTimeListener){
			myTransportable.time(_myCurrentTime);
		}
	}
	
	public void loop() {
		doLoop(!doLoop());
	}
	
	public void onTransportAction(final TransportAction theAction) {
		switch (theAction) {
		case PLAY:
			play();
			break;
		case STOP:
			stop();
			break;
		case LOOP:
			loop();
			break;

		default:
			break;
		}
	}
	
	public void update(double theDeltaT) {
		if(_myTransportRulerView != null)_myTransportRulerView.render();
		if (_myPlayMode == PlayMode.PLAYING) {
			_myCurrentTime += theDeltaT * _mySpeedFactor;
			
			if(_myIsInLoop && _myCurrentTime > _myLoop.end()) {
				_myCurrentTime = _myLoop.start() + _myCurrentTime - _myLoop.end();
			}
			
			for (TransportTimeListener myTransportable:_myTimeListener) {
				myTransportable.time(_myCurrentTime);
			}
			if(_myTransportRulerView != null)_myTransportRulerView.time(_myCurrentTime);
			
			MarkerPoint myCurrentMarker = (MarkerPoint)_myMarkerList.getFirstPointAt(_myCurrentTime);
			if(myCurrentMarker != _myLastMarker && _myLastMarker != null){
				for(MarkerListener myListener:_myMarkerListener){
					myListener.onMarker(_myLastMarker);
				}
			}
			_myLastMarker = myCurrentMarker;
		}
	}
	
	double _myLastTime = -1;
	public void time(double theTime) {
		theTime = CCMath.max(0,theTime);
		_myCurrentTime = theTime;
		for(TransportTimeListener myTransportable:_myTimeListener){
			myTransportable.time(_myCurrentTime);
		}
		if(_myTransportRulerView != null)_myTransportRulerView.time(_myCurrentTime);
//		if(theTime < _myLastTime || theTime - _myLastTime > 0.1){
//			_myLastTime = theTime;
//			_myLastMarker = null;
//			return;
//		}
		_myLastTime = theTime;
		
		// TODO make marker events work (this should be replaced by timed event tracks)
//		MarkerPoint myCurrentMarker = (MarkerPoint)_myMarkerList.getFirstPointAt(_myCurrentTime);
//		if(myCurrentMarker != _myLastMarker && _myLastMarker != null){
//			for(MarkerListener myListener:_myMarkerListener){
//				myListener.onMarker(_myLastMarker);
//			}
//		}
//		_myLastMarker = myCurrentMarker;
	}
	
	public void addTimeListener(TransportTimeListener theTransportable) {
		_myTimeListener.add(theTransportable);
	}
	
	public void removeTimeListener(TransportTimeListener theTransportable) {
		_myTimeListener.remove(theTransportable);
	}
	
	public void addStateListener(TransportStateListener theTransportable) {
		_myStateListener.add(theTransportable);
	}
	
	public void removeStateListener(TransportStateListener theTransportable) {
		_myStateListener.remove(theTransportable);
	}
}
