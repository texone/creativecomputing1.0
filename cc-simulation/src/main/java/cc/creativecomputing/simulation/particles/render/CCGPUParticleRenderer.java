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
package cc.creativecomputing.simulation.particles.render;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.simulation.particles.CCGPUParticles;

/**
 * @author christianriekoff
 *
 */
public abstract class CCGPUParticleRenderer {
	
	public abstract void setup(CCGPUParticles theParticles);

	public abstract void update(final float theDeltaTime);
	
	public abstract void draw(CCGraphics g);
	
	public abstract CCVBOMesh mesh();
}
