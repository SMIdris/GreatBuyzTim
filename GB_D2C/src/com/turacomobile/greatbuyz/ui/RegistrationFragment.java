package com.turacomobile.greatbuyz.ui;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.gcm.GCMRegistrar;
import com.turacomobile.greatbuyz.GreatBuyzApplication;
import com.turacomobile.greatbuyz.R;
import com.turacomobile.greatbuyz.service.DataController;
import com.turacomobile.greatbuyz.service.IOperationListener;
import com.turacomobile.greatbuyz.service.ResponseParser;
import com.turacomobile.greatbuyz.utils.AppConstants;
import com.turacomobile.greatbuyz.utils.GreatBuyzTextView;
import com.turacomobile.greatbuyz.utils.Utils;

public final class RegistrationFragment extends Fragment
{
	EditText edtMDN;
	CheckBox chkAgreeTerms;
	TextView txtEnterMobileNumber;
	TextView welcomeMessage;
	Button btnRegister;
	LinearLayout layoutEditMobileNumber;
	TextView txtCountryCode;
	EditText edtOTP;
	Button btnValidateOTP;
	Button btnResendOTP;
	boolean resend = false;
	boolean getMDN = true;

	static Activity activity;
	final int VIEW_REGISTRATION = 0;
	final int VIEW_VALIDATION = 1;
	ViewSwitcher viewSwitcher;
	static DataController _data;
	String msisdn;

	public static RegistrationFragment newInstance(Activity screen)
	{
		_data = GreatBuyzApplication.getDataController();

		RegistrationFragment fragment = new RegistrationFragment();
		activity = screen;
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);

		activity = getActivity();
		_data = GreatBuyzApplication.getDataController();

