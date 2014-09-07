package com.ddiehl.flashcard.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.quizsession.Sentence;

public class EditPhraseSentenceAdapter extends ArrayAdapter<Sentence> {
	private static final String TAG = EditPhraseSentenceAdapter.class.getSimpleName();
	private Context context; 
    private int layoutResourceId;
    private ArrayList<Sentence> data = null;
    private SparseBooleanArray mSelectedItemsIds;

    public EditPhraseSentenceAdapter(Context context, int layoutResourceId, ArrayList<Sentence> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemHolder holder = null;
        Sentence s = data.get(position);
        
        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new ItemHolder();
            holder.itemLabel = (ImageView) row.findViewById(R.id.itemLabel);
            holder.itemText = (TextView) row.findViewById(R.id.itemText);
            row.setTag(holder);
        } else {
            holder = (ItemHolder) row.getTag();
        }
        
        String imageResourceName = "ic_item_" + ( String.format("%02d", position+1));
        int imageResourceId = context.getResources().getIdentifier(imageResourceName, "drawable", context.getPackageName());
        
        holder.itemLabel.setImageResource(imageResourceId);
        holder.itemText.setText(s.getSentenceNative());
        
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
    	ImageView itemLabel;
        TextView itemText;
    }
}
