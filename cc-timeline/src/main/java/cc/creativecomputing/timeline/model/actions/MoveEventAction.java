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
package cc.creativecomputing.timeline.model.actions;

import cc.creativecomputing.timeline.controller.EventTrackController;
import cc.creativecomputing.timeline.controller.TrackDataController;
import cc.creativecomputing.timeline.model.points.ControlPoint;
import cc.creativecomputing.timeline.model.points.TimedEventPoint;

/**
 * @author christianriekoff
 *
 */
public class MoveEventAction implements Action{
	
	private TrackDataController _myEventTrackController;
	private TimedEventPoint _myEventPoint;
	private ControlPoint _myEndControlPoint;
	private ControlPoint _myStartControlPoint;
	private double _myEventStartEnd;
	private double _myEventEndEnd;
	
	public MoveEventAction(
		EventTrackController theEventTrackController, 
		TimedEventPoint theEventPoint, 
		ControlPoint theStartControlPoint, 
		ControlPoint theEndControlPoint,
		double theEventStartEnd,
		double theEventEndEnd
	) {
		_myEventTrackController = theEventTrackController;
		_myEventPoint = theEventPoint;
		_myStartControlPoint = theStartControlPoint.clone();
		_myEndControlPoint = theEndControlPoint.clone();
		_myEventStartEnd = theEventStartEnd;
		_myEventEndEnd = theEventEndEnd;
	}

	@Override
	public void apply() {
		_myEventTrackController.trackData().move(_myEventPoint, _myEndControlPoint);
		_myEventPoint.endTime(_myEventEndEnd);
		_myEventTrackController.trackDataView().render();
	}

	@Override
	public void undo() {
		_myEventTrackController.trackData().move(_myEventPoint, _myStartControlPoint);
		_myEventPoint.endTime(_myEventStartEnd);
		_myEventTrackController.trackDataView().render();
	}

}
