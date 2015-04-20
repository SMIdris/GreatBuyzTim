package com.turacomobile.greatbuyz.data;

import java.util.List;

public class ExploreDealsDTO
{
	private List<DealScreenDTO> _exploreDealsList;

	public ExploreDealsDTO(List<DealScreenDTO> exploreDealsList)
	{
		this._exploreDealsList = exploreDealsList;
	}

	public List<DealScreenDTO> getExploreDealsList()
	{
		return _exploreDealsList;
	}

	public void add(List<DealScreenDTO> deals)
	{
		_exploreDealsList.addAll(deals);
	}

	public void deleteAll()
	{
		if (_exploreDealsList != null)
			_exploreDealsList.clear();
	}
}
