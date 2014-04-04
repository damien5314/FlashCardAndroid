package com.ddiehl.flashcard.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.adapters.EditPhraseSentenceAdapter;
import com.ddiehl.flashcard.quizsession.Phrase;
import com.ddiehl.flashcard.quizsession.Sentence;

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
		
		ArrayList<Sentence> sentences = phrase.getPhraseSentences();
		if (sentences != null && !sentences.isEmpty()) {
			EditPhraseSentenceAdapter adapter = new EditPhraseSentenceAdapter(this, R.layout.activity_edit_phrase_sentence, sentences);
			ListView vLists = (ListView) findViewById(R.id.edit_phrase_sentences_list);
			vLists.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// Open EditSentence activity
				}
			});
			vLists.setAdapter(adapter);
		}
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
