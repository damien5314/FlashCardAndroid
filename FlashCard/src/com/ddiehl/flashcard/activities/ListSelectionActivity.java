package com.ddiehl.flashcard.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;

import android.app.FragmentManager;
import android.content.Context;
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

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.adapters.ListSelectionAdapter;
import com.ddiehl.flashcard.dialogs.ExitAppDialog;
import com.ddiehl.flashcard.listeners.ListSelectionListener;
import com.ddiehl.flashcard.quizsession.PhraseCollection;
import com.ddiehl.flashcard.util.GooglePlayConnectedActivity;
import com.ddiehl.flashcard.util.Utils;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.DriveFolder.DriveFolderResult;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

public class ListSelectionActivity extends GooglePlayConnectedActivity {
	private static final String TAG = ListSelectionActivity.class.getSimpleName();
	private ArrayList<PhraseCollection> mDriveFiles;
	private ListSelectionAdapter mListAdapter = null;
	private ListView mListView;
	private static final int REQUEST_CODE_EDIT_LIST = 1001;
	private static final int REQUEST_CODE_CREATOR = 1002;
	private static final String NEW_LIST_TITLE = "New List";
	private DriveId driveFolderId = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_selection);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		super.onConnected(connectionHint);
		generateContentFromDrive();
	}
	
	// Instantiate the ArrayList mVocabularyLists with PhraseCollection objects
	private void generateContentFromDrive() {
		mDriveFiles = new ArrayList<PhraseCollection>();
		if (getGoogleApiClient().isConnected()) {
			queryFolderInDrive();
		}
	}
	
	private void queryFolderInDrive() {
		final DriveFolder rootFolder = Drive.DriveApi.getRootFolder(getGoogleApiClient());
	    rootFolder.queryChildren(getGoogleApiClient(), new Query.Builder().addFilter(
	    		Filters.eq(SearchableField.TITLE, "FlashCard")).build())
	    .setResultCallback(new ResultCallback<MetadataBufferResult>() {
			@Override
			public void onResult(MetadataBufferResult result) {
				MetadataBuffer buffer = result.getMetadataBuffer();
				if (buffer.getCount() == 0) { // If folder does not exist
					Log.i(TAG, "FlashCard folder not found in Drive, creating.");
					createFolderInDrive(rootFolder);
				} else { // If folder does exist
					Log.i(TAG, "FlashCard folder found in Drive.");
					Metadata data = buffer.get(0);
					driveFolderId = data.getDriveId();
					DriveFolder folder = Drive.DriveApi.getFolder(getGoogleApiClient(), driveFolderId);
					queryFilesFromDriveFolder(folder);
				}
				buffer.close();
			}
	    });
	}
	
	private void createFolderInDrive(DriveFolder pFolder) {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
    		.setTitle("FlashCard").build();
        
	    final ResultCallback<DriveFolderResult> callback = new ResultCallback<DriveFolderResult>() {
			@Override
			public void onResult(DriveFolderResult result) {
				Log.i(TAG, "FlashCard folder created in Drive root folder.");
				Log.d(TAG, "Drive resource ID: " + result.getDriveFolder().getDriveId());
				driveFolderId = result.getDriveFolder().getDriveId();
//				queryFilesFromDriveFolder(result.getDriveFolder());
			}
	    };
	    
		pFolder.createFolder(getGoogleApiClient(), changeSet)
			.setResultCallback(callback);
	}
	
	private void queryFilesFromDriveFolder(DriveFolder folder) {
		folder.listChildren(getGoogleApiClient()).setResultCallback(
		new ResultCallback<MetadataBufferResult>() {
			@Override
			public void onResult(MetadataBufferResult result) {
				Log.i(TAG, "FlashCard DriveFolder contents retrieved successfully.");
				MetadataBuffer buffer = result.getMetadataBuffer();
				Iterator<Metadata> i = buffer.iterator();
				int filesProcessed = 0;
				while (i.hasNext()) {
					Metadata m = i.next();
					DriveId id = m.getDriveId();
					DriveFile f = getFileByDriveId(id);
					addDriveFileToVocabularyListCollection(f);
					filesProcessed++;
				}
				buffer.close();
				Utils.showToast(getBaseContext(), "Files processed from Drive folder: " + filesProcessed);
			}
		});
	}
	
	private void addDriveFileToVocabularyListCollection(DriveFile file) {
		Log.d(TAG, "Adding DriveFile to list collection: " + file.getDriveId());
		file.openContents(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, new DriveFile.DownloadProgressListener() {
			@Override
			public void onProgress(long bytesDownloaded, long bytesExpected) {
				// TODO Implement DownloadProgressListener when retrieving a DriveFile
				Log.v(TAG, "Bytes downloaded: " + bytesDownloaded + " (Expected " + bytesExpected + ")");
			}
		}).setResultCallback(
			new ResultCallback<ContentsResult>() {
				@Override
				public void onResult(ContentsResult result) {
					Contents contents = result.getContents();
					InputStream in_s = contents.getInputStream();
					addToFileList(in_s);
				}
		});
	}

	public void addNewItem() {
		if (driveFolderId == null) {
			createFolderInDrive(Drive.DriveApi.getRootFolder(getGoogleApiClient()));
		}
		String title = NEW_LIST_TITLE;
		PhraseCollection newPc = new PhraseCollection();
		newPc.setTitle(title);
		newPc.save(this);
		final File file = getFile(title);
		uploadFile(title, file);
		mDriveFiles.add(0, newPc);
		refreshContentView();
		mListAdapter.notifyDataSetChanged();
	}
	
	private File getFile(String name) {
		File fileDir = getFilesDir();
		File[] myFiles = fileDir.listFiles();
		for (int i = 0; i < myFiles.length; i++) {
			if (myFiles[i].getName().equals(name)) {
				return myFiles[i];
			}
		}
		return null;
	}
	
	private void addToFileList(InputStream in_s) {
		this.mDriveFiles.add(new PhraseCollection(in_s));
		refreshContentView();
		mListAdapter.notifyDataSetChanged();
	}
	
	private DriveFile getFileByDriveId(DriveId id) {
		return Drive.DriveApi.getFile(this.getGoogleApiClient(), id);
	}
	
	private void refreshContentView() {
		if (mListAdapter == null)
			mListAdapter = new ListSelectionAdapter(this, R.layout.activity_list_selection_item, mDriveFiles);

		mListView = (ListView) findViewById(R.id.vocabulary_lists);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getBaseContext(),
						LoadListDataActivity.class);
				intent.putExtra("PhraseCollection",
						mDriveFiles.get(position));
				intent.putExtra("position", position);
				view.getContext().startActivity(intent);
			}
		});
		mListView.setMultiChoiceModeListener(new ListSelectionListener(mListView, mListAdapter));
		mListView.setAdapter(mListAdapter);
	}

	public void syncListsToDrive() {

	}
	
	private void uploadFile(String title, final File file) {
		Drive.DriveApi.newContents(getGoogleApiClient())
				.setResultCallback(getNewContentsCallback(title, file));
	}
	
	private ResultCallback<ContentsResult> getNewContentsCallback(final String title, final File file) {
		return new ResultCallback<ContentsResult>() {
			@Override
			public void onResult(ContentsResult result) {
				DriveFolder folder = Drive.DriveApi.getFolder(getGoogleApiClient(), driveFolderId);
				MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
						.setTitle(title)
						.setMimeType("text/xml")
						.build();
				Contents contents = result.getContents();
				try {
					FileInputStream f_in = openFileInput(file.getName());
					OutputStream op_s = contents.getOutputStream();
					IOUtils.copy(f_in, op_s);
					f_in.close();
					op_s.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				folder.createFile(getGoogleApiClient(), changeSet, contents)
						.setResultCallback(getFileCreatedCallback());
			}
		};
	}
	
	private ResultCallback<DriveFileResult> getFileCreatedCallback() {
		return new ResultCallback<DriveFileResult>() {
			@Override
			public void onResult(DriveFileResult result) {
				Log.i(TAG, "Drive file created: " + result.getDriveFile().getDriveId());
			}
		};
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_EDIT_LIST:
			switch (resultCode) {
			case 1:
				
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
//			syncListsToDrive();
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
