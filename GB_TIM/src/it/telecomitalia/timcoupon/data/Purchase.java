package it.telecomitalia.timcoupon.data;

import java.util.Date;

public class Purchase
{
	private Date _purchaseTimeStamp;
	private String _dealId;
	private String _dealName;
	private Coupon _coupon;
	private Gift _gift;

	public Purchase(Date purchaseTimeStamp, String dealId, String dealName, Coupon coupon, Gift gift)
	{
		_purchaseTimeStamp = purchaseTimeStamp;
		_dealId = dealId;
		_dealName = dealName;
		_coupon = coupon;
		_gift = gift;
	}

	public Date getPurchaseTimeStamp()
	{
		return _purchaseTimeStamp;
	}

	public String getDealId()
	{
		return _dealId;
	}

	public String getDealName()
	{
		return _dealName;
	}

	public Coupon getCoupon()
	{
		return _coupon;
	}

	public Gift getGift()
	{
		return _gift;
	}
}
