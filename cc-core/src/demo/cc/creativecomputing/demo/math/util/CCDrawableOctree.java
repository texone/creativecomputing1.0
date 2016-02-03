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
package cc.creativecomputing.demo.math.util;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCIOctreeElement;
import cc.creativecomputing.math.util.CCOctree;

/**
 * Extends the default octree class in order to visualize currently occupied cells in the tree.
 */
public class CCDrawableOctree<ElementType extends CCIOctreeElement> extends CCOctree<ElementType> {

	public CCDrawableOctree(CCVector3f theOrigin, float theDimension) {
		super(theOrigin, theDimension);
	}

	public void draw(CCGraphics g) {
		drawNode(this, g);
	}

	public void drawNode(CCOctree<ElementType> n, CCGraphics g) {
		if (n.getNumChildren() > 0) {
			g.color(depth(), 20);
			g.pushMatrix();
			g.translate(n.aabb().center());
			g.boxGrid(n.getNodeSize());
			g.popMatrix();
			CCOctree<ElementType>[] childNodes = n.getChildren();
			for (int i = 0; i < 8; i++) {
				if (childNodes[i] != null)
					drawNode(childNodes[i], g);
			}
		}
	}
}
