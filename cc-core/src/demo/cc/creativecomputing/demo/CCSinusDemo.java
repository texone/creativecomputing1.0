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
package cc.creativecomputing.demo;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.math.util.CCArcball;

public class CCSinusDemo extends CCApp {

	class CCBall {
		private CCVector2i _myGridPosition;
		
		private float _myRandom;

		CCBall(int theX, int theY) {
			_myGridPosition = new CCVector2i(theX, theY);
			_myRandom = CCMath.random();
		}
	}

	class CCGrid {

		private List<CCBall> _myBalls = new ArrayList<>();
		private float _mySpace;
		private float gridWidth;
		private float gridHeight;
		
		private float[] _myLineXRandoms;
		private float[] _myLineYRandoms;

		CCGrid(int theRexX, int theRexY, float theSpace) {
			_mySpace = theSpace;
			 gridWidth = theRexX * theSpace;
			 gridHeight = theRexY * theSpace;
			for (int x = 0; x <= theRexX; x++) {
				for (int y = 0; y < theRexY; y++) {
					_myBalls.add(new CCBall(x, y));
				}
			}
			
			_myLineXRandoms = new float[theRexX];
			_myLineYRandoms = new float[theRexY];
			
			for(int x = 0; x < theRexX; x++){
				_myLineXRandoms[x] = CCMath.random();
			}
			for(int y = 0; y < theRexY; y++){
				_myLineYRandoms[y] = CCMath.random();
			}
		}
		
		public void draw(){
			g.pushMatrix();
			// g.translate(width / 2, height / 2);
			g.rotateX(90);
			g.translate(-gridWidth / 2, -gridHeight / 2);

			g.pointSize(_cPointSize);
			
			g.beginShape(CCDrawMode.POINTS);
			for(CCBall myBall:_myBalls){
				float myAngle = CCMath.map(myBall._myGridPosition.x, 0, resX, 0, CCMath.TWO_PI);
				float myAmp = _cAmplitude + myBall._myRandom * _cBallRandom + _myLineYRandoms[myBall._myGridPosition.y] *_cYAmp;
				float myFreq = _cFreq + _myLineYRandoms[myBall._myGridPosition.y] *_cYFreq;
				float z = CCMath.sin(myAngle * myFreq + _myAngleOffset + _myLineYRandoms[myBall._myGridPosition.y] * _cYOffset) * myAmp;
				
				g.vertex(myBall._myGridPosition.x * _mySpace, myBall._myGridPosition.y * _mySpace, z);
			}
			
			g.endShape();
			g.popMatrix();
		}
	}

	int resX = 60;
	int resY = 30;

	@CCControl(name = "amplitude", min = 1, max = 500)
	float _cAmplitude = 60;

	@CCControl(name = "speed", min = 1, max = 30)
	float _cSpeed = 60;

	@CCControl(name = "freq", min = 1, max = 30)
	float _cFreq = 60;

	@CCControl(name = "pointSize", min = 1, max = 30)
	float _cPointSize = 60;
	
	@CCControl(name = "ball random", min = 0, max = 100)
	float _cBallRandom = 60;
	
	@CCControl(name = "x offset", min = 0, max = 2)
	float _cXOffset = 0;
	
	@CCControl(name = "y offset", min = 0, max = 2)
	float _cYOffset = 60;
	
	@CCControl(name = "y freq", min = 0, max = 2)
	float _cYFreq = 60;
	
	@CCControl(name = "y amp", min = 0, max = 100)
	float _cYAmp = 60;

	private CCGrid _myGrid;
	
	private CCArcball _myArcball;
	
	@Override
	public void setup() {
		_myGrid = new CCGrid(resX, resY, 20);
		addControls("app", "app", this);
		
		_myArcball = new CCArcball(this);
	}

	float _myAngleOffset = 0;
	float time = 0;

	@Override
	public void update(float theDeltaTime) {
		time += theDeltaTime;
		_myAngleOffset += theDeltaTime * _cSpeed;
	}

	@Override
	public void draw() {

		g.clearColor(255);
		g.clear();

		g.color(0);

		g.pushMatrix();
		_myArcball.draw(g);
		_myGrid.draw();
		g.popMatrix();
		
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCSinusDemo.class);
		myManager.settings().size(1500, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
