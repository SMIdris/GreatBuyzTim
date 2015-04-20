package it.telecomitalia.timcoupon.service;

import it.telecomitalia.timcoupon.service.ResponseParser.CategoryDTO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.onmobile.utils.AppConstants;
import com.onmobile.utils.Utils;

public class DB
{
	// TABLES
	private static final String DATABASE_NAME = "gbstore.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_VERSIONS = "tblVersions";
	private static final String TABLE_USER_INFO = "tblUserInfo";
	private static final String TABLE_CITIES = "tblCities";
	private static final String TABLE_CATEGORIES = "tblCategories";
	private static final String TABLE_KEYWORDS = "tblKeywords";
	private static final String TABLE_MESSAGES = "tblMessages";
	private static final String TABLE_CONSTANTS = "tblConstants";

	// TABLE_USER_INFO
	private static final String COL_USER_MDN = "userMDN";
	private static final String COL_USER_ANDROID_ID = "userAndroidId";
	private static final String COL_USER_IMSI = "userIMSI";
	private static final String COL_USER_GCM_ID = "userGCMId";
	private static final String COL_USER_CITY = "userCity";
	private static final String COL_USER_CITY_DEFAULT = "userDefaultCity";
	private static final String COL_USER_EMAIL = "userEmail";
	private static final String COL_USER_IS_SUBSCRIBED_TO_GCM = "userIsSubscribedToGCM";
	private static final String COL_USER_SUBSCRIPTION_STATUS = "userSubscriptionStatus";
	private static final String COL_USER_UPGRADE = "upgrade";
	private static final String COL_USER_UPGRADE_FORCED = "upgradeForced";
	private static final String COL_USER_UPGRADE_MESSAGE = "upgradeMessage";
	private static final String COL_USER_UPGRADE_URL = "upgradeUrl";
	private static final String COL_USER_OTP = "otp";
	private static final String COL_USER_OTP_TIMESTAMP = "otpTimestamp";
	private static final String COL_USER_OTP_MESSAGE = "otpSuccessMessage";
	private static final String COL_USER_NOTIFICATION_FREQUENCY = "notificationFrequency";
	private static final String COL_USER_IS_DAILY_MSG_ENABLED = "isDailyMsgEnabled";

	private static final int COL_USER_MDN_IDX = 0;
	private static final int COL_USER_ANDROID_ID_IDX = 1;
	private static final int COL_USER_IMSI_IDX = 2;
	private static final int COL_USER_GCM_ID_IDX = 3;
	private static final int COL_USER_CITY_IDX = 4;
	private static final int COL_USER_CITY_DEFAULT_IDX = 5;
	private static final int COL_USER_EMAIL_IDX = 6;
	private static final int COL_USER_IS_SUBSCRIBED_TO_GCM_IDX = 7;
	private static final int COL_USER_SUBSCRIPTION_STATUS_IDX = 8;
	private static final int COL_USER_UPGRADE_IDX = 9;
	private static final int COL_USER_UPGRADE_FORCED_IDX = 10;
	private static final int COL_USER_UPGRADE_MESSAGE_IDX = 11;
	private static final int COL_USER_UPGRADE_URL_IDX = 12;
	private static final int COL_USER_OTP_IDX = 13;
	private static final int COL_USER_OTP_TIMESTAMP_IDX = 14;
	private static final int COL_USER_OTP_MESSAGE_IDX = 15;
	private static final int COL_USER_NOTIFICATION_FREQUENCY_IDX = 16;
	private static final int COL_USER_DAILY_MSG_IDX = 17;
	

	// TABLE_CITIES
	private static final String COL_CITY_NAME = "cityName";

	// TABLE_CATEGORIES
	private static final String COL_CATEGORY_NAME = "categoryName";
	private static final String COL_CATEGORY_SEL = "categorySelected";
	private static final String COL_CATEGORY_IMGURL = "categoryImageUrl";

	// TABLE_KEYWORDS
	private static final String COL_KEYWORD_NAME = "keywordName";
	private static final String COL_KEYWORD_SEL = "keywordSelected";

