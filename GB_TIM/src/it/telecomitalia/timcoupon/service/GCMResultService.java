package it.telecomitalia.timcoupon.service;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.onmobile.utils.AppConstants;
import com.onmobile.utils.Utils;

public class GCMResultService extends IntentService
{
	private static final String TAG = "[GreatBuyz]GCMResultService";

	public GCMResultService()
	{
		super("GCMResultService");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		// Log.d(TAG, "onHandleIntent");
		int action = Integer.parseInt(intent.getAction());
		// Log.d(TAG, "onHandleIntent action=" + action);

		String imei = Utils.getAndroidId(getApplicationContext());
		if (!Utils.isNothing(imei))
			GreatBuyzApplication.getDataController().updateAndroidId(imei);

		switch (action)
		{
		/*
		 * case AppConstants.GCM.GCM_RECEIVED: {
		 * if(Utils.isApplicationRunning()) { String message =
		 * intent.getStringExtra(AppConstants.JSONKeys.MESSAGE); // TODO: make
		 * call for UI changes } } break;
		 */
			case AppConstants.GCM.GCM_UPDATE_REGISTRATION_ID:
				{
					Log.d(TAG, "GCM_UPDATE_REGISTRATION_ID");
					String id = intent.getStringExtra("register");
					GreatBuyzApplication.getDataController().updateGCMId(id);
					System.out.println("IntentService"+GreatBuyzApplication.getDataController().getGCMId());
					

					try
					{
						GreatBuyzApplication.getServiceDelegate().updateUserInfo(null, null, null, id, null, -2, imei, null, null, null,null);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				break;
			case AppConstants.GCM.GCM_UPDATE_UNREGISTRATION:
				{
					// Log.d(TAG, "ActionUpdateUnregistration");

					try
					{
						GreatBuyzApplication.getServiceDelegate().updateUserInfo(null, null, null, AppConstants.EMPTY_STRING, null, -1,
								imei, null, null, null,new IOperationListener()
								{
									@Override
									public void onOperationCompleted(boolean p_OperationComplitionStatus, String p_MessageFromServer)
									{
										if (p_OperationComplitionStatus)
										{
											GCMRegistrar.setRegisteredOnServer(getApplicationContext(), false);
										}
										else
										{
											// TODO: attempt later
										}
									}
								});
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				break;
		}
	}
}