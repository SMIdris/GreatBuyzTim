package com.onmobile.utils;

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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GBWebViewClient extends WebViewClient
{
	public Activity act;
	String strNoApplicationFound;

	public GBWebViewClient(Activity activity)
	{
		super();
		this.act = activity;
		this.strNoApplicationFound = Utils.getMessageString(AppConstants.Messages.NoApplicationFound, R.string.NoApplicationFound);
	}

	@Override
	public void onPageFinished(WebView view, String url)
	{
		super.onPageFinished(view, url);
		view.invalidate();
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

			act.startActivity(intent);
		}
		catch (ActivityNotFoundException e)
		{
			try
			{
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(uri, "application/download");

				act.startActivity(intent);
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
			act.removeDialog(AppConstants.DialogConstants.LOADING_DIALOG);
			showMessageDialog(strNoApplicationFound);
		}
		return true;
	}

	public void showLoadingDialog()
	{
		act.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					act.showDialog(AppConstants.DialogConstants.LOADING_DIALOG);
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
		act.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					act.removeDialog(AppConstants.DialogConstants.LOADING_DIALOG);
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
		act.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Bundle b = new Bundle();
					b.putString(AppConstants.JSONKeys.MESSAGE, message);
					act.showDialog(AppConstants.DialogConstants.MESSAGE_DIALOG, b);
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
		act.runOnUiThread(new Runnable()
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
						act.showDialog(AppConstants.DialogConstants.MESSAGE_DIALOG, b);
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

					act.startActivity(i);
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