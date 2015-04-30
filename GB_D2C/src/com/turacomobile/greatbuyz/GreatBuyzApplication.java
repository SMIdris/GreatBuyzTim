package com.turacomobile.greatbuyz;


import java.io.File;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.webkit.CookieSyncManager;

import com.turacomobile.greatbuyz.analytics.Analytics;
import com.turacomobile.greatbuyz.analytics.Analytics.TAnalyticsMethods;
import com.turacomobile.greatbuyz.service.DB;
import com.turacomobile.greatbuyz.service.DataController;
import com.turacomobile.greatbuyz.service.ServiceDelegate;
import com.turacomobile.greatbuyz.utils.AppConstants;
import com.turacomobile.greatbuyz.utils.Utils;

public class GreatBuyzApplication extends Application
{
	private boolean isTerminated = false;
	private final String TAG = "[GreatBuyz]GreatBuyzApplication";
	private static GreatBuyzApplication _application;
	private static DataController _dataController;
	private static ServiceDelegate _serviceDelegate;
	private static DB _db;
	private SharedPreferences _prefs;
	private static Analytics _analytics;

	private String httpPrefix = "http://";
	
	//private String serverIP = "http://182.23.130.181:80/re/gb";// prod
	//private String serverIP = "103.19.90.82:80";// Test
	//
	private String serverIP = "103.19.90.82";// prod 
	//private String serverIP = "103.19.90.84:8092";
	//
	//private String baseURL = "/greatbuyz-api/v2";
	private String baseURL = "/re/gb";
	private String faqURL = "/gb/info/faq.html";
	private String tncURL = "/gb/info/terms.html";
	private String aboutURL = "/gb/info/about.html";

	private int approxCacheImagesLimit;

	private String selectedCategory = null;

	private int limitForDealByCategory = 20;// 10;
	private int skipIndexForDealByCategory = 0;

	private int limitForDealsOfTheDay = 20;// 10;
	private int skipIndexForDealsOfTheDay = 0;

	private int limitForDealsNearMe = 20;
	private int skipIndexForDealsNearMe = 0;

	private int limitForRecommnededDeals = 20;// 10;
	private int skipIndexForRecommendedDeals = 0;

	private int limitForExploreDeals = 20;
	private int skipIndexForExploreDeals = 0;

	private int limitForMyDeals = 20;
	private int skipIndexForMyDeals = 0;

	private int limitForExclusiveDeals = 20;// 10;
	private int skipIndexForExclusiveDeals = 0;

	private Typeface font;

	@Override
	public void onCreate()
	{
		super.onCreate();
		System.out.println("GreatBuyz: GBA onCreate start");
		_application = this;
		if (isTerminated == false)
		{

			// ////System.out.println("GreatBuyz: GBA onCreate inside isTerminated==false");
			clearAllData();
			// FlurryAgent.onEndSession(this);
			// _application = null;
			_db = null;
			_dataController = null;

		}
		// ////System.out.println("GreatBuyz: GBA onCreate before instantiate");
		instantiate();
		// ////System.out.println("GreatBuyz: GBA onCreate end");
	}

	@Override
	public void onLowMemory()
	{
		super.onLowMemory();

		System.out.println("MEMORY : got low memory, clearing all image loaders");

	}

	@Override
	public void onTerminate()
	{
		// isTerminated = true;
		System.out.println("GreatBuyz: GBA onTerminate start");
		super.onTerminate();
		// clearAllData();
		// FlurryAgent.onEndSession(this);
		GreatBuyzApplication.getApplication().getAnalyticsAgent().onEndSession(getApplicationContext());
		// _application = null;
		// _db = null;
		// _dataController = null;
		//
		// try
		// {
		// int pid = android.os.Process.myPid();
		// android.os.Process.killProcess(pid);
		// }
		// catch(Exception ee)
		// {}
		// ////System.out.println("GreatBuyz: GBA onTerminate end");
	}

