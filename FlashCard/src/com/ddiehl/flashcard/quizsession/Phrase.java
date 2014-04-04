package com.ddiehl.flashcard.quizsession;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Phrase implements Parcelable {
	
	private String phraseNative, phrasePhonetic, phraseRomanized, phraseTranslated;
	private ArrayList<Sentence> sentences;
	private boolean isIncludedInSession;
	
	public Phrase() {
		
	}
	
	public Phrase(String p_kanji, String p_kana, String p_romaji, String p_english, ArrayList<Sentence> p_sentences, boolean isIncluded) {
		setPhraseNative(p_kanji);
		setPhrasePhonetic(p_kana);
		setPhraseRomanized(p_romaji);
		setPhraseTranslated(p_english);
		setPhraseSentences(p_sentences);
		setIncludedInSession(isIncluded);
	}
	
	public Phrase(Parcel in) {
		setPhraseNative(in.readString());
		setPhrasePhonetic(in.readString());
		setPhraseRomanized(in.readString());
		setPhraseTranslated(in.readString());
		setPhraseSentences(in.readArrayList(Sentence.class.getClassLoader()));
		setIncludedInSession(in.readByte() != 0);
	}

	public String getPhraseNative() {
		return phraseNative;
	}
	
	public String setPhraseNative(String k) {
		phraseNative = k;
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
		s.setSentenceKanji(s_kanji);
		s.setSentenceKana(s_kana);
		s.setSentenceRomaji(s_romaji);
		s.setSentenceEnglish(s_english);
		sentences.add(s);
		return sentences;
	}
	
	public ArrayList<Sentence> setPhraseSentences(ArrayList<Sentence> s) {
		sentences = s;
		return sentences;
	}
	
	public ArrayList<Sentence> getPhraseSentences() {
		return sentences;
	}

	public boolean isIncludedInSession() {
		return isIncludedInSession;
	}

	public void setIncludedInSession(boolean isIncludedInSession) {
		this.isIncludedInSession = isIncludedInSession;
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
		arg0.writeList(sentences);
		arg0.writeByte((byte) (isIncludedInSession() ? 1 : 0));
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
