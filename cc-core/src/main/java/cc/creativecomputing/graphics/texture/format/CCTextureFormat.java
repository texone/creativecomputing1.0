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
import java.io.InputStream;
import java.net.URL;

import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCTextureData;
import cc.creativecomputing.graphics.texture.CCTextureException;

/**
 * @author christian riekoff
 *
 */
public interface CCTextureFormat {
	public CCTextureData createTextureData(
		final String theFile, 
		final CCPixelInternalFormat theInternalFormat, final CCPixelFormat thePixelFormat, 
		final String theFileSuffix
	);
	
	/**
	 * Produces a TextureData object from a file, or returns null if the file format was not supported by this
	 * TextureProvider. Does not do any OpenGL-related work. The resulting TextureData can be converted into an OpenGL
	 * texture in a later step.
	 * 
	 * @param theFile the file from which to read the texture data
	 * 
	 * @param theInternalFormat the OpenGL internal format to be used for the texture, or 0 if it should be inferred from
	 *        the file's contents
	 * 
	 * @param thePixelFormat the OpenGL pixel format to be used for the texture, or 0 if it should be inferred from the
	 *        file's contents
	 * 
	 * @param theFileSuffix the file suffix to be used as a hint to the provider to more quickly decide whether it can
	 *        handle the file, or null if the provider should infer the type from the file's contents
	 * 
	 * @throws CCTextureException if an error occurred while reading the file
	 */
	public CCTextureData createTextureData(
		File theFile, 
		CCPixelInternalFormat theInternalFormat, CCPixelFormat thePixelFormat, 
		String theFileSuffix
	) throws CCTextureException;

	/**
	 * Produces a TextureData object from a stream, or returns null if the file format was not supported by this
	 * TextureProvider. Does not do any OpenGL-related work. The resulting TextureData can be converted into an OpenGL
	 * texture in a later step.
	 * 
	 * @param theStream the stream from which to read the texture data
	 * 
	 * @param theInternalFormat the OpenGL internal format to be used for the texture, or 0 if it should be inferred from
	 *        the file's contents
	 * 
	 * @param thePixelFormat the OpenGL pixel format to be used for the texture, or 0 if it should be inferred from the
	 *        file's contents
	 * 
	 * @param theFileSuffix the file suffix to be used as a hint to the provider to more quickly decide whether it can
	 *        handle the file, or null if the provider should infer the type from the file's contents
	 * 
	 * @throws CCTextureException if an error occurred while reading the stream
	 */
	public CCTextureData createTextureData(
		InputStream theStream, 
		CCPixelInternalFormat theInternalFormat, CCPixelFormat thePixelFormat, 
		String theFileSuffix
	) throws CCTextureException;

	/**
	 * Produces a TextureData object from a URL, or returns null if the file format was not supported by this
	 * TextureProvider. Does not do any OpenGL-related work. The resulting TextureData can be converted into an OpenGL
	 * texture in a later step.
	 * 
	 * @param theUrl the URL from which to read the texture data
	 * 
	 * @param theInternalFormat the OpenGL internal format to be used for the texture, or 0 if it should be inferred from
	 *        the file's contents
	 * 
	 * @param thePixelFormat the OpenGL pixel format to be used for the texture, or 0 if it should be inferred from the
	 *        file's contents
	 * 
	 * @param theFileSuffix the file suffix to be used as a hint to the provider to more quickly decide whether it can
	 *        handle the file, or null if the provider should infer the type from the file's contents
	 * 
	 * @throws CCTextureException if an error occurred while reading the URL
	 */
	public CCTextureData createTextureData(
		URL theUrl, 
		CCPixelInternalFormat theInternalFormat, CCPixelFormat thePixelFormat, 
		String theFileSuffix
	) throws CCTextureException;

	/**
	 * Writes the given TextureData to the passed file. Returns true if this TextureWriter successfully handled the
	 * writing of the file, otherwise false. May throw IOException if either this writer did not support certain
	 * parameters of the TextureData or if an I/O error occurred.
	 */
	public boolean write(File theFile, CCTextureData theData) throws CCTextureException;
}
