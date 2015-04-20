package com.turacomobile.greatbuyz.hcoe.io;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.turacomobile.greatbuyz.utils.AppConstants;

/*
 * Handle http connections.
 * 
 * Make asynchronous http requests with Future actions. We support two threads because historically, thats all browsers
 * are allowed to pull from per domain. Since we're only touching one domain, this matches that expectation
 * 
 * @author rajeev
 * 
 */
public class HttpClientService
{
	/**
	 * Encapsulates the http request in a runnable to be executed. When called,
	 * the passed httpRequest will be executed by the HttpClientService
	 * instance's executor.
	 * 
	 * @author rajeev
	 */
	private class HttpMessageCallable implements Callable<HttpResponse>
	{
		// private static final String LOG = "HttpMessageCallable";
		private final HttpMessage mHttpRequest;

		/**
		 * @param httpRequest
		 *            The http request that will be executed when this is
		 *            called.
		 */
		public HttpMessageCallable(HttpMessage httpRequest)
		{
			this.mHttpRequest = httpRequest;
		}

		/*
		 * When called, the http message will be executed by the
		 * HttpClientService's http client.
		 * 
		 * @return HttpResponse The result of the http request.
		 */
		public HttpResponse call() throws Exception
		{
			return HttpClientService.this.mHttpClient.execute((HttpUriRequest) this.mHttpRequest);
		}
	}

	public static final String TAG = "HttpClientService";

	/**
	 * The thread executor.
	 */
	private final ExecutorService mExecutorService;

	/**
	 * Shard http client.
	 */
	final HttpClient mHttpClient;

	/**
	 * The factory used to generate http requests for this service.
	 */
	private final HttpMessageFactory mHttpMessageFactory;

	/**
	 * The hashkeys holder
	 */
	public static ArrayList<Integer> hashkeys = new ArrayList<Integer>();

	/**
	 * Construct the HttpClientService
	 * 
	 * @param poolSize
	 *            The number of threads allowed to exist at once.
	 * @param cookieDomain
	 *            I'm not quite sure what I'm doing with this. figure it out.
	 */
	public HttpClientService(int poolSize, String cookieDomain)
	{

		// //---------------------- for timeout
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		HttpConnectionParams.setConnectionTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS);

		// Thread Safe Client Connection Manager to be added to the
		// DefaultHttpClient to
		// enable MultiThreaded functioning of the Client.
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		// schemeRegistry.register(new
		// Scheme("https",SSLSocketFactory.getSocketFactory(), 443));

