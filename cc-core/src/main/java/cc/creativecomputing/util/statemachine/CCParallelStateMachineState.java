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

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;

public class CCParallelStateMachineState extends CCState {
	
  private ArrayList<CCSubStateMachine> _mySubmachines = null;
	
	public CCParallelStateMachineState(ArrayList<CCSubStateMachine> theSubStates) {
		super();
    _mySubmachines = theSubStates;
	}

	public CCParallelStateMachineState(CCSubStateMachine...theSubStates){
		this(new ArrayList<CCSubStateMachine>(Arrays.asList(theSubStates)));
	}
	
	public void onEnter(CCState theOldStateId, CCStateEvent theEvent) {
    super.onEnter(theOldStateId, theEvent);
    Iterator<CCSubStateMachine> stateIterator = _mySubmachines.iterator();
    while(stateIterator.hasNext()){
      stateIterator.next().reset();
    }
	}

	public void onExit(CCState theOldStateId) {
    super.onExit(theOldStateId);
    Iterator<CCSubStateMachine> stateIterator = _mySubmachines.iterator();
    while(stateIterator.hasNext()){
      stateIterator.next().teardown();
    }
	}

	public void handleEvent(CCStateEvent theEvent) {
    super.handleEvent(theEvent);
    Iterator<CCSubStateMachine> stateIterator = _mySubmachines.iterator();
    while(stateIterator.hasNext()){
      stateIterator.next().handleEvent(theEvent);
    }
	}

}
