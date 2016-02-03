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
package cc.creativecomputing.protocol.proxymatrix;

import cc.creativecomputing.protocol.proxymatrix.filter.CCIRasterFilter;

public class CCSimpleBackgroundSubtractionOld implements CCIRasterFilter {
	
	CCPixelRaster _myBackground = null;
	
	public void setBackgroundRaster(CCPixelRaster theBackground) {
		_myBackground = theBackground;
	}

	public CCPixelRaster filter(CCPixelRaster theRaster) {
		if (_myBackground == null) {
			return theRaster.clone();
		}
		CCPixelRaster myResult = theRaster.clone();
		float[] myRasterData = theRaster.data();
		float[] myBGData = _myBackground.data();
		int myLength = theRaster.width() * theRaster.height();
		for (int i = 0; i < myLength; i++) {
			myResult.data()[i] = (1-myBGData[i]/255.0f) * (myRasterData[i] - myBGData[i]);
		}
		return myResult;
	}

}
