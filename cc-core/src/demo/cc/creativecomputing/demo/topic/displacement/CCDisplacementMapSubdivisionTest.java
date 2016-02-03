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
package cc.creativecomputing.demo.topic.displacement;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCRenderBuffer;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCGLSLShader.CCGeometryInputType;
import cc.creativecomputing.graphics.shader.CCGLSLShader.CCGeometryOutputType;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;

public class CCDisplacementMapSubdivisionTest extends CCApp {
	
	private class CCGridQuad{
		private CCVector3f _myVector1;
		private CCVector3f _myVector2;
		private CCVector3f _myVector3;
		private CCVector3f _myVector4;
		
		private CCGridQuad(final CCVector3f theVector1, final CCVector3f theVector2, final CCVector3f theVector3, final CCVector3f theVector4){
			_myVector1 = theVector1;
			_myVector2 = theVector2;
			_myVector3 = theVector3;
			_myVector4 = theVector4;
		}
		
		private CCVector3f gridVector(final float theX, final float theY){
			CCVector3f myX1 = CCVecMath.blend(theX, _myVector1, _myVector4);
			CCVector3f myX2 = CCVecMath.blend(theX, _myVector2, _myVector3);
			return CCVecMath.blend(theY, myX1, myX2);
		}
	}
	
	private class DBLinesShader{
		
		private CCGLSLShader _myShader;
		
		private CCRenderBuffer _myFilterTexture;
		
		private float _myDisplacement;
		private float _myStrokeWeight;
		
		public DBLinesShader(CCGraphics g) {
			_myShader = new CCGLSLShader(
				CCIOUtil.classPath(this,"displacement_geometry/vertex.glsl"), 
				CCIOUtil.classPath(this,"displacement_geometry/geometry.glsl"), 
				CCIOUtil.classPath(this,"displacement_geometry/fragment.glsl")
			);
			_myShader.geometryInputType(CCGeometryInputType.LINES_ADJACENCY);
			_myShader.geometryOutputType(CCGeometryOutputType.TRIANGLE_STRIP);
			_myShader.geometryVerticesOut(_myShader.maximumGeometryOutputVertices());
			_myShader.load();
			
			_myFilterTexture = createFilterTexture(g, 100);
		}
		
		public void displacement(final float theDisplacement) {
			_myDisplacement = theDisplacement;
		}
		
		public void strokeWeight(final float theStrokeWeight) {
			_myStrokeWeight = theStrokeWeight;
		}
		
		/**
		 * Like the step function but provides a smooth transition from a to b
		 * using the cubic function 3x^2 - 2x^3.
		 * @param a
		 * @param b
		 * @param t
		 * @return
		 */
		private float smoothStep(float a, float b, float t) {
			if (t <= a)
				return 0;
			if (t >= b)
				return 1;
			t = (t - a) / (b - a); // normalize t to 0..1
			return t * t * (3 - 2 * t);
		}
		
		private CCRenderBuffer createFilterTexture(final CCGraphics g, final int theTextureSize) {
			CCRenderBuffer myFilterBuffer = new CCRenderBuffer(g, CCTextureTarget.TEXTURE_2D, theTextureSize, theTextureSize);
			myFilterBuffer.attachment(0).wrap(CCTextureWrap.MIRRORED_REPEAT);
			myFilterBuffer.attachment(0).textureFilter(CCTextureFilter.LINEAR);
			myFilterBuffer.beginDraw();
			g.clear();
			g.beginOrtho2D();
			g.beginShape(CCDrawMode.POINTS);
			// Fill in the filter texture
			for (int x = 0; x < theTextureSize; x++) {
				for (int y = 0; y < theTextureSize; y++) {
					float t = CCMath.sqrt(x * x + y * y) / theTextureSize;
					t = CCMath.min(t, 1.0f);
					t = CCMath.max(t, 0.0f);
					t = 1 - t;
					t = smoothStep(0.0f, 1.0f, t);
//					t = CCMath.pow(t, 1f);
					g.color(t);

//					g.color(1f);
					g.vertex(x,y);
				}
			}
			g.endShape();
			g.endOrtho2D();
			
			
		    myFilterBuffer.endDraw();
		    return myFilterBuffer;
		}

		/**
		 * 
		 */
		public void start() {
			g.texture(0,_myTexture);
			g.texture(1,_myFilterTexture.attachment(0));
			_myShader.start();
			_myShader.uniform1i("displacementTexture", 0);
			_myShader.uniform1f("displacement", _myDisplacement);
			_myShader.uniform1i("filterTexture", 1);
			_myShader.uniform1f("strokeWeight",_myStrokeWeight);
			_myShader.uniform1f("resolution",10f);
		}

