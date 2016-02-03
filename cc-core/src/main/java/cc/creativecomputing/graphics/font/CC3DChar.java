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
package cc.creativecomputing.graphics.font;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDisplayList;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;

/**
 * @author info
 *
 */
public class CC3DChar extends CCVectorChar{

	private final List<List<CCVector2f>>_myContour = new ArrayList<List<CCVector2f>>();
	private List<CCVector2f>_myPath;
	
	private float _myDepth;
	
	private CCDisplayList _myDisplayList;
	
	/**
	 * @param theChar
	 */
	CC3DChar(char theChar, final int theGlyphCode, final float theWidth, final float theHeight, final float theSize, final float theDepth) {
		super(theChar, theGlyphCode, theWidth, theHeight,theSize);
		_myDepth = theDepth;
	}
	
	public void setDisplayList(CCGraphics g) {
		_myDisplayList = new CCDisplayList();
		_myDisplayList.beginRecord();
		
		g.beginShape(CCDrawMode.TRIANGLES);
		
		for(int i = 0; i < _myVertexCounter;i++){
			g.normal(0, 0, -1);
			g.vertex(
				_myVertices[i * 2], 
				-_myVertices[i * 2 + 1],
				-0.5f
			);
		}
		
		for(int i = _myVertexCounter - 1; i >= 0;i--){
			g.normal(0, 0, 1);
			g.vertex(
				_myVertices[i * 2], 
				-_myVertices[i * 2 + 1],
				0.5f
			);
		}
		
		for(List<CCVector2f> myPath:_myContour){
			for(int i = 0; i < myPath.size();i++) {
				CCVector2f myVertex1 = myPath.get(i);
				CCVector2f myVertex2 = myPath.get((i+1) % myPath.size());
				
				CCVector3f _myNormal = CCVecMath.normal(
					new CCVector3f(myVertex1.x, myVertex1.y, -0.5f),
					new CCVector3f(myVertex1.x, myVertex1.y, +0.5f),
					new CCVector3f(myVertex2.x, myVertex2.y, +0.5f)
				);
				g.normal(_myNormal);
				g.vertex(
					myVertex1.x, 
					-myVertex1.y,
					-0.5f
				);
				g.vertex(
					myVertex1.x, 
					-myVertex1.y,
					0.5f
				);
				g.vertex(
					myVertex2.x, 
					-myVertex2.y,
					0.5f
				);
				
				g.vertex(
					0 + myVertex1.x, 
					-myVertex1.y,
					-0.5f
				);
				g.vertex(
					myVertex2.x, 
					-myVertex2.y,
					0.5f
				);
				g.vertex(
					myVertex2.x, 
					-myVertex2.y,
					-0.5f
				);
			}
			
		}
		
		g.endShape();
		
		_myDisplayList.endRecord();
	}
	
	public void beginPath(){
		_myPath = new ArrayList<CCVector2f>();
	}
	
	public void addOutlineVertex(final float theX, final float theY){
		_myPath.add(new CCVector2f(theX, theY));
	}
	
	public void endPath(){
		_myContour.add(_myPath);
	}
	
	public List<List<CCVector2f>> contour(){
		return _myContour;
	}
	
	public void depth(final float theDepth) {
		_myDepth = theDepth;
	}

	/* (non-Javadoc)
	 * @see cc.creativecomputing.graphics.font.CCVectorChar#draw(cc.creativecomputing.graphics.CCGraphics, float, float, float, float)
	 */
	@Override
	public float draw(CCGraphics g, float theX, float theY, float theZ, float theSize) {
		if(_myDisplayList == null)setDisplayList(g);
		final float myScale = theSize / _mySize;
		
		g.pushMatrix();
		g.translate(theX, theY, theZ);
		g.scale(myScale,myScale,_myDepth);
		_myDisplayList.draw();
		g.popMatrix();

//		for(int i = 0; i < _myVertexCounter;i++){
//			g.normal(0, 0, -1);
//			g.vertex(
//				theX + _myVertices[i * 2] * myScale, 
//				theY - _myVertices[i * 2 + 1] * myScale,
//				theZ - _myDepth / 2
//			);
//		}
//		
//		for(int i = _myVertexCounter - 1; i >= 0;i--){
//			g.normal(0, 0, 1);
//			g.vertex(
//				theX + _myVertices[i * 2] * myScale, 
//				theY - _myVertices[i * 2 + 1] * myScale,
//				theZ + _myDepth / 2
//			);
//		}
//		
//		for(List<CCVector2f> myPath:_myContour){
//			for(int i = 0; i < myPath.size();i++) {
//				CCVector2f myVertex1 = myPath.get(i);
//				CCVector2f myVertex2 = myPath.get((i+1) % myPath.size());
//				
//				CCVector3f _myNormal = CCVecMath.normal(
//					new CCVector3f(myVertex1.x(), myVertex1.y, -_myDepth / 2),
//					new CCVector3f(myVertex1.x(), myVertex1.y, +_myDepth / 2),
//					new CCVector3f(myVertex2.x(), myVertex2.y, +_myDepth / 2)
//				);
//				g.normal(_myNormal);
//				g.vertex(
//					theX + myVertex1.x() * myScale, 
//					theY - myVertex1.y * myScale,
//					theZ - _myDepth / 2
//				);
//				g.vertex(
//					theX + myVertex1.x() * myScale, 
//					theY - myVertex1.y * myScale,
//					theZ + _myDepth / 2
//				);
//				g.vertex(
//					theX + myVertex2.x() * myScale, 
//					theY - myVertex2.y * myScale,
//					theZ + _myDepth / 2
//				);
//				
//				g.vertex(
//					theX + myVertex1.x() * myScale, 
//					theY - myVertex1.y * myScale,
//					theZ - _myDepth / 2
//				);
//				g.vertex(
//					theX + myVertex2.x() * myScale, 
//					theY - myVertex2.y * myScale,
//					theZ + _myDepth / 2
//				);
//				g.vertex(
//					theX + myVertex2.x() * myScale, 
//					theY - myVertex2.y * myScale,
//					theZ - _myDepth / 2
//				);
//			}
//		}
		return _myWidth * theSize;
	}
	
	
}
