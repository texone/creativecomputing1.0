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
package cc.creativecomputing.skeleton;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.CCVector4f;

/**
 * @author christianriekoff
 *
 */
public class CCSkeletonSkin{
	
	@CCControl(name = "draw")
	private boolean _cDraw = true;
	
	public static class CCSkeletonSkinPoint{
		
		private CCVector3f _myOrigin;
		private CCVector3f _myNormal;
		
		private float[] _myWeights = new float[4];
		private float[] _myWeightIndices = new float[4];
		
		public CCSkeletonSkinPoint(
			float theX, float theY, float theZ,
			float theNormalX, float theNormalY, float theNormalZ,
			float theWeight0,
			float theWeight1,
			float theWeight2,
			float theWeight3,
			float theWeightIndex0,
			float theWeightIndex1,
			float theWeightIndex2,
			float theWeightIndex3
		){
			_myOrigin = new CCVector3f(theX, theY, theZ);
			_myNormal = new CCVector3f(theNormalX, theNormalY, theNormalZ);
			_myWeights[0] = theWeight0;
			_myWeights[1] = theWeight1;
			_myWeights[2] = theWeight2;
			_myWeights[3] = theWeight3;
			
			_myWeightIndices[0] = theWeightIndex0;
			_myWeightIndices[1] = theWeightIndex1;
			_myWeightIndices[2] = theWeightIndex2;
			_myWeightIndices[3] = theWeightIndex3;
		}
		
		public CCSkeletonSkinPoint(
			float theX, float theY, float theZ,
			float theNormalX, float theNormalY, float theNormalZ,
			float[] theWeights,
			float[] theWeightIndices
		){
			_myOrigin = new CCVector3f(theX, theY, theZ);
			_myNormal = new CCVector3f(theNormalX, theNormalY, theNormalZ);
			_myWeights[0] = theWeights[0];
			_myWeights[1] = theWeights[1];
			_myWeights[2] = theWeights[2];
			_myWeights[3] = theWeights[3];
				
			_myWeightIndices[0] = theWeightIndices[0];
			_myWeightIndices[1] = theWeightIndices[1];
			_myWeightIndices[2] = theWeightIndices[2];
			_myWeightIndices[3] = theWeightIndices[3];
		}
		
		public CCSkeletonSkinPoint(CCSkeletonSkinPoint thePoint){
			_myOrigin = thePoint._myOrigin.clone();
			_myNormal = thePoint._myNormal.clone();
			_myWeights[0] = thePoint._myWeights[0];
			_myWeights[1] = thePoint._myWeights[1];
			_myWeights[2] = thePoint._myWeights[2];
			_myWeights[3] = thePoint._myWeights[3];
			
			_myWeightIndices[0] = thePoint._myWeightIndices[0];
			_myWeightIndices[1] = thePoint._myWeightIndices[1];
			_myWeightIndices[2] = thePoint._myWeightIndices[2];
			_myWeightIndices[3] = thePoint._myWeightIndices[3];
		}
		
		public CCSkeletonSkinPoint(){
			_myOrigin = new CCVector3f();
			_myNormal = new CCVector3f();
		}
		
		public float totalWeight(){
			return _myWeights[0] + _myWeights[1] + _myWeights[2] + _myWeights[3];
		}
		
		public CCVector3f origin(){
			return _myOrigin;
		}
		
		public CCVector3f normal(){
			return _myNormal;
		}
		
		public float[] weights(){
			return _myWeights;
		}
		
		public float[] weightIndices(){
			return _myWeightIndices;
		}
	}
	
	private CCGLSLShader _myWeightsShader;
	protected CCVBOMesh _myMesh;
	
	public CCSkeletonSkin(CCDrawMode theDrawMode) {   
		_myMesh = new CCVBOMesh(theDrawMode);
		_myWeightsShader = new CCGLSLShader(
			CCIOUtil.classPath(CCSkeleton.class, "weights_vert.glsl"),
			CCIOUtil.classPath(CCSkeleton.class, "weights_frag.glsl")
		);
		_myWeightsShader.load();
	}
	
	public CCVBOMesh mesh(){
		return _myMesh;
	}
	
	public int numberOfVertices(){
		return _myMesh.numberOfVertices();
	}
	
