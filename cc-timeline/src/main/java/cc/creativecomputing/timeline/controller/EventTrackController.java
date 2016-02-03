/*  
 * Copyright (c) 2011 Christian Riekoff <info@texone.org>  
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
import cc.creativecomputing.timeline.controller.tools.ToolController;
import cc.creativecomputing.timeline.model.Track;
import cc.creativecomputing.timeline.model.UndoHistory;
import cc.creativecomputing.timeline.model.actions.AddControlPointAction;
import cc.creativecomputing.timeline.model.actions.MoveEventAction;
import cc.creativecomputing.timeline.model.points.ControlPoint;
import cc.creativecomputing.timeline.model.points.ControlPoint.ControlPointType;
import cc.creativecomputing.timeline.model.points.ControlPoint.HandleType;
import cc.creativecomputing.timeline.model.points.HandleControlPoint;
import cc.creativecomputing.timeline.model.points.TimedEventPoint;
import cc.creativecomputing.timeline.view.TimelineContainer;
import cc.creativecomputing.timeline.view.TrackView;

/**
 * @author christianriekoff
 * 
 */
public class EventTrackController extends TrackController {
	
	public final static float MIN_EVENT_TIME = 0.0001f;
	
	private boolean _myDragBlock = false;
	
	private List<EventTrackListener> _myEventTrackListener = new ArrayList<EventTrackListener>();

	/**
	 * @param theTimelineController
	 * @param theTrack
	 * @param theParent
	 */
	public EventTrackController(
		TimelineController theTimelineController,
		ToolController theToolController,
		Track theTrack, 
		GroupTrackController theParent
	) {
		super(theTimelineController, theToolController, theTrack, theParent);
	}
	
	public void addListener(EventTrackListener theEventTrackListener) {
		_myEventTrackListener.add(theEventTrackListener);
	}
	
	public void removeListener(EventTrackListener theEventTrackListener) {
		_myEventTrackListener.remove(theEventTrackListener);
	}
	
	public List<String> eventTypes(){
		List<String> myResult = new ArrayList<String>();
		String[] myEventTypesArray = _myTrack.extras().get(TimelineContainer.EVENT_TYPES).split(",");
		for(String myEventType:myEventTypesArray) {
			myResult.add(myEventType);
		}
		return myResult;
	}
	
	public void delete(TimedEventPoint theEvent) {
		for(EventTrackListener myListener:_myEventTrackListener) {
			myListener.onDelete(theEvent);
		}
	}
	
	public void properties(TimedEventPoint theEvent) {
		for(EventTrackListener myListener:_myEventTrackListener) {
			myListener.onProperties(theEvent);
		}
	}
	
