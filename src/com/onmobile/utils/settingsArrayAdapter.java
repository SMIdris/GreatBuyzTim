package com.onmobile.utils;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import it.telecomitalia.timcoupon.data.SettingItem;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class settingsArrayAdapter extends ArrayAdapter<SettingItem>
{

	List<SettingItem> items;
	Context context = null;
	LayoutInflater inflater = null;
	int resourceId = 0;

	public settingsArrayAdapter(Context context, int resourceId, List<SettingItem> items)
	{
		super(context, resourceId, items);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.resourceId = resourceId;
		this.context = context;
		this.items = items;
	}

	@Override
	public SettingItem getItem(int position)
	{
		return items.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = inflater.inflate(resourceId, parent, false);

		TextView text = (TextView) view.findViewById(android.R.id.text1);
		if (text == null)
		{
			// android.util.Log.e(getClass().getSimpleName(),
			// "textview is null");
		}
		text.setText(items.get(position).name);
		text.setTypeface(GreatBuyzApplication.getApplication().getFont());
		text.setTextSize(13);
		/* if (items.get(position).icon != null) */
		text.setCompoundDrawablePadding(15);
		text.setCompoundDrawablesWithIntrinsicBounds(items.get(position).icon, null, null, null);

		return view;
	}

}