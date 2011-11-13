package com.schneenet.android.lasers;

import android.os.Handler;

/**
 * Animator class - use this to easily handle animation without separate threads (well, you don't have to create them yourself)
 * <br>
 * <b>Usage</b> 
 * <code>
 * // Put this in the initialization of your custom View class
 * mAnimator = new Animator(100, new Animator.AnimatorCallback() {
 *	@Override
 *	public void onAnimateFrame() {
 *		// Change animation data (offsets, animation counters, etc.) here
 *		// Be sure to call postInvalidate() on the view
 *		postInvalidate();
 *	}
 *	});
 * // to start animating
 * mAnimator.start();
 * 
 * // to stop
 * mAnimator.stop();
 * 
 * </code>
 * @author Matt Schneeberger
 *
 */
public class Animator implements Runnable {
	
	/**
	 * Create a new Animator
	 * @param frameDelay Delay between frames in milliseconds
	 * @param animatorCallback Callback to run when animating
	 */
	public Animator(long frameDelay, Animator.AnimatorCallback animatorCallback) {
		mFrameDelay = frameDelay;
		mCallback = animatorCallback;
		if (mCallback == null) {
			throw new IllegalArgumentException("Argument 2 of constructor {Animator(long, Animator.AnimatorCallback)} cannot be null.");
		}
		mHandler = new Handler();
	}
	
	/**
	 * Start animating
	 */
	public void start() {
		mHandler.post(this);
	}
	
	/**
	 * Stop animating
	 */
	public void stop() {
		mHandler.removeCallbacks(this);
	}

	public void run() {
		mCallback.onAnimateFrame();
		mHandler.postDelayed(this, mFrameDelay);
	}
	
	private Handler mHandler;
	private AnimatorCallback mCallback;
	private long mFrameDelay;

	/**
	 * Animator.AnimatorCallback interface
	 * @author Matt Schneeberger
	 *
	 */
	public interface AnimatorCallback {
		public void onAnimateFrame();
	}
}
