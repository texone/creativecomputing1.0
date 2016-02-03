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
import cc.creativecomputing.CCApplicationSettings.CCDisplayMode;
import cc.creativecomputing.CCApplicationSettings.CCGLContainer;
import cc.creativecomputing.events.CCMouseAdapter;
import cc.creativecomputing.events.CCMouseEvent;
import cc.creativecomputing.util.logging.CCLog;

public class CCNewtDemo extends CCApp{

	@Override
	public void setup(){
		CCLog.info("Vendor:"+g.vendor());
		CCLog.info("Renderer:" + g.renderer());
		CCLog.info("Version:" + g.version());
		
		for(String myExtension:g.extensions()){
			CCLog.info(myExtension);
		}
		
		addMouseListener(new CCMouseAdapter() {
			@Override
			public void mousePressed(CCMouseEvent theEvent) {
				CCLog.info("MOUSE PRESSED:" + theEvent.x() + " : " + theEvent.y());
			}
			
			public void mouseDragged(CCMouseEvent theMouseEvent) {
				CCLog.info("MOUSE DRAGGED:" + theMouseEvent.x() + " : " + theMouseEvent.y());
			}
		});
		
		addMouseMotionListener(new CCMouseAdapter() {
			@Override
			public void mouseDragged(CCMouseEvent theMouseEvent) {
				CCLog.info("MOUSE DRAGGED:" + theMouseEvent.x() + " : " + theMouseEvent.y());
			}
		});
		
		CCLog.info(width+" : "+ height);
	}
	
	@Override
	public void draw() {
		g.clearColor(100);
		g.clear();

		CCLog.info(width+" : "+ height);
		CCLog.info(g.width+" : "+ g.height);
		g.line(frameCount % width - width/2,-height/2 + 5,frameCount % width - width/2,height/2 - 5);
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCNewtDemo.class);
		myManager.settings().container(CCGLContainer.NEWT);
//		myManager.settings().displayMode(CCDisplayMode.FULLSCREEN);
		myManager.settings().size(400, 400);
		myManager.settings().antialiasing(8);
		myManager.settings().frameRate(60);
		myManager.start();
	}

}
