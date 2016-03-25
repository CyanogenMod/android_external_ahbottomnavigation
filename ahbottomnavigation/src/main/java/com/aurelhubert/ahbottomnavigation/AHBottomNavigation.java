package com.aurelhubert.ahbottomnavigation;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * AHBottomNavigationLayout
 * Material Design guidelines : https://www.google.com/design/spec/components/bottom-navigation.html
 */
public class AHBottomNavigation extends FrameLayout {

	// Static
	private static String TAG = "AHBottomNavigation";
	private static final int MIN_ITEMS = 3;
	private static final int MAX_ITEMS = 5;

	// Listener
	private AHBottomNavigationListener listener;
	private OnTabSelectedListener tabSelectedListener;

	// Variables
	private Context context;
	private ArrayList<AHBottomNavigationItem> items = new ArrayList<>();
	private ArrayList<View> views = new ArrayList<>();
	private View backgroundColorView;
	private boolean colored = false;

	private int defaultBackgroundColor = Color.WHITE;
	private int accentColor = Color.WHITE;
	private int inactiveColor = Color.WHITE;

	private int currentItem = 0;
	private int currentColor = 0;
	private float selectedItemWidth, notSelectedItemWidth;
	private boolean behaviorTranslationEnabled = true;
	private boolean forceTint = false;
	private boolean forceTitlesDisplay = false;


	/**
	 * Constructor
	 *
	 * @param context
	 */
	public AHBottomNavigation(Context context) {
		super(context);
		this.context = context;
		initColors();
	}

	public AHBottomNavigation(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initColors();
	}

	public AHBottomNavigation(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		initColors();
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		initViews();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		createItems();
	}


	/////////////
	// PRIVATE //
	/////////////

	/**
	 * Init the default colors
	 */
	private void initColors() {
		accentColor = ContextCompat.getColor(context, R.color.colorAccent);
		inactiveColor = ContextCompat.getColor(context, R.color.colorInactive);
	}

	/**
	 * Init
	 */
	private void initViews() {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			setElevation(context.getResources().getDimension(R.dimen.bottom_navigation_elevation));
			setClipToPadding(false);
		}

		ViewGroup.LayoutParams params = getLayoutParams();
		params.width = ViewGroup.LayoutParams.MATCH_PARENT;
		params.height = (int) context.getResources().getDimension(R.dimen.bottom_navigation_height);
		if (getParent() instanceof CoordinatorLayout && behaviorTranslationEnabled) {
			((CoordinatorLayout.LayoutParams) params).setBehavior(new AHBottomNavigationBehavior());
		}
		setLayoutParams(params);

