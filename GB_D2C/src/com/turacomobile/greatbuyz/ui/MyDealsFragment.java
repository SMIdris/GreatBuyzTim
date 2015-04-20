package com.turacomobile.greatbuyz.ui;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.flurry.android.FlurryAgent;
import com.turacomobile.greatbuyz.GreatBuyzApplication;
import com.turacomobile.greatbuyz.R;
import com.turacomobile.greatbuyz.data.Deal;
import com.turacomobile.greatbuyz.data.DealScreenDTO;
import com.turacomobile.greatbuyz.data.Purchase;
import com.turacomobile.greatbuyz.hcoe.ui.dialog.LoadingDialogFragment;
import com.turacomobile.greatbuyz.service.IOperationListener;
import com.turacomobile.greatbuyz.service.MyDealsClipResponse;
import com.turacomobile.greatbuyz.utils.AppConstants;
import com.turacomobile.greatbuyz.utils.Utils;

public final class MyDealsFragment extends Fragment
{
	OnDealItemClick onDealItemClick;

	PaginationComposerAdapter adapter;
	String selectedCategory = null;
	boolean isTaskPending = false;

	TextView emptyView;
	ViewSwitcher viewSwitcher;
	boolean callMore = true;
	AmazingListView lsComposer;
	static Activity activity;
	int totalVisibleItems = 0;
	int currentCount = 0;

