package com.schneenet.android.lasers.obj.menu;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

public class GameMenu {

	public static final int GAME_MENU_NONE = 0;
	public static final int GAME_MENU_MAIN = 1;
	public static final int GAME_MENU_PAUSED = 2;

	public static final int GAME_MENU_MAIN_ACTION_NEWGAME = 0;
	public static final int GAME_MENU_MAIN_ACTION_ABOUT = 1;
	public static final int GAME_MENU_MAIN_ACTION_QUIT = 2;

	public static final int GAME_MENU_PAUSED_ACTION_RESUME = 0;
	public static final int GAME_MENU_PAUSED_ACTION_QUIT = 1;

	public static final int MAX_MENU_ITEMS = 10;

	private static final String[][] MENU_LABELS = { {}, { "New Game", "Level Select", "Options", "Quit" }, {}, { "Resume", "Quit" } };

	private static final float MENU_PADDING = 10.0f;
	private static final float MENU_HEIGHT = 50.0f;
	private static final int HIGHLIGHT_COLOR = 0xFF0000CC;

	public GameMenu(MenuListener l) {
		mListener = l;

		mMenuItems = new ArrayList<MenuItem>();
		mMenuButtonTextPaint = new Paint();
		mMenuButtonTextPaint.setTextSize(24.0f);
		mMenuButtonTextPaint.setColor(Color.WHITE);
		mMenuButtonTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG | Paint.DITHER_FLAG);
		mMenuButtonTextHighlightPaint = new Paint();
		mMenuButtonTextHighlightPaint.setTextSize(24.0f);
		mMenuButtonTextHighlightPaint.setColor(Color.WHITE);
		mMenuButtonTextHighlightPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG | Paint.DITHER_FLAG);
		mMenuButtonBackgroundPaint = new Paint();
		mMenuButtonBackgroundPaint.setColor(Color.BLACK);
		mMenuButtonBackgroundPaint.setStyle(Style.FILL);
		mMenuButtonBackgroundPaint.setAntiAlias(true);
		mMenuButtonBackgroundHighlightPaint = new Paint();
		mMenuButtonBackgroundHighlightPaint.setColor(HIGHLIGHT_COLOR);
		mMenuButtonBackgroundHighlightPaint.setStyle(Style.FILL);
		mMenuButtonBackgroundHighlightPaint.setAntiAlias(true);
		mMenuButtonBorderPaint = new Paint();
		mMenuButtonBorderPaint.setColor(Color.WHITE);
		mMenuButtonBorderPaint.setStyle(Style.STROKE);
		mMenuButtonBorderPaint.setAntiAlias(true);
		mMenuButtonBorderHighlightPaint = new Paint();
		mMenuButtonBorderHighlightPaint.setColor(Color.WHITE);
		mMenuButtonBorderHighlightPaint.setStyle(Style.STROKE);
		mMenuButtonBorderHighlightPaint.setAntiAlias(true);

		// TODO Prepare logo bitmap
		// For now just a placeholder

	}

	public void doMenu(int menu) {
		mGameMenu = menu;
		mMenuItems.clear();

		MenuItem menuItem;
		int n = MENU_LABELS[mGameMenu].length;
		for (int i = 0; i < n; i++) {
			menuItem = new MenuItem(mGameMenu, i);
			mMenuItems.add(menuItem);
		}
	}

	public int getGameMenuId() {
		return mGameMenu;
	}

	public void draw(Canvas c) {

		// Draw background fade
		c.drawARGB(0xAA, 0, 0, 0);

		// Align buttons on these lines
		float left = c.getWidth() * 0.2f;
		float right = c.getWidth() * 0.8f;

		// TODO Draw logo
		Paint logoPaint = new Paint();
		logoPaint.setStyle(Style.STROKE);
		logoPaint.setColor(Color.WHITE);
		RectF logoRect = new RectF(left, MENU_PADDING, right, MENU_PADDING + 100);
		c.drawRect(logoRect, logoPaint);

		// Render menu items
		float top = logoRect.bottom + MENU_PADDING;
		RectF buttonRect;
		int n = mMenuItems.size();
		for (int i = 0; i < n; i++) {
			MenuItem curItem = mMenuItems.get(i);
			buttonRect = curItem.getButtonBounds();
			if (buttonRect == null) {
				buttonRect = new RectF(left, top, right, top + MENU_HEIGHT);
				curItem.setButtonBounds(buttonRect);
			}

			boolean highlighted = touchDownPoint != null && buttonRect.contains(touchDownPoint.x, touchDownPoint.y);
			c.drawRoundRect(buttonRect, 5, 5, highlighted ? mMenuButtonBackgroundHighlightPaint : mMenuButtonBackgroundPaint);
			c.drawRoundRect(buttonRect, 5, 5, highlighted ? mMenuButtonBorderHighlightPaint : mMenuButtonBorderPaint);

			Rect textBounds = new Rect();
			if (highlighted) {
				mMenuButtonTextHighlightPaint.getTextBounds(MENU_LABELS[mGameMenu][i], 0, MENU_LABELS[mGameMenu][i].length(), textBounds);
			} else {
				mMenuButtonTextPaint.getTextBounds(MENU_LABELS[mGameMenu][i], 0, MENU_LABELS[mGameMenu][i].length(), textBounds);
			}
			float textX = (buttonRect.width() - textBounds.width()) / 2 + buttonRect.left;
			float textY = buttonRect.bottom - (buttonRect.height() - textBounds.height()) / 2;

			c.drawText(MENU_LABELS[mGameMenu][i], textX, textY, highlighted ? mMenuButtonTextHighlightPaint : mMenuButtonTextPaint);

			top += (MENU_HEIGHT + MENU_PADDING);
		}

	}

	public void handleTouchEvent(MotionEvent e) {
		// Handle menu touch events
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchDown = true;
			// Break omitted intentionally - fall through
		case MotionEvent.ACTION_MOVE:
			if (touchDown) {
				touchDownPoint = new PointF(e.getX(), e.getY());
			}
			break;
		case MotionEvent.ACTION_UP:
			// Click on this point
			if (touchDown && touchDownPoint != null) {
				int n = mMenuItems.size();
				for (int i = 0; i < n; i++) {
					RectF zone = mMenuItems.get(i).getButtonBounds();
					if (zone.contains(touchDownPoint.x, touchDownPoint.y)) {
						mListener.doMenuAction(mGameMenu, i);
						break;
					}
				}
			}
			touchDownPoint = null;
			break;
		}
	}

	public void handleBack() {
		switch (mGameMenu) {
		case GAME_MENU_MAIN:
		case GAME_MENU_NONE:
		default:
			// Do nothing
			break;
		}
	}

	private ArrayList<MenuItem> mMenuItems;
	private MenuListener mListener;

	private int mGameMenu;
	private PointF touchDownPoint;
	boolean touchDown = false;

	private Paint mMenuButtonTextPaint;
	private Paint mMenuButtonTextHighlightPaint;
	private Paint mMenuButtonBackgroundPaint;
	private Paint mMenuButtonBackgroundHighlightPaint;
	private Paint mMenuButtonBorderPaint;
	private Paint mMenuButtonBorderHighlightPaint;

	public class MenuItem {
		private int mMenuId;
		private int mIndex;
		private RectF mButtonBounds;

		public MenuItem(int menuId, int index) {
			mMenuId = menuId;
			mIndex = index;
		}

		public int getMenuId() {
			return mMenuId;
		}

		public int getIndex() {
			return mIndex;
		}

		public void setButtonBounds(RectF buttonBounds) {
			mButtonBounds = buttonBounds;
		}

		public RectF getButtonBounds() {
			return mButtonBounds;
		}
	}

	public interface MenuListener {
		public void doMenuAction(int menuId, int actionId);
	}

}
