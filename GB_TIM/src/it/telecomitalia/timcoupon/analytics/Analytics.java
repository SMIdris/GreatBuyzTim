package it.telecomitalia.timcoupon.analytics;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import it.telecomitalia.timcoupon.filetransfer.BFileTransferFactory;
import it.telecomitalia.timcoupon.filetransfer.IFileTransferMethod;
import it.telecomitalia.timcoupon.filetransfer.BFileTransferFactory.TFileTransferMethods;

import java.io.File;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;

import com.flurry.android.FlurryAgent;
import com.onmobile.utils.AppConstants;

public class Analytics
{
	public static enum TAnalyticsMethods
	{
		EAnalyticsMethodFlurry, EAnalyticsMethodVA
	}

	private TAnalyticsMethods method = TAnalyticsMethods.EAnalyticsMethodFlurry;
	private VASAnalyticsAgent VAAgent = null;

	private Timer analyticsTimer;
	private TimerTask timerTask;
	private int logFileUploadFrequencyInMinutes;
	private long logFileUploadMinSizeLimitInKB;
	public final String BASE_URL = GreatBuyzApplication.getApplication().getBaseURL();

	public Analytics(TAnalyticsMethods method, String mdn, String imsi, String imei, int logFileUploadFrequencyInMinutes,
			long logFileUploadApproxSizeLimitInKB)
	{
		this.method = method;
		if (method == TAnalyticsMethods.EAnalyticsMethodVA)
		{
			VAAgent = new VASAnalyticsAgent(mdn, imsi, imei);
			this.logFileUploadFrequencyInMinutes = logFileUploadFrequencyInMinutes;

			final int delay = logFileUploadFrequencyInMinutes * 60000;
			if (timerTask != null)
			{
				timerTask.cancel();
				timerTask = null;
			}
			if (analyticsTimer != null)
			{
				analyticsTimer.cancel();
				analyticsTimer = null;
			}
			analyticsTimer = new Timer();
			timerTask = new TimerTask()
			{
				@Override
				public void run()
				{
					long currentFileSizeInKB = VAAgent.getCurrentLogFileSizeInKB();
					if (currentFileSizeInKB > Analytics.this.logFileUploadMinSizeLimitInKB)
					{
						long newFileCreationTime = VAAgent.rotateLogFile();
						uploadLogFiles(newFileCreationTime);
					}
				}
			};
			analyticsTimer.scheduleAtFixedRate(timerTask, delay, delay);
		}
	}

	public void uploadLogFiles(long newFileCreationTime)
	{
		String cacheDirPath = GreatBuyzApplication.getApplication().getFilesDir().getAbsolutePath() + "/"
				+ AppConstants.CACHE_DIRECTORY_ANALYTICS;

		File cacheDir = new File(cacheDirPath);
		if (!cacheDir.exists())
			cacheDir.mkdir();

		String[] files = cacheDir.list();

		IFileTransferMethod method = BFileTransferFactory.createFileTransferMethod(BASE_URL + AppConstants.URIParts.ANALYTICS_APP_LOG,
				TFileTransferMethods.EMethodHTTP);

		// TODO : testing
		// IFileTransferMethod method =
		// BFileTransferFactory.createFileTransferMethod(AppConstants.URIParts.ANALYTICS_APP_LOG,
		// TFileTransferMethods.EMethodHTTP);
		// END

		for (int i = 0; i < files.length; i++)
		{
			String fileName = files[i];
			long fileTime = 0;

			try
			{
				fileTime = Long.parseLong(fileName);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
				continue;
			}

			if (fileTime == newFileCreationTime)
				continue;

			method.uploadFile(BASE_URL + AppConstants.URIParts.ANALYTICS_APP_LOG, AppConstants.EMPTY_STRING,
					AppConstants.EMPTY_STRING, 80, cacheDirPath, fileName, AppConstants.EMPTY_STRING, AppConstants.EMPTY_STRING);

			// TODO : testing
			// method.uploadFile(AppConstants.URIParts.ANALYTICS_APP_LOG,
			// AppConstants.EMPTY_STRING,
			// AppConstants.EMPTY_STRING, 80, cacheDirPath, fileName,
			// AppConstants.EMPTY_STRING, AppConstants.EMPTY_STRING);
			// END
		}
	}

	public void setMDN(String mdn)
	{
		if (VAAgent != null)
			VAAgent.setMDN(mdn);
	}

	public void setLogRotationFrequency(int frequency)
	{
		this.logFileUploadFrequencyInMinutes = frequency;

		if (timerTask != null)
		{
			timerTask.cancel();
			timerTask = null;
		}
		if (analyticsTimer != null)
		{
			analyticsTimer.cancel();
			analyticsTimer = null;
		}
		final int delay = logFileUploadFrequencyInMinutes * 60000;
		analyticsTimer = new Timer();
		timerTask = new TimerTask()
		{
			@Override
			public void run()
			{
				long currentFileSizeInKB = VAAgent.getCurrentLogFileSizeInKB();
				if (currentFileSizeInKB > Analytics.this.logFileUploadMinSizeLimitInKB)
				{
					long newFileCreationTime = VAAgent.rotateLogFile();
					uploadLogFiles(newFileCreationTime);
				}
			}
		};
		analyticsTimer.scheduleAtFixedRate(timerTask, delay, delay);
	}

	public void onStartSession(Context context, String apiKey)
	{
		try
		{
			switch (method)
			{
				case EAnalyticsMethodFlurry:
					{
						FlurryAgent.onStartSession(context, apiKey);
					}
					break;
				case EAnalyticsMethodVA:
					{
						VAAgent.onStartSession();
					}
					break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void onEndSession(Context context)
	{
		try
		{
			switch (method)
			{
				case EAnalyticsMethodFlurry:
					{
						FlurryAgent.onEndSession(context);
					}
					break;
				case EAnalyticsMethodVA:
					{
						VAAgent.onEndSession();
						if (analyticsTimer != null)
							analyticsTimer.cancel();
						if (timerTask != null)
							timerTask.cancel();
						uploadLogFiles(System.currentTimeMillis());
					}
					break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void onPageVisit(String pageName)
	{
		try
		{
			switch (method)
			{
				case EAnalyticsMethodFlurry:
					{
						FlurryAgent.onPageView();
					}
					break;
				case EAnalyticsMethodVA:
					{
						VAAgent.onPageVisit(pageName);
					}
					break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void logEvent(String eventName, Map<String, String> eventParameters)
	{
		try
		{
			switch (method)
			{
				case EAnalyticsMethodFlurry:
					{
						FlurryAgent.logEvent(eventName, eventParameters);
					}
					break;
				case EAnalyticsMethodVA:
					{
						VAAgent.logEvent(eventName, eventParameters);
					}
					break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public void logChangeSettings(String eventName, String params)
	{
		try
		{
			switch (method)
			{
				case EAnalyticsMethodFlurry:
					{
						//FlurryAgent.logEvent(eventName, params);
					}
					break;
				case EAnalyticsMethodVA:
					{
						VAAgent.logChangeSettings(eventName, params);
					}
					break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}