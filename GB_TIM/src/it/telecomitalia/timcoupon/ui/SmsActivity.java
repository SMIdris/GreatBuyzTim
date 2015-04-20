package it.telecomitalia.timcoupon.ui;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import it.telecomitalia.timcoupon.R;
import it.telecomitalia.timcoupon.data.NotificationDTO;
import it.telecomitalia.timcoupon.service.ResponseParser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.onmobile.hcoe.ui.dialog.GenericDialog;
import com.onmobile.hcoe.ui.dialog.LoadingDialog;
import com.onmobile.utils.AppConstants;
import com.onmobile.utils.Utils;

public class SmsActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.splash);
		showLoadingDialog();
		Uri data = getIntent().getData();
		String strData = data.toString();
		JSONObject message = new JSONObject();
		String url = strData;
		List<NameValuePair> params;
		try
		{
			params = URLEncodedUtils.parse(new URI(url), "UTF-8");
			if (params.size() > 0)
			{
				for (NameValuePair param : params)
				{
					System.out.println(param.getName() + " : " + param.getValue());
					message.put("_id", param.getValue());
					message.put("type", "deal");
					message.put("title", "SMS Deal");
					NotifyUserSMS(SmsActivity.this, message.toString());
				}
			}
			else
			{
				String invalidDealId = Utils.getMessageString(AppConstants.Messages.emptyDeal, R.string.emptyDeal);
				removeLoadingDialog();
				showMessageDialog(invalidDealId);
			}
		}
		catch (URISyntaxException e)
		{
			String invalidDealId = Utils.getMessageString(AppConstants.Messages.emptyDeal, R.string.emptyDeal);
			removeLoadingDialog();
			showMessageDialog(invalidDealId);
		}
		catch (JSONException e)
		{
			String invalidDealId = Utils.getMessageString(AppConstants.Messages.emptyDeal, R.string.emptyDeal);
			removeLoadingDialog();
			showMessageDialog(invalidDealId);
		}
		catch (Exception e)
		{
			String invalidDealId = Utils.getMessageString(AppConstants.Messages.emptyDeal, R.string.emptyDeal);
			removeLoadingDialog();
			showMessageDialog(invalidDealId);
		}
	}
	
	public void NotifyUserSMS(Context context, String message)
	{
		// //System.out.println("message : " + message);
		NotificationDTO notif = null;
		try
		{
			notif = ResponseParser.getNotification(new JSONObject(message));
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		if (notif == null) return;
		Intent notificationIntent = null;
		notificationIntent = new Intent(context, SampleTabsStyled.class);
		notificationIntent.putExtra(AppConstants.DEAL_SCREEN, true);
		notificationIntent.setAction(message);
		if (isApplicationRunning())
		{
			notificationIntent.putExtra(AppConstants.JSONKeys.START_HOME_SCREEN, false);
		}
		else
		{
			notificationIntent.putExtra(AppConstants.JSONKeys.START_HOME_SCREEN, true);
		}
		
		startActivity(notificationIntent);
		finish();
		removeLoadingDialog();
	}
	
	public static boolean isApplicationRunning()
	{
		Context appContext = GreatBuyzApplication.getApplication();
		ActivityManager activityManager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);
		System.out.println("services.get(0).topActivity.getPackageName().toString()" + services.get(0).topActivity.getPackageName().toString());
		System.out.println("appContext.getPackageName().toString()" + appContext.getPackageName().toString());
		if (services.get(0).topActivity.getPackageName().toString().contains(appContext.getPackageName().toString())) { return true; }
		return false;
	}
	
	public void showMessageDialog(final String message)
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
	
	public void showLoadingDialog()
	{
		try
		{
			showDialog(AppConstants.DialogConstants.LOADING_DIALOG, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void removeLoadingDialog()
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
							finish();
//							Intent notificationIntent = null;
//							notificationIntent = new Intent(SmsActivity.this, SampleTabsStyled.class);
//							
//								notificationIntent.putExtra(AppConstants.JSONKeys.START_HOME_SCREEN, true);
//							
//							removeLoadingDialog();
//							startActivity(notificationIntent);
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
		}
		return super.onCreateDialog(id, args);
	}
}
