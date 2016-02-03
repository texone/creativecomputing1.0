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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureException;
import cc.creativecomputing.graphics.texture.CCTextureIO.CCFileFormat;
import cc.creativecomputing.io.CCBufferUtil;
import cc.creativecomputing.io.CCIOUtil;



public class CCDDSFormat implements CCTextureFormat {
	
	@Override
	public CCTextureData createTextureData(
		final String theFile, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		return createTextureData(CCIOUtil.openStream(theFile), theInternalFormat, thePixelFormat, theFileSuffix);
	}
	
	@Override
	public CCTextureData createTextureData(
		final File theFile, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		if (
			CCFileFormat.DDS.fileExtension.equals(theFileSuffix) || 
			CCFileFormat.DDS.fileExtension.equals(CCIOUtil.fileExtension(theFile))
		) {
			CCDDSImage image = CCDDSImage.read(theFile);
			return createTextureData(image, theInternalFormat, thePixelFormat);
		}

		return null;
	}

	@Override
	public CCTextureData createTextureData(
		final InputStream theStream, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		if (
			CCFileFormat.DDS.fileExtension.equals(theFileSuffix) || 
			CCDDSImage.isDDSImage(theStream)
		) {
			
			CCDDSImage image;
			try {
				ByteBuffer buf = CCBufferUtil.readAll2Buffer(theStream);
				image = CCDDSImage.read(buf);
				theStream.close();
			} catch (IOException e) {
				throw new CCTextureException(e);
			}
			
			return createTextureData(image, theInternalFormat, thePixelFormat);
		}

		return null;
	}

	@Override
	public CCTextureData createTextureData(
		final URL theUrl, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		try {
			final InputStream theStream = new BufferedInputStream(theUrl.openStream());
			
			return createTextureData(theStream, theInternalFormat, thePixelFormat, theFileSuffix);
		} catch (IOException e) {
			throw new CCTextureException(e);
		}
	}

	private CCTextureData createTextureData(
		final CCDDSImage theImage, 
		CCPixelInternalFormat theInternalFormat, CCPixelFormat thePixelFormat
	) {
		CCDDSImage.ImageInfo info = theImage.getMipMap(0);
		
		if (thePixelFormat == null) {
			switch (theImage.getPixelFormat()) {
			case CCDDSImage.D3DFMT_R8G8B8:
				thePixelFormat = CCPixelFormat.RGB;
				theInternalFormat = CCPixelInternalFormat.RGB8;
				break;
			case CCDDSImage.D3DFMT_B8G8R8:
				thePixelFormat = CCPixelFormat.BGR;
				theInternalFormat = CCPixelInternalFormat.RGB8;
				break;
			default:
				thePixelFormat = CCPixelFormat.RGBA;
				theInternalFormat = CCPixelInternalFormat.RGBA8;
				break;
			}
		}

		if (info.isCompressed()) {
			switch (info.getCompressionFormat()) {
			case CCDDSImage.D3DFMT_DXT1:
				theInternalFormat = CCPixelInternalFormat.COMPRESSED_RGB_S3TC_DXT1_EXT;
				break;
			case CCDDSImage.D3DFMT_DXT3:
				theInternalFormat = CCPixelInternalFormat.COMPRESSED_RGBA_S3TC_DXT3_EXT;
				break;
			case CCDDSImage.D3DFMT_DXT5:
				theInternalFormat = CCPixelInternalFormat.COMPRESSED_RGBA_S3TC_DXT5_EXT;
				break;
			default:
				throw new CCTextureException("Unsupported DDS compression format \"" + CCDDSImage.getCompressionFormatName(info.getCompressionFormat()) + "\"");
			}
		}
		
		if (theInternalFormat == null) {
			switch (theImage.getPixelFormat()) {
			case CCDDSImage.D3DFMT_R8G8B8:
				thePixelFormat = CCPixelFormat.RGB;
				break;
			default:
				thePixelFormat = CCPixelFormat.RGBA;
				break;
			}
		}
		
		CCTextureData.Flusher flusher = new CCTextureData.Flusher() {
			public void flush() {
				theImage.close();
			}
		};
		
		CCTextureData data;
		if (theImage.getNumMipMaps() > 0) {
			Buffer[] mipmapData = new Buffer[theImage.getNumMipMaps()];
			for (int i = 0; i < theImage.getNumMipMaps(); i++) {
				mipmapData[i] = theImage.getMipMap(i).getData();
			}
			data = new CCTextureData(
				info.getWidth(), info.getHeight(), 0, 
				theInternalFormat, thePixelFormat, CCPixelType.UNSIGNED_BYTE, 
				info.isCompressed(), true, mipmapData, flusher
			);
		} else {
			data = new CCTextureData(
				info.getWidth(), info.getHeight(), 0, 
				theInternalFormat, thePixelFormat, CCPixelType.UNSIGNED_BYTE, 
				info.isCompressed(), true, info.getData(), flusher
			);
		}
		return data;
	}
	
	@Override
	public boolean write(File file, CCTextureData data){
		if (CCFileFormat.DDS.fileExtension.equals(CCIOUtil.fileExtension(file))) {
			// See whether the DDS writer can handle this TextureData
			CCPixelFormat pixelFormat = data.pixelFormat();
			CCPixelType pixelType = data.pixelType();
			if (pixelType != CCPixelType.BYTE && pixelType != CCPixelType.UNSIGNED_BYTE) {
				throw new CCTextureException("DDS writer only supports byte / unsigned byte textures");
			}

			int d3dFormat = 0;
			
			switch (pixelFormat) {
			case RGB:
				d3dFormat = CCDDSImage.D3DFMT_R8G8B8;
				break;
			case BGR:
				d3dFormat = CCDDSImage.D3DFMT_B8G8R8;
				break;
			case RGBA:
				d3dFormat = CCDDSImage.D3DFMT_A8R8G8B8;
				break;
			case COMPRESSED_RGB_S3TC_DXT1_EXT:
				d3dFormat = CCDDSImage.D3DFMT_DXT1;
				break;
			case COMPRESSED_RGBA_S3TC_DXT1_EXT:
				throw new CCTextureException("RGBA DXT1 not yet supported");
			case COMPRESSED_RGBA_S3TC_DXT3_EXT:
				d3dFormat = CCDDSImage.D3DFMT_DXT3;
				break;
			case COMPRESSED_RGBA_S3TC_DXT5_EXT:
				d3dFormat = CCDDSImage.D3DFMT_DXT5;
				break;
			default:
				throw new CCTextureException(
					"Unsupported pixel format 0x" + 
					Integer.toHexString(pixelFormat.glID) + 
					" by DDS writer"
				);
			}

			ByteBuffer[] mipmaps = null;
			if (data.mipmapData() != null) {
				mipmaps = new ByteBuffer[data.mipmapData().length];
				for (int i = 0; i < mipmaps.length; i++) {
					mipmaps[i] = (ByteBuffer) data.mipmapData()[i];
				}
			} else {
				mipmaps = new ByteBuffer[] { (ByteBuffer) data.buffer() };
			}

			CCDDSImage image = CCDDSImage.createFromData(d3dFormat, data.width(), data.height(), mipmaps);
			image.write(file);
			return true;
		}

		return false;
	}
}
