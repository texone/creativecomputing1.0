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
package cc.creativecomputing.opencl;

import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;

import com.jogamp.opencl.CLImageFormat.ChannelOrder;

/**
 * Specifies the number of channels and the channel layout i.e. the memory layout in which channels are stored in the
 * image.
 */
public enum CCCLPixelFormat {

	/**
	 * Single red channel.
	 */
	RED(ChannelOrder.R, CCPixelFormat.RED, CCPixelInternalFormat.RED),

	/**
     * Single alpha channel.
     */
	ALPHA(ChannelOrder.A, CCPixelFormat.ALPHA, CCPixelInternalFormat.ALPHA),

	/**
     * Red then green channel.
     */
	RG(ChannelOrder.RG, CCPixelFormat.LUMINANCE_ALPHA, CCPixelInternalFormat.LUMINANCE_ALPHA),

	/**
     * Red then alpha channel.
     */
	RA(ChannelOrder.RA, CCPixelFormat.LUMINANCE_ALPHA, CCPixelInternalFormat.LUMINANCE_ALPHA),

	/**
	 * Red, green, and blue channels.
	 * This format can only be used if channel data type is one of the following values:
	 * {@link ChannelType#UNORM_SHORT_565}, {@link ChannelType#UNORM_SHORT_555} or {@link ChannelType#UNORM_INT_101010}.
	 */
	RGB(ChannelOrder.RGB, CCPixelFormat.RGB, CCPixelInternalFormat.RGB),

	/**
     * Red, green, blue, then alpha channels.
     */
	RGBA(ChannelOrder.RGBA, CCPixelFormat.RGBA, CCPixelInternalFormat.RGBA),

	/**
	 * Blue, green, red, then alpha channels.
	 */
	BGRA(ChannelOrder.BGRA, CCPixelFormat.BGRA, CCPixelInternalFormat.BGRA),

	/**
	 * Single intensity channel.
	 * This format can only be used if channel data type is one of the following values: {@link ChannelType#UNORM_INT8},
	 * {@link ChannelType#UNORM_INT16}, {@link ChannelType#SNORM_INT8}, {@link ChannelType#SNORM_INT16},
	 * {@link ChannelType#HALF_FLOAT}, or {@link ChannelType#FLOAT}.
	 */
	INTENSITY(ChannelOrder.INTENSITY, CCPixelFormat.LUMINANCE, CCPixelInternalFormat.LUMINANCE),

	/**
	 * Single luminence channel.
	 * This format can only be used if channel data type is one of the following values: {@link ChannelType#UNORM_INT8},
	 * {@link ChannelType#UNORM_INT16}, {@link ChannelType#SNORM_INT8}, {@link ChannelType#SNORM_INT16},
	 * {@link ChannelType#HALF_FLOAT}, or {@link ChannelType#FLOAT}.
	 */
	LUMINANCE(ChannelOrder.LUMINANCE, CCPixelFormat.LUMINANCE, CCPixelInternalFormat.LUMINANCE);

	/**
	 * Value of wrapped OpenCL flag.
	 */
	private ChannelOrder _myChannelOrder;
	private CCPixelFormat _myPixelFormat;
	private CCPixelInternalFormat _myPixelInternalFormat;

	private CCCLPixelFormat(ChannelOrder theChannelOrder, CCPixelFormat thePixelFormat, CCPixelInternalFormat theInternalFormat) {
		_myChannelOrder = theChannelOrder;
		_myPixelFormat = thePixelFormat;
		_myPixelInternalFormat = theInternalFormat;
	}
	
	public CCPixelFormat format() {
		return _myPixelFormat;
	}
	
	public CCPixelInternalFormat internalFormat() {
		return _myPixelInternalFormat;
	}
	
	public ChannelOrder channelOrder() {
		return _myChannelOrder;
	}

	public static CCCLPixelFormat valueOf(ChannelOrder theChannelOrder) {
		switch (theChannelOrder) {
		case R:
			return RED;
		case A:
			return ALPHA;
		case INTENSITY:
			return INTENSITY;
		case LUMINANCE:
			return LUMINANCE;
		case RG:
			return RG;
		case RA:
			return RA;
		case RGB:
			return RGB;
		case RGBA:
			return RGBA;
		case BGRA:
			return BGRA;
        default:
		}
		return null;
	}

}
