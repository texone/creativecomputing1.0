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
package cc.creativecomputing.demo.topic.fractal;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCRenderBuffer;
import cc.creativecomputing.graphics.CCGraphics.CCMatrixMode;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.util.CCGPUNoise;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.d.CCTriangle2d;
import cc.creativecomputing.math.d.CCVector2d;

import com.jogamp.opengl.cg.CGparameter;

/**
 * @author christianriekoff
 *
 */
public class CCFractalClouds{
	@CCControl(name = "red", min = 0, max = 1, external = true)
	public float _cRed = 1f;
	
	@CCControl(name = "green", min = 0, max = 1, external = true)
	public float _cGreen = 1f;
	
	@CCControl(name = "blue", min = 0, max = 1, external = true)
	public float _cBlue = 1f;
	
	@CCControl(name = "alpha", min = 0, max = 1, external = true)
	public float _cAlpha = 1f;
	
	@CCControl(name = "rotate", min = 0, max = 360, external = true)
	public float _cRotate = 1f;
	
	@CCControl(name = "translate x", min = -10, max = 10)
	public float _cTranslateX = 1f;
	
	@CCControl(name = "translate y", min = -10, max = 10)
	public float _cTranslateY = 1f;
	
	
	@CCControl(name = "texture shift x", min = -2, max = 2)
	public float _cTextureShiftX = 1f;
	
	@CCControl(name = "texture shift y", min = -2, max = 2)
	public float _cTextureShiftY = 1f;
	
	@CCControl(name = "move", min = 0, max = 10, external = true)
	private float _cMove = 1;
	
	@CCControl(name = "base noise scale", min = 0, max = 10, external = true)
	private float _cBaseNoiseScale = 1;
	
	@CCControl(name = "base noise pow", min = 0.1f, max = 10, external = true)
	private float _cBaseNoisePow = 1;
	
	@CCControl(name = "base noise amplify", min = 0f, max = 10)
	private float _cBaseNoiseAmplify = 1;
	
	@CCControl(name = "base noise shift", min = 0f, max = 10)
	private float _cBaseNoiseShift = 0;
	
	@CCControl(name = "base depth move x", min = 0f, max = 10)
	private float _cBaseDepthMoveX = 1;
	
	@CCControl(name = "base depth move y", min = 0f, max = 10)
	private float _cBaseDepthMoveY = 1;
	
	@CCControl(name = "base depth move z", min = 0f, max = 10)
	private float _cBaseDepthMoveZ = 1;
	

	@CCControl(name = "fractal noise scale", min = 0, max = 10)
	private float _cFractalNoiseScale = 1;
	
	@CCControl(name = "fractal noise pow", min = 0.1f, max = 10, external = true)
	private float _cFractalNoisePow = 1;
	
	@CCControl(name = "fractal depth move x", min = 0f, max = 1)
	private float _cFractalDepthMoveX = 1;
	
	@CCControl(name = "fractal depth move y", min = 0f, max = 1)
	private float _cFractalDepthMoveY = 1;
	
	@CCControl(name = "fractal depth move z", min = 0f, max = 1)
	private float _cFractalDepthMoveZ = 1;
	
	@CCControl(name = "result scale", min = 0f, max = 10)
	private float _cResultScale = 1;
	
	@CCControl(name = "result pow", min = 0.1f, max = 10)
	private float _cResultPow = 1;
	
	@CCControl(name = "result shift", min = -10f, max = 10, external = true)
	private float _cResultShift = 0;
	

	private CCRenderBuffer _myRenderTexture;
	@CCControl(name="mosaic", tabName = "clouds", column = 1)
	private CCMosaicTriangleMesh _myParticleTriangleMesh;
	
	private CGparameter _myMoveParameter;
	
	private CGparameter _myBaseNoiseScaleParameter;
	private CGparameter _myBaseNoisePowParameter;
	private CGparameter _myBaseNoiseAmplifyParameter;
	private CGparameter _myBaseNoiseShiftParameter;
	private CGparameter _myBaseDepthMoveParameter;
	
	private CGparameter _myFractalNoiseScaleParameter;
	private CGparameter _myFractalNoisePowParameter;
	private CGparameter _myFractalDepthMoveParameter;
	
