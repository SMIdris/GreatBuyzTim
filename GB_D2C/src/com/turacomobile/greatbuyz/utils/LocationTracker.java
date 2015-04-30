package com.turacomobile.greatbuyz.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

public class LocationTracker extends Service
{
	private final Context mContext;

	// flag for GPS device availability
	boolean hasGPSDevice = false;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 30; // 30 meters

	// The minimum time between updates in milliseconds
	private static long MIN_TIME_BW_UPDATES; // = 1000 * 5 * 1; // 5 seconds

	// Declaring a Location Manager
	protected LocationManager locationManager;
	protected LocationListener locationListener;

	public LocationTracker(Context context, LocationListener locationListener, long minTimeBetwnUpdates)
	{
		this.mContext = context;
		this.locationListener = locationListener;
		MIN_TIME_BW_UPDATES = minTimeBetwnUpdates;
		getLocation();
	}

	public Location getLocation()
	{
		try
		{
			locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

			// getting GPS device availability
			PackageManager pm = mContext.getPackageManager();
			hasGPSDevice = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
			if (!hasGPSDevice)
				return null;

			// getting GPS status
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled)
			{
				canGetLocation = false;
			}
			else
			{
				this.canGetLocation = true;
				// First get location from Network Provider
				if (isNetworkEnabled)
				{
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
					// Log.d("Network", "Network");
					if (locationManager != null)
					{
						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null)
						{
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled)
				{
					if (location == null)
					{
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
						// Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null)
						{
							location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null)
							{
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return location;
	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void stopUsingGPS()
	{
		if (locationManager != null)
		{
			locationManager.removeUpdates(locationListener);
		}
	}

	/**
	 * Function to get latitude
	 * */
	public double getLatitude()
	{
		if (location != null)
		{
			latitude = location.getLatitude();
		}

		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude()
	{
		if (location != null)
		{
			longitude = location.getLongitude();
		}

		// return longitude
		return longitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation()
	{
		return this.canGetLocation;
	}

	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}
}