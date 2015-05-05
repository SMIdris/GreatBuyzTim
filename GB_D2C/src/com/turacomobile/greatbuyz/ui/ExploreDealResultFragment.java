package com.turacomobile.greatbuyz.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;
import com.turacomobile.greatbuyz.GreatBuyzApplication;
import com.turacomobile.greatbuyz.R;
import com.turacomobile.greatbuyz.data.DealScreenDTO;
import com.turacomobile.greatbuyz.service.ClipResponse;
import com.turacomobile.greatbuyz.utils.AppConstants;
import com.turacomobile.greatbuyz.utils.GreatBuyzTextView;
import com.turacomobile.greatbuyz.utils.Utils;

public class ExploreDealResultFragment extends Fragment
{
	OnDealItemClick		   onDealItemClick;
	TextView				  emptyView;
	ViewSwitcher			  viewSwitcher;
	AmazingListView		   lsComposer;
	GreatBuyzTextView			  dealSearch;
	GreatBuyzTextView			  couponSearch;
	GreatBuyzTextView			  couponDealSearch;
	PaginationComposerAdapter adapter;
	boolean				   isTaskPending	 = false;
	public boolean			isCouponClicked   = false;
	String					keyword;
	String					selectedCategory;
	String					city;
	String					locality;
	static Activity		   activity;
	int					   totalVisibleItems = 0;
	int					   currentCount	  = 0;
	
	public static ExploreDealResultFragment newInstance(Activity screen, String keyword, String selectedCategory, String city, String locality, OnDealItemClick dealItemClick)
	{
		ExploreDealResultFragment fragment = new ExploreDealResultFragment();
		activity = screen;
		fragment.onDealItemClick = dealItemClick;
		fragment.keyword = keyword;
		fragment.selectedCategory = selectedCategory;
		fragment.city = city;
		fragment.locality = locality;
		GreatBuyzApplication.getApplication().setSkipIndexForExploreDeals(0);
		try
		{
			GreatBuyzApplication.getDataController().getExploreDeals().deleteAll();
		}
		catch (NullPointerException e)
		{}
		return fragment;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		activity = getActivity();
		if (GreatBuyzApplication.getDataController().getExploreDeals() != null && GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList().size() > 0)
		{
			initialiseList();
		}
		else
		{
			listDownloader = new MyListDownloader();
			listDownloader.execute();
		}
		// lsComposer.setOnItemClickListener(new OnItemClickListener()
		// {
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		// {
		// if (arg2 < GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList().size()) Utils.startDetailsScreenNew(getActivity(), arg2, AppConstants.FramentConstants.EXPLORE_DEALS);
		// }
		// });
		// lsComposer.setLoadingView(activity.getLayoutInflater().inflate(R.layout.loading_view,
		// null));
		lsComposer.setLoadingView(getActivity().getLayoutInflater().inflate(R.layout.loading_view, null));
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
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	MyListDownloader listDownloader;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.listfragmentcoupon, null);
		lsComposer = (AmazingListView) v.findViewById(R.id.lsComposer);
		ViewSwitcher parentViewSwitcher = (ViewSwitcher) v.findViewById(R.id.gpsNeededViewSwitcher);
		parentViewSwitcher.setDisplayedChild(0);
		viewSwitcher = (ViewSwitcher) v.findViewById(android.R.id.empty);
		TextView backButtonText = (TextView) v.findViewById(R.id.emptyBackButtonText);
		backButtonText.setVisibility(View.VISIBLE);
		backButtonText.setTypeface(GreatBuyzApplication.getApplication().getFont());
		String err = Utils.getMessageString(AppConstants.Messages.emptyViewBackButton, R.string.emptyViewBackButton);
		backButtonText.setText(err);
		viewSwitcher.setDisplayedChild(0);
		emptyView = (TextView) v.findViewById(R.id.emptyText);
		emptyView.setTypeface(GreatBuyzApplication.getApplication().getFont());
		String msg = Utils.getMessageString(AppConstants.Messages.emptyDeal, R.string.emptyDeal);
		emptyView.setText(msg);
		lsComposer.setEmptyView(viewSwitcher);
		// couponsearch
		dealSearch = (GreatBuyzTextView) v.findViewById(R.id.dealsearch);
		couponSearch = (GreatBuyzTextView) v.findViewById(R.id.couponsearch);
		couponDealSearch = (GreatBuyzTextView) v.findViewById(R.id.coupondealsearch);
		final View couponBorder = (View) v.findViewById(R.id.bordercoupon);
		final View dealBorder = (View) v.findViewById(R.id.borderdeal);
		// final View searchBorder = (View) v.findViewById(R.id.bordersearch);
		//
		// GradientDrawable gd = new GradientDrawable();
		// gd.setColor(0xFF00FF00); // Changes this drawbale to use a single color instead of a gradient
		// gd.setCornerRadius(5);
		// gd.setStroke(1, 0xFF000000);
		// TextView tv = (TextView)findViewById(R.id.textView1);
		// dealSearch.setBackgroundResource(R.anim.border_bottom);
	//	dealBorder.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 12));
		dealSearch.setBackgroundResource(R.anim.border_bottom);
		couponSearch.setBackgroundResource(Color.TRANSPARENT);
		//couponDealSearch.setBackgroundResource(Color.TRANSPARENT);
		dealSearch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
//				dealBorder.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 12));
//				// dealBorder.setBackgroundColor(Color.BLACK);
//				couponBorder.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0));
				dealSearch.setBackgroundResource(R.anim.border_bottom);
				couponSearch.setBackgroundResource(Color.TRANSPARENT);
				//couponDealSearch.setBackgroundResource(Color.TRANSPARENT);
				isCouponClicked = false;
				refreshFragment();
			}
		});
		couponSearch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
