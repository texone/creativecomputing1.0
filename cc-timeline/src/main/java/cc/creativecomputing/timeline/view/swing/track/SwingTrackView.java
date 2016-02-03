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
package cc.creativecomputing.timeline.view.swing.track;

import java.awt.Color;

import cc.creativecomputing.timeline.controller.TimelineController;
import cc.creativecomputing.timeline.controller.TrackController;
import cc.creativecomputing.timeline.view.TrackDataView;
import cc.creativecomputing.timeline.view.TrackView;
import cc.creativecomputing.timeline.view.swing.SwingToolChooserPopup;


/**
 * @author christianriekoff
 *
 */
public class SwingTrackView implements TrackView{
	
	private SwingTrackDataView _myDataView;
	private SwingTrackControlView _myControlView;
	
	public SwingTrackView(
		SwingToolChooserPopup theToolChooserPopUp, 
    	SwingTrackDataRenderer theDataRenderer,
		TimelineController theTimelineController,
		TrackController theController
	) {
		_myControlView = new SwingTrackControlView(theTimelineController, theController);
		_myDataView = new SwingTrackDataView(theToolChooserPopUp, theDataRenderer, theTimelineController, theController);
	}
	
	public SwingTrackDataView dataView() {
		return _myDataView;
	}
	
	public SwingTrackControlView controlView() {
		return _myControlView;
	}

	@Override
	public void address(String theAddress) {
		_myControlView.address(theAddress);
	}

	/* (non-Javadoc)
	 * @see de.artcom.timeline.view.TrackView#color(java.awt.Color)
	 */
	@Override
	public void color(Color theColor) {
		_myControlView.color(theColor);
		_myDataView.color(theColor);
	}

	/* (non-Javadoc)
	 * @see de.artcom.timeline.view.TrackView#mute(boolean)
	 */
	@Override
	public void mute(boolean theIsMuted) {
		_myControlView.mute(theIsMuted);
	}

	/* (non-Javadoc)
	 * @see de.artcom.timeline.view.TrackView#render()
	 */
	@Override
	public void render() {
		_myDataView.render();
	}

	/* (non-Javadoc)
	 * @see de.artcom.timeline.view.TrackView#update()
	 */
	@Override
	public void update() {
		_myDataView.update();
	}

	/* (non-Javadoc)
	 * @see de.artcom.timeline.view.TrackView#value(double)
	 */
	@Override
	public void value(double theValue) {
		_myControlView.value(theValue);
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.timeline.view.TrackView#trackDataView()
	 */
	@Override
	public TrackDataView trackDataView() {
		return _myDataView;
	}
}
