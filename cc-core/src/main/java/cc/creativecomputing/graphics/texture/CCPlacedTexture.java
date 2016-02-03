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
package cc.creativecomputing.graphics.texture;

import cc.creativecomputing.math.CCVector2f;

/**
 * Interface to indicate that a texture has parameters for positioning.
 * @author texone
 *
 */
public abstract class CCPlacedTexture extends CCTexture2D{
	/**
	 * Returns the offset of the texture
	 * @return
	 */
	public abstract CCVector2f offset();
}
