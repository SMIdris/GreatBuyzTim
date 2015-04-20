package it.telecomitalia.timcoupon.ui;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import it.telecomitalia.timcoupon.R;
import it.telecomitalia.timcoupon.data.Deal;
import it.telecomitalia.timcoupon.data.DealScreenDTO;
import it.telecomitalia.timcoupon.data.NotificationDTO;
import it.telecomitalia.timcoupon.data.SettingItem;
import it.telecomitalia.timcoupon.service.DB;
import it.telecomitalia.timcoupon.service.DataController;
import it.telecomitalia.timcoupon.service.IOperationListener;
import it.telecomitalia.timcoupon.service.ResponseParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.onmobile.hcoe.ui.dialog.GenericDialog;
import com.onmobile.hcoe.ui.dialog.LoadingDialog;
import com.onmobile.utils.AppConstants;
import com.onmobile.utils.GreatBuyzTextView;
import com.onmobile.utils.Utils;
import com.onmobile.utils.settingsArrayAdapter;
import com.squareup.picasso.Picasso;

public class DetailScreen extends Activity
{
	DealScreenDTO dealScreenDTO = null;
	Deal dealDTO = null;

	DataController _data = GreatBuyzApplication.getDataController();

	static Activity activity;
	ImageView imgBack;
	LinearLayout mBackNavigation;
	Button btnVisitDeal;

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
		// ////System.out.println("GreatBuyz: function IN DetailScreen > onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		dealScreenDTO = null;
		loadNewIntent(getIntent());
		loadView();
		// ////System.out.println("GreatBuyz: function OUT DetailScreen > onCreate");
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		// ////System.out.println("GreatBuyz: function IN DetailScreen > onNewIntent");
		super.onNewIntent(intent);

