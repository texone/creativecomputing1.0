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
package cc.creativecomputing.simulation.particles.forces;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCTesselator;
import cc.creativecomputing.graphics.shader.CCCGShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.simulation.particles.CCGPUParticles;

/**
 * @author christian riekoff
 *
 */
public class CCGPUPathFollow extends CCGPUTextureForceField{
	
	public static class CCGPUPath{
		
		private List<CCVector2f> _myPoints = new ArrayList<CCVector2f>();
		private List<CCVector2f> _myDirections = new ArrayList<CCVector2f>(); 
		
		private float _myContourForce = 0.1f;
		private float _myAreaForce = 1f;
		
		private boolean _myHasChanged = true;
		
		private float _myContourWeight = 1f;
		
		public CCGPUPath() {
		}
		
		public void clear() {
			_myPoints.clear();
			_myDirections.clear();
			
			_myHasChanged = true;
		}
		
		public void contourWeight(float theContourWeight){
			_myContourWeight = theContourWeight;
		}
		
		public void contourForce(final float theContourForce) {
			_myContourForce = theContourForce;
		}
		
		public void areaForce(final float theAreaForce) {
			_myAreaForce = theAreaForce;
		}
		
		public void addPoint(final CCVector2f thePoint) {
			_myPoints.add(thePoint);
		}
		
		public void draw(CCTesselator theTesselator) {
			if(_myHasChanged) {
				for(int i = 0; i < _myPoints.size() - 1;i++) {
					CCVector2f myPoint1 = _myPoints.get(i);
					CCVector2f myPoint2 = _myPoints.get(i + 1);
					CCVector2f myDirection  = CCVecMath.subtract(myPoint2, myPoint1).normalize();
					
					_myDirections.add(myDirection);
				}
				_myDirections.add(_myDirections.get(_myDirections.size()-1).clone());
			}
			
			
			theTesselator.beginContour();
			for(int i = 0; i < _myPoints.size();i++) {
				CCVector2f myDirection = _myDirections.get(i);
				CCVector2f myPoint = _myPoints.get(i);
				
				theTesselator.normal(myDirection.y * _myAreaForce, -myDirection.x * _myAreaForce, 0);
				theTesselator.vertex(myPoint.x - myDirection.y, myPoint.y + myDirection.x);
			}
			for(int i = _myPoints.size() - 1; i >= 0;i--) {
				CCVector2f myDirection = _myDirections.get(i);
				CCVector2f myPoint = _myPoints.get(i);
				
				theTesselator.normal(-myDirection.y * _myAreaForce, myDirection.x * _myAreaForce, 0);
				theTesselator.vertex(myPoint.x + myDirection.y, myPoint.y - myDirection.x);
			}
			theTesselator.endContour();
		}
		
		public void drawContour(CCGraphics g) {
			g.color(255,0,0);
			g.beginShape(CCDrawMode.TRIANGLE_STRIP);
			for(int i = 0; i < _myPoints.size();i++) {
				CCVector2f myDirection = _myDirections.get(i);
				CCVector2f myPoint = _myPoints.get(i);
				
				g.normal(myDirection.x * _myContourForce, myDirection.y * _myContourForce, 0);
				g.vertex(myPoint.x - myDirection.y * _myContourWeight, myPoint.y + myDirection.x * _myContourWeight);
				g.vertex(myPoint.x + myDirection.y * _myContourWeight, myPoint.y - myDirection.x * _myContourWeight);
			}
			g.endShape();
		}
	}
	
	private CCShaderBuffer _myPathForceFieldTexture;
	
	private CCCGShader _myContourShader;
	
	private CCGraphics _myGraphics;
	private CCTesselator _myTesselator;
	
	private List<CCGPUPath> _myPaths = new ArrayList<>();

	/**
	 * @param theTexture
	 * @param theTextureScale
	 * @param theTextureOffset
	 */
	public CCGPUPathFollow(CCGraphics g, int theWidth, int theHeight, CCVector2f theTextureScale, CCVector2f theTextureOffset) {
		super(null, theTextureScale, theTextureOffset);
		_myPathForceFieldTexture = new CCShaderBuffer(theWidth, theHeight);
		_myTexture = _myPathForceFieldTexture.attachment(0);
		
		_myContourShader = new CCCGShader(
			CCIOUtil.classPath(CCGPUParticles.class,"shader/contour.vp"), 
			CCIOUtil.classPath(CCGPUParticles.class,"shader/contour.fp")
		);
		_myContourShader.load();
		
		_myGraphics = g;
		_myTesselator = new CCTesselator();
	}
	
	public CCGPUPathFollow(CCGraphics g, int theWidth, int theHeight){
		this(g, theWidth, theHeight, new CCVector2f(1f,1f), new CCVector2f(theWidth / 2, theHeight / 2));
	}
	
	public void addPath(CCGPUPath thePath){
		_myPaths.add(thePath);
	}

	@Override
	public void update(float theDeltaTime) {
		_myPathForceFieldTexture.beginDraw();
		_myGraphics.clearColor(0);
		_myGraphics.clear();
		_myContourShader.start();
		_myTesselator.beginPolygon();
		_myTesselator.beginContour();
		for (int i = 400; i >= 0; i -= 5) {
			_myTesselator.normal(0, 1, 0);
			_myTesselator.vertex(i, 0);
		}
//		for (int i = 0; i <= 400; i += 50) {
//			_myTesselator.normal(0, -1, 0);
//			_myTesselator.vertex(0, i);
//		}
		for (int i = 0; i <= 400; i += 5) {
			_myTesselator.normal(0, -1, 0);
			_myTesselator.vertex(i, 400);
		}
//		for (int i = 400; i >= 0; i -= 5) {
//			_myTesselator.normal(0, 1, 0);
//			_myTesselator.vertex(0, i);
//		}
		_myTesselator.endContour();
		for(CCGPUPath myPath:_myPaths){
			_myTesselator.beginContour();
			myPath.draw(_myTesselator);
			_myTesselator.endContour();
		}
		_myTesselator.endPolygon();
		for(CCGPUPath myPath:_myPaths){
			myPath.drawContour(_myGraphics);
		}
		
		_myContourShader.end();
		_myPathForceFieldTexture.endDraw();
	}
}
