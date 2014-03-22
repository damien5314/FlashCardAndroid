package com.ddiehl.flashcard;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Xml;

public class PhraseCollection implements Parcelable {
	private static final long serialVersionUID = 1L;
	private static final String TAG = "PhraseCollection";
	private List<Phrase> list = new ArrayList<Phrase>();
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
	
	public PhraseCollection(Parcel in) {
		in.readTypedList(list, Phrase.CREATOR);
		this.setTitle(in.readString());
		this.setPhrasesTotal(in.readInt());
		this.setPhrasesStarted(in.readInt());
		this.setPhrasesMastered(in.readInt());
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
                    	currentPhrase.setIncludedInSession(true);
                    	this.add(currentPhrase);
                    } 
            }
            eventType = parser.next();
        }

	}
	
	public List<Phrase> add(Phrase q) {
		list.add(q);
		return list;
	}
	
	public Phrase get(int index) {
		return list.get(index);
	}
	
	public Phrase remove(int index) {
		return list.remove(index);
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public PhraseCollection clone() {
		PhraseCollection clone = new PhraseCollection();
		clone.setTitle(getTitle());
		clone.getList().addAll(getList());
		return clone;
	}
	
	public int size() {
		return list.size();
	}
	
	public List<Phrase> getList() {
		return list;
	}
	
	public void setList(List<Phrase> in) {
		this.list = in;
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeTypedList(list);
		arg0.writeString(getTitle());
		arg0.writeInt(getPhrasesTotal());
		arg0.writeInt(getPhrasesStarted());
		arg0.writeInt(getPhrasesMastered());
	}
	
	public static final Parcelable.Creator<PhraseCollection> CREATOR = new Parcelable.Creator<PhraseCollection>() {
		public PhraseCollection createFromParcel(Parcel in) {
		    return new PhraseCollection(in);
		}

		public PhraseCollection[] newArray(int size) {
		    return new PhraseCollection[size];
		}
	};

	public Iterator<Phrase> iterator() {
		return list.iterator();
	}
}