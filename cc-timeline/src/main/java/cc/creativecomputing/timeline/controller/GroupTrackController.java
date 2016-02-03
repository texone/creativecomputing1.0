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
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.timeline.controller.tools.ToolController;
import cc.creativecomputing.timeline.model.GroupTrack;
import cc.creativecomputing.timeline.view.GroupTrackView;


/**
 * @author christianriekoff
 *
 */
public class GroupTrackController extends EventTrackController{

	private GroupTrack _myGroupTrack;
	private GroupTrackView _myGroupTrackView;
	
	private List<TrackController> _myTracks = new ArrayList<TrackController>();
	
	public GroupTrackController(
		TimelineController theTimelineController,
		ToolController theToolController,
		GroupTrack theGroupTrack
	) {
		super(theTimelineController, theToolController, theGroupTrack, null);
		_myParent = this;
		_myGroupTrack = theGroupTrack;
	}
	
	public void view(GroupTrackView theView) {
		_myGroupTrackView = theView;
		trackDataView(theView.trackDataView());
	}
	
	public void addTrack(TrackController theTrackController) {
		if(!_myTracks.contains(theTrackController))_myTracks.add(theTrackController);
		_myGroupTrack.addTrack(theTrackController.track());
	}
	
	public GroupTrack groupTrack() {
//		GroupTrack myGroupTrack = new GroupTrack();
//		myGroupTrack .setAddress(_myGroupTrack.address());
//		myGroupTrack.color(_myGroupTrack.color());
//		
//		for(TrackController myController:_myTracks) {
//			myGroupTrack.addTrack(myController.track());
//		}
		
		return _myGroupTrack;
	}
	
	public void mute(boolean theIsMuted) {
		for(TrackController myTrackController:trackController()) {
			myTrackController.mute(theIsMuted);
		}
	}
	
	public boolean isOpen(){
		return _myGroupTrack.isOpen();
	}
	
	public List<TrackController> trackController(){
		return _myTracks;
	}
	
	public void closeGroup() {
		if(!_myGroupTrack.isOpen())return;
		_myGroupTrack.isOpen(false);
		if(_myGroupTrackView!=null)_myGroupTrackView.closeGroup();
	}
	
	public void openGroup() {
		if(_myGroupTrack.isOpen())return;
		_myGroupTrack.isOpen(true);
		if(_myGroupTrackView!=null)_myGroupTrackView.openGroup();
	}

	/**
	 * @param theNewColor
	 */
	public void color(Color theNewColor) {
		_myGroupTrack.color(theNewColor);
		if(_myGroupTrackView!=null)_myGroupTrackView.color(theNewColor);
		for(TrackController myController:_myTracks) {
			myController.color(theNewColor);
		}
		
	}
	
	public void writeValues() {
		// TODO fix write values
//		for (TrackController myController : _myTracks) {
//			myController.writeValue();
//		}
	}
}
