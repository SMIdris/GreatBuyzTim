package it.telecomitalia.timcoupon.service;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import android.content.SharedPreferences;


public class PreferenceManager
{
	private PreferenceManager _manager;
	private SharedPreferences _prefs;

	private PreferenceManager()
	{
	};

	public PreferenceManager getPreferenceManager()
	{
		if (_manager == null)
			_manager = new PreferenceManager();

		_prefs = GreatBuyzApplication.getApplication().getSharedPreferences();
		return _manager;
	};

	public SharedPreferences getSharedPreferences()
	{
		return _prefs;
	}

	/*
	 * public String[] getCategories() { String categories = null; try {
	 * categories = _prefs.getString(AppConstants.SharedPrefKeys.categories,
	 * null); } catch (ClassCastException e) {}
	 * 
	 * String[] categoryArray = null; if(categories != null) { categoryArray =
	 * categories.split(AppConstants.SharedPrefKeys.splitCharacter); } return
	 * categoryArray; }
	 * 
	 * public void setCategories(String [] categoryArray) { if(categoryArray !=
	 * null) { StringBuilder categories = new StringBuilder();
	 * 
	 * for (String category : categoryArray) { categories.append(category);
	 * categories.append(AppConstants.SharedPrefKeys.splitCharacter); } Editor
	 * edit = _prefs.edit();
	 * edit.putString(AppConstants.SharedPrefKeys.categories,
	 * categories.toString()); edit.commit(); } }
	 * 
	 * public String getCity() {
	 * 
	 * }
	 */
}