	// TABLE_MESSAGES
	private static final String COL_MESSAGE_KEY = "messageKey";
	private static final String COL_MESSAGE_VALUE = "messageValue";

	// TABLE_CONSTANTS
	private static final String COL_CONSTANT_KEY = "constantKey";
	private static final String COL_CONSTANT_VALUE = "constantValue";

	// TABLE_VERSIONS
	public static final String COL_VERSION_LOCATIONS = "versionLocations";
	public static final String COL_VERSION_CATEGORIES = "versionCategories";
	public static final String COL_VERSION_KEYWORDS = "versionKeywords";
	public static final String COL_VERSION_HELP = "versionHelp";
	public static final String COL_VERSION_ABOUT = "versionAbout";
	public static final String COL_VERSION_FAQ = "versionFaq";
	public static final String COL_VERSION_TNC = "versionTnC";
	public static final String COL_VERSION_MESSAGES = "versionMessages";
	public static final String COL_VERSION_CONSTANTS = "versionConstants";
	public static final String COL_VERSION_WELCOME_DEAL = "versionWelcomeDeal";

	private Context context;
	private Helper helper;
	private SQLiteDatabase db;

	public final class DataNotFoundException extends Exception
	{
		private static final long serialVersionUID = 5351268841990748090L;
	}

