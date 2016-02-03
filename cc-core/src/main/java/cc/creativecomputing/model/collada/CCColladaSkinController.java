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
package cc.creativecomputing.model.collada;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.skeleton.CCSkeleton;
import cc.creativecomputing.xml.CCXMLElement;

/**
 * For character skinning, an animation engine drives the joints (skeleton) of a skinned character. 
 * A skin mesh describes the associations between the joints and the mesh vertices forming the skin 
 * topology. The joints influence the transformation of skin mesh vertices according to a controlling algorithm.
 * <p>
 * A common skinning algorithm blends the influences of neighboring joints according to weighted values.
 * The classical skinning algorithm transforms points of a geometry (for example vertices of a mesh) with matrices 
 * of nodes (sometimes called joints) and averages the result using scalar weights. The affected geometry is called 
 * the skin, the combination of a transform (node) and its corresponding weight is called an influence, and the set 
 * of influencing nodes (usually a hierarchy) is called a skeleton.
 * <p>
 * ?Skinning? involves two steps:
 * <ul>
 * <li>Preprocessing, known as ?binding the skeleton to the skin?</li>
 * <li>Running the skinning algorithm to modify the shape of the skin as the pose of the skeleton changes 
 * The results of the pre-processing, or ?skinning information? consists of the following:<li>
 * <ul>
 * <li>bind-shape: also called ?default shape?. 
 * This is the shape of the skin when it was bound to the skeleton. This includes positions (required) for 
 * each corresponding <mesh> vertex and may optionally include additional vertex attributes.</li>
 * <li>influences: a variable-length lists of node + weight pairs for each <mesh> vertex.</li>
 * <li>bind-pose: the transforms of all influences at the time of binding. This per-node information is 
 * usually represented by a ?bind-matrix?, which is the local-to-world matrix of a node at the time of binding.</li>
 * </ul>
 * </ul>
 * In the skinning algorithm, all transformations are done relative to the bind-pose. This relative transform is usually 
 * pre-computed for each node in the skeleton and is stored as a skinning matrix.
 * <p>
 * To derive the new (?skinned?) position of a vertex, the skinning matrix of each influencing node transforms the bind-shape 
 * position of the vertex and the result is averaged using the blending weights.The easiest way to derive the skinning matrix 
 * is to multiply the current local-to-world matrix of a node by the inverse of the node?s bind-matrix. This effectively cancels 
 * out the bind-pose transform of each node and allows us to work in the common object space of the skin.
 * The binding process usually involves:
 * <ul>
 * <li>Storing the current shape of the skin as the bind-shape</li>
 * <li>Computing and storing the bind-matrices</li>
 * <li>Generating default blending weights, usually with some fall-off function: the farther a joint is from a given vertex, 
 * the less it influences it. Also, if a weight is 0, the influence can be omitted.</li>
 * </ul>
 * After that, the artist is allowed to hand-modify the weights, usually by ?painting? them on the mesh.
 * @author christianriekoff
 *
 */
public class CCColladaSkinController extends CCColladaElement{
	
	/**
	 * Associates joint, or skeleton, nodes with attribute data. 
	 * In COLLADA, this is specified by the inverse bind matrix of each joint (influence) in the skeleton.
	 * @author christianriekoff
	 *
	 */
	public static class CCColladaSkinJoint{
		private String _myName;
		private int _myIndex;
		private CCMatrix4f _myInverseBindMatrix;
		
		private CCColladaSkinJoint(String theName, int theIndex) {
			_myName = theName;
			_myIndex = theIndex;
		}
		
		public String name() {
			return _myName;
		}
		
		public int index() {
			return _myIndex;
		}
		
		public CCMatrix4f inverseBindMatrix() {
			return _myInverseBindMatrix;
		}
	}
	
	private Map<String, CCColladaSkinJoint> _myJointMap = new HashMap<String, CCColladaSkinJoint>();
	private List<CCColladaSkinJoint> _myJoints = new ArrayList<CCColladaSkinJoint>();
	private CCColladaGeometry _myGeometry;
	
	private FloatBuffer _myWeightMap;
	private FloatBuffer _myIndexMap;
	
	/**
	 * Provides extra information about the position and orientation of the base mesh before binding. 
	 * Contains sixteen floating-point numbers representing a four-by- four matrix in column-major order;
	 * it is written in row- major order in the COLLADA document for human readability. If <bind_shape_matrix> 
	 * is not specified then an identity matrix may be used as the <bind_shape_matrix>. This element has no attributes.
	 */
	private CCMatrix4f _myBindShapeMatrix;

