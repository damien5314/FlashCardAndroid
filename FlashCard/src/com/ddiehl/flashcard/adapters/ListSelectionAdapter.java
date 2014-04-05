package com.ddiehl.flashcard.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.activities.EditListActivity;
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
        ListHolder holder = null;
        final int thisPosition = position;
        PhraseCollection pc = data.get(thisPosition);
        
        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new ListHolder();
            holder.itemText = (TextView) row.findViewById(R.id.itemText);
            holder.itemEditButton = (ImageButton) row.findViewById(R.id.itemEditButton);
            row.setTag(holder);
        } else {
            holder = (ListHolder) row.getTag();
        }
        
        holder.itemText.setText(pc.getTitle());
//		holder.itemEditButton.setOnClickListener(pc.getEditListener());
        holder.itemEditButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Context ctx = v.getContext();
				Intent intent = new Intent(ctx, EditListActivity.class);
				intent.putExtra("PhraseCollection", data.get(thisPosition));
				ctx.startActivity(intent);
			}
        });
        
        return row;
    }
    
    static class ListHolder
    {
        TextView itemText;
        ImageButton itemEditButton;
    }
}
