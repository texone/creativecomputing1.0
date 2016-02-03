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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.math.CCMatrix4f;
import cc.creativecomputing.util.CCStringUtil;
import cc.creativecomputing.xml.CCXMLElement;

/**
 * Declares a point of interest in a scene.
 * <p>
 * The <node> element embodies the hierarchical relationship of elements in a scene.by 
 * declaring a point of interest in a scene. A node denotes one point on a branch of the 
 * scene graph. The <node> element is essentially the root of a subgraph of the entire scene graph.
 * <p>
 * Within the scene graph abstraction, there are arcs and nodes. Nodes are points of information 
 * within the graph. Arcs connect nodes to other nodes. Nodes are further distinguished as interior 
 * (branch) nodes and exterior (leaf) nodes. COLLADA uses the term node to denote interior nodes. 
 * Arcs are also called paths.
 * @author christianriekoff
 *
 */
public class CCColladaSceneNode extends CCColladaElement{
	
	public enum CCColladaSceneNodeType{
		JOINT, NODE
	}
	
	private Map<String, CCColladaSceneNode> _myNodeMap = new HashMap<String, CCColladaSceneNode>();
	private List<CCColladaSceneNode> _myNodes = new ArrayList<CCColladaSceneNode>();
	
	private CCMatrix4f _myMatrix;
	private CCColladaSceneNodeType _myType;

	public CCColladaSceneNode(CCXMLElement theNodeXML) {
		super(theNodeXML);
		
		_myType = CCColladaSceneNodeType.valueOf(theNodeXML.attribute("type", "NODE"));
		
		_myMatrix = new CCMatrix4f();
		CCXMLElement myMatrixXML = theNodeXML.child("matrix");
		if(myMatrixXML != null) {
			String[] myFloatStrings = myMatrixXML.content().split("\\s");
			float[] myFloats = new float[16];
			for(int i = 0; i < myFloats.length;i++) {
				myFloats[i] = Float.parseFloat(myFloatStrings[i]);
			}
			_myMatrix.setFromRowOrder(myFloats);
		}
		
		for(CCXMLElement myNodeXML:theNodeXML.children("node")) {
			CCColladaSceneNode myNode = new CCColladaSceneNode(myNodeXML);
			_myNodeMap.put(myNode.id(), myNode);
			_myNodes.add(myNode);
		}
	}
	
	public List<CCColladaSceneNode> children(){
		return _myNodes;
	}
	
	/**
	 * Use node() to get a certain child node.
	 * @param theIndex number of the child
	 * @return CCXMLElement, the child
	 */
	public CCColladaSceneNode node(final int theIndex){
		return _myNodes.get(theIndex);
	}
	
	/**
	 * Returns the first child node with the given node name. If there
	 * is no such child node the method returns null.
	 * @param theNodeName String
	 * @return CCXMLElement
	 */
	public CCColladaSceneNode node(final String theNodeName){
		if (theNodeName.indexOf('/') != -1) {
	      return nodeRecursive(CCStringUtil.split(theNodeName, '/'), 0);
	    }
		return _myNodeMap.get(theNodeName);
	}

	/**
	 * Internal helper function for {@linkplain #child(String)}
	 * 
	 * @param theItems result of splitting the query on slashes
	 * @param theOffset where in the items[] array we're currently looking
	 * @return matching element or null if no match
	 */
	protected CCColladaSceneNode nodeRecursive(String[] theItems, int theOffset) {
		// if it's a number, do an index instead
		if (Character.isDigit(theItems[theOffset].charAt(0))) {
			CCColladaSceneNode myResult = node(Integer.parseInt(theItems[theOffset]));
			if (theOffset == theItems.length - 1) {
				return myResult;
			} else {
				return myResult.nodeRecursive(theItems, theOffset + 1);
			}
		}

		CCColladaSceneNode myResult = node(theItems[theOffset]);

		if (theOffset == theItems.length - 1) {
			return myResult;
		} else {
			return myResult.nodeRecursive(theItems, theOffset + 1);
		}
	}
	
	public CCMatrix4f matrix() {
		return _myMatrix;
	}

	public CCColladaSceneNodeType type() {
		return _myType;
	}

	public void type(CCColladaSceneNodeType _myType) {
		this._myType = _myType;
	}
}
