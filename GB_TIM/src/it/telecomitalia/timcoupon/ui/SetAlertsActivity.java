package it.telecomitalia.timcoupon.ui;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import it.telecomitalia.timcoupon.R;
import it.telecomitalia.timcoupon.R.style;
import it.telecomitalia.timcoupon.data.SettingItem;
import it.telecomitalia.timcoupon.service.DB;
import it.telecomitalia.timcoupon.service.DataController;
import it.telecomitalia.timcoupon.service.IOperationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.onmobile.hcoe.ui.dialog.GenericDialog;
import com.onmobile.hcoe.ui.dialog.LoadingDialog;
import com.onmobile.utils.AppConstants;
import com.onmobile.utils.GreatBuyzTextView;
import com.onmobile.utils.Utils;
import com.onmobile.utils.settingsArrayAdapter;

public class SetAlertsActivity extends Activity
{
	private ImageView menuView;
	private LinearLayout mBackNavigation;
	private MultiAutoCompleteTextView multiAutoTextView;

	private DataController _data;
	List<String> keywordsList;
	Button btnSend;

	private GreatBuyzTextView settingChildListTitle;
	private List<SettingItem> settingItems = null;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private static boolean mIsMainDrawerOpen = false;

	private DrawerLayout mDrawerLayoutChild;
	private RelativeLayout mDrawerLayoutChildView;
	private ListView mDrawerListChild;
	private static boolean mIsChildDrawerOpen = false;
	List<SettingItem> settingSubListItems = null;

