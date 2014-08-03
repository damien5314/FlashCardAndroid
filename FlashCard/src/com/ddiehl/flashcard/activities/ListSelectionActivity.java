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
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
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
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

public class ListSelectionActivity extends GooglePlayConnectedActivity {
	private static final String TAG = ListSelectionActivity.class
			.getSimpleName();
	private ArrayList<PhraseCollection> mVocabularyLists;
	private ListSelectionAdapter mListAdapter;
	private ListView mListView;
	private static final int REQUEST_CODE_EDIT_LIST = 1001;
	private static final int REQUEST_CODE_CREATOR = 1002;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_selection);		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		if (!mClient.isConnecting()) {
			generateContentFromLocal();
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
		generateContentFromDrive();
	}
	
	// Instantiate the ArrayList mVocabularyLists with PhraseCollection objects
	private void generateContentFromDrive() {
		if (getGoogleApiClient().isConnected()) {
			mVocabularyLists = new ArrayList<PhraseCollection>();
			final DriveFolder folder = Drive.DriveApi.getAppFolder(getGoogleApiClient());
			folder.listChildren(getGoogleApiClient()).setResultCallback(
					new ResultCallback<MetadataBufferResult>() {

						@Override
						public void onResult(MetadataBufferResult result) {
							// TODO Auto-generated method stub
							if (!result.getStatus().isSuccess()) {
								Log.e(TAG, "Error retrieving Files from Drive app folder.");
								Log.e(TAG, result.getStatus().toString());
								return;
							}
							MetadataBuffer buffer = result.getMetadataBuffer(); // Do I need to call this multiple times for many files?
							Iterator<Metadata> i = buffer.iterator();
							final int expectedDriveFiles = buffer.getCount();
							while (i.hasNext()) {
								Log.d(TAG, "Found file, adding to list.");
								Metadata m = i.next();
								DriveId id = m.getDriveId();
								DriveFile f = getFileByDriveId(id);
								f.openContents(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, new DriveFile.DownloadProgressListener() {
									@Override
									public void onProgress(long bytesDownloaded, long bytesExpected) {
										// TODO Implement DownloadProgressListener when retrieving a DriveFile
										Log.v(TAG, "Bytes downloaded: " + bytesDownloaded + " (Expected " + bytesExpected + ")");
									}
								}).setResultCallback(new ResultCallback<DriveApi.ContentsResult>() {
									@Override
									public void onResult(ContentsResult result) {
										// TODO Implement callback for ContentsResult when retrieving a DriveFile
										Contents contents = result.getContents();
										InputStream in_s = contents.getInputStream();
//										Log.v(TAG, "File contents:");
//										Log.v(TAG, com.ddiehl.flashcard.util.Utils.getStringFromInputStream(in_s));
										addToFileList(in_s);
									}
								});
							}
							buffer.close();
							Log.d(TAG, "Size of mVocabularyLists: " + mVocabularyLists.size());
							refreshContentView();
						}
				
			});
		}
	}
	
	private void generateContentFromLocal() {
		File fileDir = getFilesDir();
		File[] myFiles = fileDir.listFiles();

		// If no files are in file directory, port the files from
		// /assets/vocabulary-lists/
		if (myFiles.length == 0) {
			Log.i(TAG, "No previous files detected, copying lists from /assets/ into /data/.");
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
		
		// Store filenames for displaying in content view
		String[] filenames = new String[myFiles.length];
		mVocabularyLists = new ArrayList<PhraseCollection>();

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
		refreshContentView();
	}
	
	
	private void addToFileList(InputStream in_s) {
		this.mVocabularyLists.add(new PhraseCollection(in_s));
		mListAdapter.notifyDataSetChanged();
	}
	
	private DriveFile getFileByDriveId(DriveId id) {
		return Drive.DriveApi.getFile(this.getGoogleApiClient(), id);
	}
	
	private void refreshContentView() {
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

	public void syncListsToDrive() {
		if (getGoogleApiClient().isConnected()) {
			File[] files = getFilesDir().listFiles();
			final DriveFolder folder = Drive.DriveApi.getAppFolder(getGoogleApiClient());
			for (int i = 0; i < files.length; i++) {
				final File listToUpload = files[i];
				folder.queryChildren(
						getGoogleApiClient(),
						new Query.Builder().addFilter(
								Filters.eq(SearchableField.TITLE,
										listToUpload.getName())).build())
						.setResultCallback(
								new ResultCallback<MetadataBufferResult>() {
									@Override
									public void onResult(MetadataBufferResult result) {
										if (!result.getStatus().isSuccess()) {
											Log.e(TAG, "Error retrieving Files from Drive app folder.");
											Log.e(TAG, result.getStatus().toString());
											return;
										}
										MetadataBuffer buffer = result.getMetadataBuffer();
										switch (buffer.getCount()) {
										case 0:
											Log.d(TAG, "0 files on Drive matched, uploading..");
											Log.d(TAG, listToUpload.getName());
											uploadFile(folder, listToUpload);
											break;
										case 1:
											Log.d(TAG, "1 file on Drive matched, syncing..");
											Log.d(TAG, listToUpload.getName());
											syncFile(folder, listToUpload);
											break;
										default:
											Log.w(TAG, "Results returned from Drive query: " + buffer.getCount());
											break;
										}
										buffer.close();
									}
								});
			}
		} else {
			getGoogleApiClient().connect();
		}
	}

	private void uploadFile(DriveFolder fol_in, File f_in) {
		final DriveFolder appFolder = fol_in;
		final File fileToUpload = f_in;
		
		final ResultCallback<DriveFileResult> fileCallback = new
	            ResultCallback<DriveFileResult>() {
	        @Override
	        public void onResult(DriveFileResult result) {
	            if (!result.getStatus().isSuccess()) {
	                Log.d(TAG, "Error while trying to create the file");
	                return;
	            }
	            Log.d(TAG, "Created a file: " + result.getDriveFile().getDriveId());
	            // Add file contents here?
	            result.getDriveFile().openContents(getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, null)
	            .setResultCallback(new ResultCallback<ContentsResult>() {
					@Override
					public void onResult(ContentsResult result) {
						// TODO Auto-generated method stub
						Contents contents = result.getContents();
//						contents.getOutputStream().write(buffer);
					}
	            });
	        }
	    };

		ResultCallback<ContentsResult> contentsCallback = new ResultCallback<ContentsResult>() {
			@Override
			public void onResult(ContentsResult result) {
				if (!result.getStatus().isSuccess()) {
					Log.d(TAG, "Error while trying to create new file contents");
					return;
				}
				
				// In this change set we also need to include the content, currently we are only creating a file with the title
				MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
						.setTitle(fileToUpload.getName())
						.setMimeType("text/xml")
						.build();
				// create a file on root folder
				appFolder.createFile(getGoogleApiClient(), changeSet,
								result.getContents())
						.setResultCallback(fileCallback);
			}
		};

		Drive.DriveApi.newContents(getGoogleApiClient()).setResultCallback(contentsCallback);
	}
	
	// TODO Implement method for overwriting file on Drive
	private void syncFile(DriveFolder fol_in, File f_in) {
		
	}

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
		startActivityForResult(intent, REQUEST_CODE_EDIT_LIST);
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
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_EDIT_LIST:
			switch (resultCode) {
			case 1:
				Bundle extras = data.getExtras();
				if (extras.containsKey("PhraseCollection")
						&& extras.containsKey("position")) {
					PhraseCollection thisCollection = (PhraseCollection) extras
							.getParcelable("PhraseCollection");
					mVocabularyLists.set(extras.getInt("position"),
							thisCollection);
					mListAdapter.notifyDataSetChanged();
				}
				break;
			}
			break;
			
		default:
			Log.d(TAG, "Request Code not recognized: " + requestCode);
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
		getMenuInflater().inflate(R.menu.select_list, menu);
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
		case R.id.action_settings:
	        Intent intent = new Intent(this, SettingsActivity.class);
	        startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
} 
