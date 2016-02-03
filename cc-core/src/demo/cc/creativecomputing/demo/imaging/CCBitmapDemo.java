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
package cc.creativecomputing.demo.imaging;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;

/**
 * This program uses the bitmap data that represents the shape of a small campfire arranged as a pattern of bits
 * measuring 32x32. Remember that bitmaps are built from the bottom up, which means the first row of data actually
 * represents the bottom row of the bitmapped image. This program creates a 512x512 window and fills the window with 16
 * rows and columns of the campfire bitmap. Note that the ChangeSize function sets an orthographic projection matching
 * the window's width and height in pixels.
 * 
 * @author info
 * 
 */
public class CCBitmapDemo extends CCApp {

	// Bitmap of camp fire

	byte[] fire = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xC0, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xF0, (byte) 0x00, (byte) 0x00, (byte) 0x07,
			(byte) 0xf0, (byte) 0x0f, (byte) 0x00, (byte) 0x1f, (byte) 0xe0, (byte) 0x1f, (byte) 0x80, (byte) 0x1f, (byte) 0xc0, (byte) 0x0f, (byte) 0xc0, (byte) 0x3f,
			(byte) 0x80, (byte) 0x07, (byte) 0xe0, (byte) 0x7e, (byte) 0x00, (byte) 0x03, (byte) 0xf0, (byte) 0xff, (byte) 0x80, (byte) 0x03, (byte) 0xf5, (byte) 0xff,
			(byte) 0xe0, (byte) 0x07, (byte) 0xfd, (byte) 0xff, (byte) 0xf8, (byte) 0x1f, (byte) 0xfc, (byte) 0xff, (byte) 0xe8, (byte) 0xff, (byte) 0xe3, (byte) 0xbf,
			(byte) 0x70, (byte) 0xde, (byte) 0x80, (byte) 0xb7, (byte) 0x00, (byte) 0x71, (byte) 0x10, (byte) 0x4a, (byte) 0x80, (byte) 0x03, (byte) 0x10, (byte) 0x4e,
			(byte) 0x40, (byte) 0x02, (byte) 0x88, (byte) 0x8c, (byte) 0x20, (byte) 0x05, (byte) 0x05, (byte) 0x04, (byte) 0x40, (byte) 0x02, (byte) 0x82, (byte) 0x14,
			(byte) 0x40, (byte) 0x02, (byte) 0x40, (byte) 0x10, (byte) 0x80, (byte) 0x02, (byte) 0x64, (byte) 0x1a, (byte) 0x80, (byte) 0x00, (byte) 0x92, (byte) 0x29,
			(byte) 0x00, (byte) 0x00, (byte) 0xb0, (byte) 0x48, (byte) 0x00, (byte) 0x00, (byte) 0xc8, (byte) 0x90, (byte) 0x00, (byte) 0x00, (byte) 0x85, (byte) 0x10,
			(byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10, (byte) 0x00 };

	@Override
	public void setup() {
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {

		g.ortho2D();
		// Clear the window with current clearing color
		g.clear();

		// Set color to white
		g.color(1f);

		// Loop through 16 rows and columns
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				// set window position
				g.windowPos(x * 32, y * 32);
				// Draw the "fire" bitmap, advance raster position
				g.gl.glBitmap(32, 32, 0.0f, 0.0f, 0f, 0.0f, fire, 0);
			}
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCBitmapDemo.class);
		myManager.settings().size(512, 512);
		myManager.start();
	}
}
