package com.turacomobile.greatbuyz.ui;

public interface ExplorePageFragmentListener
{

	void onSwitchToNextFragment(String category, String city, String locality, String keyword);

	void onSwitchToPrevFragment();
}
