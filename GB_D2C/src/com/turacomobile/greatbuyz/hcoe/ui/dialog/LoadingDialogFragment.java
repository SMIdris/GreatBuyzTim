package com.turacomobile.greatbuyz.hcoe.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.turacomobile.greatbuyz.R;


public class LoadingDialogFragment extends DialogFragment
{
	public static LoadingDialogFragment newInstance()
	{
		LoadingDialogFragment frag = new LoadingDialogFragment();
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		super.onCreateDialog(savedInstanceState);
		LoadingDialog loadingDialog = new LoadingDialog(getActivity(), R.style.AlertDialogCustom);
		loadingDialog.setCancelable(false);
		/*
		 * final ProgressDialog dialog = new ProgressDialog(getActivity(),
		 * style.AlertDialogCustom); dia//Log.setIndeterminate(true);
		 * dia//Log.setCancelable(false); LayoutInflater inflater =
		 * (LayoutInflater)
		 * getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE); View
		 * view = inflater.inflate(R.layout.loading_dialog, null);
		 * dia//Log.setContentView(view);
		 * 
		 * // Disable the back button OnKeyListener keyListener = new
		 * OnKeyListener() { public boolean onKey(DialogInterface dialog, int
		 * keyCode, KeyEvent event) { return keyCode == KeyEvent.KEYCODE_BACK; }
		 * }; dia//Log.setOnKeyListener(keyListener);
		 */
		return loadingDialog;
	}
}