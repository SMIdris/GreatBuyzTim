package com.turacomobile.greatbuyz.data;

import java.util.List;

public class DealsOfTheDayDTO
{
	private List<DealScreenDTO> _dealsOfTheDayList;

	public DealsOfTheDayDTO(List<DealScreenDTO> dealsOfTheDayList)
	{
		this._dealsOfTheDayList = dealsOfTheDayList;
	}

	public List<DealScreenDTO> getDealsOfTheDayList()
	{
		return _dealsOfTheDayList;
	}

	public void add(List<DealScreenDTO> deals)
	{
		_dealsOfTheDayList.addAll(deals);
	}

	public void deleteAll()
	{
		if (_dealsOfTheDayList != null)
			_dealsOfTheDayList.clear();
	}
}
