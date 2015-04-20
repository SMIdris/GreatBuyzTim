package com.turacomobile.greatbuyz.service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.util.DisplayMetrics;

import com.turacomobile.greatbuyz.GreatBuyzApplication;
import com.turacomobile.greatbuyz.R;
import com.turacomobile.greatbuyz.data.Contact;
import com.turacomobile.greatbuyz.data.Coupon;
import com.turacomobile.greatbuyz.data.Deal;
import com.turacomobile.greatbuyz.data.Location;
import com.turacomobile.greatbuyz.data.Merchant;
import com.turacomobile.greatbuyz.data.NotificationDTO;
import com.turacomobile.greatbuyz.data.PhoneLine;
import com.turacomobile.greatbuyz.data.Purchase;
import com.turacomobile.greatbuyz.data.TnC;
import com.turacomobile.greatbuyz.utils.AppConstants;
import com.turacomobile.greatbuyz.utils.Utils;

public class ResponseParser
{
	private static final DataController _data = GreatBuyzApplication.getDataController();

	
	
	public static class CategoryDTO
	{
		public CategoryDTO(String name, boolean isSelected, String imgUrl)
		{
			this.name = name;
			this.isSelected = isSelected;
			this.imgUrl = imgUrl;
		}

		public String name;
		public boolean isSelected;
		public String imgUrl;
	}
	public static class ServiceResponse
	{
		public int _statusCode;
		public Object _resultObject;

		public ServiceResponse(int statusCode, Object resultObject)
		{
			_statusCode = statusCode;
			_resultObject = resultObject;
		}
	}

	public static ServiceResponse getServiceResponse(String data) throws JSONException
	{
		System.out.println("data   *******"+data);
		//JSONObject responseObject = new JSONObject(data).optJSONObject(AppConstants.JSONKeys.RESPONSE);
		JSONObject responseObject = new JSONObject(data);
		if (responseObject == null)
			return new ServiceResponse(0, null);
		int statusCode = responseObject.optInt(AppConstants.JSONKeys.CODE, -1);
		Object obj = responseObject.opt(AppConstants.JSONKeys.RESPONSE);
		return new ServiceResponse(statusCode, obj);
	}

