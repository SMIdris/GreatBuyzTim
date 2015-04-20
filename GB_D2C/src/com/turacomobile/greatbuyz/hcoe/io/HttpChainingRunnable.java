package com.turacomobile.greatbuyz.hcoe.io;

import java.util.HashMap;
import java.util.concurrent.Callable;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * These runnables are used to take a pair of runnables and call the first and
 * setting its HttpResponse on the second, which is then called.
 * 
 * @author rajeev
 */
class HttpChainingRunnable implements Runnable
{
	private static final String LOG = "HttpChainingRunnable";

	private final Callable<HttpResponse> mRequest;
	private final IHttpResponseListener mResponseRunnable;

	/**
	 * @param request
	 *            The http request to make.
	 * @param responseRunnable
	 *            The runnable to call after the request completes.
	 */
	public HttpChainingRunnable(Callable<HttpResponse> request, IHttpResponseListener responseRunnable)
	{
		this.mRequest = request;
		this.mResponseRunnable = responseRunnable;
	}

	public void run()
	{
		try
		{
			// Log.d("HttpChainingRunnable",
			// "Klick -- HttpChainingRunnable.run -- calling mRequest.call");
			// ////System.out.println(System.currentTimeMillis() + ":" +
			// this.mRequest.hashCode());
			final HttpResponse response = this.mRequest.call();
			// ////System.out.println(System.currentTimeMillis() + ":" +
			// this.mRequest.hashCode());
			final int statusCode = response.getStatusLine().getStatusCode();
			// Log.d("HttpChainingRunnable",
			// "Klick -- HttpChainingRunnable.run -- response : "+response);
			if (this.mResponseRunnable != null)
			{
				HeaderIterator t_HeaderIterator = response.headerIterator();
				HashMap<String, String> t_HeaderMap = new HashMap<String, String>();
				while (t_HeaderIterator.hasNext())
				{
					Header t_ResponneHeader = (Header) t_HeaderIterator.next();
					t_HeaderMap.put(t_ResponneHeader.getName(), t_ResponneHeader.getValue());
				}
				HttpClientService.hashkeys.remove(0);
				this.mResponseRunnable.onReceiveResponse(statusCode, t_HeaderMap, EntityUtils.toString(response.getEntity(), "UTF-8"));
				// this.mResponseRunnable.run();
			}
		}
		catch (final Exception e)
		{
			// Log.d(LOG, "Request failed, calling fail() after exception:", e);
			// ////System.out.println(System.currentTimeMillis() + ":" +
			// this.mRequest.hashCode());
			this.mResponseRunnable.onHttpRequestError(e);
		}
	}
}
