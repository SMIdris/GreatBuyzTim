package com.turacomobile.greatbuyz.data;

import java.util.List;

public class MyDealsDTONew
{
	private List<DealScreenDTO> _myDealsList;

	public MyDealsDTONew(List<DealScreenDTO> myDealsList)
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
