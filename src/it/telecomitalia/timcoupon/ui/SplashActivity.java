package it.telecomitalia.timcoupon.ui;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import it.telecomitalia.timcoupon.R;
import it.telecomitalia.timcoupon.analytics.Analytics;
import it.telecomitalia.timcoupon.service.DB;
import it.telecomitalia.timcoupon.service.DataController;
import it.telecomitalia.timcoupon.service.IOperationListener;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.android.gcm.GCMRegistrar;
import com.onmobile.hcoe.ui.dialog.GenericDialog;
import com.onmobile.utils.AppConstants;
import com.onmobile.utils.LocationTracker;
import com.onmobile.utils.TimeOutAsyncTask;
import com.onmobile.utils.TimeOutAsyncTask.TimeOutCallback;
import com.onmobile.utils.Utils;

public class SplashActivity extends Activity implements LocationListener, TimeOutCallback
{
	private final String TAG = "[GreatBuyz]SplashActivity";

	DataController _data;

	double latitude;
	double longitude;
	int radius;
	final long MIN_ACCURACY_FOR_LOCATION = 2000; // 2000 meters
	final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1; // 5 seconds
	int MAX_TIME_FOR_SPLASH = 1000 * 5 * 1 * 0; // 5 seconds // changing to 0
												// sec

	LocationTracker gps;

	TimeOutAsyncTask splashTimeout;
	GreatBuyzApplication _app;

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Log.v("Time test : SplashActivity onCreate start :", "" +
		_app =GreatBuyzApplication.getApplication();
		_app.instantiate();

		_data = GreatBuyzApplication.getDataController();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);

		if (!GreatBuyzApplication.getApplication().isNetworkAvailable())
		{
			String err = Utils.getMessageString(AppConstants.Messages.checkInternetConnection, R.string.checkInternetConnection);
			showErrorDialog(err);
		}
		else
		{
			/**
			 * GCM registration can fail even if the Network connection is not
			 * present. Hence, GCM gcmRegister moved to else if network
			 * connection is present block
			 */
			// obtain GCM registration id for each installation as soon as
			// possible
			try
			{
				int isVersionCode = _app.getPrefrenceIntValue(
						AppConstants.SharedPrefKeys.APP_VERSION_CODE, SplashActivity.this);
				int appVersion = getAppVersion();
				if (isVersionCode == 0) {
					_app.saveIntPrefrence(AppConstants.SharedPrefKeys.APP_VERSION_CODE,
							appVersion, SplashActivity.this);
					isVersionCode = appVersion;
				}
				
				if (isVersionCode < appVersion) {
					_app.saveIntPrefrence(AppConstants.SharedPrefKeys.APP_VERSION_CODE,
							appVersion, SplashActivity.this);
					Utils.gcmRegister(getApplicationContext());
				}else{
					String regId = GCMRegistrar.getRegistrationId(getApplicationContext());
					 if (regId.equals(AppConstants.EMPTY_STRING)){
						 Utils.gcmRegister(getApplicationContext());
					 }
				}
			}
			catch (Exception e)
			{
			}

			Utils.recordUserDetails(this, _data);

			gps = new LocationTracker(this, this, MIN_TIME_BW_UPDATES);
			if (!gps.canGetLocation())
			{
				gps.stopUsingGPS();
				gps = null;
			}
			else
			{
				gps.getLocation();
			}

			splashTimeout = new TimeOutAsyncTask(MAX_TIME_FOR_SPLASH, this);
			splashTimeout.start();
		}
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.setLogEnabled(true);
		FlurryAgent.setUseHttps(true);
		FlurryAgent.setCaptureUncaughtExceptions(true);

		String apiKey = AppConstants.Flurry.API_KEY;
		String srvApiKey = _data.getConstant(AppConstants.Constants.flurryAPIKey);
		if (!Utils.isNothing(srvApiKey))
			apiKey = srvApiKey;

