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
		if (mFiles == null) {
            Log.d(TAG, "No files in list, generating content from Drive.");

            // Set content view to loading dialog while list is generated


            generateContentFromDrive();
        }
	}

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
                        if (result.getStatus().isSuccess()) {
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
                        } else {
                            Log.e(TAG, "DriveFolder not successfully retrieved.");
                            Log.e(TAG, "Status code: " + result.getStatus().getStatusCode() + " - " + "Message: " + result.getStatus().getStatusMessage());
                        }
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
        final ListSelectionActivity c = this;
		folder.listChildren(getGoogleApiClient()).setResultCallback(
            new ResultCallback<MetadataBufferResult>() {
                @Override
                public void onResult(MetadataBufferResult result) {
                    if (result.getStatus().isSuccess()) {
                        MetadataBuffer buffer = result.getMetadataBuffer();
                        Iterator<Metadata> i = buffer.iterator();
                        int filesProcessed = 0;
                        while (i.hasNext()) { // Add each DriveFile in result to file list
                            Metadata m = i.next();
                            PhraseCollection file = new PhraseCollection(m.getDriveId());
                            file.setListTitle(m.getTitle());
                            mFiles.add(file);
                            filesProcessed++;
                        }
                        buffer.close();
                        mListAdapter = new ListSelectionAdapter(c, R.layout.activity_list_selection_item, mFiles);
                        mListView = (ListView) findViewById(R.id.vocabulary_lists);
                        mListView.setOnItemClickListener(getOnItemClickListener());
                        mListView.setMultiChoiceModeListener(new ListSelectionListener(mListView, mListAdapter));
                        mListView.setAdapter(mListAdapter);
                        Utils.showToast(c, "Files found in Drive: " + filesProcessed);
                    } else {
                        Log.e(TAG, "DriveFiles not successfully retrieved.");
                        Log.e(TAG, "Status code: " + result.getStatus().getStatusCode() + " - " + "Message: " + result.getStatus().getStatusMessage());
                    }
                }
            });
	}

    private OnItemClickListener getOnItemClickListener() {
        final ListSelectionActivity c = this;
        return new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                final PhraseCollection file = mFiles.get(position);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (file.isEmpty()) {
                            Log.d(TAG, "No phrases in PhraseCollection, generating from Drive.");
                            if (file.loadCollectionDataFromDrive(c)) startListDataActivity(file);
                            else Log.e(TAG, "Error generating PhraseCollection from DriveFile.");
                        }
                        else startListDataActivity(file);
                    }
                }).start();
            }
        };
    }

    public void startListDataActivity(PhraseCollection file) {
        final Intent intent = new Intent(getBaseContext(), LoadListDataActivity.class);
        intent.putExtra("PhraseCollection", file);
        intent.putExtra("position", mFiles.indexOf(file));
        startActivity(intent);
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
						.setTitle(file.getListTitle())
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
                mFiles.add(file);
                mListAdapter.notifyDataSetChanged();
			}
		};
	}

	public void editList(final View v) {
        final ListSelectionActivity c = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				PhraseCollection file = (PhraseCollection) v.getTag();
                if (file.isEmpty()) {
                    Log.d(TAG, "No phrases in PhraseCollection, generating from Drive.");
                    if (file.loadCollectionDataFromDrive(c)) startEditActivity(file);
                    else Log.e(TAG, "Error generating PhraseCollection from DriveFile.");
                }
                else startEditActivity(file);
			}
		}).start();
	}

    public void startEditActivity(PhraseCollection file) {
        Intent intent = new Intent(this, EditListActivity.class);
        intent.putExtra("PhraseCollection", file);
        intent.putExtra("position", mFiles.indexOf(file));
        startActivityForResult(intent, REQUEST_CODE_EDIT_LIST);
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
                Log.d(TAG, "Setting PhraseCollection to position " + position + " of " + mFiles.size());
                mFiles.set(position, list); // Test if we need this
				mListAdapter.notifyDataSetChanged();
				break;
			}
			break;
			
		default:
			Log.e(TAG, "Request Code not recognized: " + requestCode);
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
