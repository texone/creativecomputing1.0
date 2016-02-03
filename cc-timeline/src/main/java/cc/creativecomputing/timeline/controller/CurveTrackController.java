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

import java.awt.geom.Point2D;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.timeline.controller.tools.CurveToolController;
import cc.creativecomputing.timeline.model.Track;
import cc.creativecomputing.timeline.model.points.BezierControlPoint;
import cc.creativecomputing.timeline.model.points.ControlPoint;
import cc.creativecomputing.timeline.model.points.ControlPoint.ControlPointType;
import cc.creativecomputing.timeline.model.points.ControlPoint.HandleType;
import cc.creativecomputing.timeline.model.points.CubicControlPoint;
import cc.creativecomputing.timeline.model.points.ExponentialControlPoint;
import cc.creativecomputing.timeline.model.points.HandleControlPoint;
import cc.creativecomputing.timeline.model.points.LinearControlPoint;
import cc.creativecomputing.timeline.model.points.StepControlPoint;

/**
 * @author christianriekoff
 *
 */
public class CurveTrackController extends TrackController{

	private CurveToolController _myCurveTool;
	/**
	 * @param theTimelineController
	 * @param theTrack
	 * @param theParent
	 */
	public CurveTrackController(
		TrackContext theTrackContext, 
		CurveToolController theCurveTool, 
		Track theTrack, 
		GroupTrackController theParent
	) {
		super(theTrackContext, theCurveTool, theTrack, theParent);
		_myCurveTool = theCurveTool;
	}

	/* (non-Javadoc)
     * @see de.artcom.timeline.controller.TrackDataController#createPoint(de.artcom.timeline.model.points.ControlPoint)
     */
    @Override
    public ControlPoint createPointImpl(ControlPoint theCurveCoords) {
    	switch(trackType()) {
    	case BOOLEAN:
    	case INTEGER:
        	return new StepControlPoint(theCurveCoords.time(), theCurveCoords.value());
    	case DOUBLE:
    		switch(_myCurveTool.controlPointType()) {
        	case STEP:
        		return new StepControlPoint(theCurveCoords.time(), theCurveCoords.value());
        	case LINEAR:
        		return new LinearControlPoint(theCurveCoords.time(), theCurveCoords.value());
        	case CUBIC:
        		return new CubicControlPoint(theCurveCoords.time(), theCurveCoords.value());
        	case EXPONENTIAL:
        		ExponentialControlPoint myExpCoords = new ExponentialControlPoint(theCurveCoords.time(), theCurveCoords.value());
        		HandleControlPoint myTensionHandle = new HandleControlPoint(myExpCoords, HandleType.EXPONENT_HANDLE,  0.5, 0.5);
                myExpCoords.handle(myTensionHandle);
                return myExpCoords;
        	case BEZIER:
        		BezierControlPoint myBezierPoint = new BezierControlPoint(theCurveCoords.time(), theCurveCoords.value());
        		
        		ControlPoint myLower = trackData().lower(theCurveCoords);
        		double myTime;
        		if(myLower == null) {
        			myTime = theCurveCoords.time() - 1;
        		}else {
        			myTime = myLower.time() + theCurveCoords.time();
        			myTime /= 2;
        		}
        		HandleControlPoint myInHandle = new HandleControlPoint(myBezierPoint,HandleType.BEZIER_IN_HANDLE, myTime, theCurveCoords.value());
        		myBezierPoint.inHandle(myInHandle);
        		
        		ControlPoint myHigher = trackData().higher(theCurveCoords);
        		if(myHigher == null) {
        			myTime = theCurveCoords.time() + theCurveCoords.time() - myTime;
        		}else {
        			myTime = myHigher.time() + theCurveCoords.time();
        			myTime /= 2;
        		}
        		
        		HandleControlPoint myOutHandle = new HandleControlPoint(myBezierPoint,HandleType.BEZIER_OUT_HANDLE, myTime, theCurveCoords.value());
        		myBezierPoint.outHandle(myOutHandle);
        		
                return myBezierPoint;
            default:
            	break;
        	}
    	default:
    	}
    	throw new RuntimeException("Unexpected TrackType: " + trackType());
    }
    
