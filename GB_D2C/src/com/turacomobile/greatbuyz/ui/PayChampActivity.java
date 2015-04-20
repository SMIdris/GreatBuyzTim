package com.turacomobile.greatbuyz.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.acl.paychamp.android.Paychamp;
import com.acl.paychamp.util.DataValueException;
import com.turacomobile.greatbuyz.R;
import com.turacomobile.greatbuyz.service.AuthDialogListener;

public class PayChampActivity extends Activity {

	private String billingDomain = "bill.paychamp.com";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.Paychamp_activity_main);
	}



	public void buy(View view) {
		AuthDialogListener auth = new AuthDialogListener(getApplicationContext());

//		EditText mContentType = (EditText) findViewById(R.id.contentType);
//		EditText mPrice = (EditText) findViewById(R.id.price);
//		EditText mCountry = (EditText) findViewById(R.id.country);
//		EditText mAccountId = (EditText) findViewById(R.id.accountId);
//		EditText mPassKey = (EditText) findViewById(R.id.passKey);
//		EditText mProductName = (EditText) findViewById(R.id.productName);
//		EditText mImageUri = (EditText) findViewById(R.id.imageUri);
		
		String mContentType = "8";
		String mPrice = "1";
		String mCountry = "IN";
		String mAccountId = "19204455";
		String mPassKey = "1234";
		String mProductName = "speed-car.gif";
		String mImageUri = "";

		String transRef = "ABCDPC_" + new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(new Date());
		try {
			new Paychamp(mAccountId, mPassKey, this, billingDomain).requestPayment(auth,
					mProductName, Double.parseDouble(mPrice), mCountry,
					Integer.parseInt(mContentType), transRef, URLEncoder.encode(mImageUri,
							"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			Toast.makeText(getApplicationContext(), "Certain fields are not formatted correctly ! " + e.getMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (DataValueException e) {
			Toast.makeText(getApplicationContext(), e.getField() + " -- " + e.getErrorMessage(), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
}
