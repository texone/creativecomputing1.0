package cc.creativecomputing.timeline.model.communication;

import java.util.Collection;

import cc.creativecomputing.timeline.view.TimelineContainer;


abstract public class TimelineConnector implements TimelineListener {
	
	protected TimelineContainer _myTimelineContainer;
	
	public TimelineConnector(TimelineContainer theTimelineContainer) {
		_myTimelineContainer = theTimelineContainer;
		_myTimelineContainer.addTimelineListener(this);
	}
	
	public static class CommunicationLayerException extends RuntimeException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public CommunicationLayerException() {
			super();
		}

		public CommunicationLayerException(String message, Throwable cause) {
			super(message, cause);
		}

		public CommunicationLayerException(String message) {
			super(message);
		}

		public CommunicationLayerException(Throwable cause) {
			super(cause);
		}
	}
	
	@Override
	public void onCurveEvent(CurveEvent myEvent) {
		double myValue = myEvent.value();
		
		switch(myEvent.type()) {
		case DOUBLE:
			sendDoubleValue(myEvent.address(), myValue);
			break;
		case INTEGER:
			sendIntegerValue(myEvent.address(), (int)myValue);
			break;
		case BOOLEAN:
			sendBooleanValue(myEvent.address(), myValue >= 0.5);
			break;
		case TIME:
		case GROUP:
			sendTimeValue(myEvent.address(), (int)myValue, myEvent.time());
			break;
		default:
			throw new CommunicationLayerException("Unknown value type: " + myEvent.type());
		}
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.timeline.model.communication.TimelineListener#onTimedEvent()
	 */
	@Override
	public void onTimedEvent(TimedEvent theEvent) {
	}
	
	abstract public Collection<String> getObjectNames();
	
	public abstract double currentValue(String theAddress) ;
	
	public void sendDoubleValue( String theAddress, double theValue ) {};
	public void sendIntegerValue( String theAddress, int theValue ) {};
	public void sendBooleanValue( String theAddress, boolean theValue ) {};
	public void sendStringValue( String theAddress, String theValue ) {};
	public void sendTimeValue( String theAddress, int theValue, double theTime) {};
	
}
