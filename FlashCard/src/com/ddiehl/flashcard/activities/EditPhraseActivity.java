package com.ddiehl.flashcard.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.adapters.EditPhraseSentenceAdapter;
import com.ddiehl.flashcard.dialogs.DiscardChangedPhraseDialog;
import com.ddiehl.flashcard.quizsession.Phrase;
import com.ddiehl.flashcard.quizsession.Sentence;

public class EditPhraseActivity extends Activity {
	private static final String TAG = EditPhraseActivity.class.getSimpleName();
	private Phrase phrase;
	private ArrayList<Sentence> sentences;
	private int mPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_phrase);
		Bundle extras = this.getIntent().getExtras();
		if (extras.containsKey("Phrase")) {
			phrase = extras.getParcelable("Phrase");
			sentences = phrase.getPhraseSentences();
			mPosition = extras.getInt("position");
			populateContents();
		} else {
			Log.e(TAG, "No Phrase detected in extras.");
		}
	}
	
	private void populateContents() {
		EditText phraseNative, phrasePhonetic, phraseRomanized, phraseTranslated;
		phraseNative = (EditText) findViewById(R.id.edit_phrase_native_value);
		phraseNative.setText(String.valueOf(phrase.getPhraseNative()));
		phrasePhonetic = (EditText) findViewById(R.id.edit_phrase_phonetic_value);
		phrasePhonetic.setText(String.valueOf(phrase.getPhrasePhonetic()));
		phraseRomanized = (EditText) findViewById(R.id.edit_phrase_romanized_value);
		phraseRomanized.setText(String.valueOf(phrase.getPhraseRomanized()));
		phraseTranslated = (EditText) findViewById(R.id.edit_phrase_translated_value);
		phraseTranslated.setText(String.valueOf(phrase.getPhraseTranslated()));
		
		populateSentencesView();
	}
	
	private void populateSentencesView() {
		if (sentences != null && !sentences.isEmpty()) {
			EditPhraseSentenceAdapter adapter = new EditPhraseSentenceAdapter(this, R.layout.activity_edit_phrase_sentence, sentences);
			ListView vLists = (ListView) findViewById(R.id.edit_phrase_sentences_list);
			vLists.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// Open EditSentence activity
					Intent intent = new Intent(view.getContext(), EditSentenceActivity.class);
					intent.putExtra("Sentence", sentences.get(position));
					intent.putExtra("position", position);
					startActivityForResult(intent, 1);
				}
			});
			vLists.setAdapter(adapter);
		}
	}
	
	public void save(View v) {
		Log.i(TAG, "Saving Phrase.");
		EditText phraseNative, phrasePhonetic, phraseRomanized, phraseTranslated;
		phraseNative = (EditText) findViewById(R.id.edit_phrase_native_value);
		phrase.setPhraseNative(phraseNative.getText().toString());
		phrasePhonetic = (EditText) findViewById(R.id.edit_phrase_phonetic_value);
		phrase.setPhrasePhonetic(phrasePhonetic.getText().toString());
		phraseRomanized = (EditText) findViewById(R.id.edit_phrase_romanized_value);
		phrase.setPhraseRomanized(phraseRomanized.getText().toString());
		phraseTranslated = (EditText) findViewById(R.id.edit_phrase_translated_value);
		phrase.setPhraseTranslated(phraseTranslated.getText().toString());
		
		Intent returnIntent = new Intent();
		returnIntent.putExtra("Phrase", phrase);
		returnIntent.putExtra("position", mPosition);
		setResult(1, returnIntent);
		finish();
	}
	
	public void quitAndSave(View v) {
		save(v);
	}
	
	public void quitAndDiscard(View v) {
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	FragmentManager fm = getFragmentManager();
	        final DiscardChangedPhraseDialog dialog = DiscardChangedPhraseDialog.newInstance();
	        dialog.show(fm, "dialog_discard_changed_phrase");
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == 1) {
    		if (resultCode == 1) {
    			Bundle extras = data.getExtras();
    			if (extras.containsKey("Sentence")) {
					sentences.set(extras.getInt("position"), (Sentence) extras.getParcelable("Sentence"));
    			}
    		}
    	}
    	populateSentencesView();
    }
}
