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
package cc.creativecomputing.demo.graphics.shader.imaging;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.shader.imaging.CCGPUEdgeBlender;
import cc.creativecomputing.graphics.shader.imaging.CCGPUEdgeBlender.CCGPUEdgeBlendDirection;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;

public class CCGPUEdgeBlendTest extends CCApp {
	
	
	private CCGPUEdgeBlender _myEdgeBlender;
	private CCTexture2D _myBlendTexture;

	@Override
	public void setup() {
		_myBlendTexture = new CCTexture2D(CCTextureIO.newTextureData("demo/gpu/imaging/test1.jpg"),CCTextureTarget.TEXTURE_2D);
		_myEdgeBlender = new CCGPUEdgeBlender(g, _myBlendTexture,CCGPUEdgeBlendDirection.VERTICAL);
		addControls("settings", "edgeblender", _myEdgeBlender);
	}

	@Override
	public void update(final float theDeltaTime) {
		
	}

	@Override
	public void draw() {
		g.clear();
//		g.polygonMode(CCPolygonMode.LINE);
		
		_myEdgeBlender.draw(g);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUEdgeBlendTest.class);
		myManager.settings().size(1200, 600);
		myManager.start();
	}
}

