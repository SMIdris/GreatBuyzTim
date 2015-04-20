package it.telecomitalia.timcoupon.data;

public class Location
{
	private String _firstLine;
	private String _secondLine;
	private String _district;
	private String _state;
	private String _country;
	private String _pin;
	private double _longitude;
	private double _latitude;

	public Location(String firstLine, String secondLine, String district, String state, String country, String pin, double longitude,
			double latitude)
	{
		_firstLine = firstLine;
		_secondLine = secondLine;
		_district = district;
		_state = state;
		_country = country;
		_pin = pin;
		_longitude = longitude;
		_latitude = latitude;
	}

	public String getFirstLine()
	{
		return _firstLine;
	}

	public String getSecondLine()
	{
		return _secondLine;
	}

	public String getDistrict()
	{
		return _district;
	}

	public String getState()
	{
		return _state;
	}

	public String getCountry()
	{
		return _country;
	}

	public String getPin()
	{
		return _pin;
	}

	public double getLongitude()
	{
		return _longitude;
	}

	public double getLatitude()
	{
		return _latitude;
	}
}