package com.ddiehl.flashcard.activities;

import java.util.ArrayList;
import java.util.Iterator;

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

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.adapters.ListSelectionAdapter;
import com.ddiehl.flashcard.dialogs.ExitAppDialog;
import com.ddiehl.flashcard.fileio.FlashcardFile;
import com.ddiehl.flashcard.listeners.ListSelectionListener;
import com.ddiehl.flashcard.quizsession.PhraseCollection;
import com.ddiehl.flashcard.util.GooglePlayConnectedActivity;
import com.ddiehl.flashcard.util.Utils;
import com.google.android.gms.common.api.ResultCallback;
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
	private ArrayList<FlashcardFile> mFiles;
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
		mFiles = new ArrayList<FlashcardFile>();
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
					listFilesFromDrive(folder);
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
	
	private void listFilesFromDrive(DriveFolder folder) {
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
					DriveFile df = getFileByDriveId(m.getDriveId());
					FlashcardFile file = new FlashcardFile(m.getTitle(), df);
					addFileToCollection(file);
					filesProcessed++;
				}
				buffer.close();
				refreshContentView();
				Utils.showToast(getBaseContext(), "Files processed from Drive folder: " + filesProcessed);
			}
		});
	}
	
	private void addFileToCollection(FlashcardFile file) {
		mFiles.add(file);
	}

	public void addNewItem() {
		if (driveFolderId == null) {
			createFolderInDrive(Drive.DriveApi.getRootFolder(getGoogleApiClient()));
		}
		FlashcardFile newFile = new FlashcardFile();
		newFile.setTitle(NEW_LIST_TITLE);
		Drive.DriveApi.newContents(getGoogleApiClient())
			.setResultCallback(getNewFileCallback(newFile));
	}
	
	private ResultCallback<ContentsResult> getNewFileCallback(final FlashcardFile file) {
		return new ResultCallback<ContentsResult>() {
			@Override
			public void onResult(ContentsResult result) {
				DriveFolder folder = Drive.DriveApi.getFolder(getGoogleApiClient(), driveFolderId);
				MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
						.setTitle(file.getTitle())
						.setMimeType("text/xml")
						.build();
				folder.createFile(getGoogleApiClient(), changeSet, result.getContents())
						.setResultCallback(getFileCreatedCallback(file));
			}
		};
	}
	
	private ResultCallback<DriveFileResult> getFileCreatedCallback(final FlashcardFile file) {
		return new ResultCallback<DriveFileResult>() {
			@Override
			public void onResult(DriveFileResult result) {
				Log.i(TAG, "Drive file created: " + result.getDriveFile().getDriveId().encodeToString());
				DriveFile createdFile = result.getDriveFile();
				file.setDriveFile(createdFile);
				addToFileList(file);
			}
		};
	}
	
	private void addToFileList(FlashcardFile file) {
		mFiles.add(file);
		refreshContentView();
	}
	
	private DriveFile getFileByDriveId(DriveId id) {
		return Drive.DriveApi.getFile(this.getGoogleApiClient(), id);
	}
	
	private PhraseCollection getPhraseCollectionFromFile(FlashcardFile file) {
		
		
		
		return null;
	}
	
	private void refreshContentView() {
		if (mListAdapter == null) {
			mListAdapter = new ListSelectionAdapter(this, R.layout.activity_list_selection_item, mFiles);
			mListView = (ListView) findViewById(R.id.vocabulary_lists);
			mListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
					final Intent intent = new Intent(getBaseContext(), LoadListDataActivity.class);
					final FlashcardFile file = mFiles.get(position);
					new Thread(new Runnable() {
						@Override
						public void run() {
							PhraseCollection pc = file.generatePhraseCollectionFromDriveFile(getGoogleApiClient());
							intent.putExtra("PhraseCollection", pc);
							intent.putExtra("position", position);
							view.getContext().startActivity(intent);
						}
					}).start();
				}
			});
			mListView.setMultiChoiceModeListener(new ListSelectionListener(mListView, mListAdapter));
			mListView.setAdapter(mListAdapter);
		} else {
			mListAdapter.notifyDataSetChanged();
		}
	}

	public void syncListsToDrive() {

	}

	public void editList(final View v) {
		final FlashcardFile file = (FlashcardFile) v.getTag();
		final Intent intent = new Intent(this, EditListActivity.class);
		new Thread(new Runnable() {
			@Override
			public void run() {
				PhraseCollection pc = file.generatePhraseCollectionFromDriveFile(getGoogleApiClient());
				intent.putExtra("PhraseCollection", pc);
				ListView lv = (ListView) findViewById(R.id.vocabulary_lists);
				int position = lv.getPositionForView(v);
				intent.putExtra("position", position);
				startActivityForResult(intent, REQUEST_CODE_EDIT_LIST);
			}
		}).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bundle extras = data.getExtras();
		switch (requestCode) {
		case REQUEST_CODE_EDIT_LIST:
			switch (resultCode) {
			case EditListActivity.RESULT_CODE_SAVE:
				int position = extras.getInt("position");
				PhraseCollection list = extras.getParcelable("PhraseCollection");
				FlashcardFile file = mFiles.get(position);
				file.updateContents(list);
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
