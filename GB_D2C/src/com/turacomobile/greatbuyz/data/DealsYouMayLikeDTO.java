package com.turacomobile.greatbuyz.data;

import java.util.List;

public class DealsYouMayLikeDTO
{
	private List<DealScreenDTO> _dealsYouMayLikeList;

	public DealsYouMayLikeDTO(List<DealScreenDTO> dealsYouMayLikeList)
	{
		this._dealsYouMayLikeList = dealsYouMayLikeList;
	}

	public List<DealScreenDTO> getDealsOfTheDayList()
	{
		return _dealsYouMayLikeList;
	}

	public void add(List<DealScreenDTO> deals)
	{
		_dealsYouMayLikeList.addAll(deals);
	}

	public void deleteAll()
	{
		if (_dealsYouMayLikeList != null)
			_dealsYouMayLikeList.clear();
	}

	public void set_dealsYouMayLikeList(List<DealScreenDTO> dealsYouMayLikeList)
	{
		_dealsYouMayLikeList = dealsYouMayLikeList;
	}
}