	public void createPoint(MouseEvent theEvent, String theEventType) {
		Point2D myViewCoords = new Point2D.Double(theEvent.getX(), theEvent.getY());
		TimedEventPoint myPoint = (TimedEventPoint)createPoint(myViewCoords);
		myPoint.eventType(theEventType);
		for(EventTrackListener myListener:_myEventTrackListener) {
			myListener.onCreate(myPoint);
		}
		UndoHistory.instance().apply(new AddControlPointAction(this, myPoint));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.artcom.timeline.controller.TrackDataController#createPoint(de.artcom.timeline.model.points.ControlPoint)
	 */
	@Override
	public TimedEventPoint createPointImpl(ControlPoint theCurveCoords) {
		double myBlend = CCMath.blend(_myTrack.minValue(), _myTrack.maxValue(), theCurveCoords.value());
		myBlend = CCMath.round(myBlend);
		myBlend /= _myTrack.maxValue() - _myTrack.minValue();

		TimedEventPoint myStartPoint = new TimedEventPoint(theCurveCoords.time(), myBlend);
		return myStartPoint;
	}
	
	@Override
	protected TimedEventPoint createPoint(Point2D theViewCoords) {
        ControlPoint myControlPoint = viewToCurveSpace(theViewCoords);
        TimedEventPoint myEventPoint = createPointImpl(myControlPoint);
        	
        trackData().add(myEventPoint);
        
        double myViewTime = _myTrackContext._myZoomController.upperBound() - _myTrackContext._myZoomController.lowerBound();
		myViewTime /= 10;
		
		double myEventEndTime = myEventPoint.time() + myViewTime;
		
		ControlPoint myHigherPoint = myEventPoint.getNext();
		if(myHigherPoint != null) {
			myEventEndTime = Math.min(myHigherPoint.time(), myEventEndTime);
		}
		
		HandleControlPoint myEndPoint = new HandleControlPoint(
			myEventPoint, 
			HandleType.TIME_END, 
			myEventEndTime, 
			1.0
		);
		myEventPoint.endPoint(myEndPoint);
        
        _myHasAdd = true;
        _myTrackDataView.render();
        return myEventPoint;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.artcom.timeline.controller.TrackDataController#dragPointImp(java.awt.geom.Point2D, boolean)
	 */
	@Override
	public void dragPointImp(ControlPoint theDraggedPoint, Point2D myTargetPosition, boolean theIsPressedShift) {
		ControlPoint myTargetCurvePoint = viewToCurveSpace(myTargetPosition);
		ControlPoint myPoint = _myTrackContext.snapToRaster(viewToCurveSpace(myTargetPosition));
		
		if (theDraggedPoint.getType().equals(ControlPointType.HANDLE)) {
            // first get next point:
			
			HandleControlPoint myControlPoint = (HandleControlPoint)theDraggedPoint;
			ControlPoint myStart = myControlPoint.parent();
			
			double myTime = Math.max(myStart.time() + MIN_EVENT_TIME, myPoint.time());

			ControlPoint myHigherPoint = myStart.getNext();
			if(myHigherPoint != null) {
				myTime = Math.min(myHigherPoint.time(), myTime);
			}
			
            theDraggedPoint.time(myTime);
			theDraggedPoint.value(myPoint.value());
			
			for(EventTrackListener myListener:_myEventTrackListener) {
				myListener.onChange(_myEditedEvent);
			}
		}else {
			TimedEventPoint myTimedStartPoint = (TimedEventPoint) theDraggedPoint;
			
			if(_myDragBlock) {
				if(_myStartPoint == null)return;
				if(_myCurveCoords == null)return;
				
				double myMove = myTargetCurvePoint.time() - _myCurveCoords.time();
				ControlPoint myMovedTarget = new ControlPoint(_myStartPoint.time() + myMove, 1.0);
				double myEndOffset = myTimedStartPoint.endPoint().time() - myTimedStartPoint.time();

				double myTime = _myTrackContext.snapToRaster(myMovedTarget).time();
				TimedEventPoint myLowerPoint = (TimedEventPoint)theDraggedPoint.getPrevious();
				if(myLowerPoint != null) {
					myTime = Math.max(myLowerPoint.endTime(), myTime);
				}
				ControlPoint myHigherPoint = theDraggedPoint.getNext();
				if(myHigherPoint != null) {
					myTime = Math.min(myHigherPoint.time(), myTime + myEndOffset) - myEndOffset;
				}
				myTime = Math.max(0, myTime);
				
				myMovedTarget.time(myTime);
				
				trackData().move(theDraggedPoint, myMovedTarget);
				
				myTimedStartPoint.endPoint().time(myTimedStartPoint.time() + myEndOffset);
			}else {
				double myTime = _myTrackContext.snapToRaster(myPoint).time();
				HandleControlPoint myEnd = myTimedStartPoint.endPoint();
				myTime = Math.min(myEnd.time() - MIN_EVENT_TIME, myTime);

				TimedEventPoint myLowerPoint = (TimedEventPoint)theDraggedPoint.getPrevious();
				if(myLowerPoint != null) {
					myTime = Math.max(myLowerPoint.endTime(), myTime);
				}
				
				myPoint.time(myTime);
				trackData().move(theDraggedPoint, myPoint);
				for(EventTrackListener myListener:_myEventTrackListener) {
					myListener.onChange(_myEditedEvent);
				}
			}
			viewValue(myPoint.value());
		}
	}
	
	public TimedEventPoint pointAt(double theTime) {
		ControlPoint myCurveCoords = new ControlPoint(theTime, 0);
		TimedEventPoint myLower = (TimedEventPoint)trackData().lower(myCurveCoords);
		if(myLower != null) {
			ControlPoint myUpper = myLower.endPoint();
			if(myCurveCoords.time() > myLower.time() && myCurveCoords.time() < myUpper.time()) {
				return myLower;
			}
		}
		return null;
	}
	
	public TimedEventPoint clickedPoint(MouseEvent e) {
		Point2D myViewCoords = new Point2D.Double(e.getX(), e.getY());
		ControlPoint myCurveCoords = viewToCurveSpace(myViewCoords);
		return pointAt(myCurveCoords.time());
	}
	
	private TimedEventPoint _myEditedEvent = null;
	private double _myStartEnd;
	private ControlPoint _myCurveCoords;
	
	@Override
	public void mousePressed(MouseEvent e) {
		_myMouseStartX = e.getX();
		_myMouseStartY = e.getY();
				
		Point2D myViewCoords = new Point2D.Double(e.getX(), e.getY());
		_myCurveCoords = viewToCurveSpace(myViewCoords);
			 
		if (e.isAltDown()) {
			_myTrackContext.zoomController().startDrag(myViewCoords);
			return;
		}
		
		ControlPoint myControlPoint = pickNearestPoint(myViewCoords);
		HandleControlPoint myHandle = pickHandle(myViewCoords);
		_myDragBlock = false;
		
		_myEditedEvent = null;
		
		if (myHandle != null) {
			_myDraggedPoint = myHandle;
			_myEditedEvent = (TimedEventPoint)myHandle.parent();
		} else if (myControlPoint != null  && distance(myControlPoint, myViewCoords) < TrackView.PICK_RADIUS){
			_myDraggedPoint = myControlPoint;
			_myEditedEvent = (TimedEventPoint)myControlPoint;
		} else {
			
			TimedEventPoint myLower = (TimedEventPoint)trackData().lower(_myCurveCoords);

			if(myLower != null) {
				ControlPoint myUpper = myLower.endPoint();
				ControlPoint myCoords = viewToCurveSpace(myViewCoords);
				if(myCoords.time() > myLower.time() && myCoords.time() < myUpper.time()) {
					_myDraggedPoint = myLower;
					_myEditedEvent = (TimedEventPoint)_myDraggedPoint;
					_myDragBlock = true;
				}
			}
//			if(!_myDragBlock) {
//				_myAddedNewPoint = true;
//				_myDraggedPoint = createPoint(myViewCoords);
//			}
			
		}
		
		if(_myEditedEvent != null) {
			_myStartEnd = _myEditedEvent.endTime();
		}
		
		if(_myDraggedPoint != null) {
            _myStartPoint = _myDraggedPoint.clone();
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		if(_myEditedEvent != null) {
			for(EventTrackListener myListener:_myEventTrackListener) {
				myListener.onChange(_myEditedEvent);
			}
		}
		if (e.isAltDown()) {
			_myTrackContext.zoomController().endDrag();
			return;
		}
		
		if (e.getX() == _myMouseStartX && e.getY() == _myMouseStartY && !_myAddedNewPoint) {
			for(EventTrackListener myListener:_myEventTrackListener) {
				myListener.onClick(_myEditedEvent);
			}
		}
		
		if(_myDraggedPoint != null && _myEditedEvent != null) {
			UndoHistory.instance().apply(new MoveEventAction(
				this, _myEditedEvent, 
				_myStartPoint,  
				_myEditedEvent,
				_myStartEnd,
				_myEditedEvent.endTime()));
		}
        _myDraggedPoint = null;
		_myAddedNewPoint = false;
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.timeline.controller.TrackController#time(double)
	 */
//	@Override
//	public void time(double theTime) {
//		super.time(theTime);
//		if (_myTrack.mute())return;
//		
//		ControlPoint myPoint = trackData().getFirstPointAt(theTime);
//		if(myPoint == null)return;
//		if(myPoint.getType() == ControlPointType.TIMED_EVENT_END){
//			TimedEventEndPoint myEndPoint = (TimedEventEndPoint)myPoint;
//			_myEndPoints.add(myEndPoint);
//		}
//		for(TimedEventEndPoint myEndPoint:new ArrayList<TimedEventEndPoint>(_myEndPoints)){
//			if(theTime < myEndPoint.startPoint().getTime() || theTime > myEndPoint.getTime()){
//				_myEndPoints.remove(myEndPoint);
//			}
//		}
//		if(trackData().size() == 0)return;
//		for(TimedEventEndPoint myEndPoint:_myEndPoints){
//			double myTime = theTime - myEndPoint.startPoint().getTime();
//			
//			for (TimelineListener myListener : _myTimelineListeners) {
//				TimelineEvent myEvent = new TimelineEvent(
//					myTime, 
//					myEndPoint.getValue(), 
//					_myTrack.address(), _myTrack.trackType()
//				);
//				myListener.onTimelineEvent(myEvent);
//			}
//		}
//	}
}
