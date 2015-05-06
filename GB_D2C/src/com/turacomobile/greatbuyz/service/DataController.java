package com.turacomobile.greatbuyz.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.database.Cursor;
import android.util.Log;

import com.turacomobile.greatbuyz.GreatBuyzApplication;
import com.turacomobile.greatbuyz.data.CouponScreenDTO;
import com.turacomobile.greatbuyz.data.Deal;
import com.turacomobile.greatbuyz.data.DealScreenDTO;
import com.turacomobile.greatbuyz.data.DealsByCategoriesDTO;
import com.turacomobile.greatbuyz.data.DealsNearMeDTO;
import com.turacomobile.greatbuyz.data.DealsOfTheDayDTO;
import com.turacomobile.greatbuyz.data.DealsYouMayLikeDTO;
import com.turacomobile.greatbuyz.data.ExclusiveCouponsDTO;
import com.turacomobile.greatbuyz.data.ExclusiveDealsDTO;
import com.turacomobile.greatbuyz.data.ExploreDealsDTO;
import com.turacomobile.greatbuyz.data.MyDealsDTO;
import com.turacomobile.greatbuyz.data.Purchase;
import com.turacomobile.greatbuyz.service.ResponseParser.CategoryDTO;
import com.turacomobile.greatbuyz.utils.AppConstants;
import com.turacomobile.greatbuyz.utils.Utils;

public class DataController
{
	private final String TAG = "[GreatBuyz]DataController";
	private Deal _dealById;
	private Deal _purchaseDealById;
	private ExclusiveDealsDTO _exclusiveDealsDTO;
	private ExclusiveCouponsDTO _exclusiveCouponsDTO;
	private DealsYouMayLikeDTO _dealsYouMayLikeDTO;
	private DealsByCategoriesDTO _dealsByCategoriesDTO;
	private ExploreDealsDTO _exploreDealsDTO;
	//private MyDealsDTOOLD _myDealsDTO;
	private MyDealsDTO _myDealsDTO;
	private DealsOfTheDayDTO _dealsOfTheDayDTO;
	private DealsNearMeDTO _dealsNearMeDTO;
	private Deal _freeDeal;
	private Deal _surpriseMeDeal;
	private DB _db;

	public DataController()
	{
		_db = GreatBuyzApplication.getDB();
	}

	public void dealsByCategoriesReceived(List<DealScreenDTO> dtos)
	{
		if (dtos != null)
		{
			if (_dealsByCategoriesDTO == null)
				_dealsByCategoriesDTO = new DealsByCategoriesDTO(dtos);
			else
				_dealsByCategoriesDTO.add(dtos);

			// set start index for next iteration
			GreatBuyzApplication.getApplication().setSkipIndexForDealByCategory(
					GreatBuyzApplication.getApplication().getSkipIndexForDealByCategory() + dtos.size());
		}
	}

	public void dealsOfTheDayReceived(List<DealScreenDTO> dtos)
	{
		if (dtos != null)
		{
			GreatBuyzApplication.getApplication().setSkipIndexForDealsOfTheDay(
					GreatBuyzApplication.getApplication().getSkipIndexForDealsOfTheDay() + dtos.size());

			if (_dealsOfTheDayDTO == null)
				_dealsOfTheDayDTO = new DealsOfTheDayDTO(dtos);
			else
				_dealsOfTheDayDTO.add(dtos);
		}
	}

	public void dealsYouMayLikeReceived(List<DealScreenDTO> dtos)
	{
		if (dtos != null)
		{
			if (_dealsYouMayLikeDTO == null)
				_dealsYouMayLikeDTO = new DealsYouMayLikeDTO(dtos);
			else
				_dealsYouMayLikeDTO.add(dtos);

			// set start index for next iteration
			GreatBuyzApplication.getApplication().setSkipIndexForRecommendedDeals(
					GreatBuyzApplication.getApplication().getSkipIndexForRecommendedDeals() + dtos.size());
		}
	}

	public void myDealsReceived(List<Purchase> dtos)
	{
		if (dtos != null)
		{
			if (_myDealsDTO == null)
				_myDealsDTO = new MyDealsDTO(dtos);
			else
				_myDealsDTO.add(dtos);
		}
	}

