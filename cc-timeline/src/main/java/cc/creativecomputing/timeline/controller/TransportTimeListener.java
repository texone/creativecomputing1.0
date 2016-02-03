package cc.creativecomputing.timeline.controller;

import cc.creativecomputing.timeline.model.TimeRange;

public interface TransportTimeListener {
	
	public void time(double theTime);

	public void onChangeLoop(TimeRange theRange, boolean theLoopIsActive);
}
