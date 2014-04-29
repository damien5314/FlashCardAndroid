package com.ddiehl.flashcard.util;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.Metadata;

public class QueryFilesAsyncTask extends ApiClientAsyncTask<Void, Void, Metadata> {
	private static final String TAG = QueryFilesAsyncTask.class
			.getSimpleName();

	public QueryFilesAsyncTask(Context context) {
		super(context);
	}

	@Override
	protected Metadata doInBackgroundConnected(Void... arg0) {

		// First we start by creating a new contents, and blocking on the
		// result by calling await().
		ContentsResult contentsResult =
				Drive.DriveApi.newContents(getGoogleApiClient()).await();
		if (!contentsResult.getStatus().isSuccess()) {
			// We failed, stop the task and return.
			return null;
		}
	}

	@Override
	protected void onPostExecute(Metadata result) {
		super.onPostExecute(result);
		if (result == null) {
			// The creation failed somehow, so show a message.
			Log.d(TAG, "Error while creating the file.");
			return;
		}
		// The creation succeeded, show a message.
		Log.d(TAG, "File created: " + result.getDriveId());
	}
}