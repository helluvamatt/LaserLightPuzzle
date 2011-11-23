package com.schneenet.android.lasers.levels;

import java.util.ArrayList;

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
	
	public String getName() {
		return mName;
	}
	
	public String getAuthor() {
		return mAuthor;
	}
	
	public int getDifficulty() {
		return mDifficulty;
	}
	
	public LaserLightPuzzleLevel clone() {
		LaserLightPuzzleLevel clone = new LaserLightPuzzleLevel();
		clone.mName = this.mName;
		clone.mDifficulty = this.mDifficulty;
		clone.mAuthor = this.mAuthor;
		int n = this.gameObjects.size();
		for (int i = 0; i < n; i++) {
			clone.gameObjects.add(this.gameObjects.get(i).clone());
		}
		return clone;
	}
	
	String mName;
	String mAuthor;
	int mDifficulty;
	ArrayList<GameObjectRenderable> gameObjects;
	
}