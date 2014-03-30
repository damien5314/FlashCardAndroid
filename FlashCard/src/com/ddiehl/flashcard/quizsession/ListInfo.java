package com.ddiehl.flashcard.quizsession;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;

public class ListInfo {
	private static final String TAG = "ListInfo";
	InputStream list;
    private String title = null;
    private OnClickListener listener;
    
	public ListInfo(InputStream vocabulary) {
		super();
		list = vocabulary;
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
		
		setListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Edit button clicked: " + getTitle());
			}
		});
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

	public OnClickListener getListener() {
		return listener;
	}

	public void setListener(OnClickListener listener) {
		this.listener = listener;
	}
}