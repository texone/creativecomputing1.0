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
import java.awt.geom.Point2D;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.timeline.controller.tools.TimelineTool;
import cc.creativecomputing.timeline.controller.tools.ToolController;
import cc.creativecomputing.timeline.model.Selection;
import cc.creativecomputing.timeline.model.Track;
import cc.creativecomputing.timeline.model.TrackData;
import cc.creativecomputing.timeline.model.points.ControlPoint;
import cc.creativecomputing.timeline.model.util.TimedContentView;
import cc.creativecomputing.timeline.view.TrackView;


/**
 * @author christianriekoff
 *
 */
public abstract class TrackController extends TrackDataController implements Zoomable, TimedContentView{
	
	protected Track _myTrack;
	protected TrackView _myTrackView;
	
	private Selection _mySelection;
	
	protected ToolController _myToolController;
	protected GroupTrackController _myParent;
	
	public TrackController(
		TrackContext theTrackContext, 
		ToolController theToolController, 
		Track theTrack, 
		GroupTrackController theParent
	) {
		super(theTrackContext, theTrack.trackType());
		_myTrack = theTrack;
        _myToolController = theToolController;
		_myParent = theParent;
	}
	
	public boolean isParentOpen(){
		return _myParent == null || _myParent.isOpen();
	}
	
	public Track track() {
		return _myTrack;
	}
	
	public ToolController tool() {
		return _myToolController;
	}
	
	public void track(Track theTrack) {
		_myTrack = theTrack;
		if(_myTrackView == null)return;
		_myTrackView.mute(theTrack.mute());
		_myTrackView.address(theTrack.address().substring(theTrack.address().lastIndexOf("/") + 1));
		_myTrackView.color(theTrack.color());
		_myTrackView.render();
	}
	
	public void trackData(Track theTrack) {
		_myTrack = theTrack;
		if(_myTrackView == null)return;
		_myTrackView.mute(theTrack.mute());
		_myTrackView.address(theTrack.address().substring(theTrack.address().lastIndexOf("/") + 1));
		_myTrackView.render();
	}
	
	public void mute(boolean theIsMuted) {
		_myTrack.mute(theIsMuted);
		if(_myTrackView != null)_myTrackView.mute(theIsMuted);
	}
	
	public void muteGroup(boolean theIsMuted){
		_myParent.groupTrack().mute(theIsMuted);//.address(), theIsMuted);
	}
	
	protected void viewValue(double theValue) {
		if(_myTrackView != null)_myTrackView.value(CCMath.blend(_myTrack.minValue(), _myTrack.maxValue(), theValue));
	}

	public void color(Color theColor) {
		// TODO fix color track
//		_myTimelineController.colorTrack(theColor, _myTrack.address());
	}
	
	public void view(TrackView theView) {
		_myTrackView = theView;
		trackDataView(theView.trackDataView());
	}
	
	public TrackView view() {
		return _myTrackView;
	}
	
	@Override
	public TrackData trackData() {
		return _myTrack.trackData();
	}
	
	/**
	 * Returns the value of the track at the given time
	 * @param theTime time where to get the value
	 * @return value at the given time
	 */
	public double value(double theTime) {
		if(trackData().size() == 0){
			return _myTrackContext.defaultValue(this);
		}
    	return trackData().value(theTime);
    }
    
//	/**
//	 * Similar to the {@link #value(double)} method but scales the output
//	 * within the min max range of the track
//	 * @param theTime time where to get the value
//	 * @return scaled value at the given time
//	 */
//    public double scaledValue() {
//    	return CCMath.blend(_myTrack.minValue(), _myTrack.maxValue(), value(theTime));
//    }
    
    public void reset() {
    	trackData().clear();
    	if(_myTrackView != null)_myTrackView.render();
    }

    public Point2D curveToViewSpace(ControlPoint thePoint) {
        Point2D myResult = new Point2D.Double();
        int myX = timeToViewX(thePoint.time());
        int myY = (int) ((1 - thePoint.value()) * (_myTrackDataView.height() - 5) + 2.5); // reverse y axis
        myResult.setLocation(myX, myY);
        return myResult;
    }
    
    public ControlPoint viewToCurveSpace(Point2D thePoint) {
        ControlPoint myResult = new ControlPoint();
        
        myResult.time(viewXToTime((int) thePoint.getX()));
        double myValue = CCMath.constrain(1 - (thePoint.getY() - 2.5) / (_myTrackDataView.height() - 5),0,1);
        switch(trackType()) {
    	case BOOLEAN:
    		myValue = myValue >= 0.5 ? 1 : 0;
    		break;
    	case INTEGER:
    		myValue = CCMath.blend(_myTrack.minValue(), _myTrack.maxValue(), myValue);
    		myValue = CCMath.round(myValue);
    		myValue = CCMath.norm(myValue, _myTrack.minValue(), _myTrack.maxValue());
        	break;
		default:
			break;
        }
        
        myResult.value(myValue);
        
        return myResult;
    }
    
    public void clearSelection() {
        _mySelection = null;
        if(_myTrackView != null) _myTrackView.render();
    }
    
    public void selection(Selection theSelection) {
    	_mySelection = theSelection;
    	if(_myTrackView != null)_myTrackView.render();
    }
    
    public Selection selection() {
    	return _mySelection;
    }
    
    public void mousePressed(MouseEvent e) {
			
		if (_myToolController.toolMode() == TimelineTool.MOVE) {
			_myToolController.selectionController().assignTrackData(this);
			_myToolController.selectionController().mousePressed(e);
		   	return;
		} else {
			super.mousePressed(e);
		}
	}


	public void mouseReleased(MouseEvent e) {
		if (_myToolController.toolMode() == TimelineTool.MOVE) {
			_myToolController.selectionController().mouseReleased(e);
			return;
		} else {
			super.mouseReleased(e);
		}
	}
	
	public void mouseMoved(MouseEvent e){
		if (_myToolController.toolMode() == TimelineTool.MOVE) {
			_myToolController.selectionController().assignTrackData(this);
			_myToolController.selectionController().mouseMoved(e);
			return;
		}else{
			super.mouseMoved(e);
		}
	}

	public void mouseDragged(MouseEvent e) {
		if (_myToolController.toolMode() == TimelineTool.MOVE) {
			_myToolController.selectionController().assignTrackData(this);
			_myToolController.selectionController().mouseDragged(e);
			return;
		}else {
			super.mouseDragged(e);
		}
	}

}
