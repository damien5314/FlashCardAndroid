package com.ddiehl.flashcard.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ddiehl.flashcard.R;

public class DeleteListDialog extends DialogFragment {
	private static final String TAG = "DeleteListDialog";
	
	public DeleteListDialog() {
		// Empty constructor required
	}
	
	public static DeleteListDialog newInstance() {
		DeleteListDialog frag = new DeleteListDialog();
		Bundle args = new Bundle();
		frag.setArguments(args);
		return frag;
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_delete_list, null);
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
