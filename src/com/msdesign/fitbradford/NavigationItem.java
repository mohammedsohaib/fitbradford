package com.msdesign.fitbradford;

public class NavigationItem {
	String mTitle;
	int mIcon;

	public NavigationItem(String title, int icon) {
		mTitle = title;
		mIcon = icon;
	}

	public String getmTitle() {
		return mTitle;
	}

	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public int getmIcon() {
		return mIcon;
	}

	public void setmIcon(int mIcon) {
		this.mIcon = mIcon;
	}
}