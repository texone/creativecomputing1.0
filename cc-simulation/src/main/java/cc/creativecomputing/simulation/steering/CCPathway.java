/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.simulation.steering;

import cc.creativecomputing.math.CCVector3f;

public abstract class CCPathway {
	
	protected boolean _myIsClosed = false;

	public abstract int mapPointToPath(CCVector3f point, CCVector3f onPath, CCVector3f tangent);

	public abstract int mapPointToPath(CCVector3f point, CCVector3f onPath, CCVector3f tangent,int start, int numberOfPoints);

	public abstract CCVector3f mapPathDistanceToPoint(final float theDistance);

	public abstract float mapPointToPathDistance(CCVector3f vector3);

	public abstract float mapPointToPathDistance(CCVector3f vector3,int start, int numberOfPoints);
	
	public abstract int numberOfPoints();
	
	public abstract float distanceToEnd(final int theIndex);
	
	public abstract CCVector3f point(int index);
	
	public abstract CCVector3f[] points();
	
	public void isClosed(final boolean theIsClosed){
		_myIsClosed = theIsClosed;
	}
	
	public boolean isClosed(){
		return _myIsClosed;
	}
	
}
