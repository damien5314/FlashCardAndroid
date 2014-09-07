package com.ddiehl.flashcard.quizsession;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class QuizSession implements Parcelable {
	private static final String TAG = QuizSession.class.getSimpleName();
	private Date sessionStartTime, sessionEndTime; // Represents the start and end times (UTC) for the quiz session
	private float sessionActualStudyTime; // To be updated after each quiz with amount of time taken
	
	public QuizSession() {
		Date date = new Date();
		setSessionStartTime(date);
		setSessionEndTime(date);
		setSessionActualStudyTime(0);
	}
	
	public QuizSession(Parcel in) {
		// TODO Verify this actually reads and writes the same object
		setSessionStartTime(new Date(in.readLong()));
		setSessionEndTime(new Date(in.readLong()));
		setSessionActualStudyTime(in.readFloat());
	}

	public Date getSessionStartTime() {
		return sessionStartTime;
	}

	public void setSessionStartTime(Date sessionStartTime) {
		this.sessionStartTime = sessionStartTime;
	}

	public Date getSessionEndTime() {
		return sessionEndTime;
	}

	public void setSessionEndTime(Date sessionEndTime) {
		this.sessionEndTime = sessionEndTime;
	}

	public float getSessionActualStudyTime() {
		return sessionActualStudyTime;
	}

	public void setSessionActualStudyTime(float sessionActualStudyTime) {
		this.sessionActualStudyTime = sessionActualStudyTime;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel in, int flags) {
		in.writeLong(sessionStartTime.getTime());
		in.writeLong(sessionEndTime.getTime());
		in.writeFloat(sessionActualStudyTime);
	}

    public static final Parcelable.Creator<QuizSession> CREATOR
            = new Parcelable.Creator<QuizSession>() {
        public QuizSession createFromParcel(Parcel in) {
            return new QuizSession(in);
        }

        public QuizSession[] newArray(int size) {
            return new QuizSession[size];
        }
    };

}
