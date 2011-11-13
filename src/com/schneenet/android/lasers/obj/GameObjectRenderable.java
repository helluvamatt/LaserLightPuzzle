package com.schneenet.android.lasers.obj;

import android.graphics.Canvas;
import android.graphics.PointF;

import com.schneenet.android.lasers.LaserLightPuzzleView;

public abstract class GameObjectRenderable {
	public GameObjectRenderable(float x, float y) {
		cX = x;
		cY = y;
	}

	public float getX() {
		return cX;
	}

	public void setX(float newValue) {
		if (this.isMoveable())
			cX = newValue;
	}

	public float getY() {
		return cY;
	}

	public void setY(float newValue) {
		if (this.isMoveable())
			cY = newValue;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rot) {
		if (this.isRotatable())
			rotation = rot;
	}

	public abstract boolean isMoveable();
	public abstract boolean isSelectable();
	public abstract boolean isRotatable();

	public abstract void draw(Canvas c);
	
	public final PointF getCanvasPointF() {
		return LaserLightPuzzleView.translateCoordinatePointToCanvas(cX, cY);
	}

	protected float cX;
	protected float cY;
	protected float rotation;
}
