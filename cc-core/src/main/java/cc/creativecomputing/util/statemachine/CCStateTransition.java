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

import cc.creativecomputing.util.logging.CCLog;

public class CCStateTransition {
	
	private CCState _myNextState = null;
	private CCStateMachine _myStatemachine = null;
	CCStateEvent _myEvent = null;
	
	public CCStateTransition(CCState theNextState, CCStateEvent theEvent) {
		_myNextState = theNextState;
		_myEvent = theEvent;
	}
	
	protected CCState nextState() {
		return _myNextState;
	}
	
	public void registerStatemachine(CCStateMachine theStateMachine) {
		_myStatemachine = theStateMachine;
	}
	
	protected void switchState() {
		if (_myStatemachine != null) {
			_myStatemachine.switchState(_myNextState, _myEvent);
		} else {
			CCLog.error("Sorry, do not know how to switch state!");
		}
	}
		
	public void travel() {
		switchState();
	}

	public void terminate() {
		switchState();
	}
	
	public boolean handleEvent() {
		return false;
	}
}