	private CGparameter _myResultScaleParameter;
	private CGparameter _myResultPowParameter;
	private CGparameter _myResultShiftParameter;
	
	private CCCGShader _myDepthColorShader;
	
	private int _myWidth;
	private int _myHeight;
	
	public CCFractalClouds(CCGraphics g, int theWidth, int theHeight){
		_myWidth = theWidth;
		_myHeight = theHeight;
		
		_myDepthColorShader = new CCCGShader(
			CCIOUtil.classPath(this, "noisecloud.vp"),
			CCIOUtil.classPath(this, "noisecloud.fp")
		);
		_myDepthColorShader.load();
		
		CCGPUNoise.attachFragmentNoise(_myDepthColorShader);
		
		_myBaseNoiseScaleParameter = _myDepthColorShader.fragmentParameter("baseNoiseScale");
		_myBaseNoisePowParameter = _myDepthColorShader.fragmentParameter("baseNoisePow");
		_myBaseNoiseAmplifyParameter = _myDepthColorShader.fragmentParameter("baseNoiseAmplify");
		_myBaseNoiseShiftParameter = _myDepthColorShader.fragmentParameter("baseNoiseShift");
		_myBaseDepthMoveParameter = _myDepthColorShader.fragmentParameter("baseDepthMove");
		
		_myFractalNoiseScaleParameter = _myDepthColorShader.fragmentParameter("fractalNoiseScale");
		_myFractalNoisePowParameter = _myDepthColorShader.fragmentParameter("fractalNoisePow");
		_myFractalDepthMoveParameter = _myDepthColorShader.fragmentParameter("fractalDepthMove");
		
		_myMoveParameter = _myDepthColorShader.fragmentParameter("move");
		
		_myResultScaleParameter = _myDepthColorShader.fragmentParameter("resultScale");
		_myResultPowParameter = _myDepthColorShader.fragmentParameter("resultPow");
		_myResultShiftParameter = _myDepthColorShader.fragmentParameter("resultShift");
		
		_myRenderTexture = new CCRenderBuffer(g, _myWidth / 2, _myHeight / 2);
		
		int myColumns = 10;
		
		float myEdgelength = _myWidth / (float)myColumns;
		float myEdgeScale = myEdgelength / CCMath.SQRT3;
		float myTriangleHeight = myEdgelength / 2 * CCMath.SQRT3;
		float _myShortCenter = myEdgelength / 2 * CCMath.tan(CCMath.radians(30));
		float myLongCenter = myTriangleHeight - _myShortCenter;
		
		List<CCTriangle2d> myTriangles = new ArrayList<CCTriangle2d>();
		
		int myRows = CCMath.ceil(_myHeight / myTriangleHeight);
		
		for(int myColumn = 0; myColumn < myColumns + 1; myColumn++) {
			for(int myRow = 0; myRow < myRows;myRow++) {
				CCVector2d myOrigin0;
				CCVector2d myOrigin1;
				CCVector2d myOrigin2;

				myOrigin0 = new CCVector2d(CCMath.cos(CCMath.radians( -30)), CCMath.sin(CCMath.radians( -30))).scale(myEdgeScale);
				myOrigin1 = new CCVector2d(CCMath.cos(CCMath.radians(-150)), CCMath.sin(CCMath.radians(-150))).scale(myEdgeScale);
				myOrigin2 = new CCVector2d(CCMath.cos(CCMath.radians(-270)), CCMath.sin(CCMath.radians(-270))).scale(myEdgeScale);
					
				CCVector2d myTranslation = new CCVector2d(
					(myColumn) * myEdgelength,
					myRow * myTriangleHeight + _myShortCenter
				);
				
				myTriangles.add(new CCTriangle2d(
					myOrigin0.add(myTranslation),
					myOrigin1.add(myTranslation),
					myOrigin2.add(myTranslation)
				));
				
				myOrigin0 = new CCVector2d(CCMath.cos(CCMath.radians(-210)), CCMath.sin(CCMath.radians(-210))).scale(myEdgeScale);
				myOrigin1 = new CCVector2d(CCMath.cos(CCMath.radians(-330)), CCMath.sin(CCMath.radians(-330))).scale(myEdgeScale);
				myOrigin2 = new CCVector2d(CCMath.cos(CCMath.radians( -90)), CCMath.sin(CCMath.radians( -90))).scale(myEdgeScale);
					
				myTranslation = new CCVector2d(
					(myColumn + 0.5) * myEdgelength,
					myRow * myTriangleHeight + myLongCenter
				);
				
				myTriangles.add(new CCTriangle2d(
					myOrigin0.add(myTranslation),
					myOrigin1.add(myTranslation),
					myOrigin2.add(myTranslation)
				));
			}
		}
		
		_myParticleTriangleMesh = new CCMosaicTriangleMesh(g, myTriangles, 6);
		_myParticleTriangleMesh.textureSize(_myWidth, _myHeight);
		
		_myParticleTriangleMesh.texture0(_myRenderTexture.attachment(0));
		_myParticleTriangleMesh.texture1(_myRenderTexture.attachment(0));
	}
	
