package com.turacomobile.greatbuyz.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
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

import com.squareup.picasso.Picasso;
import com.turacomobile.greatbuyz.GreatBuyzApplication;
import com.turacomobile.greatbuyz.R;
import com.turacomobile.greatbuyz.data.DealScreenDTO;
import com.turacomobile.greatbuyz.service.ClipResponse;
import com.turacomobile.greatbuyz.utils.AppConstants;
import com.turacomobile.greatbuyz.utils.LocationTracker;
import com.turacomobile.greatbuyz.utils.TimeOutAsyncTask;
import com.turacomobile.greatbuyz.utils.TimeOutAsyncTask.TimeOutCallback;
import com.turacomobile.greatbuyz.utils.Utils;

public final class DealsNearMeFragment extends Fragment implements LocationListener, TimeOutCallback
{
	OnDealItemClick onDealItemClick;

	TextView emptyView;
	TextView gpsNeededTextView;
	Button btnEnableGPS;
	ViewSwitcher gpsNeededViewSwitcher;
	ViewSwitcher viewSwitcher;
	AmazingListView lsComposer;
	PaginationComposerAdapter adapter;
	static Activity activity;
	LocationTracker gps;
	TimeOutAsyncTask locationTimeout;
	boolean isTaskPending = false;

	String locationNotAvailableMessage;
	int totalVisibleItems = 0;
	int currentCount = 0;
	double latitude;
	double longitude;
	int radius;
	final long MIN_ACCURACY_FOR_LOCATION = 5000; // 5000 meters
	final long MIN_TIME_BW_UPDATES = 1000 * 10 * 1; // 10 seconds
	int MAX_TIME_FOR_LOCATION = 1000 * 60 * 1; // 60 seconds

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

		activity = getActivity();

