package com.ddiehl.flashcard.adapters;

import android.app.Activity;
import android.content.Context;
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
	Context context; 
    int layoutResourceId;
    PhraseCollection data = null;
    
    public EditListPhrasesAdapter(Context context, int layoutResourceId, PhraseCollection data) {
        super(context, layoutResourceId, data.getList());
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
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
            holder.itemText = (TextView) row.findViewById(R.id.itemText);
            row.setTag(holder);
        } else {
            holder = (PhraseHolder) row.getTag();
        }

        if (data.get(position).hasNativeText())
        	holder.itemText.setText(p.getPhraseNative());
        else
        	holder.itemText.setText(p.getPhrasePhonetic());
        
        return row;
    }
    
    static class PhraseHolder
    {
        ImageView itemImage;
        TextView itemText;
    }

}