	public void instantiate()
	{
		
		System.out.println("called instantiate");
		if (_db == null)
			_db = new DB(this);

		if (_dataController == null)
			_dataController = new DataController();

		if (_serviceDelegate == null)
			_serviceDelegate = new ServiceDelegate();

		_prefs = getSharedPreferences(AppConstants.SHARE_DATA, Context.MODE_PRIVATE);

		String strIP = _prefs.getString(AppConstants.Constants.serverIP, AppConstants.EMPTY_STRING);
		if(!Utils.isNothing(strIP))
			serverIP = strIP;
		if (_analytics == null)
		{
			String mdn = AppConstants.EMPTY_STRING;
			try
			{
				mdn = _dataController.getMDN();
			}
			catch (Exception e)
			{
			}

			int logFileUploadFrequencyInMinutes = 10;
			long logFileUploadMinSizeLimitInKB = 10;
			try
			{
				String strMinutes = _dataController.getConstant(AppConstants.Constants.logFileUploadFrequencyInMinutes);
				logFileUploadFrequencyInMinutes = Integer.parseInt(strMinutes);
				
				String strFileSize = _dataController.getConstant(AppConstants.Constants.logFileUploadMinSizeLimitInKB);
				logFileUploadMinSizeLimitInKB = Long.parseLong(strFileSize);
			}
			catch (Exception e)
			{
			}

			_analytics = new Analytics(TAnalyticsMethods.EAnalyticsMethodVA, mdn, Utils.getIMSI(getApplicationContext()),
					Utils.getAndroidId(getApplicationContext()), logFileUploadFrequencyInMinutes, logFileUploadMinSizeLimitInKB);
		}

		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		approxCacheImagesLimit = activityManager.getMemoryClass();
		approxCacheImagesLimit = approxCacheImagesLimit / 2; // 3/4 of the mem
																// left for app
		approxCacheImagesLimit = approxCacheImagesLimit / 6; // divide by number
																// of tabs
		approxCacheImagesLimit *= 1024; // size in kb
		approxCacheImagesLimit = approxCacheImagesLimit / 40; // 40 kb is max
																// size of image
		if (approxCacheImagesLimit < 10)
			approxCacheImagesLimit = 10;

		CookieSyncManager.createInstance(getApplicationContext());
	}

	public void resetDatabase()
	{
		_dataController.deleteDatabase();
		_db = null;
		_db = new DB(this);
	}
	






	// clear all the stored data;
	public void clearAllData()
	{
		if (_dataController != null)
		{
			_dataController.deleteAllExclusiveDeals();
			_dataController.deleteAllDealsByCategory();
			_dataController.deleteAllDealsYouMayLike();
			_dataController.deleteAllFlagshipDeals();
			_dataController.deleteAllExploreDeals();
			//_dataController.deleteAllMyDeals();
			_dataController.deleteAllMyDeals();
			_dataController.deleteDealById();
			_dataController.deletePurchaseDealById();
			_dataController.deleteAllDealsNearMe();
		}

		setSkipIndexForExclusiveDeals(0);
		setSkipIndexForDealByCategory(0);
		setSkipIndexForDealsOfTheDay(0);
		setSkipIndexForExploreDeals(0);
		setSkipIndexForRecommendedDeals(0);
		setSkipIndexForDealsNearMe(0);
		setSkipIndexForMyDeals(0);
		_dataController = null;
		_serviceDelegate = null;
	}

	// clear all the stored data;
	public void clearAllDataOnCityChange()
	{
		_dataController.deleteAllDealsByCategory();
		_dataController.deleteAllDealsYouMayLike();
		_dataController.deleteAllFlagshipDeals();
		_dataController.deleteAllExploreDeals();
		_dataController.deleteAllDealsNearMe();
		_dataController.deleteAllMyDeals();
		setSkipIndexForDealByCategory(0);
		setSkipIndexForDealsOfTheDay(0);
		setSkipIndexForExploreDeals(0);
		setSkipIndexForRecommendedDeals(0);
		setSkipIndexForDealsNearMe(0);
		setSkipIndexForMyDeals(0);
	}

	public static void trimFileCache()
	{
		String cacheDirPath = Utils.getCacheDirectory();
		File cacheDir = new File(cacheDirPath);
		Utils.trimDirectoryToSize(cacheDir, AppConstants.CACHE_DIRECTORY_TRIM_SIZE);
	}

	public static GreatBuyzApplication getApplication()
	{
		return _application;
	}

