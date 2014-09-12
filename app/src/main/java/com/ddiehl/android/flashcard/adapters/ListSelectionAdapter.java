package com.ddiehl.android.flashcard.adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ddiehl.android.flashcard.R;
import com.ddiehl.android.flashcard.activities.ListSelectionActivity;
import com.ddiehl.android.flashcard.quizsession.PhraseCollection;

import java.util.ArrayList;

public class ListSelectionAdapter extends ArrayAdapter<PhraseCollection> {
	private static final String TAG = ListSelectionAdapter.class.getSimpleName();
	private ListSelectionActivity context;
    private int layoutResourceId;
    private ArrayList<PhraseCollection> data = null;
    private SparseBooleanArray mSelectedItemsIds;
    
    public ListSelectionAdapter(Context context, int id, ArrayList<PhraseCollection> data) {
        super(context, id, data);
        this.layoutResourceId = id;
        this.context = (ListSelectionActivity) context;
        this.data = data;
        this.mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            row = context.getLayoutInflater().inflate(layoutResourceId, parent, false);
        }

        PhraseCollection file = data.get(position);
        ItemHolder holder = new ItemHolder();
        holder.itemText = (TextView) row.findViewById(R.id.itemText);
        holder.itemText.setText(file.getListTitle());
        holder.itemEditButton = (ImageButton) row.findViewById(R.id.itemEditButton);
        holder.itemEditButton.setTag(file);
        row.setTag(holder);

        return row;
    }
 
    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }
 
    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }
 
    public void selectView(int position, boolean value) {
        if (value) {
            mSelectedItemsIds.put(position, value);
        } else {
            mSelectedItemsIds.delete(position);
        }
        notifyDataSetChanged();
    }
 
    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }
    
    public SparseBooleanArray getSelectedIds() {
    	return this.mSelectedItemsIds;
    }
    
    static class ItemHolder
    {
        TextView itemText;
        ImageButton itemEditButton;
    }
}