	public static JSONObject parseGetInfo(JSONObject resultObject) throws JSONException
	{
		JSONObject versions = new JSONObject();
		JSONObject upgradeObject = resultObject.optJSONObject(AppConstants.JSONKeys.CLIENT_UPGRADE_PROPERTIES);
		if (upgradeObject != null)
			parseUpgrade(upgradeObject);

		JSONArray versionsObject = resultObject.optJSONArray(AppConstants.JSONKeys.DATA_VERSION);
		if (versionsObject != null)
			parseVersions(versionsObject, versions);

		JSONObject userObject = resultObject.optJSONObject(AppConstants.JSONKeys.USER);
		if (userObject != null)
			parseSubscription(userObject);

		String newVersion = versions.optString(AppConstants.JSONKeys.CITIES);
		if (!Utils.isNothing(newVersion))
		{
			/*
			 * JSONArray citiesArray =
			 * resultObject.optJSONArray(AppConstants.JSONKeys
			 * .LOCATION_MAPPING); if (citiesArray != null &&
			 * citiesArray.length() > 0) { parseCities(citiesArray);
			 * _data.updateVersion(DB.COL_VERSION_LOCATIONS, newVersion); } else
			 * if (citiesArray == null) { JSONObject cityObject =
			 * resultObject.optJSONObject
			 * (AppConstants.JSONKeys.LOCATION_MAPPING); if (cityObject != null)
			 * { String city =
			 * cityObject.optString(AppConstants.JSONKeys.DEFAULT_LOCATION); if
			 * (!Utils.isNothing(city)) { _data.updateDefaultCity(city);
			 * 
			 * _data.updateVersion(DB.COL_VERSION_LOCATIONS, newVersion);
			 * 
			 * String userCity = _data.getUserCity();
			 * if(Utils.isNothing(userCity)) _data.updateUserCity(city); } } }
			 */
			String oldVersion = _data.getVersion(DB.COL_VERSION_LOCATIONS);
			
			if (!oldVersion.equals(newVersion)) {
				
				JSONObject cityObject =	  resultObject.optJSONObject(AppConstants.JSONKeys.LOCATION_MAPPING);
				if (cityObject != null)
				{
					String city = cityObject
							.optString(AppConstants.JSONKeys.DEFAULT_LOCATION);
					
					if (!Utils.isNothing(city)) {
						_data.updateDefaultCity(city);
					}
					String selectedCity = cityObject
							.optString(AppConstants.JSONKeys.OM_LOCATION);
					
					if (!Utils.isNothing(selectedCity)) {
						_data.updateUserCity(selectedCity);
					}
				}
				SharedPreferences.Editor editor = GreatBuyzApplication
						.getApplication().getSharedPreferences().edit();
				editor.putString(DB.COL_VERSION_LOCATIONS, newVersion);
				editor.commit();
			}
			versions.remove(AppConstants.JSONKeys.CITIES);
		}

		newVersion = versions.optString(AppConstants.JSONKeys.CATEGORIES);
		if (!Utils.isNothing(newVersion))
		{
			JSONArray categoriesArray = resultObject.optJSONArray(AppConstants.JSONKeys.CATEGORY_MAPPING);
			if (categoriesArray != null && categoriesArray.length() > 0)
			{
				parseCategories(categoriesArray);
				_data.updateVersion(DB.COL_VERSION_CATEGORIES, newVersion);
			}
			versions.remove(AppConstants.JSONKeys.CATEGORIES);
		}

		newVersion = versions.optString(AppConstants.JSONKeys.KEYWORDS);
		if (!Utils.isNothing(newVersion))
		{
			String oldVersion = _data.getVersion(DB.COL_VERSION_KEYWORDS);
			if (!oldVersion.equals(newVersion))
			{
				SharedPreferences.Editor editor = GreatBuyzApplication.getApplication().getSharedPreferences().edit();
				editor.putString(DB.COL_VERSION_KEYWORDS, newVersion);
				editor.commit();
			}
			versions.remove(AppConstants.JSONKeys.KEYWORDS);
		}

		newVersion = versions.optString(AppConstants.JSONKeys.MESSAGES);
		if (!Utils.isNothing(newVersion))
		{
			JSONArray messagesArray = resultObject.optJSONArray(AppConstants.JSONKeys.APP_CONFIG_MESSAGE);
			if (messagesArray != null)
			{
				System.out.println("parse message called **");
				parseMessages(messagesArray);
				_data.updateVersion(DB.COL_VERSION_MESSAGES, newVersion);
			}
			versions.remove(AppConstants.JSONKeys.MESSAGES);
		}

		newVersion = versions.optString(AppConstants.JSONKeys.CONSTANTS);
		if (!Utils.isNothing(newVersion))
		{
			JSONArray constantsArray = resultObject.optJSONArray(AppConstants.JSONKeys.APP_CONSTANT);
			if (constantsArray != null)
			{
				parseConstants(constantsArray);
				_data.updateVersion(DB.COL_VERSION_CONSTANTS, newVersion);
			}
			versions.remove(AppConstants.JSONKeys.CONSTANTS);
		}else{
			JSONArray constantsArray = resultObject.optJSONArray(AppConstants.JSONKeys.APP_CONSTANT);
			if(constantsArray != null)
			for (int i = 0; i < constantsArray.length(); i++) {
				//System.out.println(constantsArray.getJSONObject(i).getString(AppConstants.JSONKeys.NAME)+" "+constantsArray.getJSONObject(i).getString(AppConstants.JSONKeys.VALUE));
				if(constantsArray.getJSONObject(i).getString(AppConstants.JSONKeys.NAME).equals(AppConstants.Constants.serverTimeZone)){
					_data.updateConstant(AppConstants.Constants.serverTimeZone, constantsArray.getJSONObject(i).getString(AppConstants.JSONKeys.VALUE));
				}
			}
		}

		// //System.out.println("Parse GetInfo : return versions ");
		return versions;
	}

	public static void parseRegisterUserInfo(String otp) throws JSONException
	{
		_data.updateOTP(otp);
		// System.out.println("OTP: " + otp);
		 //System.out.println("System.currentTimeMillis()"+System.currentTimeMillis());
		_data.updateOTPTimestamp(System.currentTimeMillis());
	}

