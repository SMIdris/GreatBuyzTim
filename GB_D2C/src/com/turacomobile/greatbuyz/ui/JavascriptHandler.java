package com.turacomobile.greatbuyz.ui;


import com.turacomobile.greatbuyz.GreatBuyzApplication;
import com.turacomobile.greatbuyz.utils.AppConstants;

public class JavascriptHandler
{
	public String getClientVersion()
	{
		String version;
		try
		{
			version = GreatBuyzApplication.getApplication().getPackageManager()
					.getPackageInfo(GreatBuyzApplication.getApplication().getPackageName(), 0).versionName;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			version = AppConstants.EMPTY_STRING;
		}
		return version;
	}
}