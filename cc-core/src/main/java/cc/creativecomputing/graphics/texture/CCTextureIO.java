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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import cc.creativecomputing.graphics.texture.format.CCDDSFormat;
import cc.creativecomputing.graphics.texture.format.CCImageIOFormat;
import cc.creativecomputing.graphics.texture.format.CCSGIFormat;
import cc.creativecomputing.graphics.texture.format.CCTGAFormat;
import cc.creativecomputing.graphics.texture.format.CCTextureFormat;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.io.CCWriteMode;

/**
 * <p>
 * Provides input and output facilities for both loading OpenGL textures from disk and streams as well as writing
 * textures already in memory back to disk.
 * </p>
 * 
 * <p>
 * The CCTextureIO class supports an arbitrary number of plug-in formats via CCTextureFormats.
 * CCTextureFormats know how to produce TextureData objects from files, InputStreams and URLs.
 * and to write TextureData objects to disk in various file formats. The CCTextureData class
 * represents the raw data of the texture before it has been converted to an CCTexture object. 
 * The CCTexture class represents the OpenGL texture object and provides easy facilities for 
 * using the texture.
 * </p>
 * <p>
 * There are several built-in CCTextureFormats supplied with the CCTextureIO implementation. 
 * The most basic format uses the platform's Image I/O facilities to read in a BufferedImage 
 * and convert it to a texture. This is the baseline format and is registered so that it is the 
 * last one consulted. All others are asked first to open a given file.
 * </p>
 * <p>
 * There are three other formats registered by default. One handles SGI RGB (".sgi",".rgb") images 
 * from both files and streams. One handles DirectDraw Surface (".dds") images read from files, 
 * though can not read these images from streams. One handles Targa (".tga") images read from 
 * both files and streams. These formats are executed in an arbitrary order. Some of these 
 * formats require the file's suffix to either be specified via the newTextureData methods or 
 * for the file to be named with the appropriate suffix. In general a file suffix should be 
 * provided to the newTexture and newTextureData methods if at all possible.
 * </p>
 * <p>
 * Note that additional CCTextureFormats, if reading images from InputStreams, must use the 
 * mark()/reset() methods on InputStream when probing for e.g. magic numbers at the head of the 
 * file to make sure not to disturb the state of the InputStream for downstream CCTextureFormats.
 * </p>
 * <p>
 * The CCTextureFormats can also be used for writing textures back to disk if desired. 
 * Some of formats have certain limitations such as only being able to write out textures 
 * stored in RGB or RGBA format. The DDS writer supports fetching and writing to disk of 
 * texture data in DXTn compressed format. Whether this will occur is dependent on
 * whether the texture's internal format is one of the DXTn compressed formats and whether 
 * the target file is .dds format.
 * </p>
 * 
 * @author Christian Riekoff
 */
public class CCTextureIO {
	
	static public enum CCFileFormat{
		DDS("dds"),
		SGI("sgi"),
		SGI_RGB("rgb"),
		GIF("gif"),
		JPG("jpg"),
		JPEG("jpeg"),
		PNG("png"),
		TGA("tga"),
		TIFF("tiff");
		
		public final String fileExtension;
		
		private CCFileFormat(final String theFileExtension){
			fileExtension = theFileExtension;
		}
	}
	
	public static boolean DEBUG = false;
	
	// HELPERS
	
	private static String toLowerCase(final String theString) {
		if (theString == null) {
			return null;
		}

		return theString.toLowerCase();
	}
	
	/**
	 * Use this function to get a list of textures inside a folder, that can be
	 * loaded be creative computing. Supported texture formats are dds, gif,
	 * jpg, png, sgi, sgi_rgb, tga and tiff. You can also pass the file type of
	 * the textures you want to be listed.
	 * 
	 * @param theFolder
	 * @return
	 */
	public static String[] listTextures(final String theFolder){
		return CCIOUtil.list(
			theFolder, 
			CCFileFormat.DDS.fileExtension, 
			CCFileFormat.GIF.fileExtension, 
			CCFileFormat.JPG.fileExtension, 
			CCFileFormat.JPEG.fileExtension, 
			CCFileFormat.PNG.fileExtension, 
			CCFileFormat.SGI.fileExtension, 
			CCFileFormat.SGI_RGB.fileExtension,
			CCFileFormat.TGA.fileExtension,
			CCFileFormat.TIFF.fileExtension
		);
	}
	
