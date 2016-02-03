package cc.creativecomputing.exco;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class CCExcoNode extends CCExcoObject{
	
	private Map<String, CCExcoProperty> _myPropertyMap = new HashMap<>();
	
	
	public CCExcoNode(JsonObject theObjectJson){
		super(theObjectJson);
		
		if(!theObjectJson.has("properties"))return;
		
		JsonArray myPropertiesArray = theObjectJson.get("properties").getAsJsonArray();
		for(int i = 0; i < myPropertiesArray.size();i++){
			JsonObject myPropertyJson = myPropertiesArray.get(i).getAsJsonObject();
			CCExcoProperty myProperty = new CCExcoProperty(myPropertyJson);
			_myPropertyMap.put(myProperty.name(), myProperty);
		}
	}
	
	public CCExcoProperty property(String theProperty){
		return _myPropertyMap.get(theProperty);
	}
	
	public String toString(){
		StringWriter myWriter = new StringWriter();
		myWriter.append("Exco Node\n");
		myWriter.append(" type:     " + type()+"\n");
		myWriter.append(" id:       " + objectID()+"\n");
		myWriter.append(" url:      " + url()+"\n");
		for(CCExcoProperty myProperty:_myPropertyMap.values()){
			myWriter.append(" prop." + myProperty.name() +":  value:" + myProperty.value() + ":  type:" + myProperty.type() + "\n");
		}
	
		return myWriter.toString();
	}
}