    private void moveOppositeHandle(HandleControlPoint theMovedHandle, HandleControlPoint theHandleToMove) {
    	ControlPoint myCenter = theMovedHandle.parent();
    	Point2D myPoint = new Point2D.Double(
    		theMovedHandle.time() - myCenter.time(), 
    		theMovedHandle.value() - myCenter.value()
    	);
    	theHandleToMove.time(myCenter.time() -  myPoint.getX());
    	theHandleToMove.value(Math.max(0, Math.min(myCenter.value() -  myPoint.getY(),1)));
    }
    
    /* (non-Javadoc)
     * @see de.artcom.timeline.controller.TrackDataController#dragPointImp(java.awt.geom.Point2D, boolean)
     */
    @Override
    public void dragPointImp(ControlPoint theDraggedPoint, Point2D myTargetPosition, boolean theIsPressedShift) {
    	if (theDraggedPoint.getType().equals(ControlPointType.HANDLE)) {
            // first get next point:
        	HandleControlPoint myHandle = (HandleControlPoint)theDraggedPoint;
            ControlPoint myParent = ((HandleControlPoint)theDraggedPoint).parent();
            ControlPoint myCurveCoords = viewToCurveSpace(myTargetPosition);
            ControlPoint myPreviousPoint = myParent.getPrevious();
            
            switch (myHandle.handleType()) {
			case EXPONENT_HANDLE:
				if (myPreviousPoint == null)return;
				theDraggedPoint.time((myCurveCoords.time() - myPreviousPoint.time()) / (myParent.time() - myPreviousPoint.time()));
				theDraggedPoint.value(myPreviousPoint.value() < myParent.value() ? myCurveCoords.value() : 1 - myCurveCoords.value());
				break;
			case BEZIER_IN_HANDLE:
				if (myPreviousPoint == null)return;
				ControlPoint myPoint = _myTrackContext.snapToRaster(viewToCurveSpace(myTargetPosition));
				
				double time = CCMath.min(myParent.time(), myPoint.time());
				
//				if(myPreviousPoint.getType() == ControlPointType.BEZIER) {
//					HandleControlPoint myOutHandle = ((BezierControlPoint)myPreviousPoint).outHandle();
//					time = CCMath.max(time, myOutHandle.getTime());
//				}else {
					time = CCMath.max(myPoint.time(), myPreviousPoint.time());
//				}
				
				theDraggedPoint.time(CCMath.constrain(myPoint.time(), myPreviousPoint.time(), myParent.time()));
				theDraggedPoint.value(myPoint.value());
				
				if(theIsPressedShift) {
					HandleControlPoint myOutHandle = ((BezierControlPoint)myParent).outHandle();
					moveOppositeHandle(myHandle,myOutHandle);
				}
				
				break;
			case BEZIER_OUT_HANDLE:
				myPoint = _myTrackContext.snapToRaster(viewToCurveSpace(myTargetPosition));
				
				time = CCMath.max(myParent.time(), myPoint.time());
				
				ControlPoint myNextPoint = myParent.getNext();
				
				if(myNextPoint != null) {
//					if(myNextPoint.getType() == ControlPointType.BEZIER) {
//						HandleControlPoint myInHandle = ((BezierControlPoint)myNextPoint).inHandle();
//						time = CCMath.min(time, myInHandle.getTime());
//					}else {
						time = CCMath.min(time, myNextPoint.time());
//					}
				}
				
				theDraggedPoint.time(time);
				theDraggedPoint.value(myPoint.value());
				
				if(theIsPressedShift) {
					HandleControlPoint myInHandle = ((BezierControlPoint)myParent).inHandle();
					moveOppositeHandle(myHandle,myInHandle);
				}
				break;
			default:
				break;
			}
            trackData().move(myParent,myParent);
        } else {
            ControlPoint myPoint = _myTrackContext.snapToRaster(viewToCurveSpace(myTargetPosition));
            
//          double myTimeChange = myPoint.getTime() - theDraggedPoint.getTime();
            double myValueChange = myPoint.value() - theDraggedPoint.value();
            
            trackData().move(theDraggedPoint, _myTrackContext.snapToRaster(myPoint));

            switch(theDraggedPoint.getType()) {
            case BEZIER:
            	BezierControlPoint myBezierPoint = (BezierControlPoint)theDraggedPoint;
            	myBezierPoint.inHandle().value(myBezierPoint.inHandle().value() + myValueChange);
            	myBezierPoint.outHandle().value(myBezierPoint.outHandle().value() + myValueChange);
            	break;
			default:
				break;
            }
            
            viewValue(myPoint.value());
        }
    }
}
