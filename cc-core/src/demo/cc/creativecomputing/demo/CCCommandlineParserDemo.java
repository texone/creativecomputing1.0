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
import cc.creativecomputing.util.logging.CCLog;

public class CCCommandlineParserDemo extends CCApp{

	@Override
	public void setup(){
		CCLog.info(settings.getProperty("custom"));
//		CCLog.info("Vendor:"+g.vendor());
//		CCLog.info("Renderer:" + g.renderer());
//		CCLog.info("Version:" + g.version());
//		for(String myProfile:GLProfile.GL_PROFILE_LIST_ALL) {
//			CCLog.info(myProfile + ":" + GLProfile.isAvailable(myProfile));
//			
//		}
//		CCLog.info(GLProfile.getMaxFixedFunc(true).getName());
//		CCLog.info(GLProfile.getMaxProgrammable(true).getName());
//		
//		for(String myExtension:g.extensions()){
//			CCLog.info(myExtension);
//		}
	}
	
	@Override
	public void draw() {
		g.clear();
		
		g.line(mouseX,mouseY,100,100);
	}
	
	public static void main(String[] args) {
		
		CCApplicationManager myManager = new CCApplicationManager(CCCommandlineParserDemo.class);
		myManager.settings().addParameter("custom", true, "custom variable");	
		myManager.settings(args);
//		myManager.settings().displayMode(CCDisplayMode.NEWT);
//		myManager.settings().size(400, 400);
		myManager.start();
	}

}
