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
package cc.creativecomputing.demo.protocol.proxymatrix;

import cc.creativecomputing.protocol.proxymatrix.CCPixelRaster;
import cc.creativecomputing.protocol.proxymatrix.filter.CCIRasterFilter;

/**
 * The centerboard proxymatrix has an exotic indexing, which has to be
 * adjusted. Starting from a certain index the rows are shifted by a 
 * certain amount. 
 **/

public class CCProximatrixShiftFilter implements CCIRasterFilter {

    int _myStartIndex = 0;
    int _myShift = 0;

    public CCProximatrixShiftFilter( int theStartIndex, int theShift ) {
        _myStartIndex = theStartIndex;
        _myShift = theShift;
    }

	public CCPixelRaster filter(CCPixelRaster theRaster) {
        CCPixelRaster myResult = theRaster.clone();
        int myMaxRow = theRaster.height() - _myShift;
        int myLength = theRaster.width() - _myStartIndex;
        int myWidth = theRaster.width();
        for (int i = myMaxRow-1; i >= 0; i--) {
            System.arraycopy(myResult.data(), i * myWidth + _myStartIndex, 
                             myResult.data(), (i+_myShift) * myWidth + _myStartIndex, 
                             myLength);
        }
       return myResult;
	}


}
