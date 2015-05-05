package com.acl.paychamp.android;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.acl.paychamp.android.Paychamp.DialogListener;
import com.acl.paychamp.util.Receipt;

public class CCBDialog extends Dialog {

	static final int FB_BLUE = 0xFF6D84B4;
	static final float[] DIMENSIONS_DIFF_LANDSCAPE = { 20, 60 };
	static final float[] DIMENSIONS_DIFF_PORTRAIT = { 40, 60 };
	static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT);
	static final int MARGIN = 0;
	static final int PADDING = 2;
	static final String LOGO = "logo.png";
	private String mUrl;
	private DialogListener mListener;
	private ProgressDialog mSpinner;
	private ImageView mCrossImage;
	private WebView webView;
	private FrameLayout mContent;
	private Paychamp paychamp;

	public CCBDialog(Context context, String url, DialogListener listener) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		mUrl = url;
		mListener = listener;
	}

	@Override
	public void dismiss() {
		super.dismiss();
		if (mSpinner != null && mSpinner.isShowing()) {
			mSpinner.dismiss();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSpinner = new ProgressDialog(getContext());
		mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mSpinner.setMessage("Loading...");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		mContent = new FrameLayout(getContext());
		createCrossImage();
		int crossWidth = mCrossImage.getDrawable().getIntrinsicWidth();
		setUpWebView(crossWidth / 2);
		mContent.addView(mCrossImage, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		addContentView(mContent, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}

	private void createCrossImage() {
		mCrossImage = new ImageView(getContext());
		mCrossImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onCancel();
				CCBDialog.this.dismiss();
			}
		});
		Drawable crossDrawable = getContext().getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel);
		mCrossImage.setImageDrawable(crossDrawable);
		mCrossImage.setVisibility(View.INVISIBLE);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mListener.onCancel();
		CCBDialog.this.dismiss();
	}
	
//	 @Override  
//	    public boolean onKeyDown(int keyCode, KeyEvent event)  
//	  {  
//	         //replaces the default 'Back' button action  
//	         if(keyCode==KeyEvent.KEYCODE_BACK)  
//	         {  
//	        	 mListener.onCancel();
//					CCBDialog.this.dismiss();
//
//	         }  
//	         return true;  
//	   } 

	private void setUpWebView(int margin) {
		LinearLayout webViewContainer = new LinearLayout(getContext());
		webView = new WebView(getContext());
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.setWebViewClient(new CCBDialog.PayChampWebViewClient());
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
				new AlertDialog.Builder(getContext()).setTitle("Attention").setMessage(message).setPositiveButton(android.R.string.ok,
						new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								result.confirm();
							}
						}).setNegativeButton(android.R.string.cancel, new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						result.cancel();
					}
				}).setCancelable(true).create().show();
				return true;
			}
		});
		webView.loadUrl(mUrl);
		webView.setLayoutParams(FILL);
		webView.setVisibility(View.VISIBLE);
		webViewContainer.setPadding(margin, margin, margin, margin);
		webViewContainer.addView(webView);
		mContent.addView(webViewContainer);
	}

	public class PayChampWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url != null && url.startsWith("http://redirect")) {
				Bundle values = Util.parseUrl(url);
				String param = values.getString("resparam");
				//String responseCode = getDecryptData(param);

				JSONObject data = DecryptData.getDecryptedJson(param);

				Receipt receipt = new Receipt();
				receipt.setAccountId(Integer.parseInt(DecryptData.getTransactionValues(data, "account_id")));
				receipt.setTransactionId(DecryptData.getTransactionValues(data, "trxid"));
				receipt.setStatus(Integer.parseInt(DecryptData.getTransactionValues(data, "msg")));
				receipt.setItemDescription(DecryptData.getTransactionValues(data, "itemdesc"));
				receipt.setTransactionRef(DecryptData.getTransactionValues(data, "tranref"));
				receipt.setPrice(Double.parseDouble(DecryptData.getTransactionValues(data, "price")));
				receipt.setCurrency(DecryptData.getTransactionValues(data, "currency"));
				receipt.setContentType(Integer.parseInt(DecryptData.getTransactionValues(data, "contenttype")));
				receipt.setOperator(DecryptData.getTransactionValues(data, "operator"));
				receipt.setTime(DecryptData.getTransactionValues(data, "time"));
				paychamp.notifyAuthDialog(receipt);

				CCBDialog.this.dismiss();
				return true;
			} else if (url != null && url.startsWith("mailto:")) {
				String mail = url.replaceFirst("mailto:", "");
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("message/rfc822");
				intent.putExtra(Intent.EXTRA_EMAIL, mail);

				getContext().startActivity(Intent.createChooser(intent, "Send Email"));
				return true;
			}
			return false;
		}

		@SuppressLint("NewApi")
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}

		@Override
		public void onLoadResource(WebView view, String url) {
		}

		private String getDecryptData(String input) {
			String msg = DecryptData.getDecryptedText(input);
			return msg;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mListener.onError(new DialogError(description, errorCode, failingUrl));
			CCBDialog.this.dismiss();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			mSpinner.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mSpinner.dismiss();
			mContent.setBackgroundColor(Color.TRANSPARENT);
			webView.setVisibility(View.VISIBLE);
			mCrossImage.setVisibility(View.VISIBLE);
		}
	}

	public void setPaychamp(Paychamp paychamp) {
		this.paychamp = paychamp;
	}

}