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
package cc.creativecomputing.graphics.util;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.graphics.CCTesselator;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.CCVector4f;

public class CCTriangulator extends CCTesselator{
	
	private List<CCVector3f> _myVertices;
	private List<CCVector3f> _myNormals;
	private List<CCColor> _myColors;
	private List<Object> _myTextureCoords;

	public CCTriangulator() {
		super();
		_myVertices = new ArrayList<CCVector3f>();
		_myNormals = new ArrayList<CCVector3f>();
		_myTextureCoords = new ArrayList<Object>();
		_myColors = new ArrayList<CCColor>();
	}

	@Override
	public void begin(int theMode) {
	}

	@Override
	public void combineData(
		double[] theCoords, Object[] theInputData,
		float[] theWeight, Object[] theOutputData, Object theUserData
	) {
	}

	@Override
	public void edgeFlagData(final boolean theArg0, Object theData) {
	}

	@Override
	public void end() {
	}

	@Override
	public void errorData(final int theErrorNumber, Object theUserData) {
	}

	@Override
	public void vertex(final Object theVertexData) {
		if (theVertexData instanceof double[]) {
			double[] myVertexData = (double[]) theVertexData;
			
			_myVertices.add(
				new CCVector3f(
					(float)myVertexData[VERTEX_X],
					(float)myVertexData[VERTEX_Y],
					(float)myVertexData[VERTEX_Z]
				)
			);
			
			if(_myHasTextureData) {
				_myTextureCoords.add(
					new CCVector4f(
						(float)myVertexData[TEXTURE_S],
						(float)myVertexData[TEXTURE_T],
						(float)myVertexData[TEXTURE_R],
						(float)myVertexData[TEXTURE_Q]
					)
				);
			}
			
			if(_myHasNormalData) {
				_myNormals.add(
					new CCVector3f(
						(float)myVertexData[NORMAL_X],
						(float)myVertexData[NORMAL_Y],
						(float)myVertexData[NORMAL_Z]
					)
				);
			}
			
			if(_myHasColorData) {
				_myColors.add(
					new CCColor(
						(float)myVertexData[COLOR_R],
						(float)myVertexData[COLOR_G],
						(float)myVertexData[COLOR_B],
						(float)myVertexData[COLOR_A]
					)
				);
			}

		} else {
			throw new RuntimeException("TessCallback vertex() data not understood");
		}
	}
	
	public void addTriangleVertices(final CCVector3f the1, final CCVector3f the2, final CCVector3f the3){
		_myVertices.add(the1);
		_myVertices.add(the2);
		_myVertices.add(the3);
	}
	
	public void addTriangleNormals(final CCVector3f the1, final CCVector3f the2, final CCVector3f the3){
		_myNormals.add(the1);
		_myNormals.add(the2);
		_myNormals.add(the3);
	}
	
	public void addTriangleTextureCoords(final CCVector4f the1, final CCVector4f the2, final CCVector4f the3){
		_myTextureCoords.add(the1);
		_myTextureCoords.add(the2);
		_myTextureCoords.add(the3);
	}
	
	public void addTriangleTextureCoords(final CCVector2f the1, final CCVector2f the2, final CCVector2f the3){
		_myTextureCoords.add(new CCVector4f(the1));
		_myTextureCoords.add(new CCVector4f(the2));
		_myTextureCoords.add(new CCVector4f(the3));
	}
	
	public void addTriangleColors(final CCColor the1, final CCColor the2, final CCColor the3){
		_myColors.add(the1);
		_myColors.add(the2);
		_myColors.add(the3);
	}

	public List<CCVector3f> vertices(){
		return _myVertices;
	}

	public List<CCVector3f> normals(){
		return _myNormals;
	}

	public List<Object> textureCoords(){
		return _myTextureCoords;
	}

	public List<CCColor> colors(){
		return _myColors;
	}
	
	public void reset() {
		_myVertices = new ArrayList<CCVector3f>();
		_myNormals = new ArrayList<CCVector3f>();
		_myTextureCoords = new ArrayList<Object>();
		_myColors = new ArrayList<CCColor>();
	}
}
