package com.turacomobile.greatbuyz.data;

public class ActivationWithFreeDealDTO
{
	private String _message;
	private Deal _freeDeal;

	public ActivationWithFreeDealDTO(String message, Deal freeDeal)
	{
		this._message = message;
		this._freeDeal = freeDeal;
	}

	public String getMessage()
	{
		return _message;
	}

	public Deal getFreeDeal()
	{
		return _freeDeal;
	}
}
