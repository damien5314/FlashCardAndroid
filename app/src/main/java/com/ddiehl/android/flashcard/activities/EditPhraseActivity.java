package com.ddiehl.android.flashcard.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.ddiehl.android.flashcard.adapters.EditPhraseSentenceAdapter;
import com.ddiehl.android.flashcard.dialogs.DiscardChangedPhraseDialog;
import com.ddiehl.android.flashcard.listeners.SentenceSelectionListener;
import com.ddiehl.android.flashcard.quizsession.Phrase;
import com.ddiehl.android.flashcard.quizsession.Sentence;
import com.ddiehl.android.flashcard.R;

import java.util.ArrayList;

public class EditPhraseActivity extends Activity {
	private static final String TAG = EditPhraseActivity.class.getSimpleName();
	private Phrase mPhrase;
	private ArrayList<Sentence> mSentences;
	private int mPosition;
	private EditPhraseSentenceAdapter mSentenceAdapter;
	private boolean isAltered;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_phrase);
		Bundle extras = this.getIntent().getExtras();
		if (extras.containsKey("Phrase")) {
			mPhrase = extras.getParcelable("Phrase");
			mSentences = mPhrase.getPhraseSentences();
			mPosition = extras.getInt("position");
			populateContents();
		} else {
			Log.e(TAG, "No Phrase detected in extras.");
		}
	}
	
	private void populateContents() {
		EditText phraseNative, phrasePhonetic, phraseRomanized, phraseTranslated;
		phraseNative = (EditText) findViewById(R.id.edit_phrase_native_value);
		phraseNative.setText(String.valueOf(mPhrase.getPhraseNative()));
		phrasePhonetic = (EditText) findViewById(R.id.edit_phrase_phonetic_value);
		phrasePhonetic.setText(String.valueOf(mPhrase.getPhrasePhonetic()));
		phraseRomanized = (EditText) findViewById(R.id.edit_phrase_romanized_value);
		phraseRomanized.setText(String.valueOf(mPhrase.getPhraseRomanized()));
		phraseTranslated = (EditText) findViewById(R.id.edit_phrase_translated_value);
		phraseTranslated.setText(String.valueOf(mPhrase.getPhraseTranslated()));
		
		populateSentencesView();
	}
	
	private void populateSentencesView() {
		mSentenceAdapter = new EditPhraseSentenceAdapter(this, R.layout.activity_edit_phrase_item, mSentences);
		ListView vLists = (ListView) findViewById(R.id.edit_phrase_sentences_list);
		vLists.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(view.getContext(), EditSentenceActivity.class);
				intent.putExtra("Sentence", mSentences.get(position));
				intent.putExtra("position", position);
				startActivityForResult(intent, 1);
			}
		});
		vLists.setMultiChoiceModeListener(new SentenceSelectionListener(vLists, mSentenceAdapter));
		vLists.setAdapter(mSentenceAdapter);
		if (mSentences != null && !mSentences.isEmpty()) {
			
		}
	}
	
	private void addNewItem() {
		isAltered = true;
		Sentence newItem = new Sentence();
		mSentences.add(newItem);
		mSentenceAdapter.notifyDataSetChanged();
	}
	
	public void save(View v) {
		Log.i(TAG, "Saving Phrase.");
		EditText phraseNative, phrasePhonetic, phraseRomanized, phraseTranslated;
		phraseNative = (EditText) findViewById(R.id.edit_phrase_native_value);
		mPhrase.setPhraseNative(phraseNative.getText().toString());
		phrasePhonetic = (EditText) findViewById(R.id.edit_phrase_phonetic_value);
		mPhrase.setPhrasePhonetic(phrasePhonetic.getText().toString());
		phraseRomanized = (EditText) findViewById(R.id.edit_phrase_romanized_value);
		mPhrase.setPhraseRomanized(phraseRomanized.getText().toString());
		phraseTranslated = (EditText) findViewById(R.id.edit_phrase_translated_value);
		mPhrase.setPhraseTranslated(phraseTranslated.getText().toString());
		
		Intent returnIntent = new Intent();
		returnIntent.putExtra("Phrase", mPhrase);
		returnIntent.putExtra("position", mPosition);
		setResult(1, returnIntent);
		finish();
	}
	
	private boolean checkIfAltered() {
		if (!mPhrase.getPhraseNative().equals( ( (EditText) findViewById(R.id.edit_phrase_native_value)).getText().toString() ) ) {
			isAltered = true;
		} else if (!mPhrase.getPhrasePhonetic().equals( ( (EditText) findViewById(R.id.edit_phrase_phonetic_value)).getText().toString() ) ) {
			isAltered = true;
		} else if (!mPhrase.getPhraseRomanized().equals( ( (EditText) findViewById(R.id.edit_phrase_romanized_value)).getText().toString() ) ) {
			isAltered = true;
		} else if (!mPhrase.getPhraseTranslated().equals( ( (EditText) findViewById(R.id.edit_phrase_translated_value)).getText().toString() ) ) {
			isAltered = true;
		}
		
		if (isAltered)
			return true;
		
		return false;
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
	    	if (checkIfAltered()) {
		    	FragmentManager fm = getFragmentManager();
		        final DiscardChangedPhraseDialog dialog = DiscardChangedPhraseDialog.newInstance();
		        dialog.show(fm, "dialog_discard_changed_phrase");
	    	} else {
	    		finish();
	    	}
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == 1) {
    		if (resultCode == 1) {
				isAltered = true;
    			Bundle extras = data.getExtras();
    			if (extras.containsKey("Sentence")) {
					mSentences.set(extras.getInt("position"), (Sentence) extras.getParcelable("Sentence"));
    			}
    	    	populateSentencesView();
    		}
    	}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_item, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_add_new:
				addNewItem();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
