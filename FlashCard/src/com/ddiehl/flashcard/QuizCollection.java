package com.ddiehl.flashcard;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;

public class QuizCollection extends ArrayList<Quiz> {
	private static final String TAG = "QuizCollection";
	private static final long serialVersionUID = 1L;
	
	public QuizCollection() {
		super();		
	}
	
	public QuizCollection(PhraseCollection pc) {
		super();
		
		int quizTypes = 1; // Increase as more quiz types are developed
		Iterator<Phrase> i = pc.iterator();
		while (i.hasNext()) {
			Phrase p = i.next();
			for (int type = 1; type <= quizTypes; type++) {
				Quiz q = new Quiz(type, p);
				this.add(q);
			}
		}
	}

}
