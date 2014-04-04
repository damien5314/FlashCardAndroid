package com.ddiehl.flashcard.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.quizsession.Phrase;

public class EditPhraseActivity extends Activity {
	private static final String TAG = "EditPhraseActivity";
	private Phrase phrase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_phrase);
		Bundle extras = this.getIntent().getExtras();
		if (extras.containsKey("Phrase")) {
			phrase = extras.getParcelable("Phrase");
			populatePhraseContents();
		} else {
			Log.e(TAG, "No Phrase detected in extras.");
		}
	}
	
	private void populatePhraseContents() {
		EditText phraseNative, phrasePhonetic, phraseRomanized, phraseTranslated;
		phraseNative = (EditText) findViewById(R.id.edit_phrase_native_value);
		phraseNative.setText(String.valueOf(phrase.getPhraseNative()));
		phrasePhonetic = (EditText) findViewById(R.id.edit_phrase_phonetic_value);
		phrasePhonetic.setText(String.valueOf(phrase.getPhrasePhonetic()));
		phraseRomanized = (EditText) findViewById(R.id.edit_phrase_romanized_value);
		phraseRomanized.setText(String.valueOf(phrase.getPhraseRomanized()));
		phraseTranslated = (EditText) findViewById(R.id.edit_phrase_translated_value);
		phraseTranslated.setText(String.valueOf(phrase.getPhraseTranslated()));
	}
	
	public void savePhrase(View v) {
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_phrase, menu);
		return true;
	}

}
