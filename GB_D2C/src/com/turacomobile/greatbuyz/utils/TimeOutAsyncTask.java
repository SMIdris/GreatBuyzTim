package com.turacomobile.greatbuyz.utils;

import android.os.AsyncTask;

public class TimeOutAsyncTask extends AsyncTask<Void, Void, Boolean>
{
	private static final String TAG = "TimeOutAsyncTask -- ";
	private long startTime = 0;
	private int timeOut = 0;

	private TimeOutCallback callback;

	public TimeOutAsyncTask(int timeOut, TimeOutCallback callback)
	{
		this.callback = callback;
		this.timeOut = timeOut;
	}

	public void reset()
	{
		startTime = System.currentTimeMillis();
	}

	public void start()
	{
		execute();
	}

	public void cancel()
	{
		cancel(false);
	}

	@Override
	protected Boolean doInBackground(Void... params)
	{
		if (timeOut < 0)
		{
			return false;
		}

		long currentTime = 0;

		reset();

		while (!isCancelled())
		{
			try
			{
				Thread.sleep(1000); // sleep 1 second
			}
			catch (InterruptedException e)
			{
				// Log.e(TAG, "doInBackground() exception", e);
			}

			currentTime = System.currentTimeMillis();

			if (currentTime > getEndTime())
			{
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result)
	{
		if (result && !isCancelled() && callback != null)
		{
			callback.onTimeout();
		}
	}

	private long getEndTime()
	{
		return startTime + timeOut;
	}

	public interface TimeOutCallback
	{
		void onTimeout();
	}

}
