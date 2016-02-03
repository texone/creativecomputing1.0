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
import java.util.HashMap;
import java.util.List;

import cc.creativecomputing.events.CCEventListener;

public abstract class CCState implements CCEventListener<CCStateEvent>{
	
	private CCStateMachine _myStateMachine;
	private List<CCEventListener<?>> _myListener;
	
	public CCState(){
		_myListener = new ArrayList<>();
	}
	
	public void addListener(CCEventListener<?> theListener){
		_myListener.add(theListener);
	}

	public void onExit(CCState theNewStateId) {
	}

	public void onEnter(CCState theOldStateId, CCStateEvent theEvent) {
	}
	
	public void registerStatemachine(CCStateMachine theStatemachine) {
		_myStateMachine = theStatemachine;
	}
	
	public void switchState(CCState theState, CCStateEvent theEvent){
		_myStateMachine.switchState(theState, theEvent);
	}
	
	public void startTransition(CCStateTransition theTransition){
		_myStateMachine.startTransition(theTransition);
	}

	public Object onHandleTransitionEvent(String theEvent, HashMap<String, Object> theData) {
		return null;
	}
	
	public List<CCEventListener<?>> listener(){
		return _myListener;
	}
	
	@Override
	public void handleEvent(CCStateEvent theEvent) {
//		return theEvent;
	}
	
	@Override
	public Class<CCStateEvent> eventType() {
		return CCStateEvent.class;
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	};
}
