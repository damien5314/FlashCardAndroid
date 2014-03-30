package com.ddiehl.flashcard.quizsession;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class ListInfo {
	private static final String TAG = "ListInfo";
    private String title = null;
    
	public ListInfo(InputStream vocabulary) {
		super();
		XmlPullParser parser = Xml.newPullParser();
        try {
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	        parser.setInput(vocabulary, null);
		} catch (Exception e) {
			Log.e(TAG, "Error initializing XmlPullParser");
			//e.printStackTrace();
		}
        
		try {
			parseXML(parser);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseXML(XmlPullParser parser) throws XmlPullParserException, IOException
	{
        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = null;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("title")) {
                    	setTitle(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            eventType = parser.next();
        }
	}
	
	public void setTitle(String in) {
		title = in;
	}
	
	public String getTitle() {
		return title;
	}
}