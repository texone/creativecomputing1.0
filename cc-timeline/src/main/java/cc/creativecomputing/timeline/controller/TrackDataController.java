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

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.timeline.model.TrackData;
import cc.creativecomputing.timeline.model.TrackType;
import cc.creativecomputing.timeline.model.UndoHistory;
import cc.creativecomputing.timeline.model.actions.AddControlPointAction;
import cc.creativecomputing.timeline.model.actions.MoveControlPointAction;
import cc.creativecomputing.timeline.model.actions.RemoveControlPointAction;
import cc.creativecomputing.timeline.model.points.BezierControlPoint;
import cc.creativecomputing.timeline.model.points.ControlPoint;
import cc.creativecomputing.timeline.model.points.ControlPoint.ControlPointType;
import cc.creativecomputing.timeline.model.points.ExponentialControlPoint;
import cc.creativecomputing.timeline.model.points.HandleControlPoint;
import cc.creativecomputing.timeline.model.points.TimedEventPoint;
import cc.creativecomputing.timeline.model.util.TimedContentView;
import cc.creativecomputing.timeline.view.TrackDataView;
import cc.creativecomputing.timeline.view.TrackView;


/**
 * @author christianriekoff
 *
 */
public abstract class TrackDataController implements Zoomable, TimedContentView{
	
    protected int _myMouseStartX;
    protected int _myMouseStartY;
    protected boolean _myAddedNewPoint = false;

    protected ControlPoint _myDraggedPoint;
	
    protected TrackDataView _myTrackDataView;
    
    protected TrackContext _myTrackContext;
    
    protected TrackType _myTrackType;
	
	public TrackDataController(
		TrackContext theTrackContext, 
		TrackType theTrackType
	) {
		_myTrackContext = theTrackContext;
        _myTrackType = theTrackType;
	}
	
	public TrackContext context() {
		return _myTrackContext;
	}
	
	public void trackDataView(TrackDataView theView) {
		_myTrackDataView = theView;
	}
	
	public TrackDataView trackDataView() {
		return _myTrackDataView;
	}
	
	public abstract TrackData trackData();
	
	public TrackType trackType() {
		return _myTrackType;
	}
    
    public void reset() {
    	trackData().clear();
    	_myTrackDataView.render();
    }
    
    public double viewXToTime(int theViewX) {
        return (double) theViewX / (double) _myTrackDataView.width() * (_myTrackContext.viewTime()) + _myTrackContext._myLowerBound;
    }

    public int timeToViewX(double theCurveX) {
        return (int) ((theCurveX - _myTrackContext._myLowerBound) / (_myTrackContext.viewTime()) * _myTrackDataView.width());
    }
    
//    public abstract double viewXToTime(int theViewX);
//
//    public abstract int timeToViewX(double theCurveX);

    public abstract Point2D curveToViewSpace(ControlPoint thePoint);
    
    public abstract ControlPoint viewToCurveSpace(Point2D thePoint);
    
    @Override
    public void setRange(double theLowerBound, double theUpperBound) {
        if(_myTrackDataView != null)_myTrackDataView.render();
    }
    
    protected double pickRange() {
    	return TrackView.PICK_RADIUS / _myTrackDataView.width() * (_myTrackContext.viewTime());
    }
	
	// picks the nearest point (could be null) and returns it in view space
    public ControlPoint pickNearestPoint(Point2D theViewCoords) {
        ControlPoint myPickCoords = viewToCurveSpace(theViewCoords);
        double myPickRange = pickRange();
        ArrayList<ControlPoint> myPoints = trackData().rangeList(
        	myPickCoords.time()-myPickRange,
        	myPickCoords.time()+myPickRange
        );
        
        if (myPoints.size()==0) {
            return null;
        }
        
        Point2D myCurrentPoint = curveToViewSpace(myPoints.get(0));
        ControlPoint myNearest = myPoints.get(0);
        double myMinDistance = myCurrentPoint.distance(theViewCoords);
        for (ControlPoint myPoint : myPoints) {
            myCurrentPoint = curveToViewSpace(myPoint);
            double myDistance = myCurrentPoint.distance(theViewCoords);
            if (myDistance < myMinDistance) {
                myNearest = myPoint;
                myMinDistance = myDistance;
            }
        }
        return myNearest; 
    }
    
    protected boolean _myHasAdd = false;
    
    public abstract ControlPoint createPointImpl(ControlPoint theCurveCoords);
    
