/*  
 * Copyright (c) 2012 Christian Riekoff <info@texone.org>  
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

import cc.creativecomputing.timeline.controller.tools.CurveToolController;
import cc.creativecomputing.timeline.controller.tools.ToolController;
import cc.creativecomputing.timeline.model.points.ControlPoint;

/**
 * The TrackContext is used to share informations between a number of tracks
 * @author christianriekoff
 *
 */
public class TrackContext implements Zoomable{
    
    protected double _myLowerBound;
    protected double _myUpperBound;

	protected CCZoomController _myZoomController;

	protected ToolController _myToolController;
	protected CurveToolController _myCurveToolController;
    
	protected int _myRaster = 5;
	
	public TrackContext() {
		_myZoomController = new CCZoomController();
		_myToolController = new ToolController(this);
		_myCurveToolController = new CurveToolController(this);
		
		_myZoomController.addZoomable(this);

        _myLowerBound = 0;
        _myUpperBound = 1;
	}
	
	public CurveToolController curveTool() {
		return _myCurveToolController;
	}
	
	public double defaultValue(TrackController theTrackController) {
		return 0;
	}

	/**
	 * Raster resolution to align the track data
	 * @param theRaster Raster resolution to align the track data
	 */
	public void raster(final int theRaster){
		_myRaster = Math.max(0,theRaster);
	}
	
	/**
	 * Raster resolution to align the track data
	 * @return Raster resolution to align the track data
	 */
	public int raster(){
		return _myRaster;
	}
	
	/**
	 * Snaps the time of the given point to the raster of this context. This is called quantization.
	 * @param thePoint
	 * @return
	 */
	public ControlPoint snapToRaster(ControlPoint thePoint) {
    	if(_myRaster == 0) {
    		return thePoint;
    	}
        double myTime = Math.round(thePoint.time() / _myRaster) * _myRaster;
        thePoint.time(myTime);
        return thePoint;
	}

	public double snapToRaster(double theTime) {
		if (_myRaster == 0) {
			return theTime;
		}
		return Math.round(theTime / _myRaster) * _myRaster;
	}
    
    public double lowerBound() {
    	return _myLowerBound;
    }
    
    public double upperBound() {
    	return _myUpperBound;
    }
    
    public double viewTime() {
    	return _myUpperBound - _myLowerBound;
    }

	/**
	 * Controller for track zooming
	 * @return
	 */
	public CCZoomController zoomController() {
		return _myZoomController;
	}

	@Override
	public void setRange(double theLowerBound, double theUpperBound) {
        if (theLowerBound > theUpperBound) {
            double tmp = theLowerBound;
            theLowerBound = theUpperBound;
            theUpperBound = tmp;
        }
        _myLowerBound = theLowerBound;
        _myUpperBound = theUpperBound;
	}
	
	public void render(){
		
	}
}
