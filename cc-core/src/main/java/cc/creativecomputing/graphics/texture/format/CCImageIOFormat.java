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
package cc.creativecomputing.graphics.texture.format;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureException;
import cc.creativecomputing.graphics.texture.CCTextureIO;
import cc.creativecomputing.graphics.texture.CCTextureIO.CCFileFormat;
import cc.creativecomputing.graphics.texture.CCTextureUtil;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.util.logging.CCLog;

public class CCImageIOFormat implements CCTextureFormat {
	
	private CCTextureData createTextureData(
		BufferedImage theImage, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat,
		final String theFileSuffix	
	) {
		if (theImage == null) {
			return null;
		}
		
		if(theImage.getType() == BufferedImage.TYPE_BYTE_INDEXED) {
			theImage = CCTextureUtil.convert(theImage, BufferedImage.TYPE_4BYTE_ABGR);
		}
		
		if (CCTextureIO.DEBUG) {
			CCLog.info("TextureIO.newTextureData(): BufferedImage type for stream = " + theImage.getType());
		}
		
		CCTextureData myTextureData = new CCTextureData();
		if (theInternalFormat == null) {
			myTextureData.internalFormat(theImage.getColorModel().hasAlpha() ? CCPixelInternalFormat.RGBA : CCPixelInternalFormat.RGB);
		} else {
			myTextureData.internalFormat(theInternalFormat);
		}
		myTextureData.pixelFormat(thePixelFormat);
		
		return CCTextureUtil.toTextureData(theImage, myTextureData);
	}
	
	@Override
	public CCTextureData createTextureData(
		final String theFile, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		try {
			return createTextureData(ImageIO.read(CCIOUtil.openStream(theFile)), theInternalFormat, thePixelFormat, theFileSuffix);
		} catch (IOException e) {
			throw new CCTextureException(e);
		}
	}
	
	@Override
	public CCTextureData createTextureData(
		final File theFile, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		try {
			return createTextureData(ImageIO.read(theFile), theInternalFormat, thePixelFormat, theFileSuffix);
		} catch (IOException e) {
			throw new CCTextureException(e);
		}
	}

	@Override
	public CCTextureData createTextureData(
		final InputStream theStream, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){

		try {
			return createTextureData(ImageIO.read(theStream), theInternalFormat, thePixelFormat, theFileSuffix);
		} catch (IOException e) {
			throw new CCTextureException(e);
		}
	}

	@Override
	public CCTextureData createTextureData(
		final URL theUrl, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		
		try {
			final InputStream myStream = theUrl.openStream();
			try {
				return createTextureData(myStream, theInternalFormat, thePixelFormat, theFileSuffix);
			} finally {
				myStream.close();
			}
		} catch (IOException e) {
			throw new CCTextureException(e);
		}
	}
	
	@Override
	public boolean write(final File theFile, final CCTextureData theData) throws CCTextureException {
			
		// Convert TextureData to appropriate BufferedImage
		BufferedImage myImage = CCTextureUtil.toBufferedImage(theData);
			
		// Happened to notice that writing RGBA images to JPEGS is broken
		if (CCFileFormat.JPG.fileExtension.equals(CCIOUtil.fileExtension(theFile)) && myImage.getType() == BufferedImage.TYPE_4BYTE_ABGR) {
			BufferedImage tmpImage = new BufferedImage(myImage.getWidth(), myImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			Graphics g = tmpImage.getGraphics();
			g.drawImage(myImage, 0, 0, null);
			g.dispose();
			myImage = tmpImage;
		}
		try {
			return ImageIO.write(myImage, CCIOUtil.fileExtension(theFile), theFile);
		} catch (IOException e) {
			throw new CCTextureException(e);
		}
	}
}
