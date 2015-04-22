package it.telecomitalia.timcoupon.ui;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import it.telecomitalia.timcoupon.R;
import it.telecomitalia.timcoupon.data.DealScreenDTO;
import it.telecomitalia.timcoupon.framework.L;
import it.telecomitalia.timcoupon.service.ClipResponse;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.Resources.NotFoundException;
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

import com.onmobile.utils.AppConstants;
import com.onmobile.utils.Utils;
import com.squareup.picasso.Picasso;

public final class DealsByCategoryFragment extends Fragment
{
	FirstPageFragmentListener fpListener;
	PaginationComposerAdapter adapter;
	String					selectedCategory  = null;
	boolean				   isTaskPending	 = false;
	TextView				  emptyView;
	ViewSwitcher			  viewSwitcher;
	boolean				   callMore		  = true;
	AmazingListView		   lsComposer;
	static Activity		   activity;
	int					   totalVisibleItems = 0;
	int					   currentCount	  = 0;
	
	public static DealsByCategoryFragment newInstance(String content, Activity screen, FirstPageFragmentListener fpListener)
	{
		DealsByCategoryFragment fragment = new DealsByCategoryFragment();
		fragment.fpListener = fpListener;
		fragment.selectedCategory = content;
		activity = screen;
		try
		{
			GreatBuyzApplication.getDataController().getDealsByCategoriesDTO().deleteAll();
		}
		catch (NullPointerException e)
		{}
		return fragment;
	}
	
	public void cancelDownload()
	{
		try
		{
			getActivity().runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						if (adapter != null) adapter.reset();
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
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		// FlurryAgent.onPageView();
		try
		{
			GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit("DealsByCategory");
		}
		catch (Exception e)
		{
			if (GreatBuyzApplication.isDebug) L.i("\nStack Trace: " + Thread.currentThread().getStackTrace()[2] + "\nMessage Exception : " + e.getMessage());
		}
	}
	
	public void onDestroy()
	{
		super.onDestroy();
		// ////System.out.println("onDestroy");
	}
	
	public void onPause()
	{
		super.onPause();
		try
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
		}
		catch (Exception e)
		{
			if (GreatBuyzApplication.isDebug) L.i("\nStack Trace: " + Thread.currentThread().getStackTrace()[2] + "\nMessage Exception : " + e.getMessage());
		}
	}
	
	public void onResume()
	{
		super.onResume();
		// if (isTaskPending)
		// refreshFragment();
		isTaskPending = false;
	}
	
	MyListDownloader listDownloader;
	
