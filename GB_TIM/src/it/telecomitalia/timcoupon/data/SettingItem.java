package it.telecomitalia.timcoupon.data;

import android.graphics.drawable.Drawable;

public class SettingItem
{
	public SettingItem(int id, String name, Drawable icon)
	{
		this.id = id;
		this.name = name;
		this.icon = icon;
	}

	public int id;
	public String name;
	public Drawable icon;

	@Override
	public String toString()
	{
		return this.name;
	}
}