package cc.creativecomputing.exco;


public enum CCExcoNodeType {
	EXHIBIT("exhibit"),
	EVENT("event"),
	EVENT_LIST("event_list"),
	LOCATION("location"),
	OPERATIONAL_MODE("operational_mode"),
	RUNNING_MODE("running_mode"),
	RUNTIME_STATE("runtime_states"),
	UNKNOWN("unknown_node_type");
	
	private String _myID;
	
	private CCExcoNodeType(String theID){
		_myID = theID;
	}
	
	public String id(){
		return _myID;
	}
	
	public static CCExcoNodeType fromID(String theID){
		switch(theID){
		case "exhibit": return EXHIBIT;
		case "event" : return EVENT;
		case "event_list" : return EVENT_LIST;
		case "location" : return LOCATION;
		case "operational_mode": return OPERATIONAL_MODE;
		}
		throw new CCExcoException("Invalid Node Type ID:" + theID);
	}
}
