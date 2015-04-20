package it.telecomitalia.timcoupon.ui;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import it.telecomitalia.timcoupon.R;
import it.telecomitalia.timcoupon.R.style;
import it.telecomitalia.timcoupon.service.DataController;
import it.telecomitalia.timcoupon.service.IOperationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.onmobile.hcoe.ui.dialog.LoadingDialog;
import com.onmobile.utils.AppConstants;
import com.onmobile.utils.Utils;

public class IPChangeActivity extends Activity
{
	EditText edtCurrentIP;
	EditText edtNewIP;
	Button btnCancel;
	Button btnSubmit;
	DataController _data;
	Spinner serverSelectionSpinner;
	String currentIP;
	private List<ServerDomain> serverNames = new ArrayList<ServerDomain>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ipchange);

		_data = GreatBuyzApplication.getDataController();
		
		edtCurrentIP = (EditText) findViewById(R.id.edtCurrentIP);
		serverSelectionSpinner = (Spinner) findViewById(R.id.serverSelection);
		GetSeverUrls geturls = new GetSeverUrls();
		geturls.execute();
		//edtNewIP = (EditText) findViewById(R.id.edtNewIP);

		btnCancel = (Button) findViewById(R.id.btnCancelIP);
		btnSubmit = (Button) findViewById(R.id.btnSubmitIP);

		currentIP = _data.getConstant(AppConstants.Constants.serverIP);
		
		if(Utils.isNothing(currentIP))
			currentIP = GreatBuyzApplication.getApplication().getServerIP();

		edtCurrentIP.setText(currentIP);

		
		
		
		
		btnCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						
						
						finish();
					}
				});
			}
		});

		btnSubmit.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int spinnerSelected = serverSelectionSpinner.getSelectedItemPosition();
				//final String newIP = edtNewIP.getText().toString();
				final String newIP = serverNames.get(spinnerSelected).url;//edtNewIP.getText().toString();

				if (Utils.isNothing(newIP))
				{
					//edtNewIP.setError("Set an IP");
					return;
				}

				final String mdn = _data.getMDN();
				try
				{
					
					showOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG, null);
					GreatBuyzApplication.getServiceDelegate().unsubscribeFromChannel(mdn, AppConstants.JSONKeys.GCM,
							AppConstants.JSONKeys.WAP, new IOperationListener()
							{
								@Override
								public void onOperationCompleted(boolean p_OperationComplitionStatus, String p_MessageFromServer)
								{
									removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
									if (p_OperationComplitionStatus)
									{
										HashMap<String, String> m = new HashMap<String, String>();
										m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.Unsubscribe);
										m.put(AppConstants.Flurry.MDN, mdn);
										m.put(AppConstants.Flurry.STATUS, AppConstants.Flurry.SUCCESS);
										GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.Unsubscribe, m);

										System.out.println("new IP setting **"+newIP);
										SharedPreferences prefs = GreatBuyzApplication.getApplication().getSharedPreferences();
										prefs.edit().putString(AppConstants.Constants.serverIP, newIP).commit();

										runOnUiThread(new Runnable()
										{
											public void run()
											{
												Toast.makeText(IPChangeActivity.this, "IP change success, exiting app.. ", Toast.LENGTH_LONG).show();
												
												GreatBuyzApplication.getApplication().resetDatabase();

												setResult(AppConstants.RESULT_EXIT_APP);
												finish();
											}
										});
									}
									else
									{
										runOnUiThread(new Runnable()
										{
											public void run()
											{
												Toast.makeText(IPChangeActivity.this, "IP change failed", Toast.LENGTH_LONG).show();
											}
										});
									}
								}
							});
				}
				catch (Exception e)
				{
					removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					runOnUiThread(new Runnable()
					{
						public void run()
						{
							Toast.makeText(IPChangeActivity.this, "IP change failed", Toast.LENGTH_LONG).show();
						}
					});
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args)
	{
		switch (id)
		{
			case AppConstants.DialogConstants.LOADING_DIALOG:
				LoadingDialog loadingDialog = new LoadingDialog(this, style.AlertDialogCustom);
				loadingDialog.setCancelable(false);
				return loadingDialog;
		}
		return super.onCreateDialog(id, args);
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
	private List<String> sponsorItems = new ArrayList<String>();
	
	private void setSponsorsArrayAdaptor() {
		
		for (int i = 0; i < serverNames.size(); i++) {
			//System.out.println("serverNames.get(i).name ***"+serverNames.get(i).name);
			sponsorItems.add(serverNames.get(i).name);
		}
		//System.out.println("sponsorItems ***"+sponsorItems.size());
		//System.out.println("serverNames  ***"+serverNames.size());
		ArrayAdapter<String> sponsorsAdapter = new ArrayAdapter<String>(IPChangeActivity.this,
				android.R.layout.simple_spinner_item, sponsorItems);
		sponsorsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		if (serverSelectionSpinner != null) {
			serverSelectionSpinner.setAdapter(sponsorsAdapter);
			//System.out.println("currentIP  ***"+currentIP);
			if(currentIP.equalsIgnoreCase("gbtim.turacomobile.com")){
				serverSelectionSpinner.setSelection(1);
			}else{
				serverSelectionSpinner.setSelection(0);
			}
			
		}
		
	}
	
	private class ServerDomain {
		
		private String name;
		private String url;
	}
	
	private class GetSeverUrls extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			
			if(serverNames != null){
				ServerDomain domain = new ServerDomain();
				domain.name = "GB_QA";
				domain.url = "103.19.90.82:8080";
				ServerDomain domain1 = new ServerDomain();
				domain1.name = "GB_PROD";
				domain1.url = "gbtim.turacomobile.com";
				serverNames.add(domain);
				serverNames.add(domain1);
				
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			if(serverNames.size() >0){
				setSponsorsArrayAdaptor();
			}
			
			
			super.onPostExecute(result);
		}
		
		
		
	}
	
}
