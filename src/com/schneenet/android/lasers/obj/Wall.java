package com.schneenet.android.lasers.obj;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.schneenet.android.lasers.LaserLightPuzzleView;

public class Wall extends GameObjectRenderable implements Targetable {

	public Wall(PointF start, PointF end) {
		super(start.x, end.y);
		mStartPt = start;
		mEndPt = end;
		mWallPaint = new Paint();
		mWallPaint.setColor(Color.GRAY);
		mWallPaint.setAntiAlias(true);
		mWallPaint.setStrokeWidth(5);
	}
	
	private PointF getStartPoint() {
		return LaserLightPuzzleView.translateCoordinatePointToCanvas(mStartPt);
	}
	
	private PointF getEndPoint() {
		return LaserLightPuzzleView.translateCoordinatePointToCanvas(mEndPt);
	}

	@Override
	public PointF getTargetPointPrimary(float srcAngle) {
		return getStartPoint();
	}

	@Override
	public PointF getTargetPointSecondary(float srcAngle) {
		return getEndPoint();
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
	public void setLit(boolean lit) {
		// Don't care
	}

	@Override
	public void setRequiresColor(boolean requires, int color) {
		// Don't care
	}
	
	@Override
	public boolean getRequiresColor() {
		return false;
	}

	@Override
	public int getRequiredColor() {
		return 0;
	}

	@Override
	public boolean isMoveable() {
		return false;
	}

	@Override
	public boolean isSelectable() {
		return false;
	}

	@Override
	public boolean isRotatable() {
		return false;
	}

	@Override
	public void draw(Canvas c) {
		c.drawLine(getStartPoint().x, getStartPoint().y, getEndPoint().x, getEndPoint().y, mWallPaint);
	}
	
	private PointF mStartPt;
	private PointF mEndPt;
	private Paint mWallPaint;

}
