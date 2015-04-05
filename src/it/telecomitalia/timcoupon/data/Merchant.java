package it.telecomitalia.timcoupon.data;

public class Merchant
{
	private String _name;
	private Contact _contact;

	public Merchant(String name, Contact contact)
	{
		_name = name;
		_contact = contact;
	}

	public String getName()
	{
		return _name;
	}

	public Contact getContact()
	{
		return _contact;
	}
}