	public void initialiseList()
	{
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					adapter = new PaginationComposerAdapter();
					lsComposer.setAdapter(adapter);
					totalVisibleItems = lsComposer.getLastVisiblePosition() - lsComposer.getFirstVisiblePosition();
					adapter.notifyDataSetChanged();
					if (morePageAvailable()) adapter.notifyMayHaveMorePages();
				}
				catch (Exception e)
				{
					if (GreatBuyzApplication.isDebug) L.i("\nStack Trace: " + Thread.currentThread().getStackTrace()[2] + "\nMessage Exception : " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.listfragment, null);
		try
		{
			lsComposer = (AmazingListView) v.findViewById(R.id.lsComposer);
			ViewSwitcher parentViewSwitcher = (ViewSwitcher) v.findViewById(R.id.gpsNeededViewSwitcher);
			parentViewSwitcher.setDisplayedChild(0);
			viewSwitcher = (ViewSwitcher) v.findViewById(android.R.id.empty);
			TextView backButtonText = (TextView) v.findViewById(R.id.emptyBackButtonText);
			backButtonText.setTypeface(GreatBuyzApplication.getApplication().getFont());
			String err = Utils.getMessageString(AppConstants.Messages.emptyViewBackButton, R.string.emptyViewBackButton);
			backButtonText.setText(err);
			backButtonText.setVisibility(View.VISIBLE);
			viewSwitcher.setDisplayedChild(0);
			emptyView = (TextView) v.findViewById(R.id.emptyText);
			emptyView.setTypeface(GreatBuyzApplication.getApplication().getFont());
			String msg = Utils.getMessageString(AppConstants.Messages.emptyDeal, R.string.emptyDeal);
			emptyView.setText(msg);
			lsComposer.setEmptyView(viewSwitcher);
		}
		catch (Exception e)
		{
			if (GreatBuyzApplication.isDebug) L.i("\nStack Trace: " + Thread.currentThread().getStackTrace()[2] + "\nMessage Exception : " + e.getMessage());
		}
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		activity = getActivity();
		if (GreatBuyzApplication.getDataController().getDealsByCategoriesDTO() != null && GreatBuyzApplication.getDataController().getDealsByCategoriesDTO().getDeals().size() > 0)
		{
			initialiseList();
		}
		else
		{
			listDownloader = new MyListDownloader();
			listDownloader.execute();
		}
		lsComposer.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
			{
				if (arg2 < GreatBuyzApplication.getDataController().getDealsByCategoriesDTO().getDeals().size()) Utils.startDetailsScreenNew(getActivity(), arg2, AppConstants.FramentConstants.DEAL_BY_CATEGORY);
			}
		});
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
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}
	
	public void bRefresh_click(View v)
	{
		adapter.reset();
		adapter.resetPage();
		adapter.notifyMayHaveMorePages();
	}
	
	List<DealScreenDTO> dataContainer = new ArrayList<DealScreenDTO>();
	
	class PaginationComposerAdapter extends AmazingAdapter
	{
		public PaginationComposerAdapter()
		{}
		
		public void reset()
		{
			viewSwitcher.setDisplayedChild(1);
			GreatBuyzApplication.getDataController().getDealsByCategoriesDTO().getDeals().clear();
		}
		
		@Override
		public int getCount()
		{
			try
			{
				if (morePageAvailable())
					return GreatBuyzApplication.getDataController().getDealsByCategoriesDTO().getDeals().size() + 1;
				else
					return GreatBuyzApplication.getDataController().getDealsByCategoriesDTO().getDeals().size();
			}
			catch (Exception e)
			{}
			return 0;
		}
		
		@Override
		public DealScreenDTO getItem(int position)
		{
			return GreatBuyzApplication.getDataController().getDealsByCategoriesDTO().getDeals().get(position);
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
			TextView  txtDealPayHeading;
			TextView  txtDealValHeading;
			TextView  txtDealDiscHeading;
			TextView  txtDealVal;
			TextView  txtDealDisc;
			TextView  txtDealPay;
			TextView  txtEuroSymbol1;
			TextView  txtEuroSymbol2;
			TextView  txtPercentSymbol;
			TextView  txtDealLocation;
			
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
			try
			{
				ViewHolder holder = null;
				if (position < GreatBuyzApplication.getDataController().getDealsByCategoriesDTO().getDeals().size())
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
							row = (View) getActivity().getLayoutInflater().inflate(R.layout.deallistitemimageexpand, null);
						else
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
					Utils.setLocationToForCategoryDeal(holder.txtDealLocation, item.get_dealLocation(), item.get_dealLocationDistrict());
				}
				else
				{
					// row =
					// activity.getLayoutInflater().inflate(R.layout.loading_view,
					// null);
					row = getActivity().getLayoutInflater().inflate(R.layout.searching_deals_view, null);
				}
			}
			catch (Exception e)
			{
				if (GreatBuyzApplication.isDebug) L.i("\nStack Trace: " + Thread.currentThread().getStackTrace()[2] + "\nMessage Exception : " + e.getMessage());
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
	
	class MyListDownloader extends AsyncTask<Void, Void, ClipResponse>
	{
		@Override
		protected ClipResponse doInBackground(Void... params)
		{
			try
			{
				ClipResponse clipResponse = GreatBuyzApplication.getServiceDelegate().getDealsByCategories(AppConstants.JSONKeys.WAP, selectedCategory, GreatBuyzApplication.getApplication().getLimitForDealByCategory(), GreatBuyzApplication.getApplication().getSkipIndexForDealByCategory());
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
						currentCount = result.getListData().size();
						if (GreatBuyzApplication.getDataController() == null) return;
						GreatBuyzApplication.getDataController().dealsByCategoriesReceived(result.getListData());
						if (adapter == null)
						{
							// ////System.out.println("Calling from onPostExecute().......................");
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
						currentCount = 0;
						if (GreatBuyzApplication.getDataController().getDealsByCategoriesDTO() == null || GreatBuyzApplication.getDataController().getDealsByCategoriesDTO().getDeals().size() == 0)
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
					if (GreatBuyzApplication.getDataController().getDealsByCategoriesDTO() == null || GreatBuyzApplication.getDataController().getDealsByCategoriesDTO().getDeals().size() == 0)
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
			catch (NotFoundException e)
			{
				if (GreatBuyzApplication.isDebug) L.i("\nStack Trace: " + Thread.currentThread().getStackTrace()[2] + "\nMessage Exception : " + e.getMessage());
				e.printStackTrace();
			}
			catch (Exception e)
			{
				if (GreatBuyzApplication.isDebug) L.i("\nStack Trace: " + Thread.currentThread().getStackTrace()[2] + "\nMessage Exception : " + e.getMessage());
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
			if (GreatBuyzApplication.getDataController().getDealsByCategoriesDTO().getDeals().size() >= totalVisibleItems && currentCount >= GreatBuyzApplication.getApplication().getLimitForDealByCategory()) morePage = true;
		}
		catch (Exception e)
		{
			if (GreatBuyzApplication.isDebug) L.i("\nStack Trace: " + Thread.currentThread().getStackTrace()[2] + "\nMessage Exception : " + e.getMessage());
		}
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
			{
				if (GreatBuyzApplication.isDebug) L.i("\nStack Trace: " + Thread.currentThread().getStackTrace()[2] + "\nMessage Exception : " + e.getMessage());
			}
		}
	}
	
	public void refreshFragment()
	{
		try
		{
			stopDownloader();
			listDownloader = null;
			if (GreatBuyzApplication.getDataController().getDealsByCategoriesDTO() != null && GreatBuyzApplication.getDataController().getDealsByCategoriesDTO().getDeals() != null && GreatBuyzApplication.getDataController().getDealsByCategoriesDTO().getDeals().size() > 0)
			{
				GreatBuyzApplication.getDataController().getDealsByCategoriesDTO().getDeals().clear();
			}
			GreatBuyzApplication.getApplication().setSkipIndexForDealByCategory(0);
			if (adapter != null) adapter.notifyDataSetChanged();
			adapter = null;
			listDownloader = new MyListDownloader();
			viewSwitcher.setDisplayedChild(0);
			listDownloader.execute();
		}
		catch (Exception e)
		{
			if (GreatBuyzApplication.isDebug) L.i("\nStack Trace: " + Thread.currentThread().getStackTrace()[2] + "\nMessage Exception : " + e.getMessage());
		}
	}
}
