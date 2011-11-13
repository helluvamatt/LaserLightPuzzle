package com.schneenet.android.lasers;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class LaserLightPuzzleActivity extends Activity implements LaserLightPuzzleView.GameListener {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		puzzleView = new LaserLightPuzzleView(this);
		puzzleView.setGameListener(this);
		puzzleView.setKeepScreenOn(true);
		setContentView(puzzleView);
		puzzleView.initialize();
	}

	public void onPause() {
		super.onPause();
		puzzleView.pause();
	}

	public void onResume() {
		super.onResume();
		puzzleView.resume();
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		puzzleView.menu();
		return false;
	}

	public void onBackPressed() {
		puzzleView.back();
	}

	private LaserLightPuzzleView puzzleView;

	@Override
	public void onQuit() {
		finish();
	}

	@Override
	public void onLevelSelect() {
		// TODO Level selection dialog
	}

}