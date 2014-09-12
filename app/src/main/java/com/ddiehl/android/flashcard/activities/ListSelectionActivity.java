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
import android.widget.ListView;

import com.ddiehl.android.flashcard.R;
import com.ddiehl.android.flashcard.adapters.ListSelectionAdapter;
import com.ddiehl.android.flashcard.dialogs.ExitAppDialog;
import com.ddiehl.android.flashcard.listeners.ListSelectionListener;
import com.ddiehl.android.flashcard.quizsession.PhraseCollection;
import com.ddiehl.android.flashcard.util.GooglePlayConnectedActivity;
import com.ddiehl.android.flashcard.util.Utils;
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

import java.util.ArrayList;
import java.util.Iterator;

public class ListSelectionActivity extends GooglePlayConnectedActivity {
	private static final String TAG = ListSelectionActivity.class.getSimpleName();
	private ArrayList<PhraseCollection> mFiles = null;
	private ListSelectionAdapter mListAdapter = null;
	private ListView mListView;
	private static final int REQUEST_CODE_EDIT_LIST = 1001;
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
		if (mFiles == null)
			generateContentFromDrive();
	}
	
	// Instantiate the ArrayList mVocabularyLists with PhraseCollection objects
	private void generateContentFromDrive() {
		mFiles = new ArrayList<PhraseCollection>();
		if (getGoogleApiClient().isConnected()) {
			if (driveFolderId == null)
				queryFolderInDrive();
			else
				listFilesFromDrive(Drive.DriveApi.getFolder(getGoogleApiClient(), driveFolderId));
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
	    
		pFolder.createFolder(getGoogleApiClient(), changeSet)
			.setResultCallback(getFolderCreatedCallback());
	}
	
	private ResultCallback<DriveFolderResult> getFolderCreatedCallback() {
		return new ResultCallback<DriveFolderResult>() {
			@Override
			public void onResult(DriveFolderResult result) {
				Log.i(TAG, "FlashCard folder created in Drive root folder.");
				Log.d(TAG, "Drive resource ID: " + result.getDriveFolder().getDriveId());
				driveFolderId = result.getDriveFolder().getDriveId();
			}
	    };
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
                        PhraseCollection file = new PhraseCollection(df.getDriveId());
						addFileToCollection(file);
						filesProcessed++;
					}
					buffer.close();
					refreshContentView();
					Utils.showToast(getBaseContext(), "Files processed from Drive folder: " + filesProcessed);
				}
			});
	}

    private void addFileToCollection(PhraseCollection file) {
        mFiles.add(file);
        refreshContentView();
    }

    // TODO Rewrite the Add New List flow to be synchronous
	public void addNewItem() {
		if (driveFolderId == null) {
			createFolderInDrive(Drive.DriveApi.getRootFolder(getGoogleApiClient()));
		}
		PhraseCollection newFile = new PhraseCollection();
		Drive.DriveApi.newContents(getGoogleApiClient())
			.setResultCallback(getNewFileCallback(newFile));
	}
	
	private ResultCallback<ContentsResult> getNewFileCallback(final PhraseCollection file) {
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
	
	private ResultCallback<DriveFileResult> getFileCreatedCallback(final PhraseCollection file) {
		return new ResultCallback<DriveFileResult>() {
			@Override
			public void onResult(DriveFileResult result) {
				Log.i(TAG, "Drive file created: " + result.getDriveFile().getDriveId().encodeToString());
                file.setDriveId(result.getDriveFile().getDriveId());
				addFileToCollection(file);
			}
		};
	}
	
	private DriveFile getFileByDriveId(DriveId id) {
		return Drive.DriveApi.getFile(this.getGoogleApiClient(), id);
	}
	
	private void refreshContentView() {
		if (mListAdapter == null) {
			mListAdapter = new ListSelectionAdapter(this, R.layout.activity_list_selection_item, mFiles);
			mListView = (ListView) findViewById(R.id.vocabulary_lists);
			mListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
					final Intent intent = new Intent(getBaseContext(), LoadListDataActivity.class);
					final PhraseCollection file = mFiles.get(position);
					new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO setContentView to loading overlay while generating PhraseCollection
                            file.generateCollectionFromDriveFile(getGoogleApiClient());
							intent.putExtra("PhraseCollection", file);
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

	public void editList(final View v) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				PhraseCollection file = (PhraseCollection) v.getTag();
				Intent intent = new Intent(v.getContext(), EditListActivity.class);
				file.generateCollectionFromDriveFile(getGoogleApiClient());
				intent.putExtra("PhraseCollection", file);
				int position = ((ListView) findViewById(R.id.vocabulary_lists)).getPositionForView(v);
				DriveId driveId = file.getDriveId();
				intent.putExtra("position", position);
				intent.putExtra("DriveId", driveId.encodeToString());
				startActivityForResult(intent, REQUEST_CODE_EDIT_LIST);
			}
		}).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_EDIT_LIST:
			switch (resultCode) {
			case EditListActivity.RESULT_CODE_SAVE:
                Bundle extras = data.getExtras();
				int position = extras.getInt("position");
				PhraseCollection list = extras.getParcelable("PhraseCollection");
                mFiles.set(position, list); // Test if we need this
				mListAdapter.notifyDataSetChanged();
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
		case R.id.action_refresh:
            generateContentFromDrive();
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
