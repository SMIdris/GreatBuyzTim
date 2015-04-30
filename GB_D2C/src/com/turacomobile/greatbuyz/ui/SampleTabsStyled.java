package com.turacomobile.greatbuyz.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.turacomobile.greatbuyz.GreatBuyzApplication;
import com.turacomobile.greatbuyz.R;
import com.turacomobile.greatbuyz.analytics.Analytics;
import com.turacomobile.greatbuyz.data.SettingItem;
import com.turacomobile.greatbuyz.framework.ActionItem;
import com.turacomobile.greatbuyz.framework.QuickAction;
import com.turacomobile.greatbuyz.hcoe.ui.dialog.GenericDialog;
import com.turacomobile.greatbuyz.hcoe.ui.dialog.LoadingDialog;
import com.turacomobile.greatbuyz.service.DataController;
import com.turacomobile.greatbuyz.service.IOperationListener;
import com.turacomobile.greatbuyz.utils.AppConstants;
import com.turacomobile.greatbuyz.utils.GBWebViewClient;
import com.turacomobile.greatbuyz.utils.GreatBuyzTextView;
import com.turacomobile.greatbuyz.utils.PopoverView;
import com.turacomobile.greatbuyz.utils.PopoverView.PopoverViewDelegate;
import com.turacomobile.greatbuyz.utils.Utils;
import com.turacomobile.greatbuyz.utils.settingsArrayAdapter;

public class SampleTabsStyled extends FragmentActivity implements OnCategoryClick, OnDealItemClick, OnPageChangeListener, PopoverViewDelegate
{
	GreatBuyzApplication			  _app				= GreatBuyzApplication.getApplication();
	GoogleMusicAdapter				mAdapter;
	ViewPager						 mPager;
	PageIndicator					 mIndicator;
	RelativeLayout					mBtnPrevTab;
	RelativeLayout					mBtnNextTab;
	int							   mSelectedTabIndex;
	ViewGroup						 settingsView;
	ImageView						 menuView;
	ImageView						 imgBack;
	private LinearLayout			  mBackNavigation;
	private static final List<String> CONTENT			 = new ArrayList<String>();
	// private static final String[] CONTENT = new String[7];
	// private static final String[] CONTENT = new String[6];
	Fragment[]						fragments		   = new Fragment[7];
	// Fragment[] fragments = new Fragment[6];
	boolean						   isFirstTime		 = true;
	DataController					_data			   = GreatBuyzApplication.getDataController();
	String							selectedCity		= _data.getUserCity();
	boolean						   isSubscribedToGCM   = false;
	String							emailId			 = null;
	String							categoryListTabName;
	int							   categoryListFragmentPosition;
	int							   categoryHeaderLength;
	// Settings
	private GreatBuyzTextView		 settingChildListTitle;
	private List<SettingItem>		 settingItems		= null;
	private DrawerLayout			  mDrawerLayout;
	private ListView				  mDrawerList;
	private static boolean			mIsMainDrawerOpen   = false;
	private DrawerLayout			  mDrawerLayoutChild;
	private RelativeLayout			mDrawerLayoutChildView;
	private ListView				  mDrawerListChild;
	private static boolean			mIsChildDrawerOpen  = false;
	List<SettingItem>				 settingSubListItems = null;
	private RelativeLayout			mNotificationLayout;
	private LinearLayout			  mVersionLayout;
	private Button					mNotifButton;
	private  LinearLayout ivPicRel = null;
	/*
	 * private CheckBox mInappCheckBox; private Spinner mNotificationSpinner;
	 */
	private CheckBox				  mDailyMsgCheckBox;
	private TextView				  mSpinnerText;
	private TextView				  mNotifSetAlertTextView;
	private boolean				   isSearchVisible	 = false;
	private boolean				   isMyDealsVisible	= false;
	private int					   mSearchIndex		= -1;
	private int					   mMyDealIndex		= -1;
	public boolean[]				  selection;
	public boolean[]				  cloneSelection;
	static final ArrayList<String>	spinnerValues	   = Utils.getNotificationFrequencies();
	RelativeLayout					mWebViewLayout;
	private WebView				   webView;
	private TextView				  titleView;
	GBWebViewClient				   webViewClient;
	String							mwebUrl;
	String							mwebTitle;
	private  TextView ivPop = null;
	private  QuickAction mQuickAction = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Log.v("Time test : Home screen create start :", "" +
		// Utils.getCurrentTimeStamp());
		// ////System.out.println("GreatBuyz: function IN SampleTabsStyled > onCreate");
		mAdapter = null;
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.simple_tabs);
		// setting analytics log parameters
		try
		{
			Analytics a = GreatBuyzApplication.getApplication().getAnalyticsAgent();
			a.setMDN(_data.getMDN());
			int f = Integer.parseInt(_data.getConstant(AppConstants.Constants.logFileUploadFrequencyInMinutes));
			a.setLogRotationFrequency(f);
		}
		catch (Exception e)
		{}
		setToDealDetailScreen(getIntent());
		// Exclusive Deals
		String name = Utils.getMessageString(AppConstants.Messages.dealsOfTheDay, R.string.dealsOfTheDay);
		CONTENT.clear();
		CONTENT.add(0, name);
		// Top Deals
		name = Utils.getMessageString(AppConstants.Messages.topDeals, R.string.topDeals);
		CONTENT.add(1, name);
		// Deals by Category
		name = Utils.getMessageString(AppConstants.Messages.dealsByCategories, R.string.dealsByCategories);
		CONTENT.add(2, name);
		categoryListFragmentPosition = 2;
		categoryListTabName = name;
		/*
		 * // Deals near me name = Utils.getMessageString(AppConstants.Messages.dealsNearMe, R.string.dealsNearMe); CONTENT.add(3, name);
		 */
		// coupons tab
		name = Utils.getMessageString(AppConstants.Messages.exploreCoupons, R.string.exploreCoupons);
		CONTENT.add(3, name);
		//
		// Surprise Me
		name = Utils.getMessageString(AppConstants.Messages.surpriseMe, R.string.surpriseMe);
		CONTENT.add(4, name);
		// Explore Deals
		name = Utils.getMessageString(AppConstants.Messages.exploreDeals, R.string.exploreDeals);
		CONTENT.add(5, name);
		// My Deals
		name = Utils.getMessageString(AppConstants.Messages.myDeals, R.string.myDeals);
		// CONTENT.add(5, name);
		// CONTENT.remove(6);
		// CONTENT.remove(5);
		// Deals You May Like
		/*
		 * name = getResources().getString(R.string.dealsYouMayLike); srvName = _data.getMessage(AppConstants.Messages.dealsYouMayLike); if(!Utils.isNothing(srvName)) name = srvName; CONTENT[7] = name;
		 */
		categoryHeaderLength = AppConstants.CATEGORY_HEADER_LENGTH;
		String categoryHeaderLengthSrv = _data.getConstant(AppConstants.Constants.categoryNameHeaderLength);
		if (!Utils.isNothing(categoryHeaderLengthSrv))
		{
			try
			{
				categoryHeaderLength = Integer.parseInt(categoryHeaderLengthSrv);
			}
			catch (Exception e)
			{}
		}
		imgBack = (ImageView) findViewById(R.id.imgBackArrow);
		imgBack.setVisibility(View.INVISIBLE);
		// imgBack.setOnClickListener(new OnClickListener()
		// {
		// @Override
		// public void onClick(View v)
		// {
		// onBack();
		// }
		// });
		mBackNavigation = (LinearLayout) findViewById(R.id.back_navigation);
		mBackNavigation.setClickable(false);
		mBackNavigation.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onBack();
			}
		});
		mAdapter = null;
		mAdapter = new GoogleMusicAdapter(getSupportFragmentManager(), false);
		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
		mIndicator.setHorizontalFadingEdgeEnabled(false);
		mIndicator.setOnPageChangeListener(this);
		mBtnPrevTab = (RelativeLayout) findViewById(R.id.btn_indicator_left);
		mBtnPrevTab.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				System.out.println("Left");
				mIndicator.setCurrentItem(mSelectedTabIndex - 1);
			}
		});
		mBtnPrevTab.setEnabled(false);
		// mBtnPrevTab.setVisibility(View.INVISIBLE);
		mBtnNextTab = (RelativeLayout) findViewById(R.id.btn_indicator_right);
//		 ivPicRel = (RelativeLayout) this.findViewById(R.id.popover);
//		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivPicRel.getLayoutParams();
//		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//		ivPicRel.setLayoutParams(params);
//		
		ivPicRel = (LinearLayout) this.findViewById(R.id.popover);
		
		ivPop = (TextView) this.findViewById(R.id.popoverText);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.RIGHT;
				ivPop.setLayoutParams(params);
		//
		ActionItem addAction = new ActionItem();
		addAction.setTitle("Phone");
		addAction.setIcon(getResources().getDrawable(R.drawable.phone));
		// Accept action item
		ActionItem accAction = new ActionItem();
		accAction.setTitle("Gmail");
		accAction.setIcon(getResources().getDrawable(R.drawable.gmail));
		// Upload action item
		ActionItem upAction = new ActionItem();
		upAction.setTitle("Talk");
		upAction.setIcon(getResources().getDrawable(R.drawable.talk));
		 mQuickAction = new QuickAction(this);
		mQuickAction.addActionItem(addAction);
		mQuickAction.addActionItem(accAction);
		mQuickAction.addActionItem(upAction);
		//
		// ScrollView ScrollView = (ScrollView) this.findViewById(R.id.scrollv);
		// setup the action item click listener
		mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener()
		{
			public void onItemClick(int pos)
			{
				// if (pos == 0) { // Add item selected
				// Toast.makeText(MainActivity.this,
				// "PHONE item selected", Toast.LENGTH_SHORT)
				// .show();
				// } else if (pos == 1) { // Accept item selected
				// Toast.makeText(MainActivity.this,
				// "GMAIL item selected", Toast.LENGTH_SHORT)
				// .show();
				// } else if (pos == 2) { // Upload item selected
				// Toast.makeText(MainActivity.this, "TALK selected",
				// Toast.LENGTH_SHORT).show();
				// }
			}
		});
		ivPop.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				
			}
		});
		
		mBtnNextTab.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				System.out.println("Right");
				mIndicator.setCurrentItem(mSelectedTabIndex + 1);
				// RelativeLayout rootView = (RelativeLayout)findViewById(R.id.popover);
				//
				// PopoverView popoverView = new PopoverView(SampleTabsStyled.this, R.layout.popover_showed_view);
				// popoverView.setContentSizeForViewInPopover(new Point(320, 340));
				// popoverView.setDelegate(SampleTabsStyled.this);
				// popoverView.showPopoverFromRectInViewGroup(rootView, PopoverView.getFrameForView(v), PopoverView.PopoverArrowDirectionAny, true);
				//ivPop.performClick();