	/*public void myDealsReceived(List<DealScreenDTO> dtos)
	{
		if (dtos != null)
		{
			GreatBuyzApplication.getApplication().setSkipIndexForMyDeals(
					GreatBuyzApplication.getApplication().getSkipIndexForMyDeals() + dtos.size());

			if (_myDealsDTO == null)
				_myDealsDTO = new MyDealsDTO(dtos);
			else
				_myDealsDTO.add(dtos);
		}
	}*/

	public MyDealsDTO getMyDealsDTO()
	{
		return _myDealsDTO;
	}

	public void deleteAllMyDeals()
	{
		if (_myDealsDTO != null)
			_myDealsDTO.deleteAll();
	}

	public DealsByCategoriesDTO getDealsByCategoriesDTO()
	{
		return _dealsByCategoriesDTO;
	}

	public void deleteAllDealsByCategory()
	{
		if (_dealsByCategoriesDTO != null)
			_dealsByCategoriesDTO.deleteAll();

		_dealsByCategoriesDTO = null;
	}

	public DealsOfTheDayDTO getDealsOfTheDayDTO()
	{
		return _dealsOfTheDayDTO;
	}

	public void setDealsOfTheDayDTO(DealsOfTheDayDTO dto)
	{
		_dealsOfTheDayDTO = dto;
	}

	public void deleteAllFlagshipDeals()
	{
		if (_dealsOfTheDayDTO != null)
			_dealsOfTheDayDTO.deleteAll();
	}

	public void exclusiveDealsReceived(List<DealScreenDTO> dtos)
	{
		if (dtos != null)
		{
			GreatBuyzApplication.getApplication().setSkipIndexForExclusiveDeals(
					GreatBuyzApplication.getApplication().getSkipIndexForExclusiveDeals() + dtos.size());

			if (_exclusiveDealsDTO == null)
				_exclusiveDealsDTO = new ExclusiveDealsDTO(dtos);
			else
				_exclusiveDealsDTO.add(dtos);
		}
	}
	
//	public void exclusiveCouponsReceived(List<CouponScreenDTO> dtos)
//	{
//		if (dtos != null)
//		{
//			GreatBuyzApplication.getApplication().setSkipIndexForExclusiveCoupons(
//					GreatBuyzApplication.getApplication().getSkipIndexForExclusiveCoupons() + dtos.size());
//
//			if (_exclusiveCouponsDTO == null)
//				_exclusiveCouponsDTO = new ExclusiveCouponsDTO(dtos);
//			else
//				_exclusiveCouponsDTO.add(dtos);
//		}
//	}

	public ExclusiveDealsDTO getExclusiveDealsDTO()
	{
		return _exclusiveDealsDTO;
	}

	public void setExclusiveDealsDTO(ExclusiveDealsDTO dto)
	{
		_exclusiveDealsDTO = dto;
	}

	public void deleteAllExclusiveDeals()
	{
		if (_exclusiveDealsDTO != null)
			_exclusiveDealsDTO.deleteAll();
	}
	
	public void exclusiveCouponsReceived(List<CouponScreenDTO> dtos)
	{
		if (dtos != null)
		{
			GreatBuyzApplication.getApplication().setSkipIndexForExclusiveCoupons(
					GreatBuyzApplication.getApplication().getSkipIndexForExclusiveCoupons() + dtos.size());

			if (_exclusiveCouponsDTO == null)
				_exclusiveCouponsDTO = new ExclusiveCouponsDTO(dtos);
			else
				_exclusiveCouponsDTO.add(dtos);
		}
	}

	public ExclusiveCouponsDTO getExclusiveCouponsDTO()
	{
		return _exclusiveCouponsDTO;
	}

	public void setExclusiveCouponsDTO(ExclusiveCouponsDTO dto)
	{
		_exclusiveCouponsDTO = dto;
	}

	public void deleteAllExclusiveCoupons()
	{
		if (_exclusiveCouponsDTO != null)
			_exclusiveCouponsDTO.deleteAll();
	}

	public DealsYouMayLikeDTO getDealsYouMayLikeDTO()
	{
		return _dealsYouMayLikeDTO;
	}

