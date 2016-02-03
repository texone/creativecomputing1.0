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
import java.util.HashMap;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.CCGraphics.CCColorMaterialMode;
import cc.creativecomputing.graphics.util.CCTriangulator;
import cc.creativecomputing.math.CCAABB;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCVector2f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.model.material.CCModelMaterial;


public class CCModel {
	private final HashMap<String, CCObject> _myObjectMap = new HashMap<>();
	private final HashMap<String, CCObjectGroup> _myObjectGroupMap = new HashMap<>();
	
	private final HashMap<String, CCModelMaterial> _myMaterialMap = new HashMap<String, CCModelMaterial>();
	
	private final List<CCVector3f> _myVertices = new ArrayList<CCVector3f>();
	private final List<CCVector3f> _myNormals = new ArrayList<CCVector3f>();
	private final List<CCVector2f> _myTextureCoords = new ArrayList<CCVector2f>();
	private final List<CCFace> _myFaces = new ArrayList<CCFace>();
	
	private final CCAABB _myBoundingBox = new CCAABB();
	
	private final CCMatrix4f _myTransformationMatrix;
	
	public CCModel(){
		_myTransformationMatrix = new CCMatrix4f();
	}
	
	public CCAABB boundingBox(){
		return _myBoundingBox;
	}

	public HashMap<String, CCObject> objectMap() {
		return _myObjectMap;
	}
	
	public String[] objectNames(){
		return _myObjectMap.keySet().toArray(new String[0]);
	}
	
	public CCObject object(final String theGroup){
		return _myObjectMap.get(theGroup);
	}
	
	public HashMap<String, CCObjectGroup> groupMap() {
		return _myObjectGroupMap;
	}
	
	public String[] groupNames(){
		return _myObjectGroupMap.keySet().toArray(new String[0]);
	}
	
	public CCObjectGroup group(final String theGroup){
		return _myObjectGroupMap.get(theGroup);
	}
	
	/**
	 * Returns the first segment of the given group. Usually this
	 * will be the only segment as most files only have one segment
	 * per group although more would be possible
	 * @param theObject
	 * @return
	 */
	public CCSegment segment(final String theObject){
		return _myObjectMap.get(theObject).segments().get(0);
	}

	public HashMap<String, CCModelMaterial> materialMap() {
		return _myMaterialMap;
	}
	
	public String[] getMaterialNames(){
		return _myMaterialMap.keySet().toArray(new String[0]);
	}
	
	public CCModelMaterial material(final String theMaterialName){
		return _myMaterialMap.get(theMaterialName);
	}

	public List<CCVector3f> vertices() {
		return _myVertices;
	}
	
	public void addVertex(final CCVector3f theVertex){
		_myBoundingBox.checkSize(theVertex);
		_myVertices.add(theVertex);
	}

	public List<CCVector3f> normals() {
		return _myNormals;
	}

	public List<CCVector2f> textureCoords() {
		return _myTextureCoords;
	}
	
	public List<CCFace> faces(){
		return _myFaces;
	}
	
	public CCMatrix4f transform(){
		return _myTransformationMatrix;
	}
	
	/**
	 * Centers the model by moving the coordinates
	 */
	public void center(){
		CCVector3f myTranslation = _myBoundingBox.center().clone();
		myTranslation.scale(-1);
		translate(myTranslation);
	}
	
	public void scale(final float theScale){
		for(CCVector3f myVertex:_myVertices){
			myVertex.scale(theScale);
		}
		
		/* also update boundingbox */
		_myBoundingBox.max().scale(theScale);
		_myBoundingBox.min().scale(theScale);
	}
	
	public void translate(final CCVector3f theVector){
		for(CCVector3f myVertex:_myVertices){
			myVertex.add(theVector);
		}
		
		/* also update boundingbox */
		_myBoundingBox.max().add(theVector);
		_myBoundingBox.min().add(theVector);
	}
	
	public CCVector3f centerOfModel(){
		return _myBoundingBox.center().clone();
	}
	
