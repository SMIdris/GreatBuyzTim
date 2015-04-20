package com.onmobile.hcoe.io;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.HttpMessage;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;

import android.webkit.CookieManager;

/**
 * Generate http request objects that can be executed by the http client
 * service.
 * 
 * @author rajeev
 */
class HttpMessageFactory
{

	public static final String TAG = "[GreatBuyz]HttpMessageFactory";

	private final String mCookieDomain;

	/**
	 * Creates an httpMessageFactory controlling the specified domain.
	 * 
	 * @param cookieDomain
	 */
	public HttpMessageFactory(String cookieDomain)
	{
		this.mCookieDomain = cookieDomain;

	}

	/**
	 * Create a new http uri request with more standard datatypes.
	 * 
	 * @param url
	 *            The url to push the request to.
	 * @param p_Params
	 *            String parameters to pass in the post.
	 * @throws URISyntaxException
	 */
	public HttpMessage createHttpGetRequest(String url, Map<String, String> headers) throws URISyntaxException
	{
		HttpGet t_HttpMethod = new HttpGet(url);

		if (headers != null)
		{
			for (final String key : headers.keySet())
			{
				t_HttpMethod.addHeader(key, headers.get(key));
			}

			if (mCookieDomain != null)
			{
				final CookieManager cookieManager = CookieManager.getInstance();
				t_HttpMethod.addHeader("Cookie", cookieManager.getCookie(this.mCookieDomain));
			}
		}
		// Log.d(TAG, t_HttpMethod.getRequestLine().toString());
		return t_HttpMethod;
	}

	/**
	 * Create a new http uri request with more standard datatypes.
	 * 
	 * @param url
	 *            The url to push the request to.
	 * @param headers
	 *            String headers to pass.
	 * @throws URISyntaxException
	 */
	public HttpMessage createHttpDeleteRequest(String url, Map<String, String> headers) throws URISyntaxException
	{
		HttpDelete t_HttpMethod = new HttpDelete(url);
		if (headers != null)
		{
			for (final String key : headers.keySet())
			{
				t_HttpMethod.addHeader(key, headers.get(key));
			}

			if (mCookieDomain != null)
			{
				final CookieManager cookieManager = CookieManager.getInstance();
				t_HttpMethod.addHeader("Cookie", cookieManager.getCookie(this.mCookieDomain));
			}
		}

		return t_HttpMethod;
	}

	/**
	 * Create a new http uri request and submit for execution. Promote this to
	 * public when I need to actually instantiate an HttpMessage with these
	 * parameters.
	 * 
	 * @param url
	 *            The url that this request will access.
	 * @param data
	 *            The data to be sent in the request
	 * @return
	 * @throws URISyntaxException
	 */
	public HttpMessage createHttpPostRequest(String url, Map<String, String> headers, byte[] t_data) throws URISyntaxException
	{
		// Log.d(TAG, "Using HttpPost");
		HttpPost t_HttpMethod = new HttpPost(url);
		t_HttpMethod.setEntity(new ByteArrayEntity(t_data));
		if (headers != null)
		{
			for (final String key : headers.keySet())
			{
				t_HttpMethod.addHeader(key, headers.get(key));
			}

			if (mCookieDomain != null)
			{
				final CookieManager cookieManager = CookieManager.getInstance();
				t_HttpMethod.addHeader("Cookie", cookieManager.getCookie(this.mCookieDomain));
			}
		}
		return t_HttpMethod;
	}

	/**
	 * Create a new HttpPut uri request and submit for execution.
	 * 
	 * @param url
	 *            The url that this request will access.
	 * @param headers
	 *            The additional headers to send with the request.
	 * @return
	 * @throws URISyntaxException
	 */

	public HttpMessage createHttpPutRequest(String url, Map<String, String> headers, String t_Data) throws URISyntaxException,
			UnsupportedEncodingException
	{
		HttpPut t_HttpMethod = new HttpPut(url);
		t_HttpMethod.setEntity(new StringEntity(t_Data));

		if (headers != null)
		{
			for (final String key : headers.keySet())
			{
				t_HttpMethod.addHeader(key, headers.get(key));
			}

			if (mCookieDomain != null)
			{
				final CookieManager cookieManager = CookieManager.getInstance();
				t_HttpMethod.addHeader("Cookie", cookieManager.getCookie(this.mCookieDomain));
			}
		}
		return t_HttpMethod;
	}

	/**
	 * Create a new http uri request based on the method parameter and submit
	 * for execution.
	 * 
	 * @param url
	 *            The url that this request will access.
	 * @param headers
	 *            The additional headers to send with the request.
	 * @param method
	 *            The HTTP method to be used
	 * @return
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 * @throws MethodNotSupportedException
	 */

	public HttpMessage createUsingMethod(String url, Map<String, String> headers, String method) throws URISyntaxException,
			UnsupportedEncodingException, MethodNotSupportedException
	{
		if (method.equalsIgnoreCase("get"))
		{
			return createHttpGetRequest(url, headers);
		}
		else if (method.equalsIgnoreCase("delete"))
		{
			return createHttpDeleteRequest(url, headers);
		}
		throw new MethodNotSupportedException("Method " + method + "not allowed");
	}

	public HttpMessage createUsingMethod(String url, Map<String, String> headers, String params, String method) throws URISyntaxException,
			UnsupportedEncodingException, MethodNotSupportedException
	{
		if (method.equalsIgnoreCase("post"))
		{
			return createHttpPostRequest(url, headers, params.getBytes());
		}
		else if (method.equalsIgnoreCase("put"))
		{
			return createHttpPutRequest(url, headers, params);
		}
		throw new MethodNotSupportedException("Method " + method + "not allowed");
	}
}
