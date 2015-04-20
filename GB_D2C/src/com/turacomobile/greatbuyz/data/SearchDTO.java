package com.turacomobile.greatbuyz.data;

import java.util.List;

public class SearchDTO
{
	private int _status;
	private int _start;
	private int _numFound;
	private List<Deal> _searchDealsList;

	public SearchDTO(int status, int start, int numFound, List<Deal> searchDeals)
	{
		this._status = status;
		this._start = start;
		this._numFound = numFound;
		this._searchDealsList = searchDeals;
	}

	public int getStatus()
	{
		return _status;
	}

	public int getStart()
	{
		return _start;
	}

	public int getNumFound()
	{
		return _numFound;
	}

	public List<Deal> getSearchDeals()
	{
		return _searchDealsList;
	}

	public void add(List<Deal> deals)
	{
		_searchDealsList.addAll(deals);
	}

	public void deleteAll()
	{
		_searchDealsList.clear();
	}
}