	public static String[] listTextures(final String theFolder, final CCFileFormat theFormat){
		return CCIOUtil.list(theFolder, theFormat.fileExtension);
	}
	
	/**
	 * Use this function to load more than one texture at once, this makes sense
	 * if you want to load all textures of a folder for example. Be aware that
	 * the paths are ordered alphabetically. Files that can not be read, will be
	 * ignored and do not throw an exception.
	 * @shortdesc Loads multiple textures and returns them as list.
	 * @param theTexturePaths paths of the textures you want to load
	 * @return a list with all the loaded textures
	 */
	public static List<CCTextureData> newTextureDatas(final String[] theTexturePaths){
		List<CCTextureData> myResult = new ArrayList<CCTextureData>();
		Arrays.sort(theTexturePaths);
		
		for(String myTexture:theTexturePaths){
			try {
				myResult.add(newTextureData(myTexture));
			} catch (CCTextureException e) {
				// just catch if single files can not be loaded
			}
		}
		
		return myResult;
	}
	
	/**
	 * @param theTexturePaths theTexturePaths paths of the textures you want to load
	 */
	public static List<CCTextureData> newTextureDatas(final List<String> theTexturePaths){
		List<CCTextureData> myResult = new ArrayList<CCTextureData>();
		Collections.sort(theTexturePaths);
		
		for(String myTexture:theTexturePaths){
			try {
				myResult.add(newTextureData(myTexture));
			} catch (CCTextureException e) {
				// just catch if single files can not be loaded
			}
		}
		return myResult;
	}

	/**
	 * @param theFolder folder containing all textures you want to load
	 */
	public static List<CCTextureData> newTextureDatas(final String theFolder){
		String[] myFiles = listTextures(theFolder);
		for(int i = 0; i < myFiles.length;i++) {
			myFiles[i] = theFolder + File.separator + myFiles[i];
		}
		return newTextureDatas(myFiles);
	}

	/**
	 * @param theFolder folder containing all textures you want to load
	 * @param theFormat format of the textures you want to load
	 */
	public static List<CCTextureData> newTextureDatas(final String theFolder, final CCFileFormat theFormat){
		String[] myFiles = listTextures(theFolder,theFormat);
		for(int i = 0; i < myFiles.length;i++) {
			myFiles[i] = theFolder + File.separator + myFiles[i];
		}
		return newTextureDatas(myFiles);
	}
	
	////////////////////////////////////////////////////
	//
	// LOAD TEXTURE DATA
	//
	////////////////////////////////////////////////////
	
	/**
	 * Creates a CCTextureData from the given resource.
	 * @param theTexturePath
	 * 			the resource from which to read the texture data
	 * @throws CCTextureException
	 * @return the texture data from the resource, or null if none of the registered texture providers could read the file
	 */
	public static CCTextureData newTextureData(final String theTexturePath) {
		String myLowerFileTyp = theTexturePath.toLowerCase();
		final int myIndex = theTexturePath.lastIndexOf('.') + 1;
		myLowerFileTyp = myLowerFileTyp.substring(myIndex, myLowerFileTyp.length());

		final InputStream myInputStream = CCIOUtil.openStream(theTexturePath);
		if (myInputStream == null) {
			throw new CCTextureException("The Texture you tried to load is not availabe!:" + CCIOUtil.dataPath(theTexturePath));
		}
		
		return newTextureData(CCIOUtil.openStream(theTexturePath), myLowerFileTyp);
	}
	
