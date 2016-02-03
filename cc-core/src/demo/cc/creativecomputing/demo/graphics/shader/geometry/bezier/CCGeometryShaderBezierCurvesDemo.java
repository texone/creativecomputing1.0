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
package cc.creativecomputing.demo.graphics.shader.geometry.bezier;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCGLSLShader.CCGeometryInputType;
import cc.creativecomputing.graphics.shader.CCGLSLShader.CCGeometryOutputType;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.util.CCArcball;

public class CCGeometryShaderBezierCurvesDemo extends CCApp {
	
	private CCGLSLShader _myShader;
	private CCArcball _myArcball;
	private float _myScale = 0.5f;
	
	private class MovingPoint{
		private CCVector3f _myStartPosition;
		private CCVector3f _myEndPosition;
		private CCVector3f _myCurrentPosition;
		
		private float _myTime = 0;
		private float _myDuration = 10;
		
		public MovingPoint(){
			_myStartPosition = new CCVector3f(
					CCMath.random(-width * _myScale, width * _myScale), 
					CCMath.random(-width * _myScale, width * _myScale),
					CCMath.random(-width * _myScale, width * _myScale));
			_myEndPosition = new CCVector3f(
					CCMath.random(-width * _myScale, width * _myScale), 
					CCMath.random(-width * _myScale, width * _myScale),
					CCMath.random(-width * _myScale, width * _myScale));
			_myCurrentPosition = _myStartPosition.clone();
			
			_myDuration = CCMath.random(10);
		}
		
		public void update (final float theDeltaTime){
			_myTime += theDeltaTime;
			if(_myTime > _myDuration) {
				_myTime -= _myDuration;
				_myStartPosition = _myEndPosition;
				_myEndPosition = new CCVector3f(
					CCMath.random(-width * _myScale, width * _myScale), 
					CCMath.random(-width * _myScale, width * _myScale),
					CCMath.random(-width * _myScale, width * _myScale));
				_myDuration = CCMath.random(10);
			}
			_myCurrentPosition = CCVecMath.blend(_myTime/_myDuration, _myStartPosition, _myEndPosition);
		}
	}
	
	private List<MovingPoint> _myMovingPoints = new ArrayList<MovingPoint>();


	@Override
	public void setup() {
		
		_myShader = CCGLSLShader.createFromResource(this);
		_myShader.geometryInputType(CCGeometryInputType.LINES_ADJACENCY);
		_myShader.geometryOutputType(CCGeometryOutputType.TRIANGLE_STRIP);
		_myShader.geometryVerticesOut(_myShader.maximumGeometryOutputVertices());
		_myShader.load();
		
		for(int i = 0; i < 200;i++) {
			_myMovingPoints.add(new MovingPoint());
		}
		
		_myArcball = new CCArcball(this);
	}



	@Override
	public void update(final float theDeltaTime) {
		for(MovingPoint myMovingPoint:_myMovingPoints) {
			myMovingPoint.update(theDeltaTime);
		}
	}

	@Override
	public void draw() {
		g.clearColor(0);
		g.clear();
		_myArcball.draw(g);
		
		g.color(255,50);
		g.blend(CCBlendMode.ADD);
		g.noDepthTest();
		_myShader.start();
		g.beginShape(CCDrawMode.LINES_ADJACENCY);
		for(MovingPoint myMovingPoint:_myMovingPoints) {
			g.vertex(myMovingPoint._myCurrentPosition);
		}
		g.endShape();
		_myShader.end();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGeometryShaderBezierCurvesDemo.class);
		myManager.settings().size(1500, 900);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