	public static MyDealsFragment newInstance(String content, Activity activity, OnDealItemClick dealItemClick)
	{
		MyDealsFragment fragment = new MyDealsFragment();
		
		fragment.onDealItemClick = dealItemClick;
		fragment.selectedCategory = content;
		MyDealsFragment.activity = activity;
		return fragment;
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
    	FlurryAgent.onPageView();
    }
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		//System.out.println("onDestroy");
	}
	
	public void onPause()
	{
		boolean isTaskRunning = false;
		if(listDownloader != null)
			isTaskRunning = listDownloader.getStatus() == AsyncTask.Status.FINISHED ? false : true;
		if(isTaskRunning)
		{
			try
			{
				listDownloader.cancel(true);
			}
			catch(Exception e)
			{
			}
			adapter = null;
			listDownloader = null;
			isTaskPending = true;
		}
		super.onPause();
	}
	
	public void onResume()
	{
		super.onResume();
		if(isTaskPending)
			refreshFragment();
		isTaskPending = false;
	}

	MyListDownloader listDownloader;

	public void initialiseList()
	{
		//activity.runOnUiThread(new Runnable()
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
					if (morePageAvailable())
						adapter.notifyMayHaveMorePages();
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
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
		lsComposer = (AmazingListView) v.findViewById(R.id.lsComposer);
		ViewSwitcher parentViewSwitcher = (ViewSwitcher) v.findViewById(R.id.gpsNeededViewSwitcher);
		parentViewSwitcher.setDisplayedChild(0);
		viewSwitcher = (ViewSwitcher) v.findViewById(android.R.id.empty);
		viewSwitcher.setDisplayedChild(0);
		emptyView = (TextView) v.findViewById(R.id.emptyText);

		String err = getResources().getString(R.string.emptyMyDealListMessage);
		String serverErr = GreatBuyzApplication.getDataController().getMessage(AppConstants.Messages.emptyMyDealListMessage);
		if(!Utils.isNothing(serverErr))
			err = serverErr;
		emptyView.setText(err);

		emptyView.setTypeface(GreatBuyzApplication.getApplication().getFont());
		lsComposer.setEmptyView(viewSwitcher);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		
		activity = getActivity();
		
		if (GreatBuyzApplication.getDataController().getMyDealsDTO() != null
				&& GreatBuyzApplication.getDataController().getMyDealsDTO().getMyDealsList().size() > 0)
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
				startDetailsScreen(arg2);
			}
		});
		//lsComposer.setLoadingView(activity.getLayoutInflater().inflate(R.layout.loading_view, null));
		lsComposer.setLoadingView(getActivity().getLayoutInflater().inflate(R.layout.loading_view, null));
		lsComposer.setOnScrollListener(new OnScrollListener()
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				if ( scrollState == OnScrollListener.SCROLL_STATE_IDLE )
					lsComposer.invalidateViews();
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
			{
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}
	
	public void startDetailsScreen(final int index)
	{
		String dealId = GreatBuyzApplication.getDataController().getMyDealsDTO().getMyDealsList().get(index).getDealId();
		if (dealId != null)
		{
			showLoadingDialog();
			try
			{
				GreatBuyzApplication.getServiceDelegate().getPurchaseDealById(dealId, new IOperationListener()
				{
					@Override
					public void onOperationCompleted(boolean pOperationComplitionStatus, String pMessageFromServer)
					{
						//System.out.println(pOperationComplitionStatus);
						dismissLoadingDialog();
						if (pOperationComplitionStatus)
						{
							Deal deal = GreatBuyzApplication.getDataController().getPurchaseDealById();
							System.out.println("deal  ***"+deal);
							String merchantName = deal.getMerchant().getName();
							//String contactDetails = deal.getContact().getDetails();
							String contactDetails = deal.getMerchant().getContact().getDetails();
							String offer = deal.getTnC().getOffer();
							Intent detailScreen = new Intent(getActivity(), MyDealsDetailScreen.class);
							detailScreen.putExtra("index", index);
							detailScreen.putExtra("merchantName", merchantName);
							detailScreen.putExtra("contactDetails", contactDetails);
							detailScreen.putExtra("offer", offer);
							//activity.startActivity(detailScreen);
							getActivity().startActivity(detailScreen);
						}
						
						else
						{
							if (pMessageFromServer != null)
								showMessageDialog(AppConstants.DialogConstants.MESSAGE_DIALOG, pMessageFromServer);
						}
						
					}
				});
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			getActivity().removeDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			dismissLoadingDialog();
		}
	}

	List<DealScreenDTO> dataContainer = new ArrayList<DealScreenDTO>();

	class PaginationComposerAdapter extends AmazingAdapter
	{
		public PaginationComposerAdapter()
		{
		}

		public void reset()
		{
			viewSwitcher.setDisplayedChild(1);
			GreatBuyzApplication.getDataController().getMyDealsDTO().getMyDealsList().clear();
		}

		@Override
		public int getCount()
		{
			try
			{
//				if (morePageAvailable())
//					return GreatBuyzApplication.getDataController().getMyDealsDTO().getMyDealsList().size() + 1;
//				else
					return GreatBuyzApplication.getDataController().getMyDealsDTO().getMyDealsList().size();
			}
			catch(Exception e)
			{}
			return 0;
		}

		@Override
		public Purchase getItem(int position)
		{
			return GreatBuyzApplication.getDataController().getMyDealsDTO().getMyDealsList().get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public void onNextPageRequested(int page)
		{
			Log.d(TAG, "Got onNextPageRequested page=" + page);
		}

		@Override
		public void bindSectionHeader(View view, int position, boolean displaySectionHeader)
		{
		}
		
		class ViewHolder
		{
			TextView txtMerchantName;
			TextView txtIssueDate;
			TextView txtOffer;
			TextView txtCouponId;
			public ViewHolder(View v)
			{
				txtMerchantName = (TextView) v.findViewById(R.id.txt_merchant_name);
				txtIssueDate = (TextView) v.findViewById(R.id.txt_issue_date);
				txtOffer = (TextView) v.findViewById(R.id.txt_offer);
				txtCouponId = (TextView) v.findViewById(R.id.txt_couponid);
				txtCouponId.setTypeface(GreatBuyzApplication.getApplication().getFont());
			}
		}

		@Override
		public View getAmazingView(int position, View convertView, ViewGroup parent)
		{
			View row = null;
			ViewHolder holder = null;

			if (position < GreatBuyzApplication.getDataController().getMyDealsDTO().getMyDealsList().size())
			{
				row = convertView;
				Purchase item = getItem(position);

				boolean createNew = false;
				if (row == null)
				{
					createNew = true;
				}
				else
				{
					holder = (ViewHolder) row.getTag();
					if(holder == null)
						createNew = true;
				}
				
				if(createNew)
				{
					//row = (View) activity.getLayoutInflater().inflate(R.layout.mydeallistitem, null);
					row = (View) getActivity().getLayoutInflater().inflate(R.layout.mydeallistitem, null);
					holder = new ViewHolder(row);
					row.setTag(holder);
				}

				holder.txtMerchantName.setText(item.getDealName());
				holder.txtIssueDate.setText(DateFormat.format("dd MMM yyyy", item.getCoupon().getIssueDate()));
				//holder.txtOffer.setText(item.getDealName());
				holder.txtCouponId.setText(item.getCoupon().getCouponId());
			}
			else
			{
				//row = activity.getLayoutInflater().inflate(R.layout.loading_view, null);
				row = getActivity().getLayoutInflater().inflate(R.layout.loading_view, null);
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

	class MyListDownloader extends AsyncTask<Void, Void, MyDealsClipResponse>
	{
		@Override
		protected MyDealsClipResponse doInBackground(Void... params)
		{
			try {
				MyDealsClipResponse clipResponse = GreatBuyzApplication.getServiceDelegate().getMyDeals(GreatBuyzApplication.getDataController().getMDN());
				return clipResponse;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(MyDealsClipResponse result)
		{
			try {
				if (result != null)
				{

					if (result.getListData() != null && result.getListData().size() > 0)
					{
						currentCount = result.getListData().size();
						if (GreatBuyzApplication.getDataController() == null)
							return;

						GreatBuyzApplication.getDataController().myDealsReceived(result.getListData());

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
						if (GreatBuyzApplication.getDataController().getMyDealsDTO() == null
								|| GreatBuyzApplication.getDataController().getMyDealsDTO().getMyDealsList().size() == 0)
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
					if (GreatBuyzApplication.getDataController().getMyDealsDTO() == null
							|| GreatBuyzApplication.getDataController().getMyDealsDTO().getMyDealsList().size() == 0)
					{
						if (emptyView != null)
						{
							String err = getResources().getString(R.string.networkProblemMessage);
							String serverErr = GreatBuyzApplication.getDataController().getMessage(AppConstants.Messages.networkProblemMessage);
							if(!Utils.isNothing(serverErr))
								err = serverErr;
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
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public boolean morePageAvailable()
	{
		if (lsComposer == null)
			return false;

		totalVisibleItems = lsComposer.getLastVisiblePosition() - lsComposer.getFirstVisiblePosition();
		try
		{
			if (GreatBuyzApplication.getDataController().getMyDealsDTO().getMyDealsList().size() >= totalVisibleItems
					&& currentCount > 0)
				return true;
		}
		catch(Exception e)
		{}

		return false;
	}

	public void stopDownloader()
	{
		boolean isTaskRunning = false;
		if(listDownloader != null)
			isTaskRunning = listDownloader.getStatus() == AsyncTask.Status.FINISHED ? false : true;
		if(isTaskRunning)
		{
			try
			{
				listDownloader.cancel(true);
			}
			catch(Exception e)
			{
			}
		}
	}
	
	public void refreshFragment()
	{
		stopDownloader();
		listDownloader = null;
		if (GreatBuyzApplication.getDataController().getMyDealsDTO() != null
				&& GreatBuyzApplication.getDataController().getMyDealsDTO().getMyDealsList() != null
				&& GreatBuyzApplication.getDataController().getMyDealsDTO().getMyDealsList().size() > 0)
		{
			GreatBuyzApplication.getDataController().getMyDealsDTO().getMyDealsList().clear();
		}
		if (adapter != null)
			adapter.notifyDataSetChanged();
		adapter = null;
		listDownloader = new MyListDownloader();
		listDownloader.execute();
	}
	
	final static String LOADING_DIALOG_TAG = "progress_dialog";	

	/**
	 * Shows the loading progress dialog
	 */
	public void showLoadingDialog() {

		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(
				LOADING_DIALOG_TAG);

		if (prev != null) {
			ft.remove(prev);
		}

		// Create and show the dialog.
		LoadingDialogFragment newFragment = LoadingDialogFragment.newInstance();
		newFragment.show(ft, LOADING_DIALOG_TAG);
	}

	/**
	 * Dismisses the loading progress dialog
	 */
	public void dismissLoadingDialog() {
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(
				LOADING_DIALOG_TAG);

		if (prev != null) {
			ft.remove(prev);
			ft.commit();
		}
	}
	
	public void showMessageDialog(final int whichDialog, final String message)
	{
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Bundle b = new Bundle();
					b.putString(AppConstants.JSONKeys.MESSAGE, message);
					getActivity().showDialog(whichDialog, b);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	
}
