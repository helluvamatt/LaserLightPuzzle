package com.schneenet.android.lasers.obj;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class LightSource extends GameObjectRenderable {

	public LightSource(float x, float y, boolean moveable, boolean rotatable, float initialRotation, int laserColor) {
		super(x, y);
		mMoveable = moveable;
		mRotatable = rotatable;
		mLaserColor = laserColor;
		rotation = initialRotation;
		
		mPaint = new Paint();
		mPaint.setColor(Color.GRAY);
		mPaint.setAntiAlias(true);
		
		mPaintInd = new Paint();
		mPaintInd.setColor(laserColor);
		mPaintInd.setAntiAlias(true);
	}

	public boolean isMoveable() {
		return mMoveable;
	}
	
	public boolean isRotatable() {
		return mRotatable;
	}
	
	public boolean isSelectable() {
		return mRotatable || mMoveable;
	}

	public void draw(Canvas c) {
		// Get our canvas center point
		PointF cP = getCanvasPointF();
		
		// Save current matrix
		c.save();
		
		// Rotate canvas
		c.rotate(rotation, cP.x, cP.y);

		// Draw laser
		c.drawRect(cP.x - 5, cP.y - 50, cP.x + 5, cP.y, mPaint);
		c.drawCircle(cP.x, cP.y, 25, mPaint);
		c.drawCircle(cP.x, cP.y, 20, mPaintInd);
		
		// Restore current matrix
		c.restore();
	}
	
	public int getLaserColor() {
		return mLaserColor;
	}

	private boolean mMoveable;
	private boolean mRotatable;
	
	private Paint mPaint;
	private Paint mPaintInd;
	private int mLaserColor;
	
	public static final int LASER_COLOR_INFRARED = 0x22FF0000;
	public static final int LASER_COLOR_GREEN = 0xFF00FF00;
	public static final int LASER_COLOR_ULTRAVIOLET = 0xFFAAAAFF;

}
