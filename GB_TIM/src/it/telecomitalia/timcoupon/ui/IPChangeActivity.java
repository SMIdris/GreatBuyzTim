package it.telecomitalia.timcoupon.ui;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import it.telecomitalia.timcoupon.R;
import it.telecomitalia.timcoupon.R.style;
import it.telecomitalia.timcoupon.service.DataController;
import it.telecomitalia.timcoupon.service.IOperationListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
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
	private EditText edtCurrentIP;
	private EditText edtNewIP;
	private EditText edtCurrentIPNew;
	private Button btnCancel;
	private Button btnSubmit;
	public String newIP = "";
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
		edtCurrentIPNew = (EditText) findViewById(R.id.edtCurrentIPNew);
		//edtCurrentIPNew.setVisibility(View.GONE);
		serverSelectionSpinner = (Spinner) findViewById(R.id.serverSelection);
		GetSeverUrls geturls = new GetSeverUrls();
		geturls.execute();
		
		//serverSelectionSpinner.setVisibility(View.INVISIBLE);
		//edtNewIP = (EditText) findViewById(R.id.edtNewIP);

		btnCancel = (Button) findViewById(R.id.btnCancelIP);
		btnSubmit = (Button) findViewById(R.id.btnSubmitIP);

		currentIP = _data.getConstant(AppConstants.Constants.serverIP);
		
		if(Utils.isNothing(currentIP))
			currentIP = GreatBuyzApplication.getApplication().getServerIP();

		edtCurrentIP.setText(currentIP);
		edtCurrentIPNew.setText("");
		
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
				// newIP = edtCurrentIP.getText().toString();
				//String currentIPNew = edtCurrentIPNew.getText().toString();
//				if(edtCurrentIPNew.getText().toString().equals("")){
//					newIP = edtCurrentIP.getText().toString();
//				}
//				else
//				{
//					 newIP = edtCurrentIPNew.getText().toString();
//				}
				
				
				if (Utils.isNothing(newIP))
				{
					//edtNewIP.setError("Set an IP");
					Toast.makeText(IPChangeActivity.this, "Select Valid IP", Toast.LENGTH_LONG).show();

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
												//GreatBuyzApplication.getApplication().clearAllData();
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
		final ArrayAdapter<String> sponsorsAdapter = new ArrayAdapter<String>(IPChangeActivity.this,
				android.R.layout.simple_spinner_item, sponsorItems);
		sponsorsAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		if (serverSelectionSpinner != null) {
			
			IPChangeActivity.this.runOnUiThread(new Runnable() {
		        @Override
		        public void run() {
		        	serverSelectionSpinner.setAdapter(sponsorsAdapter);
		        }//public void run() {
		});
			
			serverSelectionSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			    @Override
			    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
			        // your code here
			    	edtCurrentIPNew.setText(serverNames.get(position).url);
			    }

			    @Override
			    public void onNothingSelected(AdapterView<?> parentView) {
			        // your code here
			    }

			});
			
			//System.out.println("currentIP  ***"+currentIP);
//			if(currentIP.equalsIgnoreCase("gbtim.turacomobile.com")){
//				serverSelectionSpinner.setSelection(1);
//			}else{
//				serverSelectionSpinner.setSelection(0);
//			}
			
		}
		
	}
	
	private class ServerDomain {
		
		private String name;
		private String url;
	}
	
	private class GetSeverUrls extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			
		//	if(serverNames != null){
//				ServerDomain domain = new ServerDomain();
//				domain.name = "GB_QA";
//				domain.url = "103.19.90.82:8080";
//				ServerDomain domain1 = new ServerDomain();
//				domain1.name = "GB_PROD";
//				domain1.url = "gbtim.turacomobile.com";
//				serverNames.add(domain);
//				serverNames.add(domain1);
				try
				{
					showOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG, null);
					GreatBuyzApplication.getServiceDelegate().getServerIps(new IOperationListener()
					{
						@Override
						public void onOperationCompleted(boolean p_OperationComplitionStatus, String p_MessageFromServer)
						{
							//{"serverConifgInformation":[{"serverName":"GB_QA","serverUrl":"103.19.90.82:8080"},{"serverName":"GB_PROD","serverUrl":"gbtim.turacomobile.com"},{"serverName":"GB_STAGE","serverUrl":"172.16.2.125"}]}
							try
							{
								serverNames  = new ArrayList<ServerDomain>();
								JSONObject objectIps = new JSONObject(p_MessageFromServer);
								JSONArray serverIps = objectIps.getJSONArray("serverConifgInformation");
								ServerDomain domainDefault = new ServerDomain();
								domainDefault.name = "Select Server";
								domainDefault.url = "";	
								serverNames.add(domainDefault);
								for(int i =0; i< serverIps.length();i++){
									ServerDomain domain = new ServerDomain();
									domain.name = serverIps.getJSONObject(i).getString("serverName");
									domain.url = serverIps.getJSONObject(i).getString("serverUrl");						
									serverNames.add(domain);
								}
								
								
								if(serverNames.size() >0){
									setSponsorsArrayAdaptor();
								}
							}
							catch (JSONException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
						//	populateKeywords();
						}
					});
				}
				catch (Exception e)
				{
					e.printStackTrace();
					removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					//populateKeywords();
				}
			//}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
		
			
			
			super.onPostExecute(result);
		}
		
		
		
	}
	

	
}
