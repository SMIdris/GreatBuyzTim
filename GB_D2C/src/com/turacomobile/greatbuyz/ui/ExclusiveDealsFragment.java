package com.turacomobile.greatbuyz.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;
import com.turacomobile.greatbuyz.GreatBuyzApplication;
import com.turacomobile.greatbuyz.R;
import com.turacomobile.greatbuyz.data.DealScreenDTO;
import com.turacomobile.greatbuyz.service.ClipResponse;
import com.turacomobile.greatbuyz.utils.AppConstants;
import com.turacomobile.greatbuyz.utils.Utils;

public final class ExclusiveDealsFragment extends Fragment
{
	OnDealItemClick onDealItemClick;

	TextView emptyView;
	ViewSwitcher viewSwitcher;
	AmazingListView lsComposer;
	PaginationComposerAdapter adapter;
	boolean isTaskPending = false;
	static Activity activity;
	int totalVisibleItems = 0;
	int currentCount = 0;

	public static ExclusiveDealsFragment newInstance(String content, Activity screen, OnDealItemClick dealItemClick)
	{
		// ////System.out.println("GreatBuyz: function IN ExclusiveDealsFragment > ExclusiveDealsFragment");
		ExclusiveDealsFragment fragment = new ExclusiveDealsFragment();

		fragment.onDealItemClick = dealItemClick;
		activity = screen;
		// ////System.out.println("GreatBuyz: function OUT ExclusiveDealsFragment > ExclusiveDealsFragment");
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// Log.v("Time test : Exclusive deals screen create start :", "" +
		// Utils.getCurrentTimeStamp());
		// ////System.out.println("GreatBuyz: function IN ExclusiveDealsFragment > onActivityCreated");
		super.onActivityCreated(savedInstanceState);

		activity = getActivity();

		if (GreatBuyzApplication.getDataController().getExclusiveDealsDTO() != null
				&& GreatBuyzApplication.getDataController().getExclusiveDealsDTO().getExclusiveDealsList().size() > 0)
		{
			initialiseList();
		}
		else
		{
			listDownloader = new MyListDownloader();
			listDownloader.execute();
		}
		// lsComposer.setLoadingView(activity.getLayoutInflater().inflate(R.layout.loading_view,
		// null));
		lsComposer.setLoadingView(getActivity().getLayoutInflater().inflate(R.layout.loading_view, null));
		lsComposer.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				if (arg2 < GreatBuyzApplication.getDataController().getExclusiveDealsDTO().getExclusiveDealsList().size())
					Utils.startDetailsScreenNew(getActivity(), arg2, AppConstants.FramentConstants.EXCLUSIVE_DEALS);
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

		// ////System.out.println("GreatBuyz: function OUT ExclusiveDealsFragment > onActivityCreated");
		// Log.v("Time test : Exclusive deals screen create end :", "" +
		// Utils.getCurrentTimeStamp());
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// ////System.out.println("GreatBuyz: function IN ExclusiveDealsFragment > onCreate");
		super.onCreate(savedInstanceState);
		// ////System.out.println("GreatBuyz: function OUT ExclusiveDealsFragment > onCreate");
	}

	public MyListDownloader listDownloader;

	public MyListDownloader getListDownloader()
	{
		// ////System.out.println("GreatBuyz: function IN-OUT ExclusiveDealsFragment > getListDownloader");
		return listDownloader;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// Log.v("Time test : Exclusive deals screen view create start :", "" +
		// Utils.getCurrentTimeStamp());
		// ////System.out.println("GreatBuyz: function IN ExclusiveDealsFragment > onCreateView");
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.listfragment, null);
		lsComposer = (AmazingListView) v.findViewById(R.id.lsComposer);
		ViewSwitcher parentViewSwitcher = (ViewSwitcher) v.findViewById(R.id.gpsNeededViewSwitcher);
		parentViewSwitcher.setDisplayedChild(0);
		viewSwitcher = (ViewSwitcher) v.findViewById(android.R.id.empty);
		viewSwitcher.setDisplayedChild(0);
		emptyView = (TextView) v.findViewById(R.id.emptyText);
		emptyView.setTypeface(GreatBuyzApplication.getApplication().getFont());

		String msg = Utils.getMessageString(AppConstants.Messages.emptyDeal, R.string.emptyDeal);
		emptyView.setText(msg);

		lsComposer.setEmptyView(viewSwitcher);
		// ////System.out.println("GreatBuyz: function OUT ExclusiveDealsFragment > onCreateView");
		// Log.v("Time test : Exclusive deals view create end :", "" +
		// Utils.getCurrentTimeStamp());
		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		// ////System.out.println("GreatBuyz: function IN-OUT ExclusiveDealsFragment > onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}

	public void initialiseList()
	{
		// ////System.out.println("GreatBuyz: function IN ExclusiveDealsFragment > initialiseList");
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
		// ////System.out.println("GreatBuyz: function OUT ExclusiveDealsFragment > initialiseList");
	}

	class PaginationComposerAdapter extends AmazingAdapter
	{
		public PaginationComposerAdapter()
		{
		}

