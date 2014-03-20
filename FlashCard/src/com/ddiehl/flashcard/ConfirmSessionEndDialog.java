package com.ddiehl.flashcard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ConfirmSessionEndDialog extends DialogFragment {
	private static final String TAG = "ConfirmSessionEndDialog";
	
	public ConfirmSessionEndDialog() {
		// Empty constructor required
	}
	
	public static ConfirmSessionEndDialog newInstance() {
		ConfirmSessionEndDialog frag = new ConfirmSessionEndDialog();
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_confirm_end_session, null);
        Button vCancel = (Button) v.findViewById(R.id.dialog_cancel);
        vCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
        });
        builder.setView(v);
        return builder.create();
	}
}