	private static class Helper extends SQLiteOpenHelper
	{
		public Helper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_VERSIONS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_INFO);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_KEYWORDS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONSTANTS);
			onCreate(db);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
		}
	}

	public DB(Context context)
	{
		checkDB(context);
		this.context = context;
		helper = new Helper(this.context);

		db = helper.getWritableDatabase();

		Cursor userCursor = getUserInfo();
		boolean succeeded = userCursor.moveToFirst();
		if (!succeeded)
			addUser();
		userCursor.close();
	}

	public Cursor getUserInfo()
	{
		return this.db.rawQuery("select * from " + TABLE_USER_INFO, null);
	}

	public Cursor getCitiesList()
	{
		return db.rawQuery("select " + COL_CITY_NAME + " from " + TABLE_CITIES, null);
	}

	public Cursor getCategoriesList()
	{
		return db.rawQuery(
				"select " + COL_CATEGORY_NAME + "," + COL_CATEGORY_SEL + "," + COL_CATEGORY_IMGURL + " from " + TABLE_CATEGORIES, null);
	}

	public Cursor getKeywordsList()
	{
		return db.rawQuery("select " + COL_KEYWORD_NAME + " from " + TABLE_KEYWORDS, null);
	}

	public String getMessage(String key)
	{
		try
		{
			String ret = null;
			StringBuffer sb = new StringBuffer();
			sb.append("select ");
			sb.append(COL_MESSAGE_VALUE);
			sb.append(" from ");
			sb.append(TABLE_MESSAGES);
			sb.append(" where ");
			sb.append(COL_MESSAGE_KEY);
			sb.append("=?");
			Cursor cursor = db.rawQuery(sb.toString(), new String[] { key });
			boolean succeeded = cursor.moveToFirst();
			if (succeeded)
				ret = cursor.getString(0);
			cursor.close();
			return ret;
		}
		catch (Exception e)
		{
		}
		return AppConstants.EMPTY_STRING;
	}

	public String getConstant(String key)
	{
		try
		{
			String ret = null;
			StringBuffer sb = new StringBuffer();
			sb.append("select ");
			sb.append(COL_CONSTANT_VALUE);
			sb.append(" from ");
			sb.append(TABLE_CONSTANTS);
			sb.append(" where ");
			sb.append(COL_CONSTANT_KEY);
			sb.append("=?");
			Cursor cursor = db.rawQuery(sb.toString(), new String[] { key });
			boolean succeeded = cursor.moveToFirst();
			if (succeeded)
				ret = cursor.getString(0);
			cursor.close();
			return ret;
		}
		catch (Exception e)
		{
		}
		return AppConstants.EMPTY_STRING;
	}

	public String getMDN()
	{
		String ret = null;
		Cursor userCursor = getUserInfo();
		boolean succeeded = userCursor.moveToFirst();
		if (succeeded)
			ret = userCursor.getString(COL_USER_MDN_IDX);
		userCursor.close();
		return ret;
	}

	public String getAndroidId()
	{
		String ret = null;
		Cursor userCursor = getUserInfo();
		boolean succeeded = userCursor.moveToFirst();
		if (succeeded)
			ret = userCursor.getString(COL_USER_ANDROID_ID_IDX);
		userCursor.close();
		return ret;
	}

	public String getIMSI()
	{
		String ret = null;
		Cursor userCursor = getUserInfo();
		boolean succeeded = userCursor.moveToFirst();
		if (succeeded)
			ret = userCursor.getString(COL_USER_IMSI_IDX);
		userCursor.close();
		return ret;
	}

	public String getGCMId()
	{
		String ret = null;
		Cursor userCursor = getUserInfo();
		boolean succeeded = userCursor.moveToFirst();
		if (succeeded)
			ret = userCursor.getString(COL_USER_GCM_ID_IDX);
		userCursor.close();
		return ret;
	}

	public String getEmailId()
	{
		String ret = null;
		Cursor userCursor = getUserInfo();
		boolean succeeded = userCursor.moveToFirst();
		if (succeeded)
			ret = userCursor.getString(COL_USER_EMAIL_IDX);
		userCursor.close();
		return ret;
	}

	public boolean isUserSubscribedToGCM()
	{
		boolean ret = false;
		Cursor userCursor = getUserInfo();
		boolean succeeded = userCursor.moveToFirst();
		if (succeeded)
			ret = userCursor.getInt(COL_USER_IS_SUBSCRIBED_TO_GCM_IDX) > 0 ? true : false;
		userCursor.close();
		return ret;
	}

	public String getUserSubscriptionStatus()
	{
		String ret = null;
		Cursor cursor = getUserInfo();
		boolean succeeded = cursor.moveToFirst();
		if (succeeded)
			ret = cursor.getString(COL_USER_SUBSCRIPTION_STATUS_IDX);
		cursor.close();
		return ret;
	}

	
	public String isDailyMsgEnabled()
	{
		String ret = null;
		Cursor cursor = getUserInfo();
		boolean succeeded = cursor.moveToFirst();
		if (succeeded)
			ret = cursor.getString(COL_USER_DAILY_MSG_IDX);
		cursor.close();
		return ret;
	}
	public boolean isUpgradeAvailable()
	{
		boolean ret = false;
		Cursor cursor = getUserInfo();
		boolean succeeded = cursor.moveToFirst();
		if (succeeded)
			ret = cursor.getInt(COL_USER_UPGRADE_IDX) > 0 ? true : false;
		cursor.close();
		return ret;
	}

	public boolean isUpgradeForced()
	{
		boolean ret = false;
		Cursor cursor = getUserInfo();
		boolean succeeded = cursor.moveToFirst();
		if (succeeded)
			ret = cursor.getInt(COL_USER_UPGRADE_FORCED_IDX) > 0 ? true : false;
		cursor.close();
		return ret;
	}

	public String getOTP()
	{
		String ret = null;
		Cursor cursor = getUserInfo();
		boolean succeeded = cursor.moveToFirst();
		if (succeeded)
			ret = cursor.getString(COL_USER_OTP_IDX);
		cursor.close();
		return ret;
	}

	public long getOTPTimestamp()
	{
		long ret = 0;
		Cursor cursor = getUserInfo();
		boolean succeeded = cursor.moveToFirst();
		if (succeeded)
			ret = cursor.getLong(COL_USER_OTP_TIMESTAMP_IDX);
		cursor.close();
		return ret;
	}

	public String getOTPMessage()
	{
		String ret = null;
		Cursor cursor = getUserInfo();
		boolean succeeded = cursor.moveToFirst();
		if (succeeded)
			ret = cursor.getString(COL_USER_OTP_MESSAGE_IDX);
		cursor.close();
		return ret;
	}

	public int getNotificationFrequnecy()
	{
		int ret = -1;
		Cursor cursor = getUserInfo();
		boolean succeeded = cursor.moveToFirst();
		if (succeeded)
			ret = cursor.getInt(COL_USER_NOTIFICATION_FREQUENCY_IDX);
		cursor.close();
		return ret;
	}

	public String getUpgradeMessage()
	{
		String ret = null;
		Cursor cursor = getUserInfo();
		boolean succeeded = cursor.moveToFirst();
		if (succeeded)
			ret = cursor.getString(COL_USER_UPGRADE_MESSAGE_IDX);
		cursor.close();
		return ret;
	}

	public String getUpgradeURL()
	{
		String ret = null;
		Cursor cursor = getUserInfo();
		boolean succeeded = cursor.moveToFirst();
		if (succeeded)
			ret = cursor.getString(COL_USER_UPGRADE_URL_IDX);
		cursor.close();
		return ret;
	}

	public String getUserCity()
	{
		String city = null;
		String defaultCity = null;

		Cursor userCursor = getUserInfo();
		boolean succeeded = userCursor.moveToFirst();
		if (!succeeded)
		{
			userCursor.close();
			return null;
		}
		String userCity = userCursor.getString(COL_USER_CITY_IDX);
		String userDefaultCity = userCursor.getString(COL_USER_CITY_DEFAULT_IDX);
		userCursor.close();
		if (!Utils.isNothing(userCity))
		{
			/*succeeded = false;
			Cursor citiesCursor = this.db.rawQuery("select " + COL_CITY_NAME + " from " + TABLE_CITIES + " where " + COL_CITY_NAME + "='"
					+ userCity.replaceAll("'", "''") + "'", null);
			succeeded = citiesCursor.moveToFirst();
			System.out.println("succeede"+succeeded);
			
			citiesCursor.close();
			if (succeeded)
				return userCity;*/
			return userCity;
		}

		/*if (userCity != null && userCity.equals(AppConstants.EMPTY_STRING))
		{
			
			succeeded = false;
			Cursor citiesCursor1 = this.db.rawQuery("select " + COL_CITY_NAME + " from " + TABLE_CITIES + " LIMIT 1", null);
			succeeded = citiesCursor1.moveToFirst();
			if (succeeded)
				city = citiesCursor1.getString(0);
			
			System.out.println("city in emypty sy=tring ***"+city);
			citiesCursor1.close();
		}
*/
		if (userDefaultCity != null && !userDefaultCity.equalsIgnoreCase(AppConstants.EMPTY_STRING))
		{
			succeeded = false;
			Cursor defaultCityCursor = this.db.rawQuery("select " + COL_CITY_NAME + " from " + TABLE_CITIES + " where " + COL_CITY_NAME
					+ "='" + userDefaultCity.replaceAll("'", "''") + "'", null);
			succeeded = defaultCityCursor.moveToFirst();
			if (succeeded)
				defaultCity = defaultCityCursor.getString(0);
			defaultCityCursor.close();
		}

		if (!Utils.isNothing(defaultCity))
			city = defaultCity;

		if (!Utils.isNothing(city))
			updateCity(city);
		
		return city;
	}
	
	public String getUserDefaultCity(){
		String defaultCity ="";
		Cursor userCursor = getUserInfo();
		boolean succeeded = userCursor.moveToFirst();
		if (!succeeded)
		{
			userCursor.close();
			return null;
		}
		defaultCity = userCursor.getString(COL_USER_CITY_DEFAULT_IDX);
		userCursor.close();
		return defaultCity;
	}

	public String getVersion(String key)
	{
		String ret = null;
		Cursor cursor = db.rawQuery("select " + key + " from " + TABLE_VERSIONS, null);
		boolean succeeded = cursor.moveToFirst();
		if (succeeded)
			ret = cursor.getString(0);
		cursor.close();

		if (TextUtils.isEmpty(ret))
			ret = "1";
		if (key.equalsIgnoreCase(DB.COL_VERSION_CATEGORIES))
			ret = "0";
		return ret;
	}

	public void updateAllCategories()
	{
		db.rawQuery("update " + TABLE_CATEGORIES + " set " + COL_CATEGORY_SEL + "=1", null);
	}

	public void updateVersion(String key, String version)
	{
		ContentValues values = new ContentValues();
		values.put(key, version);
		db.update(TABLE_VERSIONS, values, null, null);
	}

	public void updateMDN(String mdn)
	{
		ContentValues values = new ContentValues();
		values.put(COL_USER_MDN, mdn);
		db.update(TABLE_USER_INFO, values, null, null);
	}

	public void updateAndroidId(String androidId)
	{
		ContentValues values = new ContentValues();
		values.put(COL_USER_ANDROID_ID, androidId);
		db.update(TABLE_USER_INFO, values, null, null);
	}

	public void updateIMSI(String imsi)
	{
		ContentValues values = new ContentValues();
		values.put(COL_USER_IMSI, imsi);
		db.update(TABLE_USER_INFO, values, null, null);
	}

	public void updateGCMId(String gcmId)
	{
		ContentValues values = new ContentValues();
		values.put(COL_USER_GCM_ID, gcmId);
		db.update(TABLE_USER_INFO, values, null, null);
	}

	public void updateCity(String city)
	{
		ContentValues values = new ContentValues();
		values.put(COL_USER_CITY, city);
		int i  = db.update(TABLE_USER_INFO, values, null, null);
	}

	public void updateDefaultCity(String defaultCity)
	{
		ContentValues values = new ContentValues();
		values.put(COL_USER_CITY_DEFAULT, defaultCity);
		db.update(TABLE_USER_INFO, values, null, null);
	}

	public void updateEmail(String email)
	{
		ContentValues values = new ContentValues();
		values.put(COL_USER_EMAIL, email);
		db.update(TABLE_USER_INFO, values, null, null);
	}

	public void updateIsUserSubscribedToGCM(boolean isUserSubscribed)
	{
		ContentValues values = new ContentValues();
		values.put(COL_USER_IS_SUBSCRIBED_TO_GCM, isUserSubscribed ? 1 : 0);
		db.update(TABLE_USER_INFO, values, null, null);
	}

	public void updateUserSubscriptionStatus(String userSubscriptionStatus)
	{
		ContentValues values = new ContentValues();
		values.put(COL_USER_SUBSCRIPTION_STATUS, userSubscriptionStatus);
		db.update(TABLE_USER_INFO, values, null, null);
	}

	public void updateUpgradeAvailable(boolean isUpgradeAvailable)
	{
		ContentValues values = new ContentValues();
		values.put(COL_USER_UPGRADE, isUpgradeAvailable ? 1 : 0);
		db.update(TABLE_USER_INFO, values, null, null);
	}

	public void updateUpgradeForced(boolean isUpgradeForced)
	{
		ContentValues values = new ContentValues();
		values.put(COL_USER_UPGRADE_FORCED, isUpgradeForced ? 1 : 0);
		db.update(TABLE_USER_INFO, values, null, null);
	}

	public void updateUpgradeMessage(String upgradeMessage)
	{
		ContentValues values = new ContentValues();
		values.put(COL_USER_UPGRADE_MESSAGE, upgradeMessage);
		db.update(TABLE_USER_INFO, values, null, null);
	}

	public void updateUpgradeURL(String upgradeURL)
	{
		ContentValues values = new ContentValues();
		values.put(COL_USER_UPGRADE_URL, upgradeURL);
		db.update(TABLE_USER_INFO, values, null, null);
	}

	public void updateOTP(String otp)
	{	
		ContentValues values = new ContentValues();
		values.put(COL_USER_OTP, otp);
		db.update(TABLE_USER_INFO, values, null, null);
	}

	public void updateOTPTimestamp(long otpTimestamp)
	{
		ContentValues values = new ContentValues();
		values.put(COL_USER_OTP_TIMESTAMP, otpTimestamp);
		db.update(TABLE_USER_INFO, values, null, null);
	}

	public void updateOTPMessage(String otpMessage)
	{
		ContentValues values = new ContentValues();
		values.put(COL_USER_OTP_MESSAGE, otpMessage);
		db.update(TABLE_USER_INFO, values, null, null);
	}

	public void updateNotificationFrequency(int notificationFrequency)
	{
		ContentValues values = new ContentValues();
		values.put(COL_USER_NOTIFICATION_FREQUENCY, notificationFrequency);
		db.update(TABLE_USER_INFO, values, null, null);
	}
	
	public void updateIsDailyMagEnabled(String isDailyMsgEnabled)
	{
		ContentValues values = new ContentValues();
		values.put(COL_USER_IS_DAILY_MSG_ENABLED, isDailyMsgEnabled);
		db.update(TABLE_USER_INFO, values, null, null);
	}

	public void addUser()
	{
		deleteAllUserInfo();
		ContentValues values = new ContentValues();
		values.put(DB.COL_USER_MDN, AppConstants.EMPTY_STRING);
		values.put(DB.COL_USER_ANDROID_ID, AppConstants.EMPTY_STRING);
		values.put(DB.COL_USER_IMSI, AppConstants.EMPTY_STRING);
		values.put(DB.COL_USER_GCM_ID, AppConstants.EMPTY_STRING);
		values.put(DB.COL_USER_CITY, AppConstants.EMPTY_STRING);
		values.put(DB.COL_USER_CITY_DEFAULT, AppConstants.EMPTY_STRING);
		values.put(DB.COL_USER_EMAIL, AppConstants.EMPTY_STRING);
		values.put(DB.COL_USER_IS_SUBSCRIBED_TO_GCM, 0);
		values.put(DB.COL_USER_SUBSCRIPTION_STATUS, AppConstants.EMPTY_STRING);
		values.put(DB.COL_USER_UPGRADE, 0);
		values.put(DB.COL_USER_UPGRADE_FORCED, 0);
		values.put(DB.COL_USER_UPGRADE_MESSAGE, AppConstants.EMPTY_STRING);
		values.put(DB.COL_USER_OTP, AppConstants.EMPTY_STRING);
		values.put(DB.COL_USER_OTP_TIMESTAMP, 0);
		values.put(DB.COL_USER_OTP_MESSAGE, AppConstants.EMPTY_STRING);
		values.put(DB.COL_USER_NOTIFICATION_FREQUENCY, -1);
		values.put(DB.COL_USER_IS_DAILY_MSG_ENABLED, AppConstants.EMPTY_STRING);
		db.insert(TABLE_USER_INFO, null, values);
	}

	public void addVersions()
	{
		deleteAllVersions();
		ContentValues values = new ContentValues();
		values.put(COL_VERSION_LOCATIONS, AppConstants.EMPTY_STRING);
		values.put(COL_VERSION_CATEGORIES, AppConstants.EMPTY_STRING);
		values.put(COL_VERSION_KEYWORDS, AppConstants.EMPTY_STRING);
		values.put(COL_VERSION_HELP, AppConstants.EMPTY_STRING);
		values.put(COL_VERSION_ABOUT, AppConstants.EMPTY_STRING);
		values.put(COL_VERSION_FAQ, AppConstants.EMPTY_STRING);
		values.put(COL_VERSION_TNC, AppConstants.EMPTY_STRING);
		values.put(COL_VERSION_MESSAGES, AppConstants.EMPTY_STRING);
		values.put(COL_VERSION_CONSTANTS, AppConstants.EMPTY_STRING);
		values.put(COL_VERSION_WELCOME_DEAL, AppConstants.EMPTY_STRING);
		db.insert(TABLE_VERSIONS, null, values);
	}

	public void addCity(String cityName)
	{
		ContentValues values = new ContentValues();
		values.put(COL_CITY_NAME, cityName);
		db.insert(TABLE_CITIES, null, values);
	}

	public void addCities(List<String> cities)
	{
		InsertHelper ih = new InsertHelper(db, TABLE_CITIES);
		final int nameColumn = ih.getColumnIndex(COL_CITY_NAME);

		try
		{
			db.beginTransaction();
			for (String city : cities)
			{
				ih.prepareForInsert();
				ih.bind(nameColumn, city);
				ih.execute();
			}
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
			ih.close();
		}
	}

	public void addCategory(String categoryName, boolean categoryIsSelected, String imgUrl)
	{
		ContentValues values = new ContentValues();
		values.put(COL_CATEGORY_NAME, categoryName);
		values.put(COL_CATEGORY_SEL, categoryIsSelected ? 1 : 0);
		values.put(COL_CATEGORY_IMGURL, imgUrl);
		db.insert(TABLE_CATEGORIES, null, values);
	}

	public void addCategories(List<CategoryDTO> categories)
	{
		InsertHelper ih = new InsertHelper(db, TABLE_CATEGORIES);

		final int nameColumn = ih.getColumnIndex(COL_CATEGORY_NAME);
		final int selectedColumn = ih.getColumnIndex(COL_CATEGORY_SEL);
		final int urlColumn = ih.getColumnIndex(COL_CATEGORY_IMGURL);

		try
		{
			db.beginTransaction();
			for (CategoryDTO categoryObj : categories)
			{
				ih.prepareForInsert();
				ih.bind(nameColumn, categoryObj.name);
				ih.bind(selectedColumn, categoryObj.isSelected ? 1 : 0);
				ih.bind(urlColumn, categoryObj.imgUrl);
				ih.execute();
			}
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
			ih.close();
		}
	}

	public void addKeyword(String keywordName, boolean keywordIsSelected)
	{
		ContentValues values = new ContentValues();
		values.put(COL_KEYWORD_NAME, keywordName);
		values.put(COL_KEYWORD_SEL, keywordIsSelected ? 1 : 0);
		db.insert(TABLE_KEYWORDS, null, values);
	}

	public void addKeywords(List<String> keywords)
	{
		InsertHelper ih = new InsertHelper(db, TABLE_KEYWORDS);

		final int nameColumn = ih.getColumnIndex(COL_KEYWORD_NAME);
		final int selectedColumn = ih.getColumnIndex(COL_KEYWORD_SEL);

		try
		{
			db.beginTransaction();
			for (String keyword : keywords)
			{
				ih.prepareForInsert();
				ih.bind(nameColumn, keyword);
				ih.bind(selectedColumn, false);
				ih.execute();
			}
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
			ih.close();
		}
	}

	public void addMessage(String key, String message)
	{
		ContentValues values = new ContentValues();
		values.put(COL_MESSAGE_KEY, key);
		values.put(COL_MESSAGE_VALUE, message);
		db.insert(TABLE_MESSAGES, null, values);
	}

	public void addMessages(HashMap<String, String> messages)
	{
		InsertHelper ih = new InsertHelper(db, TABLE_MESSAGES);

		final int keyColumn = ih.getColumnIndex(COL_MESSAGE_KEY);
		final int valueColumn = ih.getColumnIndex(COL_MESSAGE_VALUE);

		Set<Entry<String, String>> messagesCollection = messages.entrySet();
		try
		{
			db.beginTransaction();
			for (Entry<String, String> messageEntry : messagesCollection)
			{
				ih.prepareForInsert();
				ih.bind(keyColumn, messageEntry.getKey());
				ih.bind(valueColumn, messageEntry.getValue());
				ih.execute();
			}
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
			ih.close();
		}
	}

	public void updateMessage(String key, String message)
	{
		ContentValues values = new ContentValues();
		values.put(COL_MESSAGE_KEY, key);
		values.put(COL_MESSAGE_VALUE, message);
		db.update(TABLE_MESSAGES, values, COL_MESSAGE_KEY + " = ?", new String[] { String.valueOf(key) });
	}

	public void addConstant(String key, String constant)
	{
		ContentValues values = new ContentValues();
		values.put(COL_CONSTANT_KEY, key);
		values.put(COL_CONSTANT_VALUE, constant);
		db.insert(TABLE_CONSTANTS, null, values);
	}

	public void addConstants(HashMap<String, String> constants)
	{
		InsertHelper ih = new InsertHelper(db, TABLE_CONSTANTS);

		final int keyColumn = ih.getColumnIndex(COL_CONSTANT_KEY);
		final int valueColumn = ih.getColumnIndex(COL_CONSTANT_VALUE);

		Set<Entry<String, String>> constantsCollection = constants.entrySet();
		try
		{
			db.beginTransaction();
			for (Entry<String, String> constantEntry : constantsCollection)
			{
				ih.prepareForInsert();
				ih.bind(keyColumn, constantEntry.getKey());
				ih.bind(valueColumn, constantEntry.getValue());
				ih.execute();
			}
			db.setTransactionSuccessful();
		}
		finally
		{
			db.endTransaction();
			ih.close();
		}
	}

	public void updateConstant(String key, String constant)
	{
		ContentValues values = new ContentValues();
		values.put(COL_CONSTANT_VALUE, constant);
		db.update(TABLE_CONSTANTS, values, COL_CONSTANT_KEY + " = ?", new String[] { String.valueOf(key) });
	}

	public void clearCategoriesSelectedStatus()
	{
		ContentValues values = new ContentValues();
		values.put(COL_CATEGORY_SEL, 0);
		db.update(TABLE_CATEGORIES, values, null, null);

		db.execSQL("UPDATE tblCategories SET categorySelected=0");
	}

	public void setCategoriesSelectedStatus(String queryPart)
	{
		db.execSQL("UPDATE tblCategories SET categorySelected=1 WHERE categoryName IN " + queryPart);
	}

	public void deleteAllVersions()
	{
		db.delete(TABLE_VERSIONS, null, null);
	}

	public void deleteAllUserInfo()
	{
		db.delete(TABLE_USER_INFO, null, null);
	}

	public void deleteAllCities()
	{
		db.delete(TABLE_CITIES, null, null);
	}

	public void deleteAllCategories()
	{
		db.delete(TABLE_CATEGORIES, null, null);
	}

	public void deleteAllKeywords()
	{
		db.delete(TABLE_KEYWORDS, null, null);
	}

	public void deleteAllMessages()
	{
		db.delete(TABLE_MESSAGES, null, null);
	}

	public void deleteAllConstants()
	{
		db.delete(TABLE_CONSTANTS, null, null);
	}

	public void checkDB(Context ctx)
	{
		try
		{

			// android default database location is :
			// /data/data/youapppackagename/databases/
			String packageName = ctx.getPackageName();
			String destPath = "/data/data/" + packageName + "/databases";
			String fullPath = "/data/data/" + packageName + "/databases/" + DATABASE_NAME;

			// this database folder location
			File f = new File(destPath);

			// this database file location
			File obj = new File(fullPath);

			// check if databases folder exists or not. if not create it
			if (!f.exists())
			{
				f.mkdirs();
				f.createNewFile();
			}

			// check database file exists or not, if not copy database from
			// assets
			if (!obj.exists())
			{
				this.CopyDB(ctx, fullPath);
			}

		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();

		}
		catch (IOException e)
		{
			e.printStackTrace();

		}
	}

	public void CopyDB(Context ctx, String path) throws IOException
	{
		// Log.d("Time test : ", "Copying database locally : " +
		// Utils.getCurrentTimeStamp());

		InputStream databaseInput = null;
		String outFileName = path;
		OutputStream databaseOutput = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;

		// open database file from asset folder
		databaseInput = ctx.getAssets().open(DATABASE_NAME);
		while ((length = databaseInput.read(buffer)) > 0)
		{
			databaseOutput.write(buffer, 0, length);
			databaseOutput.flush();
		}
		databaseInput.close();

		databaseOutput.flush();
		databaseOutput.close();

		// Log.d("Time test : ", "Copying database locally completed : " +
		// Utils.getCurrentTimeStamp());
	}

	public void deleteSelf()
	{
		try
		{
			this.context.deleteDatabase(DATABASE_NAME);
			this.context.deleteFile(DATABASE_NAME);
			String packageName = this.context.getPackageName();
			String fullPath = "/data/data/" + packageName + "/databases/" + DATABASE_NAME;
			File dbfile = new File(fullPath);
			dbfile.delete();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}