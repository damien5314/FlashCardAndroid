package com.ddiehl.flashcard.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.adapters.ListSelectionAdapter;
import com.ddiehl.flashcard.listeners.ListSelectionListener;
import com.ddiehl.flashcard.quizsession.PhraseCollection;

public class ListSelectionActivity extends Activity {
	private static final String TAG = ListSelectionActivity.class.getSimpleName();
	private ArrayList<PhraseCollection> vocabularyLists = new ArrayList<PhraseCollection>();
	private ListSelectionAdapter adapter;
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_selection);
		refreshContentView();
	}

	private void refreshContentView() {
		File fileDir = getFilesDir();
		File[] myFiles = fileDir.listFiles();

		// If no files are in file directory, port the files from
		// /assets/vocabulary-lists/
		if (myFiles.length == 0) {
			Log.i(TAG, "No previous files detected, copying lists from /assets/ into /data/.");
			AssetManager assets = getAssets();
			try {
				String[] assetFilenames = assets.list(getString(R.string.assetListGroup));
				for (int i = 0; i < assetFilenames.length; i++) {
					copyAssetToData(assetFilenames[i]);
				}
			} catch (IOException e) {
				Log.e(TAG, "Error retrieving assets.");
				e.printStackTrace();
			}
			myFiles = fileDir.listFiles(); // Refresh file list
		}

		String[] filenames = new String[myFiles.length];

		for (int i = 0; i < myFiles.length; i++) {
			Log.d(TAG, "Loading: " + myFiles[i].getName());
			filenames[i] = myFiles[i].getName();
		}

		for (int i = 0; i < filenames.length; i++) {
			String filename = filenames[i];
			FileInputStream thisList = null;
			try {
				thisList = openFileInput(filenames[i]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			vocabularyLists.add(new PhraseCollection(thisList, filename));
		}

		adapter = new ListSelectionAdapter(this, R.layout.activity_list_selection_item, vocabularyLists);

		mListView = (ListView) findViewById(R.id.vocabulary_lists);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getBaseContext(), LoadListDataActivity.class);
				intent.putExtra("PhraseCollection", vocabularyLists.get(position));
				intent.putExtra("position", position);
				view.getContext().startActivity(intent);
			}
		});
		mListView.setMultiChoiceModeListener(new ListSelectionListener(mListView, adapter));
		mListView.setAdapter(adapter);
	}

	public void addNewItem() {
		PhraseCollection newPc = new PhraseCollection();
		newPc.setTitle("New List");
		newPc.save(this);
		vocabularyLists.add(0, newPc);
		adapter.notifyDataSetChanged();
	}

	public void editList(View v) {
		PhraseCollection pc = (PhraseCollection) v.getTag();
		Intent intent = new Intent(this, EditListActivity.class);
		intent.putExtra("PhraseCollection", pc);
		ListView lv = (ListView) findViewById(R.id.vocabulary_lists);
		int position = lv.getPositionForView(v);
		intent.putExtra("position", position);
		startActivityForResult(intent, 1);
	}

	private void copyAssetToData(String filename) throws IOException {
		InputStream myInput = getAssets().open("vocabulary-lists/" + filename);
		String outFilename = filename;
		FileOutputStream myFile = openFileOutput(outFilename, Context.MODE_PRIVATE);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myFile.write(buffer, 0, length);
		}
		myFile.flush();
		myFile.close();
		myInput.close();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 1) {
			Bundle extras = data.getExtras();
			if (extras.containsKey("PhraseCollection")
					&& extras.containsKey("position")) {
				PhraseCollection thisCollection = (PhraseCollection) extras
						.getParcelable("PhraseCollection");
				vocabularyLists.set(extras.getInt("position"), thisCollection);
				adapter.notifyDataSetChanged();
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
