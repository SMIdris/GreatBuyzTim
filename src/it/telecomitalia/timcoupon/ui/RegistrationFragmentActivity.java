package it.telecomitalia.timcoupon.ui;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import it.telecomitalia.timcoupon.R;
import it.telecomitalia.timcoupon.R.style;
import it.telecomitalia.timcoupon.service.DataController;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onmobile.hcoe.ui.dialog.GenericDialog;
import com.onmobile.hcoe.ui.dialog.LoadingDialog;
import com.onmobile.utils.AppConstants;
import com.onmobile.utils.Utils;

public class RegistrationFragmentActivity extends FragmentActivity
{
	GoogleMusicAdapter mAdapter;
	ViewPager mPager;
	ImageView imgBack;
	private LinearLayout mBackNavigation;
	private View mHeaderView;
	PageIndicator mIndicator;
	DataController _data;
	boolean resend = false;

	@Override
	protected void onCreate(Bundle arg0)
	{
		super.onCreate(arg0);

		_data = GreatBuyzApplication.getDataController();
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.registration);
		// Log.v("Time test : RegistrationFragmentActivity onCreate start :", ""
		// + Utils.getCurrentTimeStamp());
		RelativeLayout myDealsShortcut = (RelativeLayout) findViewById(R.id.imgMyDeals);
		myDealsShortcut.setVisibility(View.GONE);

		ImageView menuView = (ImageView) findViewById(R.id.imgMenu);
		menuView.setVisibility(View.GONE);

