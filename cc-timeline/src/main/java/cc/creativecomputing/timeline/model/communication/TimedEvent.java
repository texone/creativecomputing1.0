package cc.creativecomputing.timeline.model.communication;

import cc.creativecomputing.timeline.model.TrackType;
import cc.creativecomputing.timeline.model.points.TimedEventPoint;

public class TimedEvent extends TimelineEvent{
	
	private TimedEventPoint _myTimedEventPoint;

	public TimedEvent(TimedEventPoint thePoint, double theTime, String theAddress, TrackType theTrackType) {
		super(theTime, theAddress, theTrackType);
		_myTimedEventPoint = thePoint;
	}
	
	public TimedEventPoint point() {
		return _myTimedEventPoint;
	}

}
