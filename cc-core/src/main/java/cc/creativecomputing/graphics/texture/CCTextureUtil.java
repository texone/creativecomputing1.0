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

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelGrabber;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import cc.creativecomputing.CCAppCapabilities;
import cc.creativecomputing.graphics.CCColor;
import cc.creativecomputing.util.logging.CCLog;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.util.awt.ImageUtil;

/**
 * @author christianriekoff
 *
 */
public class CCTextureUtil {
	
	/////////////////////////////////////////////////
	//
	// CONVERT JAVA BUFFERED IMAGES TO TEXTURE DATA
	//
	/////////////////////////////////////////////////
	
	private static final ColorModel rgbaColorModel = new ComponentColorModel(
		ColorSpace.getInstance(ColorSpace.CS_sRGB),
		new int[] { 8, 8, 8, 8 }, true, true, Transparency.TRANSLUCENT,
		DataBuffer.TYPE_BYTE
	);
		
	private static final ColorModel rgbColorModel = new ComponentColorModel(
		ColorSpace.getInstance(ColorSpace.CS_sRGB),
		new int[] { 8, 8, 8, 0 }, false, false, Transparency.OPAQUE,
		DataBuffer.TYPE_BYTE
	);
	
	/**
	 * Reads the data from the given image and returns it as buffer
	 * @param theImage the image with the data
	 * @return the data as buffer
	 */
	private static Buffer createNIOBufferFromImage(final BufferedImage theImage) {
		//
		// Note: Grabbing the DataBuffer will defeat Java2D's image
		// management mechanism (as of JDK 5/6, at least).  This shouldn't
		// be a problem for most JOGL apps, but those that try to upload
		// the image into an OpenGL texture and then use the same image in
		// Java2D rendering might find the 2D rendering is not as fast as
		// it could be.
		//
		
		DataBuffer data = theImage.getRaster().getDataBuffer();
		if (data instanceof DataBufferByte) {
			return Buffers.newDirectByteBuffer(((DataBufferByte) data).getData());
		} else if (data instanceof DataBufferDouble) {
			throw new CCTextureException("DataBufferDouble rasters not supported by OpenGL");
		} else if (data instanceof DataBufferFloat) {
			return Buffers.newDirectFloatBuffer(((DataBufferFloat) data).getData());
		} else if (data instanceof DataBufferInt) {
			return Buffers.newDirectIntBuffer(((DataBufferInt) data).getData());
		} else if (data instanceof DataBufferShort) {
			return Buffers.newDirectShortBuffer(((DataBufferShort) data).getData());
		} else if (data instanceof DataBufferUShort) {
			return Buffers.newDirectShortBuffer(((DataBufferUShort) data).getData());
		} else {
			throw new RuntimeException("Unexpected DataBuffer type?");
		}
	}
	
	/**
	 * The given Buffered image is not opengl conform and needs to be converted
	 * before the buffer can be generated
	 * @param theImage
	 * @return
	 */
	private static BufferedImage createFromCustom(final BufferedImage theImage) {
		int myWidth = theImage.getWidth();
		int myHeight = theImage.getHeight();

		// create a temporary image that is compatible with OpenGL
		boolean myHasAlpha = theImage.getColorModel().hasAlpha();
		ColorModel myColorModel = null;
		int myDataBufferType = theImage.getRaster().getDataBuffer().getDataType();
		
		// Don't use integer components for packed int images
		if (isPackedInt(theImage)) {
			myDataBufferType = DataBuffer.TYPE_BYTE;
		}
		
		if (myDataBufferType == DataBuffer.TYPE_BYTE) {
			myColorModel = myHasAlpha ? rgbaColorModel : rgbColorModel;
		} else {
			if (myHasAlpha) {
				myColorModel = new ComponentColorModel(
					ColorSpace.getInstance(ColorSpace.CS_sRGB), 
					null, true, true,
					Transparency.TRANSLUCENT, 
					myDataBufferType
				);
			} else {
				myColorModel = new ComponentColorModel(
					ColorSpace.getInstance(ColorSpace.CS_sRGB), 
					null, false, false,
					Transparency.OPAQUE, 
					myDataBufferType
				);
			}
		}

		boolean premult = myColorModel.isAlphaPremultiplied();
		WritableRaster raster = myColorModel.createCompatibleWritableRaster(myWidth, myHeight);
		BufferedImage texImage = new BufferedImage(myColorModel, raster, premult, null);

		// copy the source image into the temporary image
		Graphics2D g = texImage.createGraphics();
		g.setComposite(AlphaComposite.Src);
		g.drawImage(theImage, 0, 0, null);
		g.dispose();

		// Wrap the buffer from the temporary image
		return texImage;
	}
	
