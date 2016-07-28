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
package cc.creativecomputing.simulation.domain;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVecMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.CCParticle;
/**
 * The point o is a point on the plane. n is the normal vector of the plane. 
 * It need not be unit length. If you have a plane in a,b,c,d form remember 
 * that n = [a,b,c] and you can compute a suitable point o as o = -n*d. 
 * The normal will get normalized, so it need not already be normalized.
 * <br>
 * Generate returns the point o.
 * <br>
 * Within returns true if the point is in the positive half-space of the plane 
 * (in the plane or on the side that n points to).
 * @author christianr
 *
 */
public class CCPlaneDomain extends CCDomain{
	protected CCVector3f _myPoint;
	protected CCVector3f _myNormal;
	float d;

	/**
	 * Initializes a new Plane
	 * @param thePoint
	 * @param theNormal
	 */
	public CCPlaneDomain(final CCVector3f thePoint, final CCVector3f theNormal){
		_myPoint = thePoint;
		_myNormal = theNormal;
		_myNormal.normalize(); // Must normalize it.
		d = -_myPoint.dot(_myNormal);
	}
	
	public CCPlaneDomain(CCVector3f v1,  CCVector3f v2,  CCVector3f v3){
		set(v1,v2,v3);
	}
	
	/**
	 * Constructor for extensions of plane were the normal is set later
	 * @param thePoint
	 */
	protected CCPlaneDomain(final CCVector3f thePoint){
		_myPoint = thePoint;
	}
	
	public void set(CCVector3f v1,  CCVector3f v2,  CCVector3f v3) {
		CCVector3f aux1 = CCVecMath.subtract(v1, v2);
		CCVector3f aux2 = CCVecMath.subtract(v3, v2);

		_myNormal = CCVecMath.cross(aux2, aux1);
		_myNormal.normalize();
		_myPoint = v2;
		d = -_myPoint.dot(_myNormal);
	}
	
	/**
	 * Distance from plane = n * p + d
	 * Inside is the positive half-space.
	 */
	public boolean isWithin(final CCVector3f i_vector){
		return _myNormal.dot(i_vector) >= -d;
	}

	/**
	 * How do I sensibly make a point on an infinite plane?
	 */
	public CCVector3f generate(){
		return _myPoint.clone();
	}
	
	/**
	 * Returns the distance of the given vector to the plane
	 * @param theVector
	 * @return
	 */
	public float distance(final CCVector3f theVector){
		return theVector.dot(_myNormal) + d;
	}
	
	@Override
	public boolean intersectsLine(final CCVector3f theVectorA, final CCVector3f theVectorB){
		final CCVector3f p3_p1 = _myPoint.clone();
		p3_p1.subtract(theVectorA);
		
		final CCVector3f p2_p1 = theVectorB.clone();
		p2_p1.subtract(theVectorA);
		
		float u = _myNormal.dot(p3_p1)/_myNormal.dot(p2_p1);
		return u > 0 && u < 1;
	}
	
	

	@Override
	public boolean intersectsLine(
		final CCVector3f theVectorA, final CCVector3f theVectorB,
		final CCVector3f thePointOnSurface, final CCVector3f theNormal
	){
		final CCVector3f p3_p1 = _myPoint.clone();
		p3_p1.subtract(theVectorA);
		
		final CCVector3f p2_p1 = theVectorB.clone();
		p2_p1.subtract(theVectorA);
		
		float lineDot = _myNormal.dot(p2_p1);
		float u = _myNormal.dot(p3_p1)/_myNormal.dot(p2_p1);

		
		if(u > 0 && u < 1){
			thePointOnSurface.set(p2_p1);
			thePointOnSurface.scale(u);
			thePointOnSurface.add(theVectorA);
			
			theNormal.set(_myNormal);
			
			if(lineDot > 0)theNormal.scale(-1);
			
			return true;
		}
		
		return false;
	}

	@Override
	public boolean intersectsBox(final CCVector3f theMinCorner, final CCVector3f theMaxVector) {
		return intersectsLine(theMinCorner, theMaxVector);
	}
	
