package aurelhubert.com.ahbottomnavigation;

import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import java.util.ArrayList;

public class DemoActivity extends AppCompatActivity {

	private DemoFragment currentFragment;
	private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();
	private FragmentManager fragmentManager = getFragmentManager();
	private AHBottomNavigation bottomNavigation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initUI();
	}

	/**
	 * Init UI
	 */
	private void initUI() {

		bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

		AHBottomNavigationItem item1 = new AHBottomNavigationItem("Label One", R.drawable.ic_maps_place, Color.parseColor("#455C65"));
		AHBottomNavigationItem item2 = new AHBottomNavigationItem("Label Two", R.drawable.ic_maps_local_bar, Color.parseColor("#00886A"));
		AHBottomNavigationItem item3 = new AHBottomNavigationItem("Label Three", R.drawable.ic_maps_local_restaurant, Color.parseColor("#8B6B62"));

		bottomNavigationItems.add(item1);
		bottomNavigationItems.add(item2);
		bottomNavigationItems.add(item3);

		bottomNavigation.addItems(bottomNavigationItems);
		bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
		bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
		bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
			@Override
			public void onTabSelected(int position, boolean wasSelected) {
				if (!wasSelected) {
					currentFragment = DemoFragment.newInstance(position);
					fragmentManager.beginTransaction()
							.setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
							.replace(R.id.fragment_container, currentFragment)
							.commit();
				} else if (position > 0) {
					currentFragment.refresh();
				}
			}
		});

		currentFragment = DemoFragment.newInstance(0);
		fragmentManager.beginTransaction()
				.replace(R.id.fragment_container, currentFragment)
				.commit();
	}

	/**
	 * Update the bottom navigation colored param
	 */
	public void updateBottomNavigationColor(boolean isColored) {
		bottomNavigation.setColored(isColored);
	}

	/**
	 * Return if the bottom navigation is colored
	 */
	public boolean isBottomNavigationColored() {
		return bottomNavigation.isColored();
	}

	/**
	 * Add or remove items of the bottom navigation
	 */
	public void updateBottomNavigationItems(boolean addItems) {

		AHBottomNavigationItem item4 = new AHBottomNavigationItem("Label Four", R.drawable.ic_maps_local_bar, Color.parseColor("#6C4A42"));
		AHBottomNavigationItem item5 = new AHBottomNavigationItem("Label Five", R.drawable.ic_maps_place, Color.parseColor("#F63D2B"));

		if (addItems) {
			bottomNavigation.addItem(item4);
			bottomNavigation.addItem(item5);
		} else {
			bottomNavigation.removeAllItems();
			bottomNavigation.addItems(bottomNavigationItems);
		}
	}

	/**
	 * Return the number of items in the bottom navigation
	 */
	public int getBottomNavigationNbItems() {
		return bottomNavigation.getItemsCount();
	}

}
