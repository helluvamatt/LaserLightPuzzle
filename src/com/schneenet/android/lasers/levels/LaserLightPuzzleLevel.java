package com.schneenet.android.lasers.levels;

import java.util.ArrayList;

import android.content.Context;
import android.preference.PreferenceManager;

import com.schneenet.android.lasers.obj.GameObjectRenderable;

public class LaserLightPuzzleLevel implements Cloneable {

	protected LaserLightPuzzleLevel() {
		gameObjects = new ArrayList<GameObjectRenderable>();
	}

	public int getGameObjectCount() {
		return gameObjects.size();
	}

	public GameObjectRenderable getGameObject(int i) {
		final GameObjectRenderable obj = gameObjects.get(i);
		return obj;
	}
	
	public String getId() {
		return mId;
	}
	
	public String getName() {
		return mName;
	}
	
	public String getAuthor() {
		return mAuthor;
	}
	
	public int getDifficulty() {
		return mDifficulty;
	}
	
	public long getHighScore(Context ctxt) {
		return PreferenceManager.getDefaultSharedPreferences(ctxt).getLong("highscore." + mId, -1);
	}
	
	public LaserLightPuzzleLevel clone() {
		LaserLightPuzzleLevel clone = new LaserLightPuzzleLevel();
		clone.mId = this.mId;
		clone.mName = this.mName;
		clone.mDifficulty = this.mDifficulty;
		clone.mAuthor = this.mAuthor;
		int n = this.gameObjects.size();
		for (int i = 0; i < n; i++) {
			clone.gameObjects.add(this.gameObjects.get(i).clone());
		}
		return clone;
	}
	
	String mId;
	String mName;
	String mAuthor;
	int mDifficulty;
	ArrayList<GameObjectRenderable> gameObjects;
	
}