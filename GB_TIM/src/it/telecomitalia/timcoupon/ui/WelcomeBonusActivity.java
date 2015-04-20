package it.telecomitalia.timcoupon.ui;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import it.telecomitalia.timcoupon.R;
import it.telecomitalia.timcoupon.data.Deal;
import it.telecomitalia.timcoupon.service.DataController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onmobile.hcoe.ui.dialog.GenericDialog;
import com.onmobile.hcoe.ui.dialog.LoadingDialog;
import com.onmobile.utils.AppConstants;
import com.onmobile.utils.Utils;

@SuppressLint("NewApi")
public class WelcomeBonusActivity extends Activity implements OnClickListener
{
	Deal dealDTO = null;
	// ImageLoader imageLoader = null;

	public static final DataController _data = GreatBuyzApplication.getDataController();

	static Activity activity;
	Button btnVisitDeal;

	LinearLayout backNav;

	private String strNoApplicationFound;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("GreatBuyz: function IN WelcomeBonusActivity > onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		loadNewIntent(getIntent());
		// ////System.out.println("GreatBuyz: function OUT DetailScreen > onCreate");
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getKeyCode() == KeyEvent.KEYCODE_MENU)
			return true;
		return super.dispatchKeyEvent(event);
	}

	@Override
	protected void onStart()
	{
		System.out.println("GreatBuyz: function IN WelcomeBonusActivity > onStart");
		super.onStart();
		// FlurryAgent.onPageView();
		GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit("WelcomeBonus");

		activity = this;
		// ////System.out.println("GreatBuyz: function OUT DetailScreen > onStart");
	}

	@Override
	protected void onStop()
	{
		System.out.println("GreatBuyz: function IN WelcomeBonusActivity > onStop");
		super.onStop();
		activity = null;
		// ////System.out.println("GreatBuyz: function OUT DetailScreen > onStop");
	}

	@Override
	public void onBackPressed()
	{
		// ////System.out.println("GreatBuyz: function IN WelcomeBonusActivity > onBackPressed");
		finish();

		Intent mainIntent = new Intent(this, SampleTabsStyled.class);
		mainIntent.addCategory("com.jakewharton.android.viewpagerindicator.sample.SAMPLE");
		mainIntent.putExtra("com.jakewharton.android.viewpagerindicator.sample.Path", "Tabs");
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mainIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(mainIntent);
		// ////System.out.println("GreatBuyz: function OUT WelcomeBonusActivity > onBackPressed");
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void loadNewIntent(Intent intent)
	{
		System.out.println("GreatBuyz: function IN WelcomeBonusActivity > loadNewIntent start");
		//_data = GreatBuyzApplication.getDataController();

		this.strNoApplicationFound = Utils.getMessageString(AppConstants.Messages.NoApplicationFound, R.string.NoApplicationFound);

		setContentView(R.layout.welcome_bonus);
		backNav = (LinearLayout) findViewById(R.id.back_navigation);
		backNav.setOnClickListener(this);
		RelativeLayout myDealsShortcut = (RelativeLayout) findViewById(R.id.imgMyDeals);
		myDealsShortcut.setVisibility(View.GONE);

		findViewById(R.id.imgMenu).setVisibility(View.INVISIBLE);
		System.out.println("_data object loadNewIntent"+_data);
		dealDTO = _data.getFreeDeal();
		loadView();
		System.out.println("GreatBuyz: function IN WelcomeBonusActivity > loadNewIntent End");
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void loadView()
	{
		System.out.println("GreatBuyz: function IN WelcomeBonusActivity > loadView Start"+dealDTO);
		
		
		if (dealDTO == null)
			return;

		// imageLoader = new ImageLoader(this, null, false);
		// String url = dealDTO.getImage();
		// ImageView img;
		// img = (ImageView) findViewById(R.id.img_bonus);
		// img.setTag(url);

		/*
		 * if (!Utils.isNothing(url)) imageLoader.DisplayImage(url, this, img);
		 */

		btnVisitDeal = (Button) findViewById(R.id.btnVisit);
		// btnVisitDeal.setVisibility(View.VISIBLE);
		btnVisitDeal.setTypeface(GreatBuyzApplication.getApplication().getFont());
		Utils.setMessageToButton(btnVisitDeal, AppConstants.Messages.welcomeDealContinueButtonText);
		visitDeal();

		PaymentWebViewClient paymentWebViewClient = new PaymentWebViewClient();

		WebView webView;
		webView = (WebView) findViewById(R.id.webDealDetails);
		webView.setBackgroundColor(0x00000000);
		webView.clearCache(true);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(paymentWebViewClient);
		//System.out.println("dealDTO.getLongDescription() ***"+dealDTO.getLongDescription());
		webView.loadDataWithBaseURL(null, dealDTO.getLongDescription(), "text/html", "UTF-8", null);
		/*
		 * "<html><body background=\"#00000000\" link=\"white\"><font color=\"white\">"
		 * +
		 * "<body><span class='textBold'>Complimenti!</span><br><br>La registrazione � stata completata.<br>Questo � il tuo codice: <span class='textYellow'>2106013</span>.<br><br> Inseriscilo sul sito di Tempo di Sconti (<a href='http://www.tempodisconti.it' class='textYellow'>www.tempodisconti.it</a>) prima di concludere un acquisto ed otterrai un ulteriore 7% di sconto e le spese Gratis! Ricorda che scade il 01 Gennaio 2014.<br>Inoltre, la prima settimana di TIM Coupon, per te sara' Gratis!'</body>"
		 * + "</font></body</html>"
		 */
		webView.setBackgroundColor(0x00000000);
		if (Build.VERSION.SDK_INT >= 11) // Android v3.0+
		{
			try
			{
				Method method = View.class.getMethod("setLayerType", int.class, Paint.class);
				method.invoke(webView, 1, new Paint()); // 1=LAYER_TYPE_SOFTWARE(API11)
			}
			catch (Exception e)
			{
			}
		}
		
		/*try {
	        Class clsWebSettingsClassic = 
	            getClassLoader().loadClass("android.webkit.WebSettingsClassic");
	        @SuppressWarnings("unchecked")
			Method md = clsWebSettingsClassic.getMethod(
	                "setProperty", String.class, String.class);
	        md.invoke(webView.getSettings(), "inverted", "true");
	        md.invoke(webView.getSettings(), "inverted_contrast", "1");
	    } catch (Exception e) {}*/
	}

	private void visitDeal()
	{
		btnVisitDeal.setTypeface(GreatBuyzApplication.getApplication().getFont());
		btnVisitDeal.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				System.out.println(" GreatBuyz: function IN WelcomeBonusActivvisitDeal  Button clicked ");
				/*
				 * HashMap<String, String> m = new HashMap<String, String>();
				 * m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.VISIT);
				 * m.put(AppConstants.Flurry.DEALID, dealDTO.getId()); //
				 * FlurryAgent.logEvent(AppConstants.Flurry.WelcomeDeal, m);
				 * GreatBuyzApplication
				 * .getApplication().getAnalyticsAgent().logEvent
				 * (AppConstants.Flurry.WelcomeDeal, m);
				 * Utils.launchUri(WelcomeBonusActivity.this,
				 * dealDTO.getDealVisitUrl());
				 */
				onBackPressed();
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
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			System.out.println("url ***"+url);
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
				removeOtherDialog(AppConstants.DialogConstants.LOADING_DIALOG);
				showMessageDialog(strNoApplicationFound);
			}
			return true;
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
							showOtherDialog(AppConstants.DialogConstants.MESSAGE_DIALOG, b);
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
					catch (NotFoundException e)
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

	private void showOtherDialog(final int which, final Bundle b)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				showDialog(which, b);
			}
		});
	}

	public void removeOtherDialog(final int which)
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

	@Override
	public void onClick(View v)
	{
		if (v == backNav)
		{
			onBackPressed();
		}

	}
}