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
import cc.creativecomputing.events.CCEvent;
import cc.creativecomputing.events.CCEventAdapter;
import cc.creativecomputing.events.CCEventManager;
import cc.creativecomputing.util.logging.CCLog;

public class CCEventManagerDemo extends CCApp {
	
	private class CCUpdateEvent extends CCEvent{
		
		private float _myDeltaTime;

		public CCUpdateEvent(float theDeltaTime) {
			super("UPDATE");
			_myDeltaTime = theDeltaTime;
		}
	}
	
	public class CCUpdateListener extends CCEventAdapter<CCUpdateEvent>{

		public CCUpdateListener() {
			super(CCUpdateEvent.class);
		}

		@Override
		public void handleEvent(CCUpdateEvent theEvent) {
			System.out.println(theEvent._myDeltaTime);
		}
	}
	
	public class CCDrawEvent extends CCEvent{
		public CCDrawEvent() {
			super("DRAW");
		}
	}
	

	@Override
	public void setup() {
		CCEventManager.instance().addListener(new CCUpdateListener());
		CCEventManager.instance().addListener(new CCEventAdapter<CCDrawEvent>(CCDrawEvent.class) {
			@Override
			public void handleEvent(CCDrawEvent theEvent) {
				CCLog.info("YO DRAW");
			}
		});
	}

	@Override
	public void update(final float theDeltaTime) {
		CCEventManager.instance().trigger(new CCUpdateEvent(theDeltaTime));
	}

	@Override
	public void draw() {
		CCEventManager.instance().trigger(new CCDrawEvent());
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCEventManagerDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
