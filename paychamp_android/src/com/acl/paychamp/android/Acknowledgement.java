package com.acl.paychamp.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;

public class Acknowledgement extends AsyncTask<String, Integer, String> {

	private String domain = "bill.paychamp.com";
	private String trxId;

	public Acknowledgement(String trxId) {
		this.trxId = trxId;
	}

	public void sendACk(String strId) {

		HttpURLConnection connection = null;
		try {
			URL url = new URL("http://" + domain + "/webservice/trxinfo/save/" + strId);

			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.connect();
			int status = ((HttpURLConnection) connection).getResponseCode();

			String result = "";
			if (status == 200) {
				result = readResponse(connection);
			}
			System.out.println(result);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private static String readResponse(HttpURLConnection connection) {
		StringBuilder result = new StringBuilder();
		InputStream inputStream = null;
		BufferedReader in = null;
		try {
			inputStream = connection.getInputStream();
			in = new BufferedReader(new InputStreamReader(inputStream));
			String sCurrentLine;
			while ((sCurrentLine = in.readLine()) != null) {
				result.append(sCurrentLine);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}

		}
		return result.toString();
	}

	@Override
	protected String doInBackground(String... params) {
		sendACk(trxId);
		return null;
	}
}