	/**
	 * Creates a CCTextureData from the given resource.
	 * @param theFile
	 * 			the file from which to read the texture data
	 * @param theFileSuffix 
	 * 		 	the suffix of the file name to be used as a hint of the 
	 * 			file format to the underlying texture provider, or null if none and 
	 * 			should be auto-detected (some texture providers do not support this)
	 * @return the texture data from the resource, or null if none of the registered texture providers could read the file
	 */
	public static CCTextureData newTextureData(final File theFile, final String theFileSuffix){
		return newTextureDataImpl(theFile, null, null, theFileSuffix);
	}
	
	public static CCTextureData newTextureData(final File theFile){
		return newTextureDataImpl(theFile, null, null, null);
	}
	
	/**
	 * Creates a CCTextureData from the given resource.
	 * @param theStream
	 * 			the stream from which to read the texture data
	 * @param theFileSuffix
	 * 		 	the suffix of the file name to be used as a hint of the 
	 * 			file format to the underlying texture provider, or null if none and 
	 * 			should be auto-detected (some texture providers do not support this)
	 * @return the texture data from the stream, or null if none of the registered texture providers could read the file
	 */
	public static CCTextureData newTextureData(final InputStream theStream, final String theFileSuffix){
		return newTextureDataImpl(theStream, null, null, theFileSuffix);
	}
	
	/**
	 * Creates a CCTextureData from the given resource.
	 * @param theUrl
	 * 			the url from which to read the texture data
	 * @param theFileSuffix
	 * 		 	the suffix of the file name to be used as a hint of the 
	 * 			file format to the underlying texture provider, or null if none and 
	 * 			should be auto-detected (some texture providers do not support this)
	 * @return the texture data from the url, or null if none of the registered texture providers could read the file
	 */
	public static CCTextureData newTextureData(final URL theUrl, String theFileSuffix){
		if (theFileSuffix == null) {
			theFileSuffix = CCIOUtil.fileExtension(theUrl.getPath());
		}
		return newTextureDataImpl(theUrl, null, null, theFileSuffix);
	}
	
	/**
	 * Creates a CCTextureData from the given buffered image.
	 * @param theImage
	 * 			the image from which to read the texture data
	 * @return the texture data from the buffered image, or null if none of the registered texture providers could read the file
	 */
	public static CCTextureData newTextureData(final Image theImage) {
		return newTextureDataImpl(theImage, null, null);
	}

	//----------------------------------------------------------------------
	// These methods make no assumption about the OpenGL internal format
	// or pixel format of the texture; they must be specified by the
	// user. It is not allowed to supply 0 (indicating no preference)
	// for either the internalFormat or the pixelFormat;
	// IllegalArgumentException will be thrown in this case.

	/**
	 * Creates a TextureData from the given file, using the specified OpenGL internal format and pixel format for the
	 * texture which will eventually result. The internalFormat and pixelFormat must be specified and may not be zero;
	 * to use default values, use the variant of this method which does not take these arguments. Does no OpenGL work.
	 * 
	 * @param theFile the file from which to read the texture data
	 * @param theInternalFormat the OpenGL internal format of the texture which will eventually result from the TextureData
	 * @param thePixelFormat the OpenGL pixel format of the texture which will eventually result from the TextureData
	 * @param theFileSuffix the suffix of the file name to be used as a hint of the file format to the underlying texture
	 *        provider, or null if none and should be auto-detected (some texture providers do not support this)
	 * @return the texture data from the file, or null if none of the registered texture providers could read the file
	 * @throws IllegalArgumentException if either internalFormat or pixelFormat was 0
	 * @throws IOException if an error occurred while reading the file
	 * @invisible
	 */
	public static CCTextureData newTextureData(
		final File theFile, 
		final CCPixelInternalFormat theInternalFormat,
		final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	){
		if ((theInternalFormat == null) || (thePixelFormat == null)) {
			throw new CCTextureException("internalFormat and pixelFormat must be non-zero");
		}

		return newTextureDataImpl(theFile, theInternalFormat, thePixelFormat,theFileSuffix);
	}

