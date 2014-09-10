package com.ddiehl.android.flashcard.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ddiehl.android.flashcard.R;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PreferenceManager.setDefaultValues(this, R.xml.advanced_preferences, false);
        setContentView(R.layout.activity_main);
    }
    
    public void startAppFlow(View view) {
    	Intent i = new Intent(this, ListSelectionActivity.class);
    	startActivity(i);
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	finish();
    }
    
}
