package it.telecomitalia.timcoupon.data;

import java.util.List;

public class ExclusiveDealsDTO
{
	private List<DealScreenDTO> _exclusiveDealsList;

	public ExclusiveDealsDTO(List<DealScreenDTO> exclusiveDealsList)
	{
		this._exclusiveDealsList = exclusiveDealsList;
	}

	public List<DealScreenDTO> getExclusiveDealsList()
	{
		return _exclusiveDealsList;
	}

	public void add(List<DealScreenDTO> deals)
	{
		_exclusiveDealsList.addAll(deals);
	}

	public void deleteAll()
	{
		if (_exclusiveDealsList != null)
			_exclusiveDealsList.clear();
	}
}
