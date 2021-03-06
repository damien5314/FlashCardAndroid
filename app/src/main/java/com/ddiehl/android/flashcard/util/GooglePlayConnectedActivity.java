package com.ddiehl.android.flashcard.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;

public abstract class GooglePlayConnectedActivity extends Activity implements
		ConnectionCallbacks, OnConnectionFailedListener {
	private static final String TAG = GooglePlayConnectedActivity.class
			.getSimpleName();
	private static final int REQUEST_RESOLVE_ERROR = 1001;
	private static final String STATE_RESOLVING_ERROR = "resolving_error";
	private static final String DIALOG_ERROR = "dialog_error";
	private boolean mResolvingError = false;
	protected GoogleApiClient mClient = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mResolvingError = savedInstanceState != null
				&& savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
		mClient = new GoogleApiClient.Builder(this)
				.addApi(Drive.API)
				.addScope(Drive.SCOPE_APPFOLDER)
				.addScope(Drive.SCOPE_FILE)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (!mResolvingError)
			mClient.connect();
	}

	@Override
	protected void onStop() {
		mClient.disconnect();
		super.onStop();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
	}

	@Override
	public void onConnected(Bundle connectionHint) {

	}

	@Override
	public void onConnectionSuspended(int cause) {
		// The connection has been interrupted.
		// Disable any UI components that depend on Google APIs
		// until onConnected() is called.
		Log.i(TAG, "Connection to Google Play services has been suspended. Cause: " + cause);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i(TAG, "Failed to connect to Google Play services.");
		if (mResolvingError) {
			// Already attempting to resolve an error.
			return;
		} else if (result.hasResolution()) {
			try {
				mResolvingError = true;
				result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
			} catch (SendIntentException e) {
				// There was an error with the resolution intent. Try again.
				mClient.connect();
			}
		} else {
			showErrorDialog(result.getErrorCode());
			mResolvingError = true;
		}
	}

	/* Creates a dialog for an error message */
	private void showErrorDialog(int errorCode) {
		// Create a fragment for the error dialog
		ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
		// Pass the error that should be displayed
		Bundle args = new Bundle();
		args.putInt(DIALOG_ERROR, errorCode);
		dialogFragment.setArguments(args);
		dialogFragment.show(getFragmentManager(), "errordialog");
	}

	public void onDialogDismissed() {
		mResolvingError = false;
	}

	/* A fragment to display an error dialog */
	public static class ErrorDialogFragment extends DialogFragment {
		public ErrorDialogFragment() {

		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Get the error code and retrieve the appropriate dialog
			int errorCode = this.getArguments().getInt(DIALOG_ERROR);
			return GooglePlayServicesUtil.getErrorDialog(errorCode,
					this.getActivity(), REQUEST_RESOLVE_ERROR);
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			((GooglePlayConnectedActivity) getActivity()).onDialogDismissed();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_RESOLVE_ERROR:
			mResolvingError = false;
			switch (resultCode) {
			case RESULT_OK:
				if (!mClient.isConnecting() && !mClient.isConnected()) {
					mClient.connect();
				}
				break;
			}
			break;
		}
	}

	public GoogleApiClient getGoogleApiClient() {
		return this.mClient;
	}
}
