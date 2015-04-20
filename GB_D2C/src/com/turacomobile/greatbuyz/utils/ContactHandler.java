package com.turacomobile.greatbuyz.utils;


import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;

import com.turacomobile.greatbuyz.data.TContactItem;

/**
 * ContactHandler.java
 * 
 * Source file for ContactHandler class Class to manage Contact with multiple
 * phone numbers.
 */

public class ContactHandler
{
	public static final String TAG = "ContactHandler -";
	Context mContext;

	public ContactHandler(Context mContext)
	{

		this.mContext = mContext;
	}

	public ArrayList<TContactItem> getSelectedContactArray(Intent a_Intent)
	{
		ArrayList<TContactItem> numberList = new ArrayList<TContactItem>();

		if (a_Intent != null)
		{
			Uri contactData = a_Intent.getData();
			Cursor cursor = mContext.getContentResolver().query(contactData, null, null, null, null);
			String name = "";
			if (cursor.moveToFirst())
			{
				String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				// Get all phone numbers.
				Cursor phones = mContext.getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " + contactId, null,
						null);
				int nameId = cursor.getColumnIndex(Contacts.DISPLAY_NAME);
				name = cursor.getString(nameId);
				while (phones.moveToNext())
				{
					int type = phones.getInt(phones.getColumnIndex(Phone.TYPE));
					switch (type)
					{
						case Phone.TYPE_HOME:
							if (phones.getString(phones.getColumnIndex(Phone.NUMBER)) != null)
							{
								numberList.add(new TContactItem((String) Phone.getTypeLabel(mContext.getResources(), Phone.TYPE_HOME, ""),
										phones.getString(phones.getColumnIndex(Phone.NUMBER)), name));
							}
							break;
						case Phone.TYPE_MOBILE:
							if (phones.getString(phones.getColumnIndex(Phone.NUMBER)) != null)
							{
								numberList.add(new TContactItem(
										(String) Phone.getTypeLabel(mContext.getResources(), Phone.TYPE_MOBILE, ""), phones
												.getString(phones.getColumnIndex(Phone.NUMBER)), name));
							}
							break;
						case Phone.TYPE_WORK:
							if (phones.getString(phones.getColumnIndex(Phone.NUMBER)) != null)
							{
								numberList.add(new TContactItem((String) Phone.getTypeLabel(mContext.getResources(), Phone.TYPE_WORK, ""),
										phones.getString(phones.getColumnIndex(Phone.NUMBER)), name));
							}
							break;
						case Phone.TYPE_OTHER:
							if (phones.getString(phones.getColumnIndex(Phone.NUMBER)) != null)
							{
								numberList.add(new TContactItem((String) Phone.getTypeLabel(mContext.getResources(), Phone.TYPE_OTHER, ""),
										phones.getString(phones.getColumnIndex(Phone.NUMBER)), name));
							}
							break;
					}
				}
				phones.close();
			}
			cursor.close();
		}
		
		return numberList;

	}

}
