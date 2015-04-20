package com.turacomobile.greatbuyz.data;

import android.os.Parcel;
import android.os.Parcelable;

/** TContactItem.java
*
* Source file for TContactItem class
* Class for creating Contact. 
*/

public class TContactItem implements Parcelable
{
	public static final String TAG = "ContactItem :";
	private String contactType;
	private String contact;
	private String contactName;

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public static final Parcelable.Creator<TContactItem> CREATOR = new Parcelable.Creator<TContactItem>() 
	{
		public TContactItem createFromParcel(Parcel a_Source) 
		{
			return new TContactItem(a_Source);
		}
		
		@Override
		public TContactItem[] newArray(int a_Size) 
		{
			return new TContactItem[a_Size];
		}
	};

	public TContactItem() 
	{
		
	}
	
	public TContactItem(String a_ContactType, String a_ContactNumber , String a_contactName) 
	{
		contactType = a_ContactType;
		contact     = a_ContactNumber;
		contactName = a_contactName;
	}
	
	public TContactItem(Parcel a_Source) 
	{
		contactType = a_Source.readString();
		contact     = a_Source.readString();
		contactName = a_Source.readString();
	}
	
	public String getContactType() 
	{
		return contactType;
	}
	
	public void setContactType(String contactType) 
	{
		this.contactType = contactType;
	}
	
	public String getContact() 
	{
		return contact;
	}
	
	public void setContact(String contact) 
	{
		this.contact = contact;
	}

	@Override
	public int describeContents() 
	{
		return hashCode();
	}

	@Override
	public void writeToParcel(Parcel a_Destination, int a_Flags) 
	{
		a_Destination.writeString(contactType);
		a_Destination.writeString(contact);
		a_Destination.writeString(contactName);
	}
}
