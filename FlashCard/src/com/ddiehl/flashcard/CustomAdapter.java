package com.ddiehl.flashcard;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<Quiz> {

	Context context; 
    int layoutResourceId;    
    QuizCollection data = null;
    
    public CustomAdapter(Context context, int layoutResourceId, QuizCollection data) {
        super(context, layoutResourceId, data.getList());
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        QuizHolder holder = null;
        Quiz q = data.get(position);
        
        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new QuizHolder();
            holder.itemText = (TextView) row.findViewById(R.id.itemText);
        	holder.itemImage = (ImageView) row.findViewById(R.id.itemImage);
            row.setTag(holder);
        } else {
            holder = (QuizHolder) row.getTag();
        }
        
        holder.itemText.setText(q.getQuizPhrase().getPhraseNative());
        if (q.getPotentialScore() == q.getActualScore())
        	holder.itemImage.setImageResource(R.drawable.answer_correct);
        else
        	holder.itemImage.setImageResource(R.drawable.answer_incorrect);
        
        return row;
    }
    
    static class QuizHolder
    {
        ImageView itemImage;
        TextView itemText;
    }

}