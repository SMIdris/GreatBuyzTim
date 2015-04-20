package com.onmobile.utils;

import it.telecomitalia.timcoupon.GreatBuyzApplication;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class GreatBuyzTextView extends TextView
{

	public GreatBuyzTextView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	public GreatBuyzTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public GreatBuyzTextView(Context context)
	{
		super(context);
		init();
	}

	private void init()
	{
		if (!isInEditMode())
		{
			Typeface tf = GreatBuyzApplication.getApplication().getFont();
			setTypeface(tf);
		}
	}
}