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
package cc.creativecomputing.model;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.util.CCTriangulator;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.model.material.CCModelMaterial;

 
/**
 * The segment class bundles a number of faces that belong to
 * one group and have the same material
 * @author texone
 *
 */
public class CCSegment {
	
	private final CCModel _myParentModel;
	
	private List<CCFace> _myFaces; 
 
	private String _myMaterialName;
	
	private CCMesh _myMesh;
	private CCModelMaterial _myMaterial;

	public CCSegment(final CCModel theParentModel) {
		_myParentModel = theParentModel;
		_myFaces = new ArrayList<CCFace>();
	}
	
	/**
	 * Returns a list with the faces of this object
	 * @return
	 */
	public List<CCFace> faces() {
		return _myFaces;
	}

	/**
	 * Sets the Name of the material that the faces of this segment use
	 * @param theMaterialName
	 */
	public void materialName(final String theMaterialName) {
		_myMaterialName = theMaterialName;
	}

	/**
	 * Returns the name of the material of this segment
	 * @return
	 */
	public String materialName() {
		return _myMaterialName;
	}
	
	/**
	 * Returns the material that is linked to this segment
	 * @return Material linked to this segment
	 */
	public CCModelMaterial material(){
		return _myParentModel.material(_myMaterialName);
	}
	
	public CCTriangulator triangulate(boolean theGenerateNormals) {
		final CCTriangulator myTriangulator = new CCTriangulator();

		CCVector3f v1,v2,v3,v4;
		CCVector3f n1, n2, n3,n4;
		CCVector2f t1, t2, t3,t4;
		CCVector3f normal;
		
		boolean hasTexture = false;
		boolean hasNormal = false;
		
		for (CCFace myFace:faces()) {
			hasTexture = myFace.textureIndices().size() > 0;
			hasNormal = myFace.normalIndices().size() > 0 && !theGenerateNormals;
			
			if(myFace.size() == 3){
				v1 = _myParentModel.vertices().get(myFace.vertexIndex(0));
				v2 = _myParentModel.vertices().get(myFace.vertexIndex(1));
				v3 = _myParentModel.vertices().get(myFace.vertexIndex(2));
				myTriangulator.addTriangleVertices(v1, v2, v3);
				
				if (hasNormal) {
					n1 = _myParentModel.normals().get(myFace.normalIndex(0));
					n2 = _myParentModel.normals().get(myFace.normalIndex(1));
					n3 = _myParentModel.normals().get(myFace.normalIndex(2));
					myTriangulator.addTriangleNormals(n1, n2, n3);
				}
				
				if(theGenerateNormals){
					normal = CCVecMath.normal(v1, v2, v3);
					myTriangulator.addTriangleNormals(normal, normal, normal);
				}

				if (hasTexture) {
					t1 = _myParentModel.textureCoords().get(myFace.textureIndex(0));
					t2 = _myParentModel.textureCoords().get(myFace.textureIndex(1));
					t3 = _myParentModel.textureCoords().get(myFace.textureIndex(2));
					myTriangulator.addTriangleTextureCoords(t1, t2, t3);
				}
			}else if(myFace.size() == 4){
				v1 = _myParentModel.vertices().get(myFace.vertexIndex(0));
				v2 = _myParentModel.vertices().get(myFace.vertexIndex(1));
				v3 = _myParentModel.vertices().get(myFace.vertexIndex(2));
				v4 = _myParentModel.vertices().get(myFace.vertexIndex(3));
				myTriangulator.addTriangleVertices(v1, v2, v3);
				myTriangulator.addTriangleVertices(v1, v3, v4);
				
				if (hasNormal) {
					n1 = _myParentModel.normals().get(myFace.normalIndex(0));
					n2 = _myParentModel.normals().get(myFace.normalIndex(1));
					n3 = _myParentModel.normals().get(myFace.normalIndex(2));
					n4 = _myParentModel.normals().get(myFace.normalIndex(3));
					myTriangulator.addTriangleNormals(n1, n2, n3);
					myTriangulator.addTriangleNormals(n1, n3, n4);
				}
				
				if(theGenerateNormals){
					normal = CCVecMath.normal(v1, v2, v3);
					myTriangulator.addTriangleNormals(normal, normal, normal);
					myTriangulator.addTriangleNormals(normal, normal, normal);
				}

				if (hasTexture) {
					t1 = _myParentModel.textureCoords().get(myFace.textureIndex(0));
					t2 = _myParentModel.textureCoords().get(myFace.textureIndex(1));
					t3 = _myParentModel.textureCoords().get(myFace.textureIndex(2));
					t4 = _myParentModel.textureCoords().get(myFace.textureIndex(3));
					myTriangulator.addTriangleTextureCoords(t1, t2, t3);
					myTriangulator.addTriangleTextureCoords(t1, t3, t4);
				}
			}else if(myFace.size() > 4){
				myTriangulator.beginPolygon();
				if(theGenerateNormals) {
					normal = CCVecMath.normal(
						_myParentModel.vertices().get(myFace.vertexIndex(0)),
						_myParentModel.vertices().get(myFace.vertexIndex(1)),
						_myParentModel.vertices().get(myFace.vertexIndex(2))
					);
				}
				for(int i = 0; i < myFace.size();i++){
					myTriangulator.vertex(_myParentModel.vertices().get(myFace.vertexIndex(i)));
					if(hasNormal)
						myTriangulator.normal(_myParentModel.normals().get(myFace.normalIndex(i)));
					if(hasTexture)
						myTriangulator.textureCoords(_myParentModel.textureCoords().get(myFace.textureIndex(i)));
					
				}
				myTriangulator.endPolygon();
			}
			

		}
		return myTriangulator;
	}
	
	public void convert(final boolean theGenerateNormals){
		_myMaterial = material();
		
		// if the material is not assigned for some reason, it uses the default material setting
		if (_myMaterial == null){
			_myMaterial = _myParentModel.material("default");
		}
		
		CCTriangulator myTriangulator = triangulate(theGenerateNormals);
		
		int myNumberOfVertices = myTriangulator.vertices().size();
		
		_myMesh = new CCVBOMesh(CCDrawMode.TRIANGLES, myNumberOfVertices);
		_myMesh.vertices(myTriangulator.vertices());
		if(myTriangulator.normals().size() == myNumberOfVertices)_myMesh.normals(myTriangulator.normals());
		if(myTriangulator.textureCoords().size() == myNumberOfVertices){
			_myMesh.textureCoords(myTriangulator.textureCoords());
		}
	}
	
	public CCMesh mesh(){
		return _myMesh;
	}
	
	public void draw(CCGraphics g){
		if(_myMesh == null)return;
//		_myMaterial.begin(g);
		_myMesh.draw(g);
//		_myMaterial.end(g);
	}
}
 
