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

import cc.creativecomputing.graphics.texture.CCPixelType;

import com.jogamp.opencl.CLImageFormat.ChannelType;

/**
 * Describes the size of the channel data type.
 */
public enum CCCLPixelType {

	/**
	 * Each channel component is a normalized signed 8-bit integer value.
	 */
	SNORM_INT8(ChannelType.SNORM_INT8, CCPixelType.BYTE),

	/**
	 * Each channel component is a normalized signed 16-bit integer value.
	 */
	SNORM_INT16(ChannelType.SNORM_INT16, CCPixelType.SHORT),

	/**
	 * Each channel component is a normalized unsigned 8-bit integer value.
	 */
	UNORM_INT8(ChannelType.UNORM_INT8, CCPixelType.UNSIGNED_BYTE),

	/**
	 * Each channel component is a normalized unsigned 16-bit integer value.
	 */
	UNORM_INT16(ChannelType.UNORM_INT16, CCPixelType.UNSIGNED_SHORT),

	/**
	 * Represents a normalized 5-6-5 3-channel RGB image. The channel order must be {@link ChannelOrder#RGB}.
	 */
	UNORM_SHORT_565(ChannelType.UNORM_SHORT_565, CCPixelType.UNSIGNED_SHORT_5_6_5),

	/**
	 * Represents a normalized x-5-5-5 4-channel xRGB image. The channel order must be {@link ChannelOrder#RGB}.
	 */
	UNORM_SHORT_555(ChannelType.UNORM_SHORT_555, CCPixelType.UNSIGNED_SHORT_5_5_5_1),

	/**
	 * Represents a normalized x-10-10-10 4-channel xRGB image. The channel order must be {@link ChannelOrder#RGB}.
	 */
	UNORM_INT_101010(ChannelType.UNORM_INT_101010, CCPixelType.UNSIGNED_INT_10_10_10_2),

	/**
	 * Each channel component is an unnormalized signed 8-bit integer value.
	 */
	SIGNED_INT8(ChannelType.SIGNED_INT8, CCPixelType.BYTE),

	/**
	 * Each channel component is an unnormalized signed 16-bit integer value.
	 */
	SIGNED_INT16(ChannelType.SIGNED_INT16, CCPixelType.SHORT),

	/**
	 * Each channel component is an unnormalized signed 32-bit integer value.
	 */
	SIGNED_INT32(ChannelType.SIGNED_INT32, CCPixelType.INT),

	/**
	 * Each channel component is an unnormalized unsigned 8-bit integer value.
	 */
	UNSIGNED_INT8(ChannelType.UNSIGNED_INT8, CCPixelType.UNSIGNED_BYTE),

	/**
	 * Each channel component is an unnormalized unsigned 16-bit integer value.
	 */
	UNSIGNED_INT16(ChannelType.UNSIGNED_INT16, CCPixelType.UNSIGNED_SHORT),

	/**
	 * Each channel component is an unnormalized unsigned 32-bit integer value.
	 */
	UNSIGNED_INT32(ChannelType.UNSIGNED_INT32, CCPixelType.UNSIGNED_INT),

	/**
	 * Each channel component is a 16-bit half-float value.
	 */
	HALF_FLOAT(ChannelType.HALF_FLOAT, CCPixelType.FLOAT),

	/**
	 * Each channel component is a single precision floating-point value.
	 */
	FLOAT(ChannelType.FLOAT, CCPixelType.FLOAT);

	/**
	 * Value of wrapped OpenCL flag.
	 */
	private final ChannelType _myChannelType;
	private CCPixelType _myPixelType;
	
	public CCPixelType pixelType() {
		return _myPixelType;
	}
	

	public ChannelType channelType() {
		return _myChannelType;
	}

	private CCCLPixelType(ChannelType theChannelType, CCPixelType thePixelType) {
		_myChannelType = theChannelType;
		_myPixelType = thePixelType;
	}

	public static CCCLPixelType valueOf(ChannelType channelFlag) {
		switch (channelFlag) {
		case SNORM_INT8:
			return SNORM_INT8;
		case SNORM_INT16:
			return SNORM_INT16;
		case UNORM_INT8:
			return UNORM_INT8;
		case UNORM_INT16:
			return UNORM_INT16;
		case UNORM_SHORT_565:
			return UNORM_SHORT_565;
		case UNORM_SHORT_555:
			return UNORM_SHORT_555;
		case UNORM_INT_101010:
			return UNORM_INT_101010;
		case SIGNED_INT8:
			return SIGNED_INT8;
		case SIGNED_INT16:
			return SIGNED_INT16;
		case SIGNED_INT32:
			return SIGNED_INT32;
		case UNSIGNED_INT8:
			return UNSIGNED_INT8;
		case UNSIGNED_INT16:
			return UNSIGNED_INT16;
		case UNSIGNED_INT32:
			return UNSIGNED_INT32;
		case HALF_FLOAT:
			return HALF_FLOAT;
		case FLOAT:
			return FLOAT;
		}
		return null;
	}

}
