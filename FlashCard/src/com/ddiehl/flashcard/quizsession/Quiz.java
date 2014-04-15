package com.ddiehl.flashcard.quizsession;

import android.os.Parcel;
import android.os.Parcelable;

public class Quiz implements Parcelable {
	private static final String TAG = Quiz.class.getSimpleName();
	private int quizType;
	private Phrase quizPhrase;
	private boolean isCorrectlyAnswered;
	private int actualScore;
	private int potentialScore;
	
	public Quiz() {
		
	}
	
	public Quiz(int type, Phrase p) {
		setQuizType(type);
		setQuizPhrase(p);
		setCorrectlyAnswered(false);
		setActualScore(0);
		setPotentialScore(0);
	}
	
	public Quiz(Parcel in) {
		quizType = in.readInt();
		isCorrectlyAnswered = (in.readByte() != 0);
		quizPhrase = in.readParcelable(Phrase.class.getClassLoader());
		actualScore = in.readInt();
		potentialScore = in.readInt();
	}

	public int getQuizType() {
		return quizType;
	}

	public void setQuizType(int quizType) {
		this.quizType = quizType;
	}

	public Phrase getQuizPhrase() {
		return quizPhrase;
	}

	public void setQuizPhrase(Phrase quizPhrase) {
		this.quizPhrase = quizPhrase;
	}
	
	public boolean isCorrectlyAnswered() {
		return isCorrectlyAnswered;
	}
	
	public void setCorrectlyAnswered(boolean foo) {
		isCorrectlyAnswered = foo;
	}

	public int getActualScore() {
		return actualScore;
	}

	public void setActualScore(int actualScore) {
		this.actualScore = actualScore;
	}

	public int getPotentialScore() {
		return potentialScore;
	}

	public void setPotentialScore(int potentialScore) {
		this.potentialScore = potentialScore;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel in, int flags) {
		in.writeInt(quizType);
		in.writeByte((byte) (isCorrectlyAnswered() ? 1 : 0));
		in.writeParcelable(quizPhrase, flags);
		in.writeInt(actualScore);
		in.writeInt(potentialScore);
	}

    public static final Parcelable.Creator<Quiz> CREATOR
            = new Parcelable.Creator<Quiz>() {
        public Quiz createFromParcel(Parcel in) {
            return new Quiz(in);
        }

        public Quiz[] newArray(int size) {
            return new Quiz[size];
        }
    };

}