	public static DataController getDataController()
	{
		if (_dataController == null)
			_dataController = new DataController();
		return _dataController;
	}

	public static ServiceDelegate getServiceDelegate()
	{
		if (_dataController == null)
			_dataController = new DataController();

		if (_serviceDelegate == null)
			_serviceDelegate = new ServiceDelegate();
		return _serviceDelegate;
	}

	public static DB getDB()
	{
		return _db;
	}

	public SharedPreferences getSharedPreferences()
	{
		if(_prefs == null)
			_prefs = getSharedPreferences(AppConstants.SHARE_DATA, Context.MODE_PRIVATE);
		return _prefs;
	}
	
	
	public String getStringPreference(String Key) {
		String value = "";
		value = getSharedPreferences().getString(Key, "");
		return value;
	}
	
	public void saveStringPreference(String Key , String Value){
		Editor prefEditor = getSharedPreferences().edit();
		prefEditor.putString(Key, Value);
		prefEditor.commit();
	}
	
	/**
	 * @author Kiran ---- This function i am using to get int value from my
	 *         SharedPreferences.
	 */
	public int getPrefrenceIntValue(String key, Context context) {
		SharedPreferences prefs = getSharedPreferences();
		int value = prefs.getInt(key, 0);
		return value;
	}
	
	/**
	 * @author Kiran ---- This function i am using to save int value in my
	 *         SharedPreferences.
	 */

	public void saveIntPrefrence(String key, int value, Context context) {
		SharedPreferences prefs =getSharedPreferences();
		Editor edit = prefs.edit();
		edit.putInt(key, value);
		edit.commit();
	}
	
	public String getServerIP()
	{
		String strIP = getSharedPreferences().getString(AppConstants.Constants.serverIP, AppConstants.EMPTY_STRING);
		if(!Utils.isNothing(strIP))
			serverIP = strIP;

		return serverIP;
	}
	
	public String getBaseURL()
	{
		return httpPrefix + getServerIP() + baseURL;
	}
	
	public String getFAQURL()
	{
		return httpPrefix + getServerIP() + faqURL;
	}
	
	public String getTNCURL()
	{
		return httpPrefix + getServerIP() + tncURL;
	}
	
	public String getAboutURL()
	{
		return httpPrefix + getServerIP() + aboutURL;
	}

	public Analytics getAnalyticsAgent()
	{
		if (_analytics == null)
		{
			String mdn = AppConstants.EMPTY_STRING;
			try
			{
				mdn = _dataController.getMDN();
			}
			catch (Exception e)
			{
			}

			int logFileUploadFrequencyInMinutes = 10;
			long logFileUploadMinSizeLimitInKB = 10;
			try
			{
				String strMinutes = _dataController.getConstant(AppConstants.Constants.logFileUploadFrequencyInMinutes);
				logFileUploadFrequencyInMinutes = Integer.parseInt(strMinutes);
				
				String strFileSize = _dataController.getConstant(AppConstants.Constants.logFileUploadMinSizeLimitInKB);
				logFileUploadMinSizeLimitInKB = Long.parseLong(strFileSize);
			}
			catch (Exception e)
			{
			}

			_analytics = new Analytics(TAnalyticsMethods.EAnalyticsMethodVA, mdn, Utils.getIMSI(getApplicationContext()),
					Utils.getAndroidId(getApplicationContext()), logFileUploadFrequencyInMinutes, logFileUploadMinSizeLimitInKB);
		}
		return _analytics;
	}

	public int getApproxCacheImagesLimit()
	{
		// System.out.println("MEMORY total available size for application in MBs : "
		// + approxCacheImagesLimit);

		return approxCacheImagesLimit;
	}

	public boolean isNetworkAvailable()
	{
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = manager.getAllNetworkInfo();
		if (info != null && info.length > 0)
		{
			for (int i = 0; i < info.length; i++)
			{
				if (info[i].isAvailable())
					return true;
			}
		}

		return false;
	}

	public Typeface getFont()
	{
		if (font == null)
		{
			// font = Typeface.createFromAsset(getAssets(),
			// "fonts/Helvetica LT Condensed Light_0.ttf");
			//font = Typeface.createFromAsset(getAssets(), "fonts/ARLRDBD.TTF");
			font = Typeface.createFromAsset(getAssets(), "fonts/droidsans.ttf");
		}
		return font;
	}