//				couponBorder.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 12));
//				dealBorder.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 0));
				dealSearch.setBackgroundResource(Color.TRANSPARENT);
				couponSearch.setBackgroundResource(R.anim.border_bottom);
				//couponDealSearch.setBackgroundResource(Color.TRANSPARENT);
				isCouponClicked = true;
				refreshFragment();
			}
		});
		couponDealSearch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				isCouponClicked = false;
				getActivity().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
			}
		});
		//
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
					e.printStackTrace();
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
			GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList().clear();
		}
		
		@Override
		public int getCount()
		{
			try
			{
				if (morePageAvailable())
					return GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList().size() + 1;
				else
					return GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList().size();
			}
			catch (Exception e)
			{}
			return 0;
		}
		
		@Override
		public DealScreenDTO getItem(int position)
		{
			return GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList().get(position);
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
			ImageView	  img;
			TextView	   txtDealDesc;
			TextView	   txtDealVal;
			TextView	   txtDealDisc;
			TextView	   txtDealPay;
			/*
			 * TextView txtEuroSymbol1; TextView txtEuroSymbol2;
			 */
			TextView	   txtPercentSymbol;
			TextView	   txtDealPayHeading;
			TextView	   txtDealValHeading;
			TextView	   txtDealDiscHeading;
			TextView	   txtDealLocation;
			LinearLayout   layoutDealProContainer;
			RelativeLayout list_entry_base_new;
			
			public ViewHolder(View v)
			{
				img = (ImageView) v.findViewById(R.id.img_deal_new);
				txtDealDesc = (TextView) v.findViewById(R.id.txt_new_deal_desc);
				txtDealVal = (TextView) v.findViewById(R.id.txt_deal_value);
				txtDealDisc = (TextView) v.findViewById(R.id.txt_deal_desc_val);
				txtDealPay = (TextView) v.findViewById(R.id.txt_deal_pay_val);
				txtDealLocation = (TextView) v.findViewById(R.id.txt_new_deal_location);
				txtDealPayHeading = (TextView) v.findViewById(R.id.txt_deal_pay_text);
				txtDealValHeading = (TextView) v.findViewById(R.id.txt_deal_value_text);
				txtDealDiscHeading = (TextView) v.findViewById(R.id.txt_deal_desc_text);
				layoutDealProContainer = (LinearLayout) v.findViewById(R.id.layoutDealProContainer);
				list_entry_base_new = (RelativeLayout) v.findViewById(R.id.list_entry_base_new);
				Typeface font = GreatBuyzApplication.getApplication().getFont();
				txtDealVal.setTypeface(font);
				txtDealDisc.setTypeface(font);
				txtDealPay.setTypeface(font);
				txtDealValHeading.setTypeface(font);
				txtDealDiscHeading.setTypeface(font);
				txtDealPayHeading.setTypeface(font);
				txtDealDesc.setTypeface(font);
				/*
				 * txtEuroSymbol1 = (TextView) v.findViewById(R.id.txt_euro_symbol_1); txtEuroSymbol1.setTypeface(font); txtEuroSymbol2 = (TextView) v.findViewById(R.id.txt_euro_symbol_2); txtEuroSymbol2.setTypeface(font);
				 */
				txtPercentSymbol = (TextView) v.findViewById(R.id.txt_percent_symbol);
				txtPercentSymbol.setTypeface(font);
			}
		}
		
		@Override
		public View getAmazingView(final int position, View convertView, ViewGroup parent)
		{
			View row = null;
			ViewHolder holder = null;
			System.out.println("size of list " + GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList().size());
			if (position < GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList().size())
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
					if (Utils.useExpandedImageDealLayout() && !isCouponClicked)
						// row = (View)
						// activity.getLayoutInflater().inflate(R.layout.deallistitemtim,
						// null);
						row = (View) getActivity().getLayoutInflater().inflate(R.layout.deallistitemimageexpand, null);
					else
						// row = (View)
						// activity.getLayoutInflater().inflate(R.layout.deallistitem,
						// null);
						row = (View) getActivity().getLayoutInflater().inflate(R.layout.deallistitem_coupon, null);
					holder = new ViewHolder(row);
					row.setTag(holder);
				}
				if (isCouponClicked)
				{
					holder.layoutDealProContainer.setVisibility(View.GONE);
				}
				holder.img.setImageResource(R.drawable.default_category);
				String imgUrl = item.getImage();
				holder.img.setTag(imgUrl);
				if (!Utils.isNothing(imgUrl)) Picasso.with(activity).load(imgUrl).config(Bitmap.Config.RGB_565).resize(320, 180).centerCrop().placeholder(R.drawable.default_category).error(R.drawable.default_category).into(holder.img);
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
				Utils.setLocationToDealTextView(holder.txtDealLocation, item.get_dealLocation());
				holder.list_entry_base_new.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						// holder.layoutDealProContainer.setVisibility(View.GONE);
						if (isCouponClicked)
						{
							if (((ViewGroup) v).getChildAt(3).getVisibility() == View.VISIBLE && position < GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList().size()) Utils.startDetailsScreenNew(getActivity(), position, AppConstants.FramentConstants.EXPLORE_DEALS);
							((ViewGroup) v).getChildAt(3).setVisibility(View.VISIBLE);
						}
						else
						{
							if (position < GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList().size()) Utils.startDetailsScreenNew(getActivity(), position, AppConstants.FramentConstants.EXPLORE_DEALS);
						}
					}
				});
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
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		// FlurryAgent.onPageView();
		GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit("ExploreDealsResult");
	}
	
	public void onPause()
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
			try
			{
				ClipResponse clipResponse = GreatBuyzApplication.getServiceDelegate().getExploreDeals(city, locality, selectedCategory, keyword, GreatBuyzApplication.getApplication().getLimitForExploreDeals(), GreatBuyzApplication.getApplication().getSkipIndexForExploreDeals());
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
						if (GreatBuyzApplication.getDataController() == null) return;
						GreatBuyzApplication.getDataController().exploreDealsReceived(result.getListData());
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
						if (GreatBuyzApplication.getDataController().getExploreDeals() == null || GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList().size() == 0)
						{
							viewSwitcher.setDisplayedChild(1);
							Bundle b = new Bundle();
							b.putString(AppConstants.JSONKeys.MESSAGE, getString(R.string.failed_search_dialog_message));
							showOtherDialog(AppConstants.DialogConstants.FAILED_SEARCH_DIALOG, b);
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
					if (GreatBuyzApplication.getDataController().getExploreDeals() == null || GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList().size() == 0)
					{
						String err = Utils.getMessageString(AppConstants.Messages.networkProblemMessage, R.string.networkProblemMessage);
						if (emptyView != null)
						{
							emptyView.setText(err);
						}
						viewSwitcher.setDisplayedChild(1);
						Bundle b = new Bundle();
						b.putString(AppConstants.JSONKeys.MESSAGE, err);
						showOtherDialog(AppConstants.DialogConstants.FAILED_SEARCH_DIALOG, b);
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
			if (GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList().size() >= totalVisibleItems && currentCount >= GreatBuyzApplication.getApplication().getLimitForExploreDeals()) morePage = true;
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
		if (GreatBuyzApplication.getDataController().getExploreDeals() != null && GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList() != null && GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList().size() > 0)
		{
			GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList().clear();
		}
		GreatBuyzApplication.getApplication().setSkipIndexForExploreDeals(0);
		if (adapter != null) adapter.notifyDataSetChanged();
		adapter = null;
		listDownloader = new MyListDownloader();
		viewSwitcher.setDisplayedChild(0);
		listDownloader.execute();
	}
	
	private void showOtherDialog(final int which, final Bundle b)
	{
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					getActivity().showDialog(which, b);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
}
