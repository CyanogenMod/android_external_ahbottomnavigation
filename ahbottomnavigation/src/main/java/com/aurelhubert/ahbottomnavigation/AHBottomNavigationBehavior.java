package com.aurelhubert.ahbottomnavigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorUpdateListener;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

/**
 * Created by Nikola D. on 3/15/2016.
 */
public class AHBottomNavigationBehavior<V extends View> extends VerticalScrollingBehavior<V> {

	private static final Interpolator INTERPOLATOR = new LinearOutSlowInInterpolator();
	private static final int ANIM_DURATION = 300;

	private int mTabLayoutId;
	private boolean hidden = false;
	private ViewPropertyAnimatorCompat mTranslationAnimator;
	private TabLayout mTabLayout;
	private Snackbar.SnackbarLayout snackbarLayout;
	private View mTabsHolder;
	private int mSnackbarHeight = -1;
	private float targetOffset = 0;

	public AHBottomNavigationBehavior() {
		super();
	}

	public AHBottomNavigationBehavior(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AHBottomNavigationBehavior_Params);
		mTabLayoutId = a.getResourceId(R.styleable.AHBottomNavigationBehavior_Params_tabLayoutId, View.NO_ID);
		a.recycle();
	}

	@Override
	public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
		boolean layoutChild = super.onLayoutChild(parent, child, layoutDirection);
		if (mTabLayout == null && mTabLayoutId != View.NO_ID) {
			mTabLayout = findTabLayout(child);
			getTabsHolder();
		}
		return layoutChild;
	}

	private TabLayout findTabLayout(View child) {
		if (mTabLayoutId == 0) return null;
		return (TabLayout) child.findViewById(mTabLayoutId);
	}

	@Override
	public boolean onDependentViewChanged(CoordinatorLayout parent, V child, View dependency) {
		return super.onDependentViewChanged(parent, child, dependency);
	}

	@Override
	public void onDependentViewRemoved(CoordinatorLayout parent, V child, View dependency) {
		super.onDependentViewRemoved(parent, child, dependency);
	}

	@Override
	public boolean layoutDependsOn(CoordinatorLayout parent, V child, View dependency) {
		updateSnackbar(child, dependency);
		return super.layoutDependsOn(parent, child, dependency);
	}

	@Override
	public void onNestedVerticalOverScroll(CoordinatorLayout coordinatorLayout, V child, @ScrollDirection int direction, int currentOverScroll, int totalOverScroll) {
	}

	@Override
	public void onDirectionNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target, int dx, int dy, int[] consumed, @ScrollDirection int scrollDirection) {
		handleDirection(child, scrollDirection);
	}


	private void handleDirection(V child, int scrollDirection) {
		if (scrollDirection == ScrollDirection.SCROLL_DIRECTION_DOWN && hidden) {
			hidden = false;
			animateOffset(child, 0);
		} else if (scrollDirection == ScrollDirection.SCROLL_DIRECTION_UP && !hidden) {
			hidden = true;
			animateOffset(child, child.getHeight());
		}
	}

	@Override
	protected boolean onNestedDirectionFling(CoordinatorLayout coordinatorLayout, V child, View target, float velocityX, float velocityY, @ScrollDirection int scrollDirection) {
		handleDirection(child, scrollDirection);
		return true;
	}

	private void animateOffset(final V child, final int offset) {
		ensureOrCancelAnimator(child);
		mTranslationAnimator.translationY(offset).start();
		animateTabsHolder(offset);
		animateSnackBar(offset);
	}

	private void animateTabsHolder(int offset) {
		if (mTabsHolder != null) {
			offset = offset > 0 ? 0 : 1;
			ViewCompat.animate(mTabsHolder).alpha(offset).setDuration(ANIM_DURATION).start();
		}
	}

	private void animateSnackBar(int offset) {
		if (snackbarLayout != null) {
			AHHelper.updateBottomMargin(snackbarLayout, offset > 0 ? 0 : offset,
					offset > 0 ? offset : 0, ANIM_DURATION);
		}
	}

	private void ensureOrCancelAnimator(V child) {
		if (mTranslationAnimator == null) {
			mTranslationAnimator = ViewCompat.animate(child);
			mTranslationAnimator.setDuration(ANIM_DURATION);
			mTranslationAnimator.setUpdateListener(new ViewPropertyAnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(View view) {
					targetOffset = view.getTranslationY();
					if (snackbarLayout != null && snackbarLayout.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
						ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) snackbarLayout.getLayoutParams();
						p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, (int) targetOffset);
						snackbarLayout.requestLayout();
					}
				}
			});
			mTranslationAnimator.setInterpolator(INTERPOLATOR);
		} else {
			mTranslationAnimator.cancel();
		}
	}

	private void getTabsHolder() {
		if (mTabLayout != null) {
			mTabsHolder = mTabLayout.getChildAt(0);
		}
	}

	public static <V extends View> AHBottomNavigationBehavior<V> from(V view) {
		ViewGroup.LayoutParams params = view.getLayoutParams();
		if (!(params instanceof CoordinatorLayout.LayoutParams)) {
			throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
		}
		CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params)
				.getBehavior();
		if (!(behavior instanceof AHBottomNavigationBehavior)) {
			throw new IllegalArgumentException(
					"The view is not associated with AHBottomNavigationBehavior");
		}
		return (AHBottomNavigationBehavior<V>) behavior;
	}

	public void setTabLayoutId(int tabId) {
		this.mTabLayoutId = tabId;
	}

	public void resetOffset(V view) {
		animateOffset(view, 0);
	}

	/**
	 * Update Snackbar position
	 */
	public void updateSnackbar(View child, View dependency) {
		if (dependency != null && dependency instanceof Snackbar.SnackbarLayout) {

			snackbarLayout = (Snackbar.SnackbarLayout) dependency;

			if (mSnackbarHeight == -1) {
				mSnackbarHeight = dependency.getHeight();
			}

			int targetMargin = (int) (mSnackbarHeight + child.getMeasuredHeight() - child.getTranslationY());
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
				child.bringToFront();
			}

			if (dependency.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
				ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) dependency.getLayoutParams();
				p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, targetMargin);
				dependency.requestLayout();
			}
		}
	}


}