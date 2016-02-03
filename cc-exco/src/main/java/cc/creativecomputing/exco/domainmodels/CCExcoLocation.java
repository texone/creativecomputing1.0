package cc.creativecomputing.exco.domainmodels;

import java.util.Map;

import cc.creativecomputing.exco.CCExcoNode;
import cc.creativecomputing.exco.service.CCExcoWebService;

import com.google.gson.JsonObject;

public class CCExcoLocation extends CCExcoNode{
	
	private CCExcoWebService _myConnection;

	public CCExcoLocation(JsonObject theObjectJson, CCExcoWebService theConnection) {
		super(theObjectJson);
		_myConnection = theConnection;
	}
	
	public Map<String,String> description(){
		return property("description").stringMap();
	}
	
	public Map<String,String> name(){
		return property("name").stringMap();
	}
	
	public String id(){
		return property("id").stringValue();
	}

}
