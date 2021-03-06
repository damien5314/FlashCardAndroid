package com.ddiehl.android.flashcard.quizsession;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Phrase implements Parcelable {
	private static final String TAG = Phrase.class.getSimpleName();
	private String phraseNative, phrasePhonetic, phraseRomanized, phraseTranslated;
	private ArrayList<Sentence> phraseSentences;
	private boolean isIncludedInSession;
	private boolean hasNativeText;
	
	public Phrase() {
		setPhraseNative("");
		setPhrasePhonetic("");
		setPhraseRomanized("");
		setPhraseTranslated("");
		setPhraseSentences(new ArrayList<Sentence>());
		hasNativeText(!nativeIsEmpty());
	}
	
	public Phrase(String p_kanji, String p_kana, String p_romaji, String p_english, 
			ArrayList<Sentence> p_sentences, boolean isIncluded) {
		setPhraseNative(p_kanji);
		setPhrasePhonetic(p_kana);
		setPhraseRomanized(p_romaji);
		setPhraseTranslated(p_english);
		setPhraseSentences(p_sentences);
		setIncludedInSession(isIncluded);
		hasNativeText(!nativeIsEmpty());
	}
	
	public Phrase(Parcel in) {
		setPhraseNative(in.readString());
		setPhrasePhonetic(in.readString());
		setPhraseRomanized(in.readString());
		setPhraseTranslated(in.readString());
		setPhraseSentences(in.readArrayList(Sentence.class.getClassLoader()));
		setIncludedInSession(in.readByte() != 0);
		hasNativeText(in.readByte() != 0);
	}

	public String getPhraseNative() {
		return phraseNative;
	}
	
	public String setPhraseNative(String k) {
		phraseNative = k;
		hasNativeText(!nativeIsEmpty());
		return phraseNative;
	}
	
	public String getPhrasePhonetic() {
		return phrasePhonetic;
	}
	
	public String setPhrasePhonetic(String k) {
		phrasePhonetic = k;
		return phrasePhonetic;
	}
	
	public String getPhraseRomanized() {
		return phraseRomanized;
	}
	
	public String setPhraseRomanized(String k) {
		phraseRomanized = k;
		return phraseRomanized;
	}
	
	public String getPhraseTranslated() {
		return phraseTranslated;
	}
	
	public String setPhraseTranslated(String k) {
		phraseTranslated = k;
		return phraseTranslated;
	}
	
	public ArrayList<Sentence> addSentence(String s_kanji, String s_kana, String s_romaji, String s_english) {
		Sentence s = new Sentence();
		s.setSentenceNative(s_kanji);
		s.setSentencePhonetic(s_kana);
		s.setSentenceRomanized(s_romaji);
		s.setSentenceTranslated(s_english);
		phraseSentences.add(s);
		return phraseSentences;
	}
	
	public ArrayList<Sentence> setPhraseSentences(ArrayList<Sentence> s) {
		phraseSentences = s;
		return phraseSentences;
	}
	
	public ArrayList<Sentence> getPhraseSentences() {
		return phraseSentences;
	}

	public boolean isIncludedInSession() {
		return isIncludedInSession;
	}

	public void setIncludedInSession(boolean isIncludedInSession) {
		this.isIncludedInSession = isIncludedInSession;
	}

	public boolean hasNativeText() {
		return hasNativeText;
	}

	public void hasNativeText(boolean hasNativeText) {
		this.hasNativeText = hasNativeText;
	}
	
	private boolean nativeIsEmpty() {
		return ( getPhraseNative().equals(null) || getPhraseNative().equals("") );
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeString(getPhraseNative());
		arg0.writeString(getPhrasePhonetic());
		arg0.writeString(getPhraseRomanized());
		arg0.writeString(getPhraseTranslated());
		arg0.writeList(phraseSentences);
		arg0.writeByte((byte) (isIncludedInSession() ? 1 : 0));
		arg0.writeByte((byte) (hasNativeText() ? 1 : 0));
	}

    public static final Parcelable.Creator<Phrase> CREATOR
            = new Parcelable.Creator<Phrase>() {
        public Phrase createFromParcel(Parcel in) {
            return new Phrase(in);
        }

        public Phrase[] newArray(int size) {
            return new Phrase[size];
        }
    };

}