	public void deleteAllDealsYouMayLike()
	{
		if (_dealsYouMayLikeDTO != null)
			_dealsYouMayLikeDTO.deleteAll();
	}

	public DealsNearMeDTO getDealsNearMeDTO()
	{
		return _dealsNearMeDTO;
	}

	public void deleteAllDealsNearMe()
	{
		if (_dealsNearMeDTO != null)
			_dealsNearMeDTO.deleteAll();
	}

	public void dealsNearMeReceived(List<DealScreenDTO> dtos)
	{
		if (dtos != null)
		{
			GreatBuyzApplication.getApplication().setSkipIndexForDealsNearMe(
					GreatBuyzApplication.getApplication().getSkipIndexForDealsNearMe() + dtos.size());

			if (_dealsNearMeDTO == null)
				_dealsNearMeDTO = new DealsNearMeDTO(dtos);
			else
				_dealsNearMeDTO.add(dtos);
		}
	}

	/*public void onGetPurchaseHistoryReceived(List<Purchase> history)
	{
		if (_myDealsDTO == null)
			_myDealsDTO = new MyDealsDTOOLD(history);
		else
			_myDealsDTO.add(history);
	}

	public MyDealsDTOOLD getMyDealsDTO()
	{
		return _myDealsDTO;
	}*/

	/*public void deleteAllMyDeals()
	{
		if (_myDealsDTO != null)
			_myDealsDTO.deleteAll();
	}*/

	public void onGetDealByIdReceived(Deal deal)
	{
		_dealById = deal;
	}

	public Deal getDealById()
	{
		return _dealById;
	}

	public void deleteDealById()
	{
		_dealById = null;
	}

	public void onPurchaseGetDealByIdReceived(Deal deal)
	{
		System.out.println("deal onPurchaseGetDealByIdReceived **"+deal);
		_purchaseDealById = deal;
	}

	public Deal getPurchaseDealById()
	{
		return _purchaseDealById;
	}

	public void deletePurchaseDealById()
	{
		_purchaseDealById = null;
	}

	public void exploreDealsReceived(List<DealScreenDTO> dtos)
	{
		if (dtos != null)
		{
			GreatBuyzApplication.getApplication().setSkipIndexForExploreDeals(
					GreatBuyzApplication.getApplication().getSkipIndexForExploreDeals() + dtos.size());

			if (_exploreDealsDTO == null)
				_exploreDealsDTO = new ExploreDealsDTO(dtos);
			else
				_exploreDealsDTO.add(dtos);
		}
	}

	public ExploreDealsDTO getExploreDeals()
	{
		return _exploreDealsDTO;
	}

	public void deleteAllExploreDeals()
	{
		if (_exploreDealsDTO != null)
			_exploreDealsDTO.deleteAll();
	}

	public void onErrorReceived(int statusCode, String errorMessage)
	{
		// Bad request
		if (statusCode == 400)
		{
		}
		// Unauthorized
		else if (statusCode == 401)
		{
		}
		// Not found
		else if (statusCode == 404)
		{
		}
		// Http method not allowed
		else if (statusCode == 405)
		{
		}
		// Request timeout
		else if (statusCode == 408)
		{
		}
		// Unsupported Media Type (Your accept/content-type request header and
		// data in the request body do not match)
		else if (statusCode == 415)
		{
		}
		// Internal server error
		else if (statusCode == 500)
		{
		}
		// Not implemented
		else if (statusCode == 501)
		{
		}
		// Service unavailable
		else if (statusCode == 503)
		{
		}
		// Authentication parameters (source and/or key) missing
		else if (statusCode == 4001)
		{
		}
		// Source authentication failed. Invalid key
		else if (statusCode == 4002)
		{
		}
		// Invalid source
		else if (statusCode == 4003)
		{
		}
		// Recommendation error
		else if (statusCode == 5001)
		{
		}
		// Subscription manager error
		else if (statusCode == 5005)
		{
		}
	}

	public Deal getFreeDeal()
	{
		return _freeDeal;
	}

	public Deal getSurpriseDeal()
	{
		return _surpriseMeDeal;
	}

	public void onFreeDealReceived(Deal freeDeal)
	{
		_freeDeal = freeDeal;
	}