	public CCSkeletonSkin(CCDrawMode theDrawMode, CCGLSLShader theWeightShader) {   
		_myWeightsShader = theWeightShader;
	}
	
	public void weights(FloatBuffer theWeights){
		_myMesh.textureCoords(1, theWeights, 4);
	}
	
	public FloatBuffer weights() {
		return _myMesh.textureCoords(1);
	}
	
	public void weightIndices(FloatBuffer theWeightIndices){
		_myMesh.textureCoords(2, theWeightIndices, 4);
	}
	
	public FloatBuffer weightIndices() {
		return _myMesh.textureCoords(2);
	}
	
	public List<CCMatrix4f> skinningMatrices(CCSkeleton theSkeleton){
		return theSkeleton.skinningMatrices();
	}
	
	public CCSkeletonSkinPoint point(int theIndex) {
		_myMesh.vertices().position(theIndex * 3);
		_myMesh.normals().position(theIndex * 3);
		weights().position(theIndex * 4);
		weightIndices().position(theIndex * 4);
		
		return new CCSkeletonSkinPoint(
			_myMesh.vertices().get(),
			_myMesh.vertices().get(),
			_myMesh.vertices().get(),

			_myMesh.normals().get(),
			_myMesh.normals().get(),
			_myMesh.normals().get(),
			
			weights().get(),
			weights().get(),
			weights().get(),
			weights().get(),
			
			(int)weightIndices().get(),
			(int)weightIndices().get(),
			(int)weightIndices().get(),
			(int)weightIndices().get()
		);
	}
	