	/**
	 * Creates a TextureData from the given stream, using the specified OpenGL internal format and pixel format for the
	 * texture which will eventually result. The internalFormat and pixelFormat must be specified and may not be zero;
	 * to use default values, use the variant of this method which does not take these arguments. Does no OpenGL work.
	 * 
	 * @param theStream the stream from which to read the texture data
	 * @param theInternalFormat the OpenGL internal format of the texture which will eventually result from the TextureData
	 * @param thePixelFormat the OpenGL pixel format of the texture which will eventually result from the TextureData
	 * @param theFileSuffix the suffix of the file name to be used as a hint of the file format to the underlying texture
	 *        provider, or null if none and should be auto-detected (some texture providers do not support this)
	 * @return the texture data from the stream, or null if none of the registered texture providers could read the
	 *         stream
	 * @throws IllegalArgumentException if either internalFormat or pixelFormat was 0
	 * @throws IOException if an error occurred while reading the stream
	 */
	public static CCTextureData newTextureData(
		final InputStream theStream,
		final CCPixelInternalFormat theInternalFormat, 
		final CCPixelFormat thePixelFormat,
		final String theFileSuffix
	){
		if ((theInternalFormat == null) || (thePixelFormat == null)) {
			throw new CCTextureException("internalFormat and pixelFormat must be non-zero");
		}

		return newTextureDataImpl(theStream, theInternalFormat, thePixelFormat,theFileSuffix);
	}

	/**
	 * Creates a TextureData from the given URL, using the specified OpenGL internal format and pixel format for the
	 * texture which will eventually result. The internalFormat and pixelFormat must be specified and may not be zero;
	 * to use default values, use the variant of this method which does not take these arguments. Does no OpenGL work.
	 * 
	 * @param theUrl the URL from which to read the texture data
	 * @param theInternalFormat the OpenGL internal format of the texture which will eventually result from the TextureData
	 * @param thePixelFormat the OpenGL pixel format of the texture which will eventually result from the TextureData
	 * @param theFileSuffix the suffix of the file name to be used as a hint of the file format to the underlying texture
	 *        provider, or null if none and should be auto-detected (some texture providers do not support this)
	 * @return the texture data from the URL, or null if none of the registered texture providers could read the URL
	 * @throws IllegalArgumentException if either internalFormat or pixelFormat was 0
	 * @throws IOException if an error occurred while reading the URL
	 */
	public static CCTextureData newTextureData(
		final URL theUrl, 
		final CCPixelInternalFormat theInternalFormat,
		final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	)throws IOException, IllegalArgumentException {
		if ((theInternalFormat == null) || (thePixelFormat == null)) {
			throw new IllegalArgumentException("internalFormat and pixelFormat must be non-zero");
		}

		return newTextureDataImpl(theUrl, theInternalFormat, thePixelFormat,theFileSuffix);
	}

	/**
	 * Creates a TextureData from the given BufferedImage, using the specified OpenGL internal format and pixel format
	 * for the texture which will eventually result. The internalFormat and pixelFormat must be specified and may not be
	 * zero; to use default values, use the variant of this method which does not take these arguments. Does no OpenGL
	 * work.
	 * 
	 * @param theImage the BufferedImage containing the texture data
	 * @param theInternalFormat the OpenGL internal format of the texture which will eventually result from the TextureData
	 * @param thePixelFormat the OpenGL pixel format of the texture which will eventually result from the TextureData
	 * @return the texture data from the image
	 * @throws IllegalArgumentException if either internalFormat or pixelFormat was 0
	 */
	public static CCTextureData newTextureData(
		final Image theImage,
		final CCPixelInternalFormat theInternalFormat, 
		final CCPixelFormat thePixelFormat
	)throws IllegalArgumentException {
		if ((theInternalFormat == null) || (thePixelFormat == null)) {
			throw new IllegalArgumentException("internalFormat and pixelFormat must be non-zero");
		}

		return newTextureDataImpl(theImage, theInternalFormat, thePixelFormat);
	}
	