	public void onSurpriseDealReceived(Deal surpriseDeal)
	{
		_surpriseMeDeal = surpriseDeal;
	}

	// DB interface

	public String getMDN()
	{
		return _db.getMDN();
	}

	public String getIMEI()
	{
		return _db.getAndroidId();
	}

	public String getIMSI()
	{
		return _db.getIMSI();
	}

	public String getGCMId()
	{
		return _db.getGCMId();
	}

	public String getEmailId()
	{
		return _db.getEmailId();
	}

	public Cursor getCititesCursor()
	{
		return _db.getCitiesList();
	}

	public List<String> getCitiesList()
	{
		List<String> citiesList = new ArrayList<String>();
		Cursor c = _db.getCitiesList();
		while (c.moveToNext())
		{
			citiesList.add(c.getString(0));
		}
		c.close();
		return citiesList;
	}

	public Cursor getCategoriesCursor()
	{
		return _db.getCategoriesList();
	}

	public List<String> getCategoriesList()
	{
		List<String> categoriesList = new ArrayList<String>();
		Cursor c = _db.getCategoriesList();
		while (c.moveToNext())
		{
			categoriesList.add(c.getString(0));
		}
		c.close();
		return categoriesList;
	}

	public Cursor getKeywords()
	{
		return _db.getKeywordsList();
	}

	public List<String> getKeywordsList()
	{
		List<String> keywordsList = new ArrayList<String>();
		Cursor c = _db.getKeywordsList();
		while (c.moveToNext())
		{
			keywordsList.add(c.getString(0));
		}
		c.close();
		return keywordsList;
	}

	public String getMessage(String key)
	{
		return _db.getMessage(key);
	}

	public String getConstant(String key)
	{
		return _db.getConstant(key);
	}