	public CCVector3f position(CCSkeletonSkinPoint thePoint, CCSkeleton theSkeleton){
		List<CCMatrix4f> mySkinningMatrices = skinningMatrices(theSkeleton);
		CCMatrix4f skinningMatrix = new CCMatrix4f(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
	    skinningMatrix = skinningMatrix.add(mySkinningMatrices.get((int)thePoint._myWeightIndices[0] + 1).multiply(thePoint._myWeights[0]));
	    skinningMatrix = skinningMatrix.add(mySkinningMatrices.get((int)thePoint._myWeightIndices[1] + 1).multiply(thePoint._myWeights[1]));
	    skinningMatrix = skinningMatrix.add(mySkinningMatrices.get((int)thePoint._myWeightIndices[2] + 1).multiply(thePoint._myWeights[2]));
	    skinningMatrix = skinningMatrix.add(mySkinningMatrices.get((int)thePoint._myWeightIndices[3] + 1).multiply(thePoint._myWeights[3]));
	    skinningMatrix = skinningMatrix.multiply((1f / thePoint.totalWeight()));
	    CCMatrix4f bindMatrix = mySkinningMatrices.get(0);
	    CCVector4f myResult =bindMatrix.multiply(skinningMatrix).transform(new CCVector4f(thePoint.origin(), 1f));
	    return new CCVector3f(myResult.x, myResult.y, myResult.z);
	}
	
	private class JointData implements Comparable<JointData>{
		float _myIndex;
		float _myWeight;
		
		JointData(float theIndex, float theWeight){
			_myIndex = theIndex;
			_myWeight = theWeight;
		}

		@Override
		public int compareTo(JointData theO) {
			return -new Float(_myWeight).compareTo(theO._myWeight);
		}
	}
	
	Map<Float,List<Float>> _myJointMap= new HashMap<>();
	
	private List<JointData> mapJoints(float theBlend0, float theBlend1, float[][] theIndexData, float[][] theWeightData) {
		_myJointMap.clear();
		for (int p = 0; p < 3; p++) {
			for (int i = 0; i < 4; i++) {
				float myIndex = theIndexData[p][i];
				if (myIndex == -1)
					continue;
				float myWeight = theWeightData[p][i];
				if (!_myJointMap.containsKey(myIndex)) {
					_myJointMap.put(myIndex, new ArrayList<Float>());
				}
				List<Float> myWeights = _myJointMap.get(myIndex);
				for (int j = myWeights.size(); j < p; j++) {
					myWeights.add(0f);
				}
				myWeights.add(myWeight);
			}
		}
		for (List<Float> myWeights : _myJointMap.values()) {
			for (int p = myWeights.size(); p < 3; p++) {
				myWeights.add(0f);
			}
		}
		List<JointData> _myJoints = new ArrayList<>();

		for (float myKey : _myJointMap.keySet()) {
			List<Float> myWeights = _myJointMap.get(myKey);
			_myJoints.add(new JointData(myKey, CCMath.blend(CCMath.blend(myWeights.get(0), myWeights.get(1), theBlend0), myWeights.get(2), theBlend1)));
		}

		Collections.sort(_myJoints);

		// for(JointData myData:_myJoints){
		// CCLog.info(myData._myIndex+" : "+myData._myWeight);
		// }

		// System.out.println();
		// for(float myKey:_myJointMap.keySet()){
		// System.out.println("index:" + myKey);
		// for(float myWeight:_myJointMap.get(myKey)){
		// System.out.print(" weight :" + myWeight);
		// }
		// }
		// CCLog.info(_myJointMap.size());

		return _myJoints;
	}
	
	public CCSkeletonSkinPoint randomPoint() {
		int myTriangleIndex = (int)CCMath.random(_myMesh.vertices().limit() / 3 / 3) * 3;
		
		_myMesh.vertices().position(myTriangleIndex * 3);
		float myP0X = _myMesh.vertices().get();
		float myP0Y = _myMesh.vertices().get();
		float myP0Z = _myMesh.vertices().get();

		float myP1X = _myMesh.vertices().get();
		float myP1Y = _myMesh.vertices().get();
		float myP1Z = _myMesh.vertices().get();

		float myP2X = _myMesh.vertices().get();
		float myP2Y = _myMesh.vertices().get();
		float myP2Z = _myMesh.vertices().get();
		
		float myP01X = myP1X - myP0X;
		float myP01Y = myP1Y - myP0Y;
		float myP01Z = myP1Z - myP0Z;
		
		float myP02X = myP2X - myP0X;
		float myP02Y = myP2Y - myP0Y;
		float myP02Z = myP2Z - myP0Z;
	
		float myBlend1 = CCMath.random();
		float myBlend2 = CCMath.random();
		
		CCSkeletonSkinPoint myResult = new CCSkeletonSkinPoint();
				
		myResult._myOrigin.x = myP0X + myP01X * myBlend1 + myP02X * myBlend2;
		myResult._myOrigin.y = myP0Y + myP01Y * myBlend1 + myP02Y * myBlend2;
		myResult._myOrigin.z = myP0Z + myP01Z * myBlend1 + myP02Z * myBlend2;
		
		weightIndices().position(myTriangleIndex * 4);
		weights().position(myTriangleIndex * 4);
		
		float[][] myIndexData = new float[3][4];
		float[][] myWeightData = new float[3][4];
		
		for(int p = 0; p < 3;p++){
			for(int i = 0; i < 4;i++){
				myIndexData[p][i] = weightIndices().get();
				myWeightData[p][i] = weights().get();
			}
		}
		
		boolean myAllEqual = true;
		
		for(int i = 0; i < 4;i++){
			myAllEqual = myAllEqual && myIndexData[0][i] == myIndexData[1][i] && myIndexData[0][i] == myIndexData[2][i];
		}
		
		if(myAllEqual){
			for(int i = 0; i < 4;i++){
				if(myIndexData[0][i] == -1)continue;
				myResult._myWeightIndices[i] = (int)myIndexData[0][i];
				float w01 = myWeightData[1][i] - myWeightData[0][i];
				float w02 = myWeightData[2][i] - myWeightData[0][i];
				myResult._myWeights[i] = myWeightData[0][i] + w01 * myBlend1 + w02 * myBlend2;
			}
		}else{
			List<JointData>myDatas = mapJoints(myBlend1, myBlend2, myIndexData,myWeightData);
			for(int i = 0; i < CCMath.min(myDatas.size(),4);i++){
				JointData myData = myDatas.get(i);
				myResult._myWeightIndices[i] = (int)myData._myIndex;
				myResult._myWeights[i] = myData._myWeight;
			}
		}

		return myResult;
	}
	
	public void draw(CCGraphics g, CCSkeleton theSkeleton) {
		if(!_cDraw)return;
		if(theSkeleton.skinningMatrices() == null)return;
		g.pushMatrix();
		_myWeightsShader.start();
		_myWeightsShader.uniformMatrix4fv("joints", skinningMatrices(theSkeleton));
		_myMesh.draw(g);
		_myWeightsShader.end();
		g.popMatrix();
	}
}
