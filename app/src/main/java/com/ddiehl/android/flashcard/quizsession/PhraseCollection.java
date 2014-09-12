package com.ddiehl.android.flashcard.quizsession;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.util.Xml;

import com.ddiehl.android.flashcard.activities.EditListActivity;
import com.ddiehl.android.flashcard.util.GooglePlayConnectedActivity;
import com.ddiehl.android.flashcard.util.Utils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PhraseCollection implements Parcelable {
	private static final String TAG = PhraseCollection.class.getSimpleName();
	private List<Phrase> mPhraseList = new ArrayList<Phrase>();
    private DriveId mDriveId;
	private String mListTitle;
	private int phrasesTotal, phrasesStarted, phrasesMastered;
//    private OnClickListener editListener;
	
	public PhraseCollection() { }

	public PhraseCollection(InputStream vocabulary) {
		buildCollectionFromInputStream(vocabulary);
	}

    public PhraseCollection(DriveId id) {
        setDriveId(id);
    }

    public boolean loadCollectionDataFromDrive(GooglePlayConnectedActivity c) {
        DriveFile driveFile = Drive.DriveApi.getFile(c.getGoogleApiClient(), this.getDriveId());
        // Retrieve Contents from DriveFile
        DriveApi.ContentsResult result = driveFile.openContents(c.getGoogleApiClient(), DriveFile.MODE_READ_ONLY,
                new DriveFile.DownloadProgressListener() {
            @Override
            public void onProgress(long arg0, long arg1) {
                // Report download progress here
            }
        }).await();
        if (result.getStatus().isSuccess()) {
            // Return new PhraseCollection created from Contents
            buildCollectionFromInputStream(result.getContents().getInputStream());
            return true;
        }
        return false;
    }

	public void buildCollectionFromInputStream(InputStream vocabulary) {
		XmlPullParser parser = Xml.newPullParser();
        try {
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	        parser.setInput(vocabulary, null);
		} catch (Exception e) {
			Log.e(TAG, "Error initializing XmlPullParser" + e.getMessage());
		}
        
		try {
            int eventType = parser.getEventType();
            Phrase currentPhrase = null;
            ArrayList<Sentence> phraseSentences = null;
            Sentence currentSentence = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
				String name;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("information")) {

                        } else if (name.equalsIgnoreCase("title")) {
                            setListTitle(parser.nextText());
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
                            } else if (name.equalsIgnoreCase("s_native") && currentSentence != null) {
                                currentSentence.setSentenceNative(parser.nextText());
                            } else if (name.equalsIgnoreCase("s_phonetic") && currentSentence != null) {
                                currentSentence.setSentencePhonetic(parser.nextText());
                            } else if (name.equalsIgnoreCase("s_romanized") && currentSentence != null) {
                                currentSentence.setSentenceRomanized(parser.nextText());
                            } else if (name.equalsIgnoreCase("s_translated") && currentSentence != null) {
                                currentSentence.setSentenceTranslated(parser.nextText());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase("phrase") && currentPhrase != null) {
                            currentPhrase.setIncludedInSession(true);
                            this.add(currentPhrase);
                        } else if (name.equalsIgnoreCase("sentence") && currentSentence != null && phraseSentences != null) {
                            phraseSentences.add(currentSentence);
                        } else if (name.equalsIgnoreCase("sentences") && phraseSentences != null) {
                            currentPhrase.setPhraseSentences(phraseSentences);
                        }
                }
                eventType = parser.next();
            }
		} catch (Exception e) {
			Log.e(TAG, "Error while deserializing vocabulary XML: " + e.getMessage());
		}
	}

	private String serializeToXml() {
        StringWriter writer = new StringWriter();
        XmlSerializer xmlSerializer = Xml.newSerializer();
		try {
	        xmlSerializer.setOutput(writer);
	        
	        String ns = ""; // Namespace
	        xmlSerializer.startDocument("UTF-8",true);
	        xmlSerializer.startTag(ns, "vocabulary");
	        xmlSerializer.startTag(ns, "information");
	        xmlSerializer.startTag(ns, "Title");
	        xmlSerializer.text(this.getListTitle());
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
	        for (int i = 0; i < mPhraseList.size(); i++) {
	        	Phrase p = mPhraseList.get(i);
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
		} catch (Exception e) {
			Log.e(TAG, "Error while serializing vocabulary XML: " + e.getMessage());
            e.printStackTrace();
		}
        return writer.toString();
	}

	public boolean writeChangesToDrive(EditListActivity c) {
        String listXml = this.serializeToXml(); // Serialize PhraseCollection to XML
		final GoogleApiClient client = c.getGoogleApiClient();
		if (!client.isConnected()) {
			Utils.showToast(c, "Error: Play services not yet connected.");
		} else {
			// Retrieve DriveFile with the DriveId
			DriveFile driveFile = Drive.DriveApi.getFile(c.getGoogleApiClient(), mDriveId);

			// Create MetadataChangeSet to update title of DriveFile
			MetadataChangeSet cs = new MetadataChangeSet.Builder().setTitle(this.getListTitle()).build();

			// Submit MetadataChangeSet and check result
			if (driveFile.updateMetadata(client, cs).await().getStatus().isSuccess())
				Log.d(TAG, "Updated file metadata successfully.");
			else Log.e(TAG, "Error updating file metadata.");

			// Open DriveFile contents
			DriveApi.ContentsResult result = driveFile.openContents(client, DriveFile.MODE_WRITE_ONLY,
					new DriveFile.DownloadProgressListener() {
				@Override
				public void onProgress(long arg0, long arg1) {

				}
			}).await();

            if (!result.getStatus().isSuccess()) {
                Log.e(TAG, "Error while opening file contents: " + result.getStatus().getStatusMessage());
            } else {
                // Write changes to OutputStream generated from DriveFile contents
                try {
                    OutputStream f_out = result.getContents().getOutputStream();
                    f_out.write(listXml.getBytes());
                    f_out.close();
                    // Call commitAndCloseContents to write changes to DriveFile
                    if (driveFile.commitAndCloseContents(client, result.getContents()).await().getStatus().isSuccess())
                        return true;
                } catch (IOException e) {
                    Log.e(TAG, "Error writing contents to DriveFile: " + e.getMessage());
                }
            }
		}
        return false;
}

	// TODO Refactor deletion function for DriveFiles
	public boolean delete() {
        return false;
	}
	
	public List<Phrase> add(Phrase q) {
		mPhraseList.add(q);
		return mPhraseList;
	}
	
	public Phrase get(int index) {
		return mPhraseList.get(index);
	}
	
	public Phrase remove(int index) {
		return mPhraseList.remove(index);
	}
	
	public boolean isEmpty() {
		return mPhraseList.isEmpty();
	}
	
	public PhraseCollection clone() throws CloneNotSupportedException {
		super.clone();
		PhraseCollection clone = new PhraseCollection();
		clone.setListTitle(getListTitle());
		clone.getPhraseList().addAll(getPhraseList());
		return clone;
	}
	
	public int size() {
		return mPhraseList.size();
	}
	
	public List<Phrase> getPhraseList() {
		return mPhraseList;
	}
	
	public void setPhraseList(List<Phrase> in) {
		this.mPhraseList = in;
	}

    public DriveId getDriveId() {
        return mDriveId;
    }

    public void setDriveId(DriveId mDriveId) {
        this.mDriveId = mDriveId;
    }

	public String getListTitle() {
		return mListTitle;
	}

	public void setListTitle(String title) {
		this.mListTitle = title;
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
		return mPhraseList.set(index, p);
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	public PhraseCollection(Parcel in) {
		in.readTypedList(mPhraseList, Phrase.CREATOR);
		this.setListTitle(in.readString());
		this.setPhrasesTotal(in.readInt());
		this.setPhrasesStarted(in.readInt());
		this.setPhrasesMastered(in.readInt());
        this.setDriveId(DriveId.decodeFromString(in.readString()));
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeTypedList(mPhraseList);
		arg0.writeString(getListTitle());
		arg0.writeInt(getPhrasesTotal());
		arg0.writeInt(getPhrasesStarted());
		arg0.writeInt(getPhrasesMastered());
        arg0.writeString(getDriveId().encodeToString());
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
		return mPhraseList.iterator();
	}
}