		public void reset()
		{
			viewSwitcher.setDisplayedChild(1);
			GreatBuyzApplication.getDataController().getExclusiveDealsDTO().getExclusiveDealsList().clear();
		}

		@Override
		public int getCount()
		{
			try
			{
				if (morePageAvailable())
					return GreatBuyzApplication.getDataController().getExclusiveDealsDTO().getExclusiveDealsList().size() + 1;
				else
					return GreatBuyzApplication.getDataController().getExclusiveDealsDTO().getExclusiveDealsList().size();
			}
			catch (Exception e)
			{
			}
			return 0;
		}

		@Override
		public DealScreenDTO getItem(int position)
		{
			return GreatBuyzApplication.getDataController().getExclusiveDealsDTO().getExclusiveDealsList().get(position);
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

			// check if list downloader already running then return

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
			TextView txtEuroSymbol1;
			TextView txtEuroSymbol2;
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
				//txtDealLocation = (TextView) v.findViewById(R.id.txt_new_deal_location);
				

				txtDealPayHeading = (TextView) v.findViewById(R.id.txt_deal_pay_text);
				txtDealValHeading = (TextView) v.findViewById(R.id.txt_deal_value_text);
				txtDealDiscHeading = (TextView) v.findViewById(R.id.txt_deal_desc_text);
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

			if (position < GreatBuyzApplication.getDataController().getExclusiveDealsDTO().getExclusiveDealsList().size())
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
						// activity.getLayoutInflater().inflate(R.layout.exclusivedeallistitemtim,
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
				ImageView imgExclusive = (ImageView) row.findViewById(R.id.exclusive);
				if (item.isExclusiveDeal())
				{
					imgExclusive.setVisibility(View.VISIBLE);
				}
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
		// ////System.out.println("GreatBuyz: function IN-OUT ExclusiveDealsFragment > onDestroy");
		super.onDestroy();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		// FlurryAgent.onPageView();
		//GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit("ExclusiveDeals");
		// we are taking page name from message table for language support
		String name = Utils.getMessageString(AppConstants.Messages.dealsOfTheDay, R.string.dealsOfTheDay);
		GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit(name);
		
	}

	public void onPause()
	{
		// ////System.out.println("GreatBuyz: function IN ExclusiveDealsFragment > onPause");
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
		// ////System.out.println("GreatBuyz: function OUT ExclusiveDealsFragment > onPause");
	}

	public void onResume()
	{
		// ////System.out.println("GreatBuyz: function IN ExclusiveDealsFragment > onResume");
		super.onResume();
		// if (isTaskPending)
		// refreshFragment();
		isTaskPending = false;
		// ////System.out.println("GreatBuyz: function OUT ExclusiveDealsFragment > onResume");
	}

	class MyListDownloader extends AsyncTask<Void, Void, ClipResponse>
	{
		@Override
		protected ClipResponse doInBackground(Void... params)
		{
			try
			{
				// ////System.out.println("GreatBuyz: function IN ExclusiveDealsFragment > ClipResponse");
				ClipResponse clipResponse = GreatBuyzApplication.getServiceDelegate().getExclusiveDeals(AppConstants.JSONKeys.WAP,
						GreatBuyzApplication.getApplication().getLimitForExclusiveDeals(),
						GreatBuyzApplication.getApplication().getSkipIndexForExclusiveDeals());

				// ////System.out.println("GreatBuyz: function OUT ExclusiveDealsFragment > ClipResponse");
				return clipResponse;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
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
						GreatBuyzApplication.getDataController().exclusiveDealsReceived(result.getListData());
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
						if (GreatBuyzApplication.getDataController().getExclusiveDealsDTO() == null
								|| GreatBuyzApplication.getDataController().getExclusiveDealsDTO().getExclusiveDealsList().size() == 0)
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
					if (GreatBuyzApplication.getDataController().getExclusiveDealsDTO() == null
							|| GreatBuyzApplication.getDataController().getExclusiveDealsDTO().getExclusiveDealsList().size() == 0)
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
			if (GreatBuyzApplication.getDataController().getExclusiveDealsDTO().getExclusiveDealsList().size() >= totalVisibleItems
					&& currentCount >= GreatBuyzApplication.getApplication().getLimitForExclusiveDeals())
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
		if (GreatBuyzApplication.getDataController().getExclusiveDealsDTO() != null
				&& GreatBuyzApplication.getDataController().getExclusiveDealsDTO().getExclusiveDealsList() != null
				&& GreatBuyzApplication.getDataController().getExclusiveDealsDTO().getExclusiveDealsList().size() > 0)
		{
			GreatBuyzApplication.getDataController().getExclusiveDealsDTO().getExclusiveDealsList().clear();
		}
		GreatBuyzApplication.getApplication().setSkipIndexForExclusiveDeals(0);
		if (adapter != null)
			adapter.notifyDataSetChanged();
		adapter = null;
		listDownloader = new MyListDownloader();
		viewSwitcher.setDisplayedChild(0);
		listDownloader.execute();
	}
}
