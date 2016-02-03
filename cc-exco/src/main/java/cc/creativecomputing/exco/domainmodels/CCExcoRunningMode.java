package cc.creativecomputing.exco.domainmodels;

import cc.creativecomputing.exco.CCExcoNode;
import cc.creativecomputing.exco.CCExcoProperty;
import cc.creativecomputing.exco.CCExcoRelationType;
import cc.creativecomputing.exco.service.CCExcoWebService;

import com.google.gson.JsonObject;

public class CCExcoRunningMode extends CCExcoNode{
	
	private CCExcoWebService _myConnection;

	public CCExcoRunningMode(JsonObject theObjectJson, CCExcoWebService theConnection) {
		super(theObjectJson);
		_myConnection = theConnection;
	}
	
	public CCExcoNode isActiveWithConfiguration(){
		return _myConnection.relatedNode(CCExcoRelationType.IS_ACTIVE_WITH_CONFIGURATION, this);
	}
	
	public CCExcoOperationalMode operationalMode() {
		CCExcoNode myOperationalModeNode = _myConnection.relatedNode(CCExcoRelationType.IS_CURRENTLY_IN_OPERATIONAL_MODE, this);
		
		CCExcoProperty myProperty = myOperationalModeNode.property("id");
		
		if(myProperty == null)return CCExcoOperationalMode.NONE;
		
		return CCExcoOperationalMode.fromID(myProperty.stringValue());
	}
	
	public String name(){
		return property("name").stringValue();
	}
	
	public String id(){
		return property("id").stringValue();
	}
	
	public int orderKey(){
		return property("order_key").intValue();
	}

}
