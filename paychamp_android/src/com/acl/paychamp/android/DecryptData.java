package com.acl.paychamp.android;

import org.json.JSONException;
import org.json.JSONObject;

public class DecryptData {

	public static String getDecryptedText(String key) {
		String decryptedText = null;
		JSONObject json = null;
		if (key != null) {
			try {
				json = digestSignedRequest(key);
				JSONObject data = json.getJSONObject("data");
				decryptedText = data.get("msg").toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return decryptedText;
	}

	public static String getTransactionValues(JSONObject data, String jsonkey) {
		try {
			return data.get(jsonkey).toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getDecryptedText(String key, final String passKey) {
		String decryptedText = null;
		JSONObject json = null;
		if (key != null) {
			try {
				json = digestSignedRequest(key);
				JSONObject data = new JSONObject(json.get("data").toString());
				decryptedText = data.get("msg").toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return decryptedText;
	}

	public static JSONObject getDecryptedJson(String text) {
		JSONObject data = null;
		JSONObject json = null;
		if (text != null) {
			try {
				json = digestSignedRequest(text);
				data = new JSONObject(json.get("data").toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	static JSONObject digestSignedRequest(final String signedRequest) throws Exception {
		JSONObject data = null;
		String[] signedRequestPart = signedRequest.split("\\.");
		String payload = signedRequestPart[1];
		String datastr = base64UrlDecode(payload);
		data = new JSONObject(datastr);
		return data;
	}

	static String base64UrlDecode(String input) {
		CustomBase64 decoder = new CustomBase64();
		byte[] decodedBytes = decoder.decode(input);
		return new String(decodedBytes);
	}
}
