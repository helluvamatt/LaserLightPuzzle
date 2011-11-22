package com.schneenet.android.lasers;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.schneenet.android.lasers.levels.AsyncMultiLevelLoader;
import com.schneenet.android.lasers.levels.LaserLightPuzzleLevel;
import com.schneenet.android.lasers.levels.LevelAdapter;

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

		// Prepare loading animation window
		mLoadingDialog = new ProgressDialog(this);
		mLoadingDialog.setCancelable(false);
		mLoadingDialog.setTitle(R.string.loading);
		mLoadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mLoadingDialog.setIndeterminate(true);

		mLevelAdapter = new LevelAdapter(this);
		AsyncMultiLevelLoader loader = new AsyncMultiLevelLoader(this, new AsyncMultiLevelLoader.MultiLevelLoaderListener() {
			@Override
			public void onProgress(boolean indeterminate, int progress, int maxProgress) {
				// Don't care
			}

			@Override
			public void onLevelsLoaded(ArrayList<LaserLightPuzzleLevel> levels) {
				int n = levels.size();
				for (int i = 0; i < n; i++) {
					mLevelAdapter.add(levels.get(i));
				}
				setLoadingAnimationVisible(false);
			}

			@Override
			public void onError(String message, Exception ex) {
				onError(message, ex);
			}
		});
		setLoadingAnimationVisible(true);
		loader.execute();
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
	private LevelAdapter mLevelAdapter;
	private ProgressDialog mLoadingDialog;

	@Override
	public void onQuit() {
		finish();
	}

	@Override
	public void onLevelSelect() {
		// Level selection dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.level_select_dialog_title);
		builder.setAdapter(mLevelAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int position) {
				puzzleView.loadLevel(mLevelAdapter.get(position));
			}
		});
		builder.show();
	}

	@Override
	public void setLoadingAnimationVisible(boolean show) {
		// Show() or hide() the dialog
		if (show) {
			mLoadingDialog.show();
		} else {
			mLoadingDialog.hide();
		}
	}

	@Override
	public void onError(String msg, Exception ex) {
		Toast.makeText(LaserLightPuzzleActivity.this, "Error: " + msg, Toast.LENGTH_LONG).show();
		Log.e("LaserLightPuzzle", "onError(): " + msg, ex);
	}

}