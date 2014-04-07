package com.ddiehl.flashcard.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.adapters.EditListPhrasesAdapter;
import com.ddiehl.flashcard.quizsession.Phrase;
import com.ddiehl.flashcard.quizsession.PhraseCollection;

public class EditListActivity extends Activity {
	private static final String TAG = "EditListActivity";
	private PhraseCollection pc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_list);
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("PhraseCollection")) {
			populateContentView((PhraseCollection) extras.getParcelable("PhraseCollection"));
		} else {
			Log.e(TAG, "No PhraseCollection included with extras.");
		}
	}
	
	private void populateContentView(PhraseCollection in) {
		pc = in;
		TextView tv = (TextView) findViewById(R.id.edit_list_title);
		tv.setText(pc.getTitle());
		TextView vPhrasesTotal = (TextView) findViewById(R.id.editList_listPhrases_total_value);
		vPhrasesTotal.setText(String.valueOf(pc.getPhrasesTotal()));
		EditListPhrasesAdapter adapter = new EditListPhrasesAdapter(this, R.layout.activity_edit_list_phrase, pc);
		ListView vLists = (ListView) findViewById(R.id.editList_phraseList);
		vLists.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Open EditPhrase activity
				Intent i = new Intent(view.getContext(), EditPhraseActivity.class);
				i.putExtra("Phrase", pc.get(position));
				i.putExtra("position", position);
				startActivityForResult(i, 1);
			}
		});
		vLists.setAdapter(adapter);
	}
	
	private void refreshContentView() {
    	populateContentView(pc);
	}
	
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == 1) {
    		if (resultCode == 1) {
    			Bundle extras = data.getExtras();
    			if (extras.containsKey("Phrase")) {
					pc.set(extras.getInt("position"), (Phrase) extras.getParcelable("Phrase"));
    			}
    		}
    	}
    	refreshContentView();
    }
	
	public void save(View v) {
		pc.save(v.getContext());
		Intent rIntent = new Intent();
		rIntent.putExtra("PhraseCollection", pc);
		setResult(1, rIntent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
