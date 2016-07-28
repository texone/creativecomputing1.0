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
package cc.creativecomputing.simulation.steering;

import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;


public class CCPolylinePathway extends CCPathway {

	public float radius;
	
	private CCVector3f _myPoints[];
	private float _myLengths[];
	private CCVector3f _myNormals[];
	
	private float segmentLength;
	private float segmentProjection;
	
	// vectors for internal calculations
	private CCVector3f local;
	private CCVector3f chosen;
	private CCVector3f segmentNormal;

	public CCPolylinePathway() {
		local = new CCVector3f();
		chosen = new CCVector3f();
		segmentNormal = new CCVector3f();
		_myPoints = new CCVector3f[0];
		radius = 100;
	}
	
	public void points(final List<CCVector3f> thePoints){
		_myPoints = new CCVector3f[thePoints.size()];
		int counter = 0;
		for(CCVector3f myPoint:thePoints){
			_myPoints[counter++] = myPoint;
		}
		cachePathStats();
	}
	
	public CCVector3f[] points(){
		return _myPoints;
	}
	
	@Override
	public int numberOfPoints(){
		return _myPoints.length;
	}

	protected void cachePathStats() {
		_myLengths = new float[_myPoints.length];
		_myNormals = new CCVector3f[_myPoints.length];
		
		for (int i = 0; i < _myPoints.length; i++) {
			int index = (i + 1) % _myPoints.length;
			_myNormals[index] = _myPoints[i].clone();
			_myNormals[index].subtract(_myPoints[index]);
			_myLengths[index] = _myNormals[index].length();
			_myNormals[index].scale(1.0F / _myLengths[index]);
		}

	}

	/**
	 * Returns the distance of a point to a segment defined by the two end points
	 * @param thePoint
	 * @param theEndPoint1
	 * @param theEndPoint2
	 * @return
	 */
	private float pointToSegmentDistance(
		final CCVector3f thePoint, 
		final CCVector3f theEndPoint1, 
		final CCVector3f theEndPoint2
	){
		local.set(thePoint);
		local.subtract(theEndPoint1);
		
		segmentProjection = segmentNormal.dot(local);
		
		if (segmentProjection < 0.0F) {
			chosen.set(theEndPoint1);
			segmentProjection = 0.0F;
			return thePoint.approximateDistance(theEndPoint1);
		}
		if (segmentProjection > segmentLength) {
			chosen.set(theEndPoint2);
			segmentProjection = segmentLength;
			return thePoint.approximateDistance(theEndPoint2);
		}
		
		chosen.set(segmentNormal);
		chosen.scale(segmentProjection);
		chosen.add(theEndPoint1);
		return thePoint.approximateDistance(chosen);
	}
	
	@Override
	public int mapPointToPath(
		final CCVector3f point, 
		final CCVector3f onPath, 
		final CCVector3f tangent
	) {
		return mapPointToPath(point,onPath,tangent,0,_myPoints.length);
	}

	@Override
	public int mapPointToPath(
		final CCVector3f point, 
		final CCVector3f onPath, 
		final CCVector3f tangent,
		final int start,
		final int numberOfPoints
	) {
		float minDistance = Float.MAX_VALUE;
		int result = -1;
		
		if(_myIsClosed){
			for (int i = start; i < start + numberOfPoints; i++) {
				int index = (i + 1) % _myPoints.length;
				segmentLength = _myLengths[index];
				segmentNormal = _myNormals[index];
				float d = pointToSegmentDistance(
						point, 
						_myPoints[i % _myPoints.length],
						_myPoints[index]);
				
				if (d < minDistance) {
					result = i;
					minDistance = d;
					onPath.set(chosen);
					tangent.set(segmentNormal);
				}
			}
		}else{
			for (int i = CCMath.max(start,1); i < _myPoints.length && i < start + numberOfPoints; i++) {
				segmentLength = _myLengths[i];
				segmentNormal = _myNormals[i];
				
				float d = pointToSegmentDistance(point, _myPoints[i - 1], _myPoints[i]);
				
				if (d < minDistance) {
					result = i;
					minDistance = d;
					onPath.set(chosen);
					tangent.set(segmentNormal);
				}
			}
		}
		return result;
	}

