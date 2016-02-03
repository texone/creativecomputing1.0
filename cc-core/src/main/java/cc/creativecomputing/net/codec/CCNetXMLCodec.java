package cc.creativecomputing.net.codec;

import java.nio.ByteBuffer;

import cc.creativecomputing.xml.CCXMLElement;
import cc.creativecomputing.xml.CCXMLIO;

public class CCNetXMLCodec implements CCNetPacketCodec<CCXMLElement>{

	@Override
	public CCXMLElement decode(ByteBuffer theBuffer) {
		byte[] myArray = new byte[theBuffer.limit()];
		theBuffer.get(myArray);
		String myXMLString = new String(myArray);
		return CCXMLIO.parse(myXMLString);
	}

	@Override
	public void encode(CCXMLElement theMessage, ByteBuffer theBuffer) {
		String myXMLString = theMessage.toString();
		theBuffer.put(myXMLString.getBytes());
	}

}
