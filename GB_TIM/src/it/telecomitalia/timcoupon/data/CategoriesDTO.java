package it.telecomitalia.timcoupon.data;

public class CategoriesDTO
{
	private String[] _categories;

	public CategoriesDTO(String[] categories)
	{
		this._categories = categories;
	}

	public String[] getCategories()
	{
		return _categories;
	}
}