		// FlurryAgent.onStartSession(GreatBuyzApplication.getApplication(),
		// apiKey);
		GreatBuyzApplication.getApplication().getAnalyticsAgent().onStartSession(getApplicationContext(), apiKey);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	public void onBackPressed()
	{
		if (gps != null)
			gps.stopUsingGPS();

		if (splashTimeout != null)
			splashTimeout.cancel();

		super.onBackPressed();

		finish();
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle b)
	{
		String message = b.getString(AppConstants.JSONKeys.MESSAGE);
		switch (id)
		{
			case AppConstants.DialogConstants.UPGRADE_DIALOG:
				{
					final boolean isForcedUpgrade = b.getBoolean(AppConstants.JSONKeys.FORCE, false);
					final String upgradeUrl = b.getString(AppConstants.JSONKeys.URL);

					final GenericDialog dialog1 = new GenericDialog(this, R.layout.exit_message, R.style.AlertDialogCustom);
					dialog1.setCancelable(false);

					Button btn1 = (Button) dialog1.findViewById(R.id.yes_btn);
					btn1.setText(Utils.getMessageString(AppConstants.Messages.upgrade, R.string.upgrade));
					btn1.setTypeface(GreatBuyzApplication.getApplication().getFont());
					btn1.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							try
							{
								removeDialog(AppConstants.DialogConstants.UPGRADE_DIALOG);
								Utils.launchUri(SplashActivity.this, upgradeUrl);
								SplashActivity.this.finish();
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					});
					Button noBtn = (Button) dialog1.findViewById(R.id.no_btn);
					noBtn.setText(getString(R.string.cancel));
					noBtn.setTypeface(GreatBuyzApplication.getApplication().getFont());
					noBtn.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							try
							{
								removeDialog(AppConstants.DialogConstants.UPGRADE_DIALOG);
								if (isForcedUpgrade)
								{
									SplashActivity.this.finish();
								}
								else
									completeGetInfo();
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					});

					TextView msgText1 = (TextView) dialog1.findViewById(R.id.msg);
					msgText1.setText(message);
					msgText1.setTypeface(GreatBuyzApplication.getApplication().getFont());
					TextView titleText11 = (TextView) dialog1.findViewById(R.id.title);
					titleText11.setText(AppConstants.EMPTY_STRING);
					titleText11.setTypeface(GreatBuyzApplication.getApplication().getFont());
					return dialog1;
				}
			case AppConstants.DialogConstants.WAIT_FOR_REG_DIALOG:
				{
					final GenericDialog waitDialog = new GenericDialog(SplashActivity.this, R.layout.status_message,
							R.style.AlertDialogCustom);

					Button waitBtn = (Button) waitDialog.findViewById(R.id.status_btn);
					waitBtn.setText(Utils.getMessageString(AppConstants.Messages.ok, R.string.ok));
					waitBtn.setTypeface(GreatBuyzApplication.getApplication().getFont());
					waitBtn.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{

							try
							{
								waitDialog.dismiss();
								finish();
							}
							catch (Exception e)
							{
								// Log.e("LoginActivity Error message Ok button onClick() ",
								// "Application is crashed due to exception --> "
								// + e.toString());
								e.printStackTrace();
							}

						}
					});
					TextView waitMsgText = (TextView) waitDialog.findViewById(R.id.msg);
					waitMsgText.setText(message);
					waitMsgText.setTypeface(GreatBuyzApplication.getApplication().getFont());
					TextView titleText = (TextView) waitDialog.findViewById(R.id.title);
					titleText.setText(Utils.getMessageString(AppConstants.Messages.titleInfo, R.string.titleInfo));
					titleText.setTypeface(GreatBuyzApplication.getApplication().getFont());
					return waitDialog;
				}
			default:
				{
					final GenericDialog dialog = new GenericDialog(SplashActivity.this, R.layout.status_message, R.style.AlertDialogCustom);
					dialog.setCancelable(false);
					Button btn = (Button) dialog.findViewById(R.id.status_btn);
					btn.setText(Utils.getMessageString(AppConstants.Messages.ok, R.string.ok));
					btn.setTypeface(GreatBuyzApplication.getApplication().getFont());
					btn.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{

							try
							{
								dialog.dismiss();
								finish();

							}
							catch (Exception e)
							{
								// Log.e("LoginActivity Error message Ok button onClick() ",
								// "Application is crashed due to exception --> "
								// + e.toString());
								e.printStackTrace();
							}

						}
					});
					TextView msgText = (TextView) dialog.findViewById(R.id.msg);
					msgText.setText(message);
					msgText.setTypeface(GreatBuyzApplication.getApplication().getFont());
					TextView titleText = (TextView) dialog.findViewById(R.id.title);
					titleText.setText(Utils.getMessageString(AppConstants.Messages.titleInfo, R.string.titleInfo));
					titleText.setTypeface(GreatBuyzApplication.getApplication().getFont());
					return dialog;
				}
		}
	}

	private void getInfo()
	{
		Log.v("Time test : getInfo start :", "" +
		 Utils.getCurrentTimeStamp());
		try
		{
			GreatBuyzApplication.getServiceDelegate().getInfo(latitude, longitude, new IOperationListener()
			{
				@Override
				public void onOperationCompleted(boolean pOperationComplitionStatus, String pMessageFromServer)
				{
					// Log.v("Time test : getInfo end :", "" +
					// Utils.getCurrentTimeStamp());

					if (pOperationComplitionStatus)
					{	
						String newVersion = GreatBuyzApplication.getApplication().getSharedPreferences()
								.getString(DB.COL_VERSION_LOCATIONS, null);
						if (!Utils.isNothing(newVersion))
						{
							getCitiInfo();
						}
						if (_data.isUpgradeAvailable())
						{
							Bundle b = new Bundle();
							b.putString(AppConstants.JSONKeys.MESSAGE, _data.getUpgradeMessage());
							b.putString(AppConstants.JSONKeys.URL, _data.getUpgradeURL());
							b.putBoolean(AppConstants.JSONKeys.FORCE, _data.isUpgradeForced());
							showOtherDialog(AppConstants.DialogConstants.UPGRADE_DIALOG, b);
						}
						else
						{
							completeGetInfo();
						}
					}
					else
					{
						if (pMessageFromServer != null)
							showErrorDialog(pMessageFromServer);
					}
				}
			});
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @author kiranr
	 * Calling getCities API to render Cities list from server side
	 */

	public void getCitiInfo() {
		try {
			GreatBuyzApplication.getServiceDelegate().getCities(
					new IOperationListener() {
						@Override
						public void onOperationCompleted(
								boolean pOperationComplitionStatus,
								String pMessageFromServer) {
							if (pOperationComplitionStatus) {
								Log.i("Cities", "Updated");
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public void completeGetInfo()
	{
		// changing analytics log parameters
		try
		{
			Analytics a = GreatBuyzApplication.getApplication().getAnalyticsAgent();
			a.setMDN(_data.getMDN());
			int f = Integer.parseInt(_data.getConstant(AppConstants.Constants.logFileUploadFrequencyInMinutes));
			a.setLogRotationFrequency(f);
		}
		catch (Exception e)
		{
		}

		// Log.v("Time test : checking subscription status :", "" +
		// Utils.getCurrentTimeStamp());
		String userSubscriptionStatus = _data.getUserSubscriptionStatus();

		// TODO :testing
		// _data.updateMDN("+393333479491");
		// userSubscriptionStatus = AppConstants.UserActivationStatus.ACTIVE;
		// TODO :end

		if (userSubscriptionStatus.equals(AppConstants.UserActivationStatus.ACTIVE)
				|| userSubscriptionStatus.equals(AppConstants.UserActivationStatus.TRIAL))
		{
			// Log.v("Time test : subscription status is ACTIVE starting home screen :",
			// "" + Utils.getCurrentTimeStamp());
			startHomeScreen();
		}
		else if (userSubscriptionStatus.equals(AppConstants.UserActivationStatus.PENDING))
		{
			showErrorDialog(Utils.getMessageString(AppConstants.Messages.subscription_status_pending_message,
					R.string.subscription_status_pending_message));
		}
		else
		{
			SplashActivity.this.finish();
			Intent mainIntent = new Intent(SplashActivity.this, RegistrationFragmentActivity.class);
			SplashActivity.this.startActivity(mainIntent);
		}
	}

	public void startHomeScreen()
	{
		SplashActivity.this.finish();
		Intent mainIntent = new Intent(SplashActivity.this, SampleTabsStyled.class);
		mainIntent.addCategory("com.jakewharton.android.viewpagerindicator.sample.SAMPLE");
		mainIntent.putExtra("com.jakewharton.android.viewpagerindicator.sample.Path", "Tabs");
		SplashActivity.this.startActivity(mainIntent);

		// Log.v("Time test : home screen activity start called :", "" +
		// Utils.getCurrentTimeStamp());
	}

	public void showErrorDialog(final String message)
	{
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{
					System.out.println("message ****"+message);
					Bundle b = new Bundle();
					b.putString(AppConstants.JSONKeys.MESSAGE, message);
					showDialog(AppConstants.DialogConstants.NETWORK_ERROR_DIALOG, b);

				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

			}
		});
	}

	private void showOtherDialog(final int which, final Bundle b)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					showDialog(which, b);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onLocationChanged(Location location)
	{
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		if (gps != null)
			gps.stopUsingGPS();
	}

	@Override
	public void onProviderDisabled(String provider)
	{
	}

	@Override
	public void onProviderEnabled(String provider)
	{
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	}

	@Override
	public void onTimeout()
	{
		if (gps != null)
			gps.stopUsingGPS();
		gps = null;
		// Log.v("Time test : timeout for getting location calling getInfo :",
		// "" + Utils.getCurrentTimeStamp());
		getInfo();
	}
	
	
	/**
	 * @author kiran--- getAppVesion() is for getting the app version code form
	 *         manifest
	 * 
	 */
	private int getAppVersion() {
		PackageInfo pInfo;
		int code = 0;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			String s_code = pInfo.versionName;
			s_code = s_code.replace(".", "");
			code = Integer.parseInt(s_code);
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		return code;
	}
}
