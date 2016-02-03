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

import cc.creativecomputing.timeline.controller.TrackDataController;
import cc.creativecomputing.timeline.model.points.ControlPoint;

/**
 * @author christianriekoff
 *
 */
public class AddControlPointAction implements Action{
	
	private TrackDataController _myTrackDataController;
	private ControlPoint _myControlPoint;
	
	public AddControlPointAction(TrackDataController theTrackDataController, ControlPoint theControlPoint) {
		_myTrackDataController = theTrackDataController;
		_myControlPoint = theControlPoint;
	}

	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.actions.Action#apply()
	 */
	@Override
	public void apply() {
		_myTrackDataController.trackData().add(_myControlPoint);
		_myTrackDataController.trackDataView().render();
	}

	/* (non-Javadoc)
	 * @see de.artcom.timeline.model.actions.Action#undo()
	 */
	@Override
	public void undo() {
		_myTrackDataController.trackData().remove(_myControlPoint);
		_myTrackDataController.trackDataView().render();
	}

}
