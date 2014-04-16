package com.ddiehl.flashcard.activities;

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
import android.widget.ListView;
import android.widget.TextView;

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.adapters.EditListPhrasesAdapter;
import com.ddiehl.flashcard.dialogs.DiscardChangedPhraseDialog;
import com.ddiehl.flashcard.quizsession.Phrase;
import com.ddiehl.flashcard.quizsession.PhraseCollection;

public class EditListActivity extends Activity {
	private static final String TAG = EditListActivity.class.getSimpleName();
	private PhraseCollection mPhraseCollection;
	private int mPosition;
	private EditListPhrasesAdapter mPhraseAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_list);
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("position")) {
			mPosition = extras.getInt("position");
		}
		if (extras.containsKey("PhraseCollection")) {
			populateContentView((PhraseCollection) extras.getParcelable("PhraseCollection"));
		} else {
			Log.e(TAG, "No PhraseCollection included with extras.");
		}
	}
	
	private void populateContentView(PhraseCollection in) {
		mPhraseCollection = in;
		TextView tv = (TextView) findViewById(R.id.edit_list_title);
		tv.setText(mPhraseCollection.getTitle());
		TextView vPhrasesTotal = (TextView) findViewById(R.id.editList_listPhrases_total_value);
		vPhrasesTotal.setText(String.valueOf(mPhraseCollection.getPhrasesTotal()));
		mPhraseAdapter = new EditListPhrasesAdapter(this, R.layout.activity_edit_list_phrase, mPhraseCollection);
		ListView vLists = (ListView) findViewById(R.id.editList_phraseList);
		vLists.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(view.getContext(), EditPhraseActivity.class);
				i.putExtra("Phrase", mPhraseCollection.get(position));
				i.putExtra("position", position);
				startActivityForResult(i, 1);
			}
		});
		vLists.setAdapter(mPhraseAdapter);
	}
	
	private void refreshContentView() {
    	populateContentView(mPhraseCollection);
	}
	
	public void addNewItem() {
		Phrase newPhrase = new Phrase();
		mPhraseCollection.add(newPhrase);
		mPhraseAdapter.notifyDataSetChanged();
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == 1) {
    		if (resultCode == 1) {
    			Bundle extras = data.getExtras();
    			if (extras.containsKey("Phrase")) {
					mPhraseCollection.set(extras.getInt("position"), (Phrase) extras.getParcelable("Phrase"));
    			}
    		}
    	}
    	refreshContentView();
    }
	
	public void save(View v) {
		mPhraseCollection.save(v.getContext());
		Intent rIntent = new Intent();
		rIntent.putExtra("PhraseCollection", mPhraseCollection);
		rIntent.putExtra("position", mPosition);
		setResult(1, rIntent);
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
