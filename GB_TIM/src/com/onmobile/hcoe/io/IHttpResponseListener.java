package com.onmobile.hcoe.io;

import java.util.HashMap;

/**
 * @author rajeev
 */
public interface IHttpResponseListener
{
	/**
	 * Called when an http request fails for any reason.
	 */
	abstract public void onHttpRequestError(Exception e);

	public void onReceiveResponse(int statusCode, HashMap<String, String> p_ResponseHeaders, String p_Response);
}
