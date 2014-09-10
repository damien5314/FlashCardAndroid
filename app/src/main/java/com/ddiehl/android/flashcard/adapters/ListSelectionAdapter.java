package com.ddiehl.android.flashcard.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ddiehl.android.flashcard.R;
import com.ddiehl.android.flashcard.fileio.FlashcardFile;

import java.util.ArrayList;

public class ListSelectionAdapter extends ArrayAdapter<FlashcardFile> {
	private static final String TAG = ListSelectionAdapter.class.getSimpleName();
	private Context context;
    private int layoutResourceId;
    private ArrayList<FlashcardFile> data = null;
    private SparseBooleanArray mSelectedItemsIds;
    
    public ListSelectionAdapter(Context context, int id, ArrayList<FlashcardFile> data) {
        super(context, id, data);
        this.layoutResourceId = id;
        this.context = context;
        this.data = data;
        this.mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            row = ((Activity)context).getLayoutInflater().inflate(layoutResourceId, parent, false);
        }

        FlashcardFile file = data.get(position);
        ItemHolder holder = new ItemHolder();
        holder.file = file;
        holder.itemText = (TextView) row.findViewById(R.id.itemText);
        holder.itemText.setText(file.getTitle());
        holder.itemEditButton = (ImageButton) row.findViewById(R.id.itemEditButton);
        holder.itemEditButton.setTag(holder.file);
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
    	FlashcardFile file;
        TextView itemText;
        ImageButton itemEditButton;
    }
}
