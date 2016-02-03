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

import cc.creativecomputing.xml.CCXMLElement;

/*
 * 
 */
public class CCColladaController extends CCColladaElement{
	
	private CCColladaSkinController _mySkinController;

	CCColladaController(CCXMLElement theControllerXML, CCColladaGeometries theGeometries){
		super(theControllerXML);
		
		CCXMLElement mySkinXML = theControllerXML.child("skin");
		if(mySkinXML != null) {
			_mySkinController = new CCColladaSkinController(mySkinXML, theGeometries, theControllerXML.attribute("id"));
		}
	}
	
	public CCColladaSkinController skin() {
		return _mySkinController;
	}
}
