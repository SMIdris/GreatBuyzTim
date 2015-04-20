package it.telecomitalia.timcoupon.data;

import java.util.List;

public class Contact
{
	private List<PhoneLine> _phoneLines;
	private String _email;
	private String _url;
	private String _details;

	public Contact(List<PhoneLine> phoneLines, String email, String url, String details)
	{
		_phoneLines = phoneLines;
		_email = email;
		_url = url;
		_details = details;
	}

	public List<PhoneLine> getPhoneLines()
	{
		return _phoneLines;
	}

	public String getEmail()
	{
		return _email;
	}

	public String getUrl()
	{
		return _url;
	}

	public String getDetails()
	{
		return _details;
	}
}