    /**
     * 
     * 
     * @param theViewCoords
     */
    protected ControlPoint createPoint(Point2D theViewCoords) {
        ControlPoint myControlPoint = viewToCurveSpace(theViewCoords);
        myControlPoint = createPointImpl(myControlPoint);
        	
        trackData().add(myControlPoint);
        _myHasAdd = true;
        _myTrackDataView.render();
        return myControlPoint;
    }
    
    private void removePoint(Point2D theViewCoords) {
    	ControlPoint myNearestPoint = pickNearestPoint(theViewCoords);
    	if(myNearestPoint == null)return;
    	trackData().remove(myNearestPoint);
        _myTrackDataView.render();
    	UndoHistory.instance().apply(new RemoveControlPointAction(this, myNearestPoint));
    }

    public boolean isDragging() {
        return _myDraggedPoint != null;
    }
    
    private boolean checkForPickRange(ControlPoint theHandle, ControlPoint thePrevious, ControlPoint theNext, Point2D theViewCoords) {
    	double myX = theHandle.time() * (theNext.time() - thePrevious.time()) + thePrevious.time();
        ControlPoint myTensionHandleCS = new ControlPoint(myX, trackData().value(myX));
        
        return curveToViewSpace(myTensionHandleCS).distance(theViewCoords) < TrackView.PICK_RADIUS;
    }
    
    public HandleControlPoint pickHandle(Point2D theViewCoords) {
        ControlPoint myCurveCoords = viewToCurveSpace(theViewCoords);
        ControlPoint myNextPoint = trackData().ceiling(myCurveCoords);
        ControlPoint myPreviousPoint = trackData().lower(myCurveCoords);
        
        if(myNextPoint != null) {
	        switch(myNextPoint.getType()) {
	        case EXPONENTIAL:
	        	if (myPreviousPoint == null) {
	                break;
	            }
	        	HandleControlPoint myTensionHandle = ((ExponentialControlPoint)myNextPoint).handle();
	            
	            if (checkForPickRange(myTensionHandle, myPreviousPoint, myNextPoint, theViewCoords)) {
	                return myTensionHandle;
	            }
	            
	        	break;
	        case BEZIER:
	        	HandleControlPoint myInputHandle = ((BezierControlPoint)myNextPoint).inHandle();
	        	
	        	if (curveToViewSpace(myInputHandle).distance(theViewCoords) < TrackView.PICK_RADIUS) {
	                return myInputHandle;
	            }
	        	break;
			default:
				break;
	        }
	        
	        myNextPoint = trackData().higher(myNextPoint);
	        if(myNextPoint != null && myNextPoint.getType() == ControlPointType.BEZIER) {
	        	HandleControlPoint myInputHandle = ((BezierControlPoint)myNextPoint).inHandle();
	        	if (curveToViewSpace(myInputHandle).distance(theViewCoords) < TrackView.PICK_RADIUS) {
	                return myInputHandle;
	            }
	        }
        }
        
        
        if(myPreviousPoint != null) {
	        switch(myPreviousPoint.getType()) {
	        case BEZIER:
	        	HandleControlPoint myOutputHandle = ((BezierControlPoint)myPreviousPoint).outHandle();
	        	
	        	if (curveToViewSpace(myOutputHandle).distance(theViewCoords) < TrackView.PICK_RADIUS) {
	                return myOutputHandle;
	            }
	        	break;
	        case TIMED_EVENT:
	        	HandleControlPoint myTimedEnd = ((TimedEventPoint)myPreviousPoint).endPoint();
	        	
	        	if (Math.abs(curveToViewSpace(myTimedEnd).getX() - theViewCoords.getX()) < TrackView.PICK_RADIUS) {
	                return myTimedEnd;
	            }
	        	break;
			default:
				break;
	        }
	        
	        myPreviousPoint = trackData().lower(myPreviousPoint);
	        if(myPreviousPoint == null || myPreviousPoint.getType() != ControlPointType.BEZIER) {
	        	return null;
	        }
	        
	        HandleControlPoint myOutputHandle = ((BezierControlPoint)myPreviousPoint).outHandle();
	    	if (curveToViewSpace(myOutputHandle).distance(theViewCoords) < TrackView.PICK_RADIUS) {
	            return myOutputHandle;
	        }
        }
    	
        return null;
    }
    
    protected ControlPoint _myStartPoint;
    
    public abstract void dragPointImp(ControlPoint theDraggedPoint, Point2D myTargetPosition, boolean theIsPressedShift);

