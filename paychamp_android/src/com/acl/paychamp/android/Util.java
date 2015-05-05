package com.acl.paychamp.android;

/*
 * Copyright 2010 Facebook, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * Utility class supporting the Facebook Object.
 * 
 * @author ssoneff@facebook.com
 * 
 */
public final class Util {

	/**
	 * Set this to true to enable log output. Remember to turn this back off before releasing. Sending sensitive data to
	 * log is a security risk.
	 */
	private static boolean ENABLE_LOG = true;

	/**
	 * Generate the multi-part post body providing the parameters and boundary string
	 * 
	 * @param parameters
	 *            the parameters need to be posted
	 * @param boundary
	 *            the random string as boundary
	 * @return a string of the post body
	 */
	public static String encodePostBody(Bundle parameters, String boundary) {
		if (parameters == null)
			return "";
		StringBuilder sb = new StringBuilder();

		for (String key : parameters.keySet()) {
			Object parameter = parameters.get(key);
			if (!(parameter instanceof String)) {
				continue;
			}

			sb.append("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n" + (String) parameter);
			sb.append("\r\n" + "--" + boundary + "\r\n");
		}

		return sb.toString();
	}

	public static String encodeUrl(Bundle parameters) {
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			Object parameter = parameters.get(key);
			if (!(parameter instanceof String)) {
				continue;
			}

			if (first)
				first = false;
			else
				sb.append("/");
			sb.append(URLEncoder.encode(parameters.getString(key)));
		}
		return sb.toString();
	}

	public static Bundle decodeUrl(String s) {
		Bundle params = new Bundle();
		if (s != null) {
			String array[] = s.split("&");
			for (String parameter : array) {
				String v[] = parameter.split("=");
				if (v.length == 2) {
					params.putString(URLDecoder.decode(v[0]), URLDecoder.decode(v[1]));
				}
			}
		}
		return params;
	}

	/**
	 * Parse a URL query and fragment parameters into a key-value bundle.
	 * 
	 * @param url
	 *            the URL to parse
	 * @return a dictionary bundle of keys and values
	 */
	public static Bundle parseUrl(String url) {
		try {
			URL u = new URL(url);
			Bundle b = decodeUrl(u.getQuery());
			return b;
		} catch (MalformedURLException e) {
			return new Bundle();
		}
	}

	/**
	 * Display a simple alert dialog with the given text and title.
	 * 
	 * @param context
	 *            Android context in which the dialog should be displayed
	 * @param title
	 *            Alert dialog title
	 * @param text
	 *            Alert dialog message
	 */
	public static void showAlert(Context context, String title, String text) {
		Builder alertBuilder = new Builder(context);
		alertBuilder.setTitle(title);
		alertBuilder.setMessage(text);
		alertBuilder.create().show();
	}

	/**
	 * A proxy for Log.d api that kills log messages in release build. It not recommended to send sensitive information
	 * to log output in shipping apps.
	 * 
	 * @param tag
	 * @param msg
	 */
	public static void logd(String tag, String msg) {
		if (ENABLE_LOG) {
			Log.d(tag, msg);
		}
	}
}
