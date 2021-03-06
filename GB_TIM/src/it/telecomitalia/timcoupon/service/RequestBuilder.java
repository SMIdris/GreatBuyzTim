package it.telecomitalia.timcoupon.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.onmobile.utils.AppConstants;
import com.onmobile.utils.Utils;

public class RequestBuilder
{
	private final String TAG = "[GreatBuyz]RequestBuilder";

	public static String getUpdateGoogleRegistrationIdRequest(String registrationId)
	{
		if (registrationId == null)
			return AppConstants.EMPTY_STRING;
		JSONObject json = new JSONObject();
		try
		{
			json.put(AppConstants.JSONKeys.REGISTRATION_ID, registrationId);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return json.toString();
	}

	public static final String getKeywordsRequest(String mdn) throws UnsupportedEncodingException
	{
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		BasicNameValuePair pair = new BasicNameValuePair(AppConstants.JSONKeys.MDN, mdn);
		list.add(pair);
		return URLEncodedUtils.format(list, AppConstants.ENCODING_UTF8);
	}

	public static final String searchRequest(String location, String locality, String category, String keyWords, int skip, int limit)
	{
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		try
		{
			if (keyWords != null && keyWords.length() > 0)
			{
				list.add(new BasicNameValuePair(AppConstants.JSONKeys.QUERY, keyWords));
			}
			else
			{
				list.add(new BasicNameValuePair(AppConstants.JSONKeys.QUERY, AppConstants.EMPTY_STRING));
			}
			if (category != null && category.length() > 0)
			{
				list.add(new BasicNameValuePair(AppConstants.JSONKeys.CATEGORIES, category));
			}
			if (location != null && location.length() > 0)
			{
				list.add(new BasicNameValuePair(AppConstants.JSONKeys.LOCATIONS, location));
			}
			if (locality != null && locality.length() > 0)
			{
				list.add(new BasicNameValuePair(AppConstants.JSONKeys.LOCALITY, locality));
			}
			list.add(new BasicNameValuePair(AppConstants.JSONKeys.SKIP, String.valueOf(skip)));
			list.add(new BasicNameValuePair(AppConstants.JSONKeys.LIMIT, String.valueOf(limit)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return URLEncodedUtils.format(list, AppConstants.ENCODING_UTF8);
	}

	public static final String dealsByCategoryRequest(String location, String category, int limit, int skip)
	{
		List<NameValuePair> list = new ArrayList<NameValuePair>();

		try
		{
			list.add(new BasicNameValuePair(AppConstants.JSONKeys.LOCATIONS, location));
			list.add(new BasicNameValuePair(AppConstants.JSONKeys.CATEGORIES_BROWSE, category));
			list.add(new BasicNameValuePair(AppConstants.JSONKeys.LIMIT, String.valueOf(limit)));
			list.add(new BasicNameValuePair(AppConstants.JSONKeys.SKIP, String.valueOf(skip)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return URLEncodedUtils.format(list, AppConstants.ENCODING_UTF8);
	}

	public static final String browseRequest(String channel, String[] locations, String[] categories, int limit, int skip)
	{
		List<NameValuePair> list = new ArrayList<NameValuePair>();

		String locationsList = null;
		String categoriesList = null;
		if (locations != null)
			locationsList = TextUtils.join(AppConstants.COMMA, locations);
		if (categories != null)
			categoriesList = TextUtils.join(AppConstants.COMMA, categories);

		try
		{
			if (locationsList != null && locationsList.length() > 0)
				list.add(new BasicNameValuePair(AppConstants.JSONKeys.LOCATIONS, locationsList));
			if (categoriesList != null && categoriesList.length() > 0)
				list.add(new BasicNameValuePair(AppConstants.JSONKeys.CATEGORIES, categoriesList));

			if (channel != null)
				list.add(new BasicNameValuePair(AppConstants.JSONKeys.CHANNEL, channel));

			list.add(new BasicNameValuePair(AppConstants.JSONKeys.LIMIT, String.valueOf(limit)));
			list.add(new BasicNameValuePair(AppConstants.JSONKeys.SKIP, String.valueOf(skip)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return URLEncodedUtils.format(list, AppConstants.ENCODING_UTF8);
	}

	public static final String flagshipDealsRequest(String channel, int limit, int skip)
	{
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		JSONObject json = new JSONObject();
		try
		{
			json.put(AppConstants.JSONKeys.CHANNEL, channel);
			json.put(AppConstants.JSONKeys.LIMIT, limit);
			json.put(AppConstants.JSONKeys.SKIP, skip);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		BasicNameValuePair pair = new BasicNameValuePair(AppConstants.URIParts.GET_QUERY_STRING_PARAM, json.toString());
		list.add(pair);

		return URLEncodedUtils.format(list, AppConstants.ENCODING_UTF8);
	}

	public static final String dealsNearMeRequest(String channel, double latitude, double longitude, int radius, int limit, int skip)
	{
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		try
		{
			list.add(new BasicNameValuePair(AppConstants.JSONKeys.CHANNEL, channel));
			list.add(new BasicNameValuePair(AppConstants.JSONKeys.LATITUDE, String.valueOf(latitude)));
			list.add(new BasicNameValuePair(AppConstants.JSONKeys.LONGITUDE, String.valueOf(longitude)));
			list.add(new BasicNameValuePair(AppConstants.JSONKeys.RADIUS, String.valueOf(radius)));
			list.add(new BasicNameValuePair(AppConstants.JSONKeys.LIMIT, String.valueOf(limit)));
			list.add(new BasicNameValuePair(AppConstants.JSONKeys.SKIP, String.valueOf(skip)));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return URLEncodedUtils.format(list, AppConstants.ENCODING_UTF8);
	}

	public static final String subscribeRequest(String mdn, String channel, String chargingMode, String status)
	{
		JSONObject json = new JSONObject();
		try
		{
			if (!Utils.isNothing(mdn))
				json.put(AppConstants.JSONKeys.MDN, mdn);

			if (!Utils.isNothing(channel))
				json.put(AppConstants.JSONKeys.CHANNEL, channel);

			if (!Utils.isNothing(chargingMode))
				json.put(AppConstants.JSONKeys.CHARGING_MODE, chargingMode);

			if (!Utils.isNothing(status))
				json.put(AppConstants.JSONKeys.STATUS, status);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return json.toString();
	}

	public static final String getInfoRequest(double latitude, double longitude, String imei, String imsi, String gcmid,
			List<BasicNameValuePair> versions)
	{
		JSONObject json = new JSONObject();

		try
		{
			json.put(AppConstants.JSONKeys.LAT, latitude);
			json.put(AppConstants.JSONKeys.LONG, longitude);
			json.put(AppConstants.JSONKeys.IMEI, imei);
			json.put(AppConstants.JSONKeys.IMSI, imsi);
			json.put(AppConstants.JSONKeys.GCM_ID, gcmid);

			for (NameValuePair nameValuePair : versions)
			{
				json.put(nameValuePair.getName(), nameValuePair.getValue());
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return json.toString();
	}

	public static final String updateUserInfoRequest(String mdn, String[] locations, String[] categories, String emailId,
			String googleRegistrationId, String[] keywords, int notificationFrequency, String imei, String imsi, String clientVersion, String isDailyMsgEnabled)
	{
		JSONObject json = new JSONObject();
		JSONArray loc = null, cat = null, keys = null;
		if (locations != null)
		{
			loc = Utils.convertToJSONArray(locations);
		}
		if (categories != null)
		{
			cat = Utils.convertToJSONArray(categories);
		}
		if (keywords != null)
		{
			keys = Utils.convertToJSONArray(keywords);
		}

		try
		{
			if (mdn != null && mdn.length() > 0)
				json.put(AppConstants.JSONKeys.MDN, mdn);
			if (loc != null && loc.length() > 0)
				json.put(AppConstants.JSONKeys.LOCATIONS, loc);
			if (cat != null && cat.length() > 0)
				json.put(AppConstants.JSONKeys.CATEGORIES, cat);
			if (emailId != null)
				json.put(AppConstants.JSONKeys.EMAILID, emailId);
			if (googleRegistrationId != null)
				json.put(AppConstants.JSONKeys.GOOGLE_REG_ID, googleRegistrationId);
			if (keys != null && keys.length() > 0)
				json.put(AppConstants.JSONKeys.KEYWORDS, keys);
			if (!Utils.isNothing(imei))
				json.put(AppConstants.JSONKeys.IMEI, imei);
			if (!Utils.isNothing(imsi))
				json.put(AppConstants.JSONKeys.IMSI, imsi);
			if (!Utils.isNothing(clientVersion))
				json.put(AppConstants.JSONKeys.CLIENT, clientVersion);
			if (!Utils.isNothing(isDailyMsgEnabled))
				json.put(AppConstants.JSONKeys.isDailyMsgEnabled, isDailyMsgEnabled);

			if (notificationFrequency >= -1)
				json.put(AppConstants.JSONKeys.NOTIFICATION_FREQUENCY, notificationFrequency);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		return json.toString();
	}
}