package cc.creativecomputing.exco.service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.events.CCListenerManager;
import cc.creativecomputing.exco.CCExcoNode;
import cc.creativecomputing.exco.CCExcoRelationType;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class CCExcoNotificationService extends CCExcoService implements AutoCloseable{
	
	public interface CCExcoNotificationListener {
		public void onNotification ();
	}
	
	private class RequestHandler implements HttpHandler {
		private String _myEvent;
		public RequestHandler (String theController) {
			super();
			_myEvent = theController;
		}
		public void handle(HttpExchange t) throws IOException {
			_myListenerMap.get(_myEvent).proxy().onNotification();
		}
	}

	
	
	private String _myObserverString;
	private HttpServer _myServer;
	
	private List<String> _myRegistrationPaths = new ArrayList<>();
	private Map<String, CCListenerManager<CCExcoNotificationListener>> _myListenerMap = new HashMap<String, CCListenerManager<CCExcoNotificationListener>>();
	
	public CCExcoNotificationService(
		String theNotificationUrl, 
		String theNotificationListenerUrl,
		int theNotificationListenerPort, 
		String theServerUrl,
		int theServerPort
	){
		super(theNotificationUrl);

		_myObserverString = "?observer=http://"+theNotificationListenerUrl+":"+theNotificationListenerPort;
		
		try {
			_myServer = HttpServer.create (new InetSocketAddress(theServerUrl,theServerPort), 0);
			_myServer.setExecutor (null);
			_myServer.createContext("/test", new HttpHandler() {
				
				@Override
		        public void handle(HttpExchange t) throws IOException {
		            String response = "This is the response";
		            t.sendResponseHeaders(200, response.length());
		            OutputStream os = t.getResponseBody();
		            os.write(response.getBytes());
		            os.close();
		        }
			});
			_myServer.start();
		}catch (Exception e) {
		}
	}
	
	public CCExcoNotificationService(
		String theNotificationUrl, 
		String theNotificationListenerUrl,
		int theNotificationListenerPort
	){
		this(theNotificationUrl, theNotificationListenerUrl, theNotificationListenerPort, theNotificationListenerUrl, theNotificationListenerPort);
	}
	
	public void addNotificationListener(String theController, CCExcoNotificationListener theListener) {
		if(!_myListenerMap.containsKey(theController)){
			_myListenerMap.put(theController, CCListenerManager.create(CCExcoNotificationListener.class));
		}
		_myListenerMap.get(theController).add(theListener);
	}
	
	public void removeNotificationListener(String theController, CCExcoNotificationListener theListener){
		if(!_myListenerMap.containsKey(theController))return;
		_myListenerMap.get(theController).remove(theListener);
	}
	
	public void registerEvent (String thePath, String theEvent, CCExcoNotificationListener theListener) {
		if(!theEvent.startsWith("/"))theEvent = "/" + theEvent;
		request ("/notification/registration"+thePath+_myObserverString+theEvent, "POST");
		_myRegistrationPaths.add(thePath);	
		addNotificationListener(theEvent, theListener);
		if(_myServer != null)_myServer.createContext (theEvent, new RequestHandler(theEvent));
	}
	
	public void unregisterEvent (String thePath) {
		if(!_myRegistrationPaths.remove(thePath))return;
		request ("/notification/registration"+thePath+_myObserverString, "DELETE");		
	}
	
	public void registerCreateRelation(CCExcoNode theNode, CCExcoRelationType theRelation, CCExcoNotificationListener theListener){
		registerEvent ("/created/relationship/start_node/" + theNode.objectID() + "/type/" + theRelation.id(), "/" + theNode.objectID() + "_" + theRelation.id(), theListener);
	}

	public void clearRegistrations() {
		for (String myRegistrationPath:new ArrayList<>(_myRegistrationPaths)) {
			unregisterEvent(myRegistrationPath);
		}
	}
	 
	public void update(final float theDeltaTime){
		
	}
	
	public void close() {
		clearRegistrations();
		if(_myServer != null)_myServer.stop(0);
	}
}