	/**
	 * Writes the given texture data to a file. The format is read from the file name. If no
	 * suitable writer can be found an exception is thrown.
	 * @param theTextureData the data to be written to disk
	 * @param theFile file to save to
	 * @param theWriteMode
	 */
	public static void write(final CCTextureData theTextureData, final File theFile, final CCWriteMode theWriteMode){
		switch(theWriteMode){
		case KEEP:
			if(theFile.exists())return;
		default:
		}
		String myExtension = CCIOUtil.fileExtension(theFile);
		
		CCTextureFormat myFormat = textureFormats.get(myExtension);
		
		if(myFormat == null)throw new CCTextureException("The Image format:" + myExtension + " is not supported.");
		if (myFormat.write(theFile, theTextureData)) {
			return;
		}
		
		throw new CCTextureException("The given image could not be written.");
	}
	
	/**
	 * Writes the given texture data to a file. The format is read from the file name. If no
	 * suitable writer can be found an exception is thrown.
	 * @param theTextureData the data to be written to disk
	 * @param theFile file to save to
	 */
	public static void write(final CCTextureData theTextureData, final String theFile){
		try {
			write(theTextureData,theFile,CCWriteMode.OVERWRITE);
		} catch (Exception e) {
			throw new RuntimeException("Problems writing file "+theFile,e);
		}
	}
	
	public static boolean write(final BufferedImage theData, final String theFile) throws CCTextureException {
		
		
		try {
			return ImageIO.write(theData, CCIOUtil.fileExtension(theFile), new File(CCIOUtil.dataPath(theFile)));
		} catch (IOException e) {
			throw new CCTextureException(e);
		}
	}
	
	/**
	 * Writes the given texture data to a file. The format is read from the file name. If no
	 * suitable writer can be found an exception is thrown.
	 * @param theTextureData the data to be written to disk
	 * @param theFile file to save to
	 * @param theWriteMode
	 */
	public static void write(final CCTextureData theTexture, final String theFile, final CCWriteMode theWriteMode){
		try {
			CCIOUtil.createPath(CCIOUtil.dataPath(theFile));
			write(theTexture,new File(CCIOUtil.dataPath(theFile)),theWriteMode);
		} catch (Exception e) {
			throw new RuntimeException("Problems writing file "+theFile,e);
		}
	}
	
	private static Map<String, CCTextureFormat> textureFormats = new HashMap<String, CCTextureFormat>();
	public static boolean QTinitialized;
	
	/** 
	 * Adds a TextureProvider to support reading of a new file format. 
	 * @param theFormat the format to add
	 **/
	public static void addTextureFormat(String theExtension, final CCTextureFormat theFormat) {
		// Must always add at the front so the ImageIO provider is last,
		// so we don't accidentally use it instead of a user's possibly
		// more optimal provider
		textureFormats.put(theExtension, theFormat);
	}
	
	public static CCTextureFormat IMAGE_IO_FORMAT = new CCImageIOFormat();
	public static CCTextureFormat DDS_FORMAT = new CCDDSFormat();
	public static CCTextureFormat SGI_FORMAT = new CCSGIFormat();
	public static CCTextureFormat TGA_FORMAT = new CCTGAFormat();

	static {
		
		// ImageIO provider, the fall-back, must be the first one added
		addTextureFormat("png", IMAGE_IO_FORMAT);
		addTextureFormat("jpg", IMAGE_IO_FORMAT);
		addTextureFormat("jpeg", IMAGE_IO_FORMAT);
		addTextureFormat("gif", IMAGE_IO_FORMAT);

		// Other special-case providers
		addTextureFormat("dds", DDS_FORMAT);
		addTextureFormat("sgi", SGI_FORMAT);
		addTextureFormat("tga", TGA_FORMAT);
	}

