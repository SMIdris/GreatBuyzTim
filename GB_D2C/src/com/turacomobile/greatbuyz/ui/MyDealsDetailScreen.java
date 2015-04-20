package com.turacomobile.greatbuyz.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.turacomobile.greatbuyz.GreatBuyzApplication;
import com.turacomobile.greatbuyz.R;
import com.turacomobile.greatbuyz.data.Purchase;
import com.turacomobile.greatbuyz.hcoe.ui.dialog.GenericDialog;
import com.turacomobile.greatbuyz.utils.AppConstants;

public class MyDealsDetailScreen extends Activity
{
	private Purchase purchase = null;
	private int selectedIndex = 0;
	private String contactDetails;
	private String offer;
	private ImageView menuView;
	private ImageView imgBack;
	private RelativeLayout imgMyDeals;
	private PaymentWebViewClient paymentWebViewClient;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detailed_mydeal);
		
		imgBack = (ImageView) findViewById(R.id.imgBackArrow);
		imgBack.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onBackPressed();
			}
		});

		menuView = (ImageView) findViewById(R.id.imgMenu);
		menuView.setVisibility(View.GONE);
		
		imgMyDeals =(RelativeLayout) findViewById(R.id.imgMyDeals);
		imgMyDeals.setVisibility(View.GONE);
		
		selectedIndex = getIntent().getIntExtra("index", 0);
		contactDetails = getIntent().getStringExtra("contactDetails");
		offer = getIntent().getStringExtra("offer");

		purchase = GreatBuyzApplication.getDataController().getMyDealsDTO().getMyDealsList().get(selectedIndex);

		TextView txtDetailDesc = (TextView) findViewById(R.id.txtDetailDesc);
		txtDetailDesc.setText(purchase.getDealName());
		txtDetailDesc.setTypeface(GreatBuyzApplication.getApplication().getFont());

		TextView txtCouponId = (TextView) findViewById(R.id.txt_couponid);
		txtCouponId.setText(purchase.getCoupon().getCouponId());
		txtCouponId.setTypeface(GreatBuyzApplication.getApplication().getFont());

		TextView txtIssueDate = (TextView) findViewById(R.id.txt_issueDate);
		System.out.println("purchase.getCoupon().getIssueDate() **"+purchase.getCoupon().getIssueDate().getDate());
		String issueDate = (String) DateFormat.format("dd MMM yyyy", purchase.getCoupon().getIssueDate());
		System.out.println("issueDate **"+issueDate);
		txtIssueDate.setText(issueDate);
		txtIssueDate.setTypeface(GreatBuyzApplication.getApplication().getFont());

		TextView txtCouponIdTag = (TextView) findViewById(R.id.txt_couponIdTag);
		txtCouponIdTag.setTypeface(GreatBuyzApplication.getApplication().getFont());
		TextView txtIssueDateTag = (TextView) findViewById(R.id.txt_issueDateTag);
		txtIssueDateTag.setTypeface(GreatBuyzApplication.getApplication().getFont());

		paymentWebViewClient = new PaymentWebViewClient();

		WebView webView;
		webView = (WebView) findViewById(R.id.webDetailDetails);
		String htmlStart = "<html>" +
				"<style type=\"text/css\"> \n" + 
					"   @font-face { \n" + 
					"       font-family: MyFont; \n" + 
					"       src: url(\"file:///android_asset/fonts/fonts/Helvetica LT Condensed Light_0.ttf\") \n" + 
					"   } \n" + 
					"   body { \n" + 
					"       font-family: MyFont; \n" + 
					"   } \n" + 
					"  </style> \n" + 
				"<body><font color=\"black\" size=\"2\">";
				String htmlEnd = "</font></body></html>";
				
		webView.setBackgroundColor(0);
		webView.setBackgroundResource(R.drawable.listbg);
		StringBuffer html = new StringBuffer();
		html.append(AppConstants.HTMLHEADERCSS);
		html.append(AppConstants.CONTACTDETAILHEADER1);
		html.append(getResources().getString(R.string.contactDetailHeaderText));
		html.append(AppConstants.CONTACTDETAILHEADER2);
		html.append(contactDetails);
		html.append(AppConstants.OFFERHEADER1);
		html.append(getResources().getString(R.string.offerHeaderText));
		html.append(AppConstants.OFFERHEADER2);
		html.append(offer);
		webView.clearCache(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(paymentWebViewClient);
		webView.loadDataWithBaseURL("file:///android_asset/", htmlStart + html.toString() + htmlEnd, "text/html", "utf-8", null);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		FlurryAgent.onPageView();
	}


	@Override
	protected Dialog onCreateDialog(int id, Bundle b)
	{
		String message = b.getString(AppConstants.JSONKeys.MESSAGE);
		final GenericDialog dialog = new GenericDialog(MyDealsDetailScreen.this, R.layout.status_message, R.style.AlertDialogCustom);
		dialog.setCancelable(false);
		Button btn = (Button) dialog.findViewById(R.id.status_btn);
		btn.setText(getString(R.string.ok));
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
		TextView titleText = (TextView) dialog.findViewById(R.id.title);
		titleText.setText(getResources().getString(R.string.titleInfo));
		titleText.setTypeface(GreatBuyzApplication.getApplication().getFont());
		return dialog;
	}

	private class PaymentWebViewClient extends WebViewClient
	{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			Uri uri = Uri.parse(url);

			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			try
			{
				startActivity(intent);
			}
			catch (Exception e)
			{
				Bundle b = new Bundle();
				b.putString(AppConstants.JSONKeys.MESSAGE, getResources().getString(R.string.NoApplicationFound));
				showDialog(AppConstants.DialogConstants.MESSAGE_DIALOG, b);
			}
			return true;
		}
	}
}
