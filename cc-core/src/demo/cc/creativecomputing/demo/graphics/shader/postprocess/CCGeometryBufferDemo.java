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
package cc.creativecomputing.demo.graphics.shader.postprocess;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.postprocess.CCGeometryBuffer;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.util.logging.CCLog;

public class CCGeometryBufferDemo extends CCApp {
	
	private class Box{
		private float size;
		private CCVector3f position;
		
		private Box() {
			size = CCMath.random(15, 75);
			position = CCVecMath.random(-width/2, width/2, -height/2, height/2, -width/2, width/2);
		}
		
		void draw() {
			g.pushMatrix();
			g.translate(position);
			g.box(size);
			g.popMatrix();
		}
	}
	
	private CCGeometryBuffer _myRenderContext;
	
	private CCArcball _myArcball;
	
	private List<Box> _myBoxes = new ArrayList<Box>();

	public void setup() {
		
		CCLog.info(g.camera().near() +":" + g.camera().far());
		
		_myRenderContext = new CCGeometryBuffer(g,width,height);
		g.camera().far(1000);
		
		for(int i = 0; i< 100;i++) {
			_myBoxes.add(new Box());
		}
		CCGraphics.debug();
		
		_myArcball = new CCArcball(this);
		
		addControls("app", "cam", this);
	}
	
	public void update(final float theDeltaTime) {
//		g.camera().near(_cNearClip);
//		g.camera().far(_cFarClip);
	}

	public void draw() {
		g.clear();
		_myRenderContext.beginDraw();
		g.clearColor(0, 255);
		g.clear();
		_myArcball.draw(g);
		for(Box myBox:_myBoxes) {
			myBox.draw();
		}
		_myRenderContext.endDraw();
		
		g.texture(0,_myRenderContext.positions());
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords(0f, 0f);
		g.vertex(-width / 2, -height / 2, 0);
		g.textureCoords(0f, 1f);
		g.vertex(-width / 2, height / 2, 0);
		g.textureCoords(1f, 1f);
		g.vertex(width / 2, height / 2, 0);
		g.textureCoords(1f, 0f);
		g.vertex(width / 2, -height / 2, 0);
		g.endShape();
		g.noTexture();
		
//		_myRenderContext.renderTexture().bindDepthTexture();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGeometryBufferDemo.class);
		myManager.settings().size(500, 500);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