	private static CCTextureData newTextureDataImpl(
		final File theFile,
		final CCPixelInternalFormat theInternalFormat, 
		final CCPixelFormat thePixelFormat,
		String theFileSuffix
	) {
		if (theFile == null) {
			throw new CCTextureException("File was null");
		}

		if (theFileSuffix == null) {
			theFileSuffix = CCIOUtil.fileExtension(theFile);
		}
		
		theFileSuffix = toLowerCase(theFileSuffix);
		
		CCTextureFormat myFormat = textureFormats.get(theFileSuffix);
		
		if(myFormat == null)throw new CCTextureException("The Image format:" + theFileSuffix + " is not supported.");
		
		CCTextureData data = myFormat.createTextureData(theFile, theInternalFormat, thePixelFormat, theFileSuffix);
		if (data != null) {
			return data;
		}
		
		throw new CCTextureException("The given image could not be loaded.");
	}

	private static CCTextureData newTextureDataImpl(
		InputStream theInputStream,
		final CCPixelInternalFormat theInternalFormat,
		final CCPixelFormat thePixelFormat,
		String theFileSuffix
	){
		if (theInputStream == null) {
			throw new CCTextureException("Stream was null");
		}

		theFileSuffix = toLowerCase(theFileSuffix);

		// Note: use of BufferedInputStream works around 4764639/4892246
		if (!(theInputStream instanceof BufferedInputStream)) {
			theInputStream = new BufferedInputStream(theInputStream);
		}
		
		CCTextureFormat myFormat = textureFormats.get(theFileSuffix);
		
		if(myFormat == null)throw new CCTextureException("The Image format:" + theFileSuffix + " is not supported.");
		
		CCTextureData data = myFormat.createTextureData(theInputStream, theInternalFormat, thePixelFormat, theFileSuffix);
		if (data != null) {
			return data;
		}
		
		throw new CCTextureException("The given image could not be loaded.");
	}

	private static CCTextureData newTextureDataImpl(
		final URL theURL,
		final CCPixelInternalFormat theInternalFormat, 
		final CCPixelFormat thePixelFormat,
		String theFileSuffix
	){
		if (theURL == null) {
			throw new CCTextureException("URL was null");
		}
		
		if (theFileSuffix == null) {
			theFileSuffix = CCIOUtil.fileExtension(theURL.getPath());
		}

		theFileSuffix = toLowerCase(theFileSuffix);

		CCTextureFormat myFormat = textureFormats.get(theFileSuffix);
		
		if(myFormat == null)throw new CCTextureException("The Image format:" + theFileSuffix + " is not supported.");
		
		CCTextureData data = myFormat.createTextureData(theURL, theInternalFormat, thePixelFormat, theFileSuffix);
		if (data != null) {
			return data;
		}
		
		throw new CCTextureException("The given image could not be loaded.");
	}

	/**
	 * 
	 * @param theImage
	 * @param theInternalFormat
	 * @param thePixelFormat
	 * @return
	 */
	private static CCTextureData newTextureDataImpl(
		final Image theImage,
		final CCPixelInternalFormat theInternalFormat, 
		final CCPixelFormat thePixelFormat
	) {
		CCTextureData myTextureData = new CCTextureData();
		myTextureData.internalFormat(theInternalFormat);
		myTextureData.pixelFormat(thePixelFormat);
		
		return CCTextureUtil.toTextureData(theImage, myTextureData);
	}
	
	public static CCTextureData loadCubeMapData(
		final String thePositiveX, final String theNegativeX,
		final String thePositiveY, final String theNegativeY,
		final String thePositiveZ, final String theNegativeZ
	) {
		Buffer[] myBuffers = new Buffer[6];
		CCTextureData myTextureData = newTextureData(thePositiveX);
		myBuffers[0] = myTextureData.buffer();
		myBuffers[1] = newTextureData(theNegativeX).buffer();
		myBuffers[2] = newTextureData(thePositiveY).buffer();
		myBuffers[3] = newTextureData(theNegativeY).buffer();
		myBuffers[4] = newTextureData(thePositiveZ).buffer();
		myBuffers[5] = newTextureData(theNegativeZ).buffer();
		myTextureData.buffers(myBuffers);
		
		return myTextureData;
	}
}
