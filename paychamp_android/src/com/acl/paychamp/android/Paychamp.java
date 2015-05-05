package com.acl.paychamp.android;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.KeyEvent;
import android.widget.Toast;

import com.acl.paychamp.util.DataValueException;
import com.acl.paychamp.util.PaychampConstants;
import com.acl.paychamp.util.Receipt;

public class Paychamp extends Activity {
	public static final String TOKEN = "signed_request";
	private String mAccessToken = null;
	private DialogListener mAuthDialogListener;
	private Activity activity;
	private StringBuilder billUrl;
	private String redirectUri;
	private Receipt receipt;
	private String billingDomain;
	private String accountId;
	private String passKey;
	private CCBDialog dialog;

	public Paychamp(String accountId, String passKey, Activity activity, String billingDomain) throws DataValueException {
		if (accountId == null || accountId.trim().equals("")) {
			throw new DataValueException("Account ID", PaychampConstants.FIELD_EMPTY);
		} else {
			this.accountId = accountId;
		}

		if (passKey == null || passKey.trim().equals("")) {
			throw new DataValueException("Passkey", PaychampConstants.FIELD_EMPTY);
		} else {
			this.passKey = passKey;
		}

		if (billingDomain == null || billingDomain.trim().equals("")) {
			throw new DataValueException("Billing Domain", PaychampConstants.FIELD_EMPTY);
		} else {
			this.billingDomain = billingDomain;
		}

		this.activity = activity;
	}

	public void hide() {
		if (dialog != null) {
			dialog.dismiss();
		} else
			Util.logd("Paychamp", "No dialog found to show");
	}

	public void show() {
		if (dialog != null) {
			dialog.show();
		} else
			Util.logd("Paychamp", "No dialog found to show");
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public void requestPayment(final DialogListener listener, String itemRef, Double price, String countryCode, Integer contentType,
			String transRef, String imageUri) throws DataValueException {
		if (itemRef == null || itemRef.trim().equals("")) {
			throw new DataValueException("Item Description", PaychampConstants.FIELD_EMPTY);
		} else if (price == null) {
			throw new DataValueException("Price", PaychampConstants.FIELD_EMPTY);
		} else if (countryCode == null || countryCode.trim().equals("")) {
			throw new DataValueException("Country Code", PaychampConstants.FIELD_EMPTY);
		} else if (contentType == null) {
			throw new DataValueException("Country Type", PaychampConstants.FIELD_EMPTY);
		}

		String redirectUri = "http://redirect/" + accountId + "?resparam=";
		setRedirectUri(redirectUri);

		try {
			requestPayment(activity, listener, itemRef, price, countryCode, contentType, transRef, URLEncoder.encode(redirectUri, "UTF-8"),
					imageUri);
		} catch (UnsupportedEncodingException e) {
		}
	}
	

	private void requestPayment(final Activity activity, final DialogListener listener, String itemRef, Double price, String countryCode,
			Integer contentType, String transRef, String redirectUri, String imageUri) {
		mAuthDialogListener = listener;

		billUrl =
				new StringBuilder("http://").append(billingDomain).append("/webservice/billme/initTrx/").append(accountId).append("/").append(
						new EncryptedText().getEncryptedJson(itemRef, contentType, price, 0.0, passKey));
		billUrl.append("?transaction_ref=").append(transRef);

		if (redirectUri != null) {
			billUrl.append("&redirect_uri=").append(redirectUri);
		}

		if (imageUri != null) {
			billUrl.append("&image_uri=").append(imageUri);
		}

		if (isSessionValid()) {
			mAuthDialogListener.onComplete(receipt);
		} else {
			startDialogAuth(activity);
		}
	}

	public void notifyAuthDialog(Receipt receipt) {
		mAuthDialogListener.onComplete(receipt);
	}

	private void startDialogAuth(Context activity) {
		dialog(activity, new DialogListener() {
			public void onComplete(Receipt receipt) {
				setAccessToken(TOKEN);
				setReceipt(receipt);
				if (isSessionValid()) {
					mAuthDialogListener.onComplete(receipt);
					dialog = null;
				} else {
					mAuthDialogListener.onCCBError(new CCBError("Failed to receive access token."));
				}
			}

			public void onError(DialogError error) {
				mAuthDialogListener.onError(error);
			}

			public void onCCBError(CCBError error) {
				mAuthDialogListener.onCCBError(error);
			}

			public void onCancel() {
				mAuthDialogListener.onCancel();
			}
		});
	}
	



	public void dialog(Context context, final DialogListener listener) {
		String url = getUrl();
		if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
			Util.showAlert(context, "Error", "Application requires permission to access the Internet");
		} else {
			dialog = new CCBDialog(context, url, listener);
			dialog.setPaychamp(this);
			dialog.show();
		}
	}

	private String getUrl() {
		return billUrl.toString();
	}

	public boolean isSessionValid() {
		return (getAccessToken() != null);
	}

	public String getAccessToken() {
		return mAccessToken;
	}

	public void setAccessToken(String token) {
		mAccessToken = token;
	}

	public static interface DialogListener {
		public void onComplete(Receipt receipt);

		public void onCCBError(CCBError e);

		public void onError(DialogError e);

		public void onCancel();
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getBillingDomain() {
		return billingDomain;
	}

	public void setBillingDomain(String billingDomain) {
		this.billingDomain = billingDomain;
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

}
