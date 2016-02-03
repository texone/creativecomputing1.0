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

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.util.CCStringUtil;
import cc.creativecomputing.xml.CCXMLElement;

/**
 * Embodies the entire set of information that can be visualized from the contents of a COLLADA resource.
 * <p>
 * The hierarchical structure of the visual_scene is organized into a scene graph. A scene graph is a directed 
 * acyclic graph (DAG) or tree data structure that contains nodes of visual information and related data. 
 * The structure of the scene graph contributes to optimal processing and rendering of the data and is therefore 
 * widely used in the computer graphics domain.
 * @author christianriekoff
 *
 */
public class CCColladaScene extends CCColladaElement{

	private String _myName;
	
	private Map<String, CCColladaSceneNode> _myNodeMap = new HashMap<String, CCColladaSceneNode>();
	
	/**
	 * @param theXML
	 */
	CCColladaScene(CCXMLElement theSceneXML) {
		super(theSceneXML);
		
		name(theSceneXML.attribute("name", ""));
		
		for(CCXMLElement myNodeXML:theSceneXML.children("node")) {
			CCColladaSceneNode myNode = new CCColladaSceneNode(myNodeXML);
			_myNodeMap.put(myNode.id(), myNode);
		}
	}

	public CCColladaSceneNode node(String theID) {
		if (theID.indexOf('/') != -1) {
			String[] myNodeParts = CCStringUtil.split(theID, '/');
			CCColladaSceneNode myNode = _myNodeMap.get(myNodeParts[0]);

			if(myNodeParts.length > 1) {
				return myNode.nodeRecursive(myNodeParts, 1);
			}
			return myNode;
		}
		return _myNodeMap.get(theID);
	}

	public String name() {
		return _myName;
	}

	public void name(String _myName) {
		this._myName = _myName;
	}
}
