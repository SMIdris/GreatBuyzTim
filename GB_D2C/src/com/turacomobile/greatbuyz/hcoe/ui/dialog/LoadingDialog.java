package com.turacomobile.greatbuyz.hcoe.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.turacomobile.greatbuyz.R;


public class LoadingDialog extends Dialog
{
	Context mContext;

	public LoadingDialog(Context context, int theme)
	{
		super(context, theme);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	public LoadingDialog(Context context, int theme, String message1, String message2)
	{
		super(context, theme);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View inflateView = inflater.inflate(R.layout.loading_dialog, (ViewGroup) findViewById(R.id.loading_cont));
		setContentView(inflateView);
	}

}
