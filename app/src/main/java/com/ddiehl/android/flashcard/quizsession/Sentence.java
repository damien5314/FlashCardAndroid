package com.ddiehl.android.flashcard.quizsession;

import android.os.Parcel;
import android.os.Parcelable;

public class Sentence implements Parcelable {
	private static final String TAG = Sentence.class.getSimpleName();
	private String sentenceNative, sentencePhonetic, sentenceRomanized, sentenceTranslated;
	
	public Sentence() {
		setSentenceNative("");
		setSentencePhonetic("");
		setSentenceRomanized("");
		setSentenceTranslated("");
	}
	
	public Sentence(String s_kanji, String s_kana, String s_romaji, String s_english) {
		setSentenceNative(s_kanji);
		setSentencePhonetic(s_kana);
		setSentenceRomanized(s_romaji);
		setSentenceTranslated(s_english);
	}

	public Sentence(Parcel in) {
		setSentenceNative(in.readString());
		setSentencePhonetic(in.readString());
		setSentenceRomanized(in.readString());
		setSentenceTranslated(in.readString());
	}

	public String getSentenceNative() {
		return sentenceNative;
	}

	public void setSentenceNative(String sentenceKanji) {
		this.sentenceNative = sentenceKanji;
	}

	public String getSentencePhonetic() {
		return sentencePhonetic;
	}

	public void setSentencePhonetic(String sentenceKana) {
		this.sentencePhonetic = sentenceKana;
	}

	public String getSentenceRomanized() {
		return sentenceRomanized;
	}

	public void setSentenceRomanized(String sentenceRomaji) {
		this.sentenceRomanized = sentenceRomaji;
	}

	public String getSentenceTranslated() {
		return sentenceTranslated;
	}

	public void setSentenceTranslated(String sentenceEnglish) {
		this.sentenceTranslated = sentenceEnglish;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getSentenceNative());
		dest.writeString(getSentencePhonetic());
		dest.writeString(getSentenceRomanized());
		dest.writeString(getSentenceTranslated());
	}

    public static final Parcelable.Creator<Sentence> CREATOR
            = new Parcelable.Creator<Sentence>() {
        public Sentence createFromParcel(Parcel in) {
            return new Sentence(in);
        }

        public Sentence[] newArray(int size) {
            return new Sentence[size];
        }
    };
}
