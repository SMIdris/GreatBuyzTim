package com.turacomobile.greatbuyz.data;

public class PhoneLine
{
	private String _phoneNumber;
	private String _availableFrom;
	private String _availableUpto;

	public PhoneLine(String phoneNumber, String availableFrom, String availableUpto)
	{
		_phoneNumber = phoneNumber;
		_availableFrom = availableFrom;
		_availableUpto = availableUpto;
	}

	public String getPhoneNumber()
	{
		return _phoneNumber;
	}

	public String getAvailableFrom()
	{
		return _availableFrom;
	}

	public String getAvailableUpto()
	{
		return _availableUpto;
	}
}
