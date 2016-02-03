package cc.creativecomputing.timeline.model.points;

import cc.creativecomputing.timeline.model.TrackData;

public class StepControlPoint extends ControlPoint{

	public StepControlPoint() {
		super(ControlPointType.STEP);
	}

	public StepControlPoint(double theTime, double theValue) {
		super(theTime, theValue, ControlPointType.STEP);
	}
	
	public double interpolateValue(double theTime, TrackData theData) {
		ControlPoint myPrevious = getPrevious();
		
		if(myPrevious != null) {
			return myPrevious.value();
		}
		
		return super.value();
	}
	
	public ControlPoint clone() {
		ControlPoint myCopy = new StepControlPoint(time(), value());
		return myCopy;
	}
}
