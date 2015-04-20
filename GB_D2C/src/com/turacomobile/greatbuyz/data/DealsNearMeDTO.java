package com.turacomobile.greatbuyz.data;

import java.util.List;

public class DealsNearMeDTO
{
	private List<DealScreenDTO> _dealsNearMeList;

	public DealsNearMeDTO(List<DealScreenDTO> dealsOfTheDayList)
	{
		this._dealsNearMeList = dealsOfTheDayList;
	}

	public List<DealScreenDTO> getDealsNearMeList()
	{
		return _dealsNearMeList;
	}

	public void add(List<DealScreenDTO> deals)
	{
		_dealsNearMeList.addAll(deals);
	}

	public void deleteAll()
	{
		if (_dealsNearMeList != null)
			_dealsNearMeList.clear();
	}
}
