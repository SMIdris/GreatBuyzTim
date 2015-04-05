package it.telecomitalia.timcoupon.data;

import java.util.Date;

public class TnC
{
	private Date _start;
	private Date _end;
	private int _max;
	private int _min;
	private String _offer;
	private String _howToRedeem;

	public TnC(Date start, Date end, int max, int min, String offer, String howToRedeem)
	{
		_start = start;
		_end = end;
		_max = max;
		_min = min;
		_offer = offer;
		_howToRedeem = howToRedeem;
	}

	public Date getStart()
	{
		return _start;
	}

	public Date getEnd()
	{
		return _end;
	}

	public int getMax()
	{
		return _max;
	}

	public int getMin()
	{
		return _min;
	}

	public String getOffer()
	{
		return _offer;
	}

	public String getHowToRedeem()
	{
		return _howToRedeem;
	}
}