	/**
	 * Creates one Mesh out of all the obj data ignoring materials
	 * @return
	 */
	public CCMesh merge() {
		final List<CCVector3f> myNormals = new ArrayList<CCVector3f>();
		final List<CCVector2f> myTextureCoords = new ArrayList<CCVector2f>();

		final CCTriangulator myTriangulator = new CCTriangulator();

		for (CCFace myFace : faces()) {
			if (myFace.size() > 3) {
				myTriangulator.beginPolygon();
				for (int i = 0; i < myFace.size(); i++) {
					CCVector3f v = vertices().get(myFace.vertexIndex(i));
					myTriangulator.vertex(v);
				}
				myTriangulator.endPolygon();
			}else if(myFace.size() == 3){
				myTriangulator.addTriangleVertices(
					vertices().get(myFace.vertexIndex(0)),
					vertices().get(myFace.vertexIndex(1)),
					vertices().get(myFace.vertexIndex(2))
				);
			}
		}

		final CCMesh myMesh = new CCVBOMesh();
		myMesh.vertices(myTriangulator.vertices());
		myMesh.normals(myNormals);
		myMesh.textureCoords(myTextureCoords);
		return myMesh;
	}
	
	/**
	 * You must call this method before drawing, to put your model data into a
	 * <code>CCVBOMesh</code> for fast drawing. All active groups will be converted to VBOMesh.
	 * Use <code>deactivateGroup</code> to avoid conversion of certain groups, be aware that you
	 * can not activate these groups for later drawing.
	 * @see #deactivateGroup(String)
	 */
	public void convert(final boolean theGenerateNormal){
		for(CCVector3f myVertex:_myVertices){
			_myTransformationMatrix.transform(myVertex);
		}
		_myTransformationMatrix.transform(_myBoundingBox.min());
		_myTransformationMatrix.transform(_myBoundingBox.max());
		for(CCObject myGroup:_myObjectMap.values()){
			if(!myGroup.isActiv())continue;
			for(CCSegment mySegment:myGroup.segments()){
				mySegment.convert(theGenerateNormal);
			}
		}
	}
	
	public void convert(){
		convert(false);
	}
	
	/**
	 * Use this function to activate a group of the model for drawing.
	 * 
	 * @param theGroupName
	 * @related deativateGroup ( )
	 */
	public void activateGroup(final String theGroupName){
		CCObject myGroup = _myObjectMap.get(theGroupName);
		if(myGroup != null)myGroup.isActiv(true);
	}
	
	/**
	 * Deactivates the given group, so that it will not be drawn, when you draw the model.
	 * If you call this method before convert, the group will also not be included in the
	 * resulting <code>CCVBOMesh</code> instance, and can not be activated for later drawing.
	 * @param theGroupName
	 * @see #convert()
	 * @see #activateGroup(String)
	 */
	public void deactivateGroup(final String theGroupName){
		CCObject myGroup = _myObjectMap.get(theGroupName);
		if(myGroup != null)myGroup.isActiv(false);
	}
	
	/**
	 * This method is similar to <code>deactivateGroup</code> but deactivates all groups except
	 * the given one.
	 * @param theGroupName
	 */
	public void deactivateAllBut(final String theGroupName){
		for(CCObject myGroup:_myObjectMap.values()){
			myGroup.isActiv(false);
		}
		activateGroup(theGroupName);
	}
	
	/**
	 * Draws all activated groups of the loaded model. By default all groups are
	 * activated and will be drawn. If you pass a group name to this function only
	 * this group will be drawn no matter if it is activated or not.
	 * @param g the graphics object for rendering
	 */
	public void draw(CCGraphics g){
		CCColorMaterialMode myColorMaterialMode = g.colorMaterial();
		g.colorMaterial(CCColorMaterialMode.OFF);
		for(CCObject myGroup:_myObjectMap.values()){
			if(myGroup.isActiv()){
				for(CCSegment mySegment:myGroup.segments()){
					mySegment.draw(g);
				}
			}
		}
		g.colorMaterial(myColorMaterialMode);
	}
	
	 
}
