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
import cc.creativecomputing.animation.CCAnimation;
import cc.creativecomputing.animation.CCAnimation.CCAnimationAdapter;
import cc.creativecomputing.animation.CCAnimationManager;
import cc.creativecomputing.animation.CCDelayAnimation;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.math.easing.CCEasing;
import cc.creativecomputing.math.easing.CCEasing.CCEaseFormular;
import cc.creativecomputing.util.logging.CCLog;
import cc.creativecomputing.util.statemachine.CCAnimationTransition;
import cc.creativecomputing.util.statemachine.CCState;
import cc.creativecomputing.util.statemachine.CCStateEvent;
import cc.creativecomputing.util.statemachine.CCStateMachine;

public class CCAnimationStateTransitionDemo extends CCApp {
	
CCStateMachine _myStateMachine = null;
	
	private static final String ResetEvent = "ResetEvent";
	private static final String StartEvent = "StartEvent";
	
	final CCState _myInitState = new InitState();
	final CCState _myRunningState = new RunningState();
	
	
	private CCAnimationManager _myAnimationManager = new CCAnimationManager();
	
	private class CCAnimationExample extends CCAnimationAdapter{
		@Override
		public void onPlay(CCAnimation theAnimation) {
			super.onPlay(theAnimation);	
			CCLog.info("on play in animation Example");
		}
	}

	private class CCTransitionAnimationClosure extends CCAnimationAdapter{
		@Override
		public void onProgress(CCAnimation theAnimation, float theProgress) {
			CCLog.info("closure val: "+ theProgress);
		}
	}
	
	private class InitState extends CCState {
		
		@Override
		public void handleEvent(CCStateEvent theEvent) {
			switch(theEvent.id()){
				case StartEvent:	
					CCAnimation myA = new CCAnimation(3,CCEaseFormular.CUBIC,CCEasing.CCEaseMode.OUT);
					myA.events().add(new CCTransitionAnimationClosure());
					startTransition(new CCAnimationTransition(_myRunningState, _myAnimationManager, myA, theEvent));
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
		public void onEnter(CCState theOldState, CCStateEvent theEvent) {
			//CCLog.info("enter running state listener");
			CCLog.info("enter running state listener with incoming data: " + theEvent.parameter("startValue"));
		}
		
		@Override
		public void handleEvent(CCStateEvent theEvent) {
			switch(theEvent.id()){
				case ResetEvent:
					CCDelayAnimation myAnimation = new CCDelayAnimation(1);
					myAnimation.events().add(new CCAnimationExample());
					startTransition(new CCAnimationTransition(_myInitState, _myAnimationManager, myAnimation));
					break;
			}
		}
	}
	
	@Override
	public void setup() {
		_myStateMachine = new CCStateMachine(_myInitState, _myRunningState);
	}
			
	public void keyPressed(CCKeyEvent theKeyEvent) {
		
		switch(theKeyEvent.keyCode()) {
			case VK_R:
				_myStateMachine.handleEvent(new CCStateEvent(ResetEvent));
				break;
			case VK_S:
				_myStateMachine.handleEvent(new CCStateEvent(StartEvent).addParameter("startValue", 5.5));
				break;
			default:
		}
		
	}

	@Override
	public void update(final float theDeltaTime) {
		_myAnimationManager.update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCAnimationStateTransitionDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}
