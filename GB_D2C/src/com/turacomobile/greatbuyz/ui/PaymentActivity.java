package com.turacomobile.greatbuyz.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acl.paychamp.android.Acknowledgement;
import com.acl.paychamp.android.CCBError;
import com.acl.paychamp.android.DialogError;
import com.acl.paychamp.android.Paychamp;
import com.acl.paychamp.android.Paychamp.DialogListener;
import com.acl.paychamp.util.DataValueException;
import com.acl.paychamp.util.Receipt;
import com.turacomobile.greatbuyz.GreatBuyzApplication;
import com.turacomobile.greatbuyz.R;
import com.turacomobile.greatbuyz.hcoe.ui.dialog.GenericDialog;
import com.turacomobile.greatbuyz.hcoe.ui.dialog.LoadingDialog;
import com.turacomobile.greatbuyz.service.IOperationListener;
import com.turacomobile.greatbuyz.utils.AppConstants;

public class PaymentActivity extends Activity
{
	public String	data;
	private TextView  titleView;
	private ImageView imgBack;
	public String paymentMode;
	public final int  NETWORK_ERROR_DIALOG		 = 100;
	public final int  LOADING_DIALOG			   = 103;
	public final int  EXIT_DIALOG				  = 104;
	public final int  PAYMENT_STATUS_DIALOG		= 110;
	public final int  PAYMENT_STATUS_SIMPLE_DIALOG = 111;
	public final int  PAYMENT_STATUS_PAYCHAMP	  = 222;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.help_view);
		data = getIntent().getStringExtra(AppConstants.SharedPrefKeys.help);
		 paymentMode = getIntent().getStringExtra("paymentMode");
		setContentView(R.layout.help_view);
		titleView = (TextView) findViewById(R.id.txt_help_title);
		titleView.setVisibility(View.GONE);
		imgBack = (ImageView) findViewById(R.id.imgBackArrow);
		imgBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		if (!GreatBuyzApplication.getApplication().isNetworkAvailable())
		{
			showErrorDialog(AppConstants.UserMessages.NETWORK_NOT_AVAILABLE);
		}
		else
		{
			if (paymentMode.equalsIgnoreCase("gift"))
			{
				System.out.println(" in giffriend condition ");
				giftAFriend(data);
			}
			else if (paymentMode.equalsIgnoreCase("purchage"))
			{
				purchageDeal(data);
			}
		}
		/*
		 * paymentWebViewClient = new PaymentWebViewClient(); webView = (WebView) findViewById(R.id.helpDetails); webView.setBackgroundColor(0); webView.setBackgroundResource(R.drawable.listbg); webView.setWebViewClient(paymentWebViewClient); webView.clearCache(true); webView.getSettings().setJavaScriptEnabled(true); webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); webView.addJavascriptInterface(new PaymentStatusParser(), "HTMLOUT"); webView.postUrl(paymentUrl, data.getBytes());
		 */
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
	}
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args)
	{
		String message = null;
		WebView statusView;
		switch (id)
		{
			case LOADING_DIALOG:
				LoadingDialog loadingDialog = new LoadingDialog(this, R.style.AlertDialogCustom);
				loadingDialog.setCancelable(false);
				return loadingDialog;
			case NETWORK_ERROR_DIALOG:
			{
				if (args.containsKey("message")) message = args.getString("message");
				final GenericDialog dialog = new GenericDialog(PaymentActivity.this, R.layout.status_message, R.style.AlertDialogCustom);
				dialog.setCancelable(false);
				Button btn = (Button) dialog.findViewById(R.id.status_btn);
				btn.setText(getString(R.string.ok));
				btn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							dialog.dismiss();
							finish();
						}
						catch (Exception e)
						{
							Log.e("LoginActivity Error message Ok button onClick() ", "Application is crashed due to exception --> " + e.toString());
							e.printStackTrace();
						}
					}
				});
				TextView msgText = (TextView) dialog.findViewById(R.id.msg);
				msgText.setText(message);
				msgText.setTypeface(GreatBuyzApplication.getApplication().getFont());
				TextView titleText = (TextView) dialog.findViewById(R.id.title);
				titleText.setText(AppConstants.UserMessages.TITLE_INFO);
				titleText.setTypeface(GreatBuyzApplication.getApplication().getFont());
				return dialog;
			}
			case EXIT_DIALOG:
			{
				if (args.containsKey("message")) message = args.getString("message");
				final GenericDialog dialog = new GenericDialog(this, R.layout.exit_message, R.style.AlertDialogCustom);
				dialog.setCancelable(false);
				Button btn = (Button) dialog.findViewById(R.id.yes_btn);
				btn.setText(getString(R.string.yes));
				btn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							removeDialog(EXIT_DIALOG);
							exit();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
				Button noBtn = (Button) dialog.findViewById(R.id.no_btn);
				noBtn.setText(getString(R.string.no));
				noBtn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							removeDialog(EXIT_DIALOG);
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
			case PAYMENT_STATUS_DIALOG:
			{
				if (args.containsKey("message")) message = args.getString("message");
				final GenericDialog dialog = new GenericDialog(this, R.layout.payment_status, R.style.AlertDialogCustom);
				dialog.setCancelable(false);
				Button btn = (Button) dialog.findViewById(R.id.btnOk);
				btn.setText(getString(R.string.ok));
				btn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							removeDialog(PAYMENT_STATUS_DIALOG);
							finish();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
				statusView = (WebView) findViewById(R.id.paymentStatusView);
				statusView.setBackgroundColor(0);
				statusView.setBackgroundResource(R.drawable.listbg);
				statusView.loadDataWithBaseURL(null, message, "text/html", "utf-8", null);
				TextView titleText1 = (TextView) dialog.findViewById(R.id.title);
				titleText1.setText(AppConstants.EMPTY_STRING);
				titleText1.setTypeface(GreatBuyzApplication.getApplication().getFont());
				return dialog;
			}
			case PAYMENT_STATUS_SIMPLE_DIALOG:
			{
				if (args.containsKey("message")) message = args.getString("message");
				removeDialog(LOADING_DIALOG);
				final GenericDialog dialog = new GenericDialog(PaymentActivity.this, R.layout.status_message, R.style.AlertDialogCustom);
				dialog.setCancelable(false);
				Button btn = (Button) dialog.findViewById(R.id.status_btn);
				btn.setText(getString(R.string.ok));
				btn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							dialog.dismiss();
							finish();
						}
						catch (Exception e)
						{
							Log.e("PaymentActivity Error message Ok button onClick() ", "Application is crashed due to exception --> " + e.toString());
							e.printStackTrace();
						}
					}
				});
				TextView msgText = (TextView) dialog.findViewById(R.id.msg);
				msgText.setText(message);
				msgText.setTypeface(GreatBuyzApplication.getApplication().getFont());
				TextView titleText = (TextView) dialog.findViewById(R.id.title);
				titleText.setText(AppConstants.UserMessages.TITLE_INFO);
				titleText.setTypeface(GreatBuyzApplication.getApplication().getFont());
				return dialog;
			}
			case PAYMENT_STATUS_PAYCHAMP:
			{
				message = "payment status";
				removeDialog(LOADING_DIALOG);
				final GenericDialog dialog = new GenericDialog(PaymentActivity.this, R.layout.status_message, R.style.AlertDialogCustom);
				dialog.setCancelable(false);
				Button btn = (Button) dialog.findViewById(R.id.status_btn);
				btn.setText(getString(R.string.ok));
				btn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							dialog.dismiss();
							finish();
						}
						catch (Exception e)
						{
							Log.e("PaymentActivity Error message Ok button onClick() ", "Application is crashed due to exception --> " + e.toString());
							e.printStackTrace();
						}
					}
				});
				TextView msgText = (TextView) dialog.findViewById(R.id.msg);
				msgText.setText(message);
				msgText.setTypeface(GreatBuyzApplication.getApplication().getFont());
				TextView titleText = (TextView) dialog.findViewById(R.id.title);
				titleText.setText(AppConstants.UserMessages.TITLE_INFO);
				titleText.setTypeface(GreatBuyzApplication.getApplication().getFont());
				return dialog;
			}
		}
		return super.onCreateDialog(id, args);
	}
	
	@Override
	public void onBackPressed()
	{
		// if (!isPaymentComplete)
		// {
		// Bundle b = new Bundle();
		// b.putString("message", getResources().getString(R.string.LeavePaymentPage));
		// showDialog(EXIT_DIALOG, b);
		// }
		exit();
	}
	
	private void exit()
	{
		super.onBackPressed();
	}
	
	private void showErrorDialog(final String message)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				Bundle b = new Bundle();
				b.putString("message", message);
				showDialog(NETWORK_ERROR_DIALOG, b);
			}
		});
	}
	
	private void giftAFriends(String data)
	{
		try
		{
			showDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			GreatBuyzApplication.getServiceDelegate().giftAFriend(data, new IOperationListener()
			{
				@Override
				public void onOperationCompleted(boolean p_OperationComplitionStatus, String p_MessageFromServer)
				{
					removeDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					try
					{
						System.out.println("paymentActivity giftAFriend ***" + p_MessageFromServer);
						JSONObject jo = new JSONObject(p_MessageFromServer);
						if (p_OperationComplitionStatus && jo.has("message"))
						{
							final Bundle bundle = new Bundle();
							bundle.putString("message", jo.getString("message"));
							runOnUiThread(new Runnable()
							{
								public void run()
								{
									showDialog(PAYMENT_STATUS_SIMPLE_DIALOG, bundle);
								}
							});
						}
						else
						{
							final Bundle bundle = new Bundle();
							bundle.putString("message", "Payment is not successful");
							runOnUiThread(new Runnable()
							{
								public void run()
								{
									showDialog(EXIT_DIALOG, bundle);
								}
							});
						}
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
			removeDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			// Log.d("Notification :", "Canceled");
			e.printStackTrace();
			onBackPressed();
		}
	}
	
	// private void purchageDeal(String data)
	// {
	// try
	// {
	// showDialog(AppConstants.DialogConstants.LOADING_DIALOG);
	// GreatBuyzApplication.getServiceDelegate().purchageDeal(data, new IOperationListener()
	// {
	// @Override
	// public void onOperationCompleted(boolean p_OperationComplitionStatus, String p_MessageFromServer)
	// {
	// removeDialog(AppConstants.DialogConstants.LOADING_DIALOG);
	// try {
	// System.out.println("paymentActivity giftAFriend ***"+p_MessageFromServer);
	// JSONObject jo = new JSONObject(p_MessageFromServer);
	// if (p_OperationComplitionStatus && jo.has("message"))
	// {
	// final Bundle bundle = new Bundle();
	// bundle.putString("message", jo.getString("message"));
	// runOnUiThread(new Runnable() {
	// public void run() {
	// showDialog(PAYMENT_STATUS_SIMPLE_DIALOG, bundle);
	// }
	// });
	// }
	// else
	// {
	// final Bundle bundle = new Bundle();
	// bundle.putString("message", "Payment is not successful");
	// runOnUiThread(new Runnable() {
	// public void run() {
	// showDialog(EXIT_DIALOG, bundle);
	// }
	// });
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// }
	// catch (Exception e)
	// {
	// removeDialog(AppConstants.DialogConstants.LOADING_DIALOG);
	// // Log.d("Notification :", "Canceled");
	// e.printStackTrace();
	// onBackPressed();
	// }
	// }
	private void purchageDealViaGB(String data)
	{
		try
		{
			showDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			GreatBuyzApplication.getServiceDelegate().purchageDealD2CViaGB(data, new IOperationListener()
			{
				@Override
				public void onOperationCompleted(boolean p_OperationComplitionStatus, String p_MessageFromServer)
				{
					removeDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					JSONObject jo = new JSONObject();
					try
					{
						System.out.println("paymentActivity giftAFriend ***" + p_MessageFromServer);
						
						if (p_OperationComplitionStatus )
						{
							jo = new JSONObject(p_MessageFromServer);
							final Bundle bundle = new Bundle();
							bundle.putString("message", jo.optString("message","Payment is successful. You will receive the voucher in some time"));
							runOnUiThread(new Runnable()
							{
								public void run()
								{
									showDialog(PAYMENT_STATUS_SIMPLE_DIALOG, bundle);
								}
							});
						}
						else
						{
							final Bundle bundle = new Bundle();
							bundle.putString("message", "Payment is not successful");
							runOnUiThread(new Runnable()
							{
								public void run()
								{
									showDialog(EXIT_DIALOG, bundle);
								}
							});
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						final Bundle bundle = new Bundle();
						//TODO : chnage this message when live
						bundle.putString("message", jo.optString("message","Payment is successful. You will receive the voucher in some time"));
						runOnUiThread(new Runnable()
						{
							public void run()
							{
								showDialog(PAYMENT_STATUS_SIMPLE_DIALOG, bundle);
							}
						});
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
	
	public static void dialogConfirm(final Context context)
	{
		// final GenericDialog dialog = new GenericDialog(context, R.layout.status_message, R.style.AlertDialogCustom);
		// dialog.setCancelable(false);
		// Button btn = (Button) dialog.findViewById(R.id.status_btn);
		// btn.setText("OK");
		// btn.setOnClickListener(new OnClickListener()
		// {
		// @Override
		// public void onClick(View v)
		// {
		// try
		// {
		// dialog.dismiss();
		// ((Activity) context).finish();
		// }
		// catch (Exception e)
		// {
		// Log.e("PaymentActivity Error message Ok button onClick() ", "Application is crashed due to exception --> "
		// + e.toString());
		// e.printStackTrace();
		// }
		// }
		// });
		//
		// TextView msgText = (TextView) dialog.findViewById(R.id.msg);
		// msgText.setText("message");
		// msgText.setTypeface(GreatBuyzApplication.getApplication().getFont());
		// TextView titleText = (TextView) dialog.findViewById(R.id.title);
		// titleText.setText(AppConstants.UserMessages.TITLE_INFO);
		// titleText.setTypeface(GreatBuyzApplication.getApplication().getFont());
		// dialog.show();
		((Activity) context).runOnUiThread(new Runnable()
		{
			public void run()
			{
				// new AlertDialog.Builder(context).setTitle("Purchase").setMessage("Success").setPositiveButton(android.R.string.ok,
				// new AlertDialog.OnClickListener() {
				// public void onClick(DialogInterface dialog, int which) {
				// dialog.dismiss();
				// ((Activity) context).finish();
				// }
				// }).setCancelable(true).create().show();
				//
				final GenericDialog dialog = new GenericDialog(context, R.layout.status_message, R.style.AlertDialogCustom);
				dialog.setCancelable(false);
				Button btn = (Button) dialog.findViewById(R.id.status_btn);
				btn.setText("OK");
				btn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							dialog.dismiss();
							((Activity) context).finish();
						}
						catch (Exception e)
						{
							Log.e("PaymentActivity Error message Ok button onClick() ", "Application is crashed due to exception --> " + e.toString());
							e.printStackTrace();
						}
					}
				});
				TextView msgText = (TextView) dialog.findViewById(R.id.msg);
				msgText.setText("message");
				msgText.setTypeface(GreatBuyzApplication.getApplication().getFont());
				TextView titleText = (TextView) dialog.findViewById(R.id.title);
				titleText.setText(AppConstants.UserMessages.TITLE_INFO);
				titleText.setTypeface(GreatBuyzApplication.getApplication().getFont());
				dialog.show();
			}
		});
	}
	
	private String billingDomain = "bill.paychamp.com";
	
	private void giftAFriend(String data)
	{
		AuthDialogListeners auth = new AuthDialogListeners(getApplicationContext());
		// EditText mContentType = (EditText) findViewById(R.id.contentType);
		// EditText mPrice = (EditText) findViewById(R.id.price);
		// EditText mCountry = (EditText) findViewById(R.id.country);
		// EditText mAccountId = (EditText) findViewById(R.id.accountId);
		// EditText mPassKey = (EditText) findViewById(R.id.passKey);
		// EditText mProductName = (EditText) findViewById(R.id.productName);
		// EditText mImageUri = (EditText) findViewById(R.id.imageUri);
		String mContentType = "8";
		String mPrice = "1";
		String mCountry = "IN";
		String mAccountId = "19204455";
		String mPassKey = "1234";
		String mProductName = "speed-car.gif";
		String mImageUri = "";
		String transRef = "GB_" + new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
		
		//

		
		//
		
		try
		{
			new Paychamp(mAccountId, mPassKey, this, billingDomain).requestPayment(auth, mProductName, Double.parseDouble(mPrice), mCountry, Integer.parseInt(mContentType), transRef, URLEncoder.encode(mImageUri, "UTF-8"));
			JSONObject receiptParsed = new JSONObject();
			try
			{
				JSONObject extraData = new JSONObject(data);
			
				receiptParsed.put("paymentMode", "paychamp");
				receiptParsed.put("dealId",  extraData.getString(AppConstants.PaymentScreen.DEAL_ID));
				receiptParsed.put("mdn", extraData.getString(AppConstants.PaymentScreen.MDN));
				receiptParsed.put("location", extraData.optString(AppConstants.PaymentScreen.LOCATIONS));
				receiptParsed.put("accountId", mAccountId);
				receiptParsed.put("transaction_ref", transRef);
				receiptParsed.put("price", mPrice);
				receiptParsed.put("itemRef", mProductName);
				receiptParsed.put("contentSize", Integer.parseInt("10"));
				receiptParsed.put("countryCode", mCountry);
				receiptParsed.put("contentType", mContentType);
				receiptParsed.put("merchantId", "");
				receiptParsed.put("status", -1);
				receiptParsed.put("operator", null);
				receiptParsed.put("transId", null);
				receiptParsed.put("currency", null);
				//
				/*
				 {"isGiftaFriend" : "true", 
				 "friendsData" : {"friendName":"gullu","friendPhone":"+919989888888","message":"Its for you dude","myName":"yadav"}}
				 			giftData.putOpt(AppConstants.PaymentScreen.DEAL_ID, dealId);
			giftData.putOpt(AppConstants.PaymentScreen.MDN, mdn);
			giftData.put(AppConstants.PaymentScreen.FRIEND_MSISDN, friendMsisdn);
			giftData.put(AppConstants.PaymentScreen.FRIEND_NAME, friendName);
			giftData.put(AppConstants.PaymentScreen.MESSAGE, message);
			giftData.put(AppConstants.PaymentScreen.SENDER_NAME, senderName);
			giftData.put(AppConstants.PaymentScreen.DEAL_CHANNEL, "wap");
			giftData.put(AppConstants.PaymentScreen.SM_MODE, "");
			giftData.put(AppConstants.PaymentScreen.REFERRER, "");
			giftData.put(AppConstants.PaymentScreen.CHANNEL_OF_ACTIVATION, "wap");
				 */
				receiptParsed.put("isGiftaFriend", true );
				receiptParsed.put("friendPhone",  extraData.getString(AppConstants.PaymentScreen.FRIEND_MSISDN) );
				receiptParsed.put("friendName", extraData.getString(AppConstants.PaymentScreen.FRIEND_NAME) );
				receiptParsed.put("giftMessage", extraData.getString(AppConstants.PaymentScreen.MESSAGE) );
				receiptParsed.put("myName", extraData.getString(AppConstants.PaymentScreen.SENDER_NAME) );
				//
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				receiptParsed.put("timeStamp",formatter);
				//
				//receiptData.put("response_data", receiptParsed);
				purchageDealViaGBInitial(receiptParsed.toString());
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (NumberFormatException e)
		{
			Toast.makeText(getApplicationContext(), "Certain fields are not formatted correctly ! " + e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		catch (DataValueException e)
		{
			Toast.makeText(getApplicationContext(), e.getField() + " -- " + e.getErrorMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	
	public void purchageDeal(String data)
	{
		AuthDialogListeners auth = new AuthDialogListeners(getApplicationContext());
		// EditText mContentType = (EditText) findViewById(R.id.contentType);
		// EditText mPrice = (EditText) findViewById(R.id.price);
		// EditText mCountry = (EditText) findViewById(R.id.country);
		// EditText mAccountId = (EditText) findViewById(R.id.accountId);
		// EditText mPassKey = (EditText) findViewById(R.id.passKey);
		// EditText mProductName = (EditText) findViewById(R.id.productName);
		// EditText mImageUri = (EditText) findViewById(R.id.imageUri);
		String mContentType = "8";
		String mPrice = "1";
		String mCountry = "IN";
		String mAccountId = "19204455";
		String mPassKey = "1234";
		String mProductName = "speed-car.gif";
		String mImageUri = "";
		String transRef = "GB_" + new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
		
		//

		
		//
		
		try
		{
			new Paychamp(mAccountId, mPassKey, this, billingDomain).requestPayment(auth, mProductName, Double.parseDouble(mPrice), mCountry, Integer.parseInt(mContentType), transRef, URLEncoder.encode(mImageUri, "UTF-8"));
			JSONObject receiptParsed = new JSONObject();
			try
			{
				JSONObject extraData = new JSONObject(data);
			
				receiptParsed.put("paymentMode", "paychamp");
				receiptParsed.put("dealId",  extraData.getString(AppConstants.PaymentScreen.DEAL_ID));
				receiptParsed.put("mdn", extraData.getString(AppConstants.PaymentScreen.MDN));
				receiptParsed.put("location", extraData.optString(AppConstants.PaymentScreen.LOCATIONS));
				receiptParsed.put("accountId", mAccountId);
				receiptParsed.put("transaction_ref", transRef);
				receiptParsed.put("price", mPrice);
				receiptParsed.put("itemRef", mProductName);
				receiptParsed.put("contentSize", Integer.parseInt("10"));
				receiptParsed.put("countryCode", mCountry);
				receiptParsed.put("contentType", mContentType);
				receiptParsed.put("merchantId", "");
				receiptParsed.put("status", -1);
				receiptParsed.put("operator", null);
				receiptParsed.put("transId", null);
				receiptParsed.put("currency", null);
				//
				/*
				 {"isGiftaFriend" : "true", 
				 "friendsData" : {"friendName":"gullu","friendPhone":"+919989888888","message":"Its for you dude","myName":"yadav"}}
				 			giftData.putOpt(AppConstants.PaymentScreen.DEAL_ID, dealId);
			giftData.putOpt(AppConstants.PaymentScreen.MDN, mdn);
			giftData.put(AppConstants.PaymentScreen.FRIEND_MSISDN, friendMsisdn);
			giftData.put(AppConstants.PaymentScreen.FRIEND_NAME, friendName);
			giftData.put(AppConstants.PaymentScreen.MESSAGE, message);
			giftData.put(AppConstants.PaymentScreen.SENDER_NAME, senderName);
			giftData.put(AppConstants.PaymentScreen.DEAL_CHANNEL, "wap");
			giftData.put(AppConstants.PaymentScreen.SM_MODE, "");
			giftData.put(AppConstants.PaymentScreen.REFERRER, "");
			giftData.put(AppConstants.PaymentScreen.CHANNEL_OF_ACTIVATION, "wap");
				 */
				receiptParsed.put("isGiftaFriend", false );
				
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				receiptParsed.put("timeStamp",formatter);
				//
				//receiptData.put("response_data", receiptParsed);
				purchageDealViaGBInitial(receiptParsed.toString());
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (NumberFormatException e)
		{
			Toast.makeText(getApplicationContext(), "Certain fields are not formatted correctly ! " + e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		catch (DataValueException e)
		{
			Toast.makeText(getApplicationContext(), e.getField() + " -- " + e.getErrorMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	
	private void purchageDealViaGBInitial(String data)
	{
		try
		{
			showDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			GreatBuyzApplication.getServiceDelegate().purchageDealD2CViaGB(data, new IOperationListener()
			{
				@Override
				public void onOperationCompleted(boolean p_OperationComplitionStatus, String p_MessageFromServer)
				{
					removeDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					try
					{
						System.out.println("paymentActivity purchageDealD2CViaGB ***" + p_MessageFromServer);
						//JSONObject jo = new JSONObject(p_MessageFromServer);
//						if (p_OperationComplitionStatus)
//						{
//							final Bundle bundle = new Bundle();
//							bundle.putString("message", ("message"));
//							runOnUiThread(new Runnable()
//							{
//								public void run()
//								{
//									showDialog(PAYMENT_STATUS_SIMPLE_DIALOG, bundle);
//								}
//							});
//						}
//						else
//						{
//							final Bundle bundle = new Bundle();
//							bundle.putString("message", "Payment is not successful");
//							runOnUiThread(new Runnable()
//							{
//								public void run()
//								{
//									showDialog(EXIT_DIALOG, bundle);
//								}
//							});
//						}
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
			removeDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			// Log.d("Notification :", "Canceled");
			e.printStackTrace();
			onBackPressed();
		}
	}
	
	public class AuthDialogListeners implements DialogListener
	{
		private Context  context;
		public final int PAYMENT_STATUS_SIMPLE_DIALOG = 111;
		
		public AuthDialogListeners(Context context)
		{
			this.context = context;
		}
		
		public void onComplete(Receipt receipt)
		{
			// Fetch values from receipt
			//Toast.makeText(context, "Transaction ID:: " + receipt.getTransactionId() + "\nStatus=" + receipt.getStatus(), Toast.LENGTH_LONG).show();
			Acknowledgement auth = new Acknowledgement(receipt.getTransactionId());
			auth.execute("");
			// PaymentActivity.dialogConfirm(context);
			/*
			 * {"paymentMode":"paychamp","dealId":"33434","mdn":"99898989888","location":"33434","itemdesc":"speed-car.gif", "trxid":19060191,"server_server":false,
			 * "time":"2014-07-15 10:23:00","price":1.0,"account_id":19204455,"tranref":"1400216406215", "contenttype":17,
			 * "merchant_id":"20140502173815","msg":0,"operator":"idea","currency":"INR"}
			 */
			/*
			{"paymentMode":"paychamp","dealId":"1234","mdn":"8904204261","location":["Jaipur"],"accountId":"1234",
				"transaction_ref":"1234","price":10.0,"itemRef":"abcdre",
				"contentSize":"10","countryCode":"INDIA","contentType":"abcd","merchantId":"1234","status":"0",
				"operator":"IDEA","transId":"82947","currency":"INDIA","timeStamp":"89487942"}
				*/
			//JSONObject receiptData = new JSONObject();
			JSONObject receiptParsed = new JSONObject();
			try
			{
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				JSONObject extraData = new JSONObject(data);
				receiptParsed.put("paymentMode", "paychamp");
				receiptParsed.put("dealId",  extraData.getString(AppConstants.PaymentScreen.DEAL_ID));
				receiptParsed.put("mdn", extraData.getString(AppConstants.PaymentScreen.MDN));
				receiptParsed.put("location", extraData.optString(AppConstants.PaymentScreen.LOCATIONS));
				receiptParsed.put("accountId", receipt.getAccountId());
				receiptParsed.put("transaction_ref", receipt.getTransactionRef());
				receiptParsed.put("price", receipt.getPrice());
				receiptParsed.put("itemRef", receipt.getItemDescription());
				receiptParsed.put("contentSize", Integer.parseInt("10"));
				receiptParsed.put("countryCode", "IN");
				receiptParsed.put("contentType", receipt.getContentType());
				receiptParsed.put("merchantId", "");
				receiptParsed.put("status", receipt.getStatus());
				receiptParsed.put("operator", receipt.getOperator());
				receiptParsed.put("transId", receipt.getTransactionId());
				receiptParsed.put("currency", receipt.getCurrency());
				//
				if (paymentMode.equalsIgnoreCase("gift"))
				{
					System.out.println(" in giffriend condition ");
					receiptParsed.put("isGiftaFriend", true );
					receiptParsed.put("friendPhone",  extraData.getString(AppConstants.PaymentScreen.FRIEND_MSISDN) );
					receiptParsed.put("friendName", extraData.getString(AppConstants.PaymentScreen.FRIEND_NAME) );
					receiptParsed.put("giftMessage", extraData.getString(AppConstants.PaymentScreen.MESSAGE) );
					receiptParsed.put("myName", extraData.getString(AppConstants.PaymentScreen.SENDER_NAME) );
				}
				else if (paymentMode.equalsIgnoreCase("purchage"))
				{
					receiptParsed.put("isGiftaFriend", false);
				}
				
				//
				receiptParsed.put("timeStamp", formatter.parse(receipt.getTime()));
				
				 
				// yyyy-MM-dd HH:mm:ss paymentMode dealId  mdn  
				//receiptData.put("response_data", receiptParsed);
				purchageDealViaGB(receiptParsed.toString());
		
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ParseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void onCCBError(CCBError e)
		{
			finish();
			//Toast.makeText(context, "CCB Error ! " + e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		
		@Override
		public void onError(DialogError e)
		{
			//Toast.makeText(context, "Dialog error !", Toast.LENGTH_LONG).show();
			finish();
		}
		
		@Override
		public void onCancel()
		{
			finish();
		//	Toast.makeText(context, "Dialog cancelled !", Toast.LENGTH_LONG).show();
			// PaymentActivity.dialogConfirm(context);
//			final Bundle bundle = new Bundle();
//			bundle.putString("message", "message");
//			runOnUiThread(new Runnable()
//			{
//				public void run()
//				{
//					showDialog(PAYMENT_STATUS_PAYCHAMP, bundle);
//				}
//			});
		}
		
	}
	

	
}
