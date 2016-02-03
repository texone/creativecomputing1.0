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
package cc.creativecomputing.graphics.font.text;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.graphics.font.text.CCTextUtils;
import cc.creativecomputing.graphics.font.util.CCLoremIpsumGenerator;
import cc.creativecomputing.util.logging.CCLog;

public class CCTextUtilsTest extends CCApp {

	@Override
	public void setup() {
		CCTextureMapFont font = CCFontIO.createTextureMapFont( "Verdana", 18);	
		
		String t = CCLoremIpsumGenerator.generate(60);
		CCLog.info(t);
		CCLog.info ("\n---------------------\n");
		CCLog.info(  ".." + CCTextUtils.linebreakStringToFitInWidth( 300, font, 14, t ) + ".." );
		CCLog.info ("\n---------------------");
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTextUtilsTest.class);
		myManager.start();
	}
}

