package com.ddiehl.flashcard.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.adapters.ListSelectionAdapter;
import com.ddiehl.flashcard.dialogs.ExitAppDialog;
import com.ddiehl.flashcard.listeners.ListSelectionListener;
import com.ddiehl.flashcard.quizsession.PhraseCollection;
import com.ddiehl.flashcard.util.GooglePlayConnectedActivity;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

public class ListSelectionActivity extends GooglePlayConnectedActivity {
	private static final String TAG = ListSelectionActivity.class
			.getSimpleName();
	private ArrayList<PhraseCollection> mVocabularyLists = new ArrayList<PhraseCollection>();
	private ListSelectionAdapter mListAdapter;
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_selection);
		// getWindow().getDecorView().setBackgroundResource(android.R.color.white);
		refreshContentView();
	}

	private void refreshContentView() {
		File fileDir = getFilesDir();
		File[] myFiles = fileDir.listFiles();

		// If no files are in file directory, port the files from
		// /assets/vocabulary-lists/
		if (myFiles.length == 0) {
			Log.i(TAG,
					"No previous files detected, copying lists from /assets/ into /data/.");
			AssetManager assets = getAssets();
			try {
				String[] assetFilenames = assets
						.list(getString(R.string.assetListGroup));
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
			mVocabularyLists.add(new PhraseCollection(thisList, filename));
		}

		mListAdapter = new ListSelectionAdapter(this,
				R.layout.activity_list_selection_item, mVocabularyLists);

		mListView = (ListView) findViewById(R.id.vocabulary_lists);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getBaseContext(),
						LoadListDataActivity.class);
				intent.putExtra("PhraseCollection",
						mVocabularyLists.get(position));
				intent.putExtra("position", position);
				view.getContext().startActivity(intent);
			}
		});
		mListView.setMultiChoiceModeListener(new ListSelectionListener(
				mListView, mListAdapter));
		mListView.setAdapter(mListAdapter);
	}

	private DriveFolder mAppFolder;

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
		mAppFolder = Drive.DriveApi.getAppFolder(getGoogleApiClient());
		Query query = new Query.Builder().addFilter(
				Filters.eq(SearchableField.MIME_TYPE, "text/xml")).build();
		mAppFolder.queryChildren(getGoogleApiClient(), query)
				.setResultCallback(xmlFileCallback);
	}

	private ResultCallback<MetadataBufferResult> xmlFileCallback = new ResultCallback<MetadataBufferResult>() {
		@Override
		public void onResult(MetadataBufferResult result) {
			if (!result.getStatus().isSuccess()) {
				Log.d(TAG, "Error while trying to retrieve application folder.");
				return;
			}

			MetadataBuffer data = result.getMetadataBuffer();
			Iterator<Metadata> iterator = data.iterator();
			Log.d(TAG, "Number of list files on Drive: " + data.getCount());
			while (iterator.hasNext()) {
				Metadata file = iterator.next();
				String filename = file.getOriginalFilename();
				Log.d(TAG, "Drive File: " + filename);
			}
		}
	};

	public void syncListsToDrive() {
		File[] files = getFilesDir().listFiles();
		for (int i = 0; i < files.length; i++) {
			File listToUpload = files[i];
			Log.d(TAG, "Attempting to upload file: " + listToUpload.getName());
			Drive.DriveApi.newContents(getGoogleApiClient())
            	.setResultCallback(contentsCallback);
		}
	}
	
	private ResultCallback<ContentsResult> contentsCallback = new ResultCallback<ContentsResult>() {
		@Override
		public void onResult(ContentsResult arg0) {
			DriveFolder appfolder = Drive.DriveApi.getAppFolder(getGoogleApiClient());
			MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
					.setTitle(listToUpload.getName()).setMimeType("text/xml")
					.build();
			appfolder.createFile(getGoogleApiClient(), changeSet,
					arg0.getContents());
		}
		
	};

	public void addNewItem() {
		PhraseCollection newPc = new PhraseCollection();
		newPc.setTitle("New List");
		newPc.save(this);
		mVocabularyLists.add(0, newPc);
		mListAdapter.notifyDataSetChanged();
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
		FileOutputStream myFile = openFileOutput(outFilename,
				Context.MODE_PRIVATE);
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
				mVocabularyLists.set(extras.getInt("position"), thisCollection);
				mListAdapter.notifyDataSetChanged();
			}
		}
	}

	public void quit(View v) {
		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			FragmentManager fm = getFragmentManager();
			final ExitAppDialog dialog = ExitAppDialog.newInstance();
			dialog.show(fm, "dialog_exit_app");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_item_sync, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_sync_to_drive:
			syncListsToDrive();
			return true;
		case R.id.action_add_new:
			addNewItem();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
