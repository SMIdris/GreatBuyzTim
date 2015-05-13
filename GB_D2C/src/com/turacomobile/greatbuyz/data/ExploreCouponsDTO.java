package com.turacomobile.greatbuyz.data;

import java.util.List;

public class ExploreCouponsDTO
{
	private List<CouponScreenDTO> _exploreCouponsList;

	public ExploreCouponsDTO(List<CouponScreenDTO> exploreCouponsList)
	{
		this._exploreCouponsList = exploreCouponsList;
	}

	public List<CouponScreenDTO> getExploreCouponsList()
	{
		return _exploreCouponsList;
	}

	public void add(List<CouponScreenDTO> deals)
	{
		_exploreCouponsList.addAll(deals);
	}

	public void deleteAll()
	{
		if (_exploreCouponsList != null)
			_exploreCouponsList.clear();
	}
}
