package it.telecomitalia.timcoupon.ui;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import it.telecomitalia.timcoupon.R;
import it.telecomitalia.timcoupon.data.NotificationDTO;
import it.telecomitalia.timcoupon.service.ResponseParser;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onmobile.utils.AppConstants;
import com.onmobile.utils.Utils;

public class NotificationActivity extends Activity
{
	ImageView imgBack;
	private LinearLayout mBackNavigation;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		loadNewIntent(getIntent());
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		// FlurryAgent.onPageView();
		GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit("Notification");
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		loadNewIntent(intent);
	}

	@Override
	public void onBackPressed()
	{
		finish();
		if (getIntent().getBooleanExtra(AppConstants.JSONKeys.START_HOME_SCREEN, false))
		{
			Intent mainIntent = new Intent(this, SampleTabsStyled.class);
			mainIntent.addCategory("com.jakewharton.android.viewpagerindicator.sample.SAMPLE");
			mainIntent.putExtra("com.jakewharton.android.viewpagerindicator.sample.Path", "Tabs");
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(mainIntent);
		}
	}

	private void loadNewIntent(Intent intent)
	{
		setContentView(R.layout.notification_view);
		NotificationDTO notif = null;
		try
		{
			notif = ResponseParser.getNotification(new JSONObject(intent.getAction()));
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}

		// imgBack = (ImageView) findViewById(R.id.imgBackArrow);
		// imgBack.setOnClickListener(new OnClickListener()
		// {
		// @Override
		// public void onClick(View v)
		// {
		// onBackPressed();
		// }
		// });
		mBackNavigation = (LinearLayout) findViewById(R.id.back_navigation);
		mBackNavigation.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onBackPressed();
			}
		});

		RelativeLayout myDealsShortcut = (RelativeLayout) findViewById(R.id.imgMyDeals);
		myDealsShortcut.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setResult(AppConstants.RESULT_CLICK_MYCOUPON);
				finish();
			}
		});

		ImageView menuView = (ImageView) findViewById(R.id.imgMenu);
		menuView.setVisibility(View.GONE);

		// String title =
		// getIntent().getStringExtra(AppConstants.JSONKeys.NAME);
		String title = notif.getTitle();
		TextView titleView = (TextView) findViewById(R.id.txt_help_title);
		titleView.setText(title);
		titleView.setTypeface(GreatBuyzApplication.getApplication().getFont());

		// String text =
		// getIntent().getStringExtra(AppConstants.SharedPrefKeys.help);
		String text = notif.getDescription();
		WebView webView = (WebView) findViewById(R.id.helpDetails);

		String htmlStart = "<html>" + "<style type=\"text/css\"> \n" + "   @font-face { \n" + "       font-family: MyFont; \n"
				+ "       src: url(\"file:///android_asset/fonts/fonts/ARLRDBD.TTF\") \n" + "   } \n" + "   body { \n"
				+ "       font-family: MyFont; \n" + "   } \n" + "  </style> \n"
				+ "<body><div style='word-wrap:break-word'><font color=\"black\" size=\"2\">";
		String htmlEnd = "</font></div></body></html>";

		webView.setBackgroundColor(Color.WHITE);
		webView.loadDataWithBaseURL("file:///android_asset/", htmlStart + text + htmlEnd, "text/html", "utf-8", null);

		// int type = getIntent().getIntExtra(AppConstants.JSONKeys.TYPE,
		// AppConstants.NotificationType.TEXT);
		int type = notif.getType();
		if (type == AppConstants.NotificationType.URL)
		{
			// final String url =
			// getIntent().getStringExtra(AppConstants.JSONKeys.URL);
			final String url = notif.getUrl();
			Button btnVisit = (Button) findViewById(R.id.btnClickHere);
			btnVisit.setTypeface(GreatBuyzApplication.getApplication().getFont());
			Utils.setMessageToButton(btnVisit, AppConstants.Messages.btnNotifVisitText);
			btnVisit.setVisibility(View.VISIBLE);
			btnVisit.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Utils.launchUri(NotificationActivity.this, url);
				}
			});
		}
	}
}