	public static BufferedImage convert(BufferedImage theInput, int theType) {
		BufferedImage myOutput = new BufferedImage(theInput.getWidth(),theInput.getHeight(), theType);
		myOutput.getGraphics().drawImage(theInput,0,0,null);
		return myOutput;
	}

	/**
	 * Checks if the given image is packed as integer, what means that
	 * it has to be read as unsigned byte.
	 * @param theImage the image to check
	 * @return true if the image data is packed as integer otherwise false
	 */
	private static boolean isPackedInt(final BufferedImage theImage) {
		final int myImageType = theImage.getType();
		return (
			myImageType == BufferedImage.TYPE_INT_RGB || 
			myImageType == BufferedImage.TYPE_INT_BGR || 
			myImageType == BufferedImage.TYPE_INT_ARGB || 
			myImageType == BufferedImage.TYPE_INT_ARGB_PRE
		);
	}
	
	/**
	 * Sets the pixel type and format of the given texture data based 
	 * ont the given buffered image.
	 * @param theImage the buffered image to get the settings
	 * @param theTextureData the texture data to set the settings
	 */
	private static void setupFromImageSettings(final BufferedImage theImage, final CCTextureData theTextureData) {
		boolean hasAlpha = theImage.getColorModel().hasAlpha();
		
		if(CCTextureIO.DEBUG)CCLog.info("CUSTOM");
		switch(theImage.getColorModel().getColorSpace().getType()) {
		case ColorSpace.TYPE_GRAY:
			if (theTextureData.pixelFormat() == null) {
				theTextureData.pixelFormat(hasAlpha ? CCPixelFormat.LUMINANCE_ALPHA : CCPixelFormat.LUMINANCE);
			}
			break;
		default:
			if (theTextureData.pixelFormat() == null) {
				theTextureData.pixelFormat(hasAlpha ? CCPixelFormat.RGBA : CCPixelFormat.RGB);
			}
			
			if(theTextureData.internalFormat() == null) {
				theTextureData.internalFormat(hasAlpha ? CCPixelInternalFormat.RGBA : CCPixelInternalFormat.RGB);
			}
			break;
		}
		
		// Allow previously-selected pixelType (if any) to override that
		// we can infer from the DataBuffer
		DataBuffer data = theImage.getRaster().getDataBuffer();
		if (data instanceof DataBufferByte || isPackedInt(theImage)) {
			// Don't use GL_UNSIGNED_INT for BufferedImage packed int images
			if (theTextureData.pixelType() == null)
				theTextureData.pixelType(CCPixelType.UNSIGNED_BYTE);
		} else if (data instanceof DataBufferDouble) {
			throw new RuntimeException("DataBufferDouble rasters not supported by OpenGL");
		} else if (data instanceof DataBufferFloat) {
			if (theTextureData.pixelType() == null)
				theTextureData.pixelType(CCPixelType.FLOAT);
		} else if (data instanceof DataBufferInt) {
			if (theTextureData.pixelType() == null)
				theTextureData.pixelType(CCPixelType.UNSIGNED_INT);
		} else if (data instanceof DataBufferShort) {
			if (theTextureData.pixelType() == null)
				theTextureData.pixelType(CCPixelType.SHORT);
		} else if (data instanceof DataBufferUShort) {
			if (theTextureData.pixelType() == null)
				theTextureData.pixelType(CCPixelType.UNSIGNED_SHORT);
		} else {
			throw new RuntimeException("Unexpected DataBuffer type?");
		}
	}

