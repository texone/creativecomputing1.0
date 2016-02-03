package cc.creativecomputing.exco;


public enum CCExcoSearchKeyType {
	NAME("name"),
	ID("id");
	
	private String _myID;
	
	private CCExcoSearchKeyType(String theID){
		_myID = theID;
	}
	
	public String id(){
		return _myID;
	}
	
	public static CCExcoSearchKeyType fromID(String theID){
		switch(theID){
		case "name": return NAME;
		}
		throw new CCExcoException("Invalid Search Key Type ID:" + theID);
	}
}