	public CCColladaSkinController(CCXMLElement theSkinXML, CCColladaGeometries theGeometries, String theID) {
		super(theSkinXML);
		_myID = theID;
		
		_myBindShapeMatrix = new CCMatrix4f();
		CCXMLElement myBindShapeMatrixXML = theSkinXML.child("bind_shape_matrix");
		if(myBindShapeMatrixXML != null) {
			String[] myFloatStrings = myBindShapeMatrixXML.content().split("\\s");
			float[] myFloats = new float[16];
			for(int i = 0; i < myFloats.length;i++) {
				myFloats[i] = Float.parseFloat(myFloatStrings[i]);
			}
			_myBindShapeMatrix.setFromRowOrder(myFloats);
		}
		
		CCXMLElement myJointsXML = theSkinXML.child("joints");
		CCColladaSource mySource = source(myJointsXML, "JOINT");
		String[] myNames = mySource.stringValues();
		for(String myName:myNames) {
			CCColladaSkinJoint myJoint = new CCColladaSkinJoint(myName, _myJoints.size());
			_myJoints.add(myJoint);
			_myJointMap.put(myName, myJoint);
		}
		mySource = source(myJointsXML, "INV_BIND_MATRIX");
		float[][] myValues = mySource.pointMatrix();
		
		for(int i = 0; i < _myJoints.size();i++) {
			_myJoints.get(i)._myInverseBindMatrix = new CCMatrix4f().setFromRowOrder(myValues[i]);
		}
		
		CCXMLElement myVertexWeightsxmL = theSkinXML.child("vertex_weights");
		mySource = source(myVertexWeightsxmL, "WEIGHT");
		float[][] myWeights = mySource.pointMatrix();
		
		String[] myVCountsString = myVertexWeightsxmL.child("vcount").content().split("\\s");
		int[] myVCounts = new int[myVCountsString.length];
		for(int i = 0; i < myVCountsString.length; i++) {
			myVCounts[i] = Integer.parseInt(myVCountsString[i]);
		}
		
		String[] myVJointsString = myVertexWeightsxmL.child("v").content().split("\\s");
		int[] myVJoints = new int[myVJointsString.length];
		for(int i = 0; i < myVJointsString.length; i++) {
			myVJoints[i] = Integer.parseInt(myVJointsString[i]);
		}
		
		String myGeometrySource = theSkinXML.attribute("source");
		_myGeometry = theGeometries.element(myGeometrySource.substring(1));
		CCColladaGeometryData myGeometryData = _myGeometry.geometryData(0);

		float[][] myWeightsMatrix = new float[myVCountsString.length][4];
		float[][] myIndexMatrix = new float[myVCountsString.length][4];
		
		int myWeightIndex = 0;
		for(int i = 0; i < myVCounts.length;i++) {
			int myVCount = myVCounts[i];
			int j = 0;
			for(; j < myVCount; j++) {
				if(j < 4){
					myIndexMatrix[i][j] = myVJoints[myWeightIndex * 2];
					myWeightsMatrix[i][j] = myWeights[myVJoints[myWeightIndex * 2 + 1]][0];
				}
				myWeightIndex++;
			}
			for(; j < 4;j++) {
				myWeightsMatrix[i][j] = 0;
				myIndexMatrix[i][j] = -1;
			}
		}
		
		_myWeightMap = FloatBuffer.allocate(myGeometryData.numberOfVertices() * 4);
		_myIndexMap = FloatBuffer.allocate(myGeometryData.numberOfVertices() * 4);
		
		_myWeightMap = FloatBuffer.allocate(myGeometryData.numberOfVertices() * 4);
		for(int i = 0; i < myGeometryData.numberOfVertices(); i++){
			int myIndex = myGeometryData.pointIndexMatrix()[i * myGeometryData.stride() + myGeometryData.positionOffset()];
			_myWeightMap.put(myWeightsMatrix[myIndex]);
			_myIndexMap.put(myIndexMatrix[myIndex]);
		}
		
		
		_myWeightMap.rewind();
		_myIndexMap.rewind();
	}

	public List<CCMatrix4f> skinningMatrices(CCSkeleton theSkeleton){
		List<CCMatrix4f> myResult = new ArrayList<CCMatrix4f>();
		myResult.add(_myBindShapeMatrix);
		for(CCColladaSkinJoint myJoint:_myJoints) {
			myResult.add(theSkeleton.joint(myJoint.name()).skinningMatrix());
		}
		return myResult;
	}
	
	public CCMatrix4f bindShapeMatrix() {
		return _myBindShapeMatrix;
	}
	
	public CCColladaGeometry geometry(){
		return _myGeometry;
	}
	
	public FloatBuffer weights() {
		return _myWeightMap;
	}
	
	public FloatBuffer indices() {
		return _myIndexMap;
	}
	
	public List<CCColladaSkinJoint> joints(){
		return _myJoints;
	}
	
	public CCColladaSkinJoint joint(String theID) {
		return _myJointMap.get(theID);
	}
	
	public boolean hasJoint(String theJointID) {
		return _myJointMap.containsKey(theJointID);
	}
}
