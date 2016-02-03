package cc.creativecomputing.exco.domainmodels;

import java.util.Map;

import cc.creativecomputing.exco.CCExcoNode;

import com.google.gson.JsonObject;

public class CCExcoCategory extends CCExcoNode{

	public CCExcoCategory(JsonObject theObjectJson) {
		super(theObjectJson);
	}
	
	public Map<String,String> name(){
		return property("name").stringMap();
	}
	
	public String id(){
		return property("id").stringValue();
	}


}
