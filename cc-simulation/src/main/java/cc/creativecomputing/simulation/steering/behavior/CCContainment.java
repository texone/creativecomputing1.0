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
package cc.creativecomputing.simulation.steering.behavior;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.CCParticle;
import cc.creativecomputing.simulation.steering.CCObstacle;


public class CCContainment extends CCNeighborHoodBehavior{
	
	private class Mode{
		protected final List<CCVector3f> _myProbes = new ArrayList<CCVector3f>();
		
		protected boolean apply(final CCParticle theAgent, final CCVector3f theForce){
			return false;
		}
	}
	
	private class Feeler extends Mode{
		
		private CCVector3f _myFrontProbe = new CCVector3f();
		private CCVector3f _myLeftProbe = new CCVector3f();
		private CCVector3f _myRighProble = new CCVector3f();
		private CCVector3f _myTopProbe = new CCVector3f();
		private CCVector3f _myBottomProble = new CCVector3f();
		
		Feeler(){
			_myProbes.add(_myFrontProbe);
			_myProbes.add(_myLeftProbe);
			_myProbes.add(_myRighProble);
			_myProbes.add(_myTopProbe);
			_myProbes.add(_myBottomProble);
		}
		
		private boolean applyProbe(final CCParticle theAgent, final CCObstacle theObstacle, final CCVector3f theProbe, final CCVector3f theForce){
			_myLastIntersectionPoint = new CCVector3f();
			_myLastIntersectionNormal = new CCVector3f();
			boolean inside = theObstacle.domain().intersectsLine(theAgent.position, theProbe, _myLastIntersectionPoint, _myLastIntersectionNormal);
			
			if (inside) {
				_myLastIsInside = true;
				float along = theAgent.forward.dot(_myLastIntersectionNormal);
				theForce.set(theAgent.forward);
				theForce.scale(-along);
				theForce.add(_myLastIntersectionNormal);
				return true;
			}
			return false;
		}
		
		@Override
		protected boolean apply(final CCParticle theAgent, final CCVector3f theForce){
			float speed = theAgent.velocity().length();
			float ahead1 = speed * _myPrediction;
			float ahead2 = speed * _myPrediction * 0.3F;
			ahead2 = CCMath.sqrt(2*CCMath.sq(ahead2));
			
			_myFrontProbe.set(0.0F, 0.0F, ahead1);
			_myFrontProbe.set(theAgent.globalizePosition(_myFrontProbe));
			_myLeftProbe.set(0.0F, ahead2, ahead2);
			_myLeftProbe.set(theAgent.globalizePosition(_myLeftProbe));
			_myRighProble.set(0.0F, -ahead2, ahead2);
			_myRighProble.set(theAgent.globalizePosition(_myRighProble));
			
			_myTopProbe.set(-ahead2, 0.0F, ahead2);
			_myTopProbe.set(theAgent.globalizePosition(_myTopProbe));
			_myBottomProble.set(ahead2,0.0F, ahead2);
			_myBottomProble.set(theAgent.globalizePosition(_myBottomProble));
			
			for(CCObstacle myObstacle:_myNeighborhood.getNearObstacles(theAgent, _myPrediction)){
				if(applyProbe(theAgent, myObstacle, _myFrontProbe, theForce))return true;
				if(applyProbe(theAgent, myObstacle, _myLeftProbe, theForce))return true;
				if(applyProbe(theAgent, myObstacle, _myRighProble, theForce))return true;
			}
			
			return false;
		}
	}
	
	private class Random extends Mode{
		private CCVector3f _myProbePoint = new CCVector3f();
		
		Random(){
			_myProbes.add(_myProbePoint);
		}
		
		@Override
		protected boolean apply(final CCParticle theAgent, final CCVector3f theForce){
			final float speed = theAgent.velocity().length();
			final float ahead1 = speed * _myPrediction;
			float ahead2 = speed * _myPrediction * 0.3F;
			ahead2 = CCMath.sqrt(2*CCMath.sq(ahead2));
			
			final float offset = ahead2;
			final float factor = ahead1 * 0.8F - offset;
			
			_myProbePoint.randomize();
			_myProbePoint.z = CCMath.abs(_myProbePoint.z);
			//_myProbePoint.x(0.0F);
			_myProbePoint.normalize();
			_myProbePoint.z *= factor;
			_myProbePoint.z += offset;
			_myProbePoint.scale(1.75F);
			
			_myProbePoint.set(theAgent.globalizePosition(_myProbePoint));
			
			for(CCObstacle myObstacle:_myNeighborhood.getNearObstacles(theAgent, _myPrediction)){
				_myLastIntersectionPoint = new CCVector3f();
				_myLastIntersectionNormal = new CCVector3f();
				boolean inside = myObstacle.domain().intersectsLine(theAgent.position, _myProbePoint, _myLastIntersectionPoint, _myLastIntersectionNormal);
				if (inside) {
					_myLastIsInside = true;
					float along = theAgent.forward.dot(_myLastIntersectionNormal);
					theForce.set(theAgent.forward);
					theForce.scale(-along);
					theForce.add(_myLastIntersectionNormal);
					return true;
				}
			}
			return false;
		}
	}
	
	private enum ProbeMode{
		RANDOM, FEELER;
	}
	
	public static final ProbeMode RANDOM = ProbeMode.RANDOM;
	public static final ProbeMode FEELER = ProbeMode.FEELER;

	private float _myPrediction = 20;
	
	private Mode _myMode;
	
	private boolean _myLastIsInside = false;
	private CCVector3f _myLastIntersectionPoint = new CCVector3f();
	private CCVector3f _myLastIntersectionNormal = new CCVector3f();
	
	public CCContainment(final float thePrediction, final ProbeMode theProbeMode){
		_myPrediction = thePrediction;
		
		switch (theProbeMode) {
		case RANDOM:
			_myMode = new Random();
			break;
		default:
			_myMode = new Feeler();
			break;
		}
	}
	
	public CCContainment(){
		this(20,FEELER);
	}
	
	@Override
	public boolean apply(CCParticle theAgent, CCVector3f theForce, float theDeltaTime) {
		_myLastIsInside = false;
		boolean myResult = _myMode.apply(theAgent, theForce);
		return myResult;
	}

	public Mode mode(){
		return _myMode;
	}
	
	public List<CCVector3f> probes(){
		return _myMode._myProbes;
	}
	
	public boolean isInside(){
		return _myLastIsInside;
	}
	
	public CCVector3f intersectionPoint(){
		return _myLastIntersectionPoint;
	}
	
	public CCVector3f intersectionNormal(){
		return _myLastIntersectionNormal;
	}
}
