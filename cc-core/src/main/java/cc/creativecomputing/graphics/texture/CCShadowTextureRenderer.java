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
package cc.creativecomputing.graphics.texture;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import cc.creativecomputing.graphics.texture.filter.CCGaussianBlur;

public class CCShadowTextureRenderer extends CCTextureRenderer {

	private int _myShadowSize = 0;

	public CCShadowTextureRenderer(final int theShadowSize) {
		super(theShadowSize * 2, theShadowSize * 2, -theShadowSize, theShadowSize);
		_myShadowSize = theShadowSize;
	}

	@Override
	protected BufferedImage createTexture(BufferedImage theImage, BufferedImage theTargetImage) {
		/* Create a rescale filter op that makes the image 50% opaque */
		float[] scales = { 0f, 0f, 0f, 1f };
		float[] offsets = new float[4];
		RescaleOp rop = new RescaleOp(scales, offsets, null);

		Graphics2D myGraphics = (Graphics2D) theTargetImage.getGraphics();
		myGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, _myIsAntialiased);
		myGraphics.setBackground(new Color(1f, 1f, 1f, 0f));

		/* Draw the image, applying the filter */
		myGraphics.drawImage(theImage, rop, -(int) _myOffset.x, (int) _myOffset.y);

		CCGaussianBlur myBlur = new CCGaussianBlur(_myShadowSize);
		theTargetImage = myBlur.filter(theTargetImage);

		myGraphics = (Graphics2D) theTargetImage.getGraphics();
		myGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, _myIsAntialiased);
		myGraphics.drawImage(theImage, -(int) _myOffset.x, (int) _myOffset.y, null);

		return theTargetImage;

	}

}
