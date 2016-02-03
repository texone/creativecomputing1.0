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
package cc.creativecomputing.demo.events;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCMouseWheelEvent;
import cc.creativecomputing.util.logging.CCLog;

public class CCMouseWheelEventDemo extends CCApp {
	
	private float _myX = 0;
	private float _myY = 0;

	@Override
	public void setup() {

	}

	@Override
	public void update(final float theDeltaTime) {
	}
	
	@Override
	public void mouseWheelMoved(CCMouseWheelEvent theMouseEvent) {
		if(theMouseEvent.isHorizontal()){
			_myX += theMouseEvent.rotation();
			CCLog.info("horizontal:" + theMouseEvent.rotation());
		}else{
			_myY -= theMouseEvent.rotation();
			CCLog.info("vertical:" + theMouseEvent.rotation());
		}
	}

	@Override
	public void draw() {
		g.clear();
		
		g.ellipse(_myX, _myY, 20);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCMouseWheelEventDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

