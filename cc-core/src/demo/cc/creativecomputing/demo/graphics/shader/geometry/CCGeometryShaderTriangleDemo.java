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
package cc.creativecomputing.demo.graphics.shader.geometry;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCGLSLShader.CCGeometryInputType;
import cc.creativecomputing.graphics.shader.CCGLSLShader.CCGeometryOutputType;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

public class CCGeometryShaderTriangleDemo extends CCApp {
	
	@CCControl(name = "red", min = 0, max = 1)
	private static float red = 0;
	@CCControl(name = "green", min = 0, max = 1)
	private static float green = 0;
	@CCControl(name = "blue", min = 0, max = 1)
	private static float blue = 0;
	
	@CCControl(name = "ambient red", min = 0, max = 1)
	private static float ared = 0;
	@CCControl(name = "ambient green", min = 0, max = 1)
	private static float agreen = 0;
	@CCControl(name = "ambient blue", min = 0, max = 1)
	private static float ablue = 0;

	
	@CCControl(name = "specular red", min = 0, max = 1)
	private static float sred = 0;
	@CCControl(name = "specular green", min = 0, max = 1)
	private static float sgreen = 0;
	@CCControl(name = "specular blue", min = 0, max = 1)
	private static float sblue = 0;
	@CCControl(name = "shininess", min = 0, max = 10)
	private static float shininess = 0;
	@CCControl(name = "normalScale", min = 0, max = 1)
	private static float normalScale = 0;
	

	@CCControl(name = "x", min = -1, max = 1)
	private static float x = 0;
	@CCControl(name = "y", min = -1, max = 1)
	private static float y = 0;
	@CCControl(name = "z", min = -1, max = 1)
	private static float z = 0;
	
	private CCGLSLShader _myShader;
	private CCMesh _myMesh;
	private CCArcball _myArcball;
	
	@CCControl(name = "thickness", min = 0, max = 30)
	private float _cThickness = 10;
	
	@Override
	public void setup() {
//		frameRate(10);
		
		_myShader = new CCGLSLShader(
			CCIOUtil.classPath (this,"shader/triangle_vertex.glsl"), 
			CCIOUtil.classPath (this,"shader/triangle_geometry.glsl"), 
			CCIOUtil.classPath (this,"shader/triangle_fragment.glsl")
		);
		_myShader.geometryInputType(CCGeometryInputType.TRIANGLES);
		_myShader.geometryOutputType(CCGeometryOutputType.TRIANGLE_STRIP);
		_myShader.geometryVerticesOut(_myShader.maximumGeometryOutputVertices());
		_myShader.load();
		
		_myArcball = new CCArcball(this);
		
		_myMesh = new CCVBOMesh(CCDrawMode.TRIANGLES, 10000 * 3);
		
		for(int i = 0; i < 10000;i++) {
			CCVector3f myVec1 = CCVecMath.random3f(CCMath.random(200));
			CCVector3f myVec2 = CCVecMath.add(myVec1, CCVecMath.random3f(CCMath.random(10)));
			CCVector3f myVec3 = CCVecMath.add(myVec1, CCVecMath.random3f(CCMath.random(10)));
			
			_myMesh.addVertex(myVec1);
			_myMesh.addVertex(myVec2);
			_myMesh.addVertex(myVec3);
		}
		
		addControls("triangle", "triangle", this);
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
		_myShader.uniform("diffuse", new CCColor(red, green ,blue));
		_myShader.uniform("ambient", new CCColor(ared, agreen ,ablue));
		_myShader.uniform("specular", new CCColor(sred, sgreen, sblue));
		_myShader.uniform1f("shininess", shininess);
		_myShader.uniform3f("lightDir", new CCVector3f(x,y,z).normalize());
		_myShader.uniform1f("thickness", _cThickness);
		_myMesh.draw(g);
		_myShader.end();
		
//		g.polygonMode(CCPolygonMode.FILL);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGeometryShaderTriangleDemo.class);
		myManager.settings().size(600, 600);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

