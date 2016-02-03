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
package cc.creativecomputing.demo.graphics.texture;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.texture.CCTexture1D;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.math.CCMath;

public class CCTexture1DTest extends CCApp {
	
	private CCTextureData _myTextureData;
	private CCTexture1D _myTexture;

	@Override
	public void setup() {
		_myTextureData = CCTextureIO.newTextureData("textures/1d_texture.png");
		
		_myTexture = new CCTexture1D();
		_myTexture.data(_myTextureData);
		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		g.texture(_myTexture);
		g.beginShape(CCDrawMode.TRIANGLE_FAN);
		g.textureCoords(0);
		g.vertex(0,0);
		for(int i=0;i <= 360;i++) {
			
			float myRadius = (CCMath.sin(CCMath.radians(i)*7) + 1) * 50 + 50;
			
			float myX = CCMath.sin(CCMath.radians(i)) * myRadius;
			float myY = CCMath.cos(CCMath.radians(i)) * myRadius;
			
			
			g.textureCoords(2);
			g.vertex(myX, myY);
		}
		g.endShape();
		g.noTexture();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTexture1DTest.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