	public void setFont(Typeface font)
	{
		this.font = font;
	}

	public String getSelectedCategory()
	{
		return selectedCategory;
	}

	public void setSelectedCategory(String selectedCategory)
	{
		this.selectedCategory = selectedCategory;
	}

	public int getLimitForDealByCategory()
	{
		return limitForDealByCategory;
	}

	public void setLimitForDealByCategory(int startIndexForDealByCategory)
	{
		this.limitForDealByCategory = startIndexForDealByCategory;
	}

	public int getSkipIndexForDealByCategory()
	{
		return skipIndexForDealByCategory;
	}

	public void setSkipIndexForDealByCategory(int skipIndexForDealByCategory)
	{
		this.skipIndexForDealByCategory = skipIndexForDealByCategory;
	}

	public int getLimitForDealsOfTheDay()
	{
		return limitForDealsOfTheDay;
	}

	public void setLimitForDealsOfTheDay(int limitForDealsOfTheDay)
	{
		this.limitForDealsOfTheDay = limitForDealsOfTheDay;
	}

	public int getSkipIndexForDealsOfTheDay()
	{
		return skipIndexForDealsOfTheDay;
	}

	public void setSkipIndexForDealsOfTheDay(int skipIndexForDealsOfTheDay)
	{
		this.skipIndexForDealsOfTheDay = skipIndexForDealsOfTheDay;
	}

	public int getLimitForDealsNearMe()
	{
		return limitForDealsNearMe;
	}

	public void setLimitForDealsNearMe(int limitForDealsNearMe)
	{
		this.limitForDealsNearMe = limitForDealsNearMe;
	}

	public int getSkipIndexForDealsNearMe()
	{
		return skipIndexForDealsNearMe;
	}

	public void setSkipIndexForDealsNearMe(int skipIndexForDealsNearMe)
	{
		this.skipIndexForDealsNearMe = skipIndexForDealsNearMe;
	}

	public int getLimitForRecommnededDeals()
	{
		return limitForRecommnededDeals;
	}

	public void setLimitForRecommnededDeals(int limitForRecommnededDeals)
	{
		this.limitForRecommnededDeals = limitForRecommnededDeals;
	}

	public int getSkipIndexForRecommendedDeals()
	{
		return skipIndexForRecommendedDeals;
	}

	public void setSkipIndexForRecommendedDeals(int skipIndexForRecommendedDeals)
	{
		this.skipIndexForRecommendedDeals = skipIndexForRecommendedDeals;
	}

	public int getLimitForExploreDeals()
	{
		return limitForExploreDeals;
	}

	public void setLimitForExploreDeals(int limitForExploreDeals)
	{
		this.limitForExploreDeals = limitForExploreDeals;
	}

	public int getSkipIndexForExploreDeals()
	{
		return skipIndexForExploreDeals;
	}

	public void setSkipIndexForExploreDeals(int skipIndexForExploreDeals)
	{
		this.skipIndexForExploreDeals = skipIndexForExploreDeals;
	}

	/*public void setLimitMyDeals(int limitForMyDeals)
	{
		this.limitForMyDeals = limitForMyDeals;
	}*/

	public int getSkipIndexForMyDeals()
	{
		return skipIndexForMyDeals;
	}

	public void setSkipIndexForMyDeals(int skipIndexForMyDeals)
	{
		this.skipIndexForMyDeals = skipIndexForMyDeals;
	}

	public int getLimitForMyDeals()
	{
		return limitForMyDeals;
	}

	public int getLimitForExclusiveDeals()
	{
		return limitForExclusiveDeals;
	}

	public void setLimitForExclusiveDeals(int limitForExclusiveDeals)
	{
		this.limitForExclusiveDeals = limitForExclusiveDeals;
	}

	public int getSkipIndexForExclusiveDeals()
	{
		return skipIndexForExclusiveDeals;
	}

	public void setSkipIndexForExclusiveDeals(int skipIndexForExclusiveDeals)
	{
		this.skipIndexForExclusiveDeals = skipIndexForExclusiveDeals;
	}
}
