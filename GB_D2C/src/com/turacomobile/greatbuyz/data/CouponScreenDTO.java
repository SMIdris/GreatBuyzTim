package com.turacomobile.greatbuyz.data;

import java.util.Date;

public class CouponScreenDTO
{
	private String _dealId;
	private String _merchantName;
	private String _name;
	private String _image;
	private String _details;
	private String _category;
	private String _dealVisitUrl;

	private String _couponPrice;
	private String _price;
	private String _discount;
	private Date _expire;
	private boolean _isExclusiveDeal;
	private String _dealLocation;
	private String _couponCode;

	public CouponScreenDTO(String dealId, String merchantName, String name, String image, String details, String couponPrice, String price,
			String discount, String category, String dealVisitUrl, Date expire, boolean isExclusiveDeal, String dealLocation, String couponCode)
	{
		_dealId = dealId;
		_merchantName = merchantName;
		_name = name;
		_image = image;
		_details = details;
		_couponPrice = couponPrice;
		_price = price;
		_discount = discount;
		_category = category;
		_dealVisitUrl = dealVisitUrl;
		_expire = expire;
		_isExclusiveDeal = isExclusiveDeal;
		_dealLocation = dealLocation;
		_couponCode = couponCode;
	}

	public String getDealId()
	{
		return _dealId;
	}
	
	public String getCouponCode()
	{
		return _couponCode;
	}

	public void setCouponCode(String couponCode)
	{
		 _couponCode = couponCode ;
	}

	public String getName()
	{
		return _name;
	}

	public String getImage()
	{
		return _image;
	}

	public String getCouponPrice()
	{
		return _couponPrice;
	}

	public String getPrice()
	{
		return _price;
	}

	public String getDiscount()
	{
		return _discount;
	}

	public String getDetails()
	{
		return _details;
	}

	public String getMerchantName()
	{
		return _merchantName;
	}

	public String getCategory()
	{
		return _category;
	}

	public String getDealVisitUri()
	{
		return _dealVisitUrl;
	}

	public Date get_expire()
	{
		return _expire;
	}

	public void set_expire(Date _expire)
	{
		this._expire = _expire;
	}

	public boolean isExclusiveDeal()
	{
		return _isExclusiveDeal;
	}

	public String get_dealLocation() {
		return _dealLocation;
	}
	
	
}