	/**
	 * Converts the given buffered image to a texture data object
	 * @param theImage buffered image to convert
	 * @return texture data object
	 */
	public static CCTextureData toTextureData(BufferedImage theImage, final CCTextureData theTextureData) {
		switch(theImage.getType()) {
		case BufferedImage.TYPE_4BYTE_ABGR:
			theImage = createFromCustom(theImage);
		}
		theTextureData.mustFlipVertically(true);

		theTextureData.width(theImage.getWidth());
		theTextureData.height(theImage.getHeight());

		int myScanlineStride;
		
		SampleModel mySampleModel = theImage.getRaster().getSampleModel();

		if (mySampleModel instanceof SinglePixelPackedSampleModel) {
			myScanlineStride = ((SinglePixelPackedSampleModel) mySampleModel).getScanlineStride();
		} else if (mySampleModel instanceof MultiPixelPackedSampleModel) {
			myScanlineStride = ((MultiPixelPackedSampleModel) mySampleModel).getScanlineStride();
		} else if (mySampleModel instanceof ComponentSampleModel) {
			myScanlineStride = ((ComponentSampleModel) mySampleModel).getScanlineStride();
		} else {
			// This will only happen for TYPE_CUSTOM anyway
			theTextureData.pixelStorageModes().rowLength(theTextureData.width());
			theTextureData.pixelStorageModes().alignment(1);
			setupFromImageSettings(theImage, theTextureData);
			theTextureData.buffer(createNIOBufferFromImage(theImage));
			return theTextureData;
		}
		

		
//		boolean myExpectingGL12 = false;
//		boolean myExpectingEXTABGR = false;
//		CCPixelType _myPixelType = null; // Determine from image
		switch (theImage.getType()) {
		case BufferedImage.TYPE_INT_RGB:
			if(CCTextureIO.DEBUG)CCLog.info("TYPE_INT_RGB");
			theTextureData.pixelFormat(CCPixelFormat.BGRA);
			theTextureData.pixelStorageModes().rowLength(theTextureData.width());
			theTextureData.pixelStorageModes().alignment(1);
//			myExpectingGL12 = true;
			setupFromImageSettings(theImage, theTextureData);
			break;
		case BufferedImage.TYPE_INT_ARGB_PRE:
			if(CCTextureIO.DEBUG)CCLog.info("TYPE_INT_ARGB_PRE");
			theTextureData.pixelFormat(CCPixelFormat.BGRA);
			theTextureData.pixelStorageModes().rowLength(theTextureData.width());
			theTextureData.pixelStorageModes().alignment(1);
//			myExpectingGL12 = true;
			setupFromImageSettings(theImage, theTextureData);
			break;
		case BufferedImage.TYPE_INT_BGR:
			if(CCTextureIO.DEBUG)CCLog.info("TYPE_INT_BGR");
			theTextureData.pixelFormat(CCPixelFormat.RGBA);
			theTextureData.pixelStorageModes().rowLength(theTextureData.width());
			theTextureData.pixelStorageModes().alignment(1);
//			myExpectingGL12 = true;
			setupFromImageSettings(theImage, theTextureData);
			break;
		case BufferedImage.TYPE_3BYTE_BGR:
			if(CCTextureIO.DEBUG)CCLog.info("TYPE_3BYTE_BGR");
			// we can pass the image data directly to OpenGL only if
			// we have an integral number of pixels in each scanline
			if ((myScanlineStride % 3) == 0) {
				theTextureData.pixelFormat(CCPixelFormat.BGR);
				theTextureData.pixelType(CCPixelType.UNSIGNED_BYTE);
				theTextureData.pixelStorageModes().rowLength(myScanlineStride / 3);
				theTextureData.pixelStorageModes().alignment(1);
			} else {
				theTextureData.pixelStorageModes().rowLength(theTextureData.width());
				theTextureData.pixelStorageModes().alignment(1);
				setupFromImageSettings(theImage, theTextureData);
			}
		
			break;
			
		case BufferedImage.TYPE_4BYTE_ABGR_PRE:
			if(CCTextureIO.DEBUG)CCLog.info("TYPE_4BYTE_ABGR_PRE");
			theTextureData.pixelStorageModes().rowLength(theTextureData.width());
			theTextureData.pixelStorageModes().alignment(1);
			setupFromImageSettings(theImage, theTextureData);
			break;
		case BufferedImage.TYPE_USHORT_565_RGB:
			if(CCTextureIO.DEBUG)CCLog.info("TYPE_USHORT_565_RGB");
			theTextureData.pixelFormat(CCPixelFormat.RGB);
			theTextureData.pixelStorageModes().rowLength(theTextureData.width());
			theTextureData.pixelStorageModes().alignment(1);
//			myExpectingGL12 = true;
			setupFromImageSettings(theImage, theTextureData);
			break;
		case BufferedImage.TYPE_USHORT_555_RGB:
			if(CCTextureIO.DEBUG)CCLog.info("TYPE_USHORT_555_RGB");
			theTextureData.pixelFormat(CCPixelFormat.BGRA);
			theTextureData.pixelStorageModes().rowLength(theTextureData.width());
			theTextureData.pixelStorageModes().alignment(1);
//			myExpectingGL12 = true;
			setupFromImageSettings(theImage, theTextureData);
			break;
		case BufferedImage.TYPE_BYTE_GRAY:
			if(CCTextureIO.DEBUG)CCLog.info("TYPE_BYTE_GRAY");
			theTextureData.pixelFormat(CCPixelFormat.LUMINANCE);
			theTextureData.pixelType(CCPixelType.UNSIGNED_BYTE);
			theTextureData.pixelStorageModes().rowLength(myScanlineStride);
			theTextureData.pixelStorageModes().alignment(1);
			break;
		case BufferedImage.TYPE_BYTE_INDEXED:
			if(CCTextureIO.DEBUG)CCLog.info("TYPE_BYTE_INDEXED");
			theTextureData.pixelFormat(CCPixelFormat.RGB);
			theTextureData.internalFormat(CCPixelInternalFormat.RGB8);
			
			theTextureData.pixelType(CCPixelType.UNSIGNED_BYTE);
			theTextureData.pixelStorageModes().rowLength(myScanlineStride);
			theTextureData.pixelStorageModes().alignment(1);
			break;
		case BufferedImage.TYPE_USHORT_GRAY:
			if(CCTextureIO.DEBUG)CCLog.info("TYPE_USHORT_GRAY");
			theTextureData.pixelFormat(CCPixelFormat.LUMINANCE);
			theTextureData.pixelType(CCPixelType.UNSIGNED_SHORT);
			theTextureData.pixelStorageModes().rowLength(myScanlineStride);
			theTextureData.pixelStorageModes().alignment(2);
			break;
			// Note: TYPE_INT_ARGB and TYPE_4BYTE_ABGR images go down the
			// custom code path to satisfy the invariant that images with an
			// alpha channel always go down with premultiplied alpha.
		case BufferedImage.TYPE_4BYTE_ABGR:
			if(CCTextureIO.DEBUG)CCLog.info("4BYTE_ABGR");
			theTextureData.pixelFormat(CCPixelFormat.ABGR);
			theTextureData.internalFormat(CCPixelInternalFormat.ABGR);
			theTextureData.pixelType(CCPixelType.UNSIGNED_BYTE);
			theTextureData.pixelStorageModes().rowLength(myScanlineStride /4);
			theTextureData.pixelStorageModes().alignment(4);
            break;
		case BufferedImage.TYPE_CUSTOM:
		case BufferedImage.TYPE_INT_ARGB:
		case BufferedImage.TYPE_BYTE_BINARY:
		default:
			if(CCTextureIO.DEBUG)CCLog.info("DEFAULT:" + theImage.getType());
			ColorModel cm = theImage.getColorModel();
			if (cm.equals(rgbColorModel)) {
				theTextureData.pixelFormat(CCPixelFormat.RGB);
				theTextureData.pixelType(CCPixelType.UNSIGNED_BYTE);
				theTextureData.pixelStorageModes().rowLength(myScanlineStride / 3);
				theTextureData.pixelStorageModes().alignment(1);
			} else if (cm.equals(rgbaColorModel)) {
				theTextureData.pixelFormat(CCPixelFormat.RGBA);
				theTextureData.pixelType(CCPixelType.UNSIGNED_BYTE);
				theTextureData.pixelStorageModes().rowLength(myScanlineStride / 4);
				theTextureData.pixelStorageModes().alignment(4);
			} else {
				theTextureData.pixelStorageModes().rowLength(theTextureData.width());
				theTextureData.pixelStorageModes().alignment(1);
				setupFromImageSettings(theImage, theTextureData);
			}
			break;
		}

		if(CCTextureIO.DEBUG) {
			CCLog.info("CCAppCapabilities.GL_EXT_abgr:" + CCAppCapabilities.GL_EXT_abgr);
			CCLog.info("CCAppCapabilities.GL_VERSION_1_2:"+CCAppCapabilities.GL_VERSION_1_2);
		}
//		if (myExpectingEXTABGR && !CCAppCapabilities.GL_EXT_abgr || myExpectingGL12 && !CCAppCapabilities.GL_VERSION_1_2) {
//			// Must present the illusion to the end user that we are simply
//			// wrapping the input BufferedImage
//			theTextureData.buffer(createFromCustom(theImage));
//		} else {
			theTextureData.buffer(createNIOBufferFromImage(theImage));
//		}
		return theTextureData;
	}
	
