package com.ddiehl.flashcard.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.quizsession.Sentence;

public class EditSentenceActivity extends Activity {
	private static final String TAG = "EditSentenceActivity";
	private Sentence sentence;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_sentence);
		Bundle extras = this.getIntent().getExtras();
		if (extras.containsKey("Sentence")) {
			sentence = extras.getParcelable("Sentence");
			populateContents();
		} else {
			Log.e(TAG, "No Sentence included in extras.");
		}
	}
	
	private void populateContents() {
		EditText sentenceNative, sentencePhonetic, sentenceRomanized, sentenceTranslated;
		sentenceNative = (EditText) findViewById(R.id.edit_sentence_native_value);
		sentenceNative.setText(String.valueOf(sentence.getSentenceNative()));
		sentencePhonetic = (EditText) findViewById(R.id.edit_sentence_phonetic_value);
		sentencePhonetic.setText(String.valueOf(sentence.getSentencePhonetic()));
		sentenceRomanized = (EditText) findViewById(R.id.edit_sentence_romanized_value);
		sentenceRomanized.setText(String.valueOf(sentence.getSentenceRomanized()));
		sentenceTranslated = (EditText) findViewById(R.id.edit_sentence_translated_value);
		sentenceTranslated.setText(String.valueOf(sentence.getSentenceTranslated()));
	}
	
	public void save(View v) {
		EditText sentenceNative, sentencePhonetic, sentenceRomanized, sentenceTranslated;
		sentenceNative = (EditText) findViewById(R.id.edit_sentence_native_value);
		sentence.setSentenceNative(sentenceNative.getText().toString());
		sentencePhonetic = (EditText) findViewById(R.id.edit_sentence_phonetic_value);
		sentence.setSentencePhonetic(sentencePhonetic.getText().toString());
		sentenceRomanized = (EditText) findViewById(R.id.edit_sentence_romanized_value);
		sentence.setSentenceRomanized(sentenceRomanized.getText().toString());
		sentenceTranslated = (EditText) findViewById(R.id.edit_sentence_translated_value);
		sentence.setSentenceTranslated(sentenceTranslated.getText().toString());
		
		Intent returnIntent = new Intent();
		returnIntent.putExtra("Sentence", sentence);
		setResult(1, returnIntent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
