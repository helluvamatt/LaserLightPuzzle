package com.schneenet.android.lasers.obj;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class Mirror extends GameObjectRenderable implements Targetable {

	public Mirror(float x, float y, boolean moveable, boolean rotatable, float initialRotation, float mirrorLength) {
		super(x, y);
		mPaint = new Paint();
		mPaint.setStrokeWidth(5);
		mPaint.setAntiAlias(true);
		mMirrorLength = mirrorLength;
		mMoveable = moveable;
		mRotatable = rotatable;
		rotation = initialRotation;
	}
	
	public void setRequiresColor(boolean requires, int color) {
		mRequiresColor = requires;
		mRequiredColor = color;
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

	@Override
	public void draw(Canvas c) {
		// Determine endpoints for the line
		float primaryEndpointOffsetX = (float) Math.sin(Math.toRadians(rotation)) * (mMirrorLength / 2);
		float primaryEndpointOffsetY = (float) Math.cos(Math.toRadians(rotation)) * (mMirrorLength / 2);
		float secondaryEndpointOffsetX = (float) Math.sin(Math.toRadians(rotation + 180)) * (mMirrorLength / 2);
		float secondaryEndpointOffsetY = (float) Math.cos(Math.toRadians(rotation + 180)) * (mMirrorLength / 2);
		PointF cP = getCanvasPointF();
		mPrimaryEndpoint = new PointF(cP.x + primaryEndpointOffsetX, cP.y - primaryEndpointOffsetY);
		mSecondaryEndpoint = new PointF(cP.x + secondaryEndpointOffsetX, cP.y - secondaryEndpointOffsetY);

		// Draw mirror
		mPaint.setColor(mRequiresColor ? mRequiredColor : Color.WHITE);
		c.drawLine(mPrimaryEndpoint.x, mPrimaryEndpoint.y, mSecondaryEndpoint.x, mSecondaryEndpoint.y, mPaint);
	}

	public PointF getTargetPointPrimary(float srcAngle) {
		// srcAngle is ignored
		return mPrimaryEndpoint != null ? mPrimaryEndpoint : new PointF(0, 0);
	}

	public PointF getTargetPointSecondary(float srcAngle) {
		// srcAngle is ignored
		// May God have mercy upon my soul.
		return mSecondaryEndpoint != null ? mSecondaryEndpoint : new PointF(0, 0);
	}

	public boolean isReflecting(int color) {
		return !mRequiresColor || color == mRequiredColor;
	}

	public float getReflectionAngle(float srcAngle) {

		// Bound mRotation to [0,180]
		float mRot = rotation;
		while (mRot >= 180)
			mRot -= 360;
		while (mRot < -180)
			mRot += 360;

		float attack = 180 - mRot - (360 - srcAngle);
		return mRot + 180 - attack;
	}

	public void setLit(boolean lit) {
		// Don't care
	}
	
	@Override
	public boolean getRequiresColor() {
		return mRequiresColor;
	}

	@Override
	public int getRequiredColor() {
		return mRequiredColor;
	}
	
	private boolean mRequiresColor = false;
	private int mRequiredColor = 0;
	
	private boolean mMoveable;
	private boolean mRotatable;
	
	private float mMirrorLength;
	private PointF mPrimaryEndpoint;
	private PointF mSecondaryEndpoint;
	private Paint mPaint;

	public static final float LENGTH_LONG = 50;
	public static final float LENGTH_MEDIUM = 40;
	public static final float LENGTH_SHORT = 25;
}
