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
import java.util.List;

/**
 * Sphere based on subdivision of the icosahedron. This gives better
 * spreaded vertices as for the normal sphere points get closer at the
 * poles.
 * @author christian riekoff
 *
 */
public class CCIcoSphere {

	// data to create an icosahedron
	private static final float X = 0.525731112119133606f;
	private static final float Z = 0.850650808352039932f;

	private static final CCIndexVertex vdata[] = new CCIndexVertex[]{    
	   new CCIndexVertex(0,-X, 0, Z), new CCIndexVertex(1, X, 0,  Z), new CCIndexVertex(2,-X,  0, -Z), new CCIndexVertex(3, X,  0, -Z),    
	   new CCIndexVertex(4, 0, Z, X), new CCIndexVertex(5, 0, Z, -X), new CCIndexVertex(6, 0, -Z,  X), new CCIndexVertex(7, 0, -Z, -X),    
	   new CCIndexVertex(8, Z, X, 0), new CCIndexVertex(9,-Z, X,  0), new CCIndexVertex(10, Z, -X,  0), new CCIndexVertex(11, -Z, -X,  0) 
	};

	private static final int tindices[][] = new int[][]{ 
	   {0,  4,  1}, {0, 9,  4}, {9,  5, 4}, { 4, 5, 8}, {4, 8,  1},    
	   {8, 10,  1}, {8, 3, 10}, {5,  3, 8}, { 5, 2, 3}, {2, 7,  3},    
	   {7, 10,  3}, {7, 6, 10}, {7, 11, 6}, {11, 0, 6}, {0, 1,  6}, 
	   {6,  1, 10}, {9, 0, 11}, {9, 11, 2}, { 9, 2, 5}, {7, 2, 11} 
	};
	
	private static class CCIndexVertex{
		private int index;
		private CCVector3f vertex;
		
		private CCIndexVertex(final int theIndex, final CCVector3f theVertex) {
			index = theIndex;
			vertex = theVertex;
		}
		
		private CCIndexVertex(final int theIndex, final float theX, final float theY, final float theZ) {
			index = theIndex;
			vertex = new CCVector3f(theX, theY, theZ);
		}
	}
	
	List<CCVector3f> _myVertices = new ArrayList<CCVector3f>();
	List<Integer> _myIndices = new ArrayList<Integer>();
	
	public CCIcoSphere(final CCVector3f theCenter, final float theRadius, final int theResolution) {
		for(CCIndexVertex myVertex:vdata) {
			_myVertices.add(myVertex.vertex.clone());
		}
		for (int i = 0; i < 20; i++) {
			subdivide(vdata[tindices[i][0]], vdata[tindices[i][1]], vdata[tindices[i][2]],theResolution);
		}
		
		for(CCVector3f myVertex:_myVertices) {
			myVertex.scale(theRadius);
			myVertex.add(theCenter);
		}
	}
	
	public void subdivide(final CCIndexVertex v1, final CCIndexVertex v2, final CCIndexVertex v3, int depth) {
		if (depth == 0) {
			_myIndices.add(v1.index);
			_myIndices.add(v2.index);
			_myIndices.add(v3.index);
			return;
		}
		
		int index12 = _myVertices.size();
		int index23 = index12 + 1;
		int index31 = index12 + 2;

		CCIndexVertex v12 = new CCIndexVertex(index12, CCVecMath.add(v1.vertex, v2.vertex).normalize());
		CCIndexVertex v23 = new CCIndexVertex(index23, CCVecMath.add(v2.vertex, v3.vertex).normalize());
		CCIndexVertex v31 = new CCIndexVertex(index31, CCVecMath.add(v3.vertex, v1.vertex).normalize());

		_myVertices.add(v12.vertex);
		_myVertices.add(v23.vertex);
		_myVertices.add(v31.vertex);
		
		subdivide(v1, v12, v31, depth - 1);
		subdivide(v2, v23, v12, depth - 1);
		subdivide(v3, v31, v23, depth - 1);
		subdivide(v12, v23, v31, depth - 1);

	}
	
	public List<CCVector3f> vertices(){
		return _myVertices;
	}
	
	public List<Integer> indices(){
		return _myIndices	;
	}
}
