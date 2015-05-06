package com.turacomobile.greatbuyz.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.cert.CertificateException;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.turacomobile.greatbuyz.GreatBuyzApplication;
import com.turacomobile.greatbuyz.R;
import com.turacomobile.greatbuyz.data.NotificationDTO;
import com.turacomobile.greatbuyz.service.DataController;
import com.turacomobile.greatbuyz.service.ResponseParser;
import com.turacomobile.greatbuyz.ui.CouponDetailScreen;
import com.turacomobile.greatbuyz.ui.DetailScreen;
import com.turacomobile.greatbuyz.ui.DetailScreenNew;
import com.turacomobile.greatbuyz.ui.NotificationActivity;
import com.turacomobile.greatbuyz.ui.SampleTabsStyled;

public final class Utils
{
	public static String getClientVersion()
	{
		String ret = null;

		try
		{
			ret = GreatBuyzApplication.getApplication().getPackageManager()
					.getPackageInfo(GreatBuyzApplication.getApplication().getPackageName(), 0).versionName;
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}

		return ret;
	}

	public static String getIPAddress(boolean useIPv4)
	{
		try
		{
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces)
			{
				List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (InetAddress addr : addrs)
				{
					// ////System.out.println("addr=" + addr);
					if (!addr.isLoopbackAddress())
					{
						String sAddr = addr.getHostAddress().toUpperCase();
						// ////System.out.println("sAddr=" + sAddr);
						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);

						if (useIPv4)
						{
							if (isIPv4)
								return sAddr;
						}
						else
						{
							if (!isIPv4)
							{
								int delim = sAddr.indexOf('%'); // drop ip6 port
																// suffix
								return delim < 0 ? sAddr : sAddr.substring(0, delim);
							}
						}
					}
				}
				// return "0";
			}
		}
		catch (Exception ex)
		{
		} // for now eat exceptions
		return "";
	}

	public static void startDetailsScreen(Activity activity, int index, int type)
	{
		Intent detailScreen = new Intent(activity, DetailScreen.class);
		detailScreen.putExtra(AppConstants.JSONKeys.TYPE, type);
		detailScreen.putExtra(AppConstants.JSONKeys.INDEX, index);
		detailScreen.putExtra(AppConstants.JSONKeys.START_HOME_SCREEN, false);
		detailScreen.putExtra(AppConstants.DEAL_SCREEN, true);
		activity.startActivityForResult(detailScreen, AppConstants.RESULT_DEAL_DETAIL_SCREEN);
	}

	public static void startDetailsScreenNew(Activity activity, int index, int type)
	{
		Intent detailScreen = new Intent(activity, DetailScreenNew.class);
		detailScreen.putExtra(AppConstants.JSONKeys.TYPE, type);
		detailScreen.putExtra(AppConstants.JSONKeys.INDEX, index);
		detailScreen.putExtra(AppConstants.JSONKeys.START_HOME_SCREEN, false);
		detailScreen.putExtra(AppConstants.DEAL_SCREEN, true);
		activity.startActivityForResult(detailScreen, AppConstants.RESULT_DEAL_DETAIL_SCREEN);
	}
	
	public static void startCouponDetailsScreenNew(Activity activity, int index, int type)
	{
		Intent detailScreen = new Intent(activity, CouponDetailScreen.class);
		detailScreen.putExtra(AppConstants.JSONKeys.TYPE, type);
		detailScreen.putExtra(AppConstants.JSONKeys.INDEX, index);
		detailScreen.putExtra(AppConstants.JSONKeys.START_HOME_SCREEN, false);
		detailScreen.putExtra(AppConstants.DEAL_SCREEN, true);
		activity.startActivityForResult(detailScreen, AppConstants.RESULT_DEAL_DETAIL_SCREEN);
	}

	public static void setMessageToTextView(View parentLayout, int resource, String messageKey)
	{
		try
		{
			DataController _data = GreatBuyzApplication.getDataController();
			TextView txtView = (TextView) parentLayout.findViewById(resource);
			txtView.setTypeface(GreatBuyzApplication.getApplication().getFont());
			String message = _data.getMessage(messageKey);
			if (!Utils.isNothing(message))
				txtView.setText(message);
		}
		catch (Exception e)
		{
		}
	}

	public static void setMessageToTextView(TextView txtView, String messageKey)
	{
		try
		{
			DataController _data = GreatBuyzApplication.getDataController();
			String message = _data.getMessage(messageKey);
			if (!Utils.isNothing(message))
				txtView.setText(message);
		}
		catch (Exception e)
		{
		}
	}

	public static void setMessageToButton(Button button, String messageKey)
	{
		try
		{
			DataController _data = GreatBuyzApplication.getDataController();
			String message = _data.getMessage(messageKey);
			if (!Utils.isNothing(message))
				button.setText(message);
		}
		catch (Exception e)
		{
		}
	}

	public static void setConstantToTextView(TextView txtView, String constantKey)
	{
		try
		{
			DataController _data = GreatBuyzApplication.getDataController();
			String constant = _data.getConstant(constantKey);
			txtView.setTypeface(GreatBuyzApplication.getApplication().getFont());
			if (!Utils.isNothing(constant))
				txtView.setText(constant);
		}
		catch (Exception e)
		{
		}
	}

	public static String getMessageString(String messageKey, int fallBackResourceId)
	{
		DataController _data = GreatBuyzApplication.getDataController();
		String strMessage = _data.getMessage(messageKey);
		if (Utils.isNothing(strMessage))
			strMessage = GreatBuyzApplication.getApplication().getResources().getString(fallBackResourceId);
		return strMessage;
	}

	public static boolean useExpandedImageDealLayout()
	{
		boolean useExpandedImageDealLayout = false;
		String temp = GreatBuyzApplication.getDataController().getConstant(AppConstants.Constants.useExpandedImageDealLayout);
		if (Utils.isNothing(temp))
			return useExpandedImageDealLayout;
		useExpandedImageDealLayout = Boolean.parseBoolean(temp);
		return useExpandedImageDealLayout;
	}

	public static void recordUserDetails(Context context, DataController dataController)
	{
		String androidId = getAndroidId(context);
		String imsi = getIMSI(context);

		String oldIMSI = dataController.getIMSI();
		if (!Utils.isNothing(imsi) && !imsi.equalsIgnoreCase(oldIMSI))
			dataController.updateMDN(AppConstants.EMPTY_STRING);
		dataController.updateAndroidId(androidId);
		dataController.updateIMSI(imsi);
	}

	public static void launchUri(Activity activity, String url)
	{
		try
		{
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			activity.startActivity(intent);
		}
		catch (Exception e)
		{
			String strNoApplicationFound = Utils.getMessageString(AppConstants.Messages.NoApplicationFound, R.string.NoApplicationFound);

			Bundle b = new Bundle();
			b.putString(AppConstants.JSONKeys.MESSAGE, strNoApplicationFound);
			activity.showDialog(AppConstants.DialogConstants.MESSAGE_DIALOG, b);
		}
	}

	public static String getAndroidId(Context context)
	{
		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}

	public static String getIMSI(Context context)
	{
		TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telManager.getSimSerialNumber();
	}

	public static boolean isNothing(String text)
	{
		if (TextUtils.isEmpty(text))
			return true;
		if (text.equalsIgnoreCase(AppConstants.NULL_IN_STRING))
			return true;
		return false;
	}

	public static boolean isEmailValid(String email)
	{
		String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@" + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|" + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches())
			return true;
		else
			return false;
	}

	public static String md5(String s)
	{
		try
		{
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static JSONArray convertToJSONArray(String[] strings)
	{
		List<String> collection = Arrays.asList(strings);
		JSONArray jsonArray = new JSONArray(collection);
		return jsonArray;
	}

	public static boolean isApplicationRunning()
	{
		Context appContext = GreatBuyzApplication.getApplication();

		ActivityManager activityManager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);

		if (services.get(0).topActivity.getPackageName().toString().contains(appContext.getPackageName().toString()))
		{
			return true;
		}

		return false;
	}

	public static boolean isDetailDealScreenShowing()
	{
		Context appContext = GreatBuyzApplication.getApplication();

		ActivityManager activityManager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);

		if (services.get(0).topActivity.getClassName().contains(DetailScreen.class.getSimpleName()))
		{
			return true;
		}

		return false;
	}

	public static boolean isDetailDealScreenNewShowing()
	{
		Context appContext = GreatBuyzApplication.getApplication();

		ActivityManager activityManager = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);

		if (services.get(0).topActivity.getClassName().contains(DetailScreenNew.class.getSimpleName()))
		{
			return true;
		}

		return false;
	}

	public static void displayNotification(Context context, Intent notificationIntent, String title)
	{
		System.out.println("NotifyUser jsnfslfsn");
		NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.notif_icon, null, System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		CharSequence contentTitle = Utils.getMessageString(AppConstants.Messages.app_name, R.string.app_name);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, title, contentIntent);

		manager.notify((int) System.currentTimeMillis(), notification);
	}

	public static void NotifyUser(Context context, String message)
	{
		// //System.out.println("message : " + message);
		NotificationDTO notif = null;
		try
		{
			notif = ResponseParser.getNotification(new JSONObject(message));
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}

		if (notif == null)
			return;

		Intent notificationIntent = null;

		if (notif.getType() == AppConstants.NotificationType.URL || notif.getType() == AppConstants.NotificationType.TEXT)
		{
			notificationIntent = new Intent(context, NotificationActivity.class);

		}
		else if (notif.getType() == AppConstants.NotificationType.DEAL)
		{
			notificationIntent = new Intent(context, SampleTabsStyled.class);
			notificationIntent.putExtra(AppConstants.DEAL_SCREEN, true);

		}
		notificationIntent.setAction(message);

		if (isApplicationRunning())
		{
			notificationIntent.putExtra(AppConstants.JSONKeys.START_HOME_SCREEN, false);
		}
		else
		{
			notificationIntent.putExtra(AppConstants.JSONKeys.START_HOME_SCREEN, true);
		}

		displayNotification(context, notificationIntent, notif.getTitle());
	}

	public static void gcmRegister(final Context context) throws UnsupportedOperationException, IllegalStateException
	{
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(context);

		// Make sure the manifest was properly set
		GCMRegistrar.checkManifest(context);

		// Get GCM registration id
		// final String regId = GCMRegistrar.getRegistrationId(context);

		// Check if regid already presents
		// if (regId.equals(AppConstants.EMPTY_STRING))
		// {
		// Registration is not present, register now with GCM
		GCMRegistrar.register(context, AppConstants.GCM.GCM_PROJECT_ID);
		// }
	}

	public static void gcmUnRegister(Context context)
	{
		GCMRegistrar.unregister(context);
		Intent unregIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
		unregIntent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0));
		context.startService(unregIntent);
	}

	public static boolean isFilePresent(String fileName)
	{
		try
		{
			File root = null;
			root = GreatBuyzApplication.getApplication().getApplicationContext().getFilesDir();

			if (root.canRead())
			{
				File f = new File(root, fileName);
				return f.exists();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public static void dumpToFile(String text, String file)
	{
		try
		{
			File root = null;
			root = GreatBuyzApplication.getApplication().getApplicationContext().getFilesDir();

			if (root.canWrite())
			{
				File f = new File(root, file);
				FileOutputStream fos = new FileOutputStream(f, false);
				fos.write(text.getBytes("UTF-8"));
				fos.flush();
				fos.close();
			}
			else
			{
				// ////System.out.println("failed to save file :" + file);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static String readFromFile(String file)
	{
		String text = null;

		try
		{
			File root = null;
			root = GreatBuyzApplication.getApplication().getApplicationContext().getFilesDir();

			if (root.canRead())
			{
				File f = new File(root, file);
				if (f.exists())
				{
					String str = AppConstants.EMPTY_STRING;
					StringBuffer buf = new StringBuffer();
					BufferedReader reader = new BufferedReader(new FileReader(f));
					while ((str = reader.readLine()) != null)
					{
						buf.append(str);
						buf.append(AppConstants.htmlNewLineCharacter);
					}
					reader.close();
					text = buf.toString();
				}
			}
			else
			{
				// ////System.out.println("failed to read file :" + file);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return text;
	}

	public static String readFromAssets(String fileName)
	{
		String text = null;
		try
		{
			StringBuilder sb = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(GreatBuyzApplication.getApplication().getApplicationContext()
					.getAssets().open(fileName)));

			String mLine = reader.readLine();
			while (mLine != null)
			{
				sb.append(mLine);
				mLine = reader.readLine();
			}

			reader.close();
			text = sb.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return text;
	}

	public static String getCacheDirectory()
	{
		Context context = GreatBuyzApplication.getApplication().getApplicationContext();
		File cacheDir;
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(context.getExternalFilesDir(null), AppConstants.CACHE_DIRECTORY);
		else
			cacheDir = context.getFilesDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();

		return cacheDir.getAbsolutePath();
	}

	public static long dirSizeBytes(File dir)
	{
		if (!dir.exists())
			return 0;
		long result = 0;
		File[] fileList = dir.listFiles();
		for (int i = 0; i < fileList.length; i++)
		{
			if (fileList[i].isDirectory())
			{
				result += dirSizeBytes(fileList[i]);
			}
			else
			{
				result += fileList[i].length();
			}
		}
		return result;
	}

	public static void trimDirectoryToSize(File dir, int requiredSizeInMB)
	{
		synchronized (dir)
		{
			if (!dir.exists())
				return;
			File[] fileList = dir.listFiles();
			long currentDirSize = dirSizeBytes(dir);
			long requiredSizeBytes = requiredSizeInMB * 1048576;
			if (currentDirSize <= requiredSizeBytes)
				return;

			for (int i = 0; i < fileList.length; i++)
			{
				if (fileList[i].isDirectory())
					continue;

				long fileBytes = fileList[i].length();
				if (fileList[i].delete())
					currentDirSize -= fileBytes;
				if (currentDirSize <= requiredSizeBytes)
					break;
			}
		}
	}

	public static int getDeviceScreenDensity()
	{
		DisplayMetrics metrics = GreatBuyzApplication.getApplication().getResources().getDisplayMetrics();
		return metrics.densityDpi;
	}

	public static void setError(EditText edt, String errorText)
	{
		int ecolor = Color.RED;
		ForegroundColorSpan fgcspan = new ForegroundColorSpan(ecolor);
		SpannableStringBuilder ssbuilder = new SpannableStringBuilder(errorText);
		ssbuilder.setSpan(fgcspan, 0, errorText.length(), 0);
		edt.setError(ssbuilder);
	}

	public static boolean compareTwoArray(boolean[] first, boolean[] second)
	{
		int arrayLen = first.length;

		for (int index = 0; index < arrayLen; index++)
		{
			if (first[index] != second[index])
			{
				return true;
			}
		}
		return false;
	}

	public static String getCurrentTimeStamp()
	{
		long currentStamp = System.currentTimeMillis();
		Date currentDate = new Date(currentStamp);
		return DateFormat.format("yyyy-MM-dd hh:mm:ss", currentDate).toString();
	}

	public static ArrayList<String> getNotificationFrequencies()
	{
		ArrayList<String> frequencies = new ArrayList<String>();

		try
		{
			DataController _data = GreatBuyzApplication.getDataController();
			String strMinLimit = _data.getConstant(AppConstants.Constants.notificationFrequencyMinLimit);
			String strMaxLimit = _data.getConstant(AppConstants.Constants.notificationFrequencyMaxLimit);

			int minLimit = Integer.valueOf(strMinLimit);
			int maxLimit = Integer.valueOf(strMaxLimit);

			for (int i = minLimit; i <= maxLimit; i++)
				frequencies.add(String.valueOf(i));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return frequencies;
	}
	
	
	/**
	 * @author kiranr
	 * @return
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws UnrecoverableKeyException
	 * @throws java.security.cert.CertificateException
	 */
	public static HttpClient createHttpClient() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException, UnrecoverableKeyException, java.security.cert.CertificateException
	{
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		trustStore.load(null, null);

		SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
		sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	    HttpParams params = new BasicHttpParams();
	 	
	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	    HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
	    HttpProtocolParams.setUseExpectContinue(params, true);
	    
	    // Set time out for the connection
	    ConnManagerParams.setTimeout(params, 60000);
	 	HttpConnectionParams.setConnectionTimeout(params, 60000);
	 	HttpConnectionParams.setSoTimeout(params, 60000);

	    SchemeRegistry schReg = new SchemeRegistry();
	    schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	    schReg.register(new Scheme("https", sf , 443));
	    ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
	    return new DefaultHttpClient(conMgr, params);
	}
	
	
	
	
	/**
	 * Method is used to convert current system time to different time zone time
	 * 
	 * @author kiranr
	 * @return
	 */
	public static String getTimeWithRespectToTimeZone() {
		String timeZone = GreatBuyzApplication.getDataController().getConstant(AppConstants.Constants.serverTimeZone);
		long currentStamp = System.currentTimeMillis();
		Date currentDate = new Date(currentStamp);
		//System.out.println("timeZone"+timeZone);
		TimeZone tz = TimeZone.getTimeZone(timeZone);
		Calendar first = Calendar.getInstance(tz);
		first.setTimeInMillis(currentDate.getTime());
		Calendar output = Calendar.getInstance();
		output.set(Calendar.YEAR, first.get(Calendar.YEAR));
		output.set(Calendar.MONTH, first.get(Calendar.MONTH));
		output.set(Calendar.DAY_OF_MONTH, first.get(Calendar.DAY_OF_MONTH));
		output.set(Calendar.HOUR_OF_DAY, first.get(Calendar.HOUR_OF_DAY));
		output.set(Calendar.MINUTE, first.get(Calendar.MINUTE));
		output.set(Calendar.SECOND, first.get(Calendar.SECOND));
		output.set(Calendar.MILLISECOND, first.get(Calendar.MILLISECOND));
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeStamp = dateFormat.format(output.getTime());
		return timeStamp;
	}
	
	
	public static void setLocationToDealTextView(TextView locationTextView, String location){
		DataController _data = GreatBuyzApplication.getDataController();
		if(!location.equalsIgnoreCase("")){
			System.out.println("_data.getUserDefaultCity()"+_data.getUserDefaultCity());
			System.out.println("_data.getUserCity()"+_data.getUserCity());
			if(_data.getUserCity().equalsIgnoreCase(_data.getUserDefaultCity())){
				locationTextView.setVisibility(View.VISIBLE);
				locationTextView.setText("Location: "+location);
			}
		}
		
	}
	
	/**
	 * to log change request analytics for change frequency or disabled 
	 */
	
	public static void changeFrequencyLog(int frequency){
		if(frequency<0){
			HashMap<String, String> m = new HashMap<String, String>();
			m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.ChangeReq);
			m.put(AppConstants.Flurry.GCMCount, "Disabled");
			// FlurryAgent.logEvent(AppConstants.Flurry.CategoryList, m);
			GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.ChangeReq, m);

		}else if(frequency>0){
			HashMap<String, String> m = new HashMap<String, String>();
			m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.ChangeReq);
			m.put(AppConstants.Flurry.GCMCount, String.valueOf(frequency));
			// FlurryAgent.logEvent(AppConstants.Flurry.CategoryList, m);
			GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.ChangeReq, m);

		}
	}
	
	/**
	 * using to log when categor setting has changed
	 * @param dbcategories
	 * @param selection
	 * @param selectedUnselectedCategories
	 */
	public static void changeCategorySettings(List<String> dbcategories, boolean[] selection, List<Integer>selectedUnselectedCategories){
		for (int i = 0; i < selectedUnselectedCategories.size(); i++) {
			if(selection[selectedUnselectedCategories.get(i)]){
				HashMap<String, String> m = new HashMap<String, String>();
				m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.ChangeReq);
				m.put(AppConstants.Flurry.CategoryAdd, dbcategories.get(i));
				GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.ChangeReq, m);
			}else{
				HashMap<String, String> m = new HashMap<String, String>();
				m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.ChangeReq);
				m.put(AppConstants.Flurry.CategoryRemoved, dbcategories.get(i));
				GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.ChangeReq, m);
				
			}
		}
	}
	
	/**
	 * Using for logging when changing the city settings
	 * @param selectedCity
	 */
	public static void changeCitySettings(String selectedCity){
		HashMap<String, String> m = new HashMap<String, String>();
		m.put(AppConstants.Flurry.CLICK, AppConstants.Flurry.ChangeReq);
		m.put(AppConstants.Flurry.NewCity, selectedCity);
		GreatBuyzApplication.getApplication().getAnalyticsAgent().logEvent(AppConstants.Flurry.ChangeReq, m);
	}
	
	
	public static void receiversOnOff(Context ctx){
		ComponentName receiver = new ComponentName(ctx, "myreceiver.class");
		
		
		PackageManager pm = ctx.getPackageManager();
		
		pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
		
		
	}
}