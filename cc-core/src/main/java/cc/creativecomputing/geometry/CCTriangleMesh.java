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
package cc.creativecomputing.geometry;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.creativecomputing.geometry.CCTriangleIntersector.CCTriangleIntersectionData;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.math.CCRay3f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.CCVector4f;

/**
 * @author christianriekoff
 *
 */
public class CCTriangleMesh {
	
	public static class CCTriangleMeshIntersectionData extends CCTriangleIntersectionData{
		
		private int index0;
		private int index1;
		private int index2;
		
		protected CCTriangleMeshIntersectionData(
			CCTriangleIntersectionData theData,
			CCMesh theMesh,
			int theIndex0,
			int theIndex1,
			int theIndex2
		) {
			super(theData);
			index0 = theIndex0;
			index1 = theIndex1;
			index2 = theIndex2;
		}
		
		public int index0() {
			return index0;
		}
		
		public int index1() {
			return index1;
		}
		
		public int index2() {
			return index2;
		}
	}

	private CCMesh _myMesh;
	
	private CCTriangleIntersector _myIntersector;
	
	public CCTriangleMesh(CCMesh theMesh) {
		_myMesh = theMesh;
		_myIntersector = new CCTriangleIntersector();
	}
	
	public CCVector3f position(int theIndex) {
		return new CCVector3f(
			_myMesh.vertices().get(theIndex * 3 + 0),
			_myMesh.vertices().get(theIndex * 3 + 1), 
			_myMesh.vertices().get(theIndex * 3 + 2)
		);
	}
	
	public CCVector4f texCoord4f(int theIndex, int theLevel) {
		return new CCVector4f(
			_myMesh.textureCoords(theLevel).get(theIndex * 4 + 0),
			_myMesh.textureCoords(theLevel).get(theIndex * 4 + 1),
			_myMesh.textureCoords(theLevel).get(theIndex * 4 + 2),
			_myMesh.textureCoords(theLevel).get(theIndex * 4 + 3)
		);
	}
	
	public List<CCTriangleMeshIntersectionData> intersect(CCRay3f theRay) {
		IntBuffer myIndices = _myMesh.indices();
		
		List<CCTriangleMeshIntersectionData> myResultList = new ArrayList<CCTriangleMeshIntersectionData>();
		
		if(myIndices != null) {
			while(myIndices.hasRemaining()) {
				int index0 = myIndices.get();
				int index1 = myIndices.get();
				int index2 = myIndices.get();
				
				CCTriangleIntersectionData myIntersectionData = _myIntersector.intersectsRay(
					theRay, 
						
					_myMesh.vertices().get(index0 * 3 + 0),
					_myMesh.vertices().get(index0 * 3 + 1), 
					_myMesh.vertices().get(index0 * 3 + 2), 
	
					_myMesh.vertices().get(index1 * 3 + 0), 
					_myMesh.vertices().get(index1 * 3 + 1), 
					_myMesh.vertices().get(index1 * 3 + 2), 
	
					_myMesh.vertices().get(index2 * 3 + 0), 
					_myMesh.vertices().get(index2 * 3 + 1), 
					_myMesh.vertices().get(index2 * 3 + 2)
				);
				
				if(myIntersectionData == null)continue;
				
				CCTriangleMeshIntersectionData myData = new CCTriangleMeshIntersectionData(
					myIntersectionData,
					_myMesh,
					index0, 
					index1,
					index2
				);
				
				myResultList.add(myData);
				
			}
			myIndices.rewind();
		}
		
		Collections.sort(myResultList);
		
		return myResultList;
	}
}
