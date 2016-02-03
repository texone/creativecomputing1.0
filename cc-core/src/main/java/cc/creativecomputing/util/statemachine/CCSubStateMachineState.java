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

public class CCSubStateMachineState extends CCState {
	
	private CCSubStateMachine _mySubmachine = null;
	
	
	public CCSubStateMachine getSubmachine() {
		return _mySubmachine;
	}

	public CCSubStateMachineState(CCSubStateMachine theSubMachine) {
		super();
		_mySubmachine = theSubMachine;
	}
	
	public void onEnter(CCState theOldStateId, CCStateEvent theEvent) {
		_mySubmachine.reset();
	}
	
	public void onExit() {
		_mySubmachine.teardown();
  }

	public void onExit(CCState theOldStateId) {
		_mySubmachine.teardown();
	}
	
}
