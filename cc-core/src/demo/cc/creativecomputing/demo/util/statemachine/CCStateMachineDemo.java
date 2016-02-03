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
package cc.creativecomputing.demo.util.statemachine;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.util.logging.CCLog;
import cc.creativecomputing.util.statemachine.CCState;
import cc.creativecomputing.util.statemachine.CCStateEvent;
import cc.creativecomputing.util.statemachine.CCStateMachine;

public class CCStateMachineDemo extends CCApp {
	
	CCStateMachine _myStateMachine = null;
	
	private static final String ResetEvent = "ResetEvent";
	private static final String StartEvent = "StartEvent";
	private static final String DoneEvent = "DoneEvent";
	
	final CCState _myInitState = new InitState();
	final CCState _myRunningState = new RunningState();
	final CCState _myDoneState = new DoneState();
	
	private class InitState extends CCState {
		
		@Override
		public void handleEvent(CCStateEvent theEvent) {
			CCLog.info("GET EVENT");
			switch(theEvent.id()){
				case StartEvent:	
					CCLog.info("start Handler in listener with returning data ");
					theEvent.addParameter("button_id", 1);
					theEvent.addParameter("content_id", 5);
					switchState(_myRunningState, theEvent);
					break;
			}
		}
		
		@Override
		public void onExit(CCState theNewStateId) {
			CCLog.info("exit init state listener");
		}
	}
	
	private class RunningState extends CCState {
		
		@Override
		public void onEnter(CCState theOldStateId, CCStateEvent theEvent) {
			CCLog.info("enter running state listener with incoming data: " + theEvent.parameter("startValue"));
		}
		
		@Override
		public void handleEvent(CCStateEvent theEvent) {
			switch(theEvent.id()){
				case DoneEvent:
					switchState(_myDoneState, theEvent);
					break;
			}
		}
	}

	private class DoneState extends CCState {
		
		DoneState() {
		}
		
		@Override
		public void handleEvent(CCStateEvent theEvent) {
			switch(theEvent.id()){
			case ResetEvent:
				CCLog.info("reset Handler without returning data, but incoming data value: " + theEvent.parameter("key"));
				switchState(_myInitState, theEvent);	
				break;
			}	
		}
	}
	
	@Override
	public void setup() {
		_myStateMachine = new CCStateMachine(_myInitState, _myRunningState, _myDoneState);
	}
			
	public void keyPressed(CCKeyEvent theKeyEvent) {
		
		switch(theKeyEvent.keyCode()) {
			case VK_R:
				_myStateMachine.handleEvent(new CCStateEvent(ResetEvent).addParameter("key", 5.4f));
				break;
			case VK_S:
				_myStateMachine.handleEvent(new CCStateEvent(StartEvent).addParameter("startValue", 1));
				break;
			case VK_D:
				_myStateMachine.handleEvent(new CCStateEvent(DoneEvent));
				break;
			default:
		}
		
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCStateMachineDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
