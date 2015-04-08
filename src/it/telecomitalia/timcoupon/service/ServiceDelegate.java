package it.telecomitalia.timcoupon.service;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import it.telecomitalia.timcoupon.R;
import it.telecomitalia.timcoupon.data.Deal;
import it.telecomitalia.timcoupon.data.DealScreenDTO;
import it.telecomitalia.timcoupon.data.Purchase;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.onmobile.hcoe.io.HttpClientService;
import com.onmobile.hcoe.io.IHttpResponseListener;
import com.onmobile.utils.AppConstants;
import com.onmobile.utils.Utils;

public class ServiceDelegate
{
	private final String				TAG	  = "[GreatBuyz]ServiceDelegate";
	private static final DataController _data	= GreatBuyzApplication.getDataController();
	private HttpClientService		   gateway  = new HttpClientService(5, "");
	private Map<String, String>		 headers  = new HashMap<String, String>(4);
	private String					  BASE_URL = GreatBuyzApplication.getApplication().getBaseURL();
	private String					  httpErrorString;
	
	public ServiceDelegate()
	{
		initializeDefaultHeaders();
		httpErrorString = Utils.getMessageString(AppConstants.Messages.unableToConnect, R.string.unableToConnect);
	}
	
	private void initializeDefaultHeaders()
	{
		headers.put("Accept", "application/json");
		headers.put("Content-Type", "application/json");
		headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.3.6; en-us; Nexus S Build/GRK39F) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
		headers.put("channel", "wap");
		headers.put("key", "ee4ef426-8cab-4853-ace0-2c1660284a7c");
	}
	
	public Map<String, String> getHeaders()
	{
		try
		{
			System.out.println("_data.getGCMId()" + _data.getGCMId());
			headers.put(AppConstants.JSONKeys.GCM, _data.getGCMId());
			System.out.println("_data.getMDN() **" + _data.getMDN());
			headers.put(AppConstants.JSONKeys.MSISDN, _data.getMDN());
		}
		catch (Exception e)
		{}
		return headers;
	}
	
	public void getDealById(String dealId, final IOperationListener _operationListener) throws UnsupportedEncodingException, URISyntaxException
	{
		try
		{
			gateway.submitRequest(BASE_URL + AppConstants.URIParts.GET_DEAL_BY_ID + dealId, getHeaders(), new IHttpResponseListener()
			{
				@Override
				public void onReceiveResponse(int pStatusCode, HashMap<String, String> pResponseHeaders, String pResponse)
				{
					if (pResponse != null && pStatusCode == 200)
					{
						try
						{
							JSONObject response = new JSONObject(pResponse);
							JSONArray array = response.getJSONArray(AppConstants.JSONKeys.DEAL_DTO);
							if (array.length() > 0 && !array.isNull(0))
							{
								JSONObject job = array.getJSONObject(0);
								Deal deal = ResponseParser.parseDeal(job);
								_data.onGetDealByIdReceived(deal);
								_operationListener.onOperationCompleted(true, null);
							}
							else
								_operationListener.onOperationCompleted(false, httpErrorString);
						}
						catch (Exception e)
						{
							e.printStackTrace();
							_operationListener.onOperationCompleted(false, httpErrorString);
						}
					}
					else
					{
						onErrorReceived(pResponse, _operationListener);
					}
				}
				
				@Override
				public void onHttpRequestError(Exception e)
				{
					e.printStackTrace();
					_operationListener.onOperationCompleted(false, httpErrorString);
				}
			}, HttpGet.METHOD_NAME);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_operationListener.onOperationCompleted(false, httpErrorString);
		}
	}
	
	public void getSurpiseDeal(final IOperationListener _operationListener) throws UnsupportedEncodingException, URISyntaxException
	{
		try
		{
			gateway.submitRequest(BASE_URL + AppConstants.URIParts.GET_SURPRISE_DEAL, getHeaders(), new IHttpResponseListener()
			{
				@Override
				public void onReceiveResponse(int pStatusCode, HashMap<String, String> pResponseHeaders, String pResponse)
				{
					if (pResponse != null && pStatusCode == 200)
					{
						try
						{
							JSONObject job = new JSONObject(pResponse);
							Deal deal = ResponseParser.parseDeal(job);
							_data.onSurpriseDealReceived(deal);
							_operationListener.onOperationCompleted(true, null);
						}
						catch (Exception e)
						{
							e.printStackTrace();
							String emptyDealString = Utils.getMessageString(AppConstants.Messages.emptyDeal, R.string.emptyDeal);
							_operationListener.onOperationCompleted(false, emptyDealString);
						}
					}
					else
					{
						onErrorReceived(pResponse, _operationListener);
					}
				}
				
				@Override
				public void onHttpRequestError(Exception e)
				{
					e.printStackTrace();
					_operationListener.onOperationCompleted(false, httpErrorString);
				}
			}, HttpGet.METHOD_NAME);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_operationListener.onOperationCompleted(false, httpErrorString);
		}
	}
	
	public void getInfo(final double latitude, final double longitude, final IOperationListener _operationListener) throws UnsupportedEncodingException
	{
		String imei = Utils.getAndroidId(GreatBuyzApplication.getApplication());
		if (!Utils.isNothing(imei)) GreatBuyzApplication.getDataController().updateAndroidId(imei);
		String imsi = null;
	//	imsi = _data.getIMSI();
		 imsi = "123sdfsdf3423452345";
		 imei = "4354354354354351235";
		String data = RequestBuilder.getInfoRequest(latitude, longitude, imei, imsi, _data.getGCMId(), _data.getVersions());
		try
		{
			gateway.submitRequest(BASE_URL + AppConstants.URIParts.GET_INFO, getHeaders(), data, new IHttpResponseListener()
			{
				@Override
				public void onReceiveResponse(int pStatusCode, HashMap<String, String> pResponseHeaders, String pResponse)
				{
					// System.out.println("httpErrorString"+httpErrorString);
					if (pResponse != null && pStatusCode == 200)
					{
						try
						{
							ResponseParser.parseGetInfo(new JSONObject(pResponse));
							_operationListener.onOperationCompleted(true, null);
						}
						catch (Exception e)
						{
							e.printStackTrace();
							_operationListener.onOperationCompleted(false, httpErrorString);
						}
					}
					else
					{
						onErrorReceived(pResponse, _operationListener);
					}
				}
				
				@Override
				public void onHttpRequestError(Exception e)
				{
					e.printStackTrace();
					_operationListener.onOperationCompleted(false, httpErrorString);
				}
			}, HttpPost.METHOD_NAME);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_operationListener.onOperationCompleted(false, httpErrorString);
		}
	}
	
	public void registerUser(String mdn, final IOperationListener _operationListener) throws UnsupportedEncodingException
	{
		try
		{
			Map<String, String> headers = getHeaders();
			headers.put(AppConstants.JSONKeys.MSISDN, mdn);
			// Log.e(TAG, "registerUser : " + BASE_URL +
			// AppConstants.URIParts.REGISTER);
			gateway.submitRequest(BASE_URL + AppConstants.URIParts.REGISTER, headers, new IHttpResponseListener()
			{
				@Override
				public void onReceiveResponse(int pStatusCode, HashMap<String, String> pResponseHeaders, String pResponse)
				{
					if (pResponse != null && pStatusCode == 200)
					{
						try
						{
							ResponseParser.parseRegisterUserInfo(pResponse);
							_data.updateOTP(pResponse);
							_data.updateOTPTimestamp(System.currentTimeMillis());
							_operationListener.onOperationCompleted(true, null);
						}
						catch (Exception e)
						{
							e.printStackTrace();
							_operationListener.onOperationCompleted(false, httpErrorString);
						}
					}
					else
					{
						onErrorReceived(pResponse, _operationListener);
					}
				}
				
				@Override
				public void onHttpRequestError(Exception e)
				{
					e.printStackTrace();
					_operationListener.onOperationCompleted(false, httpErrorString);
				}
			}, HttpGet.METHOD_NAME);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_operationListener.onOperationCompleted(false, httpErrorString);
		}
	}
	
	public void updateUserInfo(String[] locations, String[] categories, String emailId, String googleRegistrationId, String[] keywords, int notificationFrequency, String imei, String imsi, String clientVersion, String isDailyMsgEnabled, final IOperationListener _operationListener) throws UnsupportedEncodingException
	{
		String mdn = _data.getMDN();
		if (Utils.isNothing(mdn)) return;
		String data = RequestBuilder.updateUserInfoRequest(mdn, locations, categories, emailId, googleRegistrationId, keywords, notificationFrequency, imei, imsi, clientVersion, isDailyMsgEnabled);
		// if (BuildConfig.DEBUG)
		// Log.d("Update User info request data : ", data);
		try
		{
			// Log.e(TAG, "updateUserInfo : " + BASE_URL +
			// AppConstants.URIParts.USER_INFO);
			gateway.submitRequest(BASE_URL + AppConstants.URIParts.USER_INFO, getHeaders(), data, new IHttpResponseListener()
			{
				@Override
				public void onReceiveResponse(int pStatusCode, HashMap<String, String> pResponseHeaders, String pResponse)
				{
					if (pResponse != null && pStatusCode == 200)
					{
						try
						{
							boolean updateSuccess = false;
							if (pResponse.equalsIgnoreCase(AppConstants.JSONKeys.SUCCESS)) updateSuccess = true;
							if (_operationListener != null) _operationListener.onOperationCompleted(updateSuccess, null);
						}
						catch (Exception e)
						{
							e.printStackTrace();
							if (_operationListener != null) _operationListener.onOperationCompleted(false, httpErrorString);
						}
					}
					else
					{
						if (_operationListener != null) onErrorReceived(pResponse, _operationListener);
					}
				}
				
				@Override
				public void onHttpRequestError(Exception e)
				{
					e.printStackTrace();
					if (_operationListener != null) _operationListener.onOperationCompleted(false, httpErrorString);
				}
			}, HttpPost.METHOD_NAME);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (_operationListener != null) _operationListener.onOperationCompleted(false, httpErrorString);
		}
	}
	
	public void subscribeToChannel(final String mdn, final String channel, final String chargingMode, final String status, final String serviceKey, final IOperationListener _operationListener) throws UnsupportedEncodingException
	{
		String data = RequestBuilder.subscribeRequest(mdn, channel, chargingMode, status);
		try
		{
			gateway.submitRequest(BASE_URL + AppConstants.URIParts.SUBSCRIBE, getHeaders(), data, new IHttpResponseListener()
			{
				@Override
				public void onReceiveResponse(int pStatusCode, HashMap<String, String> pResponseHeaders, String pResponse)
				{
					if (pResponse != null && pStatusCode == 200)
					{
						try
						{
							boolean subscribed = Boolean.parseBoolean(pResponse);
							if (subscribed)
								_operationListener.onOperationCompleted(subscribed, AppConstants.UserActivationStatus.ACTIVE);
							else
								_operationListener.onOperationCompleted(subscribed, AppConstants.UserActivationStatus.PENDING);
						}
						catch (Exception e)
						{
							e.printStackTrace();
							_operationListener.onOperationCompleted(false, httpErrorString);
						}
					}
					else
					{
						onErrorReceived(pResponse, _operationListener);
					}
				}
				
				@Override
				public void onHttpRequestError(Exception e)
				{
					e.printStackTrace();
					_operationListener.onOperationCompleted(false, httpErrorString);
				}
			}, HttpPost.METHOD_NAME);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_operationListener.onOperationCompleted(false, httpErrorString);
		}
	}
	
	public void getMDNFromNetworkGET(final String url, final Map<String, String> headers, final IOperationListener _operationListener)
	{
		try
		{
			gateway.submitRequest(url, headers, new IHttpResponseListener()
			{
				@Override
				public void onReceiveResponse(int statusCode, HashMap<String, String> p_ResponseHeaders, String p_Response)
				{
					String mdn = null;
					try
					{
						mdn = ResponseParser.findMDN(p_Response);
						if (!Utils.isNothing(mdn))
						{
							if (!mdn.startsWith("+") && mdn.length() == 12)
							{
								mdn = "+" + mdn;
							}
							_data.updateMDN(mdn);
						}
						_operationListener.onOperationCompleted(true, null);
					}
					catch (Exception e)
					{
						_operationListener.onOperationCompleted(false, httpErrorString);
					}
				}
				
				@Override
				public void onHttpRequestError(Exception e)
				{
					e.printStackTrace();
					_operationListener.onOperationCompleted(false, httpErrorString);
				}
			}, HttpGet.METHOD_NAME);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_operationListener.onOperationCompleted(false, httpErrorString);
		}
	}
	
	public void getMDNFromNetworkPOST(final String url, final Map<String, String> headers, final String postData, final IOperationListener _operationListener) throws UnsupportedEncodingException
	{
		try
		{
			gateway.submitRequest(url, headers, postData, new IHttpResponseListener()
			{
				@Override
				public void onReceiveResponse(int pStatusCode, HashMap<String, String> pResponseHeaders, String pResponse)
				{
					String mdn = null;
					try
					{
						mdn = ResponseParser.findMDN(pResponse);
						if (Utils.isNothing(mdn))
						{
							_operationListener.onOperationCompleted(false, pResponse);
						}
						else
						{
							if (!mdn.startsWith("+") && mdn.length() == 12)
							{
								mdn = "+" + mdn;
							}
							_data.updateMDN(mdn);
							_operationListener.onOperationCompleted(true, pResponse);
						}
					}
					catch (Exception e)
					{
						_operationListener.onOperationCompleted(false, httpErrorString);
					}
				}
				
				@Override
				public void onHttpRequestError(Exception e)
				{
					e.printStackTrace();
					_operationListener.onOperationCompleted(false, httpErrorString);
				}
			}, HttpPost.METHOD_NAME);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_operationListener.onOperationCompleted(false, httpErrorString);
		}
	}
	
	public void unsubscribeFromChannel(final String mdn, final String channel, final String chargingMode, final IOperationListener _operationListener) throws UnsupportedEncodingException
	{
		String data = RequestBuilder.subscribeRequest(mdn, channel, chargingMode, null);
		try
		{
			gateway.submitRequest(BASE_URL + AppConstants.URIParts.UNSUBSCRIBE, getHeaders(), data, new IHttpResponseListener()
			{
				@Override
				public void onReceiveResponse(int pStatusCode, HashMap<String, String> pResponseHeaders, String pResponse)
				{
					if (pResponse != null && pStatusCode == 200)
					{
						try
						{
							boolean unsubscribed = Boolean.parseBoolean(pResponse);
							_operationListener.onOperationCompleted(unsubscribed, null);
						}
						catch (Exception e)
						{
							e.printStackTrace();
							_operationListener.onOperationCompleted(false, httpErrorString);
						}
					}
					else
					{
						onErrorReceived(pResponse, _operationListener);
					}
				}
				
				@Override
				public void onHttpRequestError(Exception e)
				{
					e.printStackTrace();
					_operationListener.onOperationCompleted(false, httpErrorString);
				}
			}, HttpPost.METHOD_NAME);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_operationListener.onOperationCompleted(false, httpErrorString);
		}
	}
	
	public void getFreeDeals(final IOperationListener _operationListener) throws UnsupportedEncodingException
	{
		try
		{
			gateway.submitRequest(BASE_URL + AppConstants.URIParts.GET_WELCOME_DEALS, headers, new IHttpResponseListener()
			{
				@Override
				public void onReceiveResponse(int pStatusCode, HashMap<String, String> pResponseHeaders, String pResponse)
				{
					if (pResponse != null && pStatusCode == 200)
					{
						try
						{
							Deal freeDeal = null;
							try
							{
								JSONObject dealsObj = new JSONObject(pResponse);
								JSONArray dealsArray = dealsObj.optJSONArray(AppConstants.JSONKeys.DEAL_DTO);
								List<Deal> deals = ResponseParser.parseBrowse(dealsArray);
								if (deals != null && deals.size() > 0) freeDeal = deals.get(0);
								_data.onFreeDealReceived(freeDeal);
								if (freeDeal != null)
									_operationListener.onOperationCompleted(true, null);
								else
									_operationListener.onOperationCompleted(false, httpErrorString);
							}
							catch (Exception e)
							{
								_data.onFreeDealReceived(null);
								_operationListener.onOperationCompleted(false, httpErrorString);
							}
						}
						catch (Exception e)
						{
							e.printStackTrace();
							_operationListener.onOperationCompleted(false, httpErrorString);
						}
					}
					else
					{
						onErrorReceived(pResponse, _operationListener);
					}
				}
				
				@Override
				public void onHttpRequestError(Exception e)
				{
					e.printStackTrace();
					_operationListener.onOperationCompleted(false, httpErrorString);
				}
			}, HttpGet.METHOD_NAME);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_operationListener.onOperationCompleted(false, null);
		}
	}
	
	/**
	 * @author kiranr
	 * @param _operationListener
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 *             It is used to get cities when version mismatch happen in get info call
	 */
	public void getCities(final IOperationListener _operationListener) throws UnsupportedEncodingException, URISyntaxException
	{
		try
		{
			gateway.submitRequest(BASE_URL + AppConstants.URIParts.GET_CITIES, getHeaders(), new IHttpResponseListener()
			{
				@Override
				public void onReceiveResponse(int pStatusCode, HashMap<String, String> pResponseHeaders, String pResponse)
				{
					if (pResponse != null && pStatusCode == 200)
					{
						try
						{
							JSONObject object = new JSONObject(pResponse);
							JSONArray array = (JSONArray) object.get("location");
							if (array != null && array.length() > 0)
							{
								ResponseParser.parseCities(array);
								String newVersion = GreatBuyzApplication.getApplication().getSharedPreferences().getString(DB.COL_VERSION_LOCATIONS, null);
								_data.updateVersion(DB.COL_VERSION_LOCATIONS, newVersion);
								SharedPreferences.Editor editor = GreatBuyzApplication.getApplication().getSharedPreferences().edit();
								editor.remove(DB.COL_VERSION_LOCATIONS);
								editor.commit();
							}
							/*
							 * JSONObject entry = object.getJSONObject(AppConstants .JSONKeys.KEYWORD); JSONArray keywordsArray = entry.getJSONArray(AppConstants .JSONKeys.ENTRY); ResponseParser.parseKeywords (keywordsArray); String newVersion = GreatBuyzApplication .getApplication().getSharedPreferences() .getString(DB.COL_VERSION_KEYWORDS, null); if (!Utils.isNothing(newVersion)) { _data.updateVersion(DB.COL_VERSION_KEYWORDS , newVersion); SharedPreferences.Editor editor = GreatBuyzApplication.getApplication ().getSharedPreferences().edit(); editor.remove(DB.COL_VERSION_KEYWORDS); editor.commit(); }
							 */
							_operationListener.onOperationCompleted(true, null);
						}
						catch (Exception e)
						{
							e.printStackTrace();
							_operationListener.onOperationCompleted(false, httpErrorString);
						}
					}
					else
					{
						onErrorReceived(pResponse, _operationListener);
					}
				}
				
				@Override
				public void onHttpRequestError(Exception e)
				{
					e.printStackTrace();
					_operationListener.onOperationCompleted(false, httpErrorString);
				}
			}, HttpGet.METHOD_NAME);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_operationListener.onOperationCompleted(false, httpErrorString);
		}
	}
	
	public void getKeywords(final IOperationListener _operationListener) throws UnsupportedEncodingException, URISyntaxException
	{
		try
		{
			gateway.submitRequest(BASE_URL + AppConstants.URIParts.GET_KEYWORDS, getHeaders(), new IHttpResponseListener()
			{
				@Override
				public void onReceiveResponse(int pStatusCode, HashMap<String, String> pResponseHeaders, String pResponse)
				{
					if (pResponse != null && pStatusCode == 200)
					{
						try
						{
							JSONObject object = new JSONObject(pResponse);
							JSONObject entry = object.getJSONObject(AppConstants.JSONKeys.KEYWORD);
							JSONArray keywordsArray = entry.getJSONArray(AppConstants.JSONKeys.ENTRY);
							ResponseParser.parseKeywords(keywordsArray);
							String newVersion = GreatBuyzApplication.getApplication().getSharedPreferences().getString(DB.COL_VERSION_KEYWORDS, null);
							if (!Utils.isNothing(newVersion))
							{
								_data.updateVersion(DB.COL_VERSION_KEYWORDS, newVersion);
								SharedPreferences.Editor editor = GreatBuyzApplication.getApplication().getSharedPreferences().edit();
								editor.remove(DB.COL_VERSION_KEYWORDS);
								editor.commit();
							}
							_operationListener.onOperationCompleted(true, null);
						}
						catch (Exception e)
						{
							e.printStackTrace();
							_operationListener.onOperationCompleted(false, httpErrorString);
						}
					}
					else
					{
						onErrorReceived(pResponse, _operationListener);
					}
				}
				
				@Override
				public void onHttpRequestError(Exception e)
				{
					e.printStackTrace();
					_operationListener.onOperationCompleted(false, httpErrorString);
				}
			}, HttpGet.METHOD_NAME);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_operationListener.onOperationCompleted(false, httpErrorString);
		}
	}
	
	public void getPurchaseDealById(String dealId, final IOperationListener _operationListener) throws UnsupportedEncodingException, URISyntaxException
	{
		try
		{
			gateway.submitRequest(BASE_URL + AppConstants.URIParts.GET_PURCHASE_DEAL_BY_ID + dealId, getHeaders(), new IHttpResponseListener()
			{
				@Override
				public void onReceiveResponse(int pStatusCode, HashMap<String, String> pResponseHeaders, String pResponse)
				{
					try
					{
						if (pResponse != null && pStatusCode == 200)
						{
							try
							{
								JSONArray array = new JSONArray(pResponse);
								if (array.length() > 0 && !array.isNull(0))
								{
									JSONObject job = array.getJSONObject(0);
									Deal deal = ResponseParser.parseDeal(job);
									GreatBuyzApplication.getDataController().onPurchaseGetDealByIdReceived(deal);
									_operationListener.onOperationCompleted(true, null);
								}
								else
									_operationListener.onOperationCompleted(false, httpErrorString);
							}
							catch (Exception e)
							{
								e.printStackTrace();
								_operationListener.onOperationCompleted(false, httpErrorString);
							}
						}
						else
						{
							onErrorReceived(pResponse, _operationListener);
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						_operationListener.onOperationCompleted(false, httpErrorString);
					}
				}
				
				@Override
				public void onHttpRequestError(Exception e)
				{
					e.printStackTrace();
					_operationListener.onOperationCompleted(false, httpErrorString);
				}
			}, HttpGet.METHOD_NAME);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_operationListener.onOperationCompleted(false, httpErrorString);
		}
	}
	
	private void onErrorReceived(String response, final IOperationListener _operationListener)
	{
		_operationListener.onOperationCompleted(false, response);
	}
	
	public List<DealScreenDTO> getDealScreenDTOs(List<Deal> deals)
	{
		List<DealScreenDTO> dtos = new ArrayList<DealScreenDTO>(deals.size());
		for (Deal deal : deals)
		{
			DealScreenDTO dto = getDealScreenDTO(deal);
			dtos.add(dto);
		}
		return dtos;
	}
	
	public DealScreenDTO getDealScreenDTO(Deal deal)
	{
		/*
		 * StringBuilder details = new StringBuilder(); details.append(AppConstants.HTMLHEADERCSS); String longDescription = deal.getLongDescription(); if (longDescription != null && longDescription.length() > 0 && !longDescription.equals(AppConstants.NULL_IN_STRING)) { details.append(longDescription); } if (deal.getTnC() != null) { String offer = deal.getTnC().getOffer(); if (offer != null && offer.length() > 0 && !offer.equals(AppConstants.NULL_IN_STRING)) { details.append(AppConstants.OFFERHEADER1); details.append(GreatBuyzApplication .getApplication().getResources().getString (R.string.offerHeaderText)); details.append(AppConstants.OFFERHEADER2); details.append(offer); } String howToRedeem = deal.getTnC().getHowToRedeem(); if (howToRedeem != null && howToRedeem.length() > 0 && !howToRedeem.equals(AppConstants.NULL_IN_STRING)) { details.append(AppConstants.OFFERHEADER1); details.append(GreatBuyzApplication .getApplication().getResources().getString(R.string.howToRedeem)); details.append(AppConstants.OFFERHEADER2); details.append(howToRedeem); } Date expireDate = deal.getTnC().getEnd(); if (expireDate != null) { details.append(AppConstants.OFFERHEADER1); details.append(GreatBuyzApplication .getApplication().getResources().getString(R.string.date)); details.append(AppConstants.OFFERHEADER2); details.append(expireDate); } } if (deal.getContact() != null) { String contactDetails = deal.getContact().getDetails(); if (contactDetails != null && contactDetails.length() > 0 && !contactDetails.equals(AppConstants.NULL_IN_STRING)) { details.append(AppConstants.OFFERHEADER1); details.append(GreatBuyzApplication .getApplication().getResources().getString (R.string.contactDetailHeaderText)); details.append(AppConstants.OFFERHEADER2); details.append(contactDetails); } } // details.append(AppConstants.HTMLFOOTER);
		 */
		boolean isExclusiveDeal = false;
		List<String> tagList = deal.getTags();
		if (tagList != null && tagList.size() > 0)
		{
			isExclusiveDeal = tagList.contains(AppConstants.JSONKeys.EXCLUSIVE_DEALS);
		}
		return new DealScreenDTO(deal.getId(), null, deal.getName(), deal.getImage(), deal.getLongDescription(), deal.getCouponPrice(), deal.getPrice(), deal.getDiscount(), null, deal.getDealVisitUrl(), deal.getTnC().getEnd(), isExclusiveDeal, deal.getLocations(), deal.getLocationDistrict());
		// return new DealScreenDTO(deal.getId(), deal.getMerchant().getName(),
		// deal.getName(), deal.getImage(), details.toString(),
		// deal.getCouponPrice(), deal.getPrice(), deal.getDiscount(),
		// deal.getCategory(), deal.getDealVisitUrl());
	}
	
	public ClipResponse getDealsNearMe(String channel, double latitude, double longitude, int radius, int limit, int skip)
	{
		ClipResponse clipResponce = null;
		String data = RequestBuilder.dealsNearMeRequest(channel, latitude, longitude, radius, limit, skip);
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		HttpConnectionParams.setConnectionTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS);
		// Thread Safe Client Connection Manager to be added to the
		// DefaultHttpClient to
		// enable MultiThreaded functioning of the Client.
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParameters, schemeRegistry);
		// //----------------------
		// Use the client defaults and create a client.
		// this.mHttpClient = createHttpClient();//new DefaultHttpClient();
		// Client made using the Connection Manager. Safe across threads
		int statusCode = 10000;
		try
		{
			HttpClient mHttpClient = new DefaultHttpClient(cm, httpParameters);
			// Log.e(TAG, "GetDealsNearMe -- url : " + BASE_URL +
			// AppConstants.URIParts.DEALS_BY_LOCATION + data);
			// Log.e(TAG, "GetDealsNearMe -- limit : " + limit + " skip " +
			// skip);
			HttpMessage mHttpMessage = createHttpGetRequest(BASE_URL + AppConstants.URIParts.DEALS_BY_LOCATION + data, getHeaders());
			HttpResponse responce = mHttpClient.execute((HttpUriRequest) mHttpMessage);
			statusCode = responce.getStatusLine().getStatusCode();
			// Log.e(TAG, "GetDealsNearMe -- response : " + responce);
			{
				HeaderIterator t_HeaderIterator = responce.headerIterator();
				HashMap<String, String> t_HeaderMap = new HashMap<String, String>();
				while (t_HeaderIterator.hasNext())
				{
					Header t_ResponneHeader = (Header) t_HeaderIterator.next();
					t_HeaderMap.put(t_ResponneHeader.getName(), t_ResponneHeader.getValue());
				}
				String s = EntityUtils.toString(responce.getEntity(), "UTF-8");
				// Log.e(TAG, "GetDealsNearMe -- response : s : " + s);
				// System.out.print("########################## : " + s +
				// "##########################" + statusCode);
				if (s != null && statusCode == 200)
				{
					try
					{
						List<Deal> deals = null;
						JSONObject dealsObj = new JSONObject(s);
						JSONArray dealsArray = dealsObj.optJSONArray(AppConstants.JSONKeys.DEAL_DTO);
						if (dealsArray.length() > 0) deals = (List<Deal>) ResponseParser.parseBrowse(dealsArray);
						final List<DealScreenDTO> dtos = getDealScreenDTOs(deals);
						clipResponce = new ClipResponse(statusCode, s, dtos);
					}
					catch (OutOfMemoryError oome)
					{
						System.gc();
						clipResponce = new ClipResponse(statusCode, httpErrorString, null);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						clipResponce = new ClipResponse(statusCode, httpErrorString, null);
					}
				}
				else
				{
					clipResponce = new ClipResponse(statusCode, httpErrorString, null);
				}
			}
		}
		catch (OutOfMemoryError oome)
		{
			System.gc();
			clipResponce = new ClipResponse(statusCode, httpErrorString, null);
		}
		catch (Exception e)
		{
			clipResponce = new ClipResponse(statusCode, httpErrorString, null);
		}
		return clipResponce;
	}
	
	public ClipResponse getDealsYouMayLike(int limit, int skip)
	{
		ClipResponse clipResponce = null;
		String data = RequestBuilder.browseRequest(null, null, null, limit, skip);
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		HttpConnectionParams.setConnectionTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS);
		// Thread Safe Client Connection Manager to be added to the
		// DefaultHttpClient to
		// enable MultiThreaded functioning of the Client.
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParameters, schemeRegistry);
		// //----------------------
		// Use the client defaults and create a client.
		// this.mHttpClient = createHttpClient();//new DefaultHttpClient();
		// Client made using the Connection Manager. Safe across threads
		int statusCode = 10000;
		try
		{
			HttpClient mHttpClient = new DefaultHttpClient(cm, httpParameters);
			HttpMessage mHttpMessage = createHttpGetRequest(BASE_URL + AppConstants.URIParts.RECOMMEND + data, getHeaders());
			HttpResponse responce = mHttpClient.execute((HttpUriRequest) mHttpMessage);
			statusCode = responce.getStatusLine().getStatusCode();
			// Log.d(TAG, "HttpChainingRunnable : response : " + responce);
			{
				HeaderIterator t_HeaderIterator = responce.headerIterator();
				HashMap<String, String> t_HeaderMap = new HashMap<String, String>();
				while (t_HeaderIterator.hasNext())
				{
					Header t_ResponneHeader = (Header) t_HeaderIterator.next();
					t_HeaderMap.put(t_ResponneHeader.getName(), t_ResponneHeader.getValue());
				}
				String s = EntityUtils.toString(responce.getEntity(), "UTF-8");
				// System.out.print("########################## : " + s + "##########################" + statusCode);
				if (s != null && statusCode == 200)
				{
					try
					{
						List<Deal> deals = null;
						JSONObject dealsObj = new JSONObject(s);
						JSONArray dealsArray = dealsObj.optJSONArray(AppConstants.JSONKeys.DEAL_DTO);
						if (dealsArray.length() > 0) deals = (List<Deal>) ResponseParser.parseBrowse(dealsArray);
						final List<DealScreenDTO> dtos = getDealScreenDTOs(deals);
						clipResponce = new ClipResponse(statusCode, s, dtos);
					}
					catch (OutOfMemoryError oome)
					{
						System.gc();
						clipResponce = new ClipResponse(statusCode, httpErrorString, null);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						clipResponce = new ClipResponse(statusCode, httpErrorString, null);
					}
				}
				else
				{
					clipResponce = new ClipResponse(statusCode, httpErrorString, null);
				}
			}
		}
		catch (OutOfMemoryError oome)
		{
			System.gc();
			clipResponce = new ClipResponse(statusCode, httpErrorString, null);
		}
		catch (Exception e)
		{
			clipResponce = new ClipResponse(statusCode, httpErrorString, null);
		}
		return clipResponce;
	}
	
	public ClipResponse getDealsOfTheDay(String channel, int limit, int skip)
	{
		ClipResponse clipResponce = null;
		String data = RequestBuilder.browseRequest(channel, null, null, limit, skip);
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		HttpConnectionParams.setConnectionTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS);
		// Thread Safe Client Connection Manager to be added to the
		// DefaultHttpClient to
		// enable MultiThreaded functioning of the Client.
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParameters, schemeRegistry);
		// //----------------------
		// Use the client defaults and create a client.
		// this.mHttpClient = createHttpClient();//new DefaultHttpClient();
		// Client made using the Connection Manager. Safe across threads
		int statusCode = 10000;
		try
		{
			HttpClient mHttpClient = new DefaultHttpClient(cm, httpParameters);
			// Log.e(TAG, "GetDealsOfTheDay -- url : " + BASE_URL +
			// AppConstants.URIParts.FLAGSHIP_DEALS + data);
			// Log.e(TAG, "GetDealsOfTheDay -- limit : " + limit + " skip " +
			// skip);
			HttpMessage mHttpMessage = createHttpGetRequest(BASE_URL + AppConstants.URIParts.FLAGSHIP_DEALS + data, getHeaders());
			HttpResponse responce = mHttpClient.execute((HttpUriRequest) mHttpMessage);
			statusCode = responce.getStatusLine().getStatusCode();
			// Log.e(TAG, "GetDealsOfTheDay -- response : " + responce);
			{
				HeaderIterator t_HeaderIterator = responce.headerIterator();
				HashMap<String, String> t_HeaderMap = new HashMap<String, String>();
				while (t_HeaderIterator.hasNext())
				{
					Header t_ResponneHeader = (Header) t_HeaderIterator.next();
					t_HeaderMap.put(t_ResponneHeader.getName(), t_ResponneHeader.getValue());
				}
				String s = EntityUtils.toString(responce.getEntity(), "UTF-8");
				// Log.e(TAG, "GetDealsOfTheDay -- response : s : " + s);
				/*
				 * System.out.print("########################## : " + s + "##########################" + statusCode);
				 */
				if (s != null && statusCode == 200)
				{
					try
					{
						List<Deal> deals = null;
						JSONObject dealsObj = new JSONObject(s);
						JSONArray dealsArray = dealsObj.optJSONArray(AppConstants.JSONKeys.DEAL_DTO);
						if (dealsArray.length() > 0) deals = (List<Deal>) ResponseParser.parseBrowse(dealsArray);
						final List<DealScreenDTO> dtos = getDealScreenDTOs(deals);
						clipResponce = new ClipResponse(statusCode, s, dtos);
					}
					catch (OutOfMemoryError oome)
					{
						System.gc();
						clipResponce = new ClipResponse(statusCode, httpErrorString, null);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						clipResponce = new ClipResponse(statusCode, httpErrorString, null);
					}
				}
				else
				{
					clipResponce = new ClipResponse(statusCode, httpErrorString, null);
				}
			}
		}
		catch (OutOfMemoryError oome)
		{
			System.gc();
			clipResponce = new ClipResponse(statusCode, httpErrorString, null);
		}
		catch (Exception e)
		{
			clipResponce = new ClipResponse(statusCode, httpErrorString, null);
		}
		return clipResponce;
	}
	
	public ClipResponse getDealsByCategories(String channel, String category, int limit, int skip)
	{
		ClipResponse clipResponce = null;
		String location = _data.getUserCity();
		System.out.println("location ******" + location);
		String data = RequestBuilder.dealsByCategoryRequest(location, category, limit, skip);
		System.out.println("data");
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		HttpConnectionParams.setConnectionTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS);
		// Thread Safe Client Connection Manager to be added to the
		// DefaultHttpClient to
		// enable MultiThreaded functioning of the Client.
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParameters, schemeRegistry);
		// //----------------------
		// Use the client defaults and create a client.
		// this.mHttpClient = createHttpClient();//new DefaultHttpClient();
		// Client made using the Connection Manager. Safe across threads
		int statusCode = 10000;
		try
		{
			HttpClient mHttpClient = new DefaultHttpClient(cm, httpParameters);
			HttpMessage mHttpMessage = createHttpGetRequest(BASE_URL + AppConstants.URIParts.BROWSE + data, getHeaders());
			System.out.println("BASE_URL + AppConstants.URIParts.BROWSE + data ***" + BASE_URL + AppConstants.URIParts.BROWSE + data);
			HttpResponse responce = mHttpClient.execute((HttpUriRequest) mHttpMessage);
			statusCode = responce.getStatusLine().getStatusCode();
			Log.e(TAG, "getDealsByCategories -- response : " + responce);
			{
				HeaderIterator t_HeaderIterator = responce.headerIterator();
				HashMap<String, String> t_HeaderMap = new HashMap<String, String>();
				while (t_HeaderIterator.hasNext())
				{
					Header t_ResponneHeader = (Header) t_HeaderIterator.next();
					t_HeaderMap.put(t_ResponneHeader.getName(), t_ResponneHeader.getValue());
				}
				String s = EntityUtils.toString(responce.getEntity(), "UTF-8");
				Log.e(TAG, "getDealsByCategories -- response : s : " + s);
				// System.out.print("########################## : " + s +
				// "##########################" + statusCode);
				if (s != null && statusCode == 200)
				{
					try
					{
						List<Deal> deals = null;
						JSONObject dealsObj = new JSONObject(s);
						JSONArray dealsArray = dealsObj.optJSONArray(AppConstants.JSONKeys.DEAL_DTO);
						if (dealsArray.length() > 0) deals = (List<Deal>) ResponseParser.parseBrowse(dealsArray);
						final List<DealScreenDTO> dtos = getDealScreenDTOs(deals);
						clipResponce = new ClipResponse(statusCode, s, dtos);
					}
					catch (OutOfMemoryError oome)
					{
						System.gc();
						clipResponce = new ClipResponse(statusCode, httpErrorString, null);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						clipResponce = new ClipResponse(statusCode, httpErrorString, null);
					}
				}
				else
				{
					clipResponce = new ClipResponse(statusCode, httpErrorString, null);
				}
			}
		}
		catch (OutOfMemoryError oome)
		{
			System.gc();
			clipResponce = new ClipResponse(statusCode, httpErrorString, null);
		}
		catch (Exception e)
		{
			clipResponce = new ClipResponse(statusCode, httpErrorString, null);
		}
		return clipResponce;
	}
	
	public ClipResponse getExploreDeals(String location, String locality, String categories, String keyWords, int limit, int skip)
	{
		ClipResponse clipResponce = null;
		String data = RequestBuilder.searchRequest(location, locality, categories, keyWords, skip, limit);
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		HttpConnectionParams.setConnectionTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS);
		// Thread Safe Client Connection Manager to be added to the
		// DefaultHttpClient to
		// enable MultiThreaded functioning of the Client.
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParameters, schemeRegistry);
		// //----------------------
		// Use the client defaults and create a client.
		// this.mHttpClient = createHttpClient();//new DefaultHttpClient();
		// Client made using the Connection Manager. Safe across threads
		int statusCode = 10000;
		try
		{
			HttpClient mHttpClient = new DefaultHttpClient(cm, httpParameters);
			HttpMessage mHttpMessage = createHttpGetRequest(BASE_URL + AppConstants.URIParts.SEARCH + data, getHeaders());
			// Log.d(TAG, BASE_URL + AppConstants.URIParts.SEARCH +
			// data);
			HttpResponse responce = mHttpClient.execute((HttpUriRequest) mHttpMessage);
			statusCode = responce.getStatusLine().getStatusCode();
			// Log.e(TAG, "getExploreDeals -- response : " + responce);
			{
				HeaderIterator t_HeaderIterator = responce.headerIterator();
				HashMap<String, String> t_HeaderMap = new HashMap<String, String>();
				while (t_HeaderIterator.hasNext())
				{
					Header t_ResponneHeader = (Header) t_HeaderIterator.next();
					t_HeaderMap.put(t_ResponneHeader.getName(), t_ResponneHeader.getValue());
				}
				String s = EntityUtils.toString(responce.getEntity(), "UTF-8");
				// //Log.e(TAG, "getExploreDeals -- response : s : " + s);
				/*
				 * System.out.print("########################## : " + s + "##########################" + statusCode);
				 */
				if (s != null && statusCode == 200)
				{
					try
					{
						boolean isSuggested = false;
						List<Deal> deals = null;
						JSONObject dealsObj = new JSONObject(s);
						isSuggested = dealsObj.optBoolean(AppConstants.JSONKeys.SUGGESTED);
						JSONArray dealsArray = dealsObj.optJSONArray(AppConstants.JSONKeys.DEAL_DTO);
						if (isSuggested == false && dealsArray == null)
						{
							final List<DealScreenDTO> dtos = new ArrayList<DealScreenDTO>();
							clipResponce = new ClipResponse(statusCode, s, dtos);
							return clipResponce;
						}
						if (dealsArray.length() > 0) deals = (List<Deal>) ResponseParser.parseBrowse(dealsArray);
						final List<DealScreenDTO> dtos = getDealScreenDTOs(deals);
						clipResponce = new ClipResponse(statusCode, s, dtos);
					}
					catch (OutOfMemoryError oome)
					{
						System.gc();
						clipResponce = new ClipResponse(statusCode, httpErrorString, null);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						clipResponce = new ClipResponse(statusCode, httpErrorString, null);
					}
				}
				else
				{
					clipResponce = new ClipResponse(statusCode, httpErrorString, null);
				}
			}
		}
		catch (OutOfMemoryError oome)
		{
			System.gc();
			clipResponce = new ClipResponse(statusCode, httpErrorString, null);
		}
		catch (Exception e)
		{
			clipResponce = new ClipResponse(statusCode, httpErrorString, null);
		}
		return clipResponce;
	}
	
	/*
	 * public MyDealsClipResponse getMyDeals(String mdn) { MyDealsClipResponse clipResponse = null; HttpParams httpParameters = new BasicHttpParams(); // Set the timeout in milliseconds until a connection is established. HttpConnectionParams.setConnectionTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS); // Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data. HttpConnectionParams.setSoTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS); // Thread Safe Client Connection Manager to be added to the // DefaultHttpClient to // enable MultiThreaded functioning of the Client. SchemeRegistry schemeRegistry = new SchemeRegistry(); schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80)); ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParameters, schemeRegistry); // //---------------------- // Use the client defaults and create a client. // this.mHttpClient = createHttpClient();//new DefaultHttpClient(); // Client made using the Connection Manager. Safe across threads int statusCode = 10000; try { HttpClient mHttpClient = new DefaultHttpClient(cm, httpParameters); HttpMessage mHttpMessage = createHttpGetRequest(BASE_URL + AppConstants.URIParts.GET_PURCHASE_HISTORY, getHeaders()); HttpResponse responce = mHttpClient.execute((HttpUriRequest) mHttpMessage); statusCode = responce.getStatusLine().getStatusCode(); // Log.d(TAG, "HttpChainingRunnable response : " + responce); { HeaderIterator t_HeaderIterator = responce.headerIterator(); HashMap<String, String> t_HeaderMap = new HashMap<String, String>(); while (t_HeaderIterator.hasNext()) { Header t_ResponneHeader = (Header) t_HeaderIterator.next(); t_HeaderMap.put(t_ResponneHeader.getName(), t_ResponneHeader.getValue()); } String s = EntityUtils.toString(responce.getEntity(), "UTF-8"); // System.out.print("########################## : " + s + // "##########################" + statusCode); if (s != null && statusCode == 200) { try { List<Purchase> history = null; JSONArray historyArray = new JSONArray(s); if (historyArray.length() > 0) history = (List<Purchase>) ResponseParser.parsePurchaseHistory(historyArray); clipResponse = new MyDealsClipResponse(statusCode, s, history); } catch (OutOfMemoryError oome) { System.gc(); clipResponse = new MyDealsClipResponse(statusCode, httpErrorString, null); } catch (Exception e) { e.printStackTrace(); clipResponse = new MyDealsClipResponse(statusCode, httpErrorString, null); } } else { clipResponse = new MyDealsClipResponse(statusCode, httpErrorString, null); } } } catch (OutOfMemoryError oome) { System.gc(); clipResponse = new MyDealsClipResponse(statusCode, httpErrorString, null); } catch (Exception e) { clipResponse = new MyDealsClipResponse(statusCode, httpErrorString, null); } return clipResponse; }
	 */
	public ClipResponse getMyDeals(int limit, int skip)
	{
		ClipResponse clipResponce = null;
		String data = RequestBuilder.browseRequest(null, null, null, limit, skip);
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		HttpConnectionParams.setConnectionTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS);
		// Thread Safe Client Connection Manager to be added to the
		// DefaultHttpClient to
		// enable MultiThreaded functioning of the Client.
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParameters, schemeRegistry);
		// //----------------------
		// Use the client defaults and create a client.
		// this.mHttpClient = createHttpClient();//new DefaultHttpClient();
		// Client made using the Connection Manager. Safe across threads
		int statusCode = 10000;
		try
		{
			HttpClient mHttpClient = new DefaultHttpClient(cm, httpParameters);
			// Log.e(TAG, "GetMyDealsTIM -- url : " + BASE_URL +
			// AppConstants.URIParts.GET_MY_DEALS_TIM + data);
			// Log.e(TAG, "GetMyDealsTIM -- limit : " + limit + " skip " +
			// skip);
			HttpMessage mHttpMessage = createHttpGetRequest(BASE_URL + AppConstants.URIParts.GET_MY_DEALS + data, getHeaders());
			HttpResponse responce = mHttpClient.execute((HttpUriRequest) mHttpMessage);
			statusCode = responce.getStatusLine().getStatusCode();
			// Log.e(TAG, "GetMyDealsTIM -- response : " + responce);
			{
				HeaderIterator t_HeaderIterator = responce.headerIterator();
				HashMap<String, String> t_HeaderMap = new HashMap<String, String>();
				while (t_HeaderIterator.hasNext())
				{
					Header t_ResponneHeader = (Header) t_HeaderIterator.next();
					t_HeaderMap.put(t_ResponneHeader.getName(), t_ResponneHeader.getValue());
				}
				String s = EntityUtils.toString(responce.getEntity(), "UTF-8");
				// //Log.e(TAG, "GetMyDealsTIM -- response : s : " + s);
				// System.out.print("########################## : " + s +
				// "##########################" + statusCode);
				if (s != null && statusCode == 200)
				{
					try
					{
						List<Deal> deals = null;
						JSONObject dealsObj = new JSONObject(s);
						JSONArray dealsArray = dealsObj.optJSONArray(AppConstants.JSONKeys.DEAL_DTO);
						if (dealsArray.length() > 0) deals = (List<Deal>) ResponseParser.parseBrowse(dealsArray);
						final List<DealScreenDTO> dtos = getDealScreenDTOs(deals);
						clipResponce = new ClipResponse(statusCode, s, dtos);
					}
					catch (OutOfMemoryError oome)
					{
						System.gc();
						clipResponce = new ClipResponse(statusCode, httpErrorString, null);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						clipResponce = new ClipResponse(statusCode, httpErrorString, null);
					}
				}
				else
				{
					clipResponce = new ClipResponse(statusCode, httpErrorString, null);
				}
			}
		}
		catch (OutOfMemoryError oome)
		{
			System.gc();
			clipResponce = new ClipResponse(statusCode, httpErrorString, null);
		}
		catch (Exception e)
		{
			clipResponce = new ClipResponse(statusCode, httpErrorString, null);
		}
		return clipResponce;
	}
	
	public ClipResponse getExclusiveDeals(String channel, int limit, int skip)
	{
		ClipResponse clipResponce = null;
		String data = RequestBuilder.browseRequest(channel, null, null, limit, skip);
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		HttpConnectionParams.setConnectionTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS);
		// Thread Safe Client Connection Manager to be added to the
		// DefaultHttpClient to
		// enable MultiThreaded functioning of the Client.
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParameters, schemeRegistry);
		// //----------------------
		// Use the client defaults and create a client.
		// this.mHttpClient = createHttpClient();//new DefaultHttpClient();
		// Client made using the Connection Manager. Safe across threads
		int statusCode = 10000;
		try
		{
			HttpClient mHttpClient = new DefaultHttpClient(cm, httpParameters);
			// Log.e(TAG, "GetExclusiveDeals -- url : " + BASE_URL
			// + AppConstants.URIParts.GET_EXCLUSIVE_DEALS + data);
			// Log.e(TAG, "GetExclusiveDeals -- limit : " + limit + " skip " +
			// skip);
			HttpMessage mHttpMessage = createHttpGetRequest(BASE_URL + AppConstants.URIParts.GET_EXCLUSIVE_DEALS + data, getHeaders());
			HttpResponse responce = mHttpClient.execute((HttpUriRequest) mHttpMessage);
			statusCode = responce.getStatusLine().getStatusCode();
			// Log.e(TAG, "GetExclusiveDeals -- response : " + responce);
			{
				HeaderIterator t_HeaderIterator = responce.headerIterator();
				HashMap<String, String> t_HeaderMap = new HashMap<String, String>();
				while (t_HeaderIterator.hasNext())
				{
					Header t_ResponneHeader = (Header) t_HeaderIterator.next();
					t_HeaderMap.put(t_ResponneHeader.getName(), t_ResponneHeader.getValue());
				}
				String s = EntityUtils.toString(responce.getEntity(), "UTF-8");
				// //Log.e(TAG, "GetExclusiveDeals -- response : s : " + s);
				// System.out.print("########################## : " + s +
				// "##########################" + statusCode);
				if (s != null && statusCode == 200)
				{
					try
					{
						List<Deal> deals = null;
						JSONObject dealsObj = new JSONObject(s);
						JSONArray dealsArray = dealsObj.optJSONArray(AppConstants.JSONKeys.DEAL_DTO);
						if (dealsArray.length() > 0) deals = (List<Deal>) ResponseParser.parseBrowse(dealsArray);
						final List<DealScreenDTO> dtos = getDealScreenDTOs(deals);
						clipResponce = new ClipResponse(statusCode, s, dtos);
					}
					catch (OutOfMemoryError oome)
					{
						System.gc();
						clipResponce = new ClipResponse(statusCode, httpErrorString, null);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						clipResponce = new ClipResponse(statusCode, httpErrorString, null);
					}
				}
				else
				{
					clipResponce = new ClipResponse(statusCode, httpErrorString, null);
				}
			}
		}
		catch (OutOfMemoryError oome)
		{
			System.gc();
			clipResponce = new ClipResponse(statusCode, httpErrorString, null);
		}
		catch (Exception e)
		{
			clipResponce = new ClipResponse(statusCode, httpErrorString, null);
		}
		return clipResponce;
	}
	
	public HttpMessage createHttpGetRequest(String url, Map<String, String> headers) throws URISyntaxException
	{
		HttpGet t_HttpMethod = new HttpGet(url);
		if (headers != null)
		{
			for (final String key : headers.keySet())
			{
				t_HttpMethod.addHeader(key, headers.get(key));
			}
		}
		return t_HttpMethod;
	}
}
