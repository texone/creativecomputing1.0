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
package cc.creativecomputing.io;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class CCFileFilter extends FileFilter {

	private String[] _myExtensions;
	private String _myDescribtion;

	public CCFileFilter(String theDescribtion, String... theExtensions) {
		_myExtensions = theExtensions;
		_myDescribtion = theDescribtion;
	}

	@Override
	public boolean accept(File pathname) {
		int myDotIndex = pathname.getName().lastIndexOf(".");
		if (myDotIndex > 0) {
			String myExtension = pathname.getName().substring(myDotIndex + 1);
			for (String myAllowedExtension : _myExtensions) {
				if (myExtension.equals(myAllowedExtension))
					return true;

			}
		} else if (pathname.isDirectory()) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return _myDescribtion;
	}

}
