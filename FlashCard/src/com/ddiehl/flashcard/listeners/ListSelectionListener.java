package com.ddiehl.flashcard.listeners;

import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.adapters.ListSelectionAdapter;
import com.ddiehl.flashcard.quizsession.PhraseCollection;

public class ListSelectionListener implements MultiChoiceModeListener {
	private static final String TAG = "ListSelectionListener";
	private ListSelectionAdapter mAdapter;
	private ListView mListView;
	
	public ListSelectionListener(ListView view, ListSelectionAdapter adapter) {
		super();
		mListView = view;
		mAdapter = adapter;
	}
	
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_delete:
            SparseBooleanArray selected = mAdapter.getSelectedIds();
            for (int i = (selected.size() - 1); i >= 0; i--) {
                if (selected.valueAt(i)) {
                    PhraseCollection selecteditem = mAdapter.getItem(selected.keyAt(i));
                    mAdapter.remove(selecteditem);
                }
            }
            mode.finish();
            return true;
        default:
        	return false;
        }
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.list_selection_context, menu);
        return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode arg0) {
		mAdapter.removeSelection();
	}

	@Override
	public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
		// Here you can perform updates to the CAB due to
        // an invalidate() request
        return false;
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode,
			int position, long id, boolean checked) {
        final int checkedCount = mListView.getCheckedItemCount();
        mode.setTitle(checkedCount + " Selected");
        mAdapter.toggleSelection(position);
	}
}
