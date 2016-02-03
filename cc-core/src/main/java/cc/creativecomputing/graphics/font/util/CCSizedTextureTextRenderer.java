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
package cc.creativecomputing.graphics.font.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCMath;

public class CCSizedTextureTextRenderer extends CCTextRenderer{
	
	private int _myWidth;
	private int _myHeight;
	
	public CCSizedTextureTextRenderer(final int theWidth, final int theHeight){
		_myWidth = theWidth;
		_myHeight = theHeight;
	}
	
	@Override
	protected void createTexture(final CCTextData theTextData, final String theText, final Font theFont, final Object theIsAntialias){
		float myTextWidth = theTextData.width;
		float myTextHeight = theTextData.height;
		
		theTextData.width = _myWidth;
		theTextData.height = _myHeight;
		
		BufferedImage myTextImage = new BufferedImage(
			CCMath.ceil(theTextData.width), 
			CCMath.ceil(theTextData.height), 
			BufferedImage.TYPE_INT_ARGB
		);
		Graphics2D myGraphics = (Graphics2D) myTextImage.getGraphics();
		myGraphics.setFont(theFont);
		myGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, theIsAntialias);
		myGraphics.setBackground(TRANSPARENT);
		myGraphics.setColor(Color.WHITE);
		myGraphics.drawString(theText, (_myWidth - myTextWidth)/2, (_myHeight - myTextHeight)/2);
			
		theTextData.textureData = CCTextureIO.newTextureData(myTextImage);
	}
}