	/**
	 * Converts the given {@linkplain CCTextureData} object to a java BufferedImage. Be aware that only 
	 * {@linkplain CCTextureData} objects with {@linkplain CCTextureData#pixelFormat()} {@linkplain CCPixelFormat#RGB}
	 * and {@linkplain CCPixelFormat#RGB} and {@linkplain CCTextureData#pixelType()} {@linkplain CCPixelType#BYTE} and
	 * {@linkplain CCPixelType#UNSIGNED_BYTE} are supported.
	 * @param theData object to convert
	 * @return converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(CCTextureData theData){
		CCPixelFormat pixelFormat = theData.pixelFormat();
		CCPixelType pixelType = theData.pixelType();
		
		if(!(pixelFormat == CCPixelFormat.RGB || pixelFormat == CCPixelFormat.RGBA || pixelFormat == CCPixelFormat.BGRA)){
			throw new CCTextureException("conversion of texture data doesn't support pixel format " + pixelFormat + " (only RGB/A)");
		}
		if(!(pixelType == CCPixelType.BYTE || pixelType == CCPixelType.UNSIGNED_BYTE)){
			throw new CCTextureException("conversion of texture data doesn't support pixel type " + pixelType + " (only BYTE and UNSIGNED_BYTE)");
		}
			
		// Convert TextureData to appropriate BufferedImage
		BufferedImage myImage = new BufferedImage(
			theData.width(), 
			theData.height(), 
			(pixelFormat == CCPixelFormat.RGB) ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_4BYTE_ABGR
		);
			
		byte[] myImageData = ((DataBufferByte) myImage.getRaster().getDataBuffer()).getData();
		if(theData.buffer() instanceof ByteBuffer){
			ByteBuffer buf = (ByteBuffer) theData.buffer();
			if (buf == null) {
				buf = (ByteBuffer) theData.mipmapData()[0];
			}
			buf.rewind();
			buf.get(myImageData);
			buf.rewind();
	
			// Swizzle image components to be correct
			if (pixelFormat == CCPixelFormat.RGB) {
				for (int i = 0; i < myImageData.length; i += 3) {
					byte r = myImageData[i + 0];
					byte b = myImageData[i + 2];
					myImageData[i + 0] = b;
					myImageData[i + 2] = r;
				}
			} else {
				for (int i = 0; i < myImageData.length; i += 4) {
					byte r = myImageData[i + 0];
					byte g = myImageData[i + 1];
					byte b = myImageData[i + 2];
					byte a = myImageData[i + 3];
					myImageData[i + 0] = a;
					myImageData[i + 1] = b;
					myImageData[i + 2] = g;
					myImageData[i + 3] = r;
				}
			}
		}
		if(theData.buffer() instanceof IntBuffer){
			IntBuffer buf = ((IntBuffer) theData.buffer());
			CCLog.info(theData.width() + ":" + theData.height() + ":" + theData.width() * theData.height() + ":" + buf.capacity());
			if (buf == null) {
				buf = (IntBuffer) theData.mipmapData()[0];
			}
			buf.rewind();
//			buf.get(myImageData);
			buf.rewind();
	CCLog.info(pixelFormat);
			// Swizzle image components to be correct
			if (pixelFormat == CCPixelFormat.RGB) {
				for (int i = 0; i < myImageData.length; i += 3) {
					byte r = (byte)buf.get();
					byte g = (byte)buf.get();
					byte b = (byte)buf.get();
					myImageData[i + 0] = b;
					myImageData[i + 1] = g;
					myImageData[i + 2] = r;
				}
			} else {
				for (int i = 0; i < myImageData.length; i += 4) {
					int rgba = buf.get();
					byte b0 = (byte)((rgba >> 24) );
					byte b1 = (byte)((rgba >> 16));
					byte b2 = (byte)((rgba >>  8)); 
					byte b3 = (byte)(rgba );
					switch(pixelFormat){
					case RGBA:
						myImageData[i + 0] = b3;
						myImageData[i + 1] = b2;
						myImageData[i + 2] = b1;
						myImageData[i + 3] = b0;
						break;
					case BGRA:
						myImageData[i + 0] = b3;
						myImageData[i + 1] = b0;
						myImageData[i + 2] = b1;
						myImageData[i + 3] = b2;
						break;
					}
				}
			}
		}

		// Flip image vertically for the user's convenience
		ImageUtil.flipImageVertically(myImage);
			
		return myImage;
	}
	
	/**
	 * This method returns true if the specified image has transparent pixels
	 * @param theImage
	 * @return
	 */
	public static boolean hasAlpha(Image theImage) {
		// If buffered image, the color model is readily available
		if (theImage instanceof BufferedImage) {
			BufferedImage myBufferedImage = (BufferedImage) theImage;
			return myBufferedImage.getColorModel().hasAlpha();
		}

		// Use a pixel grabber to retrieve the image's color model;
		// grabbing a single pixel is usually sufficient
		PixelGrabber myPixelGrabber = new PixelGrabber(theImage, 0, 0, 1, 1, false);
		try {
			myPixelGrabber.grabPixels();
		} catch (InterruptedException e) {
		}

		// Get the image's color model
		ColorModel myColorModel = myPixelGrabber.getColorModel();
		return myColorModel.hasAlpha();
	}
	
