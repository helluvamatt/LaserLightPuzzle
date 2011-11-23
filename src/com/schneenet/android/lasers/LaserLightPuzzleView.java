package com.schneenet.android.lasers;

import org.xml.sax.InputSource;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.schneenet.android.lasers.levels.LaserLightPuzzleLevel;
import com.schneenet.android.lasers.levels.LevelFactory;
import com.schneenet.android.lasers.obj.GameObjectRenderable;
import com.schneenet.android.lasers.obj.LightSource;
import com.schneenet.android.lasers.obj.LightTarget;
import com.schneenet.android.lasers.obj.Targetable;
import com.schneenet.android.lasers.obj.menu.GameDialog;
import com.schneenet.android.lasers.obj.menu.GameMenu;
import com.schneenet.android.lasers.obj.menu.LevelCompleteDialog;

public class LaserLightPuzzleView extends View implements View.OnTouchListener {

	public LaserLightPuzzleView(Context context, AttributeSet as) {
		super(context, as);
		initialize();
	}

	public LaserLightPuzzleView(Context context) {
		super(context);
		initialize();
	}

	public void initialize() {
		setOnTouchListener(this);
		selectedIndex = -1;
		mDragState = DRAG_NONE;
		mGameMenu = new GameMenu(getContext().getResources(), new GameMenu.MenuListener() {
			@Override
			public void doMenuAction(int menuId, int actionId) {
				// Handle menu actions
				switch (menuId) {
				case GameMenu.GAME_MENU_MAIN:
					switch (actionId) {
					case GameMenu.GAME_MENU_MAIN_ACTION_NEWGAME:
						doLevelSelect();
						break;
					case GameMenu.GAME_MENU_MAIN_ACTION_ABOUT:
						mListener.onAbout();
						break;
					case GameMenu.GAME_MENU_MAIN_ACTION_QUIT:
						mGameDialog = new GameDialog(getContext().getResources(), "Are you sure you want to quit?", "Yes", "No", new GameDialog.GameDialogListener() {
							@Override
							public void onPositiveClicked() {
								mGameDialog = null;
								if (mListener != null) {
									mListener.onQuit();
								}
							}

							@Override
							public void onNegativeClicked() {
								mGameDialog = null;
							}
						});
						break;
					}
					break;
				case GameMenu.GAME_MENU_PAUSED:
					switch (actionId) {
					case GameMenu.GAME_MENU_PAUSED_ACTION_RESUME:
						mGameMenu.doMenu(GameMenu.GAME_MENU_NONE);
						break;
					case GameMenu.GAME_MENU_PAUSED_ACTION_QUIT:
						mGameDialog = new GameDialog(getContext().getResources(), "Are you sure you want to quit this level?", "Yes", "No", new GameDialog.GameDialogListener() {
							@Override
							public void onPositiveClicked() {
								mGameMenu.doMenu(GameMenu.GAME_MENU_MAIN);
								mGameDialog = null;
							}

							@Override
							public void onNegativeClicked() {
								mGameDialog = null;
							}
						});
						break;
					}
					break;
				}

			}
		});
		mGameMenu.doMenu(GameMenu.GAME_MENU_MAIN);
		requestLayout();
		mAnimator = new Animator(100, new Animator.AnimatorCallback() {
			@Override
			public void onAnimateFrame() {
				// Increment animation counter
				mAnimState++;
				if (mAnimState > ANIM_STATE_MAX) {
					mAnimState = ANIM_STATE_MIN;
				}

				// What happens when YOU complete a level
				if (levelComplete && !levelCompleteDialogDismissed && mGameDialog == null && mGameMenu.getGameMenuId() == GameMenu.GAME_MENU_NONE && mCompleteDialog == null) {
					mCompleteDialog = new LevelCompleteDialog(getContext().getResources(), levelElapsed, new LevelCompleteDialog.LevelCompleteDialogListener() {
						@Override
						public void onButtonClicked(int which) {
							switch (which) {
							case LevelCompleteDialog.BUTTON_FIRST:
								doLevelSelect();
								mCompleteDialog = null;
								break;
							case LevelCompleteDialog.BUTTON_SECOND:
								mGameMenu.doMenu(GameMenu.GAME_MENU_MAIN);
								mCompleteDialog = null;
								break;
							}
							mCompleteDialog = null;
							levelCompleteDialogDismissed = true;
						}
					});
				}

				// Calculate time elapsed, but only if we are playing!
				long now = System.currentTimeMillis();
				if (mGameDialog == null && mGameMenu.getGameMenuId() == GameMenu.GAME_MENU_NONE && !levelComplete) {
					levelElapsed += (now - lastCheckedTime);
				}
				lastCheckedTime = now;

				// Request a redraw
				invalidate();
			}
		});

		mStatusBarPaint = new Paint();
		mStatusBarPaint.setColor(0xFF222222);
		mStatusBarTextPaint = new Paint();
		mStatusBarTextPaint.setColor(Color.WHITE);
		mStatusBarTextPaint.setTextSize(getContext().getResources().getDimensionPixelSize(R.dimen.status_bar_text_size));
		mStatusBarTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
		statusBarHeight = STATUS_BAR_PADDING + (mStatusBarTextPaint.descent() - mStatusBarTextPaint.ascent()) + STATUS_BAR_PADDING;
	}