	public static void parseSubscription(JSONObject subscriptionObject) throws JSONException
	{
		String mdn = subscriptionObject.optString(AppConstants.JSONKeys.MSISDN, null);
		if (!Utils.isNothing(mdn))
		{
			_data.updateMDN(mdn);
			String subscriptionStatus = subscriptionObject.optString(AppConstants.JSONKeys.SUBSCRIBE);
			_data.updateUserSubscriptionStatus(subscriptionStatus);
		}
		else
		{
			_data.updateMDN(AppConstants.EMPTY_STRING);
			_data.updateUserSubscriptionStatus(AppConstants.UserActivationStatus.NOTFOUND);
		}
		int frequency = subscriptionObject.optInt(AppConstants.JSONKeys.NOTIFICATION_FREQUENCY, -100);
		_data.updateNotificationFrequency(frequency);
		
		//_data.updateIsDailyMsgEnabled("true");
				boolean isDailyMsgEnabled = subscriptionObject.optBoolean(AppConstants.JSONKeys.isDailyMsgEnabled);
				//System.out.println("in parseSubscription isDailyMsgEnabled"+isDailyMsgEnabled);
				if (isDailyMsgEnabled)
				{
					_data.updateIsDailyMsgEnabled("true");
				}
				else{
					_data.updateIsDailyMsgEnabled("false");
				}
	}

	public static String findMDN(String response)
	{
		if (Utils.isNothing(response))
			return null;
		int startIndex = response.indexOf(AppConstants.JSONKeys.MSISDN_TAG_START);
		if (startIndex == -1)
			return null;
		startIndex += AppConstants.JSONKeys.MSISDN_TAG_START.length();
		int endIndex = response.indexOf(AppConstants.JSONKeys.MSISDN_TAG_END);
		if (endIndex == -1)
			return null;
		String mdn = null;
		try
		{
			mdn = response.substring(startIndex, endIndex);
		}
		catch (Exception e)
		{
		}

		return mdn;
	}

	public static Map<String, String> getOperatorMDNURLHeaders(String strHeaders) throws JSONException
	{
		Map<String, String> headers = null;
		JSONObject headerParamsObject = new JSONObject(strHeaders);
		if (headerParamsObject != null)
		{
			headers = new HashMap<String, String>();
			Iterator<?> keys = headerParamsObject.keys();

			while (keys.hasNext())
			{
				String key = (String) keys.next();
				String value = headerParamsObject.optString(key);
				headers.put(key, value);
			}
		}

		return headers;
	}

	public static void parseVersions(JSONArray versionsArray, JSONObject values) throws JSONException
	{
		HashMap<String, String> newVersions = new HashMap<String, String>();
		for (int i = 0; i < versionsArray.length(); i++)
		{
			JSONObject versionObj = (JSONObject) versionsArray.get(i);
			String source = versionObj.getString(AppConstants.JSONKeys.SOURCE);
			String version = versionObj.getString(AppConstants.JSONKeys.CURRENT_VERSION);
			newVersions.put(source, version);
		}

		String prevVersion = _data.getVersion(DB.COL_VERSION_LOCATIONS);
		String newVersion = newVersions.get(AppConstants.JSONKeys.CITIES);
		if (!newVersion.equalsIgnoreCase(prevVersion))
			values.put(AppConstants.JSONKeys.CITIES, newVersion);

		prevVersion = _data.getVersion(DB.COL_VERSION_CATEGORIES);
		newVersion = newVersions.get(AppConstants.JSONKeys.CATEGORIES);
		if (!newVersion.equalsIgnoreCase(prevVersion))
			values.put(AppConstants.JSONKeys.CATEGORIES, newVersion);

		prevVersion = _data.getVersion(DB.COL_VERSION_KEYWORDS);
		newVersion = newVersions.get(AppConstants.JSONKeys.KEYWORDS);
		if (!newVersion.equalsIgnoreCase(prevVersion))
			values.put(AppConstants.JSONKeys.KEYWORDS, newVersion);

		prevVersion = _data.getVersion(DB.COL_VERSION_MESSAGES);
		newVersion = newVersions.get(AppConstants.JSONKeys.MESSAGES);
		if (!newVersion.equalsIgnoreCase(prevVersion))
			values.put(AppConstants.JSONKeys.MESSAGES, newVersion);

		prevVersion = _data.getVersion(DB.COL_VERSION_CONSTANTS);
		newVersion = newVersions.get(AppConstants.JSONKeys.CONSTANTS);
		if (!newVersion.equalsIgnoreCase(prevVersion))
			values.put(AppConstants.JSONKeys.CONSTANTS, newVersion);
	}

	public static void parseUpgrade(JSONObject upgradeObject) throws JSONException
	{
		_data.updateIsUpgradeAvailable(false);

		String message = upgradeObject.optString(AppConstants.JSONKeys.MSG);
		if (!Utils.isNothing(message))
		{
			_data.updateIsUpgradeAvailable(true);
			_data.updateUpgradeMessage(message);
			String url = upgradeObject.optString(AppConstants.JSONKeys.URL);
			_data.updateUpgradeURL(url);
			boolean forced = upgradeObject.optBoolean(AppConstants.JSONKeys.FORCE);
			_data.updateIsUpgradeForced(forced);
		}
	}

