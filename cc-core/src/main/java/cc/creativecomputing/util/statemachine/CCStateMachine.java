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
package cc.creativecomputing.util.statemachine;

import java.util.ArrayList;
import java.util.Arrays;

import cc.creativecomputing.events.CCEventManager;
import cc.creativecomputing.util.logging.CCLog;

public class CCStateMachine {
	
	private CCState _myCurrentState = null;
	private CCState _myInitialState = null;
	private CCStateTransition _myTransition = null;
	private CCEventManager _myEventManager = null;
	
	public CCStateMachine(ArrayList<CCState> theStates) {
		_myEventManager = new CCEventManager();
		_myInitialState = theStates.get(0);
		for(CCState myState:theStates){
			myState.registerStatemachine(this);
		}
		setup();
	}
	
	protected void setup() {
		switchState(_myInitialState);
	}
	
	public CCStateMachine(CCState...theStates){
		this(new ArrayList<CCState>(Arrays.asList(theStates)));
	}
	
	public CCState getCurrentState() {
		return _myCurrentState;
	}
	
	public void reset(){
		switchState(_myInitialState);
	}

	public void teardown() {
		if (_myTransition != null) {
			CCLog.info("<StateMachine::teardown> terminated transition on teardown");
			_myTransition.terminate();
		}
		switchState(null, null);
	}
	
	public void switchState(CCState theNewStateId) {
		switchState(theNewStateId, null);
	}
	
	public void handleEvent(CCStateEvent theEvent) {
		if (_myCurrentState instanceof CCSubStateMachineState) {
			((CCSubStateMachineState) _myCurrentState).getSubmachine().handleEvent(theEvent);
		}
		_myEventManager.trigger(theEvent);
	}

	public void switchState(CCState theNewStateId, CCStateEvent theEvent) {
		if (_myTransition != null) {
			_myTransition = null;
		}
//		CCLog.info("State transition '" + _myCurrentState + "' => '" + theNewStateId + "'");
		// call old state's exit handler
		if (_myCurrentState != null) {
//			CCLog.info("<StateMachine::switchState> exiting state '" + _myCurrentState + "' and calling its exit listener");
			_myCurrentState.onExit(theNewStateId);
			_myEventManager.removeListener(_myCurrentState);
		}
		CCState myOldState = _myCurrentState;
		_myCurrentState = theNewStateId;
		if (_myCurrentState != null) {
//			CCLog.info("<StateMachine::switchState> entering state '" + _myCurrentState);
			_myEventManager.addListener(_myCurrentState);
			_myCurrentState.onEnter(myOldState, theEvent);
		}

	}
	
	public void startTransition(CCStateTransition theTransition){
		_myTransition = theTransition;
		_myTransition.registerStatemachine(this);
		_myTransition.travel();
	}
};