		mAdapter = new GoogleMusicAdapter(getSupportFragmentManager(), false);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);

		imgBack = (ImageView) findViewById(R.id.imgBackArrow);
		imgBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mAdapter.showRegistrationView();
			}
		});

		mHeaderView = (View) findViewById(R.id.registerHeader);

		mBackNavigation = (LinearLayout) findViewById(R.id.back_navigation);
		mBackNavigation.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mAdapter.showRegistrationView();
			}
		});
	}

	@Override
	public void onBackPressed()
	{
		if (mAdapter.shouldExitOnBack())
		{
			String err = Utils.getMessageString(AppConstants.Messages.exitMessage, R.string.exitMessage);
			showMessageDialog(AppConstants.DialogConstants.EXIT_DIALOG, err);
		}
		else
		{
			mAdapter.showRegistrationView();
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{

		super.onRestoreInstanceState(savedInstanceState);

		mAdapter.notifyDataSetChanged();
		startActivity(getIntent());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	public void showBackButton()
	{
		mHeaderView.setVisibility(View.VISIBLE);
		imgBack.setVisibility(View.VISIBLE);
		mBackNavigation.setClickable(true);
	}

	public void hideBackButton()
	{
		mHeaderView.setVisibility(View.GONE);
		imgBack.setVisibility(View.INVISIBLE);
		mBackNavigation.setClickable(false);
	}

	class GoogleMusicAdapter extends FragmentStatePagerAdapter
	{
		public Fragment mFragmentForRegistration;

		public GoogleMusicAdapter(FragmentManager fm, boolean isChildDisplayed)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			if (position == 0)
			{
				mFragmentForRegistration = RegistrationFragment.newInstance(RegistrationFragmentActivity.this);
				mFragmentForRegistration.setRetainInstance(true);
				return mFragmentForRegistration;
			}
			return null;
		}

		@Override
		public int getCount()
		{
			return 1;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			return "Hemant".toUpperCase();
		}

		@Override
		public int getItemPosition(Object object)
		{
			return POSITION_UNCHANGED;
		}

		public boolean shouldExitOnBack()
		{
			if (mFragmentForRegistration == null)
				return false;

			return ((RegistrationFragment) mFragmentForRegistration).shouldExitOnBack();
		}

		public void showRegistrationView()
		{
			((RegistrationFragment) mFragmentForRegistration).showRegistrationView();
		}

	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args)
	{
		String message = null;
		if (args != null)
		{
			message = args.getString(AppConstants.JSONKeys.MESSAGE);
		}
		switch (id)
		{
			case AppConstants.DialogConstants.EXIT_DIALOG:
				{
					if (args.containsKey(AppConstants.JSONKeys.MESSAGE))
						message = args.getString(AppConstants.JSONKeys.MESSAGE);

					final GenericDialog dialog = new GenericDialog(RegistrationFragmentActivity.this, R.layout.exit_message,
							R.style.AlertDialogCustom);
					dialog.setCancelable(false);
					Button btn = (Button) dialog.findViewById(R.id.yes_btn);
					btn.setText(Utils.getMessageString(AppConstants.Messages.yes, R.string.yes));
					btn.setTypeface(GreatBuyzApplication.getApplication().getFont());
					btn.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							try
							{
								removeDialog(AppConstants.DialogConstants.EXIT_DIALOG);
								RegistrationFragmentActivity.this.onBackPressed();
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
					Button noBtn = (Button) dialog.findViewById(R.id.no_btn);
					noBtn.setText(Utils.getMessageString(AppConstants.Messages.no, R.string.no));
					noBtn.setTypeface(GreatBuyzApplication.getApplication().getFont());
					noBtn.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							try
							{
								removeDialog(AppConstants.DialogConstants.EXIT_DIALOG);
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
					titleText1.setText(AppConstants.EMPTY_STRING);
					titleText1.setTypeface(GreatBuyzApplication.getApplication().getFont());
					return dialog;
				}

			case AppConstants.DialogConstants.FREE_DEAL_MESSAGE_DIALOG:
				{
					if (args.containsKey(AppConstants.JSONKeys.MESSAGE))
						message = args.getString(AppConstants.JSONKeys.MESSAGE);

					final GenericDialog dialog = new GenericDialog(RegistrationFragmentActivity.this, R.layout.exit_message,
							R.style.AlertDialogCustom);
					dialog.setCancelable(false);
					Button btn = (Button) dialog.findViewById(R.id.yes_btn);
					btn.setText(Utils.getMessageString(AppConstants.Messages.ok, R.string.ok));
					btn.setTypeface(GreatBuyzApplication.getApplication().getFont());
					btn.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							try
							{
								removeDialog(AppConstants.DialogConstants.FREE_DEAL_MESSAGE_DIALOG);
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
							startWelcomeDealScreen();
						}
					});
					Button noBtn = (Button) dialog.findViewById(R.id.no_btn);
					noBtn.setText(getString(R.string.cancel));
					noBtn.setTypeface(GreatBuyzApplication.getApplication().getFont());
					noBtn.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							try
							{
								removeDialog(AppConstants.DialogConstants.FREE_DEAL_MESSAGE_DIALOG);
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
							startHomeScreen();
						}
					});
					TextView msgText = (TextView) dialog.findViewById(R.id.msg);
					msgText.setText(message);
					msgText.setTypeface(GreatBuyzApplication.getApplication().getFont());
					TextView titleText1 = (TextView) dialog.findViewById(R.id.title);
					titleText1.setText(AppConstants.EMPTY_STRING);
					titleText1.setTypeface(GreatBuyzApplication.getApplication().getFont());
					return dialog;
				}
			case AppConstants.DialogConstants.REG_UNSUCCESSFUL_DIALOG:
				final GenericDialog dialog = new GenericDialog(this, R.layout.status_message, R.style.AlertDialogCustom);

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
							removeDialog(AppConstants.DialogConstants.REG_UNSUCCESSFUL_DIALOG);
						}
						catch (Exception e)
						{
							// Log.e("LoginActivity Error message Ok button onClick() ",
							// "Application is crashed due to exception --> " +
							// e.toString());
							e.printStackTrace();
						}

					}
				});
				TextView msgText = (TextView) dialog.findViewById(R.id.msg);
				msgText.setText(message);
				msgText.setTypeface(GreatBuyzApplication.getApplication().getFont());
				TextView titleText = (TextView) dialog.findViewById(R.id.title);
				titleText.setText(Utils.getMessageString(AppConstants.Messages.titleInfo, R.string.titleInfo));
				titleText.setTypeface(GreatBuyzApplication.getApplication().getFont());
				return dialog;

			case AppConstants.DialogConstants.REG_MESSAGE_DIALOG_WITH_EXIT:

				final GenericDialog exitdialog = new GenericDialog(this, R.layout.status_message, R.style.AlertDialogCustom);

				Button exitbtn = (Button) exitdialog.findViewById(R.id.status_btn);
				exitbtn.setText(Utils.getMessageString(AppConstants.Messages.ok, R.string.ok));
				exitbtn.setTypeface(GreatBuyzApplication.getApplication().getFont());
				exitbtn.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{

						try
						{
							removeDialog(AppConstants.DialogConstants.REG_MESSAGE_DIALOG_WITH_EXIT);
							RegistrationFragmentActivity.this.onBackPressed();
							// GreatBuyzApplication.getApplication().onTerminate();
						}
						catch (Exception e)
						{
							// Log.e("LoginActivity Error message Ok button onClick() ",
							// "Application is crashed due to exception --> " +
							// e.toString());
							e.printStackTrace();
						}

					}
				});
				TextView exitmsgText = (TextView) exitdialog.findViewById(R.id.msg);
				exitmsgText.setText(message);
				exitmsgText.setTypeface(GreatBuyzApplication.getApplication().getFont());
				TextView exittitleText = (TextView) exitdialog.findViewById(R.id.title);
				exittitleText.setText(Utils.getMessageString(AppConstants.Messages.titleInfo, R.string.titleInfo));
				exittitleText.setTypeface(GreatBuyzApplication.getApplication().getFont());
				return exitdialog;

			case AppConstants.DialogConstants.LOADING_DIALOG:
				LoadingDialog loadingDialog = new LoadingDialog(this, style.AlertDialogCustom);
				loadingDialog.setCancelable(false);
				return loadingDialog;
		}
		return super.onCreateDialog(id, args);
	}

	public void startWelcomeDealScreen()
	{
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				try
				{
					finish();
					Intent intent = new Intent(RegistrationFragmentActivity.this, WelcomeBonusActivity.class);
					startActivity(intent);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public void startHomeScreen()
	{
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				try
				{
					finish();
					Intent mainIntent = new Intent(RegistrationFragmentActivity.this, SampleTabsStyled.class);
					mainIntent.addCategory("com.jakewharton.android.viewpagerindicator.sample.SAMPLE");
					mainIntent.putExtra("com.jakewharton.android.viewpagerindicator.sample.Path", "Tabs");
					startActivity(mainIntent);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public void showMessageDialog(final int whichDialog, final String message)
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
					showDialog(whichDialog, b);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
}