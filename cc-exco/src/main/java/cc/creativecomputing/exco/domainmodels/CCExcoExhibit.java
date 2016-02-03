package cc.creativecomputing.exco.domainmodels;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.exco.CCExcoNode;
import cc.creativecomputing.exco.CCExcoProperty;
import cc.creativecomputing.exco.CCExcoRelationType;
import cc.creativecomputing.exco.service.CCExcoWebService;

import com.google.gson.JsonObject;

public class CCExcoExhibit extends CCExcoNode{
	
	private CCExcoWebService _myConnection;

	public CCExcoExhibit(JsonObject theObjectJson, CCExcoWebService theConnection) {
		super(theObjectJson);
		_myConnection = theConnection;
	}
	
	public CCExcoNode isModifiedBy(String theBucketName){
		return _myConnection.relatedNode(CCExcoRelationType.IS_MODIFIED_BY, theBucketName, this);
	}
	
	public CCExcoOperationalMode isCurrentlyInOperationalMode() {
		CCExcoNode myOperationalModeNode = _myConnection.relatedNode(CCExcoRelationType.IS_CURRENTLY_IN_OPERATIONAL_MODE, this);
		
		CCExcoProperty myProperty = myOperationalModeNode.property("id");
		
		if(myProperty == null)return CCExcoOperationalMode.NONE;
		
		return CCExcoOperationalMode.fromID(myProperty.stringValue());
	}
	
	public CCExcoRunningMode isCurrentlyInRunningMode() {
		CCExcoNode myResult = _myConnection.relatedNode(CCExcoRelationType.IS_CURRENTLY_IN_RUNNING_MODE, this);
		if(myResult == null)return null;
		return new CCExcoRunningMode(myResult.json(), _myConnection);
	}
	
	public List<CCExcoRunningMode> hasPossibleRunningMode() {
		List<CCExcoNode> myRunningModeNodes = _myConnection.relatedNodes(CCExcoRelationType.HAS_POSSIBLE_RUNNING_MODE, this);
		if(myRunningModeNodes == null)return null;
		
		List<CCExcoRunningMode> myResult = new ArrayList<>();
		for(CCExcoNode myExcoNode:myRunningModeNodes){
			myResult.add(new CCExcoRunningMode(myExcoNode.json(), _myConnection));
		}
		return myResult;
	}
	
	public String name(){
		return property("name").stringValue();
	}
	
	public String id(){
		return property("id").stringValue();
	}

}
