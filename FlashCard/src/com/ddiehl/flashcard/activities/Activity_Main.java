package com.ddiehl.flashcard.activities;

import com.ddiehl.flashcard.R;
import com.ddiehl.flashcard.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Activity_Main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
    }
    
    public void startAppFlow(View view) {
    	Intent i = new Intent(this, Activity_ListSelection.class);
    	startActivity(i);
    }
    
}
