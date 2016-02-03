package cc.creativecomputing.timeline.controller;

public interface TransportStateListener {

	public void play(double theTime);
	
	public void stop(double theTime);
}
