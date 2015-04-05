package it.telecomitalia.timcoupon.ui;

import it.telecomitalia.timcoupon.GreatBuyzApplication;

import com.onmobile.utils.AppConstants;

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