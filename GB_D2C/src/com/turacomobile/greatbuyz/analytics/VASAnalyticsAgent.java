package com.turacomobile.greatbuyz.analytics;


import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.turacomobile.greatbuyz.GreatBuyzApplication;
import com.turacomobile.greatbuyz.utils.AppConstants;
import com.turacomobile.greatbuyz.utils.Utils;

public class VASAnalyticsAgent
{
	public VASAnalyticsAgent(String mdn, String imsi, String imei)
	{
		this.mdn = mdn;
		this.imsi = imsi;
		this.imei = imei;
	}

	private String TAG = "TIMVASAnalyticsAgent: ";
	private String cacheDirPath = GreatBuyzApplication.getApplication().getFilesDir().getAbsolutePath() + "/" + AppConstants.CACHE_DIRECTORY_ANALYTICS;
	private String mdn = AppConstants.EMPTY_STRING;
	private String imsi = AppConstants.EMPTY_STRING;
	private String imei = AppConstants.EMPTY_STRING;
	private String currentLogFile = AppConstants.EMPTY_STRING;
	private String lineSeparator = "\n"; //AppConstants.EMPTY_STRING; //System.getProperty("line.separator");

	public void setMDN(String mdn)
	{
		this.mdn = mdn;
	}

	public long rotateLogFile()
	{
		// Log.d(TAG, "rotateLogFile");
		synchronized (this)
		{
			currentLogFile = AppConstants.EMPTY_STRING;
			return System.currentTimeMillis();
		}
	}
	
	/*
	public long rotateLogFile()
	{
		// Log.d(TAG, "rotateLogFile");
		long newFileCreationTime = 0;

		String fileName = getCurrentFileName();
		boolean success = createLogFileIfNotExists(fileName);

		if (!success)
		{
			// Log.d(TAG, "rotateLogFile : rotation failed");
			return newFileCreationTime;
		}

		// Log.d(TAG, "rotateLogFile : new file created : " +
		// newFileCreationTime);

		currentLogFile = fileName;

		try
		{
			newFileCreationTime = Long.parseLong(fileName);
		}
		catch (Exception e)
		{
		}

		return newFileCreationTime;
	}
	*/

	public void onStartSession()
	{
		// ts,mdn,imsi,deviceId,event,parameters,extraData\n
		try
		{
			String sessinId = generateSessionId();
			GreatBuyzApplication.getApplication().saveStringPreference(AppConstants.SharedPrefKeys.SessionId, sessinId);
			/*String logLine = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"StartSession\",\"\",\"\"" + lineSeparator, getCurrentTimeStamp(),
					mdn, imsi, imei);*/
			String logLine = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"StartSession\",\"%s\",\"%s\"" + lineSeparator, Utils.getTimeWithRespectToTimeZone(),
					mdn, imsi, imei,System.currentTimeMillis(),sessinId);
			appendToLogFile(logLine);
			// Log.d(TAG, "startSession : " + logLine);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void onEndSession()
	{
		// ts,mdn,imsi,deviceId,event,parameters,extraData\n
		try
		{
			String logLine = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"EndSession\",\"%s\",\"%s\"" + lineSeparator, Utils.getTimeWithRespectToTimeZone(),
					mdn, imsi, imei,System.currentTimeMillis(), GreatBuyzApplication.getApplication().getStringPreference(AppConstants.SharedPrefKeys.SessionId));
			appendToLogFile(logLine);
			// Log.d(TAG, "endSession : " + logLine);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void onPageVisit(String pageName)
	{
		// ts,mdn,imsi,deviceId,event,parameters,extraData\n
		try
		{
			String logLine = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"PageVisit\",\"Page=%s\",\"%s\"" + lineSeparator,
					Utils.getTimeWithRespectToTimeZone(), mdn, imsi, imei, pageName, GreatBuyzApplication.getApplication().getStringPreference(AppConstants.SharedPrefKeys.SessionId) );
			appendToLogFile(logLine);
			// Log.d(TAG, "pageVisit : " + logLine);
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
			StringBuilder parameterString = new StringBuilder(AppConstants.EMPTY_STRING);
			Iterator<String> keys = eventParameters.keySet().iterator();
			while (keys.hasNext())
			{
				String key = keys.next();
				parameterString.append(key);
				parameterString.append("=");
				parameterString.append(eventParameters.get(key));
				if (keys.hasNext())
					parameterString.append(",");
			}
			System.out.println("parameterString"+parameterString);
			String logLine = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"" + lineSeparator, Utils.getTimeWithRespectToTimeZone(), mdn, imsi,
					imei, eventName, parameterString.toString(), GreatBuyzApplication.getApplication().getStringPreference(AppConstants.SharedPrefKeys.SessionId));
			appendToLogFile(logLine);
			// Log.d(TAG, "event : " + logLine);
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
			//System.out.println("parameterString"+params);
			String logLine = String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"" + lineSeparator, Utils.getTimeWithRespectToTimeZone(), mdn, imsi,
					imei, eventName, params, GreatBuyzApplication.getApplication().getStringPreference(AppConstants.SharedPrefKeys.SessionId));
			appendToLogFile(logLine);
			// Log.d(TAG, "event : " + logLine);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private String getCurrentTimeStamp()
 {
		 /* date format changed to 24 hours
		  @author kiranr
		 */
		long currentStamp = System.currentTimeMillis();
		Date currentDate = new Date(currentStamp);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeStamp = dateFormat.format(currentDate.getTime());
		return timeStamp;
	}

	private String getCurrentFileName()
	{
		File cacheDir = new File(cacheDirPath);
		if(!cacheDir.exists())
			cacheDir.mkdir();

		StringBuilder sb = new StringBuilder(cacheDirPath);
		sb.append("/");
		long currentStamp = System.currentTimeMillis();
		sb.append(currentStamp);
		return sb.toString();
	}
	
	public long getCurrentLogFileSizeInKB()
	{
		File currentFile = new File(currentLogFile);
		long bytes = currentFile.length();
		long kilobytes = bytes / 1024;
		return kilobytes;
	}

	private boolean createLogFileIfNotExists(String fileName)
	{
		File logFile = new File(fileName);
		if (logFile.exists())
			return true;
		else
		{
			try
			{
				logFile.createNewFile();

				/*
				FileWriter writter = new FileWriter(logFile);
				writter.append("ts,mdn,imsi,deviceId,event,parameters,extraData" + lineSeparator);
				writter.flush();
				writter.close();
				*/
				// Log.d(TAG, "new log file created : " + fileName);
				return true;
			}
			catch (Exception e)
			{
				// Log.d(TAG, "Failed to create log file");
				e.printStackTrace();
			}
		}
		return false;
	}

	private void appendToLogFile(String logLine)
	{
		synchronized (this)
		{
			String fileName = currentLogFile;
			if (Utils.isNothing(currentLogFile))
			{
				fileName = getCurrentFileName();
				boolean success = createLogFileIfNotExists(fileName);
				if (success)
					currentLogFile = fileName;
			}
			try
			{
				FileWriter writter = new FileWriter(fileName, true);
				writter.write(logLine);
				writter.flush();
				writter.close();
			}
			catch (Exception e)
			{
				// Log.d(TAG, "Failed to write to log file : " + "log : " + logLine
				// + " exception : " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	private String generateSessionId(){
		String timeStamp = getCurrentTimeStamp();
		timeStamp = timeStamp.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
		Random ran = new Random();
		int random = ran.nextInt();
		String sessionId = (timeStamp + String.valueOf(random)).replaceAll("-", "");
		//System.out.println("sessionId "+sessionId);
		return sessionId;
	}
}
