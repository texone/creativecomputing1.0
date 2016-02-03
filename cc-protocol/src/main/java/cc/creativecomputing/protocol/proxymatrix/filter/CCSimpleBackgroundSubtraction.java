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
package cc.creativecomputing.protocol.proxymatrix.filter;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.protocol.proxymatrix.CCPixelRaster;

public class CCSimpleBackgroundSubtraction implements CCIRasterFilter {
	
	CCPixelRaster _myBackground = null;
	
	public void setBackgroundRaster(CCPixelRaster theBackground) {
		_myBackground = theBackground;
	}

	public CCPixelRaster filter(CCPixelRaster theRaster) {
		if (_myBackground == null) {
			_myBackground = theRaster.clone();
		}
		
		CCPixelRaster myResult = new CCPixelRaster("", theRaster.width(), theRaster.height());
		int myLength = theRaster.width() * theRaster.height();
		for (int i = 0; i < myLength; i++) {
			float myBackgroundSubpression = CCMath.abs(theRaster.data()[i] - _myBackground.data()[i]) / 255f;
			
			myResult.data()[i] = (255f - theRaster.data()[i]) * myBackgroundSubpression;
		}
		return myResult;
	}

}
