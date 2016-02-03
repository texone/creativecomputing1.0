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
package cc.creativecomputing.demo.animation;


import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.animation.CCAnimation;
import cc.creativecomputing.animation.CCAnimation.CCAnimationAdapter;
import cc.creativecomputing.animation.CCAnimationManager;
import cc.creativecomputing.animation.CCDelayAnimation;
import cc.creativecomputing.animation.CCParallelAnimation;
import cc.creativecomputing.animation.CCSequenceAnimation;
import cc.creativecomputing.math.easing.CCEasing;
import cc.creativecomputing.math.easing.CCEasing.CCEaseFormular;
import cc.creativecomputing.util.logging.CCLog;

public class CCCompositeAnimationDemo extends CCApp{
	
	private CCAnimationManager _myManager = new CCAnimationManager();

	@Override
	public void setup(){

		_myManager.init(0);
		CCSequenceAnimation mySequenceAnimation = new CCSequenceAnimation();
		CCParallelAnimation myParallelAnimation = new CCParallelAnimation();
		
		CCDelayAnimation myDelayAnimation0 = new CCDelayAnimation(1);
		myDelayAnimation0.events().add(new CCAnimationAdapter() {
			
			@Override
			public void onPlay(CCAnimation theAnimation) {
				CCLog.info("start cb of animation 0");
			}
			
			@Override
			public void onFinish(CCAnimation theAnimation) {
				CCLog.info("finish cb of animation 0");
			}
		});
		
		CCDelayAnimation myDelayAnimation1 = new CCDelayAnimation(2);
		myDelayAnimation1.events().add(new CCAnimationAdapter() {
			
			@Override
			public void onPlay(CCAnimation theAnimation) {
				CCLog.info("start cb of animation 1");
			}
			
			@Override
			public void onFinish(CCAnimation theAnimation) {
				CCLog.info("finish cb of animation 1");
			}
		});

		CCDelayAnimation myDelayAnimation2 = new CCDelayAnimation(3);
		myDelayAnimation2.events().add(new CCAnimationAdapter() {
			
			@Override
			public void onPlay(CCAnimation theAnimation) {
				CCLog.info("start cb of animation 2");
			}
			
			@Override
			public void onFinish(CCAnimation theAnimation) {
				CCLog.info("finish cb of animation 2");
			}
		});

		CCAnimation myAnimation = new CCAnimation(3,CCEaseFormular.CUBIC,CCEasing.CCEaseMode.OUT);
		myAnimation.events().add(new CCAnimationAdapter() {
			
			@Override
			public void onProgress(CCAnimation theAnimation, float theProgress) {
				CCLog.info("closure val: "+ theProgress);
			}
		});

		mySequenceAnimation.add(myDelayAnimation0);
		mySequenceAnimation.add(myDelayAnimation1);
		mySequenceAnimation.add(myParallelAnimation);
		
		myParallelAnimation.add(myDelayAnimation2);
		myParallelAnimation.add(myAnimation);
		
		_myManager.play(mySequenceAnimation);		
	}
	
	@Override
	public void update(final float theDeltaTime) {
		//CCLog.info("update time:"+theDeltaTime);
		_myManager.update(theDeltaTime);
	}	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCCompositeAnimationDemo.class);
		myManager.settings().size(400, 400);
		myManager.start();
	}

}
