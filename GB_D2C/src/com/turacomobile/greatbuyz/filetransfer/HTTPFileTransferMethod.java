package com.turacomobile.greatbuyz.filetransfer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import com.turacomobile.greatbuyz.utils.AppConstants;

public class HTTPFileTransferMethod implements IFileTransferMethod
{
	@Override
	public boolean uploadFile(final String host, final String username, final String password, final int port,
			final String srcFileDirectory, final String srcFileName, final String desFileDirectory, final String desFileName)
	{
		Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				doUpload(host, port, srcFileDirectory, srcFileName);
			}
		};

		runnable.run();
		return true;
	}

	private void doUpload(String host, int port, String srcFileDirectory, String srcFileName)
	{
		try
		{
			File fileWithDirectory = new File(srcFileDirectory, srcFileName);
			FileReader fr = new FileReader(fileWithDirectory);
			BufferedReader br = new BufferedReader(fr);
			StringBuilder sb = new StringBuilder();
			String s;
			while ((s = br.readLine()) != null)
			{
				sb.append(s);
				sb.append("\n");
			}
			fr.close();
			
			String analyticsFileHeader = "ts,mdn,imsi,deviceId,event,parameters,extraData\n";
			if(sb.indexOf(analyticsFileHeader) == -1)
				sb.insert(0, analyticsFileHeader);

			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS);
			HttpConnectionParams.setSoTimeout(httpParameters, AppConstants.CONNECTION_TIMEOUT_MILLIS);

			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), port));
			ClientConnectionManager cm = new ThreadSafeClientConnManager(httpParameters, schemeRegistry);

			httpParameters.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, true);
			HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);
			HttpClient client = new DefaultHttpClient(cm, httpParameters);
			HttpPost post = new HttpPost(host);

			post.addHeader("key", "ee4ef426-8cab-4853-ace0-2c1660284a7c");
			post.addHeader("channel", "wap");
			post.addHeader("Content-Type", "text/csv");
			post.addHeader("charset", "utf-8");

			StringEntity postData = new StringEntity(sb.toString());
			//Log.i("HTTPFileTransferMethod", "Analytics: "+sb.toString());
			post.setEntity(postData);

			HttpResponse response = client.execute(post);

			int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode == 200)
			{
				String strResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
				if(strResponse.equalsIgnoreCase(AppConstants.JSONKeys.SUCCESS))
				{
					try
					{
						fileWithDirectory.delete();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		catch (Exception e)
		{
			// Log.d("HTTPFileTransferMethod",
			// "uploadFile: failed to upload file: " + srcFileName);
			e.printStackTrace();
		}
	}
}