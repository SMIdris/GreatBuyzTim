package com.turacomobile.greatbuyz.data;

public class NotificationDTO
{
	private int _type;
	private String _title;
	private String _description;
	private String _url;
	private String _dealid;

	public NotificationDTO(int type, String title, String description, String url, String dealid)
	{
		this._type = type;
		this._title = title;
		this._description = description;
		this._url = url;
		this._dealid = dealid;
	}

	public final int getType()
	{
		return _type;
	}

	public final String getTitle()
	{
		return _title;
	}

	public final String getDescription()
	{
		return _description;
	}

	public final String getUrl()
	{
		return _url;
	}

	public final String getDealId()
	{
		return _dealid;
	}
}