	public void setGameListener(GameListener l) {
		mListener = l;
	}

	public void pause() {
		mAnimator.stop();
	}

	public void resume() {
		mAnimator.start();
	}

	public void menu() {
		// Menu key is for pausing and resuming the game
		if (mGameMenu.getGameMenuId() == GameMenu.GAME_MENU_NONE) {
			mGameMenu.doMenu(GameMenu.GAME_MENU_PAUSED);
		} else if (mGameMenu.getGameMenuId() == GameMenu.GAME_MENU_PAUSED) {
			mGameMenu.doMenu(GameMenu.GAME_MENU_NONE);
		}
	}

	public void back() {
		// Back key goes back through the menus and closes the game when in the
		// main menu
		if (mGameDialog == null && mCompleteDialog == null) {
			switch (mGameMenu.getGameMenuId()) {
			case GameMenu.GAME_MENU_NONE:
				// Playing game -> press back -> Main Menu
				// (Psuedo-) Dialog: Are you sure you want to quit this level?

				mGameDialog = new GameDialog(getContext().getResources(), "Are you sure you want to quit this level?", "Yes", "No", new GameDialog.GameDialogListener() {
					@Override
					public void onPositiveClicked() {
						mGameMenu.doMenu(GameMenu.GAME_MENU_MAIN);
						mGameDialog = null;
					}

					@Override
					public void onNegativeClicked() {
						mGameDialog = null;
					}
				});

				break;
			case GameMenu.GAME_MENU_MAIN:
				mGameDialog = new GameDialog(getContext().getResources(), "Are you sure you want to quit?", "Yes", "No", new GameDialog.GameDialogListener() {
					@Override
					public void onPositiveClicked() {
						mGameDialog = null;
						if (mListener != null) {
							mListener.onQuit();
						}
					}

					@Override
					public void onNegativeClicked() {
						mGameDialog = null;
					}
				});
				break;
			default:
				mGameMenu.handleBack();
				break;
			}
		}
	}

	private void doLevelSelect() {
		mListener.onLevelSelect();
	}

	public void loadLevelAsync(InputSource is) {
		mListener.setLoadingAnimationVisible(true);
		LevelFactory.AsyncLevelLoader loader = new LevelFactory.AsyncLevelLoader(is, new LevelFactory.AsyncLevelLoader.AsyncLoaderListener() {

			@Override
			public void onError(String message, Exception e) {
				mListener.onError(message, e);
			}

			@Override
			public void onLevelLoaded(LaserLightPuzzleLevel level) {
				mListener.setLoadingAnimationVisible(false);
				loadLevel(level);
			}
		});
		loader.execute();
	}

