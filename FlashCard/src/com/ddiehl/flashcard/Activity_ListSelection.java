package com.ddiehl.flashcard;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
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
		String[] list_filenames = null;
		try {
			list_filenames = assets.list(getString(R.string.assetListGroup));
		} catch (IOException e) {
			Log.e(TAG, "Error retrieving assets.");
			e.printStackTrace();
		}
		String[] list_titles = new String[list_filenames.length];
		for (int i = 0; i < list_filenames.length; i++) {
			InputStream thisList;
			try {
				thisList = assets.open(getString(R.string.assetListGroup) + "/" + list_filenames[i]);
			} catch (IOException e) {
				thisList = null;
				Log.e(TAG, "Error opening asset.");
				e.printStackTrace();
			}
	        ListInfo info = new ListInfo(thisList);
	        list_titles[i] = info.getTitle();
		}
		ArrayAdapter<String> adapter = 
				new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_titles);
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
	
	private class ListInfo {
        private String title = null;
        
		public ListInfo(InputStream vocabulary) {
			super();
			XmlPullParser parser = Xml.newPullParser();
	        try {
				parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		        parser.setInput(vocabulary, null);
			} catch (Exception e) {
				Log.e(TAG, "Error initializing XmlPullParser");
				//e.printStackTrace();
			}
	        
			try {
				parseXML(parser);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void parseXML(XmlPullParser parser) throws XmlPullParserException, IOException
		{
	        int eventType = parser.getEventType();

	        while (eventType != XmlPullParser.END_DOCUMENT) {
	            String name = null;
	            switch (eventType) {
	                case XmlPullParser.START_DOCUMENT:
	                    break;
	                case XmlPullParser.START_TAG:
	                    name = parser.getName();
	                    if (name.equalsIgnoreCase("title")) {
	                    	setTitle(parser.nextText());
	                    }
	                    break;
	                case XmlPullParser.END_TAG:
	                    break;
	            }
	            eventType = parser.next();
	        }
		}
		
		private void setTitle(String in) {
			title = in;
		}
		
		private String getTitle() {
			return title;
		}
	}

}
