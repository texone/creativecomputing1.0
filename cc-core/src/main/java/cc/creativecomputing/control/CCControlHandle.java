package cc.creativecomputing.control;

public class CCControlHandle {
	
	public static interface CCControlHandleListener{
		public void setControlHandle(CCControlHandle theHandle);
	}

	private String _myTabName;
	
	private String _myObjectID;
	
	private int _myColumn;
	
	public CCControlHandle(String theTabName, String theObjectID, int theColumn){
		_myTabName = theTabName;
		_myObjectID = theObjectID;
		_myColumn = theColumn;
	}
	
	public int column(){
		return _myColumn;
	}
	
	public String tabname(){
		return _myTabName;
	}
	
	public String objectID(){
		return _myObjectID;
	}
}
