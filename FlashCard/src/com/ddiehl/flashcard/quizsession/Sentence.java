package com.ddiehl.flashcard.quizsession;

import android.os.Parcel;
import android.os.Parcelable;

public class Sentence implements Parcelable {

	private String sentenceKanji, sentenceKana, sentenceRomaji, sentenceEnglish;
	
	public Sentence() {
		
	}
	
	public Sentence(String s_kanji, String s_kana, String s_romaji, String s_english) {
		setSentenceKanji(s_kanji);
		setSentenceKana(s_kana);
		setSentenceRomaji(s_romaji);
		setSentenceEnglish(s_english);
	}

	public Sentence(Parcel in) {
		// TODO Auto-generated constructor stub
		setSentenceKanji(in.readString());
		setSentenceKana(in.readString());
		setSentenceRomaji(in.readString());
		setSentenceEnglish(in.readString());
	}

	public String getSentenceKanji() {
		return sentenceKanji;
	}

	public void setSentenceKanji(String sentenceKanji) {
		this.sentenceKanji = sentenceKanji;
	}

	public String getSentenceKana() {
		return sentenceKana;
	}

	public void setSentenceKana(String sentenceKana) {
		this.sentenceKana = sentenceKana;
	}

	public String getSentenceRomaji() {
		return sentenceRomaji;
	}

	public void setSentenceRomaji(String sentenceRomaji) {
		this.sentenceRomaji = sentenceRomaji;
	}

	public String getSentenceEnglish() {
		return sentenceEnglish;
	}

	public void setSentenceEnglish(String sentenceEnglish) {
		this.sentenceEnglish = sentenceEnglish;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(getSentenceKanji());
		dest.writeString(getSentenceKana());
		dest.writeString(getSentenceRomaji());
		dest.writeString(getSentenceEnglish());
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
