package it.telecomitalia.timcoupon.ui;

public interface ExplorePageFragmentListener
{

	void onSwitchToNextFragment(String category, String city, String locality, String keyword);

	void onSwitchToPrevFragment();
}
