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
package cc.creativecomputing.graphics.util;

import cc.creativecomputing.math.CCVector3f;

public interface CCIFrustumWrapable {

	public int frustumMode(CCClipSpaceFrustum theFrustum);
	
	public CCVector3f frustumWrapPosition();
	
	public CCVector3f frustumWrapDimension();
	
	public void frustumWrap(CCVector3f theWrapVector);
}
