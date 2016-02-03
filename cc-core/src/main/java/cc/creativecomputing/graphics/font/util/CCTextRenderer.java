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
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2f;

public class CCTextRenderer {
	
	public static class CCTextData{
		protected CCTextureData textureData;
		protected float width;
		protected float height;
		protected int size;
		protected FontMetrics fontMetrics;
		protected CCVector2f offset = new CCVector2f();
	}
	
	protected static final Color WHITE = Color.WHITE;
	protected static final Color TRANSPARENT = new Color(1F,1F,1F,0F);
	
	public CCTextData createTexture(final String theText, final Font theFont, final boolean theIsAntialias){
		
		CCTextData result = new CCTextData();
		result.size = theFont.getSize();
		
		final Object myAntialias = theIsAntialias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF;
			
		BufferedImage myTextImage = new BufferedImage(result.size, result.size, BufferedImage.TYPE_INT_ARGB);
		Graphics2D myGraphics = (Graphics2D) myTextImage.getGraphics();
		myGraphics.setFont(theFont);
		FontMetrics myFontMetrics = myGraphics.getFontMetrics();
		
		Rectangle2D myBounds = myFontMetrics.getStringBounds(theText, myGraphics);
		
		result.width = CCMath.max(1,(float)myBounds.getWidth());
		result.height = myFontMetrics.getAscent() + myFontMetrics.getDescent();
		result.fontMetrics = myFontMetrics;
		
		createTexture(result, theText, theFont, myAntialias);
		return result;
	}
	
	protected void createTexture(final CCTextData theTextData, final String theText, final Font theFont, final Object theIsAntialias){
		BufferedImage myTextImage = new BufferedImage(
			CCMath.ceil(theTextData.width), 
			CCMath.ceil(theTextData.height), 
			BufferedImage.TYPE_INT_ARGB
		);
		Graphics2D myGraphics = (Graphics2D) myTextImage.getGraphics();
		myGraphics.setFont(theFont);
		myGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, theIsAntialias);
		myGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		myGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		myGraphics.setBackground(TRANSPARENT);
		myGraphics.setColor(Color.WHITE);
		myGraphics.drawString(theText, 0, theTextData.fontMetrics.getAscent());
			
		theTextData.textureData = CCTextureIO.newTextureData(myTextImage);
		
		
	}
}
