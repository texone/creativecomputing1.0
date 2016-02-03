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
package cc.creativecomputing.demo.graphics.shader.simulation.dla;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCText;

public class CCDLADemo extends CCApp {
	
	private CCGPUDLA _myDLA;
	private CCText _myText;
	
	public void setup() {
		_myDLA = new CCGPUDLA(g, 150, 150, width, height);
		_myText = new CCText(CCFontIO.createVectorFont("arial", 360));
		_myText.position(50,250);
		_myText.text("DLA");
		addControls("dla", "dla", _myDLA);
		CCFontIO.printFontList();
		
		_myDLA.beginCrystal();
		_myText.draw(g);
		_myDLA.endCrystal();
	}
	
	public void update(final float theDeltaTime) {
		_myDLA.update(theDeltaTime);
	}

	public void draw() {
		g.clear();
		g.blend(CCBlendMode.LIGHTEST);
		_myDLA.draw(g);
		g.blend();
	}
	
	public void keyPressed(final CCKeyEvent theEvent) {
		_myDLA.reset();
		_myDLA.beginCrystal();
		_myText.draw(g);
		_myDLA.endCrystal();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCDLADemo.class);
		myManager.settings().size(800, 800);
		myManager.settings().vsync(false);
		myManager.start();
	}
}

