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
package cc.creativecomputing.model.material;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture;

public class CCTextureProperty implements CCModelMaterialProperty{
	
	private CCTexture _myTexture;
	
	public CCTextureProperty(final CCTexture theTexture){
		_myTexture = theTexture;
	}
	
	public void texture(final CCTexture theTexture){
		_myTexture = theTexture;
	}

	public void begin(CCGraphics g) {
		g.texture(_myTexture);
	}

	public void end(CCGraphics g) {
		g.noTexture();
	}

}
