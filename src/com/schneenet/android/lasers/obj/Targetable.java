package com.schneenet.android.lasers.obj;

import android.graphics.PointF;

public interface Targetable {
	public PointF getTargetPointPrimary(float srcAngle);
	public PointF getTargetPointSecondary(float srcAngle);
	public boolean isReflecting(int color);
	public float getReflectionAngle(float srcAngle);
	public void setLit(boolean lit);
	public void setRequiresColor(boolean requires, int color);
	public boolean getRequiresColor();
	public int getRequiredColor();
}
