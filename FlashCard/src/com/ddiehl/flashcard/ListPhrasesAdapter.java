package com.ddiehl.flashcard;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListPhrasesAdapter extends ArrayAdapter<Phrase> {

	Context context; 
    int layoutResourceId;    
    List<Phrase> data = null;
    
    public ListPhrasesAdapter(Context context, int layoutResourceId, PhraseCollection data) {
        super(context, layoutResourceId, data);
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
        
        holder.itemText.setText(p.getPhraseNative());
        holder.itemImage.setImageResource(R.drawable.phrase_play);
        
        return row;
    }
    
    static class PhraseHolder
    {
        ImageView itemImage;
        TextView itemText;
    }

}
