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
import com.ddiehl.flashcard.quizsession.ListInfo;

public class ListSelectionAdapter extends ArrayAdapter<ListInfo> {
	Context context; 
    int layoutResourceId;
    ArrayList<ListInfo> data = null;
    
    public ListSelectionAdapter(Context context, int layoutResourceId, ArrayList<ListInfo> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ListHolder holder = null;
        ListInfo info = data.get(position);
        
        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new ListHolder();
            holder.itemText = (TextView) row.findViewById(R.id.itemText);
            row.setTag(holder);
        } else {
            holder = (ListHolder) row.getTag();
        }
        
        holder.itemText.setText(info.getTitle());
        
        return row;
    }
    
    static class ListHolder
    {
        TextView itemText;
    }
}