		handleSSLCertificateForHttps(schemeRegistry);

		ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParameters, schemeRegistry);

		// Force HTTP client to use HTTP version 1.1

		// httpParameters.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
		// HttpVersion.HTTP_1_1);

		// //----------------------
		// Use the client defaults and create a client.
		// this.mHttpClient = createHttpClient();//new DefaultHttpClient();
		// Client made using the Connection Manager. Safe across threads
		this.mHttpClient = new DefaultHttpClient(cm, httpParameters);// new
																		// DefaultHttpClient();//

		// Handles generating HttpRequests.
		this.mHttpMessageFactory = new HttpMessageFactory(cookieDomain);

		// Sets up the thread pool part of the service.
		this.mExecutorService = Executors.newFixedThreadPool(poolSize);
	}

	private HttpResponse executeRequest(final HttpMessage httpMessage)
	{

		try
		{
			return this.mHttpClient.execute((HttpUriRequest) httpMessage);
		}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Execute an http request SYNCHRONOUSLY, calling the runnable after. Right
	 * now this is limited in that you can only pass one value per key in the
	 * http post. (The Map is the limiter).
	 * 
	 * @param url
	 *            The url to access
	 * @param responseRunnable
	 *            The runnable to execute upon http request completion.
	 * @throws URISyntaxException
	 */
	/*
	 * public HttpResponse executeRequest(String url) throws URISyntaxException
	 * { final HttpMessage httpMessage = this.mHttpMessageFactory.create(url,
	 * null); return executeRequest(httpMessage); }
	 */

	/**
	 * Execute an http request SYNCHRONOUSLY, calling the runnable after. Right
	 * now this is limited in that you can only pass one value per key in the
	 * http post. (The Map is the limiter).
	 * 
	 * @param url
	 *            The url to access
	 * @param params
	 *            The POST parameters to pass to this request.
	 * @throws URISyntaxException
	 */
	/*
	 * public HttpResponse executeRequest(String url, Map<String, String>
	 * params) throws URISyntaxException { //Log.d(TAG, "Executing"); final
	 * HttpMessage httpMessage = this.mHttpMessageFactory.create(url, params);
	 * return executeRequest(httpMessage); }
	 */

	/**
	 * Execute an http request SYNCHRONOUSLY, calling the runnable after. Right
	 * now this is limited in that you can only pass one value per key in the
	 * http post. (The Map is the limiter).
	 * 
	 * @param url
	 *            The url to access
	 * @param data
	 *            The POST parameters to pass to this request.
	 * @throws URISyntaxException
	 */
	public HttpResponse executeRequest(String url, byte[] data) throws URISyntaxException
	{
		HttpMessage httpMessage = this.mHttpMessageFactory.createHttpPostRequest(url, null, data);
		return this.executeRequest(httpMessage);
	}

	@Override
	public void finalize()
	{
		// Log.e("HttpClientService", "finalize -- shutdownNow()");
		this.mExecutorService.shutdownNow();
	}

	/**
	 * Put this HttpChainingRunnable that is composed of a HttpMessageCallable
	 * and HttpChainingRunnable on the queue to be executed. Upon completion of
	 * the http request, call the runnable. Right now this is limited in that
	 * you can only pass one value per key in the http post. (The Map is the
	 * limiter).
	 * 
	 * @param chainingRunnable
	 *            The runnable that will cause a runnable to be called when an
	 *            http request completes.
	 * @throws InterruptedException
	 */
	private void submitRequest(HttpChainingRunnable chainingRunnable)
	{
		this.mExecutorService.submit(chainingRunnable);
	}

	/**
	 * Put an http request on the queue to be executed ASYNCHRONOUSLY. Upon
	 * completion of the http request, a runnable will be called. Right now this
	 * is limited in that you can only pass one value per key in the http post.
	 * (The Map is the limiter).
	 * 
	 * @param url
	 *            The url to access.
	 * @param responseRunnable
	 *            The runnable to execute upon http request completion.
	 * @return hashvalue - The unique value of the particular request, to
	 *         identify a particular response
	 * @throws URISyntaxException
	 */
	/*
	 * public int submitRequest(String url, IHttpResponseListener
	 * responseRunnable) throws URISyntaxException { final HttpMessage
	 * httpMessage = this.mHttpMessageFactory.create(url, null);
	 * 
	 * int hashvalue = 0; HttpMessageCallable request = new
	 * HttpMessageCallable(httpMessage); hashvalue = request.hashCode();
	 * 
	 * this.submitRequest(new HttpChainingRunnable(request, responseRunnable));
	 * 
	 * this.hashkeys.add(hashvalue); return hashvalue; }
	 */

	/**
	 * Put an http request on the queue to be executed ASYNCHRONOUSLY. Upon
	 * completion of the http request, a runnable will be called. Right now this
	 * is limited in that you can only pass one value per key in the http post.
	 * (The Map is the limiter).
	 * 
	 * @param url
	 *            The url to access
	 * @param params
	 *            The POST parameters to pass to this request.
	 * @param responseRunnable
	 *            The runnable to execute upon http request completion.
	 * @return hashvalue - The unique value of the particular request, to
	 *         identify a particular response
	 * @throws URISyntaxException
	 */
	/*
	 * public int submitRequest(String url, Map<String, String> params,
	 * IHttpResponseListener responseRunnable) throws URISyntaxException { final
	 * HttpMessage httpMessage = this.mHttpMessageFactory.create(url, params);
	 * 
	 * int hashvalue = 0; HttpMessageCallable request = new
	 * HttpMessageCallable(httpMessage); hashvalue = request.hashCode();
	 * 
	 * this.submitRequest(new HttpChainingRunnable(request, responseRunnable));
	 * 
	 * this.hashkeys.add(hashvalue); return hashvalue; }
	 */

	/**
	 * Put an http request on the queue to be executed ASYNCHRONOUSLY. Upon
	 * completion of the http request, a runnable will be called. Right now this
	 * is limited in that you can only pass one value per key in the http post.
	 * (The Map is the limiter).
	 * 
	 * @param url
	 *            The url to access.
	 * @param The
	 *            additional header parameters to pass to this request.
	 * @param The
	 *            data in byte[] to pass to this request.
	 * @param responseRunnable
	 *            The runnable to execute up on http request completion.
	 * @return hashvalue - The unique value of the particular request, to
	 *         identify a particular response
	 * @throws URISyntaxException
	 */
	public int submitRequest(String url, Map<String, String> p_Params, byte[] data, IHttpResponseListener responseRunnable)
			throws URISyntaxException
	{
		HttpMessage httpMessage;

		httpMessage = this.mHttpMessageFactory.createHttpPostRequest(url, p_Params, data);

		int hashvalue = 0;
		HttpMessageCallable request = new HttpMessageCallable(httpMessage);
		hashvalue = request.hashCode();

		this.submitRequest(new HttpChainingRunnable(request, responseRunnable));

		hashkeys.add(hashvalue);
		return hashvalue;
	}

	/**
	 * Put an http request on the queue to be executed ASYNCHRONOUSLY. Upon
	 * completion of the http request, a runnable will be called. Right now this
	 * is limited in that you can only pass one value per key in the http post.
	 * (The Map is the limiter).
	 * 
	 * @param url
	 *            The url to access.
	 * @param The
	 *            additional header parameters to pass to this request.
	 * @param The
	 *            data in String format to pass to this request.
	 * @param responseRunnable
	 *            The runnable to execute up on http request completion.
	 * @return hashvalue - The unique value of the particular request, to
	 *         identify a particular response
	 * @throws URISyntaxException
	 */
	public int submitRequest(String url, Map<String, String> p_Params, String data, IHttpResponseListener responseRunnable)
			throws URISyntaxException, UnsupportedEncodingException
	{
		try
		{
			HttpMessage httpMessage;
			// Log.d("HttpClientService.submitRequest", "data ::: " + data);
			httpMessage = this.mHttpMessageFactory.createHttpPostRequest(url, p_Params, data.getBytes());

			int hashvalue = 0;
			HttpMessageCallable request = new HttpMessageCallable(httpMessage);
			hashvalue = request.hashCode();

			this.submitRequest(new HttpChainingRunnable(request, responseRunnable));

			hashkeys.add(hashvalue);
			return hashvalue;
		}
		catch (Exception e)
		{

			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * Put an http request on the queue to be executed ASYNCHRONOUSLY. Upon
	 * completion of the http request, a runnable will be called. The Http
	 * request is created based on parameter method
	 * 
	 * @param url
	 *            The url to access.
	 * @param The
	 *            additional header parameters to pass to this request.
	 * @param The
	 *            data in String format to pass to this request.
	 * @param responseRunnable
	 *            The runnable to execute up on http request completion.
	 * @param method
	 *            The Http request is created using method specified
	 * @throws URISyntaxException
	 */
	public void submitRequest(String url, Map<String, String> p_Params, String data, IHttpResponseListener responseRunnable, String method)
			throws URISyntaxException, UnsupportedEncodingException, MethodNotSupportedException
	{
		try
		{
			HttpMessage httpMessage;
			// Log.d(TAG, "submitRequest data ::: " + data);
			httpMessage = this.mHttpMessageFactory.createUsingMethod(url, p_Params, data, method);
			int hashvalue = 0;
			HttpMessageCallable request = new HttpMessageCallable(httpMessage);
			hashvalue = request.hashCode();
			hashkeys.add(hashvalue);
			this.submitRequest(new HttpChainingRunnable(request, responseRunnable));
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void submitRequest(String url, Map<String, String> p_Headers, IHttpResponseListener responseRunnable, String method)
			throws URISyntaxException, UnsupportedEncodingException, MethodNotSupportedException
	{
		try
		{
			HttpMessage httpMessage;
			httpMessage = this.mHttpMessageFactory.createUsingMethod(url, p_Headers, method);
			HttpMessageCallable request = new HttpMessageCallable(httpMessage);
			int hashvalue = 0;
			hashvalue = request.hashCode();
			hashkeys.add(hashvalue);
			// ////System.out.println(System.currentTimeMillis() + ":" + url +
			// ":" + request.hashCode());
			this.submitRequest(new HttpChainingRunnable(request, responseRunnable));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Called using the hashvalue recieved at the call back of the
	 * IHttpResponseListener of the request
	 * 
	 * @param hashvalue
	 * @return - The index of the particular request made
	 */
	public int getIndexUsingHash(int hashvalue)
	{
		return hashkeys.indexOf(hashvalue) + 1;
	}

	public void handleSSLCertificateForHttps(SchemeRegistry schemeRegistry)
	{
		KeyStore trustStore = null;
		try
		{
			trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		}
		catch (KeyStoreException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try
		{
			trustStore.load(null, null);
		}
		catch (NoSuchAlgorithmException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (CertificateException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try
		{
			schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(trustStore), 443));
		}
		catch (KeyManagementException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (UnrecoverableKeyException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (NoSuchAlgorithmException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch (KeyStoreException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public class EasySSLSocketFactory extends SSLSocketFactory
	{
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public EasySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException,
				UnrecoverableKeyException
		{
			super(truststore);

			TrustManager tm = new X509TrustManager()
			{
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
				{
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
				{
				}

				public X509Certificate[] getAcceptedIssuers()
				{
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException
		{
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException
		{
			return sslContext.getSocketFactory().createSocket();
		}
	}
}