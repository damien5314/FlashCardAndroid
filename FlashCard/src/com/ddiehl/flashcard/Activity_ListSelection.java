package com.ddiehl.flashcard;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Activity_ListSelection extends Activity {
	private static final String TAG = "Activity_ListSelection";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_selection);
		
		AssetManager assets = getAssets();
		String[] list = null;
		try {
			list = assets.list(getString(R.string.assetListGroup));
		} catch (IOException e) {
			Log.e(TAG, "Error retrieving assets.");
			e.printStackTrace();
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
		ListView vLists = (ListView) findViewById(R.id.vocabulary_lists);
		vLists.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent(getBaseContext(), Activity_LoadListData.class);
				intent.putExtra("listnumber", position);
				view.getContext().startActivity(intent);
			}
			
		});
		vLists.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void loadListData(View view) {
		Intent i = new Intent(this, Activity_LoadListData.class);
		startActivity(i);
	}

}
