package com.ddiehl.flashcard.fileio;

import com.ddiehl.flashcard.quizsession.PhraseCollection;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveFile;

public class FlashcardFile {
	
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
	
	public PhraseCollection generatePhraseCollectionFromDriveFile(GoogleApiClient client) {
		DriveFile driveFile = getDriveFile();
		// Retrieve Contents from DriveFile
		driveFile.openContents(client, DriveFile.MODE_READ_ONLY, new DriveFile.DownloadProgressListener() {
			@Override
			public void onProgress(long arg0, long arg1) {
				// Report download progress here
			}
		}).setResultCallback(new ResultCallback<ContentsResult>() {
			@Override
			public void onResult(ContentsResult result) {
				
			}
			
		});
		// Return new PhraseCollection created from Contents
		
		return null;
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
