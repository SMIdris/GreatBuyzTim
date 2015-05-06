package com.turacomobile.greatbuyz.data;

import java.util.List;

public class ExclusiveCouponsDTO
{
	private List<CouponScreenDTO> _exclusiveCouponsList;

	public ExclusiveCouponsDTO(List<CouponScreenDTO> dtos)
	{
		this._exclusiveCouponsList = dtos;
	}

	public List<CouponScreenDTO> getExclusiveCouponsList()
	{
		return _exclusiveCouponsList;
	}

	public void add(List<CouponScreenDTO> deals)
	{
		_exclusiveCouponsList.addAll(deals);
	}

	public void deleteAll()
	{
		if (_exclusiveCouponsList != null)
			_exclusiveCouponsList.clear();
	}
}
