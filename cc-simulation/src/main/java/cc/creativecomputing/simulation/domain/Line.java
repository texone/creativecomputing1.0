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
package cc.creativecomputing.simulation.domain;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;

/**
 * Domain representing a line given the two end points
 * @author christianr
 *
 */
public class Line extends CCDomain{
	public CCVector3f p0;
	public CCVector3f p1;

	public Line(final CCVector3f i_p0, final CCVector3f i_p1){
		p0 = i_p0;
		p1 = i_p1;
		p1.subtract(p0);
	}

	public CCVector3f generate(){
		final CCVector3f result = p1.clone();
		result.scale(CCMath.random());
		result.add(p0);
		return result;
	}
}