		locationNotAvailableMessage = Utils.getMessageString(AppConstants.Messages.locationNotAvailable, R.string.locationNotAvailable);

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
			}
		}

		if (GreatBuyzApplication.getDataController().getDealsNearMeDTO() != null
				&& GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().size() > 0)
		{
			initialiseList();
		}
		else
		{
			if (gps == null)
				gps = new LocationTracker(getActivity(), this, MIN_TIME_BW_UPDATES);

			gps.getLocation();
			if (gps.canGetLocation())
			{
				gpsNeededViewSwitcher.setDisplayedChild(0);
				// lsComposer.setLoadingView(activity.getLayoutInflater().inflate(R.layout.loading_view,
				// null));
				lsComposer.setLoadingView(getActivity().getLayoutInflater().inflate(R.layout.loading_view, null));
				if (locationTimeout != null)
				{
					locationTimeout.cancel(true);
					locationTimeout = null;
				}
				locationTimeout = new TimeOutAsyncTask(MAX_TIME_FOR_LOCATION, this);
				locationTimeout.start();
			}
			else
				gpsNeededViewSwitcher.setDisplayedChild(1);
		}

		lsComposer.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				/*if (arg2 < GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().size())
					Utils.startDetailsScreenNew(getActivity(), arg2, AppConstants.FramentConstants.DEALS_NEAR_ME);*/
			}
		});

		lsComposer.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE)
					lsComposer.invalidateViews();
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
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

		if (gps == null)
			gps = new LocationTracker(getActivity(), this, MIN_TIME_BW_UPDATES);

		gps.getLocation();
		if (gps.canGetLocation())
		{
			gpsNeededViewSwitcher.setDisplayedChild(0);
			if (locationTimeout != null)
			{
				locationTimeout.cancel(true);
				locationTimeout = null;
			}
			locationTimeout = new TimeOutAsyncTask(MAX_TIME_FOR_LOCATION, this);
			locationTimeout.start();
		}
		else
			gpsNeededViewSwitcher.setDisplayedChild(1);
		lsComposer.setEmptyView(viewSwitcher);
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
					if (morePageAvailable())
						adapter.notifyMayHaveMorePages();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	class PaginationComposerAdapter extends AmazingAdapter
	{
		public PaginationComposerAdapter()
		{
		}

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
			{
			}
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

			if (listDownloader != null && listDownloader.getStatus() != AsyncTask.Status.FINISHED)
			{
				return;
			}

			listDownloader = new MyListDownloader();
			listDownloader.execute();
		}

		@Override
		public void bindSectionHeader(View view, int position, boolean displaySectionHeader)
		{
		}

		class ViewHolder
		{
			ImageView img;
			TextView txtDealDesc;
			TextView txtDealVal;
			TextView txtDealDisc;
			TextView txtDealPay;
			/*TextView txtEuroSymbol1;
			TextView txtEuroSymbol2;*/
			TextView txtPercentSymbol;

			TextView txtDealPayHeading;
			TextView txtDealValHeading;
			TextView txtDealDiscHeading;
			//TextView txtDealLocation;

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
				//txtDealLocation = (TextView) v.findViewById(R.id.txt_new_deal_location);
				

				Typeface font = GreatBuyzApplication.getApplication().getFont();
				txtDealVal.setTypeface(font);
				txtDealDisc.setTypeface(font);
				txtDealPay.setTypeface(font);

				txtDealValHeading.setTypeface(font);
				txtDealDiscHeading.setTypeface(font);
				txtDealPayHeading.setTypeface(font);

				txtDealDesc.setTypeface(font);

				/*txtEuroSymbol1 = (TextView) v.findViewById(R.id.txt_euro_symbol_1);
				txtEuroSymbol1.setTypeface(font);
				txtEuroSymbol2 = (TextView) v.findViewById(R.id.txt_euro_symbol_2);
				txtEuroSymbol2.setTypeface(font);*/
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
					if (holder == null)
						createNew = true;
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
				if (!Utils.isNothing(imgUrl)) 
					Picasso.with(activity).load(imgUrl).config(Bitmap.Config.RGB_565).resize(320, 180).centerCrop().into(holder.img);

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
				
				//Utils.setLocationToDealTextView(holder.txtDealLocation, item.get_dealLocation());
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
		{
		}

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
		if (gps == null)
			gps = new LocationTracker(getActivity(), this, MIN_TIME_BW_UPDATES);
		gps.getLocation();
		if (gps.canGetLocation() && gpsNeededViewSwitcher.getDisplayedChild() == 1)
			refreshFragment();
		// FlurryAgent.onPageView();
		//GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit("DealsNearMe");
		//Kiran // we are taking page name from message table for language support //
		String name = Utils.getMessageString(AppConstants.Messages.dealsNearMe, R.string.dealsNearMe);
		GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit(name);
	}

	public void onPause()
	{
		boolean isTaskRunning = false;
		if (listDownloader != null)
			isTaskRunning = listDownloader.getStatus() == AsyncTask.Status.FINISHED ? false : true;
		if (isTaskRunning)
		{
			try
			{
				listDownloader.cancel(true);
			}
			catch (Exception e)
			{
			}
			// adapter = null;
			// listDownloader = null;
			isTaskPending = true;
		}
		super.onPause();
	}

	public void onResume()
	{
		super.onResume();
		// if (isTaskPending)
		// refreshFragment();
		isTaskPending = false;
	}

	class MyListDownloader extends AsyncTask<Void, Void, ClipResponse>
	{
		@Override
		protected ClipResponse doInBackground(Void... params)
		{
			ClipResponse clipResponse = null;
			try
			{
				if (gps != null)
				{
//					latitude = gps.getLatitude();
//					longitude = gps.getLongitude();
					radius = AppConstants.DEALS_NEAR_ME_RADIUS;

					/*clipResponse = GreatBuyzApplication.getServiceDelegate().getDealsNearMe(AppConstants.JSONKeys.WAP, latitude, longitude,
							radius, GreatBuyzApplication.getApplication().getLimitForDealsNearMe(),
							GreatBuyzApplication.getApplication().getSkipIndexForDealsNearMe());*/
				}
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
						if (GreatBuyzApplication.getDataController() == null)
							return;
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
						if (GreatBuyzApplication.getDataController().getDealsNearMeDTO() == null
								|| GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().size() == 0)
						{
							viewSwitcher.setDisplayedChild(1);
						}
						else
						{
							if (adapter != null)
								adapter.notifyNoMorePages();
						}
						if (adapter != null)
							adapter.notifyDataSetChanged();
					}
				}
				else
				{
					if (GreatBuyzApplication.getDataController().getDealsNearMeDTO() == null
							|| GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().size() == 0)
					{
						if (emptyView != null)
						{
							String err = Utils
									.getMessageString(AppConstants.Messages.networkProblemMessage, R.string.networkProblemMessage);
							emptyView.setText(err);
						}
						viewSwitcher.setDisplayedChild(1);
					}
					else
					{
						if (adapter != null)
							adapter.notifyNoMorePages();
					}
					if (adapter != null)
						adapter.notifyDataSetChanged();
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
		if (lsComposer == null)
			return false;

		totalVisibleItems = lsComposer.getLastVisiblePosition() - lsComposer.getFirstVisiblePosition();
		boolean morePage = false;
		try
		{
			if (GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().size() >= totalVisibleItems
					&& currentCount >= GreatBuyzApplication.getApplication().getLimitForDealsNearMe())
				morePage = true;
		}
		catch (Exception e)
		{
		}

		return morePage;
	}

	public void stopDownloader()
	{
		boolean isTaskRunning = false;
		if (listDownloader != null)
			isTaskRunning = listDownloader.getStatus() == AsyncTask.Status.FINISHED ? false : true;
		if (isTaskRunning)
		{
			try
			{
				listDownloader.cancel(true);
			}
			catch (Exception e)
			{
			}
		}
	}

	public void refreshFragment()
	{
		stopDownloader();
		listDownloader = null;
		if (GreatBuyzApplication.getDataController().getDealsNearMeDTO() != null
				&& GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList() != null
				&& GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().size() > 0)
		{
			GreatBuyzApplication.getDataController().getDealsNearMeDTO().getDealsNearMeList().clear();
		}
		GreatBuyzApplication.getApplication().setSkipIndexForDealsNearMe(0);
		if (adapter != null)
			adapter.notifyDataSetChanged();
		adapter = null;

		viewSwitcher.setDisplayedChild(0);

		if (gps == null)
			gps = new LocationTracker(getActivity(), this, MIN_TIME_BW_UPDATES);

		gps.getLocation();
		if (gps.canGetLocation())
		{
			gpsNeededViewSwitcher.setDisplayedChild(0);
			if (locationTimeout != null)
			{
				locationTimeout.cancel(true);
				locationTimeout = null;
			}
			locationTimeout = new TimeOutAsyncTask(MAX_TIME_FOR_LOCATION, this);
			locationTimeout.start();
		}
		else
			gpsNeededViewSwitcher.setDisplayedChild(1);
	}

	@Override
	public void onLocationChanged(Location location)
	{
		if (location.hasAccuracy() && location.getAccuracy() <= MIN_ACCURACY_FOR_LOCATION)
		{
			if (gps != null)
				gps.stopUsingGPS();

			if (locationTimeout != null)
			{
				locationTimeout.cancel(true);
				locationTimeout = null;
			}
			
			latitude = location.getLatitude();
			longitude = location.getLongitude();

			listDownloader = new MyListDownloader();
			listDownloader.execute();
		}
	}

	@Override
	public void onProviderDisabled(String provider)
	{
	}

	@Override
	public void onProviderEnabled(String provider)
	{
		if (gpsNeededViewSwitcher.getDisplayedChild() == 1)
		{
			gpsNeededViewSwitcher.setDisplayedChild(0);

			if (gps == null)
				gps = new LocationTracker(getActivity(), this, MIN_TIME_BW_UPDATES);
			gps.getLocation();
			if (locationTimeout != null)
			{
				locationTimeout.cancel(true);
				locationTimeout = null;
			}
			locationTimeout = new TimeOutAsyncTask(MAX_TIME_FOR_LOCATION, this);
			locationTimeout.start();
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
	}

	@Override
	public void onTimeout()
	{
		if (emptyView != null)
			emptyView.setText(locationNotAvailableMessage);

		if (viewSwitcher != null)
			viewSwitcher.setDisplayedChild(1);

		if (listDownloader != null)
			listDownloader.cancel(false);

	}
}
