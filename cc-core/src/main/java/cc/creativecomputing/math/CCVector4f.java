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
package cc.creativecomputing.math;

public class CCVector4f extends CCVector3f{

    private static final long serialVersionUID = 5083919691492230155L;
    
    public float w;

    public CCVector4f() {
        this(0,0,0,0);
    }


    public CCVector4f(final float theX, final float theY, final float theZ, final float theW) {
        set(theX, theY, theZ,  theW);
    }


    public CCVector4f(final double theX, final double theY, final double theZ, final double theW) {
        set(theX, theY, theZ, theW);
    }


    public CCVector4f(final float theX, final float theY, final float theZ) {
        super(theX, theY, theZ, 1);
    }


    public CCVector4f(final double theX, final double theY, final double theZ) {
        set(theX, theY, theZ);
    }


    public CCVector4f(final float theX, final float theY) {
        super(theX, theY, 0, 1);
    }


    public CCVector4f(final double theX, final double theY) {
        set(theX, theY);
    }


    public CCVector4f(float[] theVector) {
        super(theVector);
    }


    public CCVector4f(double[] theVector) {
        set(theVector);
    }


    public CCVector4f(final CCVector4f theVector) {
        set(theVector);
    }


    public CCVector4f(final CCVector3f theVector) {
        this(theVector.x, theVector.y, theVector.z, 1);
    }


    public CCVector4f(final CCVector3f theVector, float theW) {
        super(theVector.x, theVector.y, theVector.z, theW);
        w = theW;
    }


    public CCVector4f(CCVector2f theVector) {
    	super(theVector.x, theVector.y, 0, 1);
    }


    public CCVector4f set(final float theX, final float theY, final float theZ, final float theW) {
        x = theX;
        y = theY;
        z = theZ;
        w = theW;
        return this;
    }


    public CCVector4f set(final double theX, final double theY, final double theZ, final double theW) {
    	x = (float) theX;
    	y = (float) theY;
    	z = (float) theZ;
    	w = (float) theW;
    	return this;
    }


    public CCVector4f set(final float theX, final float theY, final float theZ) {
    	return set(theX, theY, theZ, 1);
    }

	public CCVector4f set(final double theX, double theY, double theZ) {
		return set(theX, theY, theZ, 1);
	}


    public CCVector4f set(final float theX, final float theY) {
        return set(theX, theY, 0);
    }


    public CCVector4f set(final double theX, final double theY) {
        return set(theX, theY, 0);
    }


    public CCVector4f set(float[] theVector) {
        return set(theVector[0],theVector[1],theVector[2],theVector[3]);
    }


    public CCVector4f set(double[] theVector) {
    	return set(theVector[0],theVector[1],theVector[2],theVector[3]);
    }


    public CCVector4f set(CCVector4f theVector) {
        return set(theVector.x, theVector.y, theVector.z, theVector.w);
    }


    public CCVector4f set(CCVector3f theVector) {
        return set(theVector.x, theVector.y, theVector.z);
    }


    public CCVector4f set(CCVector2f theVector) {
    	return set(theVector.x, theVector.y);
    }

    @Override
    public void set(int theIndex, float theValue) {
		switch(theIndex) {
		case 0:
			x = theValue;
			break;
		case 1:
			y = theValue;
			break;
		case 2:
			z = theValue;
			break;
		case 3:
			w = theValue;
			break;
		}
	}

    public CCVector4f add(final CCVector4f theVector) {
    	super.add(theVector);
        w(w() + theVector.w());
        return this;
    }

    public CCVector4f subtract(final CCVector4f theVector) {
    	super.subtract(theVector);
        w(w() - theVector.w());
        return this;
    }


    public CCVector4f scale(float theScale) {
    	super.scale(theScale);
        w(w() * theScale);
        return this;
    }


    public float lengthSquared() {
        return w() * w() + super.lengthSquared();
    }


    public float dot(CCVector4f theVector) {
        return super.dot(theVector) + w() * theVector.w();
    }


    public CCVector4f normalize() {
        final float d = 1 / length();
        scale(d);
        return this;
    }

    public boolean isNaN() {
        if (super.isNaN() || Float.isNaN(w())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean equals(CCVector4f theVector) {
        if (w() == theVector.w() && super.equals(theVector)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean almost(CCVector4f theVector) {
        if (Math.abs(w()) - Math.abs(theVector.w()) < ALMOST_THRESHOLD && super.almost(theVector)){
            return true;
        } else {
            return false;
        }
    }


    public final String toString() {
        return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }


	/**
	 * @param w the w to set
	 */
	public void w(float theW) {
		w = theW;
	}


	/**
	 * @return the w
	 */
	public float w() {
		return w;
	}

}
