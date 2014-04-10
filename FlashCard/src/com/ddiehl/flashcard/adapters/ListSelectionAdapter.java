package com.ddiehl.flashcard.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.quizsession.PhraseCollection;

public class ListSelectionAdapter extends ArrayAdapter<PhraseCollection> {
	private static final String TAG = "ListSelectionAdapter";
	Context context; 
    int layoutResourceId;
    ArrayList<PhraseCollection> data = null;
    
    public ListSelectionAdapter(Context context, int layoutResourceId, ArrayList<PhraseCollection> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemHolder holder = null;
        PhraseCollection pc = data.get(position);

        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);
        
        holder = new ItemHolder();
        holder.phraseCollection = pc;
        holder.itemText = (TextView) row.findViewById(R.id.itemText);
        holder.itemEditButton = (ImageButton) row.findViewById(R.id.itemEditButton);
        holder.itemEditButton.setTag(holder.phraseCollection);
        row.setTag(holder);

        holder.itemText.setText(pc.getTitle());
        
        return row;
    }
    
    static class ItemHolder
    {
    	PhraseCollection phraseCollection;
        TextView itemText;
        ImageButton itemEditButton;
    }
}