//				mQuickAction.show(ivPop);
//				mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
			}
		});
		// ScrollView.getViewTreeObserver().addOnScrollChangedListener(new OnScrollChangedListener() {
		//
		// // @Override
		// public void onScrollChanged() {
		//
		// ivPic1.performClick();
		//
		// }
		// });
		//
		RelativeLayout myDealsShortcut = (RelativeLayout) findViewById(R.id.imgMyDeals);
		myDealsShortcut.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				openMyCouponTab();
			}
		});
		// Settings
		menuView = (ImageView) findViewById(R.id.imgMenu);
		populateSettingsMenu();
		menuView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				clickMenuButton();
			}
		});
		menuView.setVisibility(View.VISIBLE);
		mDailyMsgCheckBox = (CheckBox) findViewById(R.id.dailyMsgCheckBox);
		// ////System.out.println("GreatBuyz: function OUT SampleTabsStyled > onCreate");
		// Log.v("Time test : Home screen create end :", "" +
		// Utils.getCurrentTimeStamp());
		// mSpinnerText = (TextView) findViewById(R.id.spinner_text);
		/*
		 * mInappCheckBox = (CheckBox) findViewById(R.id.in_app_notification_checkbox); mNotificationSpinner = (Spinner) findViewById(R.id.notification_spinner); ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerValues); adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); mNotificationSpinner.setAdapter(adapter); mInappCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		 * @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) { setNotificationSpinnerVisibility(isChecked); } });
		 */
	}
	
	public void setToDealDetailScreen(Intent intent)
	{
		if (intent.getBooleanExtra(AppConstants.DEAL_SCREEN, false))
		{
			Intent mIn = new Intent(SampleTabsStyled.this, DetailScreen.class);
			mIn.setAction(intent.getAction());
			mIn.putExtra(AppConstants.JSONKeys.TYPE, intent.getIntExtra(AppConstants.JSONKeys.TYPE, 0));
			mIn.putExtra(AppConstants.JSONKeys.INDEX, intent.getIntExtra(AppConstants.JSONKeys.INDEX, 0));
			mIn.putExtra(AppConstants.JSONKeys.START_HOME_SCREEN, intent.getBooleanExtra(AppConstants.JSONKeys.START_HOME_SCREEN, false));
			startActivityForResult(mIn, AppConstants.RESULT_DEAL_DETAIL_SCREEN);
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent)
	{
		setToDealDetailScreen(intent);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	public void showWebView(String title, String url, boolean checkRefresh)
	{
		if (!checkRefresh)
		{
			mwebTitle = title;
			mwebUrl = url;
		}
		if (mWebViewLayout == null) mWebViewLayout = (RelativeLayout) findViewById(R.id.webView_layout);
		mWebViewLayout.setVisibility(View.VISIBLE);
		imgBack.setVisibility(View.VISIBLE);
		mBackNavigation.setClickable(true);
		if (titleView == null) titleView = (TextView) findViewById(R.id.txt_help_title);
		if (!titleView.getText().equals(mwebTitle) || checkRefresh)
		{
			titleView.setText(mwebTitle);
			titleView.setTypeface(GreatBuyzApplication.getApplication().getFont());
			if (webViewClient == null) webViewClient = new GBWebViewClient(SampleTabsStyled.this);
			if (webView == null) webView = (WebView) findViewById(R.id.helpDetails);
			webView.clearView();
			webView.setBackgroundColor(Color.WHITE);
			webView.clearCache(true);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
			webView.setWebViewClient(webViewClient);
			webView.addJavascriptInterface(new JavascriptHandler(), "native");
			// webView.loadDataWithBaseURL("file:///android_asset/", htmlStart +
			// text + htmlEnd, "text/html", "utf-8", null) ;
			WebSettings settings = webView.getSettings();
			settings.setDefaultTextEncodingName(AppConstants.ENCODING_UTF8);
			webView.loadUrl(mwebUrl);
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		// //System.out.println(" ON Resume SampleTabsStyled " + isSearchVisible
		// + " " + isMyDealsVisible);
		// mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		mDrawerLayoutChild.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}
	
	/*
	 * void setNotificationSpinnerVisibility(boolean status) { if (status) { mNotificationSpinner.setEnabled(true); mSpinnerText.setEnabled(true); // mNotifButton.setEnabled(true); } else { mNotificationSpinner.setEnabled(false); mSpinnerText.setEnabled(false); // mNotifButton.setEnabled(false); } }
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		// ////System.out.println("GreatBuyz: function IN SampleTabsStyled > dispatchKeyEvent");
		if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_MENU)
		{
			clickMenuButton();
			return true;
		}
		else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK)
		{
			onBack();
			return true;
		}
		// ////System.out.println("GreatBuyz: function OUT SampleTabsStyled > dispatchKeyEvent");
		return super.dispatchKeyEvent(event);
	}
	
	private void populateSettingsMenu()
	{
		// ////System.out.println("GreatBuyz: function IN SampleTabsStyled > populateSettingsMenu");
		createSettingsView();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerList.setAdapter(new settingsArrayAdapter(this, R.layout.drawer_list_item, settingItems));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		// mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		// ////System.out.println("GreatBuyz: function OUT SampleTabsStyled > populateSettingsMenu");
		mDrawerLayout.setDrawerListener(new DrawerListener()
		{
			@Override
			public void onDrawerStateChanged(int arg0)
			{}
			
			@Override
			public void onDrawerSlide(View arg0, float arg1)
			{}
			
			@Override
			public void onDrawerOpened(View arg0)
			{
				mIsMainDrawerOpen = true;
				if (mAdapter != null && mIndicator.getCurrentItem() + 1 == AppConstants.FramentConstants.SURPRISE_ME)
				{
					if (mAdapter.mSurpriseMeFragment != null) ((SurpriseMeFragment) mAdapter.mSurpriseMeFragment).stopSensor();
				}
			}
			
			@Override
			public void onDrawerClosed(View arg0)
			{
				mIsMainDrawerOpen = false;
				if (mAdapter != null && mIndicator.getCurrentItem() + 1 == AppConstants.FramentConstants.SURPRISE_ME)
				{
					if (mAdapter.mSurpriseMeFragment != null) ((SurpriseMeFragment) mAdapter.mSurpriseMeFragment).startSensor();
				}
				if (mDrawerLayoutChild != null) mDrawerLayoutChild.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
			}
		});
		initializeChildLayout();
	}
	
	private void initializeChildLayout()
	{
		settingChildListTitle = (GreatBuyzTextView) findViewById(R.id.title_text_id);
		mDrawerLayoutChild = (DrawerLayout) findViewById(R.id.drawer_layout_parent);
		mDrawerLayoutChild.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerLayoutChild.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		mNotificationLayout = (RelativeLayout) findViewById(R.id.notification_form);
		mVersionLayout = (LinearLayout) findViewById(R.id.layout_version);
		// mNotifTime = (EditText) findViewById(R.id.hour_edit_text);
		mNotifButton = (Button) findViewById(R.id.button_notification);
		mNotifSetAlertTextView = (TextView) findViewById(R.id.setalerttextview);
		Utils.setMessageToTextView(mNotifSetAlertTextView, AppConstants.Messages.settingItemKeywords);
		mNotifSetAlertTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(SampleTabsStyled.this, SetAlertsActivity.class);
				closeMenu();
				startActivityForResult(intent, AppConstants.RESULT_SET_ALERT_SCREEN);
			}
		});
		mNotifButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int frequency = -1;
				/*
				 * if (mInappCheckBox.isChecked()) { try { Object selectedItem = mNotificationSpinner.getSelectedItem(); String strFrequency = null; if (selectedItem != null) strFrequency = selectedItem.toString(); frequency = Integer.parseInt(strFrequency); } catch (Exception e) { } }
				 */
				String isDailyMsgEnabled = null;
				if (mDailyMsgCheckBox.isChecked())
				{
					isDailyMsgEnabled = "true";
				}
				else
				{
					isDailyMsgEnabled = "false";
				}
				sendNotificationFrequencyToServer(frequency, isDailyMsgEnabled);
			}
		});
		mDrawerLayoutChildView = (RelativeLayout) findViewById(R.id.child_drawer_layout);
		mDrawerListChild = (ListView) findViewById(R.id.left_drawer_child);
	}
	
	private void populateSettingsSubMenu()
	{
		if (mDrawerListChild == null)
		{
			mDrawerListChild = (ListView) findViewById(R.id.left_drawer_child);
		}
		mDrawerListChild.setAdapter(new settingsArrayAdapter(this, R.layout.drawer_list_item, settingSubListItems));
		mDrawerListChild.setOnItemClickListener(new ChildDrawerItemClickListener());
	}
	
	public void clickMenuButton()
	{
		// ////System.out.println("GreatBuyz: function IN SampleTabsStyled > clickMenuButton");
		if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
		{
			closeMenu();
		}
		else
		{
			mDrawerLayout.openDrawer(Gravity.RIGHT);
			mIsMainDrawerOpen = true;
			if (mAdapter != null && mIndicator.getCurrentItem() + 1 == AppConstants.FramentConstants.SURPRISE_ME)
			{
				if (mAdapter.mSurpriseMeFragment != null) ((SurpriseMeFragment) mAdapter.mSurpriseMeFragment).stopSensor();
			}
		}
		// ////System.out.println("GreatBuyz: function OUT SampleTabsStyled > clickMenuButton");
	}
	
	private void createSettingsSubView(int id)
	{
		switch (id)
		{
			case AppConstants.SettingItems.PERSONAL_PROFILE:
				if (settingSubListItems != null && !settingSubListItems.isEmpty()) settingSubListItems.clear();
				settingSubListItems = new ArrayList<SettingItem>();
				settingSubListItems.add(new SettingItem(AppConstants.SettingItems.CATEGORIES, Utils.getMessageString(AppConstants.Messages.settingItemCategoryPreferences, R.string.settingItemCategoryPreferences), this.getResources().getDrawable(R.drawable.setting_category)));
				settingSubListItems.add(new SettingItem(AppConstants.SettingItems.LOCATION, Utils.getMessageString(AppConstants.Messages.settingItemChangeLocation, R.string.settingItemChangeLocation), this.getResources().getDrawable(R.drawable.setting_faq)));
				settingSubListItems.add(new SettingItem(AppConstants.SettingItems.UNSUBSCRIBE, Utils.getMessageString(AppConstants.Messages.settingItemUnsubscribe, R.string.settingItemUnsubscribe), this.getResources().getDrawable(R.drawable.setting_dea)));
				settingSubListItems.add(new SettingItem(AppConstants.SettingItems.TNC, Utils.getMessageString(AppConstants.Messages.menuTerms, R.string.menuTerms), this.getResources().getDrawable(R.drawable.setting_terms)));
				break;
			case AppConstants.SettingItems.INFO:
				if (settingSubListItems != null && !settingSubListItems.isEmpty()) settingSubListItems.clear();
				settingSubListItems = new ArrayList<SettingItem>();
				settingSubListItems.add(new SettingItem(AppConstants.SettingItems.ABOUT, Utils.getMessageString(AppConstants.Messages.menuAbout, R.string.menuAbout), this.getResources().getDrawable(R.drawable.setting_alert)));
				settingSubListItems.add(new SettingItem(AppConstants.SettingItems.FAQ, Utils.getMessageString(AppConstants.Messages.menuFAQ, R.string.menuFAQ), this.getResources().getDrawable(R.drawable.setting_faq)));
				/*
				 * String version = Utils.getClientVersion(); if(!Utils.isNothing(version)) settingSubListItems.add(new SettingItem(AppConstants.SettingItems.VERSION, version, null));
				 */
				break;
			case AppConstants.SettingItems.HELP:
				if (settingSubListItems != null && !settingSubListItems.isEmpty()) settingSubListItems.clear();
				settingSubListItems = new ArrayList<SettingItem>();
				settingSubListItems.add(new SettingItem(AppConstants.SettingItems.FAQ, getResources().getString(R.string.menuFAQ), this.getResources().getDrawable(R.drawable.setting_faq)));
				break;
			default:
				break;
		}
	}
	
	private void createSettingsView()
	{
		// System.out.println("GreatBuyz: function IN SampleTabsStyled > createSettingsView");
		settingItems = new ArrayList<SettingItem>();
		/* settingItems.add(getResources().getString(R.string.menuSettings)); */
		settingItems.add(new SettingItem(AppConstants.SettingItems.PERSONAL_PROFILE, getResources().getString(R.string.settingItemPersonalProfile), this.getResources().getDrawable(R.drawable.setting_personal)));
		settingItems.add(new SettingItem(AppConstants.SettingItems.MY_COUPON, getResources().getString(R.string.settingItemMyCoupon), this.getResources().getDrawable(R.drawable.setting_unsubscribe)));
		settingItems.add(new SettingItem(AppConstants.SettingItems.NOTIFICATIONS, getResources().getString(R.string.settingItemNotifications), SampleTabsStyled.this.getResources().getDrawable(R.drawable.setting_faq)));
		/*
		 * settingItems.add(new SettingItem(AppConstants.SettingItems.SEARCH, getResources().getString(R.string.settingItemSearch), this .getResources().getDrawable(R.drawable.setting_search)));
		 */
		boolean getEmailIdFromUser = false;
		String strGetEmail = _data.getConstant(AppConstants.Constants.getEmailIdFromUser);
		if (!Utils.isNothing(strGetEmail))
		{
			try
			{
				getEmailIdFromUser = Boolean.parseBoolean(strGetEmail);
			}
			catch (Exception e)
			{}
		}
		if (getEmailIdFromUser) settingItems.add(new SettingItem(AppConstants.SettingItems.EMAIL, getResources().getString(R.string.settingItemEmail), null));
		/*
		 * settingItems.add(new SettingItem(AppConstants.SettingItems.KEYWORDS, getResources().getString(R.string.settingItemKeywords), getApplicationContext ().getResources().getDrawable(R.drawable.setting_help)));
		 */
		settingItems.add(new SettingItem(AppConstants.SettingItems.ABOUT, getResources().getString(R.string.menuAbout), this.getResources().getDrawable(R.drawable.setting_alert)));
		settingItems.add(new SettingItem(AppConstants.SettingItems.HELP, getResources().getString(R.string.menuHelp), this.getResources().getDrawable(R.drawable.setting_help)));
		settingItems.add(new SettingItem(AppConstants.SettingItems.REFRESH, getResources().getString(R.string.menuRefresh), this.getResources().getDrawable(R.drawable.setting_refresh)));
		isSubscribedToGCM = _data.isUserSubscribedToGCM();
		emailId = _data.getEmailId();
		// System.out.println("GreatBuyz: function OUT SampleTabsStyled > createSettingsView");
	}
	
	private void stopAllDownload()
	{
		// ////System.out.println("GreatBuyz: function IN SampleTabsStyled > stopAllDownload");
		try
		{
			if (mAdapter != null)
			{
				if (((ExclusiveDealsFragment) mAdapter.mExclusiveDealsFragment) != null)
				{
					if (((ExclusiveDealsFragment) mAdapter.mExclusiveDealsFragment).listDownloader != null) ((ExclusiveDealsFragment) mAdapter.mExclusiveDealsFragment).listDownloader.cancel(true);
					if (((ExclusiveDealsFragment) mAdapter.mExclusiveDealsFragment).adapter != null) ((ExclusiveDealsFragment) mAdapter.mExclusiveDealsFragment).adapter.reset();
				}
				if (((DealsOfTheDayFragment) mAdapter.mDealsOfTheDayFragment) != null)
				{
					if (((DealsOfTheDayFragment) mAdapter.mDealsOfTheDayFragment).listDownloader != null) ((DealsOfTheDayFragment) mAdapter.mDealsOfTheDayFragment).listDownloader.cancel(true);
					if (((DealsOfTheDayFragment) mAdapter.mDealsOfTheDayFragment).adapter != null) ((DealsOfTheDayFragment) mAdapter.mDealsOfTheDayFragment).adapter.reset();
				}
				if (mAdapter.mFragmentForCategory != null && mAdapter.mFragmentForCategory instanceof DealsByCategoryFragment)
				{
					if (((DealsByCategoryFragment) mAdapter.mFragmentForCategory).listDownloader != null) ((DealsByCategoryFragment) mAdapter.mFragmentForCategory).listDownloader.cancel(true);
					if (((DealsByCategoryFragment) mAdapter.mFragmentForCategory).adapter != null) ((DealsByCategoryFragment) mAdapter.mFragmentForCategory).adapter.reset();
				}
				if (((DealsYouMayLikeFragment) mAdapter.mDealsYouMayLikeFragment) != null)
				{
					if (((DealsYouMayLikeFragment) mAdapter.mDealsYouMayLikeFragment).listDownloader != null) ((DealsYouMayLikeFragment) mAdapter.mDealsYouMayLikeFragment).listDownloader.cancel(true);
					if (((DealsYouMayLikeFragment) mAdapter.mDealsYouMayLikeFragment).adapter != null) ((DealsYouMayLikeFragment) mAdapter.mDealsYouMayLikeFragment).adapter.reset();
				}
				if (((DealsNearMeFragment) mAdapter.mDealsNearMeFragment) != null)
				{
					if (((DealsNearMeFragment) mAdapter.mDealsNearMeFragment).listDownloader != null) ((DealsNearMeFragment) mAdapter.mDealsNearMeFragment).listDownloader.cancel(true);
					if (((DealsNearMeFragment) mAdapter.mDealsNearMeFragment).adapter != null) ((DealsNearMeFragment) mAdapter.mDealsNearMeFragment).adapter.reset();
				}
				if (mAdapter.mFragmentForExploreCategory instanceof ExploreDealResultFragment && mAdapter.mFragmentForExploreCategory != null)
				{
					if (((ExploreDealResultFragment) mAdapter.mFragmentForExploreCategory).listDownloader != null) ((ExploreDealResultFragment) mAdapter.mFragmentForExploreCategory).listDownloader.cancel(true);
					if (((ExploreDealResultFragment) mAdapter.mFragmentForExploreCategory).adapter != null) ((ExploreDealResultFragment) mAdapter.mFragmentForExploreCategory).adapter.reset();
				}
				/*
				 * if (((MyDealFragment) mAdapter.mMyDealsFragment) != null) { if (((MyDealFragment) mAdapter.mMyDealsFragment).listDownloader != null) ((MyDealFragment) mAdapter.mMyDealsFragment).listDownloader.cancel(true); if (((MyDealFragment) mAdapter.mMyDealsFragment).adapter != null) ((MyDealFragment) mAdapter.mMyDealsFragment).adapter.reset(); }
				 */
				if (((MyDealsFragment) mAdapter.mMyDealsFragment) != null)
				{
					if (((MyDealsFragment) mAdapter.mMyDealsFragment).listDownloader != null) ((MyDealsFragment) mAdapter.mMyDealsFragment).listDownloader.cancel(true);
					if (((MyDealsFragment) mAdapter.mMyDealsFragment).adapter != null) ((MyDealsFragment) mAdapter.mMyDealsFragment).adapter.reset();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		// ////System.out.println("GreatBuyz: function OUT SampleTabsStyled > stopAllDownload");
	}
	
	public void showCityDialog(View v)
	{
		showDialog(AppConstants.DialogConstants.CHANGE_LOCATION_DIALOG, null);
	}
	
	boolean isCategoryByDealsDisplayed	 = false;
	boolean isCategoryAfterExplorDisplayed = false;
	int	 currentSelectedItem			= 100;
	String  dealsByCategoryKey			 = "dealByCategory";
	String  exploreDealsKey				= "exploreDeals";
	String  selectedPagerItemIndexKey	  = "pagerItemIndex";
	
	public void onBack()
	{
		System.out.println("mPager.getCurrentItem() " + mPager.getCurrentItem() + "mSearchIndex - 1" + (mSearchIndex - 1) + "isSearchVisible   " + isSearchVisible + " isCategoryAfterExplorDisplayed " + isCategoryAfterExplorDisplayed);
		// ////System.out.println("GreatBuyz: function IN SampleTabsStyled > onBack");
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isAcceptingText()) imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		if (mIsChildDrawerOpen)
		{
			mDrawerLayoutChild.closeDrawer(Gravity.RIGHT);
			mIsChildDrawerOpen = false;
			return;
		}
		else if (mIsMainDrawerOpen)
		{
			closeMenu();
			return;
		}
		/*
		 * else super.onBackPressed();
		 */
		else if (mWebViewLayout != null && mWebViewLayout.isShown())
		{
			closeInfoView();
		}
		else if (isCategoryByDealsDisplayed && mPager.getCurrentItem() == 2)
		{
			categoryFragmentNavigationListner.onSwitchToPrevFragment();
		}
		else if (isCategoryAfterExplorDisplayed && mPager.getCurrentItem() == 5)
		{
			exploreFragmentNavigationListner.onSwitchToPrevFragment();
		}
		else
		{
			Bundle b = new Bundle();
			String err = Utils.getMessageString(AppConstants.Messages.exitMessage, R.string.exitMessage);
			b.putString(AppConstants.JSONKeys.MESSAGE, err);
			showDialog(AppConstants.DialogConstants.EXIT_DIALOG, b);
			// finish();
		}
		// ////System.out.println("GreatBuyz: function OUT SampleTabsStyled > onBack");
	}
	
	/*
	 * private void exit() { // ////System.out.println("GreatBuyz: function IN SampleTabsStyled > exit"); stopAllDownload(); try { if (mAdapter != null) { if (((ExclusiveDealsFragment) mAdapter.mExclusiveDealsFragment) != null) { ((ExclusiveDealsFragment) mAdapter.mExclusiveDealsFragment).listDownloader = null; ((ExclusiveDealsFragment) mAdapter.mExclusiveDealsFragment).adapter = null; } if (((DealsOfTheDayFragment) mAdapter.mDealsOfTheDayFragment) != null) { ((DealsOfTheDayFragment) mAdapter.mDealsOfTheDayFragment).listDownloader = null; ((DealsOfTheDayFragment) mAdapter.mDealsOfTheDayFragment).adapter = null; } if (mAdapter.mFragmentForCategory != null && mAdapter.mFragmentForCategory instanceof DealsByCategoryFragment) { ((DealsByCategoryFragment) mAdapter.mFragmentForCategory).listDownloader = null; ((DealsByCategoryFragment) mAdapter.mFragmentForCategory).adapter = null; } if (((DealsYouMayLikeFragment) mAdapter.mDealsYouMayLikeFragment) != null) { ((DealsYouMayLikeFragment) mAdapter.mDealsYouMayLikeFragment).listDownloader = null; ((DealsYouMayLikeFragment) mAdapter.mDealsYouMayLikeFragment).adapter = null; } if (((DealsNearMeFragment) mAdapter.mDealsNearMeFragment) != null) { ((DealsNearMeFragment) mAdapter.mDealsNearMeFragment).listDownloader = null; ((DealsNearMeFragment) mAdapter.mDealsNearMeFragment).adapter = null; } if (mAdapter.mFragmentForExploreCategory instanceof ExploreDealResultFragment && mAdapter.mFragmentForExploreCategory != null) { ((ExploreDealResultFragment) mAdapter.mFragmentForExploreCategory).listDownloader = null; ((ExploreDealResultFragment) mAdapter.mFragmentForExploreCategory).adapter = null; } if (((MyDealFragment) mAdapter.mMyDealsFragment) != null) { ((MyDealFragment) mAdapter.mMyDealsFragment).listDownloader = null; ((MyDealFragment) mAdapter.mMyDealsFragment).adapter = null; } if (((MyDealsFragment) mAdapter.mMyDealsFragment) != null) { ((MyDealsFragment) mAdapter.mMyDealsFragment).listDownloader = null; ((MyDealsFragment) mAdapter.mMyDealsFragment).adapter = null; } } } catch (Exception e) { e.printStackTrace(); } finish(); super.onBackPressed(); // ////System.out.println("GreatBuyz: function OUT SampleTabsStyled > exit"); }
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		outState.putBoolean("isSearchVisible", isSearchVisible);
		outState.putBoolean("isMyDealsVisible", isMyDealsVisible);
		outState.putBoolean(dealsByCategoryKey, isCategoryByDealsDisplayed);
		outState.putBoolean(exploreDealsKey, isCategoryAfterExplorDisplayed);
		outState.putInt(selectedPagerItemIndexKey, currentSelectedItem);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		isSearchVisible = savedInstanceState.getBoolean("isSearchVisible");
		isMyDealsVisible = savedInstanceState.getBoolean("isMyDealsVisible");
		if (isSearchVisible)
		{
			String name = Utils.getMessageString(AppConstants.Messages.exploreDeals, R.string.exploreDeals);
			CONTENT.add(name);
			mSearchIndex = CONTENT.size();
		}
		if (isMyDealsVisible)
		{
			String name = getString(R.string.myDeals);// Utils.getMessageString(AppConstants.Messages.myDeals, R.string.myDeals);
			CONTENT.add(name);
			mMyDealIndex = CONTENT.size();
		}
		mAdapter.notifyDataSetChanged();
		mIndicator.notifyDataSetChanged();
		isCategoryByDealsDisplayed = savedInstanceState.getBoolean(dealsByCategoryKey);
		isCategoryAfterExplorDisplayed = savedInstanceState.getBoolean(exploreDealsKey);
		currentSelectedItem = savedInstanceState.getInt(selectedPagerItemIndexKey);
		mIndicator.setCurrentItem(currentSelectedItem);
		startActivity(getIntent());
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		mAdapter = null;
	}
	
	void exitFromApp()
	{
		stopAllDownload();
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					finish();
					GreatBuyzApplication.getApplication().clearAllData();
					GreatBuyzApplication.trimFileCache();
					// FlurryAgent.onEndSession(GreatBuyzApplication.getApplication().getApplicationContext());
					GreatBuyzApplication.getApplication().getAnalyticsAgent().onEndSession(getApplicationContext());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	class GoogleMusicAdapter extends FragmentStatePagerAdapter
	{
		boolean					   isChildDisplayed = false;
		public Fragment			   mExclusiveDealsFragment;
		public Fragment			   mFragmentForCategory;
		public Fragment			   mFragmentForExploreCategory;
		public Fragment			   mFragmentForExploreCoupons;
		public Fragment			   mDealsOfTheDayFragment;
		public Fragment			   mDealsNearMeFragment;
		public Fragment			   mDealsYouMayLikeFragment;
		public Fragment			   mMyDealsFragment;
		// public Fragment mMyDealsTIMFragment;
		public Fragment			   mSurpriseMeFragment;
		private final FragmentManager mFragmentManager;
		
		public GoogleMusicAdapter(FragmentManager fm, boolean isChildDisplayed)
		{
			super(fm);
			// ////System.out.println("GreatBuyz: function IN SampleTabsStyled > GoogleMusicAdapter > GoogleMusicAdapter");
			this.isChildDisplayed = isChildDisplayed;
			mFragmentManager = fm;
			// ////System.out.println("GreatBuyz: function OUT SampleTabsStyled > GoogleMusicAdapter > GoogleMusicAdapter");
		}
		
		@Override
		public Fragment getItem(int position)
		{
			if (mSearchIndex != -1 && position == mSearchIndex - 1)
			{
				position = 5;
			}
			else if (mMyDealIndex != -1 && position == mMyDealIndex - 1)
			{
				position = 6;
			}
			switch (position + 1)
			{
				case AppConstants.FramentConstants.EXCLUSIVE_DEALS:
					mExclusiveDealsFragment = ExclusiveDealsFragment.newInstance("Hemant1", SampleTabsStyled.this, SampleTabsStyled.this);
					mExclusiveDealsFragment.setRetainInstance(true);
					return mExclusiveDealsFragment;
				case AppConstants.FramentConstants.DEALS_OF_THE_DAY:
					mDealsOfTheDayFragment = DealsOfTheDayFragment.newInstance("Hemant2", SampleTabsStyled.this, SampleTabsStyled.this);
					mDealsOfTheDayFragment.setRetainInstance(true);
					return mDealsOfTheDayFragment;
				case AppConstants.FramentConstants.CATEGORY_FRAGMENT:
					if (mFragmentForCategory == null)
					{
						mFragmentForCategory = CategoryListFragment.newInstance("Hemant3", SampleTabsStyled.this, SampleTabsStyled.this, categoryFragmentNavigationListner);
					}
					mFragmentForCategory.setRetainInstance(true);
					return mFragmentForCategory;
					/*
					 * case AppConstants.FramentConstants.DEALS_NEAR_ME: mDealsNearMeFragment = DealsNearMeFragment.newInstance("Hemant4", SampleTabsStyled.this, SampleTabsStyled.this); mDealsNearMeFragment.setRetainInstance(true); return mDealsNearMeFragment;
					 */
				case AppConstants.FramentConstants.DEALS_YOU_MAY_LIKE:
					mDealsYouMayLikeFragment = DealsYouMayLikeFragment.newInstance("Hemant5", SampleTabsStyled.this, SampleTabsStyled.this);
					mDealsYouMayLikeFragment.setRetainInstance(true);
					return mDealsYouMayLikeFragment;
				case AppConstants.FramentConstants.EXPLORE_DEALS:
					if (mFragmentForExploreCategory == null) mFragmentForExploreCategory = ExploreDealsFragment.newInstance("Hemant6", SampleTabsStyled.this, exploreFragmentNavigationListner);
					mFragmentForExploreCategory.setRetainInstance(true);
					return mFragmentForExploreCategory;
					/*
					 * case AppConstants.FramentConstants.MY_DEALS: mMyDealsFragment = MyDealFragment.newInstance("Hemant7", SampleTabsStyled.this, SampleTabsStyled.this); mMyDealsFragment.setRetainInstance(true); return mMyDealsFragment;
					 */
				case AppConstants.FramentConstants.EXPLORE_COUPONS:
					if (mFragmentForExploreCoupons == null) mFragmentForExploreCoupons = ExploreCouponResultFragment.newInstance(SampleTabsStyled.this, "d", "", "AllIndia", "d", SampleTabsStyled.this);
					mFragmentForExploreCoupons.setRetainInstance(true);
					return mFragmentForExploreCoupons;
				case AppConstants.FramentConstants.MY_DEALS:
					mMyDealsFragment = MyDealsFragment.newInstance("Hemant8", SampleTabsStyled.this, SampleTabsStyled.this);
					mMyDealsFragment.setRetainInstance(true);
					return mMyDealsFragment;
				case AppConstants.FramentConstants.SURPRISE_ME:
					mSurpriseMeFragment = SurpriseMeFragment.newInstance("Hemant9", SampleTabsStyled.this, SampleTabsStyled.this);
					mSurpriseMeFragment.setRetainInstance(true);
					return mSurpriseMeFragment;
			}
			// ////System.out.println("GreatBuyz: function OUT SampleTabsStyled > GoogleMusicAdapter > getItem");
			return null;
		}
		
		@Override
		public int getCount()
		{
			return SampleTabsStyled.CONTENT.size();
		}
		
		@Override
		public CharSequence getPageTitle(int position)
		{
			return SampleTabsStyled.CONTENT.get(position % SampleTabsStyled.CONTENT.size()).toUpperCase();
		}
		
		@Override
		public int getItemPosition(Object object)
		{
			if (object instanceof CategoryListFragment && mFragmentForCategory instanceof DealsByCategoryFragment)
				return POSITION_NONE;
			else if (object instanceof DealsByCategoryFragment && mFragmentForCategory instanceof CategoryListFragment)
				return POSITION_NONE;
			else if (object instanceof ExploreDealsFragment && mFragmentForExploreCategory instanceof ExploreDealResultFragment)
				return POSITION_NONE;
			else if (object instanceof ExploreDealResultFragment && mFragmentForExploreCategory instanceof ExploreDealsFragment) return POSITION_NONE;
			return POSITION_UNCHANGED;
		}
	}
	
	@Override
	public void onCategoryClick(int index, final FirstPageFragmentListener fplistener)
	{
		final String category = _data.getCategoriesList().get(index);
		HashMap<String, String> m = new HashMap<String, String>();
		m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.CATEGORY);
		m.put(AppConstants.Flurry.CATEGORY, category);
		// FlurryAgent.logEvent(AppConstants.Flurry.CategoryList, m);
		GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.CategoryList, m);
		String prevCategory = _app.getSelectedCategory();
		if (prevCategory != null && !category.equals(prevCategory)) // reset
																	// data when
																	// new
																	// category
																	// is click
		{
			if (_data.getDealsByCategoriesDTO() != null && _data.getDealsByCategoriesDTO().getDeals() != null)
			{
				_data.getDealsByCategoriesDTO().getDeals().clear();
			}
			_app.setSkipIndexForDealByCategory(0);
		}
		_app.setSelectedCategory(category);
		fplistener.onSwitchToNextFragment(category);
	}
	
	int	 selectedIndex	  = 1000;
	boolean isCategoryChange   = false;
	String  modifiedEmailValue = null;
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args)
	{
		String message = null;
		switch (id)
		{
			case AppConstants.DialogConstants.LOADING_DIALOG:
				LoadingDialog loadingDialog = new LoadingDialog(this, R.style.AlertDialogCustom);
				loadingDialog.setCancelable(false);
				return loadingDialog;
			case AppConstants.DialogConstants.ERROR_DIALOG:
				message = args.getString(AppConstants.JSONKeys.MESSAGE);
				final GenericDialog waitDialog = new GenericDialog(this, R.layout.status_message, R.style.AlertDialogCustom);
				Button waitBtn = (Button) waitDialog.findViewById(R.id.status_btn);
				waitBtn.setTypeface(GreatBuyzApplication.getApplication().getFont());
				waitBtn.setText(Utils.getMessageString(AppConstants.Messages.ok, R.string.ok));
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
							// Log.e("LoginActivity Error message Ok button onClick() ","Application is crashed due to exception --> "
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
			case AppConstants.DialogConstants.FAILED_SEARCH_DIALOG:
				message = args.getString(AppConstants.JSONKeys.MESSAGE);
				final GenericDialog failedDealDialog = new GenericDialog(this, R.layout.status_message, R.style.AlertDialogCustom);
				Button failedDealBtn = (Button) failedDealDialog.findViewById(R.id.status_btn);
				failedDealBtn.setTypeface(GreatBuyzApplication.getApplication().getFont());
				failedDealBtn.setText(Utils.getMessageString(AppConstants.Messages.ok, R.string.ok));
				failedDealBtn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							failedDealDialog.dismiss();
							onBack();
						}
						catch (Exception e)
						{
							// Log.e("LoginActivity Error message Ok button onClick() ","Application is crashed due to exception --> "
							// + e.toString());
							e.printStackTrace();
						}
					}
				});
				TextView failedDealMsgText = (TextView) failedDealDialog.findViewById(R.id.msg);
				failedDealMsgText.setText(message);
				failedDealMsgText.setTypeface(GreatBuyzApplication.getApplication().getFont());
				TextView failedDealtitleText = (TextView) failedDealDialog.findViewById(R.id.title);
				failedDealtitleText.setText(Utils.getMessageString(AppConstants.Messages.titleInfo, R.string.titleInfo));
				failedDealtitleText.setTypeface(GreatBuyzApplication.getApplication().getFont());
				return failedDealDialog;
			case AppConstants.DialogConstants.CHANGE_LOCATION_DIALOG:
			{
				final List<String> loc = _data.getCitiesList();
				String[] locations = loc.toArray(new String[loc.size()]);
				final String prevLocation = _data.getUserCity();
				// System.out.println("prevLocation  ******"+prevLocation);
				int selLoc = loc.indexOf(prevLocation);
				AlertDialog.Builder locationBuilder = new AlertDialog.Builder(this);
				locationBuilder.setTitle(Utils.getMessageString(AppConstants.Messages.settingItemChangeLocation, R.string.settingItemChangeLocation));
				locationBuilder.setSingleChoiceItems(locations, selLoc, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						selectedIndex = which;
					}
				});
				String strOk = Utils.getMessageString(AppConstants.Messages.ok, R.string.ok);
				locationBuilder.setPositiveButton(strOk, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						if (selectedIndex < loc.size())
						{
							if (!loc.get(selectedIndex).equals(prevLocation))
							{
								setUserPreferencesAndUpdateScreen(loc.get(selectedIndex));
							}
						}
					}
				});
				String strCancel = Utils.getMessageString(AppConstants.Messages.cancel, R.string.cancel);
				locationBuilder.setNegativeButton(strCancel, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{}
				});
				return locationBuilder.create();
			}
			case AppConstants.DialogConstants.MESSAGE_DIALOG:
			{
				String message1 = args.getString(AppConstants.JSONKeys.MESSAGE);
				final GenericDialog waitDialog1 = new GenericDialog(this, R.layout.status_message, R.style.AlertDialogCustom);
				Button waitBtn1 = (Button) waitDialog1.findViewById(R.id.status_btn);
				waitBtn1.setText(Utils.getMessageString(AppConstants.Messages.ok, R.string.ok));
				waitBtn1.setTypeface(GreatBuyzApplication.getApplication().getFont());
				waitBtn1.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							removeOtherDialog(AppConstants.DialogConstants.MESSAGE_DIALOG);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
				TextView waitMsgText1 = (TextView) waitDialog1.findViewById(R.id.msg);
				waitMsgText1.setText(message1);
				waitMsgText1.setTypeface(GreatBuyzApplication.getApplication().getFont());
				TextView titleText1 = (TextView) waitDialog1.findViewById(R.id.title);
				titleText1.setText(Utils.getMessageString(AppConstants.Messages.titleInfo, R.string.titleInfo));
				titleText1.setTypeface(GreatBuyzApplication.getApplication().getFont());
				return waitDialog1;
			}
			case AppConstants.DialogConstants.EXIT_DIALOG:
			{
				if (args.containsKey(AppConstants.JSONKeys.MESSAGE)) message = args.getString(AppConstants.JSONKeys.MESSAGE);
				final GenericDialog dialog = new GenericDialog(SampleTabsStyled.this, R.layout.exit_message, R.style.AlertDialogCustom);
				dialog.setCancelable(false);
				Button btn = (Button) dialog.findViewById(R.id.yes_btn);
				btn.setTypeface(GreatBuyzApplication.getApplication().getFont());
				btn.setText(Utils.getMessageString(AppConstants.Messages.yes, R.string.yes));
				btn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							removeOtherDialog(AppConstants.DialogConstants.EXIT_DIALOG);
							exitFromApp();
							// exit();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
				Button noBtn = (Button) dialog.findViewById(R.id.no_btn);
				noBtn.setTypeface(GreatBuyzApplication.getApplication().getFont());
				noBtn.setText(Utils.getMessageString(AppConstants.Messages.no, R.string.no));
				noBtn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							removeOtherDialog(AppConstants.DialogConstants.EXIT_DIALOG);
							closeMenu();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
				TextView msgText = (TextView) dialog.findViewById(R.id.msg);
				msgText.setText(message);
				msgText.setTypeface(_app.getFont());
				TextView titleText1 = (TextView) dialog.findViewById(R.id.title);
				titleText1.setText(AppConstants.EMPTY_STRING);
				titleText1.setTypeface(_app.getFont());
				return dialog;
			}
			case AppConstants.DialogConstants.CHANGE_SETTINGS_LOCATION_DIALOG:
			{
				final List<String> loc = _data.getCitiesList();
				String[] locations = loc.toArray(new String[loc.size()]);
				final String prevLocation = _data.getUserCity();
				// System.out.println("prevLocation 222222"+prevLocation);
				int selLoc = loc.indexOf(prevLocation);
				AlertDialog.Builder locationBuilder = new AlertDialog.Builder(this);
				locationBuilder.setTitle(Utils.getMessageString(AppConstants.Messages.settingItemChangeLocation, R.string.settingItemChangeLocation));
				locationBuilder.setSingleChoiceItems(locations, selLoc, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						selectedIndex = which;
					}
				});
				String strOk = Utils.getMessageString(AppConstants.Messages.ok, R.string.ok);
				locationBuilder.setPositiveButton(strOk, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						if (selectedIndex < loc.size())
						{
							if (!loc.get(selectedIndex).equals(prevLocation))
							{
								System.out.println("loc.get(selectedIndex) **" + loc.get(selectedIndex));
								setUserPreferencesAndUpdateScreen(loc.get(selectedIndex));
								removeOtherDialog(AppConstants.DialogConstants.CHANGE_SETTINGS_LOCATION_DIALOG);
							}
						}
						closeMenu();
					}
				});
				String strCancel = Utils.getMessageString(AppConstants.Messages.cancel, R.string.cancel);
				locationBuilder.setNegativeButton(strCancel, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						closeMenu();
						removeOtherDialog(AppConstants.DialogConstants.CHANGE_SETTINGS_LOCATION_DIALOG);
					}
				});
				AlertDialog lad = locationBuilder.create();
				// lad.setCancelable(false);
				lad.setCanceledOnTouchOutside(false);
				return lad;
			}
			case AppConstants.DialogConstants.CATEGORY_PREF_DIALOG:
			{
				final List<Integer> selectedCategoryIndices = new ArrayList<Integer>();
				selectedCategoryIndices.clear();
				final List<Integer> selectedUnselectedCategories = new ArrayList<Integer>();
				selectedUnselectedCategories.clear();
				List<String> cat = _data.getCategoriesList();
				selection = new boolean[cat.size()];
				cloneSelection = new boolean[cat.size()];
				Cursor c = _data.getCategoriesCursor();
				int i = 0;
				while (c.moveToNext())
				{
					selection[i] = false;
					cloneSelection[i] = false;
					if (c.getInt(1) > 0)
					{
						cloneSelection[i] = true;
						selection[i] = true;
						selectedCategoryIndices.add(i);
					}
					i++;
				}
				c.close();
				String[] cats = cat.toArray(new String[cat.size()]);
				AlertDialog.Builder catBuilder = new AlertDialog.Builder(this);
				catBuilder.setTitle(Utils.getMessageString(AppConstants.Messages.titleCategoryPreferences, R.string.titleCategoryPreferences));
				catBuilder.setMultiChoiceItems(cats, selection, new DialogInterface.OnMultiChoiceClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked)
					{
						if (!selectedUnselectedCategories.contains(which))
						{
							selectedUnselectedCategories.add(which);
						}
						isCategoryChange = true;
						selection[which] = isChecked;
						if (isChecked)
						{
							if (!selectedCategoryIndices.contains(Integer.valueOf(which)) && isChecked)
							{
								selectedCategoryIndices.add(Integer.valueOf(which));
							}
						}
						else if (!isChecked)
						{
							if (selectedCategoryIndices.contains(Integer.valueOf(which)))
							{
								if (selectedCategoryIndices.size() < 2)
								{
									((AlertDialog) dialog).getListView().setItemChecked(which, true);
									Bundle b = new Bundle();
									b.putString(AppConstants.JSONKeys.MESSAGE, Utils.getMessageString(AppConstants.Messages.atleastOneCategoryMessage, R.string.atleastOneCategoryMessage));
									showOtherDialog(AppConstants.DialogConstants.MESSAGE_DIALOG, b);
								}
								else
									selectedCategoryIndices.remove(Integer.valueOf(which));
							}
						}
					}
				});
				String strOk = Utils.getMessageString(AppConstants.Messages.ok, R.string.ok);
				catBuilder.setPositiveButton(strOk, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						if (isCategoryChange && selectedCategoryIndices.size() > 0 && Utils.compareTwoArray(selection, cloneSelection))
						{
							setUserPreferencesCategories(selection.clone(), selectedUnselectedCategories);
							closeMenu();
							updateCurrentTab();
						}
						removeOtherDialog(AppConstants.DialogConstants.CATEGORY_PREF_DIALOG);
					}
				});
				String strCancel = Utils.getMessageString(AppConstants.Messages.cancel, R.string.cancel);
				catBuilder.setNegativeButton(strCancel, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						removeOtherDialog(AppConstants.DialogConstants.CATEGORY_PREF_DIALOG);
						closeMenu();
					}
				});
				return catBuilder.create();
			}
			case AppConstants.DialogConstants.UNSUBSCRIBE_DIALOG:
			{
				message = Utils.getMessageString(AppConstants.Messages.titleUnsubscribeMessage, R.string.titleUnsubscribeMessage);
				final GenericDialog dialog1 = new GenericDialog(this, R.layout.exit_message, R.style.AlertDialogCustom);
				dialog1.setCancelable(false);
				Button btn1 = (Button) dialog1.findViewById(R.id.yes_btn);
				btn1.setText(Utils.getMessageString(AppConstants.Messages.yes, R.string.yes));
				btn1.setTypeface(GreatBuyzApplication.getApplication().getFont());
				btn1.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							removeOtherDialog(AppConstants.DialogConstants.UNSUBSCRIBE_DIALOG);
							unsubscribe(_data.getMDN(), AppConstants.JSONKeys.GCM, AppConstants.JSONKeys.WAP);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
				Button noBtn = (Button) dialog1.findViewById(R.id.no_btn);
				noBtn.setTypeface(GreatBuyzApplication.getApplication().getFont());
				noBtn.setText(Utils.getMessageString(AppConstants.Messages.no, R.string.no));
				noBtn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							removeOtherDialog(AppConstants.DialogConstants.UNSUBSCRIBE_DIALOG);
							closeMenu();
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
			case AppConstants.DialogConstants.UNSUBSCRIBE_PENDING_DIALOG:
			{
				message = args.getString(AppConstants.JSONKeys.MESSAGE);
				final GenericDialog dialog1 = new GenericDialog(this, R.layout.exit_message, R.style.AlertDialogCustom);
				dialog1.setCancelable(false);
				Button btn1 = (Button) dialog1.findViewById(R.id.yes_btn);
				btn1.setText(Utils.getMessageString(AppConstants.Messages.ok, R.string.ok));
				btn1.setTypeface(GreatBuyzApplication.getApplication().getFont());
				btn1.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							removeOtherDialog(AppConstants.DialogConstants.UNSUBSCRIBE_PENDING_DIALOG);
							exitFromApp();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
				Button noBtn = (Button) dialog1.findViewById(R.id.no_btn);
				noBtn.setTypeface(GreatBuyzApplication.getApplication().getFont());
				noBtn.setText(getString(R.string.cancel));
				noBtn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							removeOtherDialog(AppConstants.DialogConstants.UNSUBSCRIBE_PENDING_DIALOG);
							closeMenu();
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
			case AppConstants.DialogConstants.EMAIL_DIALOG:
			{
				final AlertDialog.Builder emailDialogBuilder = new AlertDialog.Builder(this);
				LayoutInflater inflater = getLayoutInflater();
				View view = inflater.inflate(R.layout.email_config, null);
				emailDialogBuilder.setView(view);
				String strOk = Utils.getMessageString(AppConstants.Messages.ok, R.string.ok);
				String strCancel = Utils.getMessageString(AppConstants.Messages.cancel, R.string.cancel);
				emailDialogBuilder.setCancelable(false);
				final AlertDialog emailDialog = emailDialogBuilder.create();
				EditText emailModifInit = (EditText) view.findViewById(R.id.email_input);
				emailModifInit.setTypeface(GreatBuyzApplication.getApplication().getFont());
				emailModifInit.setText(emailId);
				emailDialog.setButton(Dialog.BUTTON_NEUTRAL, strOk, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						EditText emailModif = (EditText) emailDialog.findViewById(R.id.email_input);
						emailModif.setTypeface(GreatBuyzApplication.getApplication().getFont());
						modifiedEmailValue = emailModif.getText().toString();
						if (modifiedEmailValue != null && emailId != null && !modifiedEmailValue.equals(AppConstants.EMPTY_STRING) && modifiedEmailValue.equals(emailId))
						{
							closeMenu();
							return;
						}
						if (modifiedEmailValue == null) modifiedEmailValue = AppConstants.EMPTY_STRING;
						setUserPreferencesEmailId(modifiedEmailValue);
					}
				});
				emailDialog.setButton(Dialog.BUTTON_NEGATIVE, strCancel, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						try
						{
							removeOtherDialog(AppConstants.DialogConstants.EMAIL_DIALOG);
							closeMenu();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
				return emailDialog;
			}
			case AppConstants.DialogConstants.KEYWORDS_DIALOG:
		}
		return super.onCreateDialog(id, args);
	}
	
	@Override
	public void onDealItemClick(int index)
	{
		// ////System.out.println("GreatBuyz: function IN SampleTabsStyled > onDealItemClick");
		HashMap<String, String> m = new HashMap<String, String>();
		m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.DealDetails);
		// FlurryAgent.logEvent(AppConstants.Flurry.DealItem, m);
		GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.DealItem, m);
		// ////System.out.println("GreatBuyz: function OUT SampleTabsStyled > onDealItemClick");
	}
	
	public FirstPageFragmentListener getCategoryFragmentNavigationListner()
	{
		return categoryFragmentNavigationListner;
	}
	
	FirstPageFragmentListener   categoryFragmentNavigationListner = new FirstPageFragmentListener()
																  {
																	  @Override
																	  public void onSwitchToPrevFragment()
																	  {
																		  runOnUiThread(new Runnable()
																		  {
																			  public void run()
																			  {
																				  try
																				  {
																					  isCategoryByDealsDisplayed = false;
																					  CONTENT.set(categoryListFragmentPosition, categoryListTabName);
																					  mIndicator.notifyDataSetChanged();
																					  imgBack.setVisibility(View.INVISIBLE);
																					  mBackNavigation.setClickable(false);
																					  if (mAdapter.mFragmentForCategory != null && mAdapter.mFragmentForCategory instanceof DealsByCategoryFragment)
																					  {
																						  ((DealsByCategoryFragment) mAdapter.mFragmentForCategory).freeImageLoader();
																						  mAdapter.mFragmentForCategory = null;
																						  // System.out.println("MEMORY : clearing image loader for category result fragment");
																					  }
																					  // mAdapter.mFragmentManager.beginTransaction().remove(mAdapter.mFragmentForCategory).commit();
																					  mAdapter.mFragmentForCategory = CategoryListFragment.newInstance("Hemant2", SampleTabsStyled.this, SampleTabsStyled.this, categoryFragmentNavigationListner);
																					  mAdapter.notifyDataSetChanged();
																				  }
																				  catch (Exception e)
																				  {
																					  e.printStackTrace();
																				  }
																			  }
																		  });
																	  }
																	  
																	  @Override
																	  public void onSwitchToNextFragment(final String category)
																	  {
																		  runOnUiThread(new Runnable()
																		  {
																			  public void run()
																			  {
																				  try
																				  {
																					  isCategoryByDealsDisplayed = true;
																					  String categoryName = category;
																					  if (category.length() > categoryHeaderLength)
																					  {
																						  try
																						  {
																							  categoryName = category.substring(0, categoryHeaderLength);
																							  categoryName += AppConstants.ELLIPSIS_STRING;
																						  }
																						  catch (Exception e)
																						  {}
																					  }
																					  CONTENT.set(categoryListFragmentPosition, categoryName);
																					  mIndicator.notifyDataSetChanged();
																					  imgBack.setVisibility(View.VISIBLE);
																					  mBackNavigation.setClickable(true);
																					  mAdapter.mFragmentForCategory = DealsByCategoryFragment.newInstance(category, SampleTabsStyled.this, categoryFragmentNavigationListner);
																					  mAdapter.notifyDataSetChanged();
																				  }
																				  catch (Exception e)
																				  {
																					  e.printStackTrace();
																				  }
																			  }
																		  });
																	  }
																  };
	ExplorePageFragmentListener exploreFragmentNavigationListner  = new ExplorePageFragmentListener()
																  {
																	  @Override
																	  public void onSwitchToPrevFragment()
																	  {
																		  System.out.println("onSwitchToPrevFragment called **********");
																		  isCategoryAfterExplorDisplayed = false;
																		  imgBack.setVisibility(View.INVISIBLE);
																		  mBackNavigation.setClickable(false);
																		  mAdapter.mFragmentForExploreCategory = ExploreDealsFragment.newInstance("", SampleTabsStyled.this, exploreFragmentNavigationListner);
																		  mAdapter.notifyDataSetChanged();
																	  }
																	  
																	  @Override
																	  public void onSwitchToNextFragment(String category, String city, String locality, String keyword)
																	  {
																		  System.out.println("onSwitchToNextFragment called in samepletab********");
																		  isCategoryAfterExplorDisplayed = true;
																		  imgBack.setVisibility(View.VISIBLE);
																		  mBackNavigation.setClickable(true);
																		  mAdapter.mFragmentForExploreCategory = ExploreDealResultFragment.newInstance(SampleTabsStyled.this, keyword, category, city, locality, SampleTabsStyled.this);
																		  mAdapter.notifyDataSetChanged();
																	  }
																  };
	
	public void setUserPreferencesAndUpdateScreen(final String selectedCity)
	{
		try
		{
			showDialog(AppConstants.DialogConstants.LOADING_DIALOG, null);
			GreatBuyzApplication.getServiceDelegate().updateUserInfo(new String[] { selectedCity }, null, null, null, null, -2, null, null, null, null, new IOperationListener()
			{
				@Override
				public void onOperationCompleted(boolean pOperationComplitionStatus, String pMessageFromServer)
				{
					removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					if (pOperationComplitionStatus)
					{
						Utils.changeCitySettings(selectedCity);
						resetDataAndUpdateUI(selectedCity);
					}
					else
					{
						Bundle b = new Bundle();
						b.putString(AppConstants.JSONKeys.MESSAGE, pMessageFromServer);
						showDialog(AppConstants.DialogConstants.ERROR_DIALOG, b);
					}
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
			removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
		}
	}
	
	public void resetDataAndUpdateUI(String selectedCity)
	{
		_app.clearAllDataOnCityChange();
		_data.updateUserCity(selectedCity);
		finish();
		startActivity(getIntent());
	}
	
	public void updateCurrentTab()
	{
		getFragmentToUpdate(mIndicator.getCurrentItem());
	}
	
	private void getFragmentToUpdate(int position)
	{
		System.out.println("position **" + position);
		switch (position + 1)
		{
			case AppConstants.FramentConstants.EXCLUSIVE_DEALS:
				((ExclusiveDealsFragment) mAdapter.mExclusiveDealsFragment).refreshFragment();
				mAdapter.notifyDataSetChanged();
				break;
			case AppConstants.FramentConstants.DEALS_OF_THE_DAY:
				((DealsOfTheDayFragment) mAdapter.mDealsOfTheDayFragment).refreshFragment();
				mAdapter.notifyDataSetChanged();
				break;
			case AppConstants.FramentConstants.CATEGORY_FRAGMENT:
				if (isCategoryByDealsDisplayed)
				{
					((DealsByCategoryFragment) mAdapter.mFragmentForCategory).refreshFragment();
					mAdapter.notifyDataSetChanged();
				}
				break;
			case AppConstants.FramentConstants.DEALS_YOU_MAY_LIKE:
				((DealsYouMayLikeFragment) mAdapter.mDealsYouMayLikeFragment).refreshFragment();
				mAdapter.notifyDataSetChanged();
				break;
			/*
			 * case AppConstants.FramentConstants.DEALS_NEAR_ME: ((DealsNearMeFragment) mAdapter.mDealsNearMeFragment).refreshFragment(); mAdapter.notifyDataSetChanged(); break;
			 */
			/*
			 * case AppConstants.FramentConstants.MY_DEALS: ((MyDealFragment) mAdapter.mMyDealsFragment).refreshFragment(); mAdapter.notifyDataSetChanged(); break;
			 */
			case AppConstants.FramentConstants.EXPLORE_COUPONS:
				((ExploreCouponResultFragment) mAdapter.mFragmentForExploreCoupons).refreshFragment();
				mAdapter.notifyDataSetChanged();
				break;
			case AppConstants.FramentConstants.EXPLORE_DEALS:
			case AppConstants.FramentConstants.MY_DEALS:
				if (((MyDealsFragment) mAdapter.mMyDealsFragment) != null) ((MyDealsFragment) mAdapter.mMyDealsFragment).refreshFragment();
				/* for AppConstants.FramentConstants.EXPLORE_DEALS */
				if (isCategoryAfterExplorDisplayed)
				{
					if (((ExploreDealResultFragment) mAdapter.mFragmentForExploreCategory) != null) ((ExploreDealResultFragment) mAdapter.mFragmentForExploreCategory).refreshFragment();
				}
				else
				{
					if (((ExploreDealsFragment) mAdapter.mFragmentForExploreCategory) != null) ((ExploreDealsFragment) mAdapter.mFragmentForExploreCategory).refreshFragment();
				}
				mAdapter.notifyDataSetChanged();
				break;
		}
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0)
	{}
	
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{}
	
	@Override
	public void onPageSelected(int index)
	{
		mSelectedTabIndex = index;
		if (index == 0)
		{
			mBtnPrevTab.setEnabled(false);
			// mBtnPrevTab.setVisibility(View.INVISIBLE);
		}
		else
		{
			// mBtnPrevTab.setVisibility(View.VISIBLE);
			mBtnPrevTab.setEnabled(true);
		}
		if (index == mAdapter.getCount() - 1)
		{
			mBtnNextTab.setEnabled(false);
			// mBtnNextTab.setVisibility(View.INVISIBLE);
		}
		else
		{
			// mBtnNextTab.setVisibility(View.VISIBLE);
			mBtnNextTab.setEnabled(true);
		}
		// ////System.out.println("GreatBuyz: function IN SampleTabsStyled > onPageSelected");
		// hide back button
		if ((index + 1 == AppConstants.FramentConstants.EXPLORE_DEALS && isCategoryAfterExplorDisplayed) || (index + 1 == AppConstants.FramentConstants.CATEGORY_FRAGMENT && mAdapter.mFragmentForCategory instanceof DealsByCategoryFragment))
		{
			imgBack.setVisibility(View.VISIBLE);
			mBackNavigation.setClickable(true);
		}
		else
		{
			imgBack.setVisibility(View.INVISIBLE);
			mBackNavigation.setClickable(false);
		}
		// added to hide soft keyboard when page is not selected
		if (mAdapter != null && mAdapter.mFragmentForExploreCategory instanceof ExploreDealsFragment)
		{
			((ExploreDealsFragment) mAdapter.mFragmentForExploreCategory).hideKeyboard();
			((ExploreDealsFragment) mAdapter.mFragmentForExploreCategory).hideErrorPopUp();
		}
		if (mAdapter != null && index + 1 == AppConstants.FramentConstants.SURPRISE_ME)
		{
			if (mAdapter.mSurpriseMeFragment != null) ((SurpriseMeFragment) mAdapter.mSurpriseMeFragment).startSensor();
		}
		else
		{
			if (mAdapter.mSurpriseMeFragment != null) ((SurpriseMeFragment) mAdapter.mSurpriseMeFragment).stopSensor();
		}
		// ////System.out.println("GreatBuyz: function OUT SampleTabsStyled > onPageSelected");
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
	
	private void removeOtherDialog(final int which)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					removeDialog(which);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	public void resetDataAndUpdateUI(String loc, List<String> cats, String emailId)
	{
		// ////System.out.println("GreatBuyz: function IN SampleTabsStyled > resetDataAndUpdateUI");
		GreatBuyzApplication.getApplication().clearAllDataOnCityChange();
		if (loc != null) _data.updateUserCity(loc);
		if (cats != null)
		{
			_data.clearCategoriesSelectedStatus();
			_data.setCategoriesSelectedStatus(cats);
		}
		if (emailId != null) _data.updateEmail(emailId);
		// ////System.out.println("GreatBuyz: function OUT SampleTabsStyled > resetDataAndUpdateUI");
	}
	
	public void setUserPreferencesCategories(final boolean[] selection, final List<Integer> selectedUnselectedCategories)
	{
		try
		{
			showDialog(AppConstants.DialogConstants.LOADING_DIALOG, null);
			final List<String> categories = new ArrayList<String>();
			for (int i = 0; i < selection.length; i++)
			{
				if (selection[i])
				{
					String s = _data.getCategoriesList().get(i);
					categories.add(s);
				}
			}
			String[] cats = null;
			if (categories != null && categories.size() > 0)
			{
				cats = categories.toArray(new String[categories.size()]);
			}
			GreatBuyzApplication.getServiceDelegate().updateUserInfo(null, cats, null, null, null, -2, null, null, null, null, new IOperationListener()
			{
				@Override
				public void onOperationCompleted(boolean pOperationComplitionStatus, String pMessageFromServer)
				{
					removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					if (pOperationComplitionStatus)
					{
						isCategoryChange = false;
						resetDataAndUpdateUI(null, categories, null);
						List<String> dbcategories = _data.getCategoriesList();
						Utils.changeCategorySettings(dbcategories, selection, selectedUnselectedCategories);
					}
					else
					{
						Bundle b = new Bundle();
						b.putString(AppConstants.JSONKeys.MESSAGE, pMessageFromServer);
						showDialog(AppConstants.DialogConstants.ERROR_DIALOG, b);
					}
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
			removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
		}
	}
	
	public void setUserPreferencesEmailId(final String emailId)
	{
		try
		{
			if (!Utils.isEmailValid(emailId))
			{
				Bundle b = new Bundle();
				b.putString(AppConstants.JSONKeys.MESSAGE, getResources().getString(R.string.invalidEmailMessage));
				showDialog(AppConstants.DialogConstants.MESSAGE_DIALOG, b);
				return;
			}
			try
			{
				removeOtherDialog(AppConstants.DialogConstants.EMAIL_DIALOG);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			showDialog(AppConstants.DialogConstants.LOADING_DIALOG, null);
			GreatBuyzApplication.getServiceDelegate().updateUserInfo(null, null, emailId, null, null, -2, null, null, null, null, new IOperationListener()
			{
				@Override
				public void onOperationCompleted(boolean pOperationComplitionStatus, String pMessageFromServer)
				{
					removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					if (pOperationComplitionStatus)
					{
						resetDataAndUpdateUI(null, null, emailId);
					}
					else
					{
						Bundle b = new Bundle();
						b.putString(AppConstants.JSONKeys.MESSAGE, pMessageFromServer);
						showOtherDialog(AppConstants.DialogConstants.MESSAGE_DIALOG, b);
					}
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
			removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
		}
	}
	
	public void sendKeywordsToServer(final String[] keywords)
	{
		try
		{
			showDialog(AppConstants.DialogConstants.LOADING_DIALOG, null);
			GreatBuyzApplication.getServiceDelegate().updateUserInfo(null, null, null, null, keywords, -2, null, null, null, null, new IOperationListener()
			{
				@Override
				public void onOperationCompleted(boolean pOperationComplitionStatus, String pMessageFromServer)
				{
					removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					if (pOperationComplitionStatus)
					{
						Bundle b = new Bundle();
						b.putString(AppConstants.JSONKeys.MESSAGE, Utils.getMessageString(AppConstants.Messages.keywordsSentMessage, R.string.keywordsSentMessage));
						showOtherDialog(AppConstants.DialogConstants.MESSAGE_DIALOG, b);
					}
					else
					{
						Bundle b = new Bundle();
						b.putString(AppConstants.JSONKeys.MESSAGE, Utils.getMessageString(AppConstants.Messages.keywordsFailedMessage, R.string.keywordsFailedMessage));
						showOtherDialog(AppConstants.DialogConstants.MESSAGE_DIALOG, b);
					}
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
			removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
		}
	}
	
	public void unsubscribe(final String mdn, final String channel, final String chargingMode)
	{
		showDialog(AppConstants.DialogConstants.LOADING_DIALOG);
		try
		{
			GreatBuyzApplication.getServiceDelegate().unsubscribeFromChannel(mdn, channel, chargingMode, new IOperationListener()
			{
				@Override
				public void onOperationCompleted(boolean pOperationComplitionStatus, String pMessageFromServer)
				{
					removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					if (pOperationComplitionStatus)
					{
						HashMap<String, String> m = new HashMap<String, String>();
						m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.Unsubscribe);
						m.put(AppConstants.Flurry.MDN, mdn);
						m.put(AppConstants.Flurry.STATUS, AppConstants.Flurry.SUCCESS);
						// FlurryAgent.logEvent(AppConstants.Flurry.CategoryList,
						// m);
						GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.Unsubscribe, m);
						exitFromApp();
						// exit();
					}
					else
					{
						HashMap<String, String> m = new HashMap<String, String>();
						m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.Unsubscribe);
						m.put(AppConstants.Flurry.MDN, mdn);
						m.put(AppConstants.Flurry.STATUS, AppConstants.Flurry.PENDING);
						// FlurryAgent.logEvent(AppConstants.Flurry.CategoryList,
						// m);
						GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.Unsubscribe, m);
						_data.updateIsUserSubscribedToGCM(true);
						Bundle b = new Bundle();
						b.putString(AppConstants.JSONKeys.MESSAGE, getResources().getString(R.string.unsubscription_status_pending));
						showOtherDialog(AppConstants.DialogConstants.UNSUBSCRIBE_PENDING_DIALOG, b);
					}
				}
			});
		}
		catch (Exception e)
		{
			removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			HashMap<String, String> m = new HashMap<String, String>();
			m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.Unsubscribe);
			m.put(AppConstants.Flurry.MDN, mdn);
			m.put(AppConstants.Flurry.STATUS, AppConstants.Flurry.PENDING);
			// FlurryAgent.logEvent(AppConstants.Flurry.CategoryList, m);
			GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.Unsubscribe, m);
			_data.updateIsUserSubscribedToGCM(true);
			Bundle b = new Bundle();
			b.putString(AppConstants.JSONKeys.MESSAGE, getResources().getString(R.string.unsubscription_status_pending));
			showOtherDialog(AppConstants.DialogConstants.UNSUBSCRIBE_PENDING_DIALOG, b);
			e.printStackTrace();
		}
	}
	
	public void sendNotificationFrequencyToServer(final int frequency, final String isDailyMsgEnabled)
	{
		try
		{
			showDialog(AppConstants.DialogConstants.LOADING_DIALOG, null);
			GreatBuyzApplication.getServiceDelegate().updateUserInfo(null, null, null, null, null, frequency, null, null, null, isDailyMsgEnabled, new IOperationListener()
			{
				@Override
				public void onOperationCompleted(boolean p_OperationComplitionStatus, String p_MessageFromServer)
				{
					removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					// hideNotificationFrequencyScreen();
					if (p_OperationComplitionStatus)
					{
						_data.updateNotificationFrequency(frequency);
						Bundle b = new Bundle();
						b.putString(AppConstants.JSONKeys.MESSAGE, getResources().getString(R.string.notificationSentMessage));
						showOtherDialog(AppConstants.DialogConstants.MESSAGE_DIALOG, b);
						Utils.changeFrequencyLog(frequency);
					}
					else
					{
						Bundle b = new Bundle();
						b.putString(AppConstants.JSONKeys.MESSAGE, getResources().getString(R.string.notificationFailedMessage));
						showOtherDialog(AppConstants.DialogConstants.MESSAGE_DIALOG, b);
					}
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
			removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			// hideNotificationFrequencyScreen();
			Bundle b = new Bundle();
			b.putString(AppConstants.JSONKeys.MESSAGE, getResources().getString(R.string.notificationFailedMessage));
			showOtherDialog(AppConstants.DialogConstants.MESSAGE_DIALOG, b);
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		return super.onPrepareOptionsMenu(menu);
	}
	
	public class DrawerItemClickListener implements ListView.OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			selectItem(position);
		}
	}
	
	private class ChildDrawerItemClickListener implements ListView.OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			selectChildItem(position);
		}
	}
	
	private void selectChildItem(int position)
	{
		mDrawerListChild.setItemChecked(position, true);
		setTitle(settingSubListItems.get(position).name);
		mDrawerLayoutChild.closeDrawer(Gravity.RIGHT);
		mIsChildDrawerOpen = false;
		int id = settingSubListItems.get(position).id;
		switch (id)
		{
			case AppConstants.SettingItems.LOCATION:
				showDialog(AppConstants.DialogConstants.CHANGE_SETTINGS_LOCATION_DIALOG, null);
				break;
			case AppConstants.SettingItems.CATEGORIES:
				showDialog(AppConstants.DialogConstants.CATEGORY_PREF_DIALOG, null);
				break;
			case AppConstants.SettingItems.UNSUBSCRIBE:
				showDialog(AppConstants.DialogConstants.UNSUBSCRIBE_DIALOG, null);
				break;
			case AppConstants.SettingItems.TNC:
				closeMenu();
				String TNC_URL = GreatBuyzApplication.getApplication().getTNCURL();
				showWebView(Utils.getMessageString(AppConstants.Messages.termsTitle, R.string.termsTitle), TNC_URL, false);
				break;
			case AppConstants.SettingItems.FAQ:
				closeMenu();
				String FAQ_URL = GreatBuyzApplication.getApplication().getFAQURL();
				showWebView(Utils.getMessageString(AppConstants.Messages.faqTitle, R.string.faqTitle), FAQ_URL, false);
				break;
			case AppConstants.SettingItems.ABOUT:
				closeMenu();
				String ABOUT_URL = GreatBuyzApplication.getApplication().getAboutURL();
				showWebView(Utils.getMessageString(AppConstants.Messages.menuAbout, R.string.menuAbout), ABOUT_URL, false);
				break;
		}
	}
	
	private void selectItem(int position)
	{
		mDrawerList.setItemChecked(position, true);
		int id = settingItems.get(position).id;
		mDrawerListChild.setVisibility(View.GONE);
		mNotificationLayout.setVisibility(View.GONE);
		mVersionLayout.setVisibility(View.GONE);
		settingChildListTitle.setText(settingItems.get(position).name);
		settingChildListTitle.setCompoundDrawablesWithIntrinsicBounds(settingItems.get(position).icon, null, null, null);
		switch (id)
		{
			case AppConstants.SettingItems.PERSONAL_PROFILE:
				createSettingsSubView(id);
				populateSettingsSubMenu();
				mDrawerListChild.setVisibility(View.VISIBLE);
				mDrawerLayoutChild.openDrawer(mDrawerLayoutChildView);
				mDrawerLayoutChild.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				mIsChildDrawerOpen = true;
				mDrawerList.setItemChecked(position, true);
				setTitle(settingItems.get(position).name);
				break;
			case AppConstants.SettingItems.NOTIFICATIONS:
				// int frequencyIndex = _data.getNotificationFrequencyIndex();
				String isDailyMsgEnable = _data.isDailyMsgEnabled();
				if (isDailyMsgEnable != null)
				{
					if (isDailyMsgEnable.equalsIgnoreCase("true"))
					{
						mDailyMsgCheckBox.setChecked(true);
					}
					else
					{
						mDailyMsgCheckBox.setChecked(false);
					}
				}
				/*
				 * if (frequencyIndex < 0) { mInappCheckBox.setChecked(false); setNotificationSpinnerVisibility(false); } else { mInappCheckBox.setChecked(true); mNotificationSpinner.setSelection(frequencyIndex); setNotificationSpinnerVisibility(true); }
				 */
				mNotificationLayout.setVisibility(View.VISIBLE);
				mNotificationLayout.setClickable(true);
				mDrawerLayoutChild.openDrawer(mDrawerLayoutChildView);
				mDrawerLayoutChild.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				mIsChildDrawerOpen = true;
				mDrawerList.setItemChecked(position, true);
				setTitle(settingItems.get(position).name);
				break;
			case AppConstants.SettingItems.EMAIL:
				emailId = _data.getEmailId();
				showDialog(AppConstants.DialogConstants.EMAIL_DIALOG, null);
				break;
			case AppConstants.SettingItems.KEYWORDS:
				Intent intent = new Intent(this, SetAlertsActivity.class);
				closeMenu();
				startActivityForResult(intent, AppConstants.RESULT_SET_ALERT_SCREEN);
				break;
			case AppConstants.SettingItems.MY_COUPON:
				if (mWebViewLayout != null && mWebViewLayout.isShown())
				{
					closeInfoView();
				}
				openMyCouponTab();
				break;
			case AppConstants.SettingItems.SEARCH:
				if (mWebViewLayout != null && mWebViewLayout.isShown())
				{
					closeInfoView();
				}
				openSearchTab();
				break;
			case AppConstants.SettingItems.ABOUT:
				closeMenu();
				String ABOUT_URL = GreatBuyzApplication.getApplication().getAboutURL();
				showWebView(Utils.getMessageString(AppConstants.Messages.menuAbout, R.string.menuAbout), ABOUT_URL, false);
				break;
			case AppConstants.SettingItems.HELP:
				createSettingsSubView(id);
				populateSettingsSubMenu();
				mDrawerListChild.setVisibility(View.VISIBLE);
				// mDrawerLayoutChild.openDrawer(mDrawerListChild);
				mDrawerLayoutChild.openDrawer(mDrawerLayoutChildView);
				mDrawerLayoutChild.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				mIsChildDrawerOpen = true;
				mDrawerList.setItemChecked(position, true);
				setTitle(settingItems.get(position).name);
				break;
			case AppConstants.SettingItems.INFO:
				createSettingsSubView(id);
				populateSettingsSubMenu();
				String versionNumber = Utils.getClientVersion();
				if (!Utils.isNothing(versionNumber))
				{
					TextView versionValue = (TextView) findViewById(R.id.version_value);
					versionValue.setText(versionNumber);
					mVersionLayout.setVisibility(View.VISIBLE);
				}
				mDrawerListChild.setVisibility(View.VISIBLE);
				// mDrawerLayoutChild.openDrawer(mDrawerListChild);
				mDrawerLayoutChild.openDrawer(mDrawerLayoutChildView);
				mDrawerLayoutChild.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				mIsChildDrawerOpen = true;
				mDrawerList.setItemChecked(position, true);
				setTitle(settingItems.get(position).name);
				break;
			case AppConstants.SettingItems.REFRESH:
				closeMenu();
				if (mWebViewLayout != null && mWebViewLayout.isShown())
				{
					showWebView("", "", true);
				}
				else
				{
					updateCurrentTab();
				}
				break;
			default:
				break;
		}
	}
	
	public void openMyCouponTab()
	{
		String name = getString(R.string.myDeals);// Utils.getMessageString(AppConstants.Messages.myDeals, R.string.myDeals);
//		RelativeLayout.LayoutParams paramss = (RelativeLayout.LayoutParams) ivPicRel.getLayoutParams();
//		paramss.addRule(RelativeLayout.CENTER_IN_PARENT);
//		ivPicRel.setLayoutParams(paramss);
		//ivPop.performClick();
		
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//				params.gravity = Gravity.CENTER;
//				ivPop.setLayoutParams(params);
//		mQuickAction.show(ivPop);
//		mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
		
		if (!isMyDealsVisible)
		{
			CONTENT.add(name);
			mMyDealIndex = CONTENT.size();
			isMyDealsVisible = true;
		}
		closeMenu();
		if (mWebViewLayout != null && mWebViewLayout.isShown())
		{
			closeInfoView();
		}
		mAdapter.notifyDataSetChanged();
		mIndicator.setCurrentItem(mMyDealIndex - 1);
		mIndicator.notifyDataSetChanged();
	}
	
	public void openSearchTab()
	{
		String name = Utils.getMessageString(AppConstants.Messages.exploreDeals, R.string.exploreDeals);
		if (!isSearchVisible)
		{
			CONTENT.add(name);
			mSearchIndex = CONTENT.size();
			isSearchVisible = true;
		}
		closeMenu();
		mAdapter.notifyDataSetChanged();
		mIndicator.setCurrentItem(mSearchIndex - 1);
		mIndicator.notifyDataSetChanged();
		if (isCategoryAfterExplorDisplayed && isSearchVisible) exploreFragmentNavigationListner.onSwitchToPrevFragment();
	}
	
	public void closeInfoView()
	{
		mWebViewLayout.setVisibility(View.GONE);
		if (isCategoryByDealsDisplayed && mPager.getCurrentItem() == 2)
		{}
		else
		{
			imgBack.setVisibility(View.INVISIBLE);
			mBackNavigation.setClickable(false);
		}
	}
	
	public void closeMenu()
	{
		if (mIsMainDrawerOpen)
		{
			mDrawerLayout.closeDrawer(Gravity.RIGHT);
			mIsMainDrawerOpen = false;
			mIsChildDrawerOpen = false;
			if (mAdapter != null && mIndicator.getCurrentItem() + 1 == AppConstants.FramentConstants.SURPRISE_ME)
			{
				if (mAdapter.mSurpriseMeFragment != null) ((SurpriseMeFragment) mAdapter.mSurpriseMeFragment).startSensor();
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// //System.out.println(" request & resultcode " + requestCode + " " +
		// resultCode);
		if (requestCode == AppConstants.RESULT_SET_ALERT_SCREEN && resultCode == AppConstants.RESULT_CLICK_MYCOUPON)
		{
			openMyCouponTab();
		}
		else if (requestCode == AppConstants.RESULT_SET_ALERT_SCREEN && resultCode == AppConstants.RESULT_CLICK_SEARCH)
		{
			openSearchTab();
		}
		else if (requestCode == AppConstants.RESULT_DEAL_DETAIL_SCREEN && resultCode == AppConstants.RESULT_CLICK_MYCOUPON)
		{
			openMyCouponTab();
		}
		else if (requestCode == AppConstants.RESULT_DEAL_DETAIL_SCREEN && resultCode == AppConstants.RESULT_CLICK_SEARCH)
		{
			openSearchTab();
		}
		else if (resultCode == AppConstants.RESULT_SHOW_WEBVIEW)
		{
			String title = data.getStringExtra(AppConstants.JSONKeys.NAME);
			String url = data.getStringExtra(AppConstants.SharedPrefKeys.help);
			boolean refresh = data.getBooleanExtra(AppConstants.Messages.menuRefresh, false);
			showWebView(title, url, refresh);
		}
		else if (resultCode == AppConstants.RESULT_EXIT_APP)
		{
			exitFromApp();
		}
	}
	
	@Override
	public void popoverViewWillShow(PopoverView view)
	{
		Log.i("POPOVER", "Will show");
	}
	
	@Override
	public void popoverViewDidShow(PopoverView view)
	{
		Log.i("POPOVER", "Did show");
	}
	
	@Override
	public void popoverViewWillDismiss(PopoverView view)
	{
		Log.i("POPOVER", "Will dismiss");
	}
	
	@Override
	public void popoverViewDidDismiss(PopoverView view)
	{
		Log.i("POPOVER", "Did dismiss");
	}
}
