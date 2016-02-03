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
import cc.creativecomputing.animation.CCAnimatedBlend;
import cc.creativecomputing.animation.CCAnimation;
import cc.creativecomputing.animation.CCAnimationManager;
import cc.creativecomputing.animation.CCSequenceAnimation;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.math.easing.CCEasing;
import cc.creativecomputing.math.easing.CCEasing.CCEaseFormular;

public class CCAnimatedBlendDemo extends CCApp{
	
	private CCAnimationManager _myManager = new CCAnimationManager();
	private CCColor _myWhiteColor = new CCColor(255);
	private CCColor _myBlackColor = new CCColor(0);
	private CCColor _myBgColor = new CCColor(0);

	@Override
	public void setup(){

		_myManager.init(0);
		
		CCSequenceAnimation mySequence = new CCSequenceAnimation();
		mySequence.loop(true);

		CCAnimation myAnimation = new CCAnimation(1,CCEaseFormular.SINE,CCEasing.CCEaseMode.IN);
		myAnimation.events().add(new CCAnimatedBlend<CCColor>(_myBgColor, _myBlackColor, _myWhiteColor));
		mySequence.add(myAnimation);
		
		CCAnimation myAnimation2 = new CCAnimation(1,CCEaseFormular.SINE,CCEasing.CCEaseMode.IN);
		myAnimation2.events().add(new CCAnimatedBlend<CCColor>(_myBgColor, _myWhiteColor, _myBlackColor));
		mySequence.add(myAnimation2);
		
		_myManager.play(mySequence);		
	}
	
	@Override
	public void update(final float theDeltaTime) {
		//CCLog.info("update time:"+theDeltaTime);
		_myManager.update(theDeltaTime);
	}	
	
	@Override
	public void draw() {
		g.clearColor(_myBgColor);
		g.clear();
	}
	
	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCAnimatedBlendDemo.class);
		myManager.settings().size(400, 400);
		myManager.start();
	}

}
