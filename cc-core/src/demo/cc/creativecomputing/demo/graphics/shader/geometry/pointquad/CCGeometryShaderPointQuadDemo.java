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
package cc.creativecomputing.demo.graphics.shader.geometry.pointquad;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCGLSLShader.CCGeometryInputType;
import cc.creativecomputing.graphics.shader.CCGLSLShader.CCGeometryOutputType;
import cc.creativecomputing.math.util.CCArcball;

public class CCGeometryShaderPointQuadDemo extends CCApp {
	
	private CCGLSLShader _myShader;
	private CCArcball _myArcball;
	
	@CCControl(name = "thickness", min = 0, max = 30)
	private float _cThickness = 10;
	
	@Override
	public void setup() {
//		frameRate(10);
		
		_myShader = CCGLSLShader.createFromResource(this);
		_myShader.geometryInputType(CCGeometryInputType.POINTS);
		_myShader.geometryOutputType(CCGeometryOutputType.TRIANGLE_STRIP);
		_myShader.geometryVerticesOut(_myShader.maximumGeometryOutputVertices());
		_myShader.load();
		
		_myArcball = new CCArcball(this);
	}



	@Override
	public void update(final float theDeltaTime) {
//		_myMesh.clearAll();
//		for(int i = 0; i < 200;i++) {
//			_myMesh.addVertex(
//				CCMath.random(-width*2, width*2), 
//				CCMath.random(-width*2, width*2),
//				CCMath.random(-width*2, width*2)
//			);
//		}
	}

	@Override
	public void draw() {
		//Set the clear color (black)
		g.clearColor(0.0f,0.0f,0.0f,1.0f);
		//Clear the color buffer
		g.clear();

		//stretch to screen
		_myArcball.draw(g);
		//Draw a single triangle
		//geometry shader applies normal and creates rest of the geometry
//		g.noDepthTest();
//		g.polygonMode(CCPolygonMode.LINE);
		
		_myShader.start();
		g.point(0,0);
		_myShader.end();
		
//		g.polygonMode(CCPolygonMode.FILL);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGeometryShaderPointQuadDemo.class);
		myManager.settings().size(600, 600);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

