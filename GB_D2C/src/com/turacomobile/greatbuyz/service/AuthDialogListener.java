package com.turacomobile.greatbuyz.service;

import android.content.Context;
import android.widget.Toast;

import com.acl.paychamp.android.Acknowledgement;
import com.acl.paychamp.android.CCBError;
import com.acl.paychamp.android.DialogError;
import com.acl.paychamp.android.Paychamp.DialogListener;
import com.acl.paychamp.util.Receipt;

public class AuthDialogListener implements DialogListener
{
	private Context  context;
	public final int PAYMENT_STATUS_SIMPLE_DIALOG = 111;
	
	public AuthDialogListener(Context context)
	{
		this.context = context;
	}
	
	public void onComplete(Receipt receipt)
	{
		// Fetch values from receipt
		Toast.makeText(context, "Transaction ID: " + receipt.getTransactionId() + "\nStatus=" + receipt.getStatus(), Toast.LENGTH_LONG).show();
		Acknowledgement auth = new Acknowledgement(receipt.getTransactionId());
		auth.execute("");
		// PaymentActivity.dialogConfirm(context);
	}
	
	@Override
	public void onCCBError(CCBError e)
	{
		Toast.makeText(context, "Payment Error ! " + e.getMessage(), Toast.LENGTH_LONG).show();
		e.printStackTrace();
	}
	
	@Override
	public void onError(DialogError e)
	{
		Toast.makeText(context, "Payment error !", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onCancel()
	{
		Toast.makeText(context, "Payment cancelled !", Toast.LENGTH_LONG).show();
		// PaymentActivity.dialogConfirm(context);
	}
	
}
