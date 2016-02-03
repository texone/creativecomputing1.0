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
import cc.creativecomputing.util.statemachine.CCSubStateMachine;
import cc.creativecomputing.util.statemachine.CCSubStateMachineState;

public class CCSubStatemachineDemo extends CCApp {
	
	private CCStateMachine _myStateMachine = null;
	private CCSubStateMachine _mySubStateMachine = null;
	
	// states sub statemachine
	private ActiveState1 _myActiveState1 = null;
	private ActiveState2 _myActiveState2 = null;
	
	// states main statemachine
	private InitState _myInitState = null;
	private ActiveState _myActiveState = null;
	
	private static final String ResetEvent = "ResetEvent";
	private static final String StartEvent = "StartEvent";
	private static final String ActivePart1DoneEvent = "ActiveStatePart1Done";
	
	
	class InitState extends CCState {
		@Override
		public void onEnter(CCState theOldState, CCStateEvent theEvent) {
			CCLog.info("enter " + _myInitState);
		}
		// handler for event "START_EVENT", returns ACTIVE_STATE without data
		@Override
		public void handleEvent(CCStateEvent theEvent) {
			switch(theEvent.id()) {
			case StartEvent:
				switchState(_myActiveState, theEvent);
				break;
			
			}
		}
		
	}
	class ActiveState extends CCSubStateMachineState {
		
		public ActiveState(CCSubStateMachine theSubMachine) {
			super(theSubMachine);
			// TODO Auto-generated constructor stub
		}
		@Override
		public void onEnter(CCState theOldStateId, CCStateEvent theEvent) {
			super.onEnter(theOldStateId, theEvent);
			CCLog.info("enter " + _myActiveState);
		}
		// handler for event "RESET_EVENT", returns INIT_STATE without data
		@Override
		public void handleEvent(CCStateEvent theEvent) {
			switch(theEvent.id()) {
				case ResetEvent:
					switchState(_myInitState, theEvent);
					break;
				
			}
		}
		
	}
	class ActiveState1 extends CCState {
		
		@Override
		public void onEnter(CCState theOldStateId, CCStateEvent theEvent) {
			CCLog.info("enter " + _myActiveState1);
		}
		// handler for event "RUNNING_PART_1_DONE_EVENT", returns ACTIVE_STATE_PART_2 without data
		@Override
		public void handleEvent(CCStateEvent theEvent) {
			switch(theEvent.id()) {
			case ActivePart1DoneEvent:
				switchState(_myActiveState2, theEvent);
				break;
			
			}
		}
	}
	class ActiveState2 extends CCState {
		
		@Override
		public void onEnter(CCState theOldStateId, CCStateEvent theEvent) {
			CCLog.info("enter " + _myActiveState2);
		}
	}

	@Override
	public void setup() {
		_myActiveState1 = new ActiveState1();
		_myActiveState2 = new ActiveState2();
		_mySubStateMachine = new CCSubStateMachine(_myActiveState1, _myActiveState2);
		
		// states main statemachine
		_myInitState = new InitState();
		_myActiveState = new ActiveState(_mySubStateMachine);
		_myStateMachine = new CCStateMachine(_myInitState, _myActiveState);
	}

	@Override
	public void update(final float theDeltaTime) {
	}

	@Override
	public void draw() {
		g.clear();
	}
	
	public void keyPressed(CCKeyEvent theKeyEvent) {
		
		switch(theKeyEvent.keyCode()) {
			case VK_R:
				_myStateMachine.handleEvent(new CCStateEvent(ResetEvent));
				break;
			case VK_S:
				_myStateMachine.handleEvent(new CCStateEvent(StartEvent));
				break;
			case VK_A:
				_myStateMachine.handleEvent(new CCStateEvent(ActivePart1DoneEvent));
				break;
			default:
		}
		
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(
				CCSubStatemachineDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
