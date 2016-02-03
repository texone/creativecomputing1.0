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
package cc.creativecomputing.timeline.view;

import java.awt.Color;

/**
 * @author christianriekoff
 *
 */
public interface TrackView {//extends TrackDataView{
    public static final double PICK_RADIUS = 10;
    public static final double GRID_INTERVAL = 5; // curve is calculated every GRID_INTERVAL points

	public void update();
	
	public void color(Color theColor);
	
	public void mute(boolean theIsMuted);
	
	public void address(final String theAdress);
	
	public void value(final double theValue);
	
	public void render();
	
	public TrackDataView trackDataView();
}
