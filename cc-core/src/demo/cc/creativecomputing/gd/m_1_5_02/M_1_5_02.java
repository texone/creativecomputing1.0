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
package cc.creativecomputing.gd.m_1_5_02;

import java.util.Calendar;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.control.CCControl;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector2f;

public class M_1_5_02 extends CCApp {

	public class Agent {
		CCVector2f p, pOld;
		float stepSize, angle;
		boolean isOutside = false;

		Agent() {
			p = new CCVector2f(CCMath.random(width), CCMath.random(height));
			pOld = new CCVector2f(p.x, p.y);
			stepSize = CCMath.random(1, 5);
		}

		void update1() {
			angle = CCMath.noise(p.x / noiseScale, p.y / noiseScale) * noiseStrength;

			p.x += CCMath.cos(angle) * stepSize;
			p.y += CCMath.sin(angle) * stepSize;

			if (p.x < -10)
				isOutside = true;
			else if (p.x > width + 10)
				isOutside = true;
			else if (p.y < -10)
				isOutside = true;
			else if (p.y > height + 10)
				isOutside = true;

			if (isOutside) {
//				p.x = CCMath.random(width);
//				p.y = CCMath.random(height);
				p.set(CCVecMath.random2f(CCMath.random(emitRadius)));
				p.add(mouseX, mouseY);
				pOld.set(p);
			}

//			g.strokeWeight(strokeWidth * stepSize);
			g.line(pOld.x, pOld.y, p.x, p.y);

			pOld.set(p);

			isOutside = false;
		}

		void update2() {
			angle = CCMath.noise(p.x / noiseScale, p.y / noiseScale) * 24;
			angle = (angle - (int) (angle)) * noiseStrength;

			p.x += CCMath.cos(angle) * stepSize;
			p.y += CCMath.sin(angle) * stepSize;

			if (p.x < -10)
				isOutside = true;
			else if (p.x > width + 10)
				isOutside = true;
			else if (p.y < -10)
				isOutside = true;
			else if (p.y > height + 10)
				isOutside = true;

			if (isOutside) {
//				p.x = CCMath.random(width);
//				p.y = CCMath.random(height);
				p.x = mouseX + CCMath.random(-30,30);
				p.y = mouseX + CCMath.random(-30,30);
				pOld.set(p);
			}

//			g.strokeWeight(strokeWidth * stepSize);
			g.line(pOld.x, pOld.y, p.x, p.y);

			pOld.set(p);

			isOutside = false;
		}
	}

	// ------ agents ------
	Agent[] agents = new Agent[10000]; // create more ... to fit max slider agentsCount

	@CCControl(name = "agents count", min = 1, max = 10000)
	private int agentsCount = 4000;
	
	@CCControl(name = "emit radius", min = 1, max = 300)
	private int emitRadius = 100;

	@CCControl(name = "noise scale", min = 1, max = 100)
	private float noiseScale = 30;
	@CCControl(name = "noise strength", min = 0, max = 10)
	private float noiseStrength = 10;

	@CCControl(name = "stroke width", min = 0, max = 10)
	private float strokeWidth = 0.3f;

	@CCControl(name = "overlay alpha", min = 0, max = 1)
	private float overlayAlpha = 1;
	@CCControl(name = "agents alpha", min = 0, max = 1)
	private float agentsAlpha = 90;
	@CCControl(name = "agents r", min = 0, max = 1)
	private float agentsR = 90;
	@CCControl(name = "agents g", min = 0, max = 1)
	private float agentsG = 90;
	@CCControl(name = "agents b", min = 0, max = 1)
	private float agentsB = 90;

	int drawMode = 1;

	@Override
	public void setup() {

		for (int i = 0; i < agents.length; i++) {
			agents[i] = new Agent();
		}
		
		addControls("app", "app", this);
		g.clear();
	}

	public void draw() {
		g.translate(-width/2, -height/2);
		g.color(1f, overlayAlpha);
		g.rect(0, 0, width, height);

		g.color(agentsR, agentsG, agentsB, agentsAlpha);
		// draw agents
		if (drawMode == 1) {
			for (int i = 0; i < agentsCount; i++)
				agents[i].update1();
		} else {
			for (int i = 0; i < agentsCount; i++)
				agents[i].update2();
		}
	}

	 @Override
	 public void keyReleased(CCKeyEvent theEvent){
		  
	
//	 if (key == '1') drawMode = 1;
//	 if (key == '2') drawMode = 2;
	 if (key=='s' || key=='S') CCScreenCapture.capture(timestamp()+".png", width, height);
//	 if (key == ' ') {
//	 int newNoiseSeed = (int) CCMath.random(100000);
//	 noiseSeed(newNoiseSeed);
//	 }
//	 if (key == DELETE || key == BACKSPACE) background(255);
	 }

	String timestamp() {
		return String.format("%1$ty%1$tm%1$td_%1$tH%1$tM%1$tS", Calendar.getInstance());
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(M_1_5_02.class);
		myManager.settings().size(1200, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}
