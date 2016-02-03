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
package cc.creativecomputing.demo;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.xml.CCXMLElement;

public class CCAppSettingsXMLDemo extends CCApp {

	@Override
	public void setup() {
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCAppSettingsXMLDemo.class);
		CCXMLElement myXML = new CCXMLElement("settings");
		myXML.createChild("height", 700);
		myXML.createChild("width", 300);
		myXML.createChild("undecorated", true);
		myManager.settings(myXML);
		myManager.start();
	}
}

