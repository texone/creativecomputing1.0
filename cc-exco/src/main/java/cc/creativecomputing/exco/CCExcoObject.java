package cc.creativecomputing.exco;

import com.google.gson.JsonObject;

public class CCExcoObject {
	
	private int _myObjectID;
	private String _myUrl;
	private String _myType;
	
	private JsonObject _myJsonObject;

	public CCExcoObject(JsonObject theObjectJson){
		_myJsonObject = theObjectJson;
		_myUrl = theObjectJson.get("url").getAsString();
		_myObjectID = theObjectJson.get("id").getAsInt();
		_myType = theObjectJson.get("type").getAsString();
	}
	
	public int objectID(){
		return _myObjectID;
	}
	
	public String type(){
		return _myType;
	}
	
	public String url(){
		return _myUrl;
	}
	
	public JsonObject json(){
		return _myJsonObject;
	}
	
	public String toString(){
		return _myJsonObject.toString(); 
	}
}
