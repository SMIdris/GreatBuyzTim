package it.telecomitalia.timcoupon.ui;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import it.telecomitalia.timcoupon.R;
import it.telecomitalia.timcoupon.data.DealScreenDTO;
import it.telecomitalia.timcoupon.framework.L;
import it.telecomitalia.timcoupon.service.ClipResponse;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.onmobile.utils.AppConstants;
import com.onmobile.utils.LocationTracker;
import com.onmobile.utils.TimeOutAsyncTask;
import com.onmobile.utils.TimeOutAsyncTask.TimeOutCallback;
import com.onmobile.utils.Utils;
import com.squareup.picasso.Picasso;

public final class DealsNearMeFragment extends Fragment implements LocationListener, TimeOutCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, android.location.LocationListener
{
	OnDealItemClick			onDealItemClick;
	TextView				   emptyView;
	TextView				   gpsNeededTextView;
	Button					 btnEnableGPS;
	// Button btnEnableGPSRetry;
	ViewSwitcher			   gpsNeededViewSwitcher;
	ViewSwitcher			   viewSwitcher;
	AmazingListView			lsComposer;
	PaginationComposerAdapter  adapter;
	static Activity			activity;
	LocationTracker			gps;
	TimeOutAsyncTask		   locationTimeout;
	boolean					isTaskPending			 = false;
	public boolean			 itemClicked			   = false;
	public boolean			 insideServicesUpdates	 = false;
	String					 locationNotAvailableMessage;
	int						totalVisibleItems		 = 0;
	int						currentCount			  = 0;
	double					 latitude				  = 0.0;
	double					 longitude				 = 0.0;
	int						radius;
	final long				 MIN_ACCURACY_FOR_LOCATION = 5000;		 // 5000 meters
	final long				 MIN_TIME_BW_UPDATES	   = 1000 * 10 * 1; // 10 seconds
	int						MAX_TIME_FOR_LOCATION	 = 1000 * 10 * 1; // 10 seconds
	private static final long  ONE_MIN				   = 1000 * 60;
	private static final long  TWO_MIN				   = ONE_MIN * 2;
	private static final long  FIVE_MIN				  = ONE_MIN * 5;
	private static final long  POLLING_FREQ			  = 1000 * 5;	 // 1000 * 30;
	private static final long  FASTEST_UPDATE_FREQ	   = 1000 * 5;
	private static final float MIN_ACCURACY			  = 25.0f;
	private static final float MIN_LAST_READ_ACCURACY	= 500.0f;
	private LocationRequest	mLocationRequest;
	private Location		   mBestReading;
	private GoogleApiClient	mGoogleApiClient;
	