    public void dragPoint(Point2D theViewCoords, boolean theIsPressedShift) {
    	if(_myDraggedPoint == null)return;

        double myY = theViewCoords.getY();
        if (myY < 0) {
            myY = 0;
        } else if (myY > _myTrackDataView.height()) {
            myY = _myTrackDataView.height();
        }
        
        double myX = theViewCoords.getX();
        if(myX < 0) {
            myX = 0;
        }

        Point2D myTargetPosition = new Point2D.Double(myX, myY);
        dragPointImp(_myDraggedPoint, myTargetPosition, theIsPressedShift);
        
        _myTrackDataView.render();
    }

    public void endDrag() {
        if(_myHasAdd) {
            _myHasAdd = false;
            UndoHistory.instance().apply(new AddControlPointAction(this, _myDraggedPoint));
        }else {
            if(_myDraggedPoint != null) {
            	UndoHistory.instance().apply(new MoveControlPointAction(this, _myDraggedPoint, _myStartPoint,  _myDraggedPoint));
            }
        }
        _myDraggedPoint = null;
    }
    
    public double distance(ControlPoint theNearest, Point2D theViewCoords) {
    	if(_myTrackType == TrackType.MARKER || _myTrackType == TrackType.TIME || _myTrackType == TrackType.GROUP)
    		return Math.abs(curveToViewSpace(theNearest).getX() - theViewCoords.getX());
    	else 
    		return curveToViewSpace(theNearest).distance(theViewCoords);
    }
    
    private boolean _mySnap = false;
    private boolean _mySnapX = false;
    private boolean _mySnapY = false;
    
    private static int SnapRange = 10;
    
    public void mousePressed(MouseEvent e) {
		_myMouseStartX = e.getX();
		_myMouseStartY = e.getY();
		_mySnap = true;
		
		Point2D myViewCoords = new Point2D.Double(e.getX(), e.getY());
			
		if (e.isAltDown()) {
			_myTrackContext.zoomController().startDrag(myViewCoords);
			return;
		}
		
		ControlPoint myControlPoint = pickNearestPoint(myViewCoords);
		ControlPoint myHandle = pickHandle(myViewCoords);
		
		if (myHandle != null) {
			_myDraggedPoint = myHandle;
		} else if (myControlPoint != null  && distance(myControlPoint, myViewCoords) < TrackView.PICK_RADIUS){
			_myDraggedPoint = myControlPoint;
		} else {
			if(e.getClickCount() == 2){
				_myAddedNewPoint = true;
				_myDraggedPoint = createPoint(myViewCoords);
			}
		}
		
		if(!_myHasAdd && _myDraggedPoint != null) {
            _myStartPoint = _myDraggedPoint.clone();
        }
	}

	public void mouseReleased(MouseEvent e) {

		if (e.isAltDown()) {
			_myTrackContext.zoomController().endDrag();
			return;
		}
		
		if (e.getClickCount() == 2 && e.getX() == _myMouseStartX && e.getY() == _myMouseStartY && !_myAddedNewPoint) {
			Point2D myViewCoords = new Point2D.Double(e.getX(), e.getY());
			removePoint(myViewCoords);
		} else {
			endDrag();
		}

		_myAddedNewPoint = false;
	}

	public void mouseDragged(MouseEvent e) {
		
		if(e.isAltDown()) {
			_myTrackContext.zoomController().performDrag(new Point2D.Double(e.getX(), e.getY()), _myTrackDataView.width());
			return;
		}
		int targetX = e.getX();
		int targetY = e.getY();
		int myMovX = CCMath.abs(_myMouseStartX - e.getX());
		int myMovY = CCMath.abs(_myMouseStartY - e.getY());
		_mySnap = _mySnap && (myMovX < SnapRange || myMovY < SnapRange);
		if(_mySnap){
			if(myMovX > myMovY){
				targetY = _myMouseStartY;
			}else{
				targetX = _myMouseStartX;
			}
		}
		Point2D myViewCoords = new Point2D.Double(Math.min(targetX, trackDataView().width() - 1), targetY);
		boolean myPressedShift = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK;
		dragPoint(myViewCoords, myPressedShift);
	}
	
	public void mouseMoved(MouseEvent e) {
		Point2D myViewCoords = new Point2D.Double(e.getX(), e.getY());

		ControlPoint myNearest = pickNearestPoint(myViewCoords);
        ControlPoint myTensionHandle = pickHandle(myViewCoords);

        
		if (myNearest != null && distance(myNearest,myViewCoords) < TrackView.PICK_RADIUS || myTensionHandle != null) {
			switch(_myTrackType){
			case GROUP:
			case TIME:
				_myTrackDataView.moveRangeCursor();
				break;
			default:
				_myTrackDataView.selectCursour();
				break;
			}
		} else {
			_myTrackDataView.defaultCursour();
		}
	}
}
