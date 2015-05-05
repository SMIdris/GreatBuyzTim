package com.acl.paychamp.android;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONException;
import org.json.JSONObject;

public class EncryptedText {

	private JSONObject createReceiptPayload(String itemRef, String countryCode, int contentType, double price, double contentSize) {
		final JSONObject payload = new JSONObject();
		try {
			payload.put("algorithm", "HMAC-SHA256");
			final JSONObject data = new JSONObject();
			data.put("itemRef", itemRef);
			data.put("price", price);
			data.put("countryCode", countryCode);
			data.put("contentType", contentType);
			data.put("contentSize", contentSize);
			payload.put("data", data);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return payload;
	}

	public String getEncryptedJson(String itemRef, int contentType, double price, double contentSize, String passKey) {

		JSONObject json = createReceiptPayload(itemRef, "IN", contentType, price, contentSize);

		String encryptedText = null;
		encryptedText = encryptText(passKey, json.toString().getBytes());

		return encryptedText;
	}

	private String encryptText(String passKey, byte[] input) {
		String encriptedToken = null;
		try {

			String encPayload = CustomBase64.encodeBase64URLSafeString(input);
			String sig = CustomBase64.encodeBase64URLSafeString(createSignature(encPayload, passKey).getBytes());
			encriptedToken = sig + "." + encPayload;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encriptedToken;
	}

	private String createSignature(String payload, final String screte) {
		String retVal = null;
		try {
			SecretKeySpec secret = new SecretKeySpec(screte.getBytes(), "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(secret);
			byte[] enc = mac.doFinal(payload.getBytes());
			retVal = new String(Hex.encodeHex(enc));
		} catch (Exception e) {
		}
		return retVal;
	}

}
