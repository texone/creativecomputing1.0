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
package cc.creativecomputing.cv.openni;

import org.openni.Generator;
import org.openni.Point3D;

import cc.creativecomputing.math.CCVector3f;

/**
 * @author christianriekoff
 *
 */
abstract class CCOpenNIGenerator <GeneratorType extends Generator>{

	protected GeneratorType _myGenerator;
	
	CCOpenNIGenerator(CCOpenNI theOpenNI){
		_myGenerator = create(theOpenNI);
	}
	
	GeneratorType generator() {
		return _myGenerator;
	}
	
	abstract GeneratorType create(CCOpenNI theOpenNI);
	
	void update(float theDeltaTime) {};
	
	/**
	 * Converts an openNI Point3D to a CCVector3f object
	 * @param thePoint3D
	 * @return
	 */
	public CCVector3f convert(Point3D thePoint3D) {
		return new CCVector3f(thePoint3D.getX(), thePoint3D.getY(), thePoint3D.getZ());
	}
	
	/**
	 * Converts a CCVector3f object to an openNI Point3D
	 * @param thePoint3D
	 * @return
	 */
	public Point3D convert(CCVector3f thePoint) {
		return new Point3D(thePoint.x, thePoint.y, thePoint.z);
	}
}