		/**
		 * 
		 */
		public void end() {
			_myShader.end();
			g.noTexture();
		}
		
	}
	
	
	private CCGridQuad _myGridQuad;
	private CCMesh _myMesh;
	
	private CCTexture2D _myTexture;
	
	private DBLinesShader _myLinesShader;
	
	@CCControl(name="displacement", min = 0, max = 100)
	private float _cDisplacement = 10;
	
	@CCControl(name="strokeWeight", min = 0, max = 100)
	private float _cStrokeWeight = 10;

	public void setup(){
		_myTexture = new CCTexture2D(CCTextureIO.newTextureData("demo/particles/squarepusher.png"));
		addControls("line controls", "line controls",this);
	
		_myLinesShader = new DBLinesShader(g);
		createMesh();	
	}
	
	public void update(final float theDeltaTime){
		_myLinesShader.displacement(_cDisplacement);
		_myLinesShader.strokeWeight(_cStrokeWeight);
	}
	
	float _myScale = 1;
	float _myX = 0;
	float _myY = 0;
	
	public void draw(){
		g.clear();
	
		
//		g.clearColor(255);
//		g.color(255);
//		g.image(_myRenderTexture, -_myRenderTexture.width()/2, -_myRenderTexture.height()/2, _myRenderTexture.width(), _myRenderTexture.height());
//		g.polygonMode(CCPolygonMode.LINE);
		g.blend(CCBlendMode.ADD);
		g.noDepthTest();
		g.clearColor(0);
		g.color(255);
		g.translate(_myX, _myY);
		g.scale(_myScale);
		_myLinesShader.start();
		_myMesh.draw(g);
		_myLinesShader.end();
//		g.popAttribute();
		g.blend();
	}
	
	@Override
	public void keyPressed(final CCKeyEvent theEvent) {
		switch (theEvent.keyCode()) {
		case VK_LEFT:
			_myX += 10f;
			break;
		case VK_RIGHT:
			_myX -= 10f;
			break;

		case VK_UP:
			_myY += 10f;
			break;
		case VK_DOWN:
			_myY -= 10f;
			break;
			
		case VK_Q:
			_myScale += 0.1f;
			break;
		case VK_A:
			_myScale -= 0.1f;
			break;
		case VK_S:
//			CCScreenCapture.capture("export/deformed_text_"+CCFormatUtil.nf(counter++, 3)+".png", width,height);
			break;
		case VK_1:
			break;
		default:
		}
	}
	
	private void createMesh(){
		int _myYResolution = 50;
		int _myXResolution = 150;
		
		_myMesh = new CCMesh(CCDrawMode.LINES_ADJACENCY, _myXResolution * _myYResolution);
		
		_myGridQuad = new CCGridQuad(
				new CCVector3f(-width/2, height/2, 0), 
				new CCVector3f(-width/2, -height/2, 0), 
				new CCVector3f(width/2, -height/2, 0),
				new CCVector3f(width/2, height/2, 0)
			);

			CCGridQuad _myTextureQuad = new CCGridQuad(
				new CCVector3f(0, 0), 
				new CCVector3f(0, 1), 
				new CCVector3f(1,1f), 
				new CCVector3f(1, 0)
			);

		List<Integer> myIndices = new ArrayList<Integer>();
		int counter = 0;
		for (float y = 0; y < _myYResolution; y++) {
			for (float x = 0; x < _myXResolution; x++) {
				CCVector3f myVertex = _myGridQuad.gridVector(x / _myXResolution, y / _myYResolution);
				_myMesh.addVertex(myVertex);
				CCVector3f myTexCoords = _myTextureQuad.gridVector(x / _myXResolution, y / _myYResolution);
				_myMesh.addTextureCoords(myTexCoords.x, myTexCoords.y);
				if (x < _myXResolution - 3) {
					myIndices.add(counter);
					myIndices.add(counter + 1);
					myIndices.add(counter + 2);
					myIndices.add(counter + 3);
				}
				counter++;
			}
		}
		g.clearColor(255);
		g.color(0, 0.25f);
		g.strokeWeight(0.5f);
		_myMesh.indices(myIndices);
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCDisplacementMapSubdivisionTest.class);
		myManager.settings().size(800, 800);  //1920x1200
		myManager.settings().antialiasing(8);
//		myManager.settings().undecorated(true);
//		myManager.settings().antialiasing(32);
//		myManager.settings().displayMode(CCDisplayMode.FULLSCREEN);
		myManager.start();
	}
}
