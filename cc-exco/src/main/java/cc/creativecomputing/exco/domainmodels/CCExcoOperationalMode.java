package cc.creativecomputing.exco.domainmodels;

import cc.creativecomputing.exco.CCExcoException;

public enum CCExcoOperationalMode {
	EMERGENCY("emergency"),
	MAINTENANCE("maintenance"),
	OFF("off"),
	RUNNING("running"),
	NONE("none");
	
	private String _myID;
	
	private CCExcoOperationalMode(String theID){
		_myID = theID;
	}
	
	public String id(){
		return _myID;
	}
	
	public static CCExcoOperationalMode fromID(String theID){
		switch(theID){
		case "emergency": return EMERGENCY;
		case "maintenance": return MAINTENANCE;
		case "off": return OFF;
		case "running": return RUNNING;
		case "none": return NONE;
		}
		throw new CCExcoException("Invalid Operational Mode ID:" + theID);
	}
}
