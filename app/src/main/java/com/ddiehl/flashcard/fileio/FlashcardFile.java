package com.ddiehl.flashcard.fileio;

import android.util.Log;

import com.ddiehl.flashcard.quizsession.PhraseCollection;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FlashcardFile {
	private static final String TAG = FlashcardFile.class.getSimpleName();
	private String mTitle;
	private DriveFile mDriveFile;
	private static final String NEW_LIST_TITLE = "New List";

	public FlashcardFile() {
		setTitle(NEW_LIST_TITLE);
		setDriveFile(null);
	}
	
	public FlashcardFile(String title) {
		setTitle(title);
		setDriveFile(null);
	}
	
	public FlashcardFile(DriveFile file) {
		setTitle(NEW_LIST_TITLE);
		setDriveFile(file);
	}
	
	public FlashcardFile(String title, DriveFile file) {
		setTitle(title);
		setDriveFile(file);
	}
	
	public void updateContents(final GoogleApiClient client, final PhraseCollection list) {
		final DriveFile file = getDriveFile();
        // Update metadata with correct filename
        MetadataChangeSet cs = new MetadataChangeSet.Builder().setTitle(list.getTitle()).build();
        PendingResult<DriveResource.MetadataResult> result = file.updateMetadata(client, cs);
        result.setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
            @Override
            public void onResult(DriveResource.MetadataResult metadataResult) {
                Log.d(TAG, "Updated file metadata successfully.");
            }
        });
		new Thread(new Runnable() {
			@Override
			public void run() {
				Contents contents = file.openContents(client, DriveFile.MODE_WRITE_ONLY, new DriveFile.DownloadProgressListener() {
					@Override
					public void onProgress(long arg0, long arg1) {
						
					}
				}).await().getContents();
				try {
					OutputStream f_out = contents.getOutputStream();
					if (list.getContents() != null) {
						f_out.write(list.getContents().getBytes());
						file.commitAndCloseContents(client, contents);	
					} else {
						Log.e(TAG, "Contents of PhraseCollection are empty, did you save?");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}
	
	public PhraseCollection generatePhraseCollectionFromDriveFile(GoogleApiClient client) {
		DriveFile driveFile = getDriveFile();
		// Retrieve Contents from DriveFile
		ContentsResult result = driveFile.openContents(client, DriveFile.MODE_READ_ONLY, new DriveFile.DownloadProgressListener() {
			@Override
			public void onProgress(long arg0, long arg1) {
				// Report download progress here
			}
		}).await();
		Contents contents = result.getContents();
		// Return new PhraseCollection created from Contents
		InputStream f_in = contents.getInputStream();
		return new PhraseCollection(f_in);
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public DriveFile getDriveFile() {
		return mDriveFile;
	}

	public void setDriveFile(DriveFile file) {
		this.mDriveFile = file;
	}

}
