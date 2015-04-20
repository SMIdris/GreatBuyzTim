package com.turacomobile.greatbuyz.data;

import java.util.Date;

public class Coupon
{
	private String _couponId;
	private Date _issueDate;
	private Date _expiryDate;

	public Coupon(String couponId, Date issueDate, Date expiryDate)
	{
		_couponId = couponId;
		_issueDate = issueDate;
		_expiryDate = expiryDate;
	}

	public String getCouponId()
	{
		return _couponId;
	}

	public Date getIssueDate()
	{
		return _issueDate;
	}

	public Date getExpiryDate()
	{
		return _expiryDate;
	}
}
