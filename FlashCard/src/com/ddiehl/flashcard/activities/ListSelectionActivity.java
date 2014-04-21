package com.ddiehl.flashcard.activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
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
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.internal.is;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

public class ListSelectionActivity extends Activity {
	private static final String TAG = ListSelectionActivity.class
			.getSimpleName();
	private ArrayList<PhraseCollection> mVocabularyLists = new ArrayList<PhraseCollection>();
	private ListSelectionAdapter mListAdapter;
	private ListView mListView;
	private static final int AUTHORIZATION_REQUEST_CODE = 1001;
	private String mAuthenticatedEmailId = null;

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

	public void syncListsToDrive() {
		if (mAuthenticatedEmailId == null) {
			authenticateUser();
		} else {
			File[] files = getFilesDir().listFiles();
			for (int i = 0; i < files.length; i++) {
				File listToUpload = files[i];
				Log.d(TAG, "Attempting to upload file: " + listToUpload.getName());
			}
		}
	}
	
	private void authenticateUser() {
		Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
		         false, null, null, null, null);
		startActivityForResult(intent, AUTHORIZATION_REQUEST_CODE);
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
		switch (requestCode) {
		case 1:
			switch (resultCode) {
			case 1:
				Bundle extras = data.getExtras();
				if (extras.containsKey("PhraseCollection")
						&& extras.containsKey("position")) {
					PhraseCollection thisCollection = (PhraseCollection) extras
							.getParcelable("PhraseCollection");
					mVocabularyLists.set(extras.getInt("position"), thisCollection);
					mListAdapter.notifyDataSetChanged();
				}
				break;
			}
		case AUTHORIZATION_REQUEST_CODE:
			switch (resultCode) {
			case RESULT_OK:
				mAuthenticatedEmailId = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				getAndUseAuthTokenInAsyncTask();
				String authenticationToken = GoogleAuthUtil.getToken(this, mAuthenticatedEmailId, DriveScopes.DRIVE);
				Drive service = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new JacksonFactory(), credential).build();
				this.syncListsToDrive();
			}
			break;
		default:
			Log.d(TAG, "Request Code not recognized.");
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
	
	// Example of how to use the GoogleAuthUtil in a blocking, non-main thread context
	private void getAndUseAuthTokenBlocking() {
	       try {
	          // Retrieve a token for the given account and scope. It will always return either
	          // a non-empty String or throw an exception.
	          final String token = GoogleAuthUtil.getToken(this, this.mAuthenticatedEmailId, DriveScopes.DRIVE_APPDATA);
	       } catch (GooglePlayServicesAvailabilityException playEx) {
	         Dialog alert = GooglePlayServicesUtil.getErrorDialog(
	             playEx.getConnectionStatusCode(),
	             this,
	             AUTHORIZATION_REQUEST_CODE);
	         alert.show();
	         return;
	       } catch (UserRecoverableAuthException userAuthEx) {
	          // Start the user recoverable action using the intent returned by getIntent()
	          startActivityForResult(
	                  userAuthEx.getIntent(),
	                  AUTHORIZATION_REQUEST_CODE);
	          return;
	       } catch (IOException transientEx) {
	          // network or server error, the call is expected to succeed if you try again later.
	          // Don't attempt to call again immediately - the request is likely to
	          // fail, you'll hit quotas or back-off.
	    	   Log.i(TAG, "Transient error encountered: " + transientEx.getMessage());
	    	   return;
	       } catch (GoogleAuthException authEx) {
	          // Failure. The call is not expected to ever succeed so it should not be retried.
	    	  Log.e(TAG, "Unrecoverable authentication exception: " + authEx.getMessage(), authEx);
	    	  return;
	       }
	       
	       // Attempt to make test API call here
	   }

	// Example of how to use AsyncTask to call blocking code on a background thread.
	void getAndUseAuthTokenInAsyncTask() {
		AsyncTask task = new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {
				getAndUseAuthTokenBlocking();
				return null;
			}
		};
		task.execute((Void) null);
	}
}
