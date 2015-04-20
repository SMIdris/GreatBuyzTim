package com.turacomobile.greatbuyz.data;

import java.util.List;

public class DealsByCategoriesDTO
{
	private List<DealScreenDTO> _dealsByCategoriesList;

	public DealsByCategoriesDTO(List<DealScreenDTO> deals)
	{
		this._dealsByCategoriesList = deals;
	}

	public List<DealScreenDTO> getDeals()
	{
		return _dealsByCategoriesList;
	}

	public void add(List<DealScreenDTO> deals)
	{
		_dealsByCategoriesList.addAll(deals);
	}

	public void deleteAll()
	{
		if (_dealsByCategoriesList != null)
			_dealsByCategoriesList.clear();
	}
}
