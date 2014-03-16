package com.ddiehl.flashcard;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class PhraseCollection extends ArrayList<Phrase> {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "PhraseCollection";
	private String title;
	private int phrasesTotal, phrasesStarted, phrasesMastered;
	
	public PhraseCollection() {
		super();	
	}
	
	public PhraseCollection(InputStream vocabulary) {
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
        Phrase currentPhrase = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = null;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("information")) {
                    	
                    } else if (name.equalsIgnoreCase("title")) {
                    	setTitle(parser.nextText());
                    } else if (name.equalsIgnoreCase("phrasestotal")) {
                    	setPhrasesTotal(Integer.parseInt(parser.nextText()));
                    } else if (name.equalsIgnoreCase("phrasesstarted")) {
                    	setPhrasesStarted(Integer.parseInt(parser.nextText()));
                    } else if (name.equalsIgnoreCase("phrasesmastered")) {
                    	setPhrasesMastered(Integer.parseInt(parser.nextText()));
                    } else if (name.equalsIgnoreCase("phrases")) {
                    	
                    } else if (name.equalsIgnoreCase("phrase")) {  
                    	currentPhrase = new Phrase();
                    } else if (currentPhrase != null) {
                        if (name.equalsIgnoreCase("p_native")) {
                            currentPhrase.setPhraseNative(parser.nextText());
                        } else if (name.equalsIgnoreCase("p_phonetic")) {
                        	currentPhrase.setPhrasePhonetic(parser.nextText());
                        } else if (name.equalsIgnoreCase("p_romanized")) {
                        	currentPhrase.setPhraseRomanized(parser.nextText());
                        } else if (name.equalsIgnoreCase("p_translated")) {
                        	currentPhrase.setPhraseTranslated(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("phrase") && currentPhrase != null) {
                    	this.add(currentPhrase);
                    } 
            }
            eventType = parser.next();
        }

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getPhrasesTotal() {
		return phrasesTotal;
	}

	public void setPhrasesTotal(int phrasesTotal) {
		this.phrasesTotal = phrasesTotal;
	}

	public int getPhrasesStarted() {
		return phrasesStarted;
	}

	public void setPhrasesStarted(int phrasesStarted) {
		this.phrasesStarted = phrasesStarted;
	}

	public int getPhrasesMastered() {
		return phrasesMastered;
	}

	public void setPhrasesMastered(int phrasesMastered) {
		this.phrasesMastered = phrasesMastered;
	}

}
