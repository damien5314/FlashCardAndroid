package com.ddiehl.android.flashcard.activities;

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
import android.widget.TextView;

import com.ddiehl.android.flashcard.R;
import com.ddiehl.android.flashcard.adapters.EditListPhrasesAdapter;
import com.ddiehl.android.flashcard.dialogs.DiscardChangedPhraseDialog;
import com.ddiehl.android.flashcard.listeners.PhraseSelectionListener;
import com.ddiehl.android.flashcard.quizsession.Phrase;
import com.ddiehl.android.flashcard.quizsession.PhraseCollection;
import com.ddiehl.android.flashcard.util.GooglePlayConnectedActivity;
import com.google.android.gms.drive.DriveId;

public class EditListActivity extends GooglePlayConnectedActivity {
	private static final String TAG = EditListActivity.class.getSimpleName();
	public static final int RESULT_CODE_SAVE = 1001;
	private PhraseCollection mPhraseCollection;
	private int mPosition;
	private DriveId mDriveId;
	private EditListPhrasesAdapter mPhraseAdapter;
	private boolean isAltered = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_list);
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("position")) {
			mPosition = extras.getInt("position");
		}
		if (extras.containsKey("DriveId")) {
			mDriveId = DriveId.decodeFromString(extras.getString("DriveId"));
		}
		if (extras.containsKey("PhraseCollection")) {
			populateContentView((PhraseCollection) extras.getParcelable("PhraseCollection"));
		} else {
			Log.e(TAG, "No PhraseCollection included with extras.");
		}
	}
	
	private void populateContentView(PhraseCollection in) {
		mPhraseCollection = in;
		EditText tv = (EditText) findViewById(R.id.edit_list_title);
		tv.setText(mPhraseCollection.getTitle());
		updateTotalPhrases();
		mPhraseAdapter = new EditListPhrasesAdapter(this, R.layout.activity_edit_list_item, mPhraseCollection);
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
		vLists.setMultiChoiceModeListener(new PhraseSelectionListener(vLists, mPhraseAdapter));
		vLists.setAdapter(mPhraseAdapter);
	}
	
	private void refreshContentView() {
    	populateContentView(mPhraseCollection);
	}
	
	private void updateTotalPhrases() {
		TextView vPhrasesTotal = (TextView) findViewById(R.id.editList_listPhrases_total_value);
		vPhrasesTotal.setText(String.valueOf(mPhraseCollection.size()));
	}
	
	public void addNewItem() {
		isAltered = true;
		Phrase newPhrase = new Phrase();
		mPhraseCollection.add(newPhrase);
		updateTotalPhrases();
		mPhraseAdapter.notifyDataSetChanged();
	}
	
	private boolean checkIfAltered() {
		return isAltered;
	}

	public void quitAndSave(final View v) {
        // Set title of the PhraseCollection before we shift to loading view
        mPhraseCollection.setTitle(((EditText) findViewById(R.id.edit_list_title)).getText().toString());

        // Open layout with ProgressBar while we are processing DriveFile
        setContentView(R.layout.activity_circle);

        // Serialize PhraseCollection to XML and feed that into update function
        final String listXml = mPhraseCollection.serializeToXml(this);

		// Open a new Thread to save the file and exit back to ListSelectionActivity
		new Thread(new Runnable() {
			@Override
			public void run() {
				mPhraseCollection.writeChangesToDrive(listXml, (EditListActivity)v.getContext());

				// Open new Intent to create Bundle to pass back to ListSelectionActivity
				Intent rIntent = new Intent();
				rIntent.putExtra("PhraseCollection", mPhraseCollection);
				rIntent.putExtra("position", mPosition);
				setResult(RESULT_CODE_SAVE, rIntent);
				finish();
			}
		}).start();
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
    			Bundle extras = data.getExtras();
    			if (extras.containsKey("Phrase")) {
					mPhraseCollection.set(extras.getInt("position"), (Phrase) extras.getParcelable("Phrase"));
    			}
    		}
    	}
    	refreshContentView();
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
