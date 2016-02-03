package cc.creativecomputing.exco.domainmodels;

import java.util.Map;

import cc.creativecomputing.exco.CCExcoNode;
import cc.creativecomputing.exco.CCExcoRelationType;
import cc.creativecomputing.exco.service.CCExcoWebService;

import com.google.gson.JsonObject;

public class CCExcoEvent extends CCExcoNode{
	
	private CCExcoWebService _myWebService;

	public CCExcoEvent(JsonObject theObjectJson, CCExcoWebService theWebService) {
		super(theObjectJson);
		_myWebService = theWebService;
	}
	
	public Map<String,String> description(){
		return property("description").stringMap();
	}
	
	public Map<String,String> director(){
		return property("director").stringMap();
	}
	
	public Map<String,String> name(){
		return property("name").stringMap();
	}
	
	public Map<String,String> shortName(){
		return property("short_name").stringMap();
	}
	
	public Map<String,String> synopsis(){
		return property("synopsis").stringMap();
	}
	
	public String id(){
		return property("id").stringValue();
	}
	
	public CCExcoInterval date(){
		return property("date").interval();
	}

	public CCExcoCategory belongsToCategory(){
		return new CCExcoCategory(_myWebService.relatedNode(CCExcoRelationType.BELONGS_TO_CATEGORY, this).json());
	}
}
