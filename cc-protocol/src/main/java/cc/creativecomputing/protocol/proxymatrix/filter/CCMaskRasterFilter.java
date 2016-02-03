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

import cc.creativecomputing.protocol.proxymatrix.CCPixelRaster;

public class CCMaskRasterFilter implements CCIRasterFilter {

    CCPixelRaster _myMask = null; 

    public CCMaskRasterFilter( CCPixelRaster theMask ) {
        _myMask = theMask;
    }

	public CCPixelRaster filter(CCPixelRaster theRaster) {

        if (theRaster.width() != _myMask.width()
            && theRaster.height() != _myMask.height()) 
        {
            throw new RuntimeException("Input raster must have the same dimensions as the Mask");
        }

        CCPixelRaster myResult = theRaster.clone();
        int myLength = theRaster.width() * theRaster.height();
        float[] myMaskData = _myMask.data();
        float[] myRasterData = theRaster.data();
        for (int i = 0; i < myLength; i++) {
            myResult.data()[i] = (myMaskData[i]/255.0f) * myRasterData[i];
        }

        return myResult;

	}
}
