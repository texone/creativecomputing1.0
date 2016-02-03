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

import java.util.List;

import cc.creativecomputing.xml.CCXMLElement;

/**
 * <p>
 * Lucerne University of Applied Sciences and Arts <a href="http://www.hslu.ch">http://www.hslu.ch</a>
 * </p>
 * 
 * <p>
 * This source is free; you can redistribute it and/or modify it under the terms of the GNU General Public License and
 * by nameing of the originally author
 * </p>
 * 
 * <p>
 * this class maps the Library_Materials -tag
 * </p>
 * 
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @author christianriekoff
 * @version 1.0
 */
class CCColladaMaterials extends CCColladaLibrary<CCColladaMaterial>{

	CCColladaMaterials(List<CCXMLElement> theMaterials, CCColladaEffects theEffectLib) {
		for (CCXMLElement myMaterialXML : theMaterials) {
			CCColladaMaterial m = new CCColladaMaterial(myMaterialXML, theEffectLib);
			_myElementMap.put(m.id(), m);
		}
	}

	public String toString() {
		String s = "List of Materials: \n";
		for (CCColladaMaterial m : _myElementMap.values())
			s += m + "\n";

		return s;

	}

}
