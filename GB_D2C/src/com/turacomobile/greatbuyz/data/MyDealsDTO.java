package com.turacomobile.greatbuyz.data;

import java.util.List;

public class MyDealsDTO
{
	private List<Purchase> _myDealsList;

	public MyDealsDTO(List<Purchase> myDealsList)
	{
		this._myDealsList = myDealsList;
	}

	public List<Purchase> getMyDealsList()
	{
		return _myDealsList;
	}

	public void add(List<Purchase> history)
	{
		_myDealsList.addAll(history);
	}

	public void deleteAll()
	{
		if (_myDealsList != null)
			_myDealsList.clear();
	}
}