		setIntent(intent);
		dealScreenDTO = null;
		loadNewIntent(intent);
		loadView();
		// ////System.out.println("GreatBuyz: function OUT DetailScreen > onNewIntent");
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.KEYCODE_MENU)
			return true;
		return super.dispatchKeyEvent(event);
	}

	@Override
	protected void onStart()
	{
		// ////System.out.println("GreatBuyz: function IN DetailScreen > onStart");
		super.onStart();
		// FlurryAgent.onPageView();
		GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit(AppConstants.Flurry.GCMDealDetail);

		activity = this;
		// ////System.out.println("GreatBuyz: function OUT DetailScreen > onStart");
	}

	@Override
	protected void onStop()
	{
		// ////System.out.println("GreatBuyz: function IN DetailScreen > onStop");
		super.onStop();
		activity = null;
		// ////System.out.println("GreatBuyz: function OUT DetailScreen > onStop");
	}

	@Override
	public void onBackPressed()
	{
		// ////System.out.println("GreatBuyz: function IN DetailScreen > onBackPressed");

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
		finish();
		// ////System.out.println("GreatBuyz: function OUT DetailScreen > onBackPressed");
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void loadNewIntent(Intent intent)
	{
		_data = GreatBuyzApplication.getDataController();
		NotificationDTO notification = null;

		// ////System.out.println("GreatBuyz: function IN DetailScreen > loadNewIntent");
		int type = 0;

		type = intent.getIntExtra(AppConstants.JSONKeys.TYPE, 0);

		int selectedIndex = 0;
		ImageView menuView;

		setContentView(R.layout.detailed_deal);

		RelativeLayout myDealsShortcut = (RelativeLayout) findViewById(R.id.imgMyDeals);
		ImageView myIcon = (ImageView) findViewById(R.id.myicon);
		SharedPreferences pref = GreatBuyzApplication
				.getApplication().getSharedPreferences();
		boolean isNewWelcomeDeal = pref.getBoolean(DB.COL_VERSION_WELCOME_DEAL, false);
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

		selectedIndex = intent.getIntExtra(AppConstants.JSONKeys.INDEX, 0);

		try
		{
			if (type == AppConstants.FramentConstants.DEALS_OF_THE_DAY)
			{
				dealScreenDTO = _data.getDealsOfTheDayDTO().getDealsOfTheDayList().get(selectedIndex);
			}
			else if (type == AppConstants.FramentConstants.DEAL_BY_CATEGORY)
			{
				dealScreenDTO = _data.getDealsByCategoriesDTO().getDeals().get(selectedIndex);
			}
			else if (type == AppConstants.FramentConstants.DEALS_YOU_MAY_LIKE)
			{
				dealScreenDTO = _data.getDealsYouMayLikeDTO().getDealsOfTheDayList().get(selectedIndex);
			}
			else if (type == AppConstants.FramentConstants.DEALS_NEAR_ME)
			{
				dealScreenDTO = _data.getDealsNearMeDTO().getDealsNearMeList().get(selectedIndex);
			}
			else if (type == AppConstants.FramentConstants.SURPRISE_ME)
			{
				dealDTO = _data.getSurpriseDeal();
				dealScreenDTO = GreatBuyzApplication.getServiceDelegate().getDealScreenDTO(dealDTO);
			}
			else if (type == AppConstants.FramentConstants.FREE_DEAL)
			{
				dealDTO = _data.getFreeDeal();
				dealScreenDTO = GreatBuyzApplication.getServiceDelegate().getDealScreenDTO(dealDTO);
			}
			else if (type == AppConstants.FramentConstants.MY_DEALS)
			{
				dealScreenDTO = _data.getMyDealsDTO().getMyDealsList().get(selectedIndex);
			}
			else if (type == AppConstants.FramentConstants.EXCLUSIVE_DEALS)
			{
				dealScreenDTO = _data.getExclusiveDealsDTO().getExclusiveDealsList().get(selectedIndex);
			}
			else if (type == AppConstants.FramentConstants.EXPLORE_DEALS)
			{
				dealScreenDTO = _data.getExploreDeals().getExploreDealsList().get(selectedIndex);
			}
			else
			{
				try
				{
					notification = null;
					String action = intent.getAction();
					// //System.out.println(" action : " + action);
					notification = ResponseParser.getNotification(new JSONObject(action));
					String dealId = notification.getDealId();
					if (!Utils.isNothing(dealId))
					{
						type = AppConstants.FramentConstants.DEAL_BY_ID;
						showDealNotification(dealId);
						return;
					}
				}
				catch (Exception e)
				{
					onBackPressed();
					e.printStackTrace();
					return;
				}
			}
		}
		catch (Exception e)
		{
			// //System.out.println("GB: crash will finish");
			e.printStackTrace();
			finish();
			return;
		}

		loadView();
	}

	private void loadView()
	{
		if (dealScreenDTO == null)
			return;

		String url = dealScreenDTO.getImage();
		ImageView img;
		img = (ImageView) findViewById(R.id.imgDetailDeal);
		img.setTag(url);

		if (!Utils.isNothing(url))
			Picasso.with(activity).load(url).config(Bitmap.Config.RGB_565).resize(320, 180).centerCrop().into(img);

		TextView txtDetailDesc = (TextView) findViewById(R.id.txtDetailDesc);
		//txtDetailDesc.setText(dealScreenDTO.getName());
		
		String dealLongDescription = dealScreenDTO.getDetails();
		dealLongDescription = Html.fromHtml(dealLongDescription).toString();
		txtDetailDesc.setText(dealLongDescription);
		
		txtDetailDesc.setTypeface(GreatBuyzApplication.getApplication().getFont());

		TextView txtDetailDealVal = (TextView) findViewById(R.id.txt_detail_deal_value);
		txtDetailDealVal.setText(String.valueOf(dealScreenDTO.getPrice()));
		txtDetailDealVal.setTypeface(GreatBuyzApplication.getApplication().getFont());

		String discount = dealScreenDTO.getDiscount();

		TextView txtDetailDisc = (TextView) findViewById(R.id.txt_detail_deal_desc_val);
		if (discount.equals("0"))
		{
			discount = "-";
			findViewById(R.id.percentage_txt).setVisibility(View.INVISIBLE);
		}
		else
		{
			String percent = getResources().getString(R.string.percent);
			if (discount.endsWith(percent))
			{
				discount = discount.substring(0, discount.lastIndexOf(percent) - 1);
			}
		}
		txtDetailDisc.setText(discount);
		txtDetailDisc.setTypeface(GreatBuyzApplication.getApplication().getFont());

		TextView txtDetailDealPay = (TextView) findViewById(R.id.txt_detail_deal_pay_val);
		txtDetailDealPay.setText(String.valueOf(dealScreenDTO.getCouponPrice()));
		txtDetailDealPay.setTypeface(GreatBuyzApplication.getApplication().getFont());

		RelativeLayout expireLayout = (RelativeLayout) findViewById(R.id.layout_expire);
		expireLayout.setVisibility(View.VISIBLE);
		TextView txtExpireDate = (TextView) findViewById(R.id.txt_expire_date);
		// TextView txtExpireTime = (TextView)
		// findViewById(R.id.txt_expire_time);
		// TextView txtExpireHeading = (TextView)
		// findViewById(R.id.txt_expire_heading);
		// ImageView seperatorImg = (ImageView) findViewById(R.id.seperator);

		if (dealScreenDTO.get_expire() != null)
		{
			String date = getDate(dealScreenDTO.get_expire());
			String seperator = getString(R.string.expirydatetimeseperator);
			String time = getTime(dealScreenDTO.get_expire());

			txtExpireDate.setVisibility(View.VISIBLE);
			// txtExpireTime.setVisibility(View.VISIBLE);
			// seperatorImg.setVisibility(View.VISIBLE);
			// txtExpireHeading.setVisibility(View.VISIBLE);
			String str = date + " " + seperator + " " + time /*
															 * + "\r\n" +
															 * heading
															 */;

			txtExpireDate.setTypeface(GreatBuyzApplication.getApplication().getFont());
			// txtExpireTime.setTypeface(GreatBuyzApplication.getApplication().getFont());

			txtExpireDate.setText(getString(R.string.expirydate) + " " + str);
			// txtExpireTime.setText(str);

			/*
			 * txtExpireHeading.setText(heading); txtExpireHeading.setTypeface
			 * (GreatBuyzApplication.getApplication().getFont());
			 */
		}
		btnVisitDeal = (Button) findViewById(R.id.btnDetailVisitDeal);
		btnVisitDeal.setVisibility(View.VISIBLE);
		btnVisitDeal.setTypeface(GreatBuyzApplication.getApplication().getFont());
		Utils.setMessageToButton(btnVisitDeal, AppConstants.Messages.btnVisitText);
		visitDeal();
		
		HashMap<String, String> m = new HashMap<String, String>();
		m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.VISIT);
		m.put(AppConstants.Flurry.DEALID, dealScreenDTO.getDealId());
		// FlurryAgent.logEvent(AppConstants.Flurry.DealDetail, m);
		String mdn = GreatBuyzApplication.getDataController().getMDN();
		if (!Utils.isNothing(mdn))
			GreatBuyzApplication.getApplication().getAnalyticsAgent().setMDN(mdn);
		GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.GCMDealDetail, m);
	}

	private void visitDeal()
	{
		btnVisitDeal.setTypeface(GreatBuyzApplication.getApplication().getFont());
		btnVisitDeal.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				HashMap<String, String> m = new HashMap<String, String>();
				m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.VISIT);
				m.put(AppConstants.Flurry.DEALID, dealScreenDTO.getDealId());
				// FlurryAgent.logEvent(AppConstants.Flurry.DealDetail, m);
				String mdn = GreatBuyzApplication.getDataController().getMDN();
				if (!Utils.isNothing(mdn))
					GreatBuyzApplication.getApplication().getAnalyticsAgent().setMDN(mdn);
				GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.GCMDealDetail, m);
				Utils.launchUri(DetailScreen.this, dealScreenDTO.getDealVisitUri());
			}
		});
	}

	@SuppressLint("SimpleDateFormat")
	private String getTime(Date get_expire)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
		return sdf.format(get_expire);
	}

	@SuppressLint("SimpleDateFormat")
	private String getDate(Date get_expire)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(get_expire);
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args)
	{
		switch (id)
		{
			case AppConstants.DialogConstants.LOADING_DIALOG:
				LoadingDialog loadingDialog = new LoadingDialog(this, R.style.AlertDialogCustom);
				loadingDialog.setCancelable(false);
				return loadingDialog;

			case AppConstants.DialogConstants.MESSAGE_DIALOG:
				String message = args.getString(AppConstants.JSONKeys.MESSAGE);
				final GenericDialog dialog = new GenericDialog(this, R.layout.status_message, R.style.AlertDialogCustom);
				dialog.setCancelable(false);
				Button btn = (Button) dialog.findViewById(R.id.status_btn);
				btn.setText(Utils.getMessageString(AppConstants.Messages.ok, R.string.ok));
				btn.setTypeface(GreatBuyzApplication.getApplication().getFont());
				btn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{

						try
						{
							removeDialog(AppConstants.DialogConstants.MESSAGE_DIALOG);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
				TextView msgText = (TextView) dialog.findViewById(R.id.msg);
				msgText.setText(message);
				msgText.setTypeface(GreatBuyzApplication.getApplication().getFont());
				TextView titleText1 = (TextView) dialog.findViewById(R.id.title);
				titleText1.setText(Utils.getMessageString(AppConstants.Messages.titleInfo, R.string.titleInfo));
				titleText1.setTypeface(GreatBuyzApplication.getApplication().getFont());
				return dialog;
				
			case AppConstants.DialogConstants.MESSAGE_DIALOG_NODEAL:
				String messages = args.getString(AppConstants.JSONKeys.MESSAGE);
				final GenericDialog dialogs = new GenericDialog(this, R.layout.status_message, R.style.AlertDialogCustom);
				dialogs.setCancelable(false);
				Button btns = (Button) dialogs.findViewById(R.id.status_btn);
				btns.setText(Utils.getMessageString(AppConstants.Messages.ok, R.string.ok));
				btns.setTypeface(GreatBuyzApplication.getApplication().getFont());
				btns.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{

						try
						{
							removeDialog(AppConstants.DialogConstants.MESSAGE_DIALOG);
							onBackPressed();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
				TextView msgTexts = (TextView) dialogs.findViewById(R.id.msg);
				msgTexts.setText(messages);
				msgTexts.setTypeface(GreatBuyzApplication.getApplication().getFont());
				TextView titleText1s = (TextView) dialogs.findViewById(R.id.title);
				titleText1s.setText(Utils.getMessageString(AppConstants.Messages.titleInfo, R.string.titleInfo));
				titleText1s.setTypeface(GreatBuyzApplication.getApplication().getFont());
				return dialogs;

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
					catBuilder.setTitle(Utils.getMessageString(AppConstants.Messages.titleCategoryPreferences,
							R.string.titleCategoryPreferences));
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

		}
		return super.onCreateDialog(id, args);
	}

	public void showLoadingDialog()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					showDialog(AppConstants.DialogConstants.LOADING_DIALOG);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public void removeLoadingDialog()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					removeDialog(AppConstants.DialogConstants.LOADING_DIALOG);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public void showMessageDialog(final String message)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Bundle b = new Bundle();
					b.putString(AppConstants.JSONKeys.MESSAGE, message);
					showDialog(AppConstants.DialogConstants.MESSAGE_DIALOG, b);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	public void showMessageDialogNoDeal(final String message)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Bundle b = new Bundle();
					b.putString(AppConstants.JSONKeys.MESSAGE, message);
					showDialog(AppConstants.DialogConstants.MESSAGE_DIALOG_NODEAL, b);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	private void repaint(final DealScreenDTO deal)
	{
		runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				dealScreenDTO = deal;
				loadView();
			}
		});
	}

	private void showDealNotification(String dealId)
	{
		try
		{
			showDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			GreatBuyzApplication.getServiceDelegate().getDealById(dealId, new IOperationListener()
			{
				@Override
				public void onOperationCompleted(boolean p_OperationComplitionStatus, String p_MessageFromServer)
				{
					removeDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					if (p_OperationComplitionStatus)
					{
						DataController _data = GreatBuyzApplication.getDataController();
						Deal dealObj = _data.getDealById();
						if (dealObj == null)
						{
							onBackPressed();
							return;
						}

						DealScreenDTO deal = GreatBuyzApplication.getServiceDelegate().getDealScreenDTO(dealObj);
						if (deal == null)
						{
							onBackPressed();
							return;
						}

						repaint(deal);
					}
					else
					{
					
						showMessageDialogNoDeal(Utils.getMessageString(AppConstants.Messages.emptyDeal, R.string.emptyDeal));
						//onBackPressed();
					}
				}
			});
		}
		catch (Exception e)
		{
			removeDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			// Log.d("Notification :", "Canceled");
			e.printStackTrace();
			onBackPressed();
		}
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
				helpIntent = new Intent();
				helpIntent.putExtra(AppConstants.JSONKeys.NAME,
						Utils.getMessageString(AppConstants.Messages.termsTitle, R.string.termsTitle));
				String ALLTNC_URL = GreatBuyzApplication.getApplication().getALLTNCURL();
				helpIntent.putExtra(AppConstants.SharedPrefKeys.help, ALLTNC_URL);
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
				// showDialog(AppConstants.DialogConstants.EMAIL_DIALOG, null);
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
				loadNewIntent(getIntent());
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

	public void sendNotificationFrequencyToServer(final int frequency,final String isDailyMsgEnabled)
	{
		try
		{
			showDialog(AppConstants.DialogConstants.LOADING_DIALOG, null);
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

	public void setUserPreferencesAndUpdateScreen(final String selectedCity)
	{
		try
		{
			showDialog(AppConstants.DialogConstants.LOADING_DIALOG, null);
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

	public void setUserPreferencesCategories(final boolean[] selection,final List<Integer> selectedUnselectedCategories)
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