	public void loadLevel(LaserLightPuzzleLevel level) {
		currentLevel = level.clone();
		levelComplete = false;
		levelCompleteDialogDismissed = false;
		levelElapsed = 0;
		mGameMenu.doMenu(GameMenu.GAME_MENU_NONE);
		lastCheckedTime = System.currentTimeMillis();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float eX = event.getX();
		float eY = event.getY();
		if (mGameDialog != null) {
			mGameDialog.handleTouchEvent(event);
		} else if (mCompleteDialog != null) {
			mCompleteDialog.handleTouchEvent(event);
		} else if (mGameMenu.getGameMenuId() != GameMenu.GAME_MENU_NONE) {
			mGameMenu.handleTouchEvent(event);
		} else if (currentLevel != null) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (selectedIndex > -1) {
					GameObjectRenderable selectedObj = currentLevel.getGameObject(selectedIndex);
					PointF selectedCP = selectedObj.getCanvasPointF();
					int selDragState = getTouchRegion(selectedCP.x, selectedCP.y, eX, eY);
					switch (selDragState) {
					case DRAG_MOVE:
					case DRAG_ROTATE:
						mDragState = selDragState;
						break;
					case DRAG_NONE:
						// Deselect the current selection
						selectedIndex = -1;
						mDragState = searchAndSelect(eX, eY);
						break;
					}
					break;
				} else {
					mDragState = searchAndSelect(eX, eY);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				float dX = eX - prevX;
				float dY = eY - prevY;
				if (selectedIndex > -1) {
					GameObjectRenderable selectedObj = currentLevel.getGameObject(selectedIndex);
					PointF selectedCP = selectedObj.getCanvasPointF();
					switch (mDragState) {
					case DRAG_ROTATE:
						// Rotate
						float prevTheta = (float) Math.toDegrees(Math.atan2(prevX - selectedCP.x, selectedCP.y - prevY));
						float theta = (float) Math.toDegrees(Math.atan2(eX - selectedCP.x, selectedCP.y - eY));
						float dTheta = theta - prevTheta;
						float curRotation = selectedObj.getRotation();
						currentLevel.getGameObject(selectedIndex).setRotation(curRotation + dTheta);
						break;
					case DRAG_MOVE:
						// Move the object
						float newX = selectedCP.x + dX;
						float newY = selectedCP.y + dY;
						currentLevel.getGameObject(selectedIndex).setX(newX / cWidth);
						currentLevel.getGameObject(selectedIndex).setY(newY / cHeight);
						break;
					case DRAG_NONE:
					default:
						break;
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				break;
			}
		}
		prevX = eX;
		prevY = eY;
		invalidate();
		return true;
	}

	public void onDraw(Canvas c) {

		// Prepare the canvas dimensions if we haven't already.
		if (cWidth != c.getWidth() && cHeight != c.getHeight())
			initTranslation(c);

		boolean menuVisible = mGameMenu != null && mGameMenu.getGameMenuId() != GameMenu.GAME_MENU_NONE;
		boolean gameDialogVisible = mGameDialog != null;
		boolean completeDialogVisible = mCompleteDialog != null;

		// If a level is loaded
		if (currentLevel != null) {

			// Number of game objects
			int n = currentLevel.getGameObjectCount();

			for (int i = 0; i < n; i++) {
				if (currentLevel.getGameObject(i) instanceof Targetable) {
					((Targetable) currentLevel.getGameObject(i)).setLit(false);
				}
			}

			// Draw lasers!
			for (int i = 0; i < n; i++) {
				GameObjectRenderable thisObj = currentLevel.getGameObject(i);
				if (thisObj instanceof LightSource) {
					// We are a laser and we need to find our path
					LightSource ls = (LightSource) thisObj;
					float deg = clampRotationDegrees(ls.getRotation());
					Paint laserPaint = new Paint();
					laserPaint.setColor(ls.getLaserColor());
					laserPaint.setStrokeWidth(mAnimState % 2 == 0 ? 2 : 3);
					laserPaint.setAntiAlias(true);
					recursiveLightDraw(c, 0, thisObj.getCanvasPointF(), deg, laserPaint);
				}
			}

			// Draw game objects and check for win condition
			boolean objective = true;
			int lit = 0;
			int targets = 0;
			for (int i = 0; i < n; i++) {
				currentLevel.getGameObject(i).draw(c);
				if (mGameMenu.getGameMenuId() == GameMenu.GAME_MENU_NONE) {
					// Check for win condition
					if (currentLevel.getGameObject(i) instanceof LightTarget) {
						targets++;
						if (!((LightTarget) currentLevel.getGameObject(i)).isLit()) {
							objective = false;
						} else {
							lit++;
						}
					}
				} else {
					objective = false;
				}
			}
			if (objective) {
				levelComplete = true;
			}

			if (!menuVisible && !gameDialogVisible && !completeDialogVisible) {

				// Draw selected halo
				if (selectedIndex > -1) {
					GameObjectRenderable selectedObj = currentLevel.getGameObject(selectedIndex);
					PointF cP = selectedObj.getCanvasPointF();

					// Move zone
					Paint p1 = new Paint();
					p1.setColor(selectedObj.isMoveable() ? 0x9900FF00 : 0x99FF0000);
					p1.setAntiAlias(true);
					c.drawCircle(cP.x, cP.y, mTouchRegionMoveRadius, p1);

					// Rotation halo zone
					Paint p2 = new Paint();
					p2.setColor(selectedObj.isRotatable() ? 0x9900FF00 : 0x99FF0000);
					p2.setStrokeWidth(mTouchRegionRotateRadiusMax - mTouchRegionRotateRadiusMin);
					p2.setStyle(Style.STROKE);
					p2.setAntiAlias(true);
					c.drawCircle(cP.x, cP.y, mTouchRegionRotateRadiusMin + (mTouchRegionRotateRadiusMax - mTouchRegionRotateRadiusMin) / 2, p2);
				}

				// Draw status bar
				float statusBarTextY = c.getHeight() - STATUS_BAR_PADDING - mStatusBarTextPaint.descent();
				c.drawRect(0, c.getHeight() - statusBarHeight, c.getWidth(), c.getHeight(), mStatusBarPaint);
				c.drawText(DateUtils.formatElapsedTime(levelElapsed / 1000l), STATUS_BAR_PADDING, statusBarTextY, mStatusBarTextPaint);
				String targetsInfo = String.format("%d of %d targets lit.", lit, targets);
				float targetsStrX = c.getWidth() - (STATUS_BAR_PADDING + mStatusBarTextPaint.measureText(targetsInfo));
				c.drawText(targetsInfo, targetsStrX, statusBarTextY, mStatusBarTextPaint);
			}

		}

		if (menuVisible) {
			mGameMenu.draw(c);
		}

		if (gameDialogVisible) {
			mGameDialog.draw(c);
		} else if (completeDialogVisible) {
			mCompleteDialog.draw(c);
		}
	}

	private void recursiveLightDraw(Canvas c, int recurseLevel, PointF srcPt, float srcAngle, Paint laserPaint) {
		if (recurseLevel > MAX_RECURSE_LEVEL)
			return;

		// Number of game objects
		int n = currentLevel.getGameObjectCount();

		// Target point is, initially, the nearest edge of the screen
		PointF targetPt = findEdgePoint(srcPt, srcAngle);
		float targetDistance = findDistance(srcPt, targetPt);

		// Target is, initially, null
		int theTargetPointer = -1;

		// Search for targets
		for (int i = 0; i < n; i++) {
			GameObjectRenderable thisObj = currentLevel.getGameObject(i);
			if (thisObj instanceof Targetable) {
				Targetable target = (Targetable) thisObj;

				// If the light beam passes between these two points, the target
				// is lit and reflection case should be handled.
				// Method for determining if and where line segments intersect
				// adapted from here:
				// http://paulbourke.net/geometry/lineline2d/

				// targetPt is the intersection of these two lines
				// If the laser's path is shorter than the previous path, then
				// this is the new target

				PointF primaryPoint = target.getTargetPointPrimary(srcAngle);
				PointF secondaryPoint = target.getTargetPointSecondary(srcAngle);
				double denom = (targetPt.x - srcPt.x) * (secondaryPoint.y - primaryPoint.y) - (targetPt.y - srcPt.y) * (secondaryPoint.x - primaryPoint.x);
				if (denom != 0) {
					double r_numer = (srcPt.y - primaryPoint.y) * (secondaryPoint.x - primaryPoint.x) - (srcPt.x - primaryPoint.x) * (secondaryPoint.y - primaryPoint.y);
					double s_numer = (srcPt.y - primaryPoint.y) * (targetPt.x - srcPt.x) - (srcPt.x - primaryPoint.x) * (targetPt.y - srcPt.y);
					double r = r_numer / denom;
					double s = s_numer / denom;
					if (r >= 0 && r <= 1 && s >= 0 && s <= 1) {
						float x = (float) (srcPt.x + r * (targetPt.x - srcPt.x));
						float y = (float) (srcPt.y + r * (targetPt.y - srcPt.y));
						PointF tP = new PointF(x, y);
						float d = findDistance(srcPt, tP);
						if (d < targetDistance && d > 0.01f) {
							theTargetPointer = i;
							targetDistance = d;
							targetPt.x = tP.x;
							targetPt.y = tP.y;
						}
					}
				}
			}
		}

		// Draw line from srcPt to targetPt
		Paint laserGlowPaint = new Paint(laserPaint);
		int laserColor = laserPaint.getColor();
		laserGlowPaint.setColor(Color.argb(0x99, Color.red(laserColor), Color.green(laserColor), Color.blue(laserColor)));
		laserGlowPaint.setStrokeWidth(laserPaint.getStrokeWidth() * 1.5f);
		c.drawLine(srcPt.x, srcPt.y, targetPt.x, targetPt.y, laserGlowPaint);
		c.drawLine(srcPt.x, srcPt.y, targetPt.x, targetPt.y, laserPaint);

		if (theTargetPointer > -1) {
			if (!((Targetable) currentLevel.getGameObject(theTargetPointer)).getRequiresColor() || ((Targetable) currentLevel.getGameObject(theTargetPointer)).getRequiredColor() == laserColor) {
				((Targetable) currentLevel.getGameObject(theTargetPointer)).setLit(true);
			}
			if (((Targetable) currentLevel.getGameObject(theTargetPointer)).isReflecting(laserColor)) {
				float reflectionAngle = clampRotationDegrees(((Targetable) currentLevel.getGameObject(theTargetPointer)).getReflectionAngle(srcAngle));
				recursiveLightDraw(c, recurseLevel + 1, targetPt, reflectionAngle, laserPaint);
			}
		}
	}

	private int searchAndSelect(float eX, float eY) {
		// Search for a new object to select
		int n = currentLevel.getGameObjectCount();
		for (int i = 0; i < n; i++) {
			GameObjectRenderable thisObj = currentLevel.getGameObject(i);
			PointF cP = thisObj.getCanvasPointF();
			if (thisObj.isSelectable() && getTouchRegion(cP.x, cP.y, eX, eY) == DRAG_MOVE && selectedIndex < 0) {
				selectedIndex = i;
				return DRAG_MOVE;
			}
		}
		return DRAG_NONE;
	}

	public static float clampRotationDegrees(float deg) {
		while (deg < 0)
			deg += 360;
		while (deg > 360)
			deg -= 360;
		return deg;
	}

	public static float findDistance(PointF srcPt, PointF dstPt) {
		float dX = dstPt.x - srcPt.x;
		float dY = dstPt.y - srcPt.y;
		return (float) Math.hypot(dX, dY);
	}

	private PointF findEdgePoint(PointF src, float rotation) {
		PointF cTR = new PointF(cWidth, 0);
		PointF cBR = new PointF(cWidth, cHeight);
		PointF cBL = new PointF(0, cHeight);
		PointF cTL = new PointF(0, 0);
		float tTR = (float) Math.toDegrees(Math.atan2(cTR.x - src.x, src.y - cTR.y));
		float tBR = (float) Math.toDegrees(Math.atan2(cBR.x - src.x, src.y - cBR.y));
		float tBL = (float) Math.toDegrees(Math.atan2(cBL.x - src.x, src.y - cBL.y)) + 360;
		float tTL = (float) Math.toDegrees(Math.atan2(cTL.x - src.x, src.y - cTL.y)) + 360;
		if (rotation > tTL) {
			// Top
			return new PointF((float) Math.tan(Math.toRadians(rotation)) * src.y + src.x, 0);
		} else if (rotation > tBL) {
			// Left
			return new PointF(0, src.y + src.x / (float) Math.tan(Math.toRadians(rotation)));
		} else if (rotation > tBR) {
			// Bottom
			return new PointF((float) Math.tan(Math.toRadians(rotation)) * (src.y - cHeight) + src.x, cHeight);
		} else if (rotation > tTR) {
			// Right
			return new PointF(cWidth, src.y - (cWidth - src.x) / (float) Math.tan(Math.toRadians(rotation)));
		} else {
			// Top
			return new PointF((float) Math.tan(Math.toRadians(rotation)) * src.y + src.x, 0);
		}
	}

	private int getTouchRegion(float cX, float cY, float eX, float eY) {
		float d = findDistance(new PointF(cX, cY), new PointF(eX, eY));
		if (d < mTouchRegionMoveRadius) {
			return DRAG_MOVE;
		} else if (d >= mTouchRegionRotateRadiusMin && d < mTouchRegionRotateRadiusMax) {
			return DRAG_ROTATE;
		} else {
			return DRAG_NONE;
		}
	}

	public static void initTranslation(Canvas c) {
		cWidth = c.getWidth();
		cHeight = c.getHeight() - (int) Math.ceil(statusBarHeight);
	}

	public static PointF translateCoordinatePointToCanvas(PointF p) {
		return translateCoordinatePointToCanvas(p.x, p.y);
	}

	public static PointF translateCoordinatePointToCanvas(float pX, float pY) {
		return new PointF(pX * cWidth, pY * cHeight);
	}

	private int selectedIndex;

	private int mAnimState = 0;
	private Animator mAnimator;

	private LaserLightPuzzleLevel currentLevel;
	private boolean levelComplete = false;
	private boolean levelCompleteDialogDismissed = false;

	private long levelElapsed = 0;
	private long lastCheckedTime = 0;

	private Paint mStatusBarPaint;
	private Paint mStatusBarTextPaint;

	private static float statusBarHeight;

	private GameMenu mGameMenu;
	private GameDialog mGameDialog;
	private LevelCompleteDialog mCompleteDialog;

	private int mDragState;
	private float prevX;
	private float prevY;

	private static int cWidth;
	private static int cHeight;

	private GameListener mListener;

	private static final int DRAG_MOVE = 0x1F;
	private static final int DRAG_ROTATE = 0x2F;
	private static final int DRAG_NONE = 0x0;

	private static final int ANIM_STATE_MIN = 1;
	private static final int ANIM_STATE_MAX = 20;

	private static final int MAX_RECURSE_LEVEL = 10;

	private int mTouchRegionMoveRadius = 25;
	private int mTouchRegionRotateRadiusMin = 80;
	private int mTouchRegionRotateRadiusMax = 120;

	private static final float STATUS_BAR_PADDING = 10.0f;

	public interface GameListener {
		public void setLoadingAnimationVisible(boolean show);

		public void onLevelSelect();
		
		public void onAbout();

		public void onQuit();
		
		public void onError(String msg, Exception ex);
	}

}