	public static CCTextureData toTextureData(Image theImage, CCTextureData theData) {
		if (theImage instanceof BufferedImage) {
			return toTextureData((BufferedImage) theImage, theData);
		}

		// This code ensures that all the pixels in the image are loaded
		Image myImage = new ImageIcon(theImage).getImage();

		// Determine if the image has transparent pixels; for this method's
		// implementation, see Determining If an Image Has Transparent Pixels
		boolean hasAlpha = hasAlpha(myImage);

		// Create a buffered image with a format that's compatible with the screen
		BufferedImage myBufferedImage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;
			if (hasAlpha) {
				transparency = Transparency.BITMASK;
			}

			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			myBufferedImage = gc.createCompatibleImage(myImage.getWidth(null), myImage.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		}

		if (myBufferedImage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			if (hasAlpha) {
				type = BufferedImage.TYPE_INT_ARGB;
			}
			myBufferedImage = new BufferedImage(myImage.getWidth(null), myImage.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics g = myBufferedImage.getGraphics();

		// Paint the image onto the buffered image
		g.drawImage(myImage, 0, 0, null);
		g.dispose();

		return toTextureData(myBufferedImage, theData);
	}
	
	private static Map<String, CCTexture2D> textureCache = new HashMap<String, CCTexture2D>();
	
	public static CCTexture2D cacheTexture(String theTexture) {
		if(!textureCache.containsKey(theTexture)) {
			textureCache.put(theTexture,new CCTexture2D(CCTextureIO.newTextureData(theTexture)));
		}
		return textureCache.get(theTexture);
	}
}
