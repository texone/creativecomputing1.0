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
package cc.creativecomputing.math.signal;

/**
 * @author christianriekoff
 *
 */
public class CCSawSignal extends CCSignal{

	@Override
	public float[] signalImpl(float theX, float theY, float theZ) {
		return new float[]{1- theX % 1 + 1 - theY % 1 + 1 - theZ % 1};
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#signalImpl(float, float)
	 */
	@Override
	public float[] signalImpl(float theX, float theY) {
		return new float[]{1 - theX % 1 + 1 - theY % 1};
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#signalImpl(float)
	 */
	@Override
	public float[] signalImpl(float theX) {
		return new float[]{1 - theX % 1};
	}

}
