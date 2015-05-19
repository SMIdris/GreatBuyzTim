package com.turacomobile.greatbuyz.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;
import com.turacomobile.greatbuyz.GreatBuyzApplication;
import com.turacomobile.greatbuyz.R;
import com.turacomobile.greatbuyz.data.CouponScreenDTO;
import com.turacomobile.greatbuyz.service.CouponClipResponse;
import com.turacomobile.greatbuyz.utils.AppConstants;
import com.turacomobile.greatbuyz.utils.Utils;

public class ExploreCouponResultFragment extends Fragment
{
	OnDealItemClick		   onDealItemClick;
	TextView				  emptyView;
	ViewSwitcher			  viewSwitcher;
	AmazingListView		   lsComposer;
	PaginationComposerAdapter adapter;
	boolean				   isTaskPending	 = false;
	String					keyword;
	String					selectedCategory;
	String					city;
	String					locality;
	static Activity		   activity;
	int					   totalVisibleItems = 0;
	int					   currentCount	  = 0;
	
	public static ExploreCouponResultFragment newInstance(Activity screen, String keyword, String selectedCategory, String city, String locality, OnDealItemClick dealItemClick)
	{
		ExploreCouponResultFragment fragment = new ExploreCouponResultFragment();
		activity = screen;
		fragment.onDealItemClick = dealItemClick;
		fragment.keyword = keyword;
		fragment.selectedCategory = selectedCategory;
		fragment.city = city;
		fragment.locality = locality;
		// GreatBuyzApplication.getApplication().setSkipIndexForExploreDeals(0);
		// try
		// {
		// GreatBuyzApplication.getDataController().getExploreDeals().deleteAll();
		// }
		// catch (NullPointerException e)
		// {
		// }
		return fragment;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		activity = getActivity();
		if (GreatBuyzApplication.getDataController().getExclusiveCouponsDTO() != null && GreatBuyzApplication.getDataController().getExclusiveCouponsDTO().getExclusiveCouponsList().size() > 0)
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
		//
		// // for(int i=0;i<((ViewGroup) arg1).getChildCount();i++){
		// // View child=((ViewGroup) arg1).getChildAt(i);
		// // }
		// // if (((ViewGroup) arg1).getChildAt(3).getVisibility() == View.VISIBLE && arg2 < GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList().size())
		// // Utils.startDetailsScreenNew(getActivity(), arg2, AppConstants.FramentConstants.EXPLORE_DEALS);
		// // ((ViewGroup) arg1).getChildAt(3).setVisibility(View.VISIBLE);
		//
		// }
		//
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
		View v = inflater.inflate(R.layout.listfragment, null);
		lsComposer = (AmazingListView) v.findViewById(R.id.lsComposer);
		ViewSwitcher parentViewSwitcher = (ViewSwitcher) v.findViewById(R.id.gpsNeededViewSwitcher);
		parentViewSwitcher.setDisplayedChild(0);
		viewSwitcher = (ViewSwitcher) v.findViewById(android.R.id.empty);
		TextView backButtonText = (TextView) v.findViewById(R.id.emptyBackButtonText);
		backButtonText.setVisibility(View.GONE);
		// backButtonText.setTypeface(GreatBuyzApplication.getApplication().getFont());
		// String err = Utils.getMessageString(AppConstants.Messages.emptyViewBackButton, R.string.emptyViewBackButton);
		// backButtonText.setText(err);
		viewSwitcher.setDisplayedChild(0);
		emptyView = (TextView) v.findViewById(R.id.emptyText);
		emptyView.setTypeface(GreatBuyzApplication.getApplication().getFont());
		String msg = Utils.getMessageString(AppConstants.Messages.emptyMyCouponListMessage, R.string.emptyMyCouponListMessage);
		emptyView.setText(msg);
		//
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) lsComposer.getLayoutParams();
		params.topMargin = 3;
		lsComposer.setLayoutParams(params);
		lsComposer.setEmptyView(viewSwitcher);
		lsComposer.setDividerHeight(10);
		//
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
			GreatBuyzApplication.getDataController().getExclusiveCouponsDTO().getExclusiveCouponsList().clear();
		}
		
		@Override
		public int getCount()
		{
			try
			{
				if (morePageAvailable())
					return GreatBuyzApplication.getDataController().getExclusiveCouponsDTO().getExclusiveCouponsList().size() + 1;
				else
					return GreatBuyzApplication.getDataController().getExclusiveCouponsDTO().getExclusiveCouponsList().size();
			}
			catch (Exception e)
			{}
			return 0;
		}
		
		@Override
		public CouponScreenDTO getItem(int position)
		{
			return GreatBuyzApplication.getDataController().getExclusiveCouponsDTO().getExclusiveCouponsList().get(position);
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
			Button	   coupon_buy;
			
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
				coupon_buy = (Button)v.findViewById(R.id.coupon_buy);
				Typeface font = GreatBuyzApplication.getApplication().getFont();
				txtDealVal.setTypeface(font);
				txtDealDisc.setTypeface(font);
				txtDealPay.setTypeface(font);
				txtDealValHeading.setTypeface(font);
				txtDealDiscHeading.setTypeface(font);
				txtDealPayHeading.setTypeface(font);
				txtDealDesc.setTypeface(font);
				coupon_buy.setTypeface(font);
				Utils.setMessageToButton(coupon_buy, Utils.getMessageString(AppConstants.Messages.btnCouponVisitText, R.string.btnVisitCoupon));

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
			// System.out.println("size of list "+GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList().size());
			if (position < GreatBuyzApplication.getDataController().getExclusiveCouponsDTO().getExclusiveCouponsList().size())
			{
				row = convertView;
				final CouponScreenDTO item = getItem(position);
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
						row = (View) getActivity().getLayoutInflater().inflate(R.layout.deallistitem_coupon, null);
					else
						// row = (View)
						// activity.getLayoutInflater().inflate(R.layout.deallistitem,
						// null);
						row = (View) getActivity().getLayoutInflater().inflate(R.layout.deallistitem_coupon, null);
					holder = new ViewHolder(row);
					row.setTag(holder);
				}
				holder.layoutDealProContainer.setVisibility(View.GONE);
				holder.list_entry_base_new.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						// holder.layoutDealProContainer.setVisibility(View.GONE);
						if (((ViewGroup) v).getChildAt(3).getVisibility() == View.VISIBLE && position < GreatBuyzApplication.getDataController().getExclusiveCouponsDTO().getExclusiveCouponsList().size()) Utils.startCouponDetailsScreenNew(getActivity(), position, AppConstants.FramentConstants.EXCLUSIVE_COUPONS);
						((ViewGroup) v).getChildAt(3).setVisibility(View.VISIBLE);
					}
				});
				holder.coupon_buy.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						Bundle b = new Bundle();
						b.putString("message", "");
						b.putString(AppConstants.JSONKeys.DEAL_ID, item.getDealId());
						b.putString(AppConstants.JSONKeys.LOCATIONS, item.getDetails());
						b.putString(AppConstants.JSONKeys.MDN, "");
						// b.putString(AppConstants.JSONKeys.NAME, "DealDetail");
						b.putString(AppConstants.JSONKeys.URL, item.getDealVisitUri());
						b.putString(AppConstants.JSONKeys.COUPON, item.getCouponCode());
						showOtherDialog(AppConstants.DialogConstants.BUY_CONFIRMATION_DIALOG, b);
					}
				});
				// holder.layoutDealProContainer.setOnClickListener(new OnClickListener()
				// {
				// @Override
				// public void onClick(View v)
				// {
				// //holder.layoutDealProContainer.setVisibility(View.GONE);
				// // if ( position < GreatBuyzApplication.getDataController().getExploreDeals().getExploreDealsList().size())
				// // Utils.startDetailsScreenNew(getActivity(), position, AppConstants.FramentConstants.EXPLORE_DEALS);
				//
				// }
				// });
				// holder.img.setImageResource(R.drawable.default_category);
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
	
	class MyListDownloader extends AsyncTask<Void, Void, CouponClipResponse>
	{
		@Override
		protected CouponClipResponse doInBackground(Void... params)
		{
			try
			{
				CouponClipResponse clipResponse = GreatBuyzApplication.getServiceDelegate().getExclusiveCoupons(AppConstants.JSONKeys.WAP, GreatBuyzApplication.getApplication().getLimitForExclusiveCoupons(), GreatBuyzApplication.getApplication().getSkipIndexForExclusiveCoupons());
				return clipResponse;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(CouponClipResponse result)
		{
			try
			{
				if (result != null && result.getResponceCode() == 200)
				{
					if (result.getListData() != null && result.getListData().size() > 0)
					{
						if (GreatBuyzApplication.getDataController() == null) return;
						GreatBuyzApplication.getDataController().exclusiveCouponsReceived(result.getListData());
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
						if (GreatBuyzApplication.getDataController().getExclusiveCouponsDTO() == null || GreatBuyzApplication.getDataController().getExclusiveCouponsDTO().getExclusiveCouponsList().size() == 0)
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
					if (GreatBuyzApplication.getDataController().getExclusiveCouponsDTO() == null || GreatBuyzApplication.getDataController().getExclusiveCouponsDTO().getExclusiveCouponsList().size() == 0)
					{
						if (emptyView != null)
						{
							String err = Utils.getMessageString(AppConstants.Messages.emptyMyCouponListMessage, R.string.emptyMyCouponListMessage);
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
			if (GreatBuyzApplication.getDataController().getExclusiveCouponsDTO().getExclusiveCouponsList().size() >= totalVisibleItems && currentCount >= GreatBuyzApplication.getApplication().getLimitForExclusiveCoupons()) morePage = true;
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
		if (GreatBuyzApplication.getDataController().getExclusiveCouponsDTO() != null && GreatBuyzApplication.getDataController().getExclusiveCouponsDTO().getExclusiveCouponsList() != null && GreatBuyzApplication.getDataController().getExclusiveCouponsDTO().getExclusiveCouponsList().size() > 0)
		{
			GreatBuyzApplication.getDataController().getExclusiveCouponsDTO().getExclusiveCouponsList().clear();
		}
		GreatBuyzApplication.getApplication().setSkipIndexForExclusiveCoupons(0);
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
	// public static Fragment newInstance(String string, SampleTabsStyled sampleTabsStyled, SampleTabsStyled sampleTabsStyled2)
	// {
	// // TODO Auto-generated method stub
	// return null;
	// }
}
