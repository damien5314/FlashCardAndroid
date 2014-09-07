package com.ddiehl.flashcard.quizsession;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class QuizCollection implements Parcelable {
	private static final String TAG = QuizCollection.class.getSimpleName();
	private List<Quiz> list = new ArrayList<Quiz>();
	private String title;
	
	public QuizCollection() {
		super();
		list = new ArrayList<Quiz>();
		setTitle("NoTitle");
	}
	
	public QuizCollection(PhraseCollection pc) {
		super();
		list = new ArrayList<Quiz>();
		
		int quizTypes = 1; // Increase as more quiz types are developed
		Iterator<Phrase> i = pc.iterator();
		while (i.hasNext()) {
			Phrase p = i.next();
			if (p.isIncludedInSession())
				for (int type = 1; type <= quizTypes; type++) {
					Quiz q = new Quiz(type, p);
					this.add(q);
				}
		}
		setTitle(pc.getTitle());
	}

	public QuizCollection(Parcel in) {
		in.readTypedList(list, Quiz.CREATOR);
		setTitle(in.readString());
	}
	
	public List<Quiz> add(Quiz q) {
		list.add(q);
		return list;
	}
	
	public Quiz get(int index) {
		return list.get(index);
	}
	
	public Quiz remove(int index) {
		return list.remove(index);
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public QuizCollection clone() {
		QuizCollection clone = new QuizCollection();
		clone.setTitle(getTitle());
		clone.getList().addAll(getList());
		return clone;
	}
	
	public int size() {
		return list.size();
	}
	
	public List<Quiz> getList() {
		return list;
	}
	
	public void setList(List<Quiz> in) {
		this.list = in;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(list);
		dest.writeString(title);
	}

    public static final Parcelable.Creator<QuizCollection> CREATOR
            = new Parcelable.Creator<QuizCollection>() {
        public QuizCollection createFromParcel(Parcel in) {
            return new QuizCollection(in);
        }

        public QuizCollection[] newArray(int size) {
            return new QuizCollection[size];
        }
    };

}
