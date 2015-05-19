package com.turacomobile.greatbuyz.data;

import java.util.Date;
import java.util.List;

public class CouponDeal
{
	private String _id;
	private String _refId;
	private String _name;
	private String _description;
	private String _longDescription;
	private String _image;
	private String _language;
	private String _category;
	private String _source;
	private String _dealVisitUrl;

	private List<String> _operators;
	private List<String> _tags;
	private List<String> _reviews;

	private int _promotionPriority;

	private Date _retrievedOn;

	private String _couponPrice;
	private String _couponCode;
	private String _price;
	private String _discount;

	private Contact _contact;
	private String _location;
	private TnC _tnc;
	private Merchant _merchant;

	public CouponDeal(String id, String refId, int promotionPriority, String name, String description, String longDescription, String image,
			String couponPrice, String price, String discount, Contact contact, String locations, String language, String category,
			TnC tnc, Merchant merchant, List<String> operators, List<String> tags, List<String> reviews, String source, Date retrievedOn,
			String dealVisitUrl,String couponCode)
	{
		_id = id;
		_refId = refId;
		_name = name;
		_description = description;
		_longDescription = longDescription;
		_image = image;
		_language = language;
		_category = category;
		_source = source;
		_operators = operators;
		_tags = tags;
		_reviews = reviews;
		_promotionPriority = promotionPriority;
		_retrievedOn = retrievedOn;
		_couponPrice = couponPrice;
		_price = price;
		_discount = discount;
		_contact = contact;
		_location = locations;
		_tnc = tnc;
		_merchant = merchant;
		_dealVisitUrl = dealVisitUrl;
		_couponCode = couponCode;
	}

	public String getId()
	{
		return _id;
	}

	public String getRefId()
	{
		return _refId;
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

	public String getDescription()
	{
		return _description;
	}

	public String getLongDescription()
	{
		return _longDescription;
	}

	public String getImage()
	{
		return _image;
	}

	public String getLanguage()
	{
		return _language;
	}

	public String getCategory()
	{
		return _category;
	}

	public String getSource()
	{
		return _source;
	}

	public List<String> getOperators()
	{
		return _operators;
	}

	public List<String> getTags()
	{
		return _tags;
	}

	public List<String> getReviews()
	{
		return _reviews;
	}

	public int getPromotionPriority()
	{
		return _promotionPriority;
	}

	public Date getRetrievedOn()
	{
		return _retrievedOn;
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

	public Contact getContact()
	{
		return _contact;
	}

	public String getLocations()
	{
		return _location;
	}

	public TnC getTnC()
	{
		return _tnc;
	}

	public Merchant getMerchant()
	{
		return _merchant;
	}

	public String getDealVisitUrl()
	{
		return _dealVisitUrl;
	}

	public void setPrice(String price)
	{
		_price = price;
	}

	public void setName(String name)
	{
		_name = name;
	}
}