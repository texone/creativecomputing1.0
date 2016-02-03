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

import cc.creativecomputing.animation.CCAnimation;
import cc.creativecomputing.animation.CCAnimationManager;
import cc.creativecomputing.animation.CCAnimation.CCAnimationAdapter;
import cc.creativecomputing.util.logging.CCLog;

public class CCAnimationTransition extends CCStateTransition {
	
	private CCAnimation _myAnimation = null;
	private CCAnimationManager _myAnimationManager = null;
	
	private class CCAnimationTransitionI extends CCAnimationAdapter{
		@Override
		public void onFinish(CCAnimation theAnimation) {
			super.onPlay(theAnimation);		
			switchState();
		}
	}
	
	public CCAnimationTransition(CCState theNextState, CCAnimationManager theAnimationManager, CCAnimation theAnimation, CCStateEvent theEvent ) {
		super(theNextState, theEvent);
		_myAnimationManager = theAnimationManager;
		_myAnimation = theAnimation;
		_myAnimation.events().add(new CCAnimationTransitionI());
	}
	public CCAnimationTransition(CCState theNextState,CCAnimationManager theAnimationManager, CCAnimation theAnimation) {
		super(theNextState, null);
		_myAnimationManager = theAnimationManager;
		_myAnimation = theAnimation;
		_myAnimation.events().add(new CCAnimationTransitionI());
	}
	
	public void terminate() {
		_myAnimation.cancel();
	}
	
	public void travel() {
		CCLog.info("---- AnimationTransition: travel, nextState: " + nextState().getClass().getName());
		if (_myAnimation.isRunning()) {
			CCLog.error("---- AnimationTransition: travel animation already running, canceling!");
			_myAnimation.cancel();
		}
		_myAnimationManager.play(_myAnimation);				
	}
}
