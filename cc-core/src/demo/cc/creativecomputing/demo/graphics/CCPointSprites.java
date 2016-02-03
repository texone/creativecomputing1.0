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
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.util.CCArcball;

public class CCPointSprites extends CCApp {
	
	private CCArcball _myArcball;
	
	private CCMesh _myMesh;
	private CCTexture2D _myPointTexture;

	public void setup() {
		_myArcball = new CCArcball(this);
		
		_myPointTexture = new CCTexture2D(CCTextureIO.newTextureData("demo/graphics/cross.png"));
		
		g.pointSize(100);
		g.smooth();
		
		_myMesh = new CCVBOMesh(CCDrawMode.POINTS,10000);
		for(int i = 0; i < 10000;i++){
			_myMesh.addVertex(CCMath.random(-width/2, width/2), CCMath.random(-height/2,height/2), CCMath.random(-1000,1000));
		}
	}

	public void draw() {
		g.clear();
		
		_myArcball.draw(g);

		g.color(255,50);
		g.blend(CCBlendMode.ADD);
		g.noDepthMask();
		
		g.pointDistanceAttenuation(0f, 0.0f, 0.0001f );
	    g.pointSprite(_myPointTexture);
		_myMesh.draw(g);
		g.noPointSprite();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCPointSprites.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
