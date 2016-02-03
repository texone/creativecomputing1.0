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
package cc.creativecomputing.math;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;

public class CCPolygon2f implements Iterable<CCVector2f>{
	
	private List<CCVector2f> _myVertices = new ArrayList<CCVector2f>();
	
	public CCPolygon2f(){
		
	}
	
	public CCPolygon2f(CCVector2f...theVertices){
		for(CCVector2f myPoint:theVertices){
			addVertex(myPoint);
		}
	}
	
	public void addVertex(final float theX, final float theY){
		_myVertices.add(new CCVector2f(theX,theY));
	}
	
	public void addVertex(final CCVector2f theVertex){
		_myVertices.add(theVertex);
	}
	
	public CCVector2f vertex(int theID) {
		return _myVertices.get(theID);
	}
	
	private float angle2D(final float theX1, final float theY1, final float theX2, final float theY2){
		float theta1 = (float)Math.atan2(theY1, theX1);
		float theta2 = (float)Math.atan2(theY2, theX2);
		float dtheta = theta2 - theta1;

		while (dtheta > CCMath.PI)
			dtheta -= CCMath.TWO_PI;
		while (dtheta < -CCMath.PI)
			dtheta += CCMath.TWO_PI;

		return dtheta;
	}
	
	public boolean isInShape(final CCVector2f theVertex){
		return isInShape(theVertex.x, theVertex.y);
	}
	
	public boolean isInShape(final float theX, final float theY){
		float R = 0;

		for (int i = 0; i < _myVertices.size(); i++){
			float p1x = _myVertices.get(i).x - theX;
			float p1y = _myVertices.get(i).y - theY;
			float p2x = _myVertices.get((i + 1) % _myVertices.size()).x - theX;
			float p2y = _myVertices.get((i + 1) % _myVertices.size()).y - theY;

			R += angle2D(p1x, p1y, p2x, p2y);
		}

		if (CCMath.abs(R) < CCMath.PI)
			return false;
		else
			return true;
	}

	public float signedArea() {
		float area = 0;

		for (int i = 0; i < _myVertices.size(); i++) {
			int j = (i + 1) % _myVertices.size();
			CCVector2f myA = _myVertices.get(i);
			CCVector2f myB = _myVertices.get(j);
			area += myA.x * myB.y;
			area -= myA.y * myB.x;
		}
		area /= 2.0;

		return area;
	}
	
	public float area() {
		return CCMath.abs(signedArea());
	}

	public CCVector2f centroid() {
		float cx = 0, cy = 0;

		float factor = 0;
		
		for (int i = 0; i < _myVertices.size(); i++) {
			int j = (i + 1) % _myVertices.size();
			CCVector2f myA = _myVertices.get(i);
			CCVector2f myB = _myVertices.get(j);
			factor = (myA.x * myB.y - myB.x * myA.y);
			cx += (myA.x + myB.x) * factor;
			cy += (myA.y + myB.y) * factor;
		}
		factor = 1 / (signedArea() * 6);
		cx *= factor;
		cy *= factor;
		
		return new CCVector2f(cx, cy);
	}
	
	public void draw(final CCGraphics g){
		g.beginShape();
		for(CCVector2f myVertex:_myVertices){
			g.vertex(myVertex);
		}
		g.endShape();
	}

	public Iterator<CCVector2f> iterator() {
		return _myVertices.iterator();
	}
	
	public List<CCVector2f> vertices(){
		return _myVertices;
	}
}
