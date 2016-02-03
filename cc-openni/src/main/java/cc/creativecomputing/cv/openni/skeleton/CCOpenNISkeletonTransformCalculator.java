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
package cc.creativecomputing.cv.openni.skeleton;

import cc.creativecomputing.math.signal.filter.CCFilterManager;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.skeleton.CCSkeletonTransformCalculator;

/**
 * @author christianriekoff
 * 
 */
public class CCOpenNISkeletonTransformCalculator extends CCSkeletonTransformCalculator{

	public CCOpenNISkeletonTransformCalculator(CCSkeleton theSkeleton, CCFilterManager theFilter) {
		super(theSkeleton, theFilter);
	}

	

}
