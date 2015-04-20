package com.onmobile.hcoe.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class GenericDialog extends Dialog implements android.view.View.OnClickListener
{

	private final String TAG_CONTINUE = "continue";
	private final String TAG_EXIT = "exit";

	static final int FB_BLUE = 0xFF6D84B4;
	static final float[] DIMENSIONS_LANDSCAPE = { 350, 200 };
	static final float[] DIMENSIONS_PORTRAIT = { 280, 420 };
	static final FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.FILL_PARENT);
	static final int MARGIN = 4;
	static final int PADDING = 2;
	static final String DISPLAY_STRING = "touch";
	static final String FB_ICON = "icon.png";

	LinearLayout buttonPanel;

	private View mContentView;

	private int mLayout;

	public GenericDialog(Context context, int layout)
	{
		super(context);
		mLayout = layout;
		mContentView = LayoutInflater.from(context).inflate(mLayout, null);
	}

	public GenericDialog(Context context, int layout, int theam)
	{
		super(context, theam);
		mLayout = layout;
		mContentView = LayoutInflater.from(context).inflate(mLayout, null);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		Display display = getWindow().getWindowManager().getDefaultDisplay();
		final float scale = getContext().getResources().getDisplayMetrics().density;
		float[] dimensions = display.getWidth() < display.getHeight() ? DIMENSIONS_PORTRAIT : DIMENSIONS_LANDSCAPE;

		addContentView(mContentView, new FrameLayout.LayoutParams((int) (dimensions[0] * scale + 0.5f), LayoutParams.WRAP_CONTENT));

	}

	public void setItemOnClickListener(int id, final String tag, android.view.View.OnClickListener l)
	{
		View v = mContentView.findViewById(id);
		if (v != null)
		{
			v.setOnClickListener(l);
			v.setTag(tag);
		}
	}

	public View findViewById(int id)
	{
		return mContentView.findViewById(id);
	}

	@Override
	public void onClick(View v)
	{

		String tag = (String) v.getTag();
		if (tag.equals(TAG_CONTINUE))
		{

		}
		else if (tag.equals(TAG_EXIT))
		{
		}

		dismiss();
	}
}
