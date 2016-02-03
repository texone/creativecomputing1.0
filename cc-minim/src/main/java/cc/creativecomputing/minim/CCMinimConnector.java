package cc.creativecomputing.minim;

import java.io.InputStream;

import cc.creativecomputing.io.CCIOUtil;

public class CCMinimConnector {

	public InputStream createInput(String theFileName){
		return CCIOUtil.createInputStream(theFileName);
	}
	
	public String sketchPath(String thePath){
		return CCIOUtil.dataPath(thePath);
	}
}