	@Override
	public void avoidance(final CCParticle theParticle, final CCVector3f theForce, final float theLookAhead, final float theEpsilon){
		
        // See if particle's current and look_ahead positions cross plane.
        // If not, couldn't hit, so keep going.
        CCVector3f myFutureParticlePosition = theParticle.futurePosition(theLookAhead);

        // nrm stores the plane normal (the a,b,c of the plane eqn).
        // Old and new distances: dist(p,plane) = n * p + d
        final float myOldDistance = CCVecMath.dot(theParticle.position, _myNormal) + d;
        final float myNewDistance = CCVecMath.dot(myFutureParticlePosition, _myNormal) + d;
        
        if(CCMath.sameSign(myOldDistance, myNewDistance))
            return;
       
        // Time to collision
        final float myCollisionTime = -myOldDistance / CCVecMath.dot(_myNormal, theParticle.velocity());

        // Vector from projection point to point of impact
        CCVector3f s = new CCVector3f(
        	theParticle.velocity().x * myCollisionTime + _myNormal.x * myOldDistance,
        	theParticle.velocity().y * myCollisionTime + _myNormal.y * myOldDistance,
        	theParticle.velocity().z * myCollisionTime + _myNormal.z * myOldDistance
        );
        
        float slen = s.lengthSquared();
        if(slen == 0.0f)
            s = _myNormal;
        else
            s.normalize();

        s.scale(10000f / (CCMath.sq(myCollisionTime)+theEpsilon));
        theForce.add(s);
	}
	
	
	
	@Override
	public void bounce(
		final CCParticle theParticle, 
		final CCVector3f theVector, 
		final float theDeltaTime,
		final float theOneMinusFriction,
		final float theResilence,
		final float theCutOffSquared
	) {

        // See if particle's current and look_ahead positions cross plane.
        // If not, couldn't hit, so keep going.
		CCVector3f myFutureParticlePosition = theParticle.futurePosition(10);

        // Old and new distances: dist(p,plane) = n * p + d
        final float myOldDistance = CCVecMath.dot(theParticle.position, _myNormal) + d;
        final float myNewDistance = CCVecMath.dot(myFutureParticlePosition, _myNormal) + d;
        
        if(CCMath.sameSign(myOldDistance, myNewDistance))
            return;

        float nv = CCVecMath.dot(_myNormal, theParticle.velocity());
//        float t = -myOldDistance / nv; // Time steps before hit

        // A hit! A most palpable hit!
        // Compute tangential and normal components of velocity
        // Normal Vn = (V.N)N
        CCVector3f myNormalComponent = new CCVector3f(
        	_myNormal.x * nv,
        	_myNormal.y * nv,
        	_myNormal.z * nv
        );
        // Tangent Vt = V - Vn
        CCVector3f myTangentialComponent = CCVecMath.subtract(theParticle.velocity(), myNormalComponent);

        // Compute new velocity heading out:
        // Don't apply friction if tangential velocity < cutoff
        if(myTangentialComponent.lengthSquared() <= theCutOffSquared)
        	theParticle.velocity().set(
        		myTangentialComponent.x - myNormalComponent.x * theResilence,
        		myTangentialComponent.y - myNormalComponent.y * theResilence,
        		myTangentialComponent.z - myNormalComponent.z * theResilence
        	);
        else
        	theParticle.velocity().set(
            	myTangentialComponent.x * theOneMinusFriction - myNormalComponent.x * theResilence,
            	myTangentialComponent.y * theOneMinusFriction - myNormalComponent.y * theResilence,
            	myTangentialComponent.z * theOneMinusFriction - myNormalComponent.z * theResilence
            );
	}

	public void draw(CCGraphics g){
		CCVector3f p1 = _myNormal.perp();
		CCVector3f p2 = _myNormal.cross(p1);
		
		p1.normalize(1000);
		p2.normalize(1000);
		
		g.beginShape(CCDrawMode.QUADS);
		g.normal(_myNormal);
		g.vertex(_myPoint.x + p1.x + p2.x, _myPoint.y + p1.y + p2.y, _myPoint.z + p1.z + p2.z);
		g.vertex(_myPoint.x - p1.x + p2.x, _myPoint.y - p1.y + p2.y, _myPoint.z - p1.z + p2.z);
		g.vertex(_myPoint.x - p1.x - p2.x, _myPoint.y - p1.y - p2.y, _myPoint.z - p1.z - p2.z);
		g.vertex(_myPoint.x + p1.x - p2.x, _myPoint.y + p1.y - p2.y, _myPoint.z + p1.z - p2.z);
		g.endShape();
	}
}
