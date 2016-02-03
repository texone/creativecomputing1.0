package cc.creativecomputing.exco;

import cc.creativecomputing.util.logging.CCLog;

import com.google.gson.JsonObject;

/**
 * {"url":"/edm/relations/is_modified_by/307","id":307,"start_node_id":15,"end_node_id":524,"type":"is_modified_by","last_modified":"2014-04-08T11:00:34Z"}
 * @author christianr
 *
 */
public class CCExcoRelation extends CCExcoObject{
	
	private final int _myStartNodeID;
	private final int _myEndNodeID;
	
	public CCExcoRelation(JsonObject theObjectJson){
		super(theObjectJson);
		_myStartNodeID = theObjectJson.get("start_node_id").getAsInt();
		_myEndNodeID = theObjectJson.get("end_node_id").getAsInt();
	}
	
	public int startNodeID(){
		return _myStartNodeID;
	}
	
	public int endNodeID(){
		return _myEndNodeID;
	}
}
