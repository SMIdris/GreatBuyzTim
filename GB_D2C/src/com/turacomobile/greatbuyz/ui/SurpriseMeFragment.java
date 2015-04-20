package com.turacomobile.greatbuyz.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.turacomobile.greatbuyz.GreatBuyzApplication;
import com.turacomobile.greatbuyz.R;
import com.turacomobile.greatbuyz.service.IOperationListener;
import com.turacomobile.greatbuyz.utils.AppConstants;
import com.turacomobile.greatbuyz.utils.Utils;

public final class SurpriseMeFragment extends Fragment implements SensorEventListener
{
	Button btnClickHere;
	TextView txtFailed;
	TextView txtPromo, txtOr;
	static Activity activity;
	SensorManager sensor;

	long lastUpdate;
	long MIN_TIME_BETWEEN_SHAKE_EVENTS = 5000;

	public static SurpriseMeFragment newInstance(String content, Activity screen, OnDealItemClick dealItemClick)
	{
		// ////System.out.println("GreatBuyz: function IN SurpriseMeFragment > newInstance");
		SurpriseMeFragment fragment = new SurpriseMeFragment();

		activity = screen;
		// ////System.out.println("GreatBuyz: function OUT SurpriseMeFragment > newInstance");
		return fragment;
	}

	public void fetchSurpriseDeal()
	{
		// ////System.out.println("GreatBuyz: function IN SurpriseMeFragment > fetchSurpriseDeal");
		txtFailed.setVisibility(View.GONE);
		if (Utils.isDetailDealScreenNewShowing() && DetailScreenNew.activity != null)
			((DetailScreenNew) DetailScreenNew.activity).showLoadingDialog();

		// activity.showDialog(AppConstants.DialogConstants.LOADING_DIALOG);
		showOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
		try
		{
			GreatBuyzApplication.getServiceDelegate().getSurpiseDeal(new IOperationListener()
			{
				@Override
				public void onOperationCompleted(boolean p_OperationComplitionStatus, String p_MessageFromServer)
				{
					// activity.removeDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					if (Utils.isDetailDealScreenNewShowing() && DetailScreenNew.activity != null)
						((DetailScreenNew) DetailScreenNew.activity).removeLoadingDialog();

					if (p_OperationComplitionStatus && !getActivity().isFinishing())
					{
						// Utils.startDetailsScreenNew(getActivity(), 0,
						// AppConstants.FramentConstants.SURPRISE_ME);

						Intent detailScreen = new Intent(activity, DetailScreenNew.class);
						detailScreen.putExtra(AppConstants.JSONKeys.TYPE, AppConstants.FramentConstants.SURPRISE_ME);
						detailScreen.putExtra(AppConstants.JSONKeys.INDEX, 0);
						detailScreen.putExtra(AppConstants.JSONKeys.START_HOME_SCREEN, false);
						detailScreen.putExtra(AppConstants.DEAL_SCREEN, true);
						detailScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						activity.startActivityForResult(detailScreen, AppConstants.RESULT_DEAL_DETAIL_SCREEN);
					}
					else
					{
						showTxtFailedMsgInMainUI(p_MessageFromServer);
					}
				}
			});
		}
		catch (Exception e)
		{
			// activity.removeDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			getActivity().removeDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			if (Utils.isDetailDealScreenNewShowing() && DetailScreenNew.activity != null)
				((DetailScreenNew) DetailScreenNew.activity).removeLoadingDialog();
			String httpErrorString = Utils.getMessageString(AppConstants.Messages.unableToConnect, R.string.unableToConnect);
			showTxtFailedMsgInMainUI(httpErrorString);
			e.printStackTrace();
		}

		// ////System.out.println("GreatBuyz: function OUT SurpriseMeFragment > fetchSurpriseDeal");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// ////System.out.println("GreatBuyz: function IN SurpriseMeFragment > onActivityCreated");
		super.onActivityCreated(savedInstanceState);

		activity = getActivity();

		// ////System.out.println("GreatBuyz: function OUT SurpriseMeFragment > onActivityCreated");
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// ////System.out.println("GreatBuyz: function IN SurpriseMeFragment > onCreate");
		super.onCreate(savedInstanceState);
		// ////System.out.println("GreatBuyz: function OUT SurpriseMeFragment > onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// ////System.out.println("GreatBuyz: function IN SurpriseMeFragment > onCreateView");
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.surprise_me, null);

		btnClickHere = (Button) v.findViewById(R.id.btnClickHere);
		btnClickHere.setTypeface(GreatBuyzApplication.getApplication().getFont());
		Utils.setMessageToButton(btnClickHere, AppConstants.Messages.btnSurpriseMeText);
		btnClickHere.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				fetchSurpriseDeal();
			}
		});

		txtPromo = (TextView) v.findViewById(R.id.txt_promoText);
		Utils.setMessageToTextView(txtPromo, AppConstants.Messages.txtSurpriseMeText);
		txtOr = (TextView) v.findViewById(R.id.txt_or);
		Utils.setMessageToTextView(txtOr, AppConstants.Messages.txtOR);
		txtFailed = (TextView) v.findViewById(R.id.txt_failed);

		// ////System.out.println("GreatBuyz: function OUT SurpriseMeFragment > onCreateView");
		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		// ////System.out.println("GreatBuyz: function IN SurpriseMeFragment > onSaveInstanceState");
		super.onSaveInstanceState(outState);
		// ////System.out.println("GreatBuyz: function OUT SurpriseMeFragment > onSaveInstanceState");
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		// FlurryAgent.onPageView();
		// GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit("SurpriseMe");
		// Kiran // we are taking page name from message table for language
		// support //
		String name = Utils.getMessageString(AppConstants.Messages.surpriseMe, R.string.surpriseMe);
		GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit(name);
	}

	@Override
	public void onStop()
	{
		// ////System.out.println("GreatBuyz: function IN SurpriseMeFragment > onStop");
		super.onStop();
		// ////System.out.println("GreatBuyz: function OUT SurpriseMeFragment > onStop");
	}

	public void startSensor()
	{
		if (sensor == null)
			// sensor = (SensorManager)
			// activity.getSystemService(Context.SENSOR_SERVICE);
			sensor = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		sensor.registerListener(this, sensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
	}

	public void stopSensor()
	{
		if (sensor != null)
		{
			sensor.unregisterListener(this);
			sensor = null;
		}
	}

	private void getAccelerometer(SensorEvent event)
	{
		float[] values = event.values;
		// Movement
		float x = values[0];
		float y = values[1];
		float z = values[2];

		float accelationSquareRoot = (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
		long actualTime = System.currentTimeMillis();
		if (accelationSquareRoot >= 2)
		{
			if (actualTime - lastUpdate < MIN_TIME_BETWEEN_SHAKE_EVENTS)
			{
				return;
			}
			lastUpdate = actualTime;

			fetchSurpriseDeal();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1)
	{
	}

	@Override
	public void onSensorChanged(SensorEvent event)
	{
		if (event != null && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		{
			getAccelerometer(event);
		}
	}

	private void showTxtFailedMsgInMainUI(final String errorText)
	{
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					txtFailed.setText(errorText);
					txtFailed.setVisibility(View.VISIBLE);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	private void showOtherDialog(final int which)
	{
		try
		{
			if (getActivity() != null)
				getActivity().runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							getActivity().showDialog(which);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void removeOtherDialog(final int which)
	{
		try
		{
			if (getActivity() != null)
				getActivity().runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							getActivity().removeDialog(which);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
