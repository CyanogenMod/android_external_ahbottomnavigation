package com.aurelhubert.ahbottomnavigation;

import android.graphics.Color;

/**
 * AHBottomNavigationItem
 * The item is display in the AHBottomNavigation layout
 */
public class AHBottomNavigationItem {

	private String title = "";
	private int color = Color.GRAY;
	private int resource = 0;

	/**
	 * Constructor
	 */
	public AHBottomNavigationItem() {
	}

	/**
	 * Constructor
	 *
	 * @param title    Title
	 * @param resource Drawable resource
	 */
	public AHBottomNavigationItem(String title, int resource) {
		this.title = title;
		this.resource = resource;
	}

	/**
	 * @param title    Title
	 * @param resource Drawable resource
	 * @param color    Background color
	 */
	public AHBottomNavigationItem(String title, int resource, int color) {
		this.title = title;
		this.resource = resource;
		this.color = color;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getResource() {
		return resource;
	}

	public void setResource(int resource) {
		this.resource = resource;
	}
}
