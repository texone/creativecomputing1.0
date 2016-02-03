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
package cc.creativecomputing.demo.graphics;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCGraphics.CCStencilFunction;
import cc.creativecomputing.graphics.CCGraphics.CCStencilOperation;

public class CCStencilMaskDemo extends CCApp {

	@Override
	public void setup() {
		g.clearStencil(0);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clearStencil(0);
		g.clear();
		
		g.stencilTest();
		g.stencilFunc(CCStencilFunction.NEVER, 1, 1);
		g.stencilOperation(CCStencilOperation.REPLACE);
		
		g.noDepthTest();
		g.ellipse(0,0, 200,200);
		
		g.depthTest();
		g.stencilFunc(CCStencilFunction.EQUAL, 1, 1);
		g.stencilOperation(CCStencilOperation.KEEP);
		
		g.rect(0, 0, 200, 200);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCStencilMaskDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().stencilBits(8);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

