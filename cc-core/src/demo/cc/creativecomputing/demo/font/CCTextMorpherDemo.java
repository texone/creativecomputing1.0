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
package cc.creativecomputing.demo.font;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.util.CCLoremIpsumGenerator;
import cc.creativecomputing.math.CCMath;

public class CCTextMorpherDemo extends CCApp {
	
	public String morphString(final String theStart, final String theEnd, final float blend) {
		StringBuilder result = new StringBuilder();
		
		
		int stringLength = theStart.length() + (int)((theEnd.length() - theStart.length()) * blend);
		
		for(int i = 0; i < stringLength;i++) {
			char startChar = theStart.length() > i ? theStart.charAt(i) : 'a';
			char endChar = theEnd.length() > i ? theEnd.charAt(i) : 'a';
			
			int dif = endChar - startChar;
			char resultChar = (char)(startChar + (int)(dif * blend));
			result.append(resultChar);
		}
		
		return result.toString();
	}
	
	private String _myString1;
	private String _myString2;
	private String _myResult;

	@Override
	public void setup() {
		_myString1 = CCLoremIpsumGenerator.generate(10);
		_myString2 = CCLoremIpsumGenerator.generate(10);
		
		g.textFont(CCFontIO.createTextureMapFont("Courier", 18));
	}
	
	private float _myTime = 0;
	

	@Override
	public void update(final float theDeltaTime) {
		_myTime += theDeltaTime * 0.2;
		float myBlend = CCMath.round((CCMath.sin(_myTime) + 1) / 2, 2);
		_myResult = morphString(_myString1, _myString2, myBlend);
	}

	@Override
	public void draw() {
		g.clear();
		g.text(_myResult, -width/2 + 10,0);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(
				CCTextMorpherDemo.class);
		myManager.settings().size(1000, 500);
		myManager.start();
	}
}