		String mdn = _data.getMDN();
		if (Utils.isNothing(mdn) && getMDN)
			getMDNFromNetwork();
	}

	@Override
	public void onStart()
	{
		super.onStart();
		// FlurryAgent.onPageView();
		GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit("Registration");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View layout = null;
		layout = inflater.inflate(R.layout.registeractivity, null);

		viewSwitcher = (ViewSwitcher) layout.findViewById(R.id.switcherRegistration);
		edtMDN = (EditText) layout.findViewById(R.id.edtEnterMobileNumber);
		edtMDN.setTypeface(GreatBuyzApplication.getApplication().getFont());
		edtMDN.setHint(Utils.getMessageString(AppConstants.Messages.enterMobileNumber, R.string.enterMobileNumber));
		chkAgreeTerms = (CheckBox) layout.findViewById(R.id.checkAgreeToTerms);
		chkAgreeTerms.setTypeface(GreatBuyzApplication.getApplication().getFont());
		
		btnRegister = (Button) layout.findViewById(R.id.btnRegister);
		
		btnRegister.setEnabled(true);
		chkAgreeTerms.setEnabled(true);
		chkAgreeTerms.setChecked(true);
		if (_data == null)
			_data = GreatBuyzApplication.getDataController();

		String strAgree = _data.getMessage(AppConstants.Messages.agree);
		if (!Utils.isNothing(strAgree))
			chkAgreeTerms.setText(strAgree);

		GreatBuyzTextView txtTnCAndInfo = (GreatBuyzTextView) layout.findViewById(R.id.txtTnCAndInfo);
		Utils.setConstantToTextView(txtTnCAndInfo, AppConstants.Messages.txtTnCAndInfo);
		txtTnCAndInfo.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Intent helpIntent = new Intent(getActivity(), HelpActivity.class);
				String TNC_URL = GreatBuyzApplication.getApplication().getTNCURL();
				helpIntent.putExtra(AppConstants.SharedPrefKeys.help, TNC_URL);
				helpIntent.putExtra(AppConstants.JSONKeys.NAME,
						Utils.getMessageString(AppConstants.Messages.termsTitle, R.string.termsTitle));
				startActivity(helpIntent);
			}
		});

		String otp = _data.getOTP();
		String mdn = loadSavedPreferences("msisdn");
		// check if database has mdn from network / server
		String serverMDN = _data.getMDN();
		if (!Utils.isNothing(serverMDN))
			mdn = serverMDN;

		if (!Utils.isNothing(otp) && !Utils.isNothing(mdn))
		{
			String countryCode = _data.getConstant(AppConstants.Constants.countryCode).trim();
			if (mdn.startsWith(countryCode)){
				//System.out.println("mdn ***"+mdn);
				//mdn = mdn.replace("", AppConstants.EMPTY_STRING);
				StringBuffer sb = new StringBuffer(mdn);
			    sb.replace(0, 3, "");
			    mdn = sb.toString();
			}
				

			edtMDN.setText(mdn);
			chkAgreeTerms.setChecked(true);
			btnRegister.setEnabled(true);
			viewSwitcher.setDisplayedChild(VIEW_VALIDATION);
			((RegistrationFragmentActivity) getActivity()).showBackButton();
		}
		else
		{
			viewSwitcher.setDisplayedChild(VIEW_REGISTRATION);
			((RegistrationFragmentActivity) getActivity()).hideBackButton();
		}

		txtEnterMobileNumber = (TextView) layout.findViewById(R.id.txtRegisterMobileNumber);

		Utils.setMessageToTextView(txtEnterMobileNumber, AppConstants.Messages.registerMobileNumber);
		Utils.setMessageToTextView(layout, R.id.txtWelcomeOfferText, AppConstants.Messages.welcomeOfferText);

		if (_data.getUserSubscriptionStatus().equalsIgnoreCase(AppConstants.UserActivationStatus.DISCONTINUED))
		{
			welcomeMessage = (TextView) layout.findViewById(R.id.txtWelcomeMessage);
			Utils.setMessageToTextView(layout, R.id.txtWelcomeMessage, AppConstants.Messages.welcomeText_DiscontinuedUser);
		}
		else
		{
			Utils.setMessageToTextView(layout, R.id.txtWelcomeMessage, AppConstants.Messages.welcomeText);
		}

		layoutEditMobileNumber = (LinearLayout) layout.findViewById(R.id.layoutMobileNumber);

		txtCountryCode = (TextView) layout.findViewById(R.id.txtCountryCode);
		Utils.setConstantToTextView(txtCountryCode, AppConstants.Constants.countryCode);

		edtMDN.setOnFocusChangeListener(new OnFocusChangeListener()
		{

			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				edtMDN.setHint("");
			}
		});
		String strLength = _data.getConstant(AppConstants.Constants.mobileNumberMaxLength);

		if (!Utils.isNothing(strLength))
		{
			try
			{
				int length = Integer.parseInt(strLength);
				edtMDN.setFilters(new InputFilter[] { new InputFilter.LengthFilter(length) });
			}
			catch (Exception e)
			{
			}
		}

		/*
		 * if (mdn != null && !mdn.equalsIgnoreCase(AppConstants.EMPTY_STRING))
		 * { layoutEditMobileNumber.setVisibility(View.GONE);
		 * txtEnterMobileNumber.setVisibility(View.GONE); }
		 */

		Button btnAgreeToTerms = (Button) layout.findViewById(R.id.btnViewAgreement);

		chkAgreeTerms.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				//btnRegister.setEnabled(isChecked);
			}
		});

		btnAgreeToTerms.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// String text =
				// Utils.readFromFile(AppConstants.SharedPrefKeys.terms);
				Intent helpIntent = new Intent(getActivity(), HelpActivity.class);
				String TNC_URL = GreatBuyzApplication.getApplication().getTNCURL();
				helpIntent.putExtra(AppConstants.SharedPrefKeys.help, TNC_URL);
				helpIntent.putExtra(AppConstants.JSONKeys.NAME,
						Utils.getMessageString(AppConstants.Messages.termsTitle, R.string.termsTitle));
				startActivity(helpIntent);
			}
		});

		btnRegister.setTypeface(GreatBuyzApplication.getApplication().getFont());
		Utils.setMessageToButton(btnRegister, AppConstants.Messages.register);
		btnRegister.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String mdn = null;
				resend = false;

				if (layoutEditMobileNumber.getVisibility() == View.VISIBLE)
				{
					mdn = edtMDN.getText().toString().trim();
					if (mdn != null && mdn.length() < AppConstants.MDN_MIN_LENGTH)
					{
						Utils.setError(edtMDN, getActivity().getResources().getString(R.string.invalidMobileNumber));
						return;
					}

					String countryCode = txtCountryCode.getText().toString().trim();
					if (!mdn.contains(countryCode))
						mdn = countryCode + mdn;

					GreatBuyzApplication.getApplication().getAnalyticsAgent().setMDN(mdn);

					HashMap<String, String> m = new HashMap<String, String>();
					m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.REGISTER);
					m.put(AppConstants.Flurry.MDN, mdn);
					m.put(AppConstants.Flurry.USERINPUT, AppConstants.Flurry.YES);
					// FlurryAgent.logEvent(AppConstants.Flurry.Registration,
					// m);
					GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.Registration, m);

					if (!loadSavedPreferences("msisdn").equals(mdn))
					{
						getActivity().showDialog(AppConstants.DialogConstants.LOADING_DIALOG, null);
						completeRegistration(mdn);
					}
					else
						showValidationView();
				}
				else
				{
					mdn = _data.getMDN();

					GreatBuyzApplication.getApplication().getAnalyticsAgent().setMDN(mdn);

					HashMap<String, String> m = new HashMap<String, String>();
					m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.REGISTER);
					m.put(AppConstants.Flurry.MDN, mdn);
					m.put(AppConstants.Flurry.USERINPUT, AppConstants.Flurry.NO);
					// FlurryAgent.logEvent(AppConstants.Flurry.Registration,
					// m);
					GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.Registration, m);

					updateServer(mdn, AppConstants.JSONKeys.GCM, AppConstants.JSONKeys.WAP,
							AppConstants.UserActivationStatus.DIRECT_ACTIVATION, AppConstants.JSONKeys.SERVICE_KEY_VALUE);
				}
			}
		});

		edtOTP = (EditText) layout.findViewById(R.id.edtOTP);
		edtOTP.setOnFocusChangeListener(new OnFocusChangeListener()
		{

			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				edtOTP.setHint("");
			}
		});

		GreatBuyzTextView txtEnterOTP = (GreatBuyzTextView) layout.findViewById(R.id.txtEnterOTP);
		Utils.setMessageToTextView(txtEnterOTP, AppConstants.Messages.enterOTP);

		String strOTPLength = _data.getConstant(AppConstants.Constants.otpMaxLength);
		if (!Utils.isNothing(strOTPLength))
		{
			try
			{
				int length = Integer.parseInt(strOTPLength);
				edtOTP.setFilters(new InputFilter[] { new InputFilter.LengthFilter(length) });
			}
			catch (Exception e)
			{
			}
		}

		btnValidateOTP = (Button) layout.findViewById(R.id.btnValidateOTP);
		btnValidateOTP.setTypeface(GreatBuyzApplication.getApplication().getFont());
		Utils.setMessageToButton(btnValidateOTP, AppConstants.Messages.validate);
		btnValidateOTP.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				HashMap<String, String> m = new HashMap<String, String>();
				m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.VALIDATEOTP);
				// FlurryAgent.logEvent(AppConstants.Flurry.Registration, m);
				GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.Registration, m);
				System.out.println("isOTPExpired ***"+isOTPExpired());
				if (isOTPExpired())
				{
					String expiredOTP = Utils.getMessageString(AppConstants.Messages.otpExpiredMessage, R.string.otpExpiredMessage);
					showMessageDialog(AppConstants.DialogConstants.REG_UNSUCCESSFUL_DIALOG, expiredOTP);
				}
				else
				{
					String otp = edtOTP.getText().toString().trim();
					if (otp != null && !otp.equalsIgnoreCase(AppConstants.EMPTY_STRING) && otp.length() >= AppConstants.OTP_MIN_LENGTH)
					{
						String otpServer = _data.getOTP();
						if (otp.equals(otpServer))
						{
							// taking msisdn from sharef pref because when we close app from recent apps we won;t have the value msisdn when it relaunches then it is getting failed 
							// this resolves loader issue in OTP screen
							msisdn = loadSavedPreferences("msisdn");
							_data.updateMDN(msisdn);
							_data.updateOTP(null);
							savePreferences("msisdn", "");
							updateServer(msisdn, AppConstants.JSONKeys.GCM, AppConstants.JSONKeys.WAP,
									AppConstants.UserActivationStatus.DIRECT_ACTIVATION, AppConstants.JSONKeys.SERVICE_KEY_VALUE);
						}
						else
						{
							String incorrectOTP = Utils.getMessageString(AppConstants.Messages.incorrectOTP, R.string.incorrectOTP);
							showMessageDialog(AppConstants.DialogConstants.REG_UNSUCCESSFUL_DIALOG, incorrectOTP);
						}
					}
				}
			}
		});

		GreatBuyzTextView txtOTPResendPrompt = (GreatBuyzTextView) layout.findViewById(R.id.txtOTPResendPrompt);
		Utils.setMessageToTextView(txtOTPResendPrompt, AppConstants.Messages.otpResendText);

		btnResendOTP = (Button) layout.findViewById(R.id.btnResendOTP);
		btnResendOTP.setTypeface(GreatBuyzApplication.getApplication().getFont());
		Utils.setMessageToButton(btnResendOTP, AppConstants.Messages.otpResendButtonText);
		btnResendOTP.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				resend = true;
				getActivity().showDialog(AppConstants.DialogConstants.LOADING_DIALOG, null);

				HashMap<String, String> m = new HashMap<String, String>();
				m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.RESENDOTP);
				// FlurryAgent.logEvent(AppConstants.Flurry.Registration, m);
				GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.Registration, m);

				String mdn = null;
				if (layoutEditMobileNumber.getVisibility() == View.VISIBLE)
					mdn = edtMDN.getText().toString().trim();
				else
					mdn = _data.getMDN();

				if (Utils.isNothing(mdn))
				{
					viewSwitcher.setDisplayedChild(VIEW_REGISTRATION);
					return;
				}

				completeRegistration(mdn);
			}
		});

		return layout;
	}

	private boolean isOTPExpired()
	{
		String strExpiry = _data.getConstant(AppConstants.Constants.otpExpiry);
		System.out.println("strExpiry "+strExpiry);
		if (!Utils.isNothing(strExpiry))
		{
			try
			{
				long otpExpiry = Integer.parseInt(strExpiry) * 60000;
				long otpTimestamp = _data.getOTPTimestamp();
				long currentTime = System.currentTimeMillis();
				System.out.println("otpExpiry "+otpExpiry+"otpTimestamp "+otpTimestamp+" currentTime "+currentTime);
				if (currentTime - otpTimestamp > otpExpiry)
					return true;
			}
			catch (Exception e)
			{
			}
		}
		return false;
	}

	private void getMDNFromUser()
	{
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					layoutEditMobileNumber.setVisibility(View.VISIBLE);
					viewSwitcher.invalidate();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	private void getMDNUsingGET(String url, Map<String, String> headers)
	{
		try
		{
			getActivity().showDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			GreatBuyzApplication.getServiceDelegate().getMDNFromNetworkGET(url, headers, new IOperationListener()
			{
				@Override
				public void onOperationCompleted(boolean pOperationComplitionStatus, String pMessageFromServer)
				{
					removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					// ////System.out.println(pOperationComplitionStatus);
					if (!pOperationComplitionStatus)
					{
						getMDNFromUser();
					}
				}
			});
		}
		catch (Exception e)
		{
			removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			getMDNFromUser();
			e.printStackTrace();
		}
	}

	private void getMDNUsingPOST(String url, Map<String, String> headers, final String postData)
	{
		try
		{
			getActivity().showDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			GreatBuyzApplication.getServiceDelegate().getMDNFromNetworkPOST(url, headers, postData, new IOperationListener()
			{
				@Override
				public void onOperationCompleted(boolean pOperationComplitionStatus, String pMessageFromServer)
				{
					// showAlertPopup( postData + " \n\n"+ pMessageFromServer
					// +"\n\n"+pMessageFromServer);

					removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					// ////System.out.println(pOperationComplitionStatus);
					if (!pOperationComplitionStatus)
					{
						getMDNFromUser();
					}
				}
			});
		}
		catch (Exception e)
		{
			removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			getMDNFromUser();
			e.printStackTrace();
		}
	}

	private void getMDNFromNetwork()
	{
		getMDN = false;
		String url = _data.getConstant(AppConstants.Constants.getMDNUrl);

		if (Utils.isNothing(url))
		{
			layoutEditMobileNumber.setVisibility(View.VISIBLE);
			return;
		}

		String method = _data.getConstant(AppConstants.Constants.getMDNMethod);
		String strHeaders = _data.getConstant(AppConstants.Constants.getMDNHeaderParams);
		Map<String, String> headers = null;
		try
		{
			headers = ResponseParser.getOperatorMDNURLHeaders(strHeaders);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		String postData = _data.getConstant(AppConstants.Constants.getMDNPostParams);

		if (HttpGet.METHOD_NAME.equalsIgnoreCase(method))
		{
			getMDNUsingGET(url, headers);
		}
		else if (HttpPost.METHOD_NAME.equalsIgnoreCase(method))
		{
			postData = postData.replace("{ip}", Utils.getIPAddress(true));
			getMDNUsingPOST(url, headers, postData);
		}
	}

	private void completeRegistration(final String mdn)
	{
		try
		{
			GreatBuyzApplication.getServiceDelegate().registerUser(mdn, new IOperationListener()
			{
				@Override
				public void onOperationCompleted(boolean pOperationComplitionStatus, String pMessageFromServer)
				{
					removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					// ////System.out.println(pOperationComplitionStatus);
					if (!pOperationComplitionStatus)
					{
						showMessageDialog(AppConstants.DialogConstants.REG_UNSUCCESSFUL_DIALOG,
								Utils.getMessageString(AppConstants.Messages.registrationFailMessage, R.string.registrationFailMessage));
						return;
					}
					if (resend)
						showMessageDialog(AppConstants.DialogConstants.REG_UNSUCCESSFUL_DIALOG, getResources().getString(R.string.otpResendButtonInfoText));
					msisdn = mdn;

					savePreferences("msisdn", mdn);
					showValidationView();
				}
			});
		}
		catch (Exception e)
		{
			removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			e.printStackTrace();
		}
	}

	private void subscribe(final String msisdn, final String channel, final String chargingMode, final String status,
			final String serviceKey) throws UnsupportedEncodingException
	{
		showOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
		GreatBuyzApplication.getServiceDelegate().subscribeToChannel(msisdn, channel, chargingMode, status, serviceKey,
				new IOperationListener()
				{
					@Override
					public void onOperationCompleted(boolean pOperationComplitionStatus, String pMessageFromServer)
					{
						removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
						GCMRegistrar.setRegisteredOnServer(GreatBuyzApplication.getApplication(), pOperationComplitionStatus);
						_data.updateIsUserSubscribedToGCM(pOperationComplitionStatus);
						_data.updateUserSubscriptionStatus(pMessageFromServer);
						if (pOperationComplitionStatus)
						{
							if (pMessageFromServer.equals(AppConstants.UserActivationStatus.ACTIVE))
							{
								// Show free deal
								//showFreeDeal();
								 startHomeScreen();
							}
							else if (pMessageFromServer.equals(AppConstants.UserActivationStatus.PENDING))
								showMessageDialog(AppConstants.DialogConstants.REG_MESSAGE_DIALOG_WITH_EXIT, Utils.getMessageString(
										AppConstants.Messages.subscription_status_pending_message,
										R.string.subscription_status_pending_message));
							else
								showMessageDialog(AppConstants.DialogConstants.REG_MESSAGE_DIALOG_WITH_EXIT, Utils.getMessageString(
										AppConstants.Messages.subscription_failed_message, R.string.subscription_failed_message));
						}
						else
							showMessageDialog(AppConstants.DialogConstants.REG_MESSAGE_DIALOG_WITH_EXIT, Utils.getMessageString(
									AppConstants.Messages.subscription_failed_message, R.string.subscription_failed_message));
					}
				});
	}

	private void updateServer(final String msisdn, final String channel, final String chargingMode, final String status,
			final String serviceKey)
	{
		final String httpErrorString = Utils.getMessageString(AppConstants.Messages.unableToConnect, R.string.unableToConnect);

		showOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
		try
		{
			String googleRegistrationId = _data.getGCMId();
			String imei = Utils.getAndroidId(getActivity());
			if (!Utils.isNothing(imei))
				GreatBuyzApplication.getDataController().updateAndroidId(imei);

			String imsi = _data.getIMSI();
			String clientVersion = Utils.getClientVersion();
			String[] locations = new String[] { _data.getUserCity() };
			String[] categories = null;
			List<String> categoryList = _data.getCategoriesList();
			if (categoryList != null && categoryList.size() > 0)
			{
				categories = new String[0];
				categories = categoryList.toArray(categories);

				// also set all categories as selected for this user
				_data.updateAllCategories();
			}

			int frequency = _data.getNotificationFrequency();

			GreatBuyzApplication.getServiceDelegate().updateUserInfo(locations, categories, null, googleRegistrationId, null, frequency,
					imei, imsi, clientVersion,null, new IOperationListener()
					{
						@Override
						public void onOperationCompleted(boolean p_OperationComplitionStatus, String p_MessageFromServer)
						{
							removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
							if (p_OperationComplitionStatus)
							{
								try
								{
									subscribe(msisdn, channel, chargingMode, status, serviceKey);
								}
								catch (Exception e)
								{
									e.printStackTrace();
									showMessageDialog(AppConstants.DialogConstants.REG_MESSAGE_DIALOG_WITH_EXIT, httpErrorString);
								}
							}
							else
							{
								showMessageDialog(AppConstants.DialogConstants.REG_MESSAGE_DIALOG_WITH_EXIT, httpErrorString);
							}
						}
					});
		}
		catch (Exception e)
		{
			removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			_data.updateIsUserSubscribedToGCM(false);
			showMessageDialog(AppConstants.DialogConstants.REG_MESSAGE_DIALOG_WITH_EXIT, httpErrorString);
			e.printStackTrace();
		}
	}

	public boolean shouldExitOnBack()
	{
		if (viewSwitcher.getDisplayedChild() == VIEW_REGISTRATION)
			return true;
		return false;
	}

	public void showRegistrationView()
	{
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					viewSwitcher.setDisplayedChild(VIEW_REGISTRATION);
					((RegistrationFragmentActivity) getActivity()).hideBackButton();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	private void showValidationView()
	{
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					viewSwitcher.setDisplayedChild(VIEW_VALIDATION);
					((RegistrationFragmentActivity) getActivity()).showBackButton();
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

	public void removeOtherDialog(final int which)
	{
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					getActivity().removeDialog(which);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public void showOtherDialog(final int which)
	{
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					getActivity().showDialog(which);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/*public void showFreeDeal()
	{
		try
		{
			showOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			GreatBuyzApplication.getServiceDelegate().getFreeDeals(new IOperationListener()
			{

				@Override
				public void onOperationCompleted(boolean p_OperationComplitionStatus, String p_MessageFromServer)
				{
					removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					if (p_OperationComplitionStatus)
					{
						String message = Utils.getMessageString(AppConstants.Messages.freeDealMessage, R.string.freeDealMessage);
						showMessageDialog(AppConstants.DialogConstants.FREE_DEAL_MESSAGE_DIALOG, message);
					}
					else
						startHomeScreen();
				}
			});
		}
		catch (Exception e)
		{
			removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			startHomeScreen();
			e.printStackTrace();
		}
	}*/

	public void startHomeScreen()
	{
		getActivity().runOnUiThread(new Runnable()
		{
			public void run()
			{
				try
				{
					getActivity().finish();
					Intent mainIntent = new Intent(getActivity(), SampleTabsStyled.class);
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

	private void savePreferences(String key, String value)
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String loadSavedPreferences(String key)
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		return sharedPreferences.getString(key, "");
	}

	public void showAlertPopup(final String Message)
	{
		getActivity().runOnUiThread(new Runnable()
		{

			@Override
			public void run()
			{
				AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

				// Setting Dialog Title
				alertDialog.setTitle("Resoponse");

				// Setting Dialog Message
				alertDialog.setMessage(Message);

				// Setting Icon to Dialog

				// Setting OK Button

				// Showing Alert Message
				alertDialog.show();

			}
		});

	}

}