	public static void parseCities(JSONArray citiesArray) throws JSONException
	{
		if (citiesArray != null && citiesArray.length() > 0)
		{
			_data.deleteAllCities();
			List<String> cities = new ArrayList<String>();
			for (int i = 0; i < citiesArray.length(); i++) {
				cities.add(citiesArray.getString(i));
			}

			/*for (int i = 0; i < citiesArray.length(); i++)
			{
				JSONObject cityObject = citiesArray.optJSONObject(i);
				if (cityObject != null)
				{
					String name = cityObject.optString(AppConstants.JSONKeys.OM_LOCATION);
					if (!Utils.isNothing(name))
					{
						cities.add(name);
						continue;
					}

					name = null;
					name = cityObject.optString(AppConstants.JSONKeys.PREFERRED_LOCATION);
					if (!Utils.isNothing(name))
					{
						_data.updateUserCity(name);
						continue;
					}

					name = null;
					name = cityObject.optString(AppConstants.JSONKeys.DEFAULT_LOCATION);
					if (!Utils.isNothing(name))
						_data.updateDefaultCity(name);
				}
			}*/
			_data.addCities(cities);
		}
	}

	public static void parseCategories(JSONArray categoriesArray) throws JSONException
	{
		// check subscription status, if it is other that TRIAL or ACTIVE then
		// select all categories by default

		boolean selectAllCategories = true;
		String subscriptionStatus = _data.getUserSubscriptionStatus();
		if (!Utils.isNothing(subscriptionStatus)
				&& (subscriptionStatus.equalsIgnoreCase(AppConstants.UserActivationStatus.ACTIVE) || subscriptionStatus
						.equalsIgnoreCase(AppConstants.UserActivationStatus.TRIAL)))
			selectAllCategories = false;

		if (categoriesArray != null && categoriesArray.length() > 0)
		{
			_data.deleteAllCategories();
			List<CategoryDTO> categories = new ArrayList<ResponseParser.CategoryDTO>();

			for (int i = 0; i < categoriesArray.length(); i++)
			{
				JSONObject categoryObject = categoriesArray.optJSONObject(i);
				if (categoryObject != null)
				{
					String name = categoryObject.optString(AppConstants.JSONKeys.OM_CATEGORY);
					if (!Utils.isNothing(name))
					{
						boolean isSelected = categoryObject.optBoolean(AppConstants.JSONKeys.PREFERRED);
						if (selectAllCategories)
							isSelected = selectAllCategories;
						String imgUrl = null;
						int density = Utils.getDeviceScreenDensity();
						if (density <= DisplayMetrics.DENSITY_MEDIUM)
							imgUrl = categoryObject.optString(AppConstants.JSONKeys.CATEGORY_IMAGE_URL_SMALL);
						else if (density <= DisplayMetrics.DENSITY_HIGH)
							imgUrl = categoryObject.optString(AppConstants.JSONKeys.CATEGORY_IMAGE_URL_MEDIUM);
						else if (density > DisplayMetrics.DENSITY_HIGH)
							imgUrl = categoryObject.optString(AppConstants.JSONKeys.CATEGORY_IMAGE_URL_BIG);

						System.out.println("imgUrl ***"+imgUrl);
						if (Utils.isNothing(imgUrl))
							imgUrl = categoryObject.optString(AppConstants.JSONKeys.IMGURL);

						categories.add(new CategoryDTO(name, isSelected, imgUrl));
					}
				}
			}
			_data.addCategories(categories);
		}
	}

	public static void parseKeywords(JSONArray keywordsArray) throws JSONException
	{
		if (keywordsArray != null && keywordsArray.length() > 0)
		{
			// _data.deleteAllKeywords();

			List<String> keywords = new ArrayList<String>();

			for (int i = 0; i < keywordsArray.length(); i++)
			{
				JSONObject keywordObject = keywordsArray.optJSONObject(i);
				if (keywordObject != null)
				{
					String name = keywordObject.optString(AppConstants.JSONKeys.VALUE);
					if (!Utils.isNothing(name))
						keywords.add(name);
				}
			}
			_data.addKeywords(keywords);
		}
	}

	public static void parseMessages(JSONArray messagesArray) throws JSONException
	{
		if (messagesArray == null || messagesArray.length() == 0)
			return;
		_data.deleteAllMessages();

		HashMap<String, String> messages = new HashMap<String, String>();
		for (int i = 0; i < messagesArray.length(); i++)
		{
			JSONObject messageObject = messagesArray.getJSONObject(i);
			String name = messageObject.optString(AppConstants.JSONKeys.NAME);
			String value = messageObject.optString(AppConstants.JSONKeys.VALUE);
			messages.put(name, value);
		}
		_data.addMessages(messages);
	}

