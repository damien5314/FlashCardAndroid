package com.ddiehl.flashcard.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.quizsession.Sentence;

public class EditPhraseSentenceAdapter extends ArrayAdapter<Sentence> {
	Context context; 
    int layoutResourceId;
    ArrayList<Sentence> data = null;

    public EditPhraseSentenceAdapter(Context context, int layoutResourceId, ArrayList<Sentence> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
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
            holder.itemText = (TextView) row.findViewById(R.id.itemText);
            row.setTag(holder);
        } else {
            holder = (ItemHolder) row.getTag();
        }
        
        holder.itemText.setText(s.getSentenceNative());
        
        return row;
    }
    
    static class ItemHolder
    {
        TextView itemText;
    }
}