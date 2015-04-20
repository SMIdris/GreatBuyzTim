package com.turacomobile.greatbuyz.data;

public class Gift
{
	private String _friend;
	private String _msisdn;
	private String _message;

	public Gift(String friend, String msisdn, String message)
	{
		_friend = friend;
		_msisdn = msisdn;
		_message = message;
	}

	public String getFriend()
	{
		return _friend;
	}

	public String getMsisdn()
	{
		return _msisdn;
	}

	public String getMessage()
	{
		return _message;
	}
}