	public static void parseConstants(JSONArray constantsArray) throws JSONException
	{
		if (constantsArray == null || constantsArray.length() == 0)
			return;
		_data.deleteAllConstants();

		HashMap<String, String> constants = new HashMap<String, String>();
		for (int i = 0; i < constantsArray.length(); i++)
		{
			JSONObject messageObject = constantsArray.getJSONObject(i);
			String name = messageObject.optString(AppConstants.JSONKeys.NAME);
			String value = messageObject.optString(AppConstants.JSONKeys.VALUE);
			constants.put(name, value);
		}
		_data.addConstants(constants);
	}

	public static NotificationDTO getNotification(JSONObject notificationObject) throws JSONException
	{
		int type = AppConstants.NotificationType.TEXT;
		String nType = notificationObject.optString(AppConstants.JSONKeys.TYPE);
		if (Utils.isNothing(nType))
			return null;

		String title = notificationObject.optString(AppConstants.JSONKeys.TITLE);
		if (Utils.isNothing(title))
			return null;

		String description = notificationObject.optString(AppConstants.JSONKeys.DESC);
		if (!nType.equalsIgnoreCase(AppConstants.JSONKeys.DEAL) && Utils.isNothing(description))
			return null;

		if (nType.equalsIgnoreCase(AppConstants.JSONKeys.URL))
		{
			type = AppConstants.NotificationType.URL;
			String url = notificationObject.optString(AppConstants.JSONKeys.URL);
			return new NotificationDTO(type, title, description, url, null);
		}
		else if (nType.equalsIgnoreCase(AppConstants.JSONKeys.DEAL))
		{
			type = AppConstants.NotificationType.DEAL;
			String dealid = notificationObject.optString(AppConstants.JSONKeys.ALERT_DEAL_ID);
			return new NotificationDTO(type, title, description, null, dealid);
		}

		return new NotificationDTO(type, title, description, null, null);
	}

	

	public static PhoneLine parsePhoneLine(JSONObject phoneLineObject) throws JSONException
	{
		String phoneNumber = phoneLineObject.optString(AppConstants.JSONKeys.PHONE_NUMBER, null);
		String availableFrom = phoneLineObject.optString(AppConstants.JSONKeys.AVAILABLE_FROM, null);
		String availableUpto = phoneLineObject.optString(AppConstants.JSONKeys.AVAILABLE_UPTO, null);
		return new PhoneLine(phoneNumber, availableFrom, availableUpto);
	}

	public static Contact parseContact(JSONObject contactObject) throws JSONException
	{
		System.out.println("contactObject   &&&"+contactObject);
		List<PhoneLine> phoneLines = null;
		JSONArray phoneLinesArray = contactObject.optJSONArray(AppConstants.JSONKeys.PHONE_LINES);
		if (phoneLinesArray != null && phoneLinesArray.length() > 0)
		{
			phoneLines = new ArrayList<PhoneLine>();
			for (int i = 0; i < phoneLinesArray.length(); i++)
			{
				JSONObject aPhoneLineObject = phoneLinesArray.optJSONObject(i);
				if (aPhoneLineObject != null)
				{
					PhoneLine phoneLine = parsePhoneLine(aPhoneLineObject);
					phoneLines.add(phoneLine);
				}
			}
		}

		String email = contactObject.optString(AppConstants.JSONKeys.EMAIL, null);
		String url = contactObject.optString(AppConstants.JSONKeys.URL, null);
		String details = contactObject.optString(AppConstants.JSONKeys.DETAILS, null);

		return new Contact(phoneLines, email, url, details);
	}

	public static Location parseLocation(JSONObject locationsObject) throws JSONException
	{
		String firstLine = locationsObject.optString(AppConstants.JSONKeys.FIRST_LINE, null);
		String secondLine = locationsObject.optString(AppConstants.JSONKeys.SECOND_LINE, null);
		String district = locationsObject.optString(AppConstants.JSONKeys.DISTRICT, null);
		String state = locationsObject.optString(AppConstants.JSONKeys.STATE, null);
		String country = locationsObject.optString(AppConstants.JSONKeys.COUNTRY, null);
		String pin = locationsObject.optString(AppConstants.JSONKeys.PIN, null);
		double longitude = locationsObject.optDouble(AppConstants.JSONKeys.LONGITUDE, -1);
		double latitude = locationsObject.optDouble(AppConstants.JSONKeys.LATITUDE, -1);

		return new Location(firstLine, secondLine, district, state, country, pin, longitude, latitude);
	}

