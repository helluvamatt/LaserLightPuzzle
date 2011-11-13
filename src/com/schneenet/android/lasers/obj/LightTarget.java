package com.schneenet.android.lasers.obj;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class LightTarget extends GameObjectRenderable implements Targetable {
	
	public LightTarget(float x, float y, float targetRadius) {
		super(x, y);
		mPaint = new Paint();
		mPaint.setColor(Color.GRAY);
		mPaint.setAntiAlias(true);
		mPaintInd = new Paint();
		mPaintInd.setAntiAlias(true);
		mPaintReq = new Paint();
		mPaintReq.setAntiAlias(true);
		mTargetRadius = targetRadius;
		mLit = false;
	}
	
	@Override
	public void setRequiresColor(boolean requires, int color) {
		mRequiresColor = requires;
		mRequiredColor = color;
	}
	
	@Override
	public boolean getRequiresColor() {
		return mRequiresColor;
	}

	@Override
	public int getRequiredColor() {
		return mRequiredColor;
	}
	
	public boolean isLit() {
		return mLit;
	}
	
	private PointF getPointOnCircle(float deg) {
		PointF cP = getCanvasPointF();
		float offsetX = (float) Math.sin(Math.toRadians(deg)) * mTargetRadius;
		float offsetY = (float) Math.cos(Math.toRadians(deg)) * mTargetRadius;
		return new PointF(cP.x + offsetX, cP.y - offsetY);
	}

	public boolean isMoveable() {
		return false;
	}
	
	public boolean isRotatable() {
		return false;
	}
	
	public boolean isSelectable() {
		return false;
	}

	@Override
	public void draw(Canvas c) {
		// TODO Textures?
		PointF cP = getCanvasPointF();
		c.drawCircle(cP.x, cP.y, mTargetRadius, mPaint);
		mPaintReq.setColor(mRequiredColor);
		c.drawCircle(cP.x, cP.y, mTargetRadius - 2, mPaintReq);
		c.drawCircle(cP.x, cP.y, mTargetRadius - 5, mPaint);
		mPaintInd.setColor(mLit ? Color.GREEN : Color.RED);
		c.drawCircle(cP.x, cP.y, mTargetRadius - 8, mPaintInd);
	}
	
	private Paint mPaint;
	private Paint mPaintInd;
	private Paint mPaintReq;
	private float mTargetRadius;
	private boolean mLit;
	
	private boolean mRequiresColor = false;
	private int mRequiredColor = 0;

	@Override
	public PointF getTargetPointPrimary(float srcAngle) {
		return getPointOnCircle(srcAngle + 90);
	}
	
	@Override
	public boolean isReflecting(int color) {
		return false;
	}
	
	@Override
	public float getReflectionAngle(float srcAngle) {
		return 0;
	}

	@Override
	public PointF getTargetPointSecondary(float srcAngle) {
		return getPointOnCircle(srcAngle - 90);
	}
	
	@Override
	public void setLit(boolean lit) {
		mLit = lit;
	}
}
