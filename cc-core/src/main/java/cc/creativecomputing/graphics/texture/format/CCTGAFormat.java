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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureException;
import cc.creativecomputing.graphics.texture.CCTextureIO.CCFileFormat;
import cc.creativecomputing.io.CCIOUtil;

public class CCTGAFormat extends CCStreamBasedTextureFormat {

	@Override
	public CCTextureData createTextureData(
		final InputStream theStream, 
		CCPixelInternalFormat theInternalFormat, 
		CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	) throws CCTextureException {
		if (CCFileFormat.TGA.fileExtension.equals(theFileSuffix)) {
			CCTGAImage image = CCTGAImage.read(theStream);
			if (thePixelFormat == null) {
				thePixelFormat = image.pixelFormat();
			}
			if (theInternalFormat == null) {
				theInternalFormat = CCPixelInternalFormat.RGBA8;
			}
			return new CCTextureData(
				image.getWidth(), image.getHeight(), 0, 
				theInternalFormat, thePixelFormat, CCPixelType.UNSIGNED_BYTE, 
				false, false, 
				image.getData(), null
			);
		}

		return null;
	}

	@Override
	public boolean write(final File theFile, final CCTextureData theData) throws CCTextureException {
		if (CCFileFormat.TGA.fileExtension.equals(CCIOUtil.fileExtension(theFile))) {
			// See whether the TGA writer can handle this TextureData
			CCPixelFormat pixelFormat = theData.pixelFormat();
			CCPixelType pixelType = theData.pixelType();
			if ((pixelFormat == CCPixelFormat.RGB || pixelFormat == CCPixelFormat.RGBA) && (pixelType == CCPixelType.BYTE || pixelType == CCPixelType.UNSIGNED_BYTE)) {
				ByteBuffer buf = ((theData.buffer() != null) ? (ByteBuffer) theData.buffer() : (ByteBuffer) theData.mipmapData()[0]);
				// Must reverse order of red and blue channels to get correct results
				int skip = ((pixelFormat == CCPixelFormat.RGB) ? 3 : 4);
				for (int i = 0; i < buf.remaining(); i += skip) {
					byte red = buf.get(i + 0);
					byte blue = buf.get(i + 2);
					buf.put(i + 0, blue);
					buf.put(i + 2, red);
				}

				CCTGAImage image = CCTGAImage.createFromData(
					theData.width(), theData.height(), (pixelFormat == CCPixelFormat.RGBA), 
					false,
					(ByteBuffer) theData.buffer()
				);
				try {
					image.write(theFile);
				} catch (IOException e) {
					throw new CCTextureException(e);
				}
				return true;
			}

			throw new CCTextureException("TGA writer doesn't support this pixel format / type (only RGB/A + bytes)");
		}

		return false;
	}
}
