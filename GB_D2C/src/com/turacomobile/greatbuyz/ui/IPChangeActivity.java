package com.turacomobile.greatbuyz.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import com.turacomobile.greatbuyz.GreatBuyzApplication;
import com.turacomobile.greatbuyz.R;
import com.turacomobile.greatbuyz.hcoe.ui.dialog.LoadingDialog;
import com.turacomobile.greatbuyz.service.DataController;
import com.turacomobile.greatbuyz.service.IOperationListener;
import com.turacomobile.greatbuyz.utils.AppConstants;
import com.turacomobile.greatbuyz.utils.Utils;

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
				LoadingDialog loadingDialog = new LoadingDialog(this, R.style.AlertDialogCustom);
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
			System.out.println("serverNames.get(i).name ***"+serverNames.get(i).name);
			sponsorItems.add(serverNames.get(i).name);
		}
		System.out.println("sponsorItems ***"+sponsorItems.size());
		System.out.println("serverNames  ***"+serverNames.size());
		ArrayAdapter<String> sponsorsAdapter = new ArrayAdapter<String>(IPChangeActivity.this,
				android.R.layout.simple_spinner_item, sponsorItems);
		sponsorsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		if (serverSelectionSpinner != null) {
			serverSelectionSpinner.setAdapter(sponsorsAdapter);
			System.out.println("currentIP  ***"+currentIP);
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
			
			
			HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response;
	        String responseString = null;
	        try {
	            
	        	GreatBuyzApplication app = GreatBuyzApplication.getApplication();
	        	
	        	HttpGet getRequest = new HttpGet(app.getBaseURL()+AppConstants.URIParts.GET_IP_CHANGE_SERVERS);
	        	getRequest.addHeader("key", "ee4ef426-8cab-4853-ace0-2c1660284a7c");
	        	getRequest.addHeader("channel", "wap");
	        	response = httpclient.execute(getRequest);
	        	
	            StatusLine statusLine = response.getStatusLine();
	            System.out.println("statusLine.getStatusCode() "+statusLine.getStatusCode());
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                responseString = out.toString();
	                System.out.println("responseString **"+responseString);
	            } else{
	                //Closes the connection.
	                response.getEntity().getContent().close();
	                throw new IOException(statusLine.getReasonPhrase());
	            }
	        } catch (ClientProtocolException e) {
	            //TODO Handle problems..
	        } catch (IOException e) {
	            //TODO Handle problems..
	        }
			JSONObject serversObject;
			try {
				serversObject = new JSONObject(responseString);
				JSONArray serverList = serversObject.getJSONArray("serverConifgInformation");
				for (int i = 0; i < serverList.length(); i++) {
					JSONObject jo = (JSONObject)(serverList.get(i));
					
					ServerDomain domain = new ServerDomain();
					domain.name = jo.getString("serverName");
					domain.url = jo.getString("serverUrl");
					serverNames.add(domain);
				}
			} catch (JSONException e) {
				e.printStackTrace();
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
