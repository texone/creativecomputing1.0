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
package cc.creativecomputing.demo.topic.color;

import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.util.logging.CCLog;

/**
 * @author christianriekoff
 * 
 */
public class CCColorHSBRGBConverstionDemo {
	public static void main(String[] args) {

		CCColor myColor1 = new CCColor(0.4f, 0.3f, 0.8f, 1.0f);
		CCLog.info("1. r: " + myColor1.r + " g: " + myColor1.g + " b:" + myColor1.b);
		float[] hsb = myColor1.hsb();
		CCLog.info("1. h: " + hsb[0] + " s: " + hsb[1] + " b:" + hsb[2]);

		CCColor myColor2 = new CCColor();
		myColor2.setHSB(hsb[0], hsb[1], hsb[2], myColor1.alpha());
		CCLog.info("2. r: " + myColor2.r + " g: " + myColor2.g + " b:" + myColor2.b);
	}
}