	public static TnC parseTnC(JSONObject tncObject) throws JSONException
	{
		
		long startMillis = tncObject.optLong(AppConstants.JSONKeys.START, -1);
		Date start = null;
		if (startMillis != -1)
			start = new Date(startMillis);

		long endMillis = tncObject.optLong(AppConstants.JSONKeys.END, -1);
		Date end = null;
		if (endMillis != -1)
			end = new Date(endMillis);

		int max = tncObject.optInt(AppConstants.JSONKeys.MAX, -1);
		int min = tncObject.optInt(AppConstants.JSONKeys.MIN, -1);
		String offer = tncObject.optString(AppConstants.JSONKeys.OFFER, null);
		String howToRedeem = tncObject.optString(AppConstants.JSONKeys.HOW_TO_REDEEM, null);

		return new TnC(start, end, max, min, offer, howToRedeem);
	}

	public static Merchant parseMerchant(JSONObject dealObject) throws JSONException
	{
		JSONObject merchantObject = dealObject.optJSONObject(AppConstants.JSONKeys.MERCHANT);
		
		String name = merchantObject.optString(AppConstants.JSONKeys.NAME, null);
		JSONObject contactObject = dealObject.optJSONObject(AppConstants.JSONKeys.CONTACT);

		Contact contact = null;
		if (contactObject != null)
			contact = parseContact(contactObject);

		return new Merchant(name, contact);
	}

