package com.schneenet.android.lasers.obj.menu;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.format.DateUtils;
import android.view.MotionEvent;

import com.schneenet.android.lasers.R;

public class LevelCompleteDialog {

	public LevelCompleteDialog(Resources res, long time, LevelCompleteDialogListener l) {
		mListener = l;
		mFrameBorderPaint = new Paint();
		mFrameBorderPaint.setColor(Color.WHITE);
		mFrameBorderPaint.setStyle(Style.STROKE);
		mFrameBackgroundPaint = new Paint();
		mFrameBackgroundPaint.setColor(Color.BLACK);
		mFrameBackgroundPaint.setStyle(Style.FILL);
		mMessagePaint = new Paint();
		mMessagePaint.setColor(Color.WHITE);
		mMessagePaint.setTextSize(res.getDimensionPixelSize(R.dimen.dialog_message_text_size));
		mMessagePaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG | Paint.DITHER_FLAG);
		mButtonTextPaint = new Paint();
		mButtonTextPaint.setTextSize(res.getDimensionPixelSize(R.dimen.dialog_button_text_size));
		mButtonTextPaint.setColor(Color.WHITE);
		mButtonTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG | Paint.DITHER_FLAG);
		mButtonTextHighlightPaint = new Paint();
		mButtonTextHighlightPaint.setTextSize(res.getDimensionPixelSize(R.dimen.dialog_button_text_size));
		mButtonTextHighlightPaint.setColor(Color.WHITE);
		mButtonTextHighlightPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG | Paint.DITHER_FLAG);
		mButtonBackgroundPaint = new Paint();
		mButtonBackgroundPaint.setColor(Color.TRANSPARENT);
		mButtonBackgroundPaint.setStyle(Style.FILL);
		mButtonBackgroundHighlightPaint = new Paint();
		mButtonBackgroundHighlightPaint.setColor(HIGHLIGHT_COLOR);
		mButtonBackgroundHighlightPaint.setStyle(Style.FILL);
		mButtonBorderPaint = new Paint();
		mButtonBorderPaint.setColor(Color.WHITE);
		mButtonBorderPaint.setStyle(Style.STROKE);
		mButtonBorderHighlightPaint = new Paint();
		mButtonBorderHighlightPaint.setColor(Color.WHITE);
		mButtonBorderHighlightPaint.setStyle(Style.STROKE);

		mMessage1 = "Level Complete!";
		mMessage2 = "Time: " + DateUtils.formatElapsedTime(time / 1000l);

		// Measure buttons
		Rect firstButtonTextBounds = new Rect();
		mButtonTextPaint.getTextBounds(BUTTON_FIRST_STR, 0, BUTTON_FIRST_STR.length(), firstButtonTextBounds);
		Rect firstButtonHighlightedTextBounds = new Rect();
		mButtonTextHighlightPaint.getTextBounds(BUTTON_FIRST_STR, 0, BUTTON_FIRST_STR.length(), firstButtonTextBounds);
		Rect secondButtonTextBounds = new Rect();
		mButtonTextPaint.getTextBounds(BUTTON_SECOND_STR, 0, BUTTON_SECOND_STR.length(), secondButtonTextBounds);
		Rect secondButtonHighlightedTextBounds = new Rect();
		mButtonTextHighlightPaint.getTextBounds(BUTTON_SECOND_STR, 0, BUTTON_SECOND_STR.length(), secondButtonTextBounds);
		buttonWidth = PADDING + Math.max(Math.max(firstButtonTextBounds.width(), firstButtonHighlightedTextBounds.width()), Math.max(secondButtonTextBounds.width(), secondButtonHighlightedTextBounds.width())) + PADDING;
		buttonHeight = PADDING + Math.max(Math.max(firstButtonTextBounds.height(), firstButtonHighlightedTextBounds.height()), Math.max(secondButtonTextBounds.height(), secondButtonHighlightedTextBounds.height())) + PADDING;
		float buttonsHeight = PADDING + buttonHeight + PADDING + buttonHeight + PADDING + buttonHeight + PADDING;

		// Measure message
		Rect message1TextBounds = new Rect();
		Rect message2TextBounds = new Rect();
		mMessagePaint.getTextBounds(mMessage1, 0, mMessage1.length(), message1TextBounds);
		mMessagePaint.getTextBounds(mMessage2, 0, mMessage2.length(), message2TextBounds);
		float messageWidth = Math.max(message1TextBounds.width(), message2TextBounds.width());
		message1Height = message1TextBounds.height();
		message2Height = message2TextBounds.height();

		// Prepare data structures for drawing
		frameWidth = PADDING + Math.max(buttonWidth, messageWidth) + PADDING;
		frameHeight = PADDING + message1Height + PADDING + message2Height + buttonsHeight;

	}

	public void draw(Canvas c) {
		// Draw mask
		c.drawARGB(0xAA, 0, 0, 0);

		// Draw the dialog in the center of the screen
		RectF frameRect = new RectF();
		frameRect.top = c.getHeight() / 2 - frameHeight / 2;
		frameRect.bottom = c.getHeight() / 2 + frameHeight / 2;
		frameRect.left = c.getWidth() / 2 - frameWidth / 2;
		frameRect.right = c.getWidth() / 2 + frameWidth / 2;
		c.drawRect(frameRect, mFrameBackgroundPaint);
		c.drawRect(frameRect, mFrameBorderPaint);

		float top = frameRect.top + PADDING;

		// Draw messages
		float xOffset1 = frameRect.width() / 2 - mMessagePaint.measureText(mMessage1) / 2;
		c.drawText(mMessage1, frameRect.left + xOffset1, top + message1Height, mMessagePaint);
		top += message1Height + PADDING;
		float xOffset2 = frameRect.width() / 2 - mMessagePaint.measureText(mMessage2) / 2;
		c.drawText(mMessage2, frameRect.left + xOffset2, top + message2Height, mMessagePaint);
		top += message2Height + PADDING;

		// Draw First Button
		firstButtonZone = new RectF();
		firstButtonZone.left = frameRect.left + PADDING;
		firstButtonZone.right = frameRect.right - PADDING;
		firstButtonZone.top = top;
		firstButtonZone.bottom = top + buttonHeight;
		float xOffsetButtonFirst = firstButtonZone.width() / 2 - (mFirstButtonState ? mButtonTextHighlightPaint.measureText(BUTTON_FIRST_STR) : mButtonTextPaint.measureText(BUTTON_FIRST_STR)) / 2;
		c.drawRect(firstButtonZone, mFirstButtonState ? mButtonBackgroundHighlightPaint : mButtonBackgroundPaint);
		c.drawRect(firstButtonZone, mFirstButtonState ? mButtonBorderHighlightPaint : mButtonBorderPaint);
		c.drawText(BUTTON_FIRST_STR, firstButtonZone.left + xOffsetButtonFirst, firstButtonZone.bottom - PADDING, mFirstButtonState ? mButtonTextHighlightPaint : mButtonTextPaint);
		top += buttonHeight + PADDING;

		// Draw Second Button
		secondButtonZone = new RectF();
		secondButtonZone.left = frameRect.left + PADDING;
		secondButtonZone.right = frameRect.right - PADDING;
		secondButtonZone.top = top;
		secondButtonZone.bottom = top + buttonHeight;
		float xOffsetButtonSecond = secondButtonZone.width() / 2 - (mSecondButtonState ? mButtonTextHighlightPaint.measureText(BUTTON_SECOND_STR) : mButtonTextPaint.measureText(BUTTON_SECOND_STR)) / 2;
		c.drawRect(secondButtonZone, mSecondButtonState ? mButtonBackgroundHighlightPaint : mButtonBackgroundPaint);
		c.drawRect(secondButtonZone, mSecondButtonState ? mButtonBorderHighlightPaint : mButtonBorderPaint);
		c.drawText(BUTTON_SECOND_STR, secondButtonZone.left + xOffsetButtonSecond, secondButtonZone.bottom - PADDING, mSecondButtonState ? mButtonTextHighlightPaint : mButtonTextPaint);
		top += buttonHeight + PADDING;
		
		// Draw Third button
		thirdButtonZone = new RectF();
		thirdButtonZone.left = frameRect.left + PADDING;
		thirdButtonZone.right = frameRect.right - PADDING;
		thirdButtonZone.top = top;
		thirdButtonZone.bottom = top + buttonHeight;
		float xOffsetButtonThird = thirdButtonZone.width() / 2 - (mThirdButtonState ? mButtonTextHighlightPaint.measureText(BUTTON_THIRD_STR) : mButtonTextPaint.measureText(BUTTON_THIRD_STR)) / 2;
		c.drawRect(thirdButtonZone, mThirdButtonState ? mButtonBackgroundHighlightPaint : mButtonBackgroundPaint);
		c.drawRect(thirdButtonZone, mThirdButtonState ? mButtonBorderHighlightPaint : mButtonBorderPaint);
		c.drawText(BUTTON_THIRD_STR, thirdButtonZone.left + xOffsetButtonThird, thirdButtonZone.bottom - PADDING, mThirdButtonState ? mButtonTextHighlightPaint : mButtonTextPaint);
		top += buttonHeight + PADDING;

	}

	public void handleTouchEvent(MotionEvent e) {
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchDown = true;
		case MotionEvent.ACTION_MOVE:
			if (touchDown) {
				mFirstButtonState = (firstButtonZone != null && firstButtonZone.contains(e.getX(), e.getY()));
				mSecondButtonState = (secondButtonZone != null && secondButtonZone.contains(e.getX(), e.getY()));
				mThirdButtonState = (thirdButtonZone != null && thirdButtonZone.contains(e.getX(), e.getY()));
			}
			break;
		case MotionEvent.ACTION_UP:
			if (touchDown && mListener != null) {
				if (firstButtonZone != null && firstButtonZone.contains(e.getX(), e.getY())) {
					mListener.onButtonClicked(BUTTON_FIRST);
					mFirstButtonState = false;
				}
				if (secondButtonZone != null && secondButtonZone.contains(e.getX(), e.getY())) {
					mListener.onButtonClicked(BUTTON_SECOND);
					mSecondButtonState = false;
				}
				if (thirdButtonZone != null && thirdButtonZone.contains(e.getX(), e.getY())) {
					mListener.onButtonClicked(BUTTON_THIRD);
					mThirdButtonState = false;
				}
			}
			break;
		}
	}

	private static final int HIGHLIGHT_COLOR = 0xFF0000CC;
	private static final float PADDING = 15.0f;

	private float frameWidth;
	private float frameHeight;
	private float message1Height;
	private float message2Height;
	private float buttonWidth;
	private float buttonHeight;

	private Paint mFrameBorderPaint;
	private Paint mFrameBackgroundPaint;
	private Paint mMessagePaint;
	private Paint mButtonTextPaint;
	private Paint mButtonTextHighlightPaint;
	private Paint mButtonBackgroundPaint;
	private Paint mButtonBackgroundHighlightPaint;
	private Paint mButtonBorderPaint;
	private Paint mButtonBorderHighlightPaint;

	private String mMessage1;
	private String mMessage2;
	private boolean mFirstButtonState;
	private boolean mSecondButtonState;
	private boolean mThirdButtonState;

	private RectF firstButtonZone;
	private RectF secondButtonZone;
	private RectF thirdButtonZone;
	private LevelCompleteDialogListener mListener;
	private boolean touchDown;

	public static final int BUTTON_FIRST = 0;
	private static final String BUTTON_FIRST_STR = "Level Select";

	public static final int BUTTON_SECOND = 1;
	private static final String BUTTON_SECOND_STR = "Main Menu";
	
	public static final int BUTTON_THIRD = 2;
	private static final String BUTTON_THIRD_STR = "Back";

	public interface LevelCompleteDialogListener {
		public void onButtonClicked(int which);
	}

}
