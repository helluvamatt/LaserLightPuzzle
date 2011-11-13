package com.schneenet.android.lasers.levels;

import java.util.ArrayList;

import com.schneenet.android.lasers.obj.GameObjectRenderable;

public class LaserLightPuzzleLevel {

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
		return name;
	}
	
	public String getAuthor() {
		return author;
	}

	public int getDifficulty() {
		return difficulty;
	}

	ArrayList<GameObjectRenderable> gameObjects;
	String name;
	String author;
	int difficulty;
}