	@Override
	public CCVector3f mapPathDistanceToPoint(final float thePathDistance) {
		float remainingDistance = thePathDistance;
		
		final CCVector3f myResult = new CCVector3f();
		
		if(_myIsClosed){
			for (int i = 0; i < _myPoints.length; i++) {
				int index = (i + 1) % _myPoints.length;
				segmentLength = _myLengths[index];
				if (segmentLength < remainingDistance) {
					remainingDistance -= segmentLength;
				} else {
					float ratio = remainingDistance / segmentLength;
					myResult.set(_myPoints[i]);
					myResult.interpolate(ratio, _myPoints[index]);
					return myResult;
				}
			}
		}else{
			for (int i = 1; i < _myPoints.length; i++) {
				segmentLength = _myLengths[i];
				if (segmentLength < remainingDistance) {
					remainingDistance -= segmentLength;
				} else {
					float ratio = remainingDistance / segmentLength;
					myResult.set(_myPoints[i - 1]);
					myResult.interpolate(ratio, _myPoints[i]);
					return myResult;
				}
			}
		}
		
		return myResult;
	}

	@Override
	public float mapPointToPathDistance(final CCVector3f thePoint,final int start, final int numberOfPoints) {
		float minDistance = 3.402823E+038F;
		float segmentLengthTotal = 0.0F;
		float pathDistance = 0.0F;
		
		if(_myIsClosed){
			for (int i = start; i < numberOfPoints; i++) {
				int index = (i + 1) % _myPoints.length;
				segmentLength = _myLengths[index];
				segmentNormal = _myNormals[index];
				
				float d = pointToSegmentDistance(thePoint, _myPoints[i], _myPoints[index]);
				
				if (d < minDistance) {
					minDistance = d;
					pathDistance = segmentLengthTotal + segmentProjection;
				}
				segmentLengthTotal += segmentLength;
			}
		}else{
			for (int i = CCMath.max(start,1); i < _myPoints.length && i < start + numberOfPoints; i++) {
				segmentLength = _myLengths[i];
				segmentNormal = _myNormals[i];
				
				float d = pointToSegmentDistance(thePoint, _myPoints[i - 1], _myPoints[i]);
				
				if (d < minDistance) {
					minDistance = d;
					pathDistance = segmentLengthTotal + segmentProjection;
				}
				segmentLengthTotal += segmentLength;
			}
		}

		return pathDistance;
	}
	public float mapPointToPathDistance(final CCVector3f thePoint) {
		float minDistance = 3.402823E+038F;
		float segmentLengthTotal = 0.0F;
		float pathDistance = 0.0F;
		
		if(_myIsClosed){
			for (int i = 0; i < _myPoints.length; i++) {
				int index = (i + 1) % _myPoints.length;
				segmentLength = _myLengths[index];
				segmentNormal = _myNormals[index];
				
				float d = pointToSegmentDistance(thePoint, _myPoints[i], _myPoints[index]);
				
				if (d < minDistance) {
					minDistance = d;
					pathDistance = segmentLengthTotal + segmentProjection;
				}
				segmentLengthTotal += segmentLength;
			}
		}else{
			for (int i = 1; i < _myPoints.length; i++) {
				segmentLength = _myLengths[i];
				segmentNormal = _myNormals[i];
				
				float d = pointToSegmentDistance(thePoint, _myPoints[i - 1], _myPoints[i]);
				
				if (d < minDistance) {
					minDistance = d;
					pathDistance = segmentLengthTotal + segmentProjection;
				}
				segmentLengthTotal += segmentLength;
			}
		}

		return pathDistance;
	}
	
	
	
	@Override
	public CCVector3f point(int theIndex) {
		return _myPoints[theIndex];
	}

	public float distanceToEnd(final int theIndex){
		float myResult = 0;
		for (int i = theIndex; i < _myPoints.length; i++) {
			myResult += _myLengths[i];
		}
		return myResult;
	}
}