	public void time(double theTime) {
		
	}
	
	public void draw(CCGraphics g){
		if(_cAlpha == 0)return;
		_myRenderTexture.beginDraw();
		g.clearColor(0,0,0,0);
		g.clear();
		g.blend();
		
		g.matrixMode(CCMatrixMode.TEXTURE);
		g.pushMatrix();
		g.translate(_cTranslateX, _cTranslateY);
		g.rotate(_cRotate);
		g.translate(_cTextureShiftX, _cTextureShiftY);
		
		_myDepthColorShader.start();
		_myDepthColorShader.parameter(_myBaseNoiseScaleParameter, _cBaseNoiseScale);
		_myDepthColorShader.parameter(_myBaseNoisePowParameter, _cBaseNoisePow);
		_myDepthColorShader.parameter(_myBaseNoiseAmplifyParameter, _cBaseNoiseAmplify);
		_myDepthColorShader.parameter(_myBaseNoiseShiftParameter, _cBaseNoiseShift);
		_myDepthColorShader.parameter(
			_myBaseDepthMoveParameter, 
			_cBaseDepthMoveX, 
			_cBaseDepthMoveY, 
			_cBaseDepthMoveZ
		);

		_myDepthColorShader.parameter(_myFractalNoiseScaleParameter, _cFractalNoiseScale);
		_myDepthColorShader.parameter(_myFractalNoisePowParameter, _cFractalNoisePow);
		_myDepthColorShader.parameter(
			_myFractalDepthMoveParameter, 
			_cBaseDepthMoveX + _cFractalDepthMoveX, 
			_cBaseDepthMoveY + _cFractalDepthMoveY, 
			_cBaseDepthMoveZ + _cFractalDepthMoveZ
		);
		
		_myDepthColorShader.parameter(_myResultScaleParameter, _cResultScale);
		_myDepthColorShader.parameter(_myResultPowParameter, _cResultPow);
		_myDepthColorShader.parameter(_myResultShiftParameter, _cResultShift);
		
		_myDepthColorShader.parameter(_myMoveParameter, _cMove);
		g.blend();
		g.color(1f,_cAlpha);

		g.color(_cRed,_cGreen,_cBlue, _cAlpha);
			
			g.beginShape(CCDrawMode.QUADS);
			g.textureCoords(0, -0.5f, -0.5f);
			g.vertex(-_myWidth / 2, -_myHeight / 2);
			g.textureCoords(0, 0.5f, -0.5f);
			g.vertex( _myWidth / 2, -_myHeight / 2);
			g.textureCoords(0, 0.5f, 0.5f);
			g.vertex( _myWidth / 2,  _myHeight / 2);
			g.textureCoords(0, -0.5f, 0.5f);
			g.vertex(-_myWidth / 2,  _myHeight / 2);
			g.endShape();
		
		g.popMatrix();
		_myDepthColorShader.end();
		
		g.matrixMode(CCMatrixMode.MODELVIEW);
		_myRenderTexture.endDraw();

		g.pushMatrix();
		g.translate(-_myWidth/2, -_myHeight/2);
//		g.clear();
		// draw the "real" triangles with its full volume
		g.blend();
//		_myTriangleManager.draw(g);
		_myParticleTriangleMesh.draw(g);
		g.popMatrix();
	}
}