	public static Deal parseDeal(JSONObject dealObject) throws JSONException
	{
		String id = dealObject.optString(AppConstants.JSONKeys.ID, null);
		// String refId = dealObject.optString(AppConstants.JSONKeys.REF_ID,
		// null);
		// int promotionPriority =
		// dealObject.optInt(AppConstants.JSONKeys.PROMOTION_PRIORITY, -1);
		String name = dealObject.optString(AppConstants.JSONKeys.NAME, null);
		// String description =
		// dealObject.optString(AppConstants.JSONKeys.DESCRIPTION, null);
		String longDescription = dealObject.optString(AppConstants.JSONKeys.LONG_DESCRIPTION, null);

		String image = null;
		/*image = dealObject.optString(AppConstants.JSONKeys.IMAGES);
		if (Utils.isNothing(image))
		{
			JSONArray images = dealObject.optJSONArray(AppConstants.JSONKeys.IMAGEURL);
			if (images != null && images.length() > 0)
				image = images.optString(0);
		}*/
		JSONArray images = dealObject.optJSONArray(AppConstants.JSONKeys.IMAGES);
		if (images != null && images.length() > 0)
			image = images.optString(0);
		else
		{
			images = dealObject.optJSONArray(AppConstants.JSONKeys.IMAGEURL);
			if (images != null && images.length() > 0)
				image = images.optString(0);
		}
		String couponPrice = dealObject.optString(AppConstants.JSONKeys.COUPON_PRICE, null);
		String price = dealObject.optString(AppConstants.JSONKeys.PRICE, null);
		String discount = dealObject.optString(AppConstants.JSONKeys.DISCOUNT, null);
		discount = formatDecimalToString(discount);

		/*
		 * JSONObject contactObject =
		 * dealObject.optJSONObject(AppConstants.JSONKeys.CONTACT); Contact
		 * contact = null; if (contactObject != null) contact =
		 * parseContact(contactObject);
		 * 
		 * JSONArray locationsArray =
		 * dealObject.optJSONArray(AppConstants.JSONKeys.LOCATIONS);
		 * List<Location> locations = null; if (locationsArray != null &&
		 * locationsArray.length() > 0) { locations = new ArrayList<Location>();
		 * for (int i = 0; i < locationsArray.length(); i++) { JSONObject
		 * aLocationObject = locationsArray.optJSONObject(i); if
		 * (aLocationObject != null) { Location location =
		 * parseLocation(aLocationObject); locations.add(location); } } }
		 * 
		 * String language =
		 * dealObject.optString(AppConstants.JSONKeys.LANGUAGE, null); String
		 * category = dealObject.optString(AppConstants.JSONKeys.CATEGORY,
		 * null);
		 */
		
		String location= "";
		try
		{
			JSONObject jsonLocation = dealObject.optJSONObject(AppConstants.JSONKeys.LOCATIONS);
			//System.out.println("jsonLocation "+jsonLocation);
			if(jsonLocation.has("firstLine")){
				location = jsonLocation.getString("firstLine");	
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		TnC tnc = null;
		/*
		try
		{
			tnc = parseTnC(dealObject.optJSONObject(AppConstants.JSONKeys.TNC));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}*/

		Date endDate = null;
		String offer =null;
		String howToRedeem = null;
		try
		{
			String strEndDate = dealObject.optString(AppConstants.JSONKeys.ENDDATE, AppConstants.EMPTY_STRING);
			endDate = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH).parse(strEndDate);
			offer =  dealObject.optString(AppConstants.JSONKeys.OFFER, AppConstants.EMPTY_STRING);
			howToRedeem =  dealObject.optString(AppConstants.JSONKeys.HOW_TO_REDEEM, AppConstants.EMPTY_STRING);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (tnc == null)
		{
			if (endDate != null)
			{
				System.out.println("tnc is executng ***");
				tnc = new TnC(null, endDate, 0, 0, offer, howToRedeem);
			}
			else
			{
				tnc = new TnC(null, null, 0, 0, null, null);
			}
		}

		
		Merchant merchant = null;
		try
		{
			System.out.println("parse merchant object **");
			merchant = parseMerchant(dealObject);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (merchant == null)
		{
			String merchantName = dealObject.optString(AppConstants.JSONKeys.MERCHANT, null);
			merchant = new Merchant(merchantName, null);
		}

		
		// getting tags for a deal to know about the type of deal.
		
		List<String> tags = null;
		
		try {
			String tag = dealObject.optString(AppConstants.JSONKeys.TAGS);
			if( tag != null && !tag.equalsIgnoreCase("")){
				tags = new ArrayList<String>();
				tags.add(tag);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*List<String> tags = null;
		JSONArray tagsArray = dealObject.optJSONArray(AppConstants.JSONKeys.TAGS);
		if (tagsArray != null && tagsArray.length() > 0)
		{
			tags = new ArrayList<String>();
			for (int i = 0; i < tagsArray.length(); i++)
			{
				String tag = (String) tagsArray.opt(i);
				tags.add(tag);
			}
		}*/

		/*
		 * List<String> reviews = null; JSONArray reviewsArray =
		 * dealObject.optJSONArray(AppConstants.JSONKeys.REVIEWS); if
		 * (reviewsArray != null && reviewsArray.length() > 0) { reviews = new
		 * ArrayList<String>(); for (int i = 0; i < reviewsArray.length(); i++)
		 * { String operator = (String) reviewsArray.opt(i);
		 * reviews.add(operator); } }
		 */

		// String source = dealObject.optString(AppConstants.JSONKeys.SOURCE,
		// null);

		/*
		 * long millis = dealObject.optLong(AppConstants.JSONKeys.RETRIEVED_ON,
		 * -1); if (millis == -1)
		 * dealObject.optLong(AppConstants.JSONKeys.RETRIEVEDON, -1); Date
		 * retrievedOn = null; if (millis != -1) retrievedOn = new Date(millis);
		 */

		String dealVisitUrl = dealObject.optString(AppConstants.JSONKeys.VISIT_URL, null);

		return new Deal(id, null, 0, name, null, longDescription, image, couponPrice, price, discount, null, location, null, null, tnc, merchant,
				null, tags, null, null, null, dealVisitUrl);

		// return new Deal(id, refId, promotionPriority, name, description,
		// longDescription, image, couponPrice, price, discount, contact,
		// locations, language, category, tnc, merchant, operators, tags,
		// reviews, source, retrievedOn, dealVisitUrl);
	}

	public static List<Deal> parseBrowse(JSONArray dealsArray) throws JSONException
	{
		List<Deal> deals = null;

		if (dealsArray != null && dealsArray.length() > 0)
		{
			deals = new ArrayList<Deal>();
			for (int i = 0; i < dealsArray.length(); i++)
			{
				JSONObject dealObject = dealsArray.optJSONObject(i);
				if (dealObject != null)
				{
					Deal deal = parseDeal(dealObject);
					deals.add(deal);
				}
			}
		}

		return deals;
	}

	// public static SearchDTO parseSearch(JSONObject searchResponse) throws
	// JSONException
	// {
	// JSONObject responseHeaderObject =
	// searchResponse.optJSONObject(AppConstants.JSONKeys.RESPONSE_HEADER);
	// if (responseHeaderObject == null)
	// return new SearchDTO(0, 0, 0, null);
	// int status = responseHeaderObject.optInt(AppConstants.JSONKeys.STATUS,
	// -1);
	// JSONObject responseObject =
	// searchResponse.optJSONObject(AppConstants.JSONKeys.RESPONSE);
	// if (responseObject == null)
	// return new SearchDTO(0, 0, 0, null);
	// int start = responseObject.optInt(AppConstants.JSONKeys.START, -1);
	// int numFound = responseObject.optInt(AppConstants.JSONKeys.NUM_FOUND,
	// -1);
	// JSONArray docsArray =
	// responseObject.optJSONArray(AppConstants.JSONKeys.DOCS);
	// List<Deal> deals = null;
	// if (docsArray != null && docsArray.length() > 0)
	// deals = parseBrowse(docsArray);
	// return new SearchDTO(status, start, numFound, deals);
	// }

	public static Purchase parsePurchase(JSONObject purchaseObject) throws JSONException
	{
		/*Date endDate = null;
		try
		{
			String strEndDate = dealObject.optString(AppConstants.JSONKeys.ENDDATE, AppConstants.EMPTY_STRING);
			endDate = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH).parse(strEndDate);
		}*/
		//2014-10-14Z
		String  purchaseTimeStampMillis = purchaseObject.optString(AppConstants.JSONKeys.PURCHASE_TIMESTAMP, "");
		Date purchaseTime = null;
		if (!purchaseTimeStampMillis.equalsIgnoreCase("")){
			try {
				purchaseTime = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH).parse(purchaseTimeStampMillis);
				System.out.println("purchaseTime "+purchaseTime.getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
			

		String dealId = purchaseObject.optString(AppConstants.JSONKeys.DEAL_ID_PURCHASE, null);
		String dealName = purchaseObject.optString(AppConstants.JSONKeys.DEAL_NAME, null);

		JSONObject couponObject = purchaseObject.optJSONObject(AppConstants.JSONKeys.COUPON);
		Coupon coupon = null;
		if (couponObject != null)
			coupon = getCoupon(couponObject);

		return new Purchase(purchaseTime, dealId, dealName, coupon, null);
	}

	public static Coupon getCoupon(JSONObject couponObject) throws JSONException
	{
		String couponId = couponObject.optString(AppConstants.JSONKeys.COUPON_ID, null);

		/*long issueDateMillis = couponObject.optLong(AppConstants.JSONKeys.ISSUE_DATE, -1);
		Date issueDate = null;
		if (issueDateMillis != -1)
			issueDate = new Date(issueDateMillis);*/
		String issueDateMillis = couponObject.optString(AppConstants.JSONKeys.ISSUE_DATE, "");
		System.out.println("issueDateMillis ***"+issueDateMillis+ "  "  +issueDateMillis.replaceAll("Z", ""));
		Date issueDate = null;
		if (!issueDateMillis.equalsIgnoreCase("")){
			try {
				issueDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(issueDateMillis.replaceAll("Z", ""));
				System.out.println("issueDate "+issueDate.getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		Date expiryDate = null;
		long expiryDateMillis = couponObject.optLong(AppConstants.JSONKeys.EXPIRY_DATE, -1);
		if (expiryDateMillis != -1)
			expiryDate = new Date(expiryDateMillis);

		return new Coupon(couponId, issueDate, expiryDate);
	}
	public static List<Purchase> parsePurchaseHistory(JSONArray purchaseArray) throws JSONException
	{
		System.out.println("purchaseArray  ***"+purchaseArray);
		
		List<Purchase> history = null;

		if (purchaseArray != null && purchaseArray.length() > 0)
		{
			Purchase purchase = null;
			history = new ArrayList<Purchase>();
			for (int i = 0; i < purchaseArray.length(); i++)
			{
				JSONObject purchaseObject = (JSONObject) purchaseArray.opt(i);

				if (purchaseObject != null)
				{
					purchase = parsePurchase(purchaseObject);
				}
				history.add(purchase);
			}
		}
		return history;
	}

	public static String formatDecimalToString(String number)
	{
		String ss = GreatBuyzApplication.getApplication().getResources().getString(R.string.zeroDiscountText);

		try
		{
			NumberFormat numberFormat = DecimalFormat.getInstance();
			numberFormat.setMaximumFractionDigits(AppConstants.FRACTION_DIGITS);
			double discount = Double.valueOf(number);
			if (discount <= 0)
				return ss;
			ss = numberFormat.format(discount);
		}
		catch (Exception e)
		{
		}
		return ss + " " + GreatBuyzApplication.getApplication().getResources().getString(R.string.percent);
	}
}
