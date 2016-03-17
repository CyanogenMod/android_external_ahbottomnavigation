package aurelhubert.com.ahbottomnavigation;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

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

		final SwitchCompat switchColored = (SwitchCompat) findViewById(R.id.home_switch_colored);
		final SwitchCompat switchFourItems = (SwitchCompat) findViewById(R.id.home_switch_four_items);
		final AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
		final ArrayList<AHBottomNavigationItem> items = new ArrayList<>();

		AHBottomNavigationItem item1 = new AHBottomNavigationItem("Label One", R.drawable.ic_maps_place, Color.parseColor("#455C65"));
		AHBottomNavigationItem item2 = new AHBottomNavigationItem("Label Two", R.drawable.ic_maps_local_bar, Color.parseColor("#00886A"));
		AHBottomNavigationItem item3 = new AHBottomNavigationItem("Label Three", R.drawable.ic_maps_local_restaurant, Color.parseColor("#8B6B62"));
		final AHBottomNavigationItem item4 = new AHBottomNavigationItem("Label Four", R.drawable.ic_maps_local_bar, Color.parseColor("#6C4A42"));
		final AHBottomNavigationItem item5 = new AHBottomNavigationItem("Label Five", R.drawable.ic_maps_place, Color.parseColor("#8B6B62"));

		items.add(item1);
		items.add(item2);
		items.add(item3);

		bottomNavigation.addItems(items);
		bottomNavigation.setAccentColor(Color.parseColor("#00E676"));
		bottomNavigation.setAHBottomNavigationListener(new AHBottomNavigation.AHBottomNavigationListener() {
			@Override
			public void onTabSelected(int position) {
				//Toast.makeText(HomeActivity.this, "Select: " + position, Toast.LENGTH_SHORT).show();
			}
		});

		switchColored.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				bottomNavigation.setColored(isChecked);
			}
		});

		switchFourItems.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					bottomNavigation.addItem(item4);
					bottomNavigation.addItem(item5);
				} else {
					bottomNavigation.removeAllItems();
					bottomNavigation.addItems(items);
				}
			}
		});

	}

}
