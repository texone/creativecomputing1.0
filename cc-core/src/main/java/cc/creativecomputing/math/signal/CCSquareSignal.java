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
public class CCSquareSignal extends CCSignal{

	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#noiseImpl(float, float, float)
	 */
	@Override
	public float[] signalImpl(float theX, float theY, float theZ) {
		return new float[]{((int)theX) % 2 + ((int)theY) % 2 + ((int)theZ) % 2};
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#signalImpl(float, float)
	 */
	@Override
	public float[] signalImpl(float theX, float theY) {
		return new float[]{((int)theX) % 2 + ((int)theY) % 2};
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#signalImpl(float)
	 */
	@Override
	public float[] signalImpl(float theX) {
		return new float[]{((int)theX) % 2};
	}

}
