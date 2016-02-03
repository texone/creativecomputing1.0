package cc.creativecomputing.exco;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cc.creativecomputing.exco.domainmodels.CCExcoInterval;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/*
 * properties":[{
 * 		"url":"/edm/exhibits/15/properties/id",
 * 		"name":"id",
 * 		"type":"string",
 * 		"value":"BOX_02"
 * 		},
 */
public class CCExcoProperty {
	
	private String _myUrl;
	private String _myName;
	private String _myType;
	private JsonElement _myValue;
	
	private JsonObject _myJson;

	public CCExcoProperty(JsonObject theObjectJson) {
		_myJson = theObjectJson;
		_myUrl = theObjectJson.get("url").getAsString();
		_myName = theObjectJson.get("name").getAsString();
		_myType = theObjectJson.get("type").getAsString();
		_myValue = theObjectJson.get("value");
	}
	
	public void value(JsonElement theValue){
		_myJson.remove("value");
		_myJson.add("value", theValue);
	}
	
	public void value(String theValue){
		_myJson.remove("value");
		_myJson.addProperty("value", theValue);
	}
	
	public void value(int theValue){
		_myJson.remove("value");
		_myJson.addProperty("value", theValue);
	}
	
	public String url(){
		return _myUrl;
	}
	
	public String name(){
		return _myName;
	}
	
	public String type(){
		return _myType;
	}
	
	public JsonElement value(){
		return _myValue;
	}
	
	public String stringValue(){
		return _myValue.getAsString();
	}
	
	public float floatValue(){
		return _myValue.getAsFloat();
	}
	
	public int intValue(){
		return _myValue.getAsInt();
	}
	
	public int intValue(int theDefault){
		try{
			return intValue();
		}catch(Exception e){
			return theDefault;
		}
	}
	
	public CCExcoInterval interval(){
		String[] myDates = _myValue.getAsString().split("/");

		return new CCExcoInterval(
			LocalDate.parse(myDates[0], DateTimeFormatter.ISO_DATE_TIME), 
			LocalDate.parse(myDates[1], DateTimeFormatter.ISO_DATE_TIME)
		);
	}
	
	public String[] stringArray(){
		JsonArray myArray = _myValue.getAsJsonArray();
		String[] myResult = new String[myArray.size()];
		for(int i = 0; i < myResult.length;i++){
			myResult[i] = myArray.get(i).getAsString();
		}
		return myResult;
	}
	
	public Map<String, String> stringMap(){
		JsonObject myObject = _myValue.getAsJsonObject();
		Map<String, String> myResult = new HashMap<String, String>();
		for(Entry<String, JsonElement> myEntry:myObject.entrySet()){
			myResult.put(myEntry.getKey(), myEntry.getValue().getAsString());
		}
		return myResult;
	}
	
	public JsonObject json(){
		return _myJson;
	}
	
	@Override
	public String toString() {
		return getClass().getName() + "[ url:" + _myUrl + " name:" + _myName + " type:" + _myType + " value:" + _myValue + "]";
	}
}
