package com.ddiehl.flashcard.adapters;

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
import com.ddiehl.flashcard.quizsession.Phrase;
import com.ddiehl.flashcard.quizsession.PhraseCollection;

public class EditListPhrasesAdapter extends ArrayAdapter<Phrase> {
	private static final String TAG = EditListPhrasesAdapter.class.getSimpleName();
	private Context context; 
    private int layoutResourceId;
    private PhraseCollection data = null;
    private SparseBooleanArray mSelectedItemsIds;
    
    public EditListPhrasesAdapter(Context context, int layoutResourceId, PhraseCollection data) {
        super(context, layoutResourceId, data.getList());
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PhraseHolder holder = null;
        Phrase p = data.get(position);
        
        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new PhraseHolder();
            holder.itemLabel = (ImageView) row.findViewById(R.id.itemLabel);
            holder.itemText = (TextView) row.findViewById(R.id.itemText);
            row.setTag(holder);
        } else {
            holder = (PhraseHolder) row.getTag();
        }

        String imageResourceName = "ic_item_" + ( String.format("%02d", position+1));
        int imageResourceId = context.getResources().getIdentifier(imageResourceName, "drawable", context.getPackageName());
        
        holder.itemLabel.setImageResource(imageResourceId);
        
        if (data.get(position).hasNativeText())
        	holder.itemText.setText(p.getPhraseNative());
        else
        	holder.itemText.setText(p.getPhrasePhonetic());
        
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
    
    static class PhraseHolder
    {
        ImageView itemLabel;
        TextView itemText;
    }
}
