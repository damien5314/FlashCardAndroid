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

public class ListPhrasesAdapter extends ArrayAdapter<Phrase> {
	Context context; 
    int layoutResourceId;
    PhraseCollection data = null;
    
    public ListPhrasesAdapter(Context context, int layoutResourceId, PhraseCollection data) {
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
        	holder.itemImage = (ImageView) row.findViewById(R.id.itemImage);
            row.setTag(holder);
        } else {
            holder = (PhraseHolder) row.getTag();
        }
        
        if (data.get(position).hasNativeText())
        	holder.itemText.setText(p.getPhraseNative());
        else
        	holder.itemText.setText(p.getPhrasePhonetic());
        holder.itemImage.setImageResource( ((p.isIncludedInSession()) ? R.drawable.phrase_play : R.drawable.phrase_pause));
        
        return row;
    }
    
    static class PhraseHolder
    {
        ImageView itemImage;
        TextView itemText;
    }

}
