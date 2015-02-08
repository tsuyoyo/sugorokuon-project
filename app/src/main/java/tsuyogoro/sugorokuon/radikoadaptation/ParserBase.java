/**
 * Copyright (c) 
 * 2012 Tsuyoyo. All Rights Reserved.
 */
package tsuyogoro.sugorokuon.radikoadaptation;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

class ParserBase {
	
	private XmlPullParser mParser = null;
	
	protected ParserBase(InputStream source, String inputEncoding) {
		try {
			mParser = Xml.newPullParser();
			mParser.setInput(source, inputEncoding);			
		} catch(XmlPullParserException e) {
			Log.e("SugoRokuon", e.getMessage());
		}		
	}
	
	protected XmlPullParser getParser() {
		return mParser;
	}
	
	protected String getText() throws XmlPullParserException, IOException {
		String res = "";
		for(int e = mParser.getEventType();	
				XmlPullParser.END_TAG != e; e = mParser.next()) {
			if(XmlPullParser.TEXT == e) {
				res += mParser.getText();
			}
		}
		return res;
	}
}
