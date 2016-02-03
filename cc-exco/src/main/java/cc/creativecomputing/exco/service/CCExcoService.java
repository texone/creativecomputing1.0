package cc.creativecomputing.exco.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import cc.creativecomputing.util.logging.CCLog;

public class CCExcoService {
	
	protected boolean Debug = false;
	private final JsonParser _myParser;
	protected final String _myServiceUrl;
	
	public CCExcoService(String theDataUrl){
		_myServiceUrl = theDataUrl;
		_myParser = new JsonParser();
	}

	/**
	 * send http request
	 * 
	 * @param theURL
	 * @param theMethod
	 */
	public String request(String theURL, String theMethod) {

		if(Debug)CCLog.info("send " + theMethod + ": " + _myServiceUrl + theURL);
		StringBuffer myResult = new StringBuffer();
		try {
			URL url = new URL(_myServiceUrl + theURL);
			HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod(theMethod);


			try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpCon.getInputStream(), "UTF-8"))){
				for (String line; (line = reader.readLine()) != null;) {
					myResult.append(line);
				}
			} catch (Exception e) {
				CCLog.error(e);
			}

		} catch (Exception e) {
			CCLog.error(e);
			return null;
		}
		if(Debug)CCLog.info("returned " + myResult);
		return myResult.toString();
	}
	
	protected JsonElement parse(String theString) {
		return _myParser.parse(theString);
	}
	
	/**
	 * Get node data from neo4j by RESTAPI call.
	 * @param theURL
	 */
	public JsonElement jsonRequest(String thePath) {
		return parse(request (thePath, "GET"));
	}
	
	
}
