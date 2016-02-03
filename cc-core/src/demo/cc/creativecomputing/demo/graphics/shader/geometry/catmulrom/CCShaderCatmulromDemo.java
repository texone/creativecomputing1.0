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
package cc.creativecomputing.demo.graphics.shader.geometry.catmulrom;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.spline.CCCatmulRomSpline;
import cc.creativecomputing.math.util.CCArcball;

public class CCShaderCatmulromDemo extends CCApp {
	
	private CCGLSLShader _myShader;
	private CCArcball _myArcball;
	
	@CCControl(name = "tension", min = -3, max = 3)
	private float _cCurveTension;
	
	@CCControl(name = "variation", min = -5, max = 5)
	private float _cCurveVariation;
	
	@CCControl(name = "length", min = 0, max = 1)
	private float _cCurveLength;
	
	@CCControl(name = "speed", min = 0, max = 1)
	private float _cCurveSpeed;
	
	@CCControl(name = "alpha", min = 0, max = 1)
	private float _cCurveAlpha;
	
	@CCControl(name = "amount", min = 0, max = 1)
	private float _cCurveAmount = 0;
	
	@CCControl(name = "amount blend", min = 0, max = 1)
	private float _cCurveBlend = 0;
	
	@CCControl(name = "interpolation", min = 0, max = 1)
	private float _cInterpolation = 0;
	
	@CCControl(name = "thickness", min = 0, max = 30)
	private float _cThickness = 10;
	
	@CCControl(name = "draw source spline")
	private boolean _cDrawBaseSpline = false;
	
	private CCCatmulRomSpline _mySpline;
	
	private CCMesh _myMesh;
	
	private CCColor[] _myPalette = new CCColor[]{
		new CCColor(  0,  24, 168), // deutsche blue
		new CCColor(  0,  34,  68), // dark blue
		new CCColor(  0, 152, 219), // bright blue
		new CCColor(137, 150, 160), // mid grey
		new CCColor(224, 230, 230), // pale grey
		new CCColor(255, 255, 255), // white
	};
	
	@Override
	public void setup() {
//		frameRate(10);
		CCMath.randomSeed(0);
		_mySpline = new CCCatmulRomSpline(0.5f, false);
		for(int i = 0; i < 10;i++){
			_mySpline.addPoint(new CCVector3f(CCMath.random(-width/2, width/2), CCMath.random(-height/2, height/2)));
		}
		
		addControls("app", "app", this);
		
		_myShader = CCGLSLShader.createFromResource(this);
		_myShader.load();
		
		_myArcball = new CCArcball(this);
		
		int NUMBER_OF_LINES = 5000;
		int ELEMENTS_PER_LINE = 50;
		
		_myMesh = new CCMesh(CCDrawMode.LINES, NUMBER_OF_LINES * ELEMENTS_PER_LINE * 2);
		for(int i = 0; i < NUMBER_OF_LINES;i++) {
			float myStartBlend = CCMath.random();
			float myRandom = CCMath.random();
			float mySpeed = CCMath.random();
			CCColor myColor = _myPalette[(int)CCMath.random(_myPalette.length)];
			myColor.a = 0.15f;
			for(int j = 0; j < ELEMENTS_PER_LINE;j++) {
				_myMesh.addColor(myColor);
				_myMesh.addVertex(myStartBlend, myRandom, mySpeed, 1 - ((float)j) / ELEMENTS_PER_LINE);
				_myMesh.addColor(myColor);
				_myMesh.addVertex(myStartBlend, myRandom, mySpeed, 1 - ((float)j + 1) / ELEMENTS_PER_LINE);
//				_myMesh.addVertex(1f / ELEMENTS_PER_LINE * j, 0, 0);
//				_myMesh.addVertex(1f / ELEMENTS_PER_LINE * (j + 1), 0, 0);
//				_myMesh.addVertex(0, j * 10, 0);
//				_myMesh.addVertex(100, j * 10, 0);
			}
		}
	}
	
	@CCControl(name = "close")
	public void closeCurve(boolean theIsClosed){
		_mySpline.isClosed(theIsClosed);
	}
	
	private float _myTime = 0;

	@Override
	public void update(final float theDeltaTime) {
		_mySpline.curveTension(_cCurveTension);
		_myTime += theDeltaTime * _cCurveSpeed;
	}
	
	private void drawSourceSpline(CCGraphics g){
		g.strokeWeight(1);
		g.color(255);
		_mySpline.draw(g);
		g.blend();
		g.pointSize(10);
		g.beginShape(CCDrawMode.POINTS);
		g.color(255,0,0);
		g.vertex(_mySpline.points().get(0));
		g.color(255);
		for(int i = 1; i < _mySpline.points().size() - 1;i++){
			g.vertex(_mySpline.points().get(i));
		}
		g.color(0,0,255);
		g.vertex(_mySpline.points().get(_mySpline.points().size() - 1));
		g.endShape();
		
		CCVector3f myInterploatedValue = _mySpline.interpolate(_cInterpolation);
		g.ellipse(myInterploatedValue, 10);
	}

	@Override
	public void draw() {
		g.clear();
		
		_myArcball.draw(g);
		
		if(_cDrawBaseSpline)drawSourceSpline(g);
		
		
//		g.blend(CCBlendMode.ADD);
		g.strokeWeight(_cThickness);
		g.noDepthTest();
		_myShader.start();
		_myShader.uniform4fv("splinePoints", _mySpline.curvePoints());
		_myShader.uniform1fv("splineLengths", _mySpline.segmentsLengths());
		_myShader.uniform1f("splineLength", _mySpline.totalLength());
		_myShader.uniform1i("splinePointsLength", _mySpline.points().size());
		
		_myShader.uniform1f("time", _myTime);
		_myShader.uniform1f("curveTension", _cCurveTension);
		_myShader.uniform1f("curveVariation", _cCurveVariation);
		_myShader.uniform1f("curveLength", _cCurveLength / 10);
		_myShader.uniform1f("curveAlpha", _cCurveAlpha);
		_myShader.uniform1f("curveAmountBlend", _cCurveBlend);
		_myShader.uniform1f("curveAmount", _cCurveAmount);
		
		_myMesh.draw(g);
		_myShader.end();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCShaderCatmulromDemo.class);
		myManager.settings().size(1400, 900);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

