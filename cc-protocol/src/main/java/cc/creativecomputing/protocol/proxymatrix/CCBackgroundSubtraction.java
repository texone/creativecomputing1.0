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
import cc.creativecomputing.protocol.proxymatrix.filter.CCSimpleBackgroundSubtraction;


public class CCBackgroundSubtraction implements CCIRasterFilter {
	
	private CCPixelRaster _myBGRaster = null;
	private CCPixelRaster _mySumRaster = null;
	private CCSimpleBackgroundSubtraction _myBGFilter;
	
	private static int AVG_FRAMES = 3600; 
	
	
	public CCBackgroundSubtraction() {
		_myBGFilter = new CCSimpleBackgroundSubtraction();
	}

	public CCPixelRaster filter(CCPixelRaster theRaster) {
		if (_myBGRaster == null) {
			_myBGRaster = theRaster.clone();
			_mySumRaster = theRaster.clone();
			_myBGFilter.setBackgroundRaster(_myBGRaster);
		}
		
		_mySumRaster.add(theRaster.clone());
		_mySumRaster.scale(1/AVG_FRAMES);
		_myBGRaster = _mySumRaster.clone();
	
		return _myBGFilter.filter(theRaster);
	}

}
