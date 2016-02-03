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

import cc.creativecomputing.xml.CCXMLElement;

/**
 * @author christianriekoff
 *
 */
public class CCColladaElement {

	protected String _myID;
	
	protected Map<String, CCColladaSource> _mySourceMap;
	
	CCColladaElement(CCXMLElement theXML){
		_myID = theXML.attribute("id", "");
		
		_mySourceMap = new HashMap<String, CCColladaSource>();
		
		for(CCXMLElement mySourceXML:theXML.children("source")) {
			CCColladaSource mySource = new CCColladaSource(mySourceXML);
			_mySourceMap.put(mySource.id(), mySource);
		}
	}

	/**
	 * 
	 * @return the ID of the Effect-Tag
	 */
	public String id() {
		return _myID;
	}
	
	protected CCColladaSource source(CCXMLElement theElement, String theSematic) {
		for (CCXMLElement myInputXML : theElement.children("input")) {
			if (myInputXML.attribute("semantic").equals(theSematic)) {
				return _mySourceMap.get(myInputXML.attribute("source").substring(1));
			}
		}
		return null;
	}
}
