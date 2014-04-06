package com.ddiehl.flashcard.quizsession;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Xml;
import android.view.View.OnClickListener;

public class PhraseCollection implements Parcelable {
	private static final String TAG = "PhraseCollection";
	private List<Phrase> list = new ArrayList<Phrase>();
	private String title;
	private int phrasesTotal, phrasesStarted, phrasesMastered;
    private OnClickListener editListener;
	
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
        ArrayList<Sentence> phraseSentences = null;
        Sentence currentSentence = null;

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
                        } else if (name.equalsIgnoreCase("sentences")) {
                        	phraseSentences = new ArrayList<Sentence>();
                        } else if (name.equalsIgnoreCase("sentence")) {
                        	currentSentence = new Sentence();
                        } else if (name.equalsIgnoreCase("s_native")) {
                        	currentSentence.setSentenceNative(parser.nextText());
                        } else if (name.equalsIgnoreCase("s_phonetic")) {
                        	currentSentence.setSentencePhonetic(parser.nextText());                        	
                        } else if (name.equalsIgnoreCase("s_romanized")) {
                        	currentSentence.setSentenceRomanized(parser.nextText());
                        } else if (name.equalsIgnoreCase("s_translated")) {
                        	currentSentence.setSentenceTranslated(parser.nextText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("phrase") && currentPhrase != null) {
                    	currentPhrase.setIncludedInSession(true);
                    	this.add(currentPhrase);
                    } else if (name.equalsIgnoreCase("sentence") && currentSentence != null) {
                    	phraseSentences.add(currentSentence);
                    } else if (name.equalsIgnoreCase("sentences") && phraseSentences != null) {
                    	currentPhrase.setPhraseSentences(phraseSentences);
                    }
            }
            eventType = parser.next();
        }

	}
	
	public void save(Context ctx) {
		try {
			// Write PhraseCollection to XML
	        String filename = "vocabulary-saved.xml";
//	        File file = new File(ctx.getFilesDir(), filename); // Why can't I pass this below?
	        FileOutputStream myFile = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
	        XmlSerializer xmlSerializer = Xml.newSerializer();
	        StringWriter writer = new StringWriter();
	        xmlSerializer.setOutput(writer);
	        
	        String ns = ""; // namespace
	        xmlSerializer.startDocument("UTF-8",true);
	        xmlSerializer.startTag(ns, "vocabulary");
	        xmlSerializer.startTag(ns, "information");
	        xmlSerializer.startTag(ns, "Title");
	        xmlSerializer.text(this.getTitle());
	        xmlSerializer.endTag(ns, "Title");
	        xmlSerializer.startTag(ns, "PhrasesTotal");
	        xmlSerializer.text(String.valueOf(this.getPhrasesTotal()));
	        xmlSerializer.endTag(ns, "PhrasesTotal");
	        xmlSerializer.startTag(ns, "PhrasesStarted");
	        xmlSerializer.text(String.valueOf(this.getPhrasesStarted()));
	        xmlSerializer.endTag(ns, "PhrasesStarted");
	        xmlSerializer.startTag(ns, "PhrasesMastered");
	        xmlSerializer.text(String.valueOf(this.getPhrasesMastered()));
	        xmlSerializer.endTag(ns, "PhrasesMastered");
	        xmlSerializer.endTag(ns, "information");
	        xmlSerializer.startTag(ns, "phrases");
	        for (int i = 0; i < list.size(); i++) {
	        	Phrase p = list.get(i);
	        	ArrayList<Sentence> sentences = p.getPhraseSentences();
		        xmlSerializer.startTag(ns, "phrase");
		        xmlSerializer.attribute(ns, "ID", String.valueOf(i+1));
		        xmlSerializer.startTag(ns, "p_native");
		        xmlSerializer.text(p.getPhraseNative());
		        xmlSerializer.endTag(ns, "p_native");
		        xmlSerializer.startTag(ns, "p_phonetic");
		        xmlSerializer.text(p.getPhrasePhonetic());
		        xmlSerializer.endTag(ns, "p_phonetic");
		        xmlSerializer.startTag(ns, "p_romanized");
		        xmlSerializer.text(p.getPhraseRomanized());
		        xmlSerializer.endTag(ns, "p_romanized");
		        xmlSerializer.startTag(ns, "p_translated");
		        xmlSerializer.text(p.getPhraseTranslated());
		        xmlSerializer.endTag(ns, "p_translated");
		        xmlSerializer.startTag(ns, "sentences");
		        for (int j = 0; j < sentences.size(); j++) {
		        	Sentence s = sentences.get(j);
			        xmlSerializer.startTag(ns, "sentence");
			        xmlSerializer.attribute(ns, "ID", String.valueOf(j+1));
			        xmlSerializer.startTag(ns, "s_native");
			        xmlSerializer.text(s.getSentenceNative());
			        xmlSerializer.endTag(ns, "s_native");
			        xmlSerializer.startTag(ns, "s_phonetic");
			        xmlSerializer.text(s.getSentencePhonetic());
			        xmlSerializer.endTag(ns, "s_phonetic");
			        xmlSerializer.startTag(ns, "s_romanized");
			        xmlSerializer.text(s.getSentenceRomanized());
			        xmlSerializer.endTag(ns, "s_romanized");
			        xmlSerializer.startTag(ns, "s_translated");
			        xmlSerializer.text(s.getSentenceTranslated());
			        xmlSerializer.endTag(ns, "s_translated");
			        xmlSerializer.endTag(ns, "sentence");
		        }
		        xmlSerializer.endTag(ns, "sentences");
		        xmlSerializer.endTag(ns, "phrase");
	        }
	        xmlSerializer.endTag(ns, "phrases");
	        xmlSerializer.endTag(ns, "vocabulary");
	        xmlSerializer.endDocument();
	        
	        String output = writer.toString();
	        myFile.write(output.getBytes());
	        myFile.close();
		} catch (FileNotFoundException e) {
		    Log.e(TAG, "FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
		    Log.e(TAG, "Caught IOException: " + e.getMessage());
		}
	}

	public OnClickListener getEditListener() {
		return editListener;
	}

	public void setEditListener(OnClickListener listener) {
		this.editListener = listener;
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
	
	public Phrase set(int index, Phrase p) {
		return list.set(index, p);
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