	public static DealsNearMeFragment newInstance(String content, Activity screen, OnDealItemClick dealItemClick)
	{
		DealsNearMeFragment fragment = new DealsNearMeFragment();
		fragment.onDealItemClick = dealItemClick;
		activity = screen;
		return fragment;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		try
		{
		activity = getActivity();
		locationNotAvailableMessage = Utils.getMessageString(AppConstants.Messages.locationNotAvailables, R.string.locationNotAvailables);
		radius = AppConstants.DEALS_NEAR_ME_RADIUS;
		String serverRadius = GreatBuyzApplication.getDataController().getConstant(AppConstants.Constants.dealsNearMeRadius);
		if (!Utils.isNothing(serverRadius))
		{
			try
			{
				radius = Integer.parseInt(serverRadius);
			}
			catch (Exception e)
			{
				if (GreatBuyzApplication.isDebug) L.i("\nStack Trace: " + Thread.currentThread().getStackTrace()[2] + "\nMessage Exception : " + e.getMessage());
			}
		}
		if (GreatBuyzApplication.getDataController().getDealsNearMeDTO() != null && GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().size() > 0)
		{
			initialiseList();
		}
		lsComposer.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				if (arg2 < GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().size())
				{
					Utils.startDetailsScreenNew(getActivity(), arg2, AppConstants.FramentConstants.DEALS_NEAR_ME);
					itemClicked = true;
				}
			}
		});
		lsComposer.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) lsComposer.invalidateViews();
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{}
		});
		// if (!servicesAvailable())
		// {
		// //finish();
		// noGoogleServices();
		// }
		// setContentView(R.layout.row);
	}
	catch (Exception e)
	{
		if (GreatBuyzApplication.isDebug) L.i("\nStack Trace: " + Thread.currentThread().getStackTrace()[2] + "\nMessage Exception : " + e.getMessage());
	}
	}
	
	@Override
	public void onLocationChanged(Location location)
	{
		// Determine whether new location is better than current best
		// estimate
		try
		{
		if (null == mBestReading || location.getAccuracy() < mBestReading.getAccuracy())
		{
			if (gps != null) gps.stopUsingGPS();
			if (locationTimeout != null)
			{
				locationTimeout.cancel(true);
				locationTimeout = null;
			}
			mBestReading = location;
			if (servicesAvailable() && insideServicesUpdates) LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
			latitude = mBestReading.getLatitude();
			longitude = mBestReading.getLongitude();
			listDownloader = new MyListDownloader();
			listDownloader.execute();
			// if (mBestReading.getAccuracy() < MIN_ACCURACY)
			// {
			// }
		}
		
	}
	catch (Exception e)
	{
		if (GreatBuyzApplication.isDebug) L.i("\nStack Trace: " + Thread.currentThread().getStackTrace()[2] + "\nMessage Exception : " + e.getMessage());
	}
	}
	
	@Override
	public void onConnected(Bundle dataBundle)
	{
		// Get first reading. Get additional location updates if necessary
		try
		{
		if (servicesAvailable())
		{
			// Get best last location measurement meeting criteria
			mBestReading = bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);
			if (null == mBestReading || mBestReading.getAccuracy() > MIN_LAST_READ_ACCURACY || mBestReading.getTime() < System.currentTimeMillis() - TWO_MIN)
			{
				LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
				// Schedule a runnable to unregister location listeners
				insideServicesUpdates = true;
				if (locationTimeout != null)
				{
					locationTimeout.cancel(true);
					locationTimeout = null;
				}
				locationTimeout = new TimeOutAsyncTask(MAX_TIME_FOR_LOCATION + MAX_TIME_FOR_LOCATION, this);
				locationTimeout.start();
			}
			else
			{
				// get info from server
				LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
				latitude = mBestReading.getLatitude();
				longitude = mBestReading.getLongitude();
				listDownloader = new MyListDownloader();
				listDownloader.execute();
			}
		}
		else
		{
			noGoogleServices();
		}
		}
		catch (Exception e)
		{
			if (GreatBuyzApplication.isDebug) L.i("\nStack Trace: " + Thread.currentThread().getStackTrace()[2] + "\nMessage Exception : " + e.getMessage());
		}
		
	}
	
	public void noGoogleServices()
	{
		if (gps == null) gps = new LocationTracker(getActivity(), this, MIN_TIME_BW_UPDATES);
		gps.getLocation();
		if (gps.canGetLocation())
		{
			gpsNeededViewSwitcher.setDisplayedChild(0);
			if (locationTimeout != null)
			{
				locationTimeout.cancel(true);
				locationTimeout = null;
			}
			int timeToWait = MAX_TIME_FOR_LOCATION;
			if (gps.fromGPS())
			{
				timeToWait = MAX_TIME_FOR_LOCATION + MAX_TIME_FOR_LOCATION;
			}
			locationTimeout = new TimeOutAsyncTask(timeToWait, this);
			locationTimeout.start();
		}
		else
			gpsNeededViewSwitcher.setDisplayedChild(1);
	}
	
	@Override
	public void onConnectionSuspended(int i)
	{}
	
	private Location bestLastKnownLocation(float minAccuracy, long minTime)
	{
		Location bestResult = null;
		float bestAccuracy = Float.MAX_VALUE;
		long bestTime = Long.MIN_VALUE;
		// Get the best most recent location currently available
		Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (mCurrentLocation != null)
		{
			float accuracy = mCurrentLocation.getAccuracy();
			long time = mCurrentLocation.getTime();
			if (accuracy < bestAccuracy)
			{
				bestResult = mCurrentLocation;
				bestAccuracy = accuracy;
				bestTime = time;
			}
		}
		// Return best reading or null
		if (bestAccuracy > minAccuracy || bestTime < minTime)
		{
			return null;
		}
		else
		{
			return bestResult;
		}
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult)
	{}
	
	private boolean servicesAvailable()
	{
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
		if (ConnectionResult.SUCCESS == resultCode)
		{
			LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
			boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			// getting network status
			boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			// First get location from GPS Provider
			if (!isGPSEnabled && !isNetworkEnabled)
				return false;
			else
				return true;
		}
		else
		{
			// GooglePlayServicesUtil.getErrorDialog(resultCode, activity, 0).show();
			return false;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		try{
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(POLLING_FREQ);
		mLocationRequest.setNumUpdates(1);
		// mLocationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);
		mGoogleApiClient = new GoogleApiClient.Builder(activity).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
	}
	catch (Exception e)
	{
		if (GreatBuyzApplication.isDebug) L.i("\nStack Trace: " + Thread.currentThread().getStackTrace()[2] + "\nMessage Exception : " + e.getMessage());
	}
	}
	
	public MyListDownloader listDownloader;
	
	public MyListDownloader getListDownloader()
	{
		return listDownloader;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.listfragment, null);
		try{
		
		lsComposer = (AmazingListView) v.findViewById(R.id.lsComposer);
		gpsNeededViewSwitcher = (ViewSwitcher) v.findViewById(R.id.gpsNeededViewSwitcher);
		gpsNeededViewSwitcher.setDisplayedChild(0);
		viewSwitcher = (ViewSwitcher) v.findViewById(android.R.id.empty);
		viewSwitcher.setDisplayedChild(0);
		emptyView = (TextView) v.findViewById(R.id.emptyText);
		emptyView.setTypeface(GreatBuyzApplication.getApplication().getFont());
		String msg = Utils.getMessageString(AppConstants.Messages.emptyDeal, R.string.emptyDeal);
		emptyView.setText(msg);
		gpsNeededTextView = (TextView) v.findViewById(R.id.gpsNeededTextView);
		gpsNeededTextView.setTypeface(GreatBuyzApplication.getApplication().getFont());
		Utils.setMessageToTextView(gpsNeededTextView, AppConstants.Messages.gpsNeededMessage);
		btnEnableGPS = (Button) v.findViewById(R.id.btnEnableGPS);
		btnEnableGPS.setTypeface(GreatBuyzApplication.getApplication().getFont());
		Utils.setMessageToButton(btnEnableGPS, AppConstants.Messages.enable);
		btnEnableGPS.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			}
		});
		lsComposer.setEmptyView(viewSwitcher);
		}
		catch (Exception e)
		{
			if (GreatBuyzApplication.isDebug) L.i("\nStack Trace: " + Thread.currentThread().getStackTrace()[2] + "\nMessage Exception : " + e.getMessage());
		}
		return v;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}
	
	public void initialiseList()
	{
		// activity.runOnUiThread(new Runnable()
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					lsComposer.setAdapter(adapter = new PaginationComposerAdapter());
					totalVisibleItems = lsComposer.getLastVisiblePosition() - lsComposer.getFirstVisiblePosition();
					adapter.notifyDataSetChanged();
					if (morePageAvailable()) adapter.notifyMayHaveMorePages();
				}
				catch (Exception e)
				{
					if (GreatBuyzApplication.isDebug) L.i("\nStack Trace: " + Thread.currentThread().getStackTrace()[2] + "\nMessage Exception : " + e.getMessage());
				}
			}
		});
	}
	
	class PaginationComposerAdapter extends AmazingAdapter
	{
		public PaginationComposerAdapter()
		{}
		
		public void reset()
		{
			viewSwitcher.setDisplayedChild(1);
			GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().clear();
		}
		
		@Override
		public int getCount()
		{
			try
			{
				if (morePageAvailable())
					return GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().size() + 1;
				else
					return GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().size();
			}
			catch (Exception e)
			{}
			return 0;
		}
		
		@Override
		public DealScreenDTO getItem(int position)
		{
			return GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().get(position);
		}
		
		@Override
		public long getItemId(int position)
		{
			return position;
		}
		
		@Override
		public void onNextPageRequested(int page)
		{
			// Log.d(TAG, "Got onNextPageRequested page=" + page);
			if (listDownloader != null && listDownloader.getStatus() != AsyncTask.Status.FINISHED) { return; }
			listDownloader = new MyListDownloader();
			listDownloader.execute();
		}
		
		@Override
		public void bindSectionHeader(View view, int position, boolean displaySectionHeader)
		{}
		
		class ViewHolder
		{
			ImageView img;
			TextView  txtDealDesc;
			TextView  txtDealVal;
			TextView  txtDealDisc;
			TextView  txtDealPay;
			TextView  txtEuroSymbol1;
			TextView  txtEuroSymbol2;
			TextView  txtPercentSymbol;
			TextView  txtDealPayHeading;
			TextView  txtDealValHeading;
			TextView  txtDealDiscHeading;
			TextView  txtDealLocation;
			
			public ViewHolder(View v)
			{
				img = (ImageView) v.findViewById(R.id.img_deal_new);
				txtDealDesc = (TextView) v.findViewById(R.id.txt_new_deal_desc);
				txtDealVal = (TextView) v.findViewById(R.id.txt_deal_value);
				txtDealDisc = (TextView) v.findViewById(R.id.txt_deal_desc_val);
				txtDealPay = (TextView) v.findViewById(R.id.txt_deal_pay_val);
				txtDealPayHeading = (TextView) v.findViewById(R.id.txt_deal_pay_text);
				txtDealValHeading = (TextView) v.findViewById(R.id.txt_deal_value_text);
				txtDealDiscHeading = (TextView) v.findViewById(R.id.txt_deal_desc_text);
				txtDealLocation = (TextView) v.findViewById(R.id.txt_new_deal_location);
				Typeface font = GreatBuyzApplication.getApplication().getFont();
				txtDealVal.setTypeface(font);
				txtDealDisc.setTypeface(font);
				txtDealPay.setTypeface(font);
				txtDealValHeading.setTypeface(font);
				txtDealDiscHeading.setTypeface(font);
				txtDealPayHeading.setTypeface(font);
				txtDealDesc.setTypeface(font);
				txtEuroSymbol1 = (TextView) v.findViewById(R.id.txt_euro_symbol_1);
				txtEuroSymbol1.setTypeface(font);
				txtEuroSymbol2 = (TextView) v.findViewById(R.id.txt_euro_symbol_2);
				txtEuroSymbol2.setTypeface(font);
				txtPercentSymbol = (TextView) v.findViewById(R.id.txt_percent_symbol);
				txtPercentSymbol.setTypeface(font);
			}
		}
		
		@Override
		public View getAmazingView(int position, View convertView, ViewGroup parent)
		{
			View row = null;
			ViewHolder holder = null;
			if (position < GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().size())
			{
				row = convertView;
				final DealScreenDTO item = getItem(position);
				boolean createNew = false;
				if (row == null)
				{
					createNew = true;
				}
				else
				{
					holder = (ViewHolder) row.getTag();
					if (holder == null) createNew = true;
				}
				if (createNew)
				{
					if (Utils.useExpandedImageDealLayout())
						// row = (View)
						// activity.getLayoutInflater().inflate(R.layout.deallistitemtim,
						// null);
						row = (View) getActivity().getLayoutInflater().inflate(R.layout.deallistitemimageexpand, null);
					else
						// row = (View)
						// activity.getLayoutInflater().inflate(R.layout.deallistitem,
						// null);
						row = (View) getActivity().getLayoutInflater().inflate(R.layout.deallistitem, null);
					holder = new ViewHolder(row);
					row.setTag(holder);
				}
				holder.img.setImageResource(R.drawable.default_category);
				String imgUrl = item.getImage();
				holder.img.setTag(imgUrl);
				if (!Utils.isNothing(imgUrl)) Picasso.with(activity).load(imgUrl).config(Bitmap.Config.RGB_565).resize(320, 180).centerCrop().into(holder.img);
				holder.txtDealDesc.setText(item.getName());
				holder.txtDealVal.setText(String.valueOf(item.getPrice()));
				String discount = item.getDiscount();
				if (discount.equals("0"))
				{
					discount = "-";
					holder.txtPercentSymbol.setVisibility(View.INVISIBLE);
				}
				else
				{
					String percent = getResources().getString(R.string.percent);
					if (discount.endsWith(percent))
					{
						discount = discount.substring(0, discount.lastIndexOf(percent) - 1);
					}
					holder.txtPercentSymbol.setVisibility(View.VISIBLE);
				}
				holder.txtDealDisc.setText(discount);
				holder.txtDealPay.setText(String.valueOf(item.getCouponPrice()));
				holder.txtDealLocation.setText("Città: " + item.get_dealLocation());
				holder.txtDealLocation.setVisibility(View.VISIBLE);
				// Utils.setLocationToDealTextView(holder.txtDealLocation, item.get_dealLocation());
			}
			else
			{
				// row =
				// activity.getLayoutInflater().inflate(R.layout.loading_view,
				// null);
				row = getActivity().getLayoutInflater().inflate(R.layout.searching_deals_view, null);
			}
			return row;
		}
		
		@Override
		public void configurePinnedHeader(View header, int position, int alpha)
		{}
		
		@Override
		public int getPositionForSection(int section)
		{
			return 0;
		}
		
		@Override
		public int getSectionForPosition(int position)
		{
			return 0;
		}
		
		@Override
		public Object[] getSections()
		{
			return null;
		}
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (gps != null)
		{
			gps.stopUsingGPS();
			gps = null;
		}
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		// Toast.makeText(getActivity().getBaseContext(), "onStart", Toast.LENGTH_SHORT).show();
		// if (gps == null)
		// gps = new LocationTracker(getActivity(), this, MIN_TIME_BW_UPDATES);
		// gps.getLocation();
		// if ( !itemClicked)
		// {
		// refreshFragment();
		// }
		// else
		// itemClicked = false;
		// FlurryAgent.onPageView();
		// GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit("DealsNearMe");
		// Kiran // we are taking page name from message table for language support //
		String name = Utils.getMessageString(AppConstants.Messages.dealsNearMe, R.string.dealsNearMe);
		GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit(name);
	}
	
	@Override
	public void onPause()
	{
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
		{
			mGoogleApiClient.disconnect();
		}
		boolean isTaskRunning = false;
		if (listDownloader != null) isTaskRunning = listDownloader.getStatus() == AsyncTask.Status.FINISHED ? false : true;
		if (isTaskRunning)
		{
			try
			{
				listDownloader.cancel(true);
			}
			catch (Exception e)
			{}
			// adapter = null;
			// listDownloader = null;
			isTaskPending = true;
		}
		super.onPause();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		// if (isTaskPending)
		// refreshFragment();
		isTaskPending = false;
		// Toast.makeText(getActivity().getBaseContext(), "onResume", Toast.LENGTH_SHORT).show();
		// refreshFragment();
		if (!itemClicked)
		{
			refreshFragment();
		}
		else
			itemClicked = false;
	}
	
	class MyListDownloader extends AsyncTask<Void, Void, ClipResponse>
	{
		@Override
		protected ClipResponse doInBackground(Void... params)
		{
			ClipResponse clipResponse = null;
			try
			{
				// if (gps != null)
				// {
				clipResponse = GreatBuyzApplication.getServiceDelegate().getDealsNearMe(AppConstants.JSONKeys.WAP, latitude, longitude, radius, GreatBuyzApplication.getApplication().getLimitForDealsNearMe(), GreatBuyzApplication.getApplication().getSkipIndexForDealsNearMe());
				// }
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return clipResponse;
		}
		
		@Override
		protected void onPostExecute(ClipResponse result)
		{
			try
			{
				if (result != null && result.getResponceCode() == 200)
				{
					if (result.getListData() != null && result.getListData().size() > 0)
					{
						if (GreatBuyzApplication.getDataController() == null) return;
						GreatBuyzApplication.getDataController().dealsNearMeReceived(result.getListData());
						currentCount = result.getListData().size();
						if (adapter == null)
						{
							initialiseList();
						}
						else
						{
							adapter.notifyDataSetChanged();
						}
						if (morePageAvailable())
						{
							adapter.notifyMayHaveMorePages();
						}
						else
						{
							adapter.notifyNoMorePages();
						}
					}
					else
					{
						if (GreatBuyzApplication.getDataController().getDealsNearMeDTO() == null || GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().size() == 0)
						{
							viewSwitcher.setDisplayedChild(1);
						}
						else
						{
							if (adapter != null) adapter.notifyNoMorePages();
						}
						if (adapter != null) adapter.notifyDataSetChanged();
					}
				}
				else
				{
					if (GreatBuyzApplication.getDataController().getDealsNearMeDTO() == null || GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().size() == 0)
					{
						if (emptyView != null)
						{
							String err = Utils.getMessageString(AppConstants.Messages.networkProblemMessage, R.string.networkProblemMessage);
							emptyView.setText(err);
						}
						viewSwitcher.setDisplayedChild(1);
					}
					else
					{
						if (adapter != null) adapter.notifyNoMorePages();
					}
					if (adapter != null) adapter.notifyDataSetChanged();
				}
				super.onPostExecute(result);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public boolean morePageAvailable()
	{
		if (lsComposer == null) return false;
		totalVisibleItems = lsComposer.getLastVisiblePosition() - lsComposer.getFirstVisiblePosition();
		boolean morePage = false;
		try
		{
			if (GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().size() >= totalVisibleItems && currentCount >= GreatBuyzApplication.getApplication().getLimitForDealsNearMe()) morePage = true;
		}
		catch (Exception e)
		{}
		return morePage;
	}
	
	public void stopDownloader()
	{
		boolean isTaskRunning = false;
		if (listDownloader != null) isTaskRunning = listDownloader.getStatus() == AsyncTask.Status.FINISHED ? false : true;
		if (isTaskRunning)
		{
			try
			{
				listDownloader.cancel(true);
			}
			catch (Exception e)
			{}
		}
	}
	
	public void refreshFragment()
	{
		stopDownloader();
		listDownloader = null;
		if (GreatBuyzApplication.getDataController().getDealsNearMeDTO() != null && GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList() != null && GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().size() > 0)
		{
			GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().clear();
		}
		GreatBuyzApplication.getApplication().setSkipIndexForDealsNearMe(0);
		if (adapter != null) adapter.notifyDataSetChanged();
		adapter = null;
		viewSwitcher.setDisplayedChild(0);
		gpsNeededViewSwitcher.setDisplayedChild(0);
		if (locationTimeout == null)
		{
			if (servicesAvailable())
			{
				if (mGoogleApiClient != null)
				{
					mGoogleApiClient.disconnect();
					mGoogleApiClient.connect();
				}
			}
			else
			{
				noGoogleServices();
			}
		}
	}
	
	@Override
	public void onTimeout()
	{
		if (locationTimeout != null)
		{
			locationTimeout.cancel(true);
			locationTimeout = null;
		}
		if (insideServicesUpdates)
		{
			insideServicesUpdates = false;
			if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
			{
				mGoogleApiClient.disconnect();
			}
			noGoogleServices();
		}
		else
		{
			LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
			// boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (gps.fromGPS())
			{
				boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
				if (isNetworkEnabled)
				{
					gps.getLocationFromNetwork();
					if (locationTimeout != null)
					{
						locationTimeout.cancel(true);
						locationTimeout = null;
					}
					locationTimeout = new TimeOutAsyncTask(MAX_TIME_FOR_LOCATION, this);
					locationTimeout.start();
				}
				else
				{
					noLocationAvailable();
				}
			}
			else
			{
				if (locationManager != null)
				{
					// Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					mBestReading = bestLastKnownLocationUsingAPI(MIN_LAST_READ_ACCURACY, FIVE_MIN, locationManager, LocationManager.GPS_PROVIDER);
					if (null == mBestReading || mBestReading.getAccuracy() > MIN_LAST_READ_ACCURACY || mBestReading.getTime() < System.currentTimeMillis() - TWO_MIN)
					{
						// location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						mBestReading = bestLastKnownLocationUsingAPI(MIN_LAST_READ_ACCURACY, FIVE_MIN, locationManager, LocationManager.NETWORK_PROVIDER);
						if (null == mBestReading || mBestReading.getAccuracy() > MIN_LAST_READ_ACCURACY || mBestReading.getTime() < System.currentTimeMillis() - TWO_MIN)
						{
							// this.canGetLocation = false;
							noLocationAvailable();
						}
						else
						{
							locationFromLastKnown(mBestReading);
						}
					}
					else
					{
						locationFromLastKnown(mBestReading);
					}
				}
			}
		}
	}
	
	public void noLocationAvailable()
	{
		if (emptyView != null && longitude == 0.0 && latitude == 0.0)
		{
			emptyView.setText(locationNotAvailableMessage);
		}
		if (viewSwitcher != null) viewSwitcher.setDisplayedChild(1);
		if (listDownloader != null) listDownloader.cancel(false);
	}
	
	private Location bestLastKnownLocationUsingAPI(float minAccuracy, long minTime, LocationManager locationManager, String locationProvider)
	{
		Location bestResult = null;
		float bestAccuracy = Float.MAX_VALUE;
		long bestTime = Long.MIN_VALUE;
		// Get the best most recent location currently available
		Location mCurrentLocation = locationManager.getLastKnownLocation(locationProvider);
		if (mCurrentLocation != null)
		{
			float accuracy = mCurrentLocation.getAccuracy();
			long time = mCurrentLocation.getTime();
			if (accuracy < bestAccuracy)
			{
				bestResult = mCurrentLocation;
				bestAccuracy = accuracy;
				bestTime = time;
			}
		}
		// Return best reading or null
		if (bestAccuracy > minAccuracy || bestTime < minTime)
		{
			return null;
		}
		else
		{
			return bestResult;
		}
	}
	
	public void locationFromLastKnown(Location location)
	{
		if (gps != null) gps.stopUsingGPS();
		if (locationTimeout != null)
		{
			locationTimeout.cancel(true);
			locationTimeout = null;
		}
		mBestReading = location;
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		listDownloader = new MyListDownloader();
		listDownloader.execute();
	}
	
	@Override
	public void onProviderDisabled(String arg0)
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onProviderEnabled(String arg0)
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2)
	{
		// TODO Auto-generated method stub
	}
}