	private RelativeLayout mNotificationLayout;
	private LinearLayout mVersionLayout;
	private Button mNotifButton;
	private CheckBox mInappCheckBox;
	private CheckBox mDailyMsgCheckBox;
	private Spinner mNotificationSpinner;
	private TextView mSpinnerText;
	private TextView mNotifSetAlertTextView;
	public boolean[] selection;
	public boolean[] cloneSelection;
	int selectedIndex = 1000;
	boolean isCategoryChange = false;
	String message = null;
	static final ArrayList<String> spinnerValues = Utils.getNotificationFrequencies();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.keywords_entry_view);

		RelativeLayout myDealsShortcut = (RelativeLayout) findViewById(R.id.imgMyDeals);
		ImageView myIcon = (ImageView) findViewById(R.id.myicon);
		SharedPreferences pref = GreatBuyzApplication
				.getApplication().getSharedPreferences();
		boolean isNewWelcomeDeal = pref.getBoolean(DB.COL_VERSION_WELCOME_DEAL, false);
		System.out.println("isNewWelcomeDeal &&&"+isNewWelcomeDeal);
		if(isNewWelcomeDeal){
			myIcon.setImageResource(R.drawable.myone);
		}
		
		myDealsShortcut.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setResult(AppConstants.RESULT_CLICK_MYCOUPON);
				finish();
			}
		});

		_data = GreatBuyzApplication.getDataController();

		updateKeywords();

		menuView = (ImageView) findViewById(R.id.imgMenu);
		menuView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				clickMenuButton();
			}
		});
		menuView.setVisibility(View.VISIBLE);

		mBackNavigation = (LinearLayout) findViewById(R.id.back_navigation);
		mBackNavigation.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onBackPressed();
			}
		});

		InputFilter inputFilter = new InputFilter()
		{
			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend)
			{
				String spChar = " \'-&\"%�?*@#�";
				for (int i = start; i < end; i++)
				{

					if (!Character.isLetterOrDigit(source.charAt(i)))
					{
						if (!(spChar.indexOf(source.charAt(i)) > -1))
						{
							return "";
						}
					}

				}

				return null;
			}
		};

		multiAutoTextView = (MultiAutoCompleteTextView) findViewById(R.id.txt_multi_keywords);
		multiAutoTextView.setTypeface(GreatBuyzApplication.getApplication().getFont());
		multiAutoTextView.setHint(Utils.getMessageString(AppConstants.Messages.keywordHintText, R.string.keywordHintText));
		String strLength = _data.getConstant(AppConstants.Constants.setAlertsKeywordMaxLength);
		int length = -1;
		if (!Utils.isNothing(strLength))
		{
			try
			{
				length = Integer.parseInt(strLength);
			}
			catch (Exception e)
			{
			}
		}
		InputFilter lengthFilter = new InputFilter.LengthFilter(length);
		if(length > -1)
			multiAutoTextView.setFilters(new InputFilter[] { inputFilter, lengthFilter });
		else
			multiAutoTextView.setFilters(new InputFilter[] { inputFilter });
		multiAutoTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		multiAutoTextView.addTextChangedListener(textWatcher);
		multiAutoTextView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> av, View arg1, int index, long arg3)
			{
				String listItem = (String) av.getItemAtPosition(index);
				multiAutoTextView.setText(listItem);

			}

		});

		String strThreshold = _data.getConstant(AppConstants.Constants.keywordCompletionThreshold);
		int threshold = 0;
		try
		{
			threshold = Integer.parseInt(strThreshold);
			if (threshold > 0)
				multiAutoTextView.setThreshold(threshold);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		TextView titleTextView = (TextView) findViewById(R.id.title);
		titleTextView.setText(Utils.getMessageString(AppConstants.Messages.settingItemKeywords, R.string.settingItemKeywords));
		titleTextView.setTypeface(GreatBuyzApplication.getApplication().getFont());

		btnSend = (Button) findViewById(R.id.btn_send);
		btnSend.setEnabled(false);
		btnSend.setTypeface(GreatBuyzApplication.getApplication().getFont());
		Utils.setMessageToButton(btnSend, AppConstants.Messages.btnSetAlertsText);
		btnSend.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String keywordsString = multiAutoTextView.getEditableText().toString().trim();
				String[] keywords = keywordsString.split(",");
				if (keywords != null && keywords.length > 0)
					sendKeywordsToServer(keywords);
			}
		});

		populateSettingsMenu();
		mSpinnerText = (TextView) findViewById(R.id.spinner_text);
		mInappCheckBox = (CheckBox) findViewById(R.id.in_app_notification_checkbox);
		mDailyMsgCheckBox = (CheckBox) findViewById(R.id.dailyMsgCheckBox);
		mNotificationSpinner = (Spinner) findViewById(R.id.notification_spinner);
		ArrayAdapter<String> notifadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerValues);
		notifadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mNotificationSpinner.setAdapter(notifadapter);

		mInappCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				setNotificationSpinnerVisibility(isChecked);
			}
		});
	}

	private void updateKeywords()
	{
		// Check if new keywords version is received
		String newVersion = GreatBuyzApplication.getApplication().getSharedPreferences().getString(DB.COL_VERSION_KEYWORDS, null);
		if (Utils.isNothing(newVersion))
			return;
		String oldVersion = _data.getVersion(DB.COL_VERSION_KEYWORDS);
		if (oldVersion.equals(newVersion))
			return;
		try
		{
			showOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG, null);
			GreatBuyzApplication.getServiceDelegate().getKeywords(new IOperationListener()
			{
				@Override
				public void onOperationCompleted(boolean p_OperationComplitionStatus, String p_MessageFromServer)
				{
					removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					populateKeywords();
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
			removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			populateKeywords();
		}
	}

	private void populateKeywords()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					keywordsList = _data.getKeywordsList();
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(SetAlertsActivity.this, R.layout.keyword_suggestion_item,
							keywordsList);
					multiAutoTextView.setAdapter(adapter);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public void closeMenu()
	{
		if (mIsMainDrawerOpen)
		{
			mDrawerLayout.closeDrawer(Gravity.RIGHT);
			mIsMainDrawerOpen = false;
			mIsChildDrawerOpen = false;
		}
	}

	public void clickMenuButton()
	{
		if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
		{
			closeMenu();
		}
		else
		{
			mDrawerLayout.openDrawer(Gravity.RIGHT);
			mIsMainDrawerOpen = true;
		}

	}

	private void populateSettingsMenu()
	{
		createSettingsView();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		mDrawerList.setAdapter(new settingsArrayAdapter(this, R.layout.drawer_list_item, settingItems));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawerLayout.setDrawerListener(new DrawerListener()
		{

			@Override
			public void onDrawerStateChanged(int arg0)
			{
			}

			@Override
			public void onDrawerSlide(View arg0, float arg1)
			{
			}

			@Override
			public void onDrawerOpened(View arg0)
			{
				mIsMainDrawerOpen = true;
			}

			@Override
			public void onDrawerClosed(View arg0)
			{
				mIsMainDrawerOpen = false;
				if (mDrawerLayoutChild != null)
					mDrawerLayoutChild.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
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
		mNotifButton = (Button) findViewById(R.id.button_notification);
		mNotifSetAlertTextView = (TextView) findViewById(R.id.setalerttextview);
		Utils.setMessageToTextView(mNotifSetAlertTextView, AppConstants.Messages.settingItemKeywords);

		mNotifSetAlertTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				closeMenu();
			}
		});
		mNotifButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int frequency = -1;
				if (mInappCheckBox.isChecked())
				{
					try
					{
						Object selectedItem = mNotificationSpinner.getSelectedItem();
						String strFrequency = null;
						if (selectedItem != null)
							strFrequency = selectedItem.toString();

						frequency = Integer.parseInt(strFrequency);
					}
					catch (Exception e)
					{
					}
				}
				String isDailyMsgEnabled = null;
				if(mDailyMsgCheckBox.isChecked()){
					isDailyMsgEnabled ="true";
				}else{
					isDailyMsgEnabled ="false";
				}
				sendNotificationFrequencyToServer(frequency,isDailyMsgEnabled);
			}
		});
		mDrawerLayoutChildView = (RelativeLayout) findViewById(R.id.child_drawer_layout);
		mDrawerListChild = (ListView) findViewById(R.id.left_drawer_child);
	}

	private void populateSettingsSubMenu()
	{
		mDrawerListChild.setAdapter(new settingsArrayAdapter(this, R.layout.drawer_list_item, settingSubListItems));
		mDrawerListChild.setOnItemClickListener(new ChildDrawerItemClickListener());
	}

	private void createSettingsSubView(int id)
	{

		switch (id)
		{
			case AppConstants.SettingItems.PERSONAL_PROFILE:
				if (settingSubListItems != null && !settingSubListItems.isEmpty())
					settingSubListItems.clear();

				settingSubListItems = new ArrayList<SettingItem>();

				settingSubListItems.add(new SettingItem(AppConstants.SettingItems.CATEGORIES, Utils.getMessageString(
						AppConstants.Messages.settingItemCategoryPreferences, R.string.settingItemCategoryPreferences), this.getResources()
						.getDrawable(R.drawable.setting_category)));
				settingSubListItems.add(new SettingItem(AppConstants.SettingItems.LOCATION, Utils.getMessageString(
						AppConstants.Messages.settingItemChangeLocation, R.string.settingItemChangeLocation), this.getResources()
						.getDrawable(R.drawable.setting_faq)));
				settingSubListItems.add(new SettingItem(AppConstants.SettingItems.UNSUBSCRIBE, Utils.getMessageString(
						AppConstants.Messages.settingItemUnsubscribe, R.string.settingItemUnsubscribe), this.getResources().getDrawable(
						R.drawable.setting_dea)));
				//infoTermsTitle infoPrivacyTitle  infoCostiTitle 
				settingSubListItems.add(new SettingItem(AppConstants.SettingItems.ALL_TNC, Utils.getMessageString(
						AppConstants.Messages.infoTermsTitle, R.string.infoTermsTitle), this.getResources().getDrawable(R.drawable.setting_terms)));
				settingSubListItems.add(new SettingItem(AppConstants.SettingItems.INFO_PRIVACY, Utils.getMessageString(
						AppConstants.Messages.infoPrivacyTitle, R.string.infoPrivacyTitle), this.getResources().getDrawable(R.drawable.setting_privacy)));
				settingSubListItems.add(new SettingItem(AppConstants.SettingItems.INFO_COSTI, Utils.getMessageString(
						AppConstants.Messages.infoCostiTitle, R.string.infoCostiTitle), this.getResources().getDrawable(R.drawable.setting_costi)));
				
				break;
			case AppConstants.SettingItems.INFO:
				if (settingSubListItems != null && !settingSubListItems.isEmpty())
					settingSubListItems.clear();

				settingSubListItems = new ArrayList<SettingItem>();

				settingSubListItems.add(new SettingItem(AppConstants.SettingItems.ABOUT, Utils.getMessageString(
						AppConstants.Messages.menuAbout, R.string.menuAbout), this.getResources().getDrawable(R.drawable.setting_alert)));

				settingSubListItems.add(new SettingItem(AppConstants.SettingItems.FAQ, Utils.getMessageString(
						AppConstants.Messages.menuFAQ, R.string.menuFAQ), this.getResources().getDrawable(R.drawable.setting_faq)));
				break;
			default:
				break;
		}
	}

	private void createSettingsView()
	{
		settingItems = new ArrayList<SettingItem>();
		/* settingItems.add(getResources().getString(R.string.menuSettings)); */
		settingItems.add(new SettingItem(AppConstants.SettingItems.PERSONAL_PROFILE, getResources().getString(
				R.string.settingItemPersonalProfile), this.getResources().getDrawable(R.drawable.setting_personal)));
		/*
		 * settingItems.add(new SettingItem(AppConstants.SettingItems.MY_COUPON,
		 * getResources().getString(R.string.settingItemMyCoupon), this
		 * .getResources().getDrawable(R.drawable.setting_unsubscribe)));
		 */
		settingItems.add(new SettingItem(AppConstants.SettingItems.NOTIFICATIONS, getResources().getString(
				R.string.settingItemNotifications), this.getResources().getDrawable(R.drawable.setting_faq)));
		settingItems.add(new SettingItem(AppConstants.SettingItems.SEARCH, getResources().getString(R.string.settingItemSearch), this
				.getResources().getDrawable(R.drawable.setting_search)));
		boolean getEmailIdFromUser = false;
		String strGetEmail = _data.getConstant(AppConstants.Constants.getEmailIdFromUser);
		if (!Utils.isNothing(strGetEmail))
		{
			try
			{
				getEmailIdFromUser = Boolean.parseBoolean(strGetEmail);
			}
			catch (Exception e)
			{
			}
		}

		if (getEmailIdFromUser)
			settingItems.add(new SettingItem(AppConstants.SettingItems.EMAIL, getResources().getString(R.string.settingItemEmail), null));

		settingItems.add(new SettingItem(AppConstants.SettingItems.INFO, getResources().getString(R.string.menuInfo), this.getResources()
				.getDrawable(R.drawable.setting_info)));

		settingItems.add(new SettingItem(AppConstants.SettingItems.REFRESH, Utils.getMessageString(AppConstants.Messages.menuRefresh,
				R.string.menuRefresh), this.getResources().getDrawable(R.drawable.setting_refresh)));

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

		Intent helpIntent;

		mDrawerListChild.setItemChecked(position, true);
		setTitle(settingSubListItems.get(position).name);
		mDrawerLayoutChild.closeDrawer(Gravity.RIGHT);
		mIsChildDrawerOpen = false;

		int id = settingSubListItems.get(position).id;
		switch (id)
		{
			case AppConstants.SettingItems.LOCATION:
				showOtherDialog(AppConstants.DialogConstants.CHANGE_SETTINGS_LOCATION_DIALOG, null);
				break;
			case AppConstants.SettingItems.CATEGORIES:
				showOtherDialog(AppConstants.DialogConstants.CATEGORY_PREF_DIALOG, null);
				break;
			case AppConstants.SettingItems.UNSUBSCRIBE:
				showOtherDialog(AppConstants.DialogConstants.UNSUBSCRIBE_DIALOG, null);
				break;
			case AppConstants.SettingItems.TNC:
				closeMenu();
				helpIntent = new Intent();
				helpIntent.putExtra(AppConstants.JSONKeys.NAME, Utils.getMessageString(AppConstants.Messages.termsTitle, R.string.termsTitle));
				String TNC_URL = GreatBuyzApplication.getApplication().getTNCPrevURL();
				helpIntent.putExtra(AppConstants.SharedPrefKeys.help, TNC_URL);
				helpIntent.putExtra(AppConstants.Messages.menuRefresh, false);
				setResult(AppConstants.RESULT_SHOW_WEBVIEW, helpIntent);
				finish();
				break;
			case AppConstants.SettingItems.ALL_TNC:
				closeMenu();
				helpIntent = new Intent();
				helpIntent.putExtra(AppConstants.JSONKeys.NAME, Utils.getMessageString(AppConstants.Messages.infoTermsTitle, R.string.infoTermsTitle));
				String INFOTNC_URL = GreatBuyzApplication.getApplication().getINFOTNCURL();
				helpIntent.putExtra(AppConstants.SharedPrefKeys.help, INFOTNC_URL);
				helpIntent.putExtra(AppConstants.Messages.menuRefresh, false);
				setResult(AppConstants.RESULT_SHOW_WEBVIEW, helpIntent);
				finish();

				break;
			case AppConstants.SettingItems.INFO_PRIVACY:
				closeMenu();
				helpIntent = new Intent();  
				helpIntent.putExtra(AppConstants.JSONKeys.NAME, Utils.getMessageString(AppConstants.Messages.infoPrivacyTitle, R.string.infoPrivacyTitle));
				String INFOPRIVACY_URL = GreatBuyzApplication.getApplication().getINFOPRIVACYURL();
				helpIntent.putExtra(AppConstants.SharedPrefKeys.help, INFOPRIVACY_URL);
				helpIntent.putExtra(AppConstants.Messages.menuRefresh, false);
				setResult(AppConstants.RESULT_SHOW_WEBVIEW, helpIntent);
				finish();

				break;
			case AppConstants.SettingItems.INFO_COSTI:
				closeMenu();
				helpIntent = new Intent();
				helpIntent.putExtra(AppConstants.JSONKeys.NAME, Utils.getMessageString(AppConstants.Messages.infoCostiTitle, R.string.infoCostiTitle));
				String INFOCOSTI_URL = GreatBuyzApplication.getApplication().getINFOCOSTIURL();
				helpIntent.putExtra(AppConstants.SharedPrefKeys.help, INFOCOSTI_URL);
				helpIntent.putExtra(AppConstants.Messages.menuRefresh, false);
				setResult(AppConstants.RESULT_SHOW_WEBVIEW, helpIntent);
				finish();

				break;
			case AppConstants.SettingItems.FAQ:
				closeMenu();
				helpIntent = new Intent();
				helpIntent.putExtra(AppConstants.JSONKeys.NAME, Utils.getMessageString(AppConstants.Messages.faqTitle, R.string.faqTitle));
				String FAQ_URL = GreatBuyzApplication.getApplication().getFAQURL();
				helpIntent.putExtra(AppConstants.SharedPrefKeys.help, FAQ_URL);
				helpIntent.putExtra(AppConstants.Messages.menuRefresh, false);
				setResult(AppConstants.RESULT_SHOW_WEBVIEW, helpIntent);
				finish();
				break;

			case AppConstants.SettingItems.ABOUT:
				closeMenu();
				helpIntent = new Intent();
				helpIntent
						.putExtra(AppConstants.JSONKeys.NAME, Utils.getMessageString(AppConstants.Messages.menuAbout, R.string.menuAbout));
				String ABOUT_URL = GreatBuyzApplication.getApplication().getAboutURL();
				helpIntent.putExtra(AppConstants.SharedPrefKeys.help, ABOUT_URL);
				helpIntent.putExtra(AppConstants.Messages.menuRefresh, false);
				setResult(AppConstants.RESULT_SHOW_WEBVIEW, helpIntent);
				finish();
				break;
		}
	}

	private void selectItem(int position)
	{
		mDrawerList.setItemChecked(position, true);
		Intent helpIntent;
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
				int frequencyIndex = _data.getNotificationFrequencyIndex();
				String isDailyMsgEnable = _data.isDailyMsgEnabled();
				if(isDailyMsgEnable != null){
					if(isDailyMsgEnable.equalsIgnoreCase("true")){
						mDailyMsgCheckBox.setChecked(true);
					}else{
						mDailyMsgCheckBox.setChecked(false);
					}
				}
				if (frequencyIndex < 0)
				{
					mInappCheckBox.setChecked(false);
					setNotificationSpinnerVisibility(false);
				}
				else
				{
					mInappCheckBox.setChecked(true);
					mNotificationSpinner.setSelection(frequencyIndex);
					setNotificationSpinnerVisibility(true);
				}
				mNotificationLayout.setVisibility(View.VISIBLE);
				mNotificationLayout.setClickable(true);
				mDrawerLayoutChild.openDrawer(mDrawerLayoutChildView);
				mDrawerLayoutChild.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
				mIsChildDrawerOpen = true;
				mDrawerList.setItemChecked(position, true);
				setTitle(settingItems.get(position).name);
				break;

			case AppConstants.SettingItems.EMAIL:
				break;
			case AppConstants.SettingItems.KEYWORDS:
				break;
			case AppConstants.SettingItems.MY_COUPON:
				closeMenu();
				setResult(AppConstants.RESULT_CLICK_MYCOUPON);
				finish();
				break;
			case AppConstants.SettingItems.SEARCH:
				closeMenu();
				setResult(AppConstants.RESULT_CLICK_SEARCH);
				finish();
				break;

			case AppConstants.SettingItems.ABOUT:
				closeMenu();
				helpIntent = new Intent();
				helpIntent
						.putExtra(AppConstants.JSONKeys.NAME, Utils.getMessageString(AppConstants.Messages.menuAbout, R.string.menuAbout));
				String ABOUT_URL = GreatBuyzApplication.getApplication().getAboutURL();
				helpIntent.putExtra(AppConstants.SharedPrefKeys.help, ABOUT_URL);
				helpIntent.putExtra(AppConstants.Messages.menuRefresh, false);
				setResult(AppConstants.RESULT_SHOW_WEBVIEW, helpIntent);
				finish();
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
				break;
			default:
				break;
		}
	}

	void setNotificationSpinnerVisibility(boolean status)
	{
		if (status)
		{
			mNotificationSpinner.setEnabled(true);
			mSpinnerText.setEnabled(true);
			// mNotifButton.setEnabled(true);
		}
		else
		{
			mNotificationSpinner.setEnabled(false);
			mSpinnerText.setEnabled(false);
			// mNotifButton.setEnabled(false);
		}
	}

	public void sendNotificationFrequencyToServer(final int frequency, final String isDailyMsgEnabled)
	{
		try
		{
			showOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG, null);
			GreatBuyzApplication.getServiceDelegate().updateUserInfo(null, null, null, null, null, frequency, null, null, null,isDailyMsgEnabled,
					new IOperationListener()
					{

						@Override
						public void onOperationCompleted(boolean p_OperationComplitionStatus, String p_MessageFromServer)
						{
							removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);

							// hideNotificationFrequencyScreen();

							if (p_OperationComplitionStatus)
							{
								_data.updateNotificationFrequency(frequency);
								_data.updateIsDailyMsgEnabled(isDailyMsgEnabled);
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

	TextWatcher textWatcher = new TextWatcher()
	{

		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
		{

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
		{

		}

		@Override
		public void afterTextChanged(Editable editable)
		{

			if (multiAutoTextView.getEditableText().toString().trim().length() <= 0)
			{
				btnSend.setEnabled(false);
			}
			else
			{
				btnSend.setEnabled(true);
			}
		}

	};

	@Override
	protected void onStart()
	{
		super.onStart();
		// FlurryAgent.onPageView();
		GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit("SetAlerts");
	}

	@Override
	public void onBackPressed()
	{
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

		super.onBackPressed();
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle b)
	{
		switch (id)
		{
			case AppConstants.DialogConstants.LOADING_DIALOG:
				LoadingDialog loadingDialog = new LoadingDialog(this, style.AlertDialogCustom);
				loadingDialog.setCancelable(false);
				return loadingDialog;
			case AppConstants.DialogConstants.MESSAGE_DIALOG:
				String message1 = b.getString(AppConstants.JSONKeys.MESSAGE);
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

			case AppConstants.DialogConstants.CHANGE_SETTINGS_LOCATION_DIALOG:
				{
					final List<String> loc = _data.getCitiesList();
					String[] locations = loc.toArray(new String[loc.size()]);
					final String prevLocation = _data.getUserCity();
					int selLoc = loc.indexOf(prevLocation);
					AlertDialog.Builder locationBuilder = new AlertDialog.Builder(this);
					locationBuilder.setTitle(Utils.getMessageString(AppConstants.Messages.settingItemChangeLocation,
							R.string.settingItemChangeLocation));
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
							if(!selectedUnselectedCategories.contains(which)){
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
										b.putString(AppConstants.JSONKeys.MESSAGE, Utils.getMessageString(
												AppConstants.Messages.atleastOneCategoryMessage, R.string.atleastOneCategoryMessage));
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
								setUserPreferencesCategories(selection.clone(),selectedUnselectedCategories);
								closeMenu();
								// updateCurrentTab();
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
					message = b.getString(AppConstants.JSONKeys.MESSAGE);
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

		}
		return super.onCreateDialog(id, b);
	}

	private void showOtherDialog(final int which, final Bundle b)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				showDialog(which, b);
			}
		});
	}

	public void sendKeywordsToServer(final String[] keywords)
	{
		try
		{
			showOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG, null);
			GreatBuyzApplication.getServiceDelegate().updateUserInfo(null, null, null, null, keywords, -2, null, null, null,null,
					new IOperationListener()
					{
						@Override
						public void onOperationCompleted(boolean pOperationComplitionStatus, String pMessageFromServer)
						{
							removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
							if (pOperationComplitionStatus)
							{
								Bundle b = new Bundle();
								b.putString(AppConstants.JSONKeys.MESSAGE,
										Utils.getMessageString(AppConstants.Messages.keywordsSentMessage, R.string.keywordsSentMessage));
								showOtherDialog(AppConstants.DialogConstants.MESSAGE_DIALOG, b);
							}
							else
							{
								Bundle b = new Bundle();
								b.putString(AppConstants.JSONKeys.MESSAGE,
										Utils.getMessageString(AppConstants.Messages.keywordsFailedMessage, R.string.keywordsFailedMessage));
								showOtherDialog(AppConstants.DialogConstants.MESSAGE_DIALOG, b);
							}
						}
					});
		}
		catch (Exception e)
		{
			removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			e.printStackTrace();
		}
	}

	public void setUserPreferencesAndUpdateScreen(final String selectedCity)
	{
		try
		{
			showOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG, null);
			GreatBuyzApplication.getServiceDelegate().updateUserInfo(new String[] { selectedCity }, null, null, null, null, -2, null, null,null,
					null, new IOperationListener()
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
								showOtherDialog(AppConstants.DialogConstants.ERROR_DIALOG, b);
							}
						}
					});
		}
		catch (Exception e)
		{
			removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			e.printStackTrace();
		}
	}

	public void setUserPreferencesCategories(final boolean[] selection,final List<Integer> selectedUnselectedCategories)
	{
		try
		{
			showOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG, null);
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
			GreatBuyzApplication.getServiceDelegate().updateUserInfo(null, cats, null, null, null, -2, null, null, null,null,
					new IOperationListener()
					{
						@Override
						public void onOperationCompleted(boolean pOperationComplitionStatus, String pMessageFromServer)
						{
							removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
							if (pOperationComplitionStatus)
							{
								isCategoryChange = false;
								resetDataAndUpdateUI(null, categories, null);
								List<String> dbcategories =  _data.getCategoriesList();
								Utils.changeCategorySettings(dbcategories, selection, selectedUnselectedCategories);
							}
							else
							{
								Bundle b = new Bundle();
								b.putString(AppConstants.JSONKeys.MESSAGE, pMessageFromServer);
								showOtherDialog(AppConstants.DialogConstants.ERROR_DIALOG, b);
							}
						}
					});
		}
		catch (Exception e)
		{
			removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			e.printStackTrace();
		}
	}

	public void unsubscribe(final String mdn, final String channel, final String chargingMode)
	{
		showOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG, null);
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
						_data.updateIsUserSubscribedToGCM(true);

						HashMap<String, String> m = new HashMap<String, String>();
						m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.Unsubscribe);
						m.put(AppConstants.Flurry.MDN, mdn);
						m.put(AppConstants.Flurry.STATUS, AppConstants.Flurry.PENDING);
						// FlurryAgent.logEvent(AppConstants.Flurry.CategoryList,
						// m);
						GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.Unsubscribe, m);

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

	public void resetDataAndUpdateUI(String selectedCity)
	{
		GreatBuyzApplication.getApplication().clearAllDataOnCityChange();
		_data.updateUserCity(selectedCity);
		finish();
	}

	public void resetDataAndUpdateUI(String loc, List<String> cats, String emailId)
	{
		// ////System.out.println("GreatBuyz: function IN SampleTabsStyled > resetDataAndUpdateUI");
		GreatBuyzApplication.getApplication().clearAllDataOnCityChange();
		if (loc != null)
			_data.updateUserCity(loc);
		if (cats != null)
		{
			_data.clearCategoriesSelectedStatus();
			_data.setCategoriesSelectedStatus(cats);
		}
		if (emailId != null)
			_data.updateEmail(emailId);

		// ////System.out.println("GreatBuyz: function OUT SampleTabsStyled > resetDataAndUpdateUI");
	}

	public void removeOtherDialog(final int which)
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

	void exitFromApp()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					setResult(AppConstants.RESULT_EXIT_APP);
					finish();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
}