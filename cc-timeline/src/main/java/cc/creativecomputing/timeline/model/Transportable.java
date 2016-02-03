package cc.creativecomputing.timeline.model;

public interface Transportable {
	
	public void time(double theTime);

	public void onChangeLoop(TimeRange theRange, boolean theLoopIsActive);
}
