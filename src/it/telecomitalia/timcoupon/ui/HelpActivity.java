package it.telecomitalia.timcoupon.ui;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import it.telecomitalia.timcoupon.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onmobile.hcoe.ui.dialog.GenericDialog;
import com.onmobile.hcoe.ui.dialog.LoadingDialog;
import com.onmobile.utils.AppConstants;
import com.onmobile.utils.Utils;

public class HelpActivity extends Activity
{
	private String title;
	private String url;
	private WebView webView;
	private TextView titleView;
	private ImageView menuView;
	private LinearLayout mBackNavigation;
	private String strNoApplicationFound;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_view);
		url = getIntent().getStringExtra(AppConstants.SharedPrefKeys.help);
		title = getIntent().getStringExtra(AppConstants.JSONKeys.NAME);

		this.strNoApplicationFound = Utils.getMessageString(AppConstants.Messages.NoApplicationFound, R.string.NoApplicationFound);

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
		myDealsShortcut.setVisibility(View.GONE);

		menuView = (ImageView) findViewById(R.id.imgMenu);
		menuView.setVisibility(View.GONE);

		titleView = (TextView) findViewById(R.id.txt_help_title);
		titleView.setText(title);
		titleView.setTypeface(GreatBuyzApplication.getApplication().getFont());

		PaymentWebViewClient paymentWebViewClient = new PaymentWebViewClient();

		webView = (WebView) findViewById(R.id.helpDetails);
		webView.setBackgroundColor(Color.WHITE);
		webView.clearCache(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.setWebViewClient(paymentWebViewClient);
		webView.addJavascriptInterface(new JavascriptHandler(), "native");

		// webView.loadDataWithBaseURL("file:///android_asset/", htmlStart +
		// text + htmlEnd, "text/html", "utf-8", null) ;

		WebSettings settings = webView.getSettings();
		settings.setDefaultTextEncodingName(AppConstants.ENCODING_UTF8);
		webView.loadUrl(url);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		// FlurryAgent.onPageView();
		GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit("Help");
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

	private class PaymentWebViewClient extends WebViewClient
	{
		@Override
		public void onPageFinished(WebView view, String url)
		{
			super.onPageFinished(view, url);
			webView.invalidate();
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			url = url.toLowerCase();
			if (!url.contains(AppConstants.URL_REDIRECTION_BROWSER_PARAM))
			{
				view.loadUrl(url);
				return true;
			}

			Uri uri = null;
			try
			{
				url = URLDecoder.decode(url, AppConstants.ENCODING_UTF8);
				uri = Uri.parse(url);

				Intent intent = new Intent(Intent.ACTION_VIEW, uri);

				startActivity(intent);
			}
			catch (ActivityNotFoundException e)
			{
				try
				{
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(uri, "application/download");

					startActivity(intent);
				}
				catch (Exception e1)
				{
					try
					{
						showLoadingDialog();
						openFile(url);
						removeLoadingDialog();
					}
					catch (Exception r)
					{
						removeLoadingDialog();
						showMessageDialog(strNoApplicationFound);
					}
				}
			}
			catch (Exception c)
			{
				removeDialog(AppConstants.DialogConstants.LOADING_DIALOG);
				showMessageDialog(strNoApplicationFound);
			}
			return true;
		}

		public void showLoadingDialog()
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						showDialog(AppConstants.DialogConstants.LOADING_DIALOG);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
		}

		public void removeLoadingDialog()
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
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
			});
		}

		public void showMessageDialog(final String message)
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
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
			});
		}

		private void launchFile(final String fileName)
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						String cachePath = Utils.getCacheDirectory();
						File cacheDir = new File(cachePath);

						if (!cacheDir.exists())
						{
							Bundle b = new Bundle();
							b.putString(AppConstants.JSONKeys.MESSAGE, strNoApplicationFound);
							showDialog(AppConstants.DialogConstants.MESSAGE_DIALOG, b);
							return;
						}

						final String extension = fileName.substring(fileName.lastIndexOf('.') + 1);

						final String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

						File newFile = new File(cacheDir.getAbsoluteFile(), fileName);
						Intent myIntent = new Intent(Intent.ACTION_VIEW);
						myIntent.setDataAndType(Uri.fromFile(newFile), mimeType);
						myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						Intent i = Intent.createChooser(myIntent, null);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

						startActivity(i);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
		}

		private void CopyStream(InputStream is, OutputStream os)
		{
			final int buffer_size = 1024;
			try
			{
				byte[] bytes = new byte[buffer_size];
				for (;;)
				{
					int count = is.read(bytes, 0, buffer_size);
					if (count == -1)
						break;
					os.write(bytes, 0, count);
				}
			}
			catch (Exception ex)
			{
			}
		}

		private void openFile(final String url) throws URISyntaxException, MalformedURLException, IOException
		{
			final String extension = url.substring(url.lastIndexOf('.') + 1);

			String cachePath = Utils.getCacheDirectory();
			File cacheDir = new File(cachePath);

			String fileName = "aFile." + extension;
			File f = new File(cacheDir, fileName);

			if (f.exists())
				f.delete();

			InputStream is = new URL(url).openStream();
			OutputStream os = new FileOutputStream(f);
			CopyStream(is, os);
			os.close();

			launchFile(fileName);
		}
	}
}
