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


/**
 * Represents a translation, rotation and scale in one object.
 */
public final class CCTransform {

    private CCQuaternion _myRotation = new CCQuaternion();
    private CCVector3f _myTranslation = new CCVector3f();
    private CCVector3f _myScale = new CCVector3f(1,1,1);

    public CCTransform(CCVector3f theTranslation, CCQuaternion theRotation){
        this._myTranslation.set(theTranslation);
        this._myRotation.set(theRotation);
    }

    public CCTransform(CCVector3f theTranslation){
        this(theTranslation, new CCQuaternion());
    }

    public CCTransform(CCQuaternion theRotation){
        this(new CCVector3f(), theRotation);
    }

    public CCTransform(){
        this(new CCVector3f(), new CCQuaternion());
    }

    /**
     * Sets this rotation to the given CCQuaternion value.
     * @param theRotation The new rotation for this transform.
     * @return this
     */
    public CCTransform rotation(CCQuaternion theRotation) {
        _myRotation.set(theRotation);
        return this;
    }

    /**
     * Sets this translation to the given value.
     * @param theTranslation The new translation for this transform.
     * @return this
     */
    public CCTransform translation(CCVector3f theTranslation) {
        _myTranslation.set(theTranslation);
        return this;
    }

    /**
     * Sets this translation to the given x,y,z values.
     * @param theX This transform's new x translation.
     * @param theY This transform's new y translation.
     * @param theZ This transform's new z translation.
     * @return this
     */
    public CCTransform translation(float theX,float theY, float theZ) {
        _myTranslation.set(theX,theY,theZ);
        return this;
    }

    /**
     * Return the translation vector in this transform.
     * @return translation vector.
     */
    public CCVector3f translation() {
        return _myTranslation;
    }

    /**
     * Sets this scale to the given value.
     * @param theScale The new scale for this transform.
     * @return this
     */
    public CCTransform scale(CCVector3f theScale) {
        _myScale.set(theScale);
        return this;
    }

    /**
     * Sets this transform's scale to the given x,y,z values.
     * @param theX This transform's new x scale.
     * @param theY This transform's new y scale.
     * @param theZ This transform's new z scale.
     * @return this
     */
    public CCTransform scale(float theX, float theY, float theZ) {
        _myScale.set(theX,theY,theZ);
        return this;
    }

    /**
     * Sets this scale to the given value.
     * @param theScale The new scale for this transform.
     * @return this
     */
    public CCTransform scale(float theScale) {
        _myScale.set(theScale, theScale, theScale);
        return this;
    }

    /**
     * Return the scale vector in this transform.
     * @return scale vector.
     */
    public CCVector3f scale() {
        return _myScale;
    }
    
    /**
     * Return the rotation quaternion in this transform.
     * @return rotation quaternion.
     */
    public CCQuaternion rotation() {
        return _myRotation;
    } 

    /**
     * Sets this transform to the interpolation between the first transform and the second by delta amount.
     * @param t1 The begining transform.
     * @param t2 The ending transform.
     * @param delta An amount between 0 and 1 representing how far to interpolate from t1 to t2.
     */
    public void interpolateTransforms(CCTransform t1, CCTransform t2, float delta) {
        _myRotation = CCQuaternion.blend(t1._myRotation,t2._myRotation,delta);
        _myTranslation = CCVecMath.blend(delta,t1._myTranslation,t2._myTranslation);
        _myScale = CCVecMath.blend(delta, t1._myScale,t2._myScale);
    }

    /**
     * Changes the values of this transform according to it's parent.  
     * Very similar to the concept of Node/Spatial transforms.
     * @param theParent The parent transform.
     * @return This transform, after combining.
     */
    public CCTransform combineWithParent(CCTransform theParent) {
        _myScale.scale(theParent._myScale);
        theParent._myRotation.multiply(_myRotation, _myRotation);

        _myTranslation.scale(theParent._myScale);
        theParent._myRotation.multLocal(_myTranslation).add(theParent._myTranslation);
        return this;
    }
    
    public CCVector3f transformVector(final CCVector3f theInput) {
    	return transformVector(theInput, null);
    }

    public CCVector3f transformVector(final CCVector3f theInput, CCVector3f theOutput){
        if (theOutput == null)
            theOutput = new CCVector3f();

        // multiply with scale first, then rotate, finally translate (cf.
        // Eberly)
        return _myRotation.multiply(theOutput.set(theInput).scale(_myScale), theOutput).add(_myTranslation);
    }

    public CCVector3f transformInverseVector(final CCVector3f theInput, CCVector3f theOutput){
        if (theOutput == null)
            theOutput = new CCVector3f();

        // The author of this code should look above and take the inverse of that
        // But for some reason, they didnt ..
//        in.subtract(translation, store).divideLocal(scale);
//        rot.inverse().mult(store, store);
        theOutput = CCVecMath.subtract(theInput, _myTranslation);
        _myRotation.inverse().multiply(theOutput, theOutput);
        theOutput.devide(_myScale);

        return theOutput;
    }
    
    public CCVector3f transformInverseVector(final CCVector3f theInput) {
    	return transformInverseVector(theInput, null);
    }

    /**
     * Loads the identity.  Equal to translation=1,1,1 scale=0,0,0 rot=0,0,0,1.
     */
    public void loadIdentity() {
        _myTranslation.set(0,0,0);
        _myScale.set(1,1,1);
        _myRotation.set(0,0,0,1);
    }

    @Override
    public String toString(){
        return getClass().getSimpleName() + "[ " + _myTranslation.x + ", " + _myTranslation.y + ", " + _myTranslation.z + "]\n"
                                          + "[ " + _myRotation.x + ", " + _myRotation.y + ", " + _myRotation.z + ", " + _myRotation.w + "]\n"
                                          + "[ " + _myScale.x + " , " + _myScale.y + ", " + _myScale.z + "]";
    }

    /**
     * Sets this transform to be equal to the given transform.
     * @param theTransform The transform to be equal to.
     * @return this
     */
    public CCTransform set(CCTransform theTransform) {
        _myTranslation.set(theTransform._myTranslation);
        _myRotation.set(theTransform._myRotation);
        _myScale.set(theTransform._myScale);
        return this;
    }
    
    @Override
    public CCTransform clone() {
    	CCTransform myTransform = new CCTransform();
    	myTransform._myRotation = _myRotation.clone();
    	myTransform._myScale = _myScale.clone();
    	myTransform._myTranslation = _myTranslation.clone();
    	return myTransform;
    }
}
