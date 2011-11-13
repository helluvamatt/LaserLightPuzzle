package com.schneenet.android.lasers.obj.menu;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.schneenet.android.lasers.R;

public class GameDialog {

	public GameDialog(Resources res, String message, String positiveButtonText, String negativeButtonText, GameDialogListener l) {
		mMessage = message;
		mPositiveButtonText = positiveButtonText;
		mNegativeButtonText = negativeButtonText;
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

		// Measure buttons
		Rect positiveButtonTextBounds = new Rect();
		mButtonTextPaint.getTextBounds(positiveButtonText, 0, positiveButtonText.length(), positiveButtonTextBounds);
		Rect positiveButtonHighlightedTextBounds = new Rect();
		mButtonTextHighlightPaint.getTextBounds(positiveButtonText, 0, positiveButtonText.length(), positiveButtonTextBounds);
		Rect negativeButtonTextBounds = new Rect();
		mButtonTextPaint.getTextBounds(negativeButtonText, 0, negativeButtonText.length(), negativeButtonTextBounds);
		Rect negativeButtonHighlightedTextBounds = new Rect();
		mButtonTextHighlightPaint.getTextBounds(negativeButtonText, 0, negativeButtonText.length(), negativeButtonTextBounds);
		buttonWidth = PADDING + Math.max(Math.max(positiveButtonTextBounds.width(), positiveButtonHighlightedTextBounds.width()), Math.max(negativeButtonTextBounds.width(), negativeButtonHighlightedTextBounds.width())) + PADDING;
		buttonHeight = PADDING + Math.max(Math.max(positiveButtonTextBounds.height(), positiveButtonHighlightedTextBounds.height()), Math.max(negativeButtonTextBounds.height(), negativeButtonHighlightedTextBounds.height())) + PADDING;
		float buttonsWidth = PADDING + buttonWidth + PADDING + buttonWidth + PADDING;

		// Measure message
		Rect messageTextBounds = new Rect();
		mMessagePaint.getTextBounds(message, 0, message.length(), messageTextBounds);
		messageWidth = PADDING + messageTextBounds.width() + PADDING;
		messageHeight = PADDING + messageTextBounds.height() + PADDING;
		
		// Prepare data structures for drawing
		frameWidth = Math.max(buttonsWidth, messageWidth);
		frameHeight = PADDING + buttonHeight + PADDING + messageTextBounds.height() + PADDING; 
		

	}

	public String getMessage() {
		return mMessage;
	}

	public String getPositiveButtonText() {
		return mPositiveButtonText;
	}

	public String getNegativeButtonText() {
		return mNegativeButtonText;
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
		
		// Draw message
		c.drawText(mMessage, frameRect.left + PADDING, frameRect.top + messageHeight - PADDING, mMessagePaint);
		
		// Button anchor point (halfway across the dialog frame
		float buttonAnchor = frameRect.left + (frameRect.width() / 2);
		
		// Draw Negative Button
		negativeButtonZone = new RectF();
		negativeButtonZone.left = buttonAnchor + (PADDING / 2);
		negativeButtonZone.right = buttonAnchor + (PADDING / 2 + buttonWidth);
		negativeButtonZone.top = frameRect.top + messageHeight;
		negativeButtonZone.bottom = frameRect.bottom - PADDING;
		c.drawRect(negativeButtonZone, mNegativeButtonState ? mButtonBackgroundHighlightPaint : mButtonBackgroundPaint);
		c.drawRect(negativeButtonZone, mNegativeButtonState ? mButtonBorderHighlightPaint : mButtonBorderPaint);
		c.drawText(mNegativeButtonText, negativeButtonZone.left + PADDING, negativeButtonZone.bottom - PADDING, mNegativeButtonState ? mButtonTextHighlightPaint : mButtonTextPaint);
		
		// Draw Positive Button
		positiveButtonZone = new RectF();
		positiveButtonZone.left = buttonAnchor - (PADDING / 2 + buttonWidth);
		positiveButtonZone.right = buttonAnchor - (PADDING / 2);
		positiveButtonZone.top = frameRect.top + messageHeight;
		positiveButtonZone.bottom = frameRect.bottom - PADDING;
		c.drawRect(positiveButtonZone, mPositiveButtonState ? mButtonBackgroundHighlightPaint : mButtonBackgroundPaint);
		c.drawRect(positiveButtonZone, mPositiveButtonState ? mButtonBorderHighlightPaint : mButtonBorderPaint);
		c.drawText(mPositiveButtonText, positiveButtonZone.left + PADDING, positiveButtonZone.bottom - PADDING, mPositiveButtonState ? mButtonTextHighlightPaint : mButtonTextPaint);
	}

	public void handleTouchEvent(MotionEvent e) {
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchDown = true;
			// Break omitted intentionally - fall through to ACTION_MOVE
		case MotionEvent.ACTION_MOVE:
			if (touchDown) {
				mPositiveButtonState = (positiveButtonZone != null && positiveButtonZone.contains(e.getX(), e.getY()));
				mNegativeButtonState = (negativeButtonZone != null && negativeButtonZone.contains(e.getX(), e.getY()));
			}
			break;
		case MotionEvent.ACTION_UP:
			if (touchDown && mListener != null) {
				if (positiveButtonZone != null && positiveButtonZone.contains(e.getX(), e.getY())) {
					mListener.onPositiveClicked();
					mPositiveButtonState = false;
				}
				if (negativeButtonZone != null && negativeButtonZone.contains(e.getX(), e.getY())) {
					mListener.onNegativeClicked();
					mNegativeButtonState = false;
				}
			}
			break;
		}
	}

	private static final int HIGHLIGHT_COLOR = 0xFF0000CC;
	private static final float PADDING = 15.0f;

	private float frameWidth;
	private float frameHeight;
	private float messageWidth;
	private float messageHeight;
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

	private String mMessage;
	private String mPositiveButtonText;
	private String mNegativeButtonText;
	private boolean mPositiveButtonState;
	private boolean mNegativeButtonState;

	private RectF positiveButtonZone;
	private RectF negativeButtonZone;
	private GameDialogListener mListener;
	private boolean touchDown;

	public interface GameDialogListener {
		public void onPositiveClicked();

		public void onNegativeClicked();
	}

}