	public String getUserCity()
	{
		String city = AppConstants.EMPTY_STRING;
		try
		{
			return _db.getUserCity();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return city;
	}
	
	public String getUserDefaultCity()
	{
		String city = AppConstants.EMPTY_STRING;
		try
		{
			return _db.getUserDefaultCity();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return city;
	}

	public String getVersion(String key)
	{
		return _db.getVersion(key);
	}

	public List<BasicNameValuePair> getVersions()
	{
		List<BasicNameValuePair> versions = new ArrayList<BasicNameValuePair>(7);
		versions.add(new BasicNameValuePair(AppConstants.JSONKeys.LOCATIONS, _db.getVersion(DB.COL_VERSION_LOCATIONS)));
		versions.add(new BasicNameValuePair(AppConstants.JSONKeys.CATEGORIES, _db.getVersion(DB.COL_VERSION_CATEGORIES)));
		versions.add(new BasicNameValuePair(AppConstants.JSONKeys.KEYWORDS, _db.getVersion(DB.COL_VERSION_KEYWORDS)));
		versions.add(new BasicNameValuePair(AppConstants.SharedPrefKeys.about, _db.getVersion(DB.COL_VERSION_ABOUT)));
		versions.add(new BasicNameValuePair(AppConstants.JSONKeys.TNC, _db.getVersion(DB.COL_VERSION_TNC)));
		versions.add(new BasicNameValuePair(AppConstants.SharedPrefKeys.faq, _db.getVersion(DB.COL_VERSION_FAQ)));
		versions.add(new BasicNameValuePair(AppConstants.SharedPrefKeys.help, _db.getVersion(DB.COL_VERSION_HELP)));
		versions.add(new BasicNameValuePair(AppConstants.JSONKeys.MESSAGES, _db.getVersion(DB.COL_VERSION_MESSAGES)));
		versions.add(new BasicNameValuePair(AppConstants.JSONKeys.CONSTANTS, _db.getVersion(DB.COL_VERSION_CONSTANTS)));

		String client = null;
		try
		{
			client = Utils.getClientVersion();
		}
		catch (Exception e)
		{
			// Log.e(TAG, e.getMessage());
		}

		versions.add(new BasicNameValuePair(AppConstants.JSONKeys.CLIENT, client));
		return versions;
	}

	public String getUpgradeMessage()
	{
		return _db.getUpgradeMessage();
	}

	public String getUpgradeURL()
	{
		return _db.getUpgradeURL();
	}

	public String getOTP()
	{
		return _db.getOTP();
	}

	public long getOTPTimestamp()
	{
		return _db.getOTPTimestamp();
	}

	public String getOTPMessage()
	{
		return _db.getOTPMessage();
	}

	public int getNotificationFrequency()
	{
		int frequency = _db.getNotificationFrequnecy();
		ArrayList<String> frequencies = Utils.getNotificationFrequencies();
		if (frequencies.size() < 1)
			return -1;
		try
		{
			String strMinLimit = frequencies.get(0);
			String strMaxLimit = frequencies.get(frequencies.size() - 1);

			int minLimit = Integer.valueOf(strMinLimit);
			int maxLimit = Integer.valueOf(strMaxLimit);

			if (frequency < minLimit || frequency > maxLimit)
			{
				String strDefaultFrequency = getConstant(AppConstants.Constants.defaultAppNotificationLimit);

				int intDefaultFrequency = 0;
				intDefaultFrequency = Integer.parseInt(strDefaultFrequency);
				
				if(intDefaultFrequency < minLimit || intDefaultFrequency > maxLimit)
					return -1;

				frequency = intDefaultFrequency;
				updateNotificationFrequency(frequency);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return frequency;
	}

	public int getNotificationFrequencyIndex()
	{
		int frequency = _db.getNotificationFrequnecy();
		ArrayList<String> frequencies = Utils.getNotificationFrequencies();
		if (frequencies.size() < 1)
			return -1;
		try
		{
			String strMinLimit = frequencies.get(0);
			String strMaxLimit = frequencies.get(frequencies.size() - 1);

			int minLimit = Integer.valueOf(strMinLimit);
			int maxLimit = Integer.valueOf(strMaxLimit);

			if (frequency < minLimit || frequency > maxLimit)
			{
				String strDefaultFrequency = getConstant(AppConstants.Constants.defaultAppNotificationLimit);

				int intDefaultFrequency = 0;
				intDefaultFrequency = Integer.parseInt(strDefaultFrequency);
				
				if(intDefaultFrequency < minLimit || intDefaultFrequency > maxLimit)
					return -1;

				frequency = intDefaultFrequency;
				updateNotificationFrequency(frequency);
			}
			
			int index = frequency - minLimit;
			return index;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	public String isDailyMsgEnabled(){
		String isDailyMsgEnabled= null;
		
		isDailyMsgEnabled =_db.isDailyMsgEnabled();
		
		return isDailyMsgEnabled;
	}
	
	public boolean isUpgradeAvailable()
	{
		return _db.isUpgradeAvailable();
	}

	public boolean isUpgradeForced()
	{
		return _db.isUpgradeForced();
	}

	public boolean isUserSubscribedToGCM()
	{
		return _db.isUserSubscribedToGCM();
	}

	public String getUserSubscriptionStatus()
	{
		return _db.getUserSubscriptionStatus();
	}

	public void updateAndroidId(String id)
	{
		_db.updateAndroidId(id);
	}

	public void updateMDN(String mdn)
	{
		String countryCode = _db.getConstant(AppConstants.Constants.countryCode).trim();
		if(!Utils.isNothing(countryCode) && !Utils.isNothing(mdn)){
			if(!mdn.startsWith(countryCode)){
				mdn = countryCode + mdn;
			}
		}
		_db.updateMDN(mdn);
	}

	public void updateIMSI(String imsi)
	{
		_db.updateIMSI(imsi);
	}

	public void updateGCMId(String gcmId)
	{
		_db.updateGCMId(gcmId);
	}

	public void updateUserCity(String city)
	{
		_db.updateCity(city);
	}

	public void updateDefaultCity(String defaultCity)
	{
		_db.updateDefaultCity(defaultCity);
	}

	public void updateEmail(String email)
	{
		_db.updateEmail(email);
	}

	public void updateVersion(String key, String version)
	{
		_db.updateVersion(key, version);
	}

	public void updateIsUserSubscribedToGCM(boolean isUserSubscribed)
	{
		_db.updateIsUserSubscribedToGCM(isUserSubscribed);
	}

	public void updateUserSubscriptionStatus(String userSubscriptionStatus)
	{
		_db.updateUserSubscriptionStatus(userSubscriptionStatus);
	}

	public void updateIsUpgradeAvailable(boolean isUpgradeAvailable)
	{
		_db.updateUpgradeAvailable(isUpgradeAvailable);
	}

	public void updateIsUpgradeForced(boolean isUpgradeForced)
	{
		_db.updateUpgradeForced(isUpgradeForced);
	}

	public void updateUpgradeMessage(String upgradeMessage)
	{
		_db.updateUpgradeMessage(upgradeMessage);
	}

	public void updateUpgradeURL(String upgradeURL)
	{
		_db.updateUpgradeURL(upgradeURL);
	}

	public void updateOTP(String otp)
	{
		//Toast.makeText(GreatBuyzApplication.getApplication().getApplicationContext(), "OTP ***", Toast.LENGTH_LONG).show();
		Log.i("Data Controller ***", "OTP ***"+otp);
		_db.updateOTP(otp);
	}

	public void updateOTPTimestamp(long timestamp)
	{
		_db.updateOTPTimestamp(timestamp);
	}

	public void updateOTPMessage(String otpMessage)
	{
		_db.updateOTPMessage(otpMessage);
	}

	public void updateNotificationFrequency(int notificationFrequency)
	{
		_db.updateNotificationFrequency(notificationFrequency);
	}
	
	public void updateIsDailyMsgEnabled(String isDailyMsgEnabled)
	{
		_db.updateIsDailyMagEnabled(isDailyMsgEnabled);
	}

	public void addCity(String cityName)
	{
		_db.addCity(cityName);
	}

	public void addCities(List<String> cities)
	{
		_db.addCities(cities);
	}

	public void addCategory(String categoryName, boolean categoryIsSelected, String imgUrl)
	{
		_db.addCategory(categoryName, categoryIsSelected, imgUrl);
	}

	public void addCategories(List<CategoryDTO> categories)
	{
		_db.addCategories(categories);
	}

	public void addKeyword(String keywordName, boolean keywordIsSelected)
	{
		_db.addKeyword(keywordName, keywordIsSelected);
	}

	public void addKeywords(List<String> keywords)
	{
		_db.addKeywords(keywords);
	}

	public void addMessage(String key, String message)
	{
		if (Utils.isNothing(message))
			return;
		_db.addMessage(key, message);
	}

	public void addMessages(HashMap<String, String> messages)
	{
		_db.addMessages(messages);
	}

	public void updateMessage(String key, String message)
	{
		if (Utils.isNothing(message))
			return;
		_db.updateMessage(key, message);
	}

	public void addConstant(String key, String constant)
	{
		if (Utils.isNothing(constant))
			return;
		_db.addConstant(key, constant);
	}

	public void addConstants(HashMap<String, String> constants)
	{
		_db.addConstants(constants);
	}

	public void updateConstant(String key, String constant)
	{
		if (Utils.isNothing(constant))
			return;
		_db.updateConstant(key, constant);
	}

	public void clearCategoriesSelectedStatus()
	{
		_db.clearCategoriesSelectedStatus();
	}

	public void updateAllCategories()
	{
		_db.updateAllCategories();
	}

	public void setCategoriesSelectedStatus(List<String> cats)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		int i = 1;
		for (String cat : cats)
		{
			sb.append("'");
			sb.append(cat);
			sb.append("'");
			if (i++ < cats.size())
				sb.append(",");
		}
		sb.append(")");
		_db.setCategoriesSelectedStatus(sb.toString());
	}

	public void deleteAllVersions()
	{
		_db.deleteAllVersions();
	}

	public void deleteAllUserInfo()
	{
		_db.deleteAllUserInfo();
	}

	public void deleteAllCities()
	{
		_db.deleteAllCities();
	}

	public void deleteAllCategories()
	{
		_db.deleteAllCategories();
	}

	public void deleteAllKeywords()
	{
		_db.deleteAllKeywords();
	}

	public void deleteAllMessages()
	{
		_db.deleteAllMessages();
	}

	public void deleteAllConstants()
	{
		_db.deleteAllConstants();
	}
	
	public void deleteDatabase()
	{
		_db.deleteSelf();
	}
}