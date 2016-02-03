package cc.creativecomputing.timeline.model.communication;


public interface TimelineListener {
	
	public void onCurveEvent(CurveEvent theEvent);

	public void onTimedEvent(TimedEvent theEvent);
}
