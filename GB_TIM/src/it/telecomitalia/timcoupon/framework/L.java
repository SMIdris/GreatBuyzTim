package it.telecomitalia.timcoupon.framework;

import it.telecomitalia.timcoupon.BuildConfig;
import it.telecomitalia.timcoupon.GreatBuyzApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class L
{

	
	public static void v(String msg)
	{
		if (!BuildConfig.DEBUG) return;
		Log.v(GreatBuyzApplication.TAG, msg);
		logToFile("VERBOSE", msg, null);
	}
	
	public static void i(String msg)
	{
		if (!BuildConfig.DEBUG) return;
		Log.i(GreatBuyzApplication.TAG, msg);
		logToFile("INFO", msg, null);
	}
	
	public static void w(String msg)
	{
		if (!BuildConfig.DEBUG) return;
		Log.w(GreatBuyzApplication.TAG, msg);
		logToFile("WARNING", msg, null);
	}
	
	public static void e( String msg)
	{
		Log.e(GreatBuyzApplication.TAG, msg);
		logToFile("EXCEPTION", msg, null);
	}
	
	
	private static void e(Throwable tr)
	{
		Log.e(GreatBuyzApplication.TAG, tr.getMessage(), tr);
		logToFile("EXCEPTION", tr.toString(), tr);
	}
	
	private static void wtf(String msg)
	{
		Log.println(Log.ASSERT, GreatBuyzApplication.TAG, msg);
		logToFile("WTF", msg, null);
	}
	
	private static void wtf(Throwable tr)
	{
		Log.println(Log.ASSERT, GreatBuyzApplication.TAG, tr.toString());
		logToFile("WTF", tr.toString(), tr);
	}
	
	private static void logToFile(String type, String msg, Throwable tr)
	{
		try
		{
			File sdCard = Environment.getExternalStorageDirectory();
			File FingerprintLogDir = new File(sdCard.getAbsoluteFile(), File.separator + GreatBuyzApplication.TAG + File.separator + "logs");
			if (!FingerprintLogDir.exists()) FingerprintLogDir.mkdirs();
			File FingerprintLog = new File(FingerprintLogDir, "GreatBuyzApplication.log");
			if (!FingerprintLog.exists()) FingerprintLog.createNewFile();
			if (null == tr)
			{
				msg += "\n";
				FileOutputStream fos = new FileOutputStream(FingerprintLog, true);
				fos.write("-----------\n".getBytes());
				fos.write((new DateTime()).toString(DateTimeFormat.longDateTime()).getBytes());
				fos.write("\n".getBytes());
				fos.write(msg.getBytes());
				fos.write("-----------\n\n".getBytes());
				fos.close();
			}
			else
			{
				StringWriter sw = new StringWriter();
				sw.append("\n").append(type).append("\n");
				PrintWriter pw = new PrintWriter(sw);
				tr.printStackTrace(pw);
				//
				FileOutputStream fos = new FileOutputStream(FingerprintLog, true);
				fos.write((new DateTime()).toString(DateTimeFormat.longDateTime()).getBytes());
				fos.write("\n".getBytes());
				fos.write(sw.toString().getBytes());
				sw.close();
				pw.close();
				fos.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void deleteLogs(Context cx)
	{
		try
		{
			File sdCard = Environment.getExternalStorageDirectory();
			File FingerprintLogDir = new File(sdCard.getAbsoluteFile(), File.separator + GreatBuyzApplication.TAG + File.separator + "logs");
			if (FingerprintLogDir.exists()) FileUtils.deleteDirectory(FingerprintLogDir);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	

}
