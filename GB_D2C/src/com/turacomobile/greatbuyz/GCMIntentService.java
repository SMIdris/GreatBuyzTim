package com.turacomobile.greatbuyz;


import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.turacomobile.greatbuyz.service.GCMResultService;
import com.turacomobile.greatbuyz.utils.AppConstants;
import com.turacomobile.greatbuyz.utils.Utils;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService
{
	// @SuppressWarnings("hiding")
	private static final String TAG = "GCMIntentService";
	private String projectId;

	public GCMIntentService()
	{
		super();
		projectId = AppConstants.GCM.GCM_PROJECT_ID;

		try
		{
			String srvProjectId = GreatBuyzApplication.getDataController().getConstant(AppConstants.Constants.gcmProjectId);
			if (!Utils.isNothing(srvProjectId))
				projectId = srvProjectId;
		}
		catch (Exception e)
		{
			// Log.d(TAG,
			// "Database is still not instantiated, using hardcoded GCM project id");
		}

		FlurryAgent.setLogEnabled(true);
		FlurryAgent.setUseHttps(true);
		FlurryAgent.setCaptureUncaughtExceptions(true);

		String apiKey = AppConstants.Flurry.API_KEY;
		try
		{
			String srvApiKey = GreatBuyzApplication.getDataController().getConstant(AppConstants.Constants.flurryAPIKey);
			if (!Utils.isNothing(srvApiKey))
				apiKey = srvApiKey;
		}
		catch (Exception ee)
		{
			// Log.d(TAG,
			// "Database is still not instantiated, using hardcoded Flurry API key");
		}

		// FlurryAgent.onStartSession(GreatBuyzApplication.getApplication(),
		// apiKey);
		/*try
		{
			GreatBuyzApplication.getApplication().getAnalyticsAgent().onStartSession(getApplicationContext(), apiKey);
		}
		catch (Exception e)
		{
			// Log.d(TAG,
			// "GCMIntentService: Failed to start analytics session");
		}*/
	}

	@Override
	protected String[] getSenderIds(Context context)
	{
		String[] ids = new String[] { projectId };
		return ids;
	}

	/**
	 * Called after the app has been registered on the GCM service We now have
	 * the regID that we can use to register with the GreatBuyz server.
	 */
	@Override
	protected void onRegistered(Context context, String registrationId)
	{
		// Log.i(TAG, "Device registered: regId = " + registrationId);
		Context appContext = GreatBuyzApplication.getApplication().getApplicationContext();
		//boolean isApplicationRunning = Utils.isApplicationRunning();
		
		Intent service = new Intent(appContext, GCMResultService.class);
		service.setAction(Integer.toString(AppConstants.GCM.GCM_UPDATE_REGISTRATION_ID));
		service.putExtra("register", registrationId);
		context.startService(service);

		/*if (isApplicationRunning)
		{
			Intent service = new Intent(appContext, GCMResultService.class);
			service.setAction(Integer.toString(AppConstants.GCM.GCM_UPDATE_REGISTRATION_ID));
			service.putExtra("register", registrationId);
			context.startService(service);
		}*/
	}

	/**
	 * Called after the device has been unregisterd from the GCM server. We need
	 * to unregister from GreatBuyz server too.
	 */
	@Override
	protected void onUnregistered(Context context, String registrationId)
	{

		// Log.i(TAG, "Device unregistered");

		if (GCMRegistrar.isRegisteredOnServer(context))
		{
			Context appContext = GreatBuyzApplication.getApplication();
			boolean isApplicationRunning = Utils.isApplicationRunning();

			if (isApplicationRunning)
			{
				Intent service = new Intent(appContext, GCMResultService.class);
				service.setAction(Integer.toString(AppConstants.GCM.GCM_UPDATE_UNREGISTRATION));
				context.startService(service);
			}
		}
		else
		{
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			// Log.i(TAG, "Ignoring unregister callback");
		}
	}

	/**
	 * We have received a push notification from GCM, analyse the intents bundle
	 * for the payload.
	 */
	@Override
	protected void onMessage(Context context, Intent intent)
	{

		 Log.i(TAG, "Received message");
		String message = null;

		if (intent != null)
		{
			// Check the bundle for the pay load body and title
			Bundle bundle = intent.getExtras();
			if (bundle != null)
			{
				message = bundle.getString(AppConstants.JSONKeys.MESSAGE);
				Utils.NotifyUser(context, message);
			}
		}
	}

	@Override
	protected void onDeletedMessages(Context context, int total)
	{
		/*
		 * Called when the GCM servers tells that app that pending messages have
		 * been deleted because the device was idle.
		 */
		// Log.i(TAG, "Received deleted messages notification");
	}

	/**
	 * Called on registration or unregistration error. Whatever this error is,
	 * it is not recoverable
	 */
	@Override
	public void onError(Context context, String errorId)
	{
		Log.i(TAG, "Received error: " + errorId);
		HashMap<String, String> m = new HashMap<String, String>();
		m.put(AppConstants.Flurry.GCM_PROJECT_ID, projectId);
		m.put(AppConstants.Flurry.GCM_REG_ERROR, errorId);
		// FlurryAgent.logEvent(AppConstants.Flurry.GCM_PROJECT_ID, m);
		try
		{
			String mdn = GreatBuyzApplication.getDataController().getMDN();
			if(!Utils.isNothing(mdn))
				GreatBuyzApplication.getApplication().getAnalyticsAgent().setMDN(mdn);
			GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.GCM_PROJECT_ID, m);
		}
		catch (Exception e)
		{
			// Log.d(TAG,
			// "GCMIntentService: Failed to log GCM error to analytics");
		}
	}

	/**
	 * Called on a registration error that could be retried. By default, it does
	 * nothing and returns true, but could be overridden to change that
	 * behaviour and/or display the error.
	 */
	@Override
	protected boolean onRecoverableError(Context context, String errorId)
	{
		// log message
		// Log.i(TAG, "Received recoverable error: " + errorId);

		HashMap<String, String> m = new HashMap<String, String>();
		m.put(AppConstants.Flurry.GCM_PROJECT_ID, projectId);
		m.put(AppConstants.Flurry.GCM_REG_ERROR, errorId);
		// FlurryAgent.logEvent(AppConstants.Flurry.GCM_PROJECT_ID, m);
		try
		{
			String mdn = GreatBuyzApplication.getDataController().getMDN();
			if(!Utils.isNothing(mdn))
				GreatBuyzApplication.getApplication().getAnalyticsAgent().setMDN(mdn);
			GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.GCM_PROJECT_ID, m);
		}
		catch (Exception e)
		{
			// Log.d(TAG,
			// "GCMIntentService: Failed to log GCM error to analytics");
		}

		return super.onRecoverableError(context, errorId);
	}
}
