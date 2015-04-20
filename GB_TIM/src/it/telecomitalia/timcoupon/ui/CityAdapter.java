package it.telecomitalia.timcoupon.ui;

import it.telecomitalia.timcoupon.GreatBuyzApplication;
import it.telecomitalia.timcoupon.R;

import java.util.List;

import com.onmobile.utils.AppConstants;
import com.onmobile.utils.Utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class CityAdapter extends ArrayAdapter<String>
{

	List<String> data = null;
	boolean isCallFromExplore = false;

	public CityAdapter(Context context, int layout, int textViewResourceId, List<String> cities, boolean isCallFromExplore)
	{
		super(context, layout, textViewResourceId);
		this.data = cities;
		this.isCallFromExplore = isCallFromExplore;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		if (isCallFromExplore)
		{
			return data.size() + 1;
		}
		else
		{
			return data.size();
		}
	}

	@Override
	public String getItem(int position)
	{
		// TODO Auto-generated method stub
		if (isCallFromExplore)
		{
			if (position == 0)
			{
				String strAll = Utils.getMessageString(AppConstants.Messages.all, R.string.all);
				return strAll;
			}
			else
			{
				return data.get(position - 1);
			}
		}
		else
		{
			return data.get(position);
		}
	}

	public TextView getView(int position, View convertView, ViewGroup parent)
	{
		TextView v = (TextView) super.getView(position, convertView, parent);
		v.setText(getItem(position));
		v.setTypeface(GreatBuyzApplication.getApplication().getFont());
		v.setTextColor(Color.parseColor("#484747"));
		return v;
	}

	public TextView getDropDownView(int position, View convertView, ViewGroup parent)
	{
		TextView v = (TextView) super.getView(position, convertView, parent);
		v.setText(getItem(position));
		v.setTypeface(GreatBuyzApplication.getApplication().getFont());
		v.setTextColor(Color.parseColor("#484747"));
		return v;
	}

}