		if (items.size() < MIN_ITEMS) {
			Log.w(TAG, "The items list should have at least 3 items");
		} else if (items.size() > MAX_ITEMS) {
			Log.w(TAG, "The items list should not have more than 5 items");
		}
	}

	/**
	 * Create the items in the bottom navigation
	 */
	private void createItems() {

		removeAllViews();
		views.clear();

		backgroundColorView = new View(context);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && colored) {
			LayoutParams backgroundLayoutParams = new LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			addView(backgroundColorView, backgroundLayoutParams);
		}

		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setGravity(Gravity.CENTER);

		int layoutHeight = (int) context.getResources().getDimension(R.dimen.bottom_navigation_height);
		LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, layoutHeight);
		addView(linearLayout, layoutParams);

		if (items.size() == MIN_ITEMS || forceTitlesDisplay) {
			createClassicItems(linearLayout);
		} else {
			createSmallItems(linearLayout);
		}
	}

	/**
	 * Create classic items (only 3 items in the bottom navigation)
	 *
	 * @param linearLayout The layout where the items are added
	 */
	private void createClassicItems(LinearLayout linearLayout) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		float height = context.getResources().getDimension(R.dimen.bottom_navigation_height);
		float minWidth = context.getResources().getDimension(R.dimen.bottom_navigation_min_width);
		float maxWidth = context.getResources().getDimension(R.dimen.bottom_navigation_max_width);

		if (forceTitlesDisplay && items.size() > MIN_ITEMS) {
			minWidth = context.getResources().getDimension(R.dimen.bottom_navigation_small_inactive_min_width);
			maxWidth = context.getResources().getDimension(R.dimen.bottom_navigation_small_inactive_max_width);
		}

		int layoutWidth = getWidth();
		if (layoutWidth == 0 || items.size() == 0) {
			return;
		}

		float itemWidth = layoutWidth / items.size();
		if (itemWidth < minWidth) {
			itemWidth = minWidth;
		} else if (itemWidth > maxWidth) {
			itemWidth = maxWidth;
		}

		float activeSize = context.getResources().getDimension(R.dimen.bottom_navigation_text_size_active);
		float inactiveSize = context.getResources().getDimension(R.dimen.bottom_navigation_text_size_inactive);
		if (forceTitlesDisplay && items.size() > MIN_ITEMS) {
			activeSize = context.getResources().getDimension(R.dimen.bottom_navigation_text_size_forced_active);
			inactiveSize = context.getResources().getDimension(R.dimen.bottom_navigation_text_size_forced_inactive);
		}

		for (int i = 0; i < items.size(); i++) {

			final int itemIndex = i;
			AHBottomNavigationItem item = items.get(itemIndex);

			View view = inflater.inflate(R.layout.bottom_navigation_item, this, false);
			FrameLayout container = (FrameLayout) view.findViewById(R.id.bottom_navigation_container);
			ImageView icon = (ImageView) view.findViewById(R.id.bottom_navigation_item_icon);
			TextView title = (TextView) view.findViewById(R.id.bottom_navigation_item_title);
			icon.setImageDrawable(item.getDrawable(context));
			title.setText(item.getTitle(context));

			if (forceTitlesDisplay && items.size() > MIN_ITEMS) {
				container.setPadding(0, container.getPaddingTop(), 0, container.getPaddingBottom());
			}

			if (i == currentItem) {
				int activePaddingTop = (int) context.getResources()
						.getDimension(R.dimen.bottom_navigation_padding_top_active);
				view.setPadding(view.getPaddingLeft(), activePaddingTop, view.getPaddingRight(),
						view.getPaddingBottom());
			}

			if (colored) {
				if (i == currentItem) {
					setBackgroundColor(item.getColor(context));
					currentColor = item.getColor(context);
				}

				icon.setImageDrawable(AHHelper.getTintDrawable(items.get(i).getDrawable(context),
						currentItem == i ? ContextCompat.getColor(context, R.color.colorActiveSmall) :
								ContextCompat.getColor(context, R.color.colorInactiveSmall), forceTint));
				title.setTextColor(currentItem == i ?
						ContextCompat.getColor(context, R.color.colorActiveSmall) :
						ContextCompat.getColor(context, R.color.colorInactiveSmall));

			} else {
				setBackgroundColor(defaultBackgroundColor);
				icon.setImageDrawable(AHHelper.getTintDrawable(items.get(i).getDrawable(context),
						currentItem == i ? accentColor : inactiveColor, forceTint));
				title.setTextColor(currentItem == i ? accentColor : inactiveColor);
			}

			title.setTextSize(TypedValue.COMPLEX_UNIT_PX, currentItem == i ? activeSize : inactiveSize);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					updateItems(itemIndex);
				}
			});

			LayoutParams params = new LayoutParams((int) itemWidth, (int) height);
			linearLayout.addView(view, params);
			views.add(view);
		}
	}

	/**
	 * Create small items (more than 3 items in the bottom navigation)
	 *
	 * @param linearLayout The layout where the items are added
	 */
	private void createSmallItems(LinearLayout linearLayout) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		float height = context.getResources().getDimension(R.dimen.bottom_navigation_height);
		float minWidth = context.getResources().getDimension(R.dimen.bottom_navigation_small_inactive_min_width);
		float maxWidth = context.getResources().getDimension(R.dimen.bottom_navigation_small_inactive_max_width);

		int layoutWidth = getWidth();
		if (layoutWidth == 0 || items.size() == 0) {
			return;
		}

		float itemWidth = layoutWidth / items.size();

		if (itemWidth < minWidth) {
			itemWidth = minWidth;
		} else if (itemWidth > maxWidth) {
			itemWidth = maxWidth;
		}

		float difference = context.getResources().getDimension(R.dimen.bottom_navigation_small_selected_width_difference);
		selectedItemWidth = itemWidth + items.size() * difference;
		itemWidth -= difference;
		notSelectedItemWidth = itemWidth;

		for (int i = 0; i < items.size(); i++) {

			final int itemIndex = i;
			AHBottomNavigationItem item = items.get(itemIndex);

			View view = inflater.inflate(R.layout.bottom_navigation_small_item, this, false);
			ImageView icon = (ImageView) view.findViewById(R.id.bottom_navigation_small_item_icon);
			TextView title = (TextView) view.findViewById(R.id.bottom_navigation_small_item_title);
			icon.setImageDrawable(item.getDrawable(context));
			title.setText(item.getTitle(context));

			if (i == currentItem) {
				int activePaddingTop = (int) context.getResources()
						.getDimension(R.dimen.bottom_navigation_small_padding_top_active);
				int activePaddingBottom = (int) context.getResources()
						.getDimension(R.dimen.bottom_navigation_padding_bottom);
				view.setPadding(view.getPaddingLeft(), activePaddingTop, view.getPaddingRight(),
						activePaddingBottom);
			}

			if (colored) {
				if (i == currentItem) {
					setBackgroundColor(item.getColor(context));
					currentColor = item.getColor(context);
				}

				icon.setImageDrawable(AHHelper.getTintDrawable(items.get(i).getDrawable(context),
						currentItem == i ? ContextCompat.getColor(context, R.color.colorActiveSmall) :
								ContextCompat.getColor(context, R.color.colorInactiveSmall), forceTint));
				title.setTextColor(currentItem == i ?
						ContextCompat.getColor(context, R.color.colorActiveSmall) :
						ContextCompat.getColor(context, R.color.colorInactiveSmall));
			} else {
				setBackgroundColor(defaultBackgroundColor);
				icon.setImageDrawable(AHHelper.getTintDrawable(items.get(i).getDrawable(context),
						currentItem == i ? accentColor : inactiveColor, forceTint));
				title.setTextColor(currentItem == i ? accentColor : inactiveColor);
			}

			title.setAlpha(currentItem == i ? 1 : 0);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					updateSmallItems(itemIndex);
				}
			});

			LayoutParams params = new LayoutParams(
					i == currentItem ? (int) selectedItemWidth : (int) itemWidth, (int) height);
			linearLayout.addView(view, params);
			views.add(view);
		}
	}


	/**
	 * Update Items UI
	 */
	private void updateItems(final int itemIndex) {

		if (currentItem == itemIndex) {
			if (tabSelectedListener != null) {
				tabSelectedListener.onTabSelected(itemIndex, true);
			}
			return;
		}

		int activePaddingTop = (int) context.getResources().getDimension(R.dimen.bottom_navigation_padding_top_active);
		int inactivePaddingTop = (int) context.getResources().getDimension(R.dimen.bottom_navigation_padding_top_inactive);
		float activeSize = context.getResources().getDimension(R.dimen.bottom_navigation_text_size_active);
		float inactiveSize = context.getResources().getDimension(R.dimen.bottom_navigation_text_size_inactive);
		if (forceTitlesDisplay && items.size() > MIN_ITEMS) {
			activeSize = context.getResources().getDimension(R.dimen.bottom_navigation_text_size_forced_active);
			inactiveSize = context.getResources().getDimension(R.dimen.bottom_navigation_text_size_forced_inactive);
		}

		int itemActiveColor = colored ? ContextCompat.getColor(context, R.color.colorActiveSmall) :
				accentColor;
		int itemInactiveColor = colored ? ContextCompat.getColor(context, R.color.colorInactiveSmall) :
				inactiveColor;

		for (int i = 0; i < views.size(); i++) {

			if (i == itemIndex) {

				final View container = views.get(itemIndex).findViewById(R.id.bottom_navigation_container);
				final TextView title = (TextView) views.get(itemIndex).findViewById(R.id.bottom_navigation_item_title);
				final ImageView icon = (ImageView) views.get(itemIndex).findViewById(R.id.bottom_navigation_item_icon);

				AHHelper.updateTopPadding(container, inactivePaddingTop, activePaddingTop);
				AHHelper.updateTextColor(title, itemInactiveColor, itemActiveColor);
				AHHelper.updateTextSize(title, inactiveSize, activeSize);
				AHHelper.updateDrawableColor(context, items.get(itemIndex).getDrawable(context), icon,
						itemInactiveColor, itemActiveColor, forceTint);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && colored) {
					backgroundColorView.setBackgroundColor(items.get(itemIndex).getColor(context));
					int finalRadius = Math.max(getWidth(), getHeight());

					int cx = (int) views.get(itemIndex).getX() + views.get(itemIndex).getWidth() / 2;
					int cy = views.get(itemIndex).getHeight() / 2;
					Animator anim = ViewAnimationUtils.createCircularReveal(backgroundColorView, cx, cy, 0, finalRadius);
					anim.addListener(new Animator.AnimatorListener() {
						@Override
						public void onAnimationStart(Animator animation) {
						}

						@Override
						public void onAnimationEnd(Animator animation) {
							setBackgroundColor(items.get(itemIndex).getColor(context));
						}

						@Override
						public void onAnimationCancel(Animator animation) {
						}

						@Override
						public void onAnimationRepeat(Animator animation) {
						}
					});
					anim.start();
				} else if (colored) {
					AHHelper.updateViewBackgroundColor(this, currentColor,
							items.get(itemIndex).getColor(context));
				} else {
					setBackgroundColor(defaultBackgroundColor);
				}

			} else if (i == currentItem) {

				final View container = views.get(currentItem).findViewById(R.id.bottom_navigation_container);
				final TextView title = (TextView) views.get(currentItem).findViewById(R.id.bottom_navigation_item_title);
				final ImageView icon = (ImageView) views.get(currentItem).findViewById(R.id.bottom_navigation_item_icon);

				AHHelper.updateTopPadding(container, activePaddingTop, inactivePaddingTop);
				AHHelper.updateTextColor(title, itemActiveColor, itemInactiveColor);
				AHHelper.updateTextSize(title, activeSize, inactiveSize);
				AHHelper.updateDrawableColor(context, items.get(currentItem).getDrawable(context), icon,
						itemActiveColor, itemInactiveColor, forceTint);
			}
		}

		currentItem = itemIndex;
		currentColor = items.get(currentItem).getColor(context);

		if (listener != null) {
			listener.onTabSelected(itemIndex);
		}
		if (tabSelectedListener != null) {
			tabSelectedListener.onTabSelected(itemIndex, false);
		}
	}

	/**
	 * Update Small items UI
	 */
	private void updateSmallItems(final int itemIndex) {

		if (currentItem == itemIndex) {
			if (tabSelectedListener != null) {
				tabSelectedListener.onTabSelected(itemIndex, true);
			}
			return;
		}

		int activePaddingTop = (int) context.getResources().getDimension(R.dimen.bottom_navigation_small_padding_top_active);
		int inactivePadding = (int) context.getResources().getDimension(R.dimen.bottom_navigation_small_padding_top);
		int itemActiveColor = colored ? ContextCompat.getColor(context, R.color.colorActiveSmall) :
				accentColor;
		int itemInactiveColor = colored ? ContextCompat.getColor(context, R.color.colorInactiveSmall) :
				inactiveColor;

		for (int i = 0; i < views.size(); i++) {

			if (i == itemIndex) {

				final View container = views.get(itemIndex).findViewById(R.id.bottom_navigation_small_container);
				final TextView title = (TextView) views.get(itemIndex).findViewById(R.id.bottom_navigation_small_item_title);
				final ImageView icon = (ImageView) views.get(itemIndex).findViewById(R.id.bottom_navigation_small_item_icon);

				AHHelper.updateTopPadding(container, inactivePadding, activePaddingTop);
				AHHelper.updateTextColor(title, itemInactiveColor, itemActiveColor);
				AHHelper.updateAlpha(title, 0, 1);
				AHHelper.updateWidth(container, notSelectedItemWidth, selectedItemWidth);
				AHHelper.updateDrawableColor(context, items.get(itemIndex).getDrawable(context), icon,
						itemInactiveColor, itemActiveColor, forceTint);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && colored) {
					backgroundColorView.setBackgroundColor(items.get(itemIndex).getColor(context));
					int finalRadius = Math.max(getWidth(), getHeight());
					int cx = (int) views.get(itemIndex).getX() + views.get(itemIndex).getWidth() / 2;
					int cy = views.get(itemIndex).getHeight() / 2;
					Animator anim = ViewAnimationUtils.createCircularReveal(backgroundColorView, cx, cy, 0, finalRadius);
					anim.addListener(new Animator.AnimatorListener() {
						@Override
						public void onAnimationStart(Animator animation) {
						}

						@Override
						public void onAnimationEnd(Animator animation) {
							setBackgroundColor(items.get(itemIndex).getColor(context));
						}

						@Override
						public void onAnimationCancel(Animator animation) {
						}

						@Override
						public void onAnimationRepeat(Animator animation) {
						}
					});
					anim.start();
				} else if (colored) {
					AHHelper.updateViewBackgroundColor(this, currentColor,
							items.get(itemIndex).getColor(context));
				} else {
					setBackgroundColor(defaultBackgroundColor);
				}

			} else if (i == currentItem) {

				final View container = views.get(currentItem).findViewById(R.id.bottom_navigation_small_container);
				final TextView title = (TextView) views.get(currentItem).findViewById(R.id.bottom_navigation_small_item_title);
				final ImageView icon = (ImageView) views.get(currentItem).findViewById(R.id.bottom_navigation_small_item_icon);

				AHHelper.updateTopPadding(container, activePaddingTop, inactivePadding);
				AHHelper.updateTextColor(title, itemActiveColor, itemInactiveColor);
				AHHelper.updateAlpha(title, 1, 0);
				AHHelper.updateWidth(container, selectedItemWidth, notSelectedItemWidth);
				AHHelper.updateDrawableColor(context, items.get(currentItem).getDrawable(context), icon,
						itemActiveColor, itemInactiveColor, forceTint);
			}
		}

		currentItem = itemIndex;
		currentColor = items.get(currentItem).getColor(context);

		if (listener != null) {
			listener.onTabSelected(itemIndex);
		}
		if (tabSelectedListener != null) {
			tabSelectedListener.onTabSelected(itemIndex, false);
		}
	}


	////////////
	// PUBLIC //
	////////////

	/**
	 * Add an item
	 */
	public void addItem(AHBottomNavigationItem item) {
		if (this.items.size() >= MAX_ITEMS) {
			Log.w(TAG, "The items list should not have more than 5 items");
		}
		items.add(item);
		createItems();
	}

	/**
	 * Add all items
	 */
	public void addItems(List<AHBottomNavigationItem> items) {
		if (items.size() >= MAX_ITEMS || (this.items.size() + items.size()) > MAX_ITEMS) {
			Log.w(TAG, "The items list should not have more than 5 items");
		}
		this.items.addAll(items);
		createItems();
	}

	/**
	 * Remove an item at the given index
	 */
	public void removeItemAtIndex(int index) {
		if (index < items.size()) {
			this.items.remove(index);
			createItems();
		}
	}

	/**
	 * Remove all items
	 */
	public void removeAllItems() {
		this.items.clear();
		createItems();
	}

	/**
	 * Return the number of items
	 *
	 * @return int
	 */
	public int getItemsCount() {
		return items.size();
	}

	/**
	 * Return if the Bottom Navigation is colored
	 */
	public boolean isColored() {
		return colored;
	}

	/**
	 * Set if the Bottom Navigation is colored
	 */
	public void setColored(boolean colored) {
		this.colored = colored;
		createItems();
	}


	/**
	 * Return the bottom navigation background color
	 *
	 * @return The bottom navigation background color
	 */
	public int getDefaultBackgroundColor() {
		return defaultBackgroundColor;
	}

	/**
	 * Set the bottom navigation background color
	 *
	 * @param defaultBackgroundColor The bottom navigation background color
	 */
	public void setDefaultBackgroundColor(@ColorInt int defaultBackgroundColor) {
		this.defaultBackgroundColor = defaultBackgroundColor;
		createItems();
	}

	/**
	 * Get the accent color (used when the view contains 3 items)
	 *
	 * @return The default accent color
	 */
	public int getAccentColor() {
		return accentColor;
	}

	/**
	 * Set the accent color (used when the view contains 3 items)
	 *
	 * @param accentColor The new accent color
	 */
	public void setAccentColor(int accentColor) {
		this.accentColor = accentColor;
		createItems();
	}

	/**
	 * Get the inactive color (used when the view contains 3 items)
	 *
	 * @return The inactive color
	 */
	public int getInactiveColor() {
		return inactiveColor;
	}

	/**
	 * Set the inactive color (used when the view contains 3 items)
	 *
	 * @param inactiveColor The inactive color
	 */
	public void setInactiveColor(int inactiveColor) {
		this.inactiveColor = inactiveColor;
		createItems();
	}

	/**
	 * Get the current item
	 *
	 * @return The current item position
	 */
	public int getCurrentItem() {
		return currentItem;
	}

	/**
	 * Set the current item
	 *
	 * @param position The new position
	 */
	public void setCurrentItem(int position) {
		if (position >= items.size()) {
			Log.w(TAG, "The position is out of bounds of the items (" + items.size() + " elements)");
			return;
		}
		if (views.size() == 0) {
			currentItem = position;
		} else {
			if (items.size() == MIN_ITEMS) {
				updateItems(position);
			} else {
				updateSmallItems(position);
			}
		}
	}

	/**
	 * Return if the behavior translation is enabled
	 *
	 * @return a boolean value
	 */
	public boolean isBehaviorTranslationEnabled() {
		return behaviorTranslationEnabled;
	}

	/**
	 * Set the behavior translation value
	 *
	 * @param behaviorTranslationEnabled boolean for the state
	 */
	public void setBehaviorTranslationEnabled(boolean behaviorTranslationEnabled) {
		this.behaviorTranslationEnabled = behaviorTranslationEnabled;
		if (getParent() instanceof CoordinatorLayout) {
			ViewGroup.LayoutParams params = getLayoutParams();
			((CoordinatorLayout.LayoutParams) params).setBehavior(behaviorTranslationEnabled ?
					new AHBottomNavigationBehavior() : null);
		}
	}

	/**
	 * Return if the tint should be forced (with setColorFilter)
	 *
	 * @return Boolean
	 */
	public boolean isForceTint() {
		return forceTint;
	}

	/**
	 * Set the force tint value
	 * If forceTint = true, the tint is made with drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
	 *
	 * @param forceTint Boolean
	 */
	public void setForceTint(boolean forceTint) {
		this.forceTint = forceTint;
		createItems();
	}

	/**
	 * Return if we force the titles to be displayed
	 *
	 * @return Boolean
	 */
	public boolean isForceTitlesDisplay() {
		return forceTitlesDisplay;
	}

	/**
	 * Force the titles to be displayed (or used the classic behavior)
	 * Note: Against Material Design guidelines
	 *
	 * @param forceTitlesDisplay Boolean
	 */
	public void setForceTitlesDisplay(boolean forceTitlesDisplay) {
		this.forceTitlesDisplay = forceTitlesDisplay;
		createItems();
	}

	/**
	 * Set the GraphView listener
	 */
	@Deprecated
	public void setAHBottomNavigationListener(AHBottomNavigationListener listener) {
		this.listener = listener;
	}

	/**
	 * Remove the GraphView listener
	 */
	@Deprecated
	public void removeAHBottomNavigationListener() {
		this.listener = null;
	}

	/**
	 * Set AHOnTabSelectedListener
	 */
	public void setOnTabSelectedListener(OnTabSelectedListener tabSelectedListener) {
		this.tabSelectedListener = tabSelectedListener;
	}

	/**
	 * Remove AHOnTabSelectedListener
	 */
	public void removeOnTabSelectedListener() {
		this.tabSelectedListener = null;
	}


	////////////////
	// INTERFACES //
	////////////////

	/**
	 * Interface for Bottom Navigation
	 */
	@Deprecated
	public interface AHBottomNavigationListener {
		void onTabSelected(int position);
	}

	/**
	 *
	 */
	public interface OnTabSelectedListener {
		void onTabSelected(int position, boolean wasSelected);
	}

}