package com.schneenet.android.lasers.levels;

import java.io.IOException;
import java.util.ArrayList;

import org.xml.sax.InputSource;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncMultiLevelLoader extends AsyncTask<Void, Integer, ArrayList<LaserLightPuzzleLevel>> {

	public AsyncMultiLevelLoader(Context ctxt, MultiLevelLoaderListener l) {
		mContext = ctxt;
		mListener = l;
	}
	
	@Override
	protected ArrayList<LaserLightPuzzleLevel> doInBackground(Void... params) {
		ArrayList<LaserLightPuzzleLevel> levels = new ArrayList<LaserLightPuzzleLevel>();
		try {
			publishProgress(0);
			LevelFactory.LevelParser parser = new LevelFactory.LevelParser(new LevelFactory.LevelParser.ParserListener() {
				@Override
				public void onError(String msg, Exception e) {
					errMessage = msg;
					errException = e;
					publishProgress(-1);
				}
			});
			String[] levelFiles = mContext.getAssets().list("levels");
			int n = levelFiles.length;
			for (int i = 0; i < n; i++) {
				publishProgress(i, n);
				if (levelFiles[i].endsWith(".xml")) {
					InputSource is = new InputSource(mContext.getAssets().open("levels/" + levelFiles[i]));
					levels.add(parser.loadLevel(is));
				} else {
					Log.e("AsyncMultiLevelLoader", "Not an XML file: " + levelFiles[i]);
				}
			}
			publishProgress(n, n);
		} catch (IOException ex) {
			errMessage = ex.getLocalizedMessage();
			errException = ex;
			publishProgress(-1);
		}
		
		return levels;
	}
	
	@Override
	protected void onProgressUpdate(Integer... progress) {
		if (progress.length > 0) {
			if (progress[0] < 0) {
				mListener.onError(errMessage, errException);
			} else if (progress.length > 1) {
				mListener.onProgress(false, progress[0], progress[1]);
			} else if (progress[0] == 0) {
				mListener.onProgress(true, 0, 0);
			}
		}
	}
	
	@Override
	protected void onPostExecute(ArrayList<LaserLightPuzzleLevel> result) {
		mListener.onLevelsLoaded(result);
	}
	
	private Context mContext;
	private MultiLevelLoaderListener mListener;
	
	private String errMessage;
	private Exception errException;
	
	public interface MultiLevelLoaderListener {
		public void onLevelsLoaded(ArrayList<LaserLightPuzzleLevel> levels);
		public void onProgress(boolean indeterminate, int progress, int maxProgress);
		public void onError(String message, Exception ex);
	}
	
}
