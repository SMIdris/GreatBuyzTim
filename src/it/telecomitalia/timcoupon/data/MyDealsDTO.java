package it.telecomitalia.timcoupon.data;

import java.util.List;

public class MyDealsDTO
{
	private List<DealScreenDTO> _myDealsList;

	public MyDealsDTO(List<DealScreenDTO> myDealsList)
	{
		this._myDealsList = myDealsList;
	}

	public List<DealScreenDTO> getMyDealsList()
	{
		return _myDealsList;
	}

	public void add(List<DealScreenDTO> deals)
	{
		_myDealsList.addAll(deals);
	}

	public void deleteAll()
	{
		if (_myDealsList != null)
			_myDealsList.clear();
	}
}
