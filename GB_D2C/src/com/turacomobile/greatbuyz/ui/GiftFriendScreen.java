package com.turacomobile.greatbuyz.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.turacomobile.greatbuyz.GreatBuyzApplication;
import com.turacomobile.greatbuyz.R;
import com.turacomobile.greatbuyz.data.TContactItem;
import com.turacomobile.greatbuyz.hcoe.ui.dialog.GenericDialog;
import com.turacomobile.greatbuyz.service.DataController;
import com.turacomobile.greatbuyz.utils.AppConstants;
import com.turacomobile.greatbuyz.utils.ContactHandler;
import com.turacomobile.greatbuyz.utils.Utils;

public class GiftFriendScreen extends Activity implements OnClickListener
{
	public static final int REQUEST_CODE_PICK_CONTACT = 101;
	Button btnSend;

	EditText edtMobileNumber;
	EditText edtFriendName;
	EditText edtYourName;
	EditText edtMessage;
	
	ImageView btnimgBackArrow;

	Button sprCity;
	Button btnAddContact;
	private String contactNumber = "";
	 String dealId;
	
	DataController _data = GreatBuyzApplication.getDataController();
	public final int BUY_CONFIRMATION_DIALOG = 210;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.giftafriend);
		// sprCity = (Button) findViewById(R.id.sprCitySelector);
		// sprCity.setVisibility(View.GONE);

		dealId = getIntent().getStringExtra("dealId");

		Typeface font = GreatBuyzApplication.getApplication().getFont();
		
		btnimgBackArrow = (ImageView)findViewById(R.id.imgBackArrow);
		btnimgBackArrow.setOnClickListener(this);

		TextView titleView = (TextView) findViewById(R.id.giftafriendtitle);
		titleView.setTypeface(font);

		TextView titleView1 = (TextView) findViewById(R.id.title1);
		titleView1.setTypeface(font);
		TextView titleView2 = (TextView) findViewById(R.id.title2);
		titleView2.setTypeface(font);
		TextView titleView3 = (TextView) findViewById(R.id.title3);
		titleView3.setTypeface(font);
		TextView titleView4 = (TextView) findViewById(R.id.title4);
		titleView4.setTypeface(font);

		edtMobileNumber = (EditText) findViewById(R.id.edtMobileNumber);
		edtMobileNumber.setTypeface(font);
		edtMobileNumber.setFilters(new InputFilter[] { new InputFilter.LengthFilter(AppConstants.MAXNUMBERINPUTLIMIT) });

		edtFriendName = (EditText) findViewById(R.id.edtYourFriendName);
		edtFriendName.setTypeface(font);
		edtFriendName.setFilters(new InputFilter[] { new InputFilter.LengthFilter(AppConstants.MAXINPUTLIMITFOREXPLORE) });

		edtYourName = (EditText) findViewById(R.id.edtYourName);
		edtYourName.setTypeface(font);
		edtYourName.setFilters(new InputFilter[] { new InputFilter.LengthFilter(AppConstants.MAXINPUTLIMITFOREXPLORE) });

		edtMessage = (EditText) findViewById(R.id.edtMessage);
		edtMessage.setTypeface(font);
		edtMessage.setFilters(new InputFilter[] { new InputFilter.LengthFilter(AppConstants.MAXTEXTINPUTLIMIT) });

		CheckBox chkAgreeTerms = (CheckBox) findViewById(R.id.checkAgreeToTerms);
		Button btnAgreeToTerms = (Button) findViewById(R.id.btnViewAgreement);

		btnAddContact = (Button) findViewById(R.id.add_contact);
		btnAddContact.setOnClickListener(this);

		btnSend = (Button) findViewById(R.id.btnSendGift);
		btnSend.setEnabled(true);
		btnSend.setTypeface(font);
		btnSend.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (edtMobileNumber.getText().toString() == null || edtFriendName.getText().toString() == null
						|| edtMessage.getText().toString() == null || edtYourName.getText().toString() == null
						|| edtMobileNumber.getText().toString() == null || edtMobileNumber.getText().toString().length() == 0
						|| edtFriendName.getText().toString().length() == 0 || edtMessage.getText().toString().length() == 0
						|| edtYourName.getText().toString().length() == 0 || edtMobileNumber.getText().toString().length() == 0)
				{
					Bundle b = new Bundle();
					b.putString("message", getResources().getString(R.string.NullFieldMessage));
					showDialog(MESSAGE_DIALOG, b);
				}
				else if (edtMobileNumber.getText().toString() != null
						&& edtMobileNumber.getText().toString().length() < AppConstants.MINNUMBERINPUTLIMIT)
				{
					Bundle b = new Bundle();
					b.putString("message", getResources().getString(R.string.InvalidMobMessage));
					showDialog(MESSAGE_DIALOG, b);
				}
				else
				{
					String isBuyConfirmation = "true";//_data.getConstant(AppConstants.Constants.isBuyConfirmation);
					
					if(!TextUtils.isEmpty(isBuyConfirmation) && Boolean.parseBoolean(isBuyConfirmation))
					{
						Bundle b = new Bundle();
						b.putString("message", getResources().getString(R.string.BuyConfirmationMessage));
						b.putString(AppConstants.JSONKeys.DEAL_ID, dealId);
						b.putString(AppConstants.JSONKeys.NAME, "DealDetail");
						showDialog(BUY_CONFIRMATION_DIALOG, b);
					}
					else
					{
						GiftFriendScreen.this.finish();
						GreatBuyzApplication.getServiceDelegate().giftDeal(GiftFriendScreen.this, dealId,_data.getMDN(), edtFriendName.getText().toString(),
								edtMobileNumber.getText().toString(), edtMessage.getText().toString(), edtYourName.getText().toString());
					}
					
				}
			}
		});

		chkAgreeTerms.setTypeface(GreatBuyzApplication.getApplication().getFont());
		chkAgreeTerms.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
	//			btnSend.setEnabled(isChecked);
			}
		});

		btnAgreeToTerms.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String text = Utils.readFromFile(AppConstants.SharedPrefKeys.terms);
				Intent helpIntent = new Intent(GiftFriendScreen.this, HelpActivity.class);
				helpIntent.putExtra(AppConstants.SharedPrefKeys.help, text);
				helpIntent.putExtra(AppConstants.UserMessages.TITLE_INFO, AppConstants.SharedPrefKeys.termsTitle);
				startActivity(helpIntent);
			}
		});

		Button btnCancel = (Button) findViewById(R.id.btnCancelGift);
		btnCancel.setTypeface(font);
		btnCancel.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs)
	{
		return super.onCreateView(name, context, attrs);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		finish();
	}

	final int MESSAGE_DIALOG = 101;
	final int MESSAGE_DIALOG_FINISH = 102;

	@Override
	protected Dialog onCreateDialog(int id, Bundle b)
	{
		switch (id)
		{
			case MESSAGE_DIALOG:
				{
					String message = b.getString("message");
					final GenericDialog dialog = new GenericDialog(GiftFriendScreen.this, R.layout.status_message,
							R.style.AlertDialogCustom);
					dialog.setCancelable(false);
					Button btn = (Button) dialog.findViewById(R.id.status_btn);
					btn.setText(getString(R.string.ok));
					btn.setOnClickListener(new OnClickListener()
					{

						@Override
						public void onClick(View v)
						{
							try
							{
								removeDialog(MESSAGE_DIALOG);
								// finish();
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					});
					TextView msgText = (TextView) dialog.findViewById(R.id.msg);
					msgText.setText(message);
					msgText.setTypeface(GreatBuyzApplication.getApplication().getFont());
					TextView titleText = (TextView) dialog.findViewById(R.id.title);
					titleText.setText(AppConstants.UserMessages.TITLE_INFO);
					titleText.setTypeface(GreatBuyzApplication.getApplication().getFont());
					return dialog;
				}
			case MESSAGE_DIALOG_FINISH:
				{
					String message = b.getString("message");
					final GenericDialog dialog = new GenericDialog(GiftFriendScreen.this, R.layout.status_message,
							R.style.AlertDialogCustom);
					dialog.setCancelable(false);
					Button btn = (Button) dialog.findViewById(R.id.status_btn);
					btn.setText(getString(R.string.ok));
					btn.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							try
							{
								removeDialog(MESSAGE_DIALOG);
								finish();
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					});
					TextView msgText = (TextView) dialog.findViewById(R.id.msg);
					msgText.setText(message);
					msgText.setTypeface(GreatBuyzApplication.getApplication().getFont());
					TextView titleText = (TextView) dialog.findViewById(R.id.title);
					titleText.setText(AppConstants.UserMessages.TITLE_INFO);
					titleText.setTypeface(GreatBuyzApplication.getApplication().getFont());
					return dialog;
				}
			case BUY_CONFIRMATION_DIALOG:
			{
				String message1 = b.getString("message");

				final String did = b.getString(AppConstants.JSONKeys.DEAL_ID);
				final String activityName = b.getString(AppConstants.JSONKeys.NAME);

				final GenericDialog dialog1 = new GenericDialog(this, R.layout.exit_message, R.style.AlertDialogCustom);
				dialog1.setCancelable(false);

				Button btn1 = (Button) dialog1.findViewById(R.id.yes_btn);
				btn1.setText(getString(R.string.yes));
				btn1.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							removeDialog(BUY_CONFIRMATION_DIALOG);
							
							GiftFriendScreen.this.finish();
							GreatBuyzApplication.getServiceDelegate().giftDeal(GiftFriendScreen.this, dealId, _data.getMDN(),edtFriendName.getText().toString(),
									edtMobileNumber.getText().toString(), edtMessage.getText().toString(), edtYourName.getText().toString());
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
				Button noBtn = (Button) dialog1.findViewById(R.id.no_btn);
				noBtn.setText(getString(R.string.no));
				noBtn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						try
						{
							removeDialog(BUY_CONFIRMATION_DIALOG);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});

				TextView msgText1 = (TextView) dialog1.findViewById(R.id.msg);
				msgText1.setText(message1);
				msgText1.setTypeface(GreatBuyzApplication.getApplication().getFont());
				TextView titleText11 = (TextView) dialog1.findViewById(R.id.title);
				titleText11.setText(AppConstants.EMPTY_STRING);
				titleText11.setTypeface(GreatBuyzApplication.getApplication().getFont());
				return dialog1;
			}
		}
		return super.onCreateDialog(id);
	}

	@Override
	public void onClick(View v)
	{
		if (v == btnAddContact)
		{
			startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACT);
		}
		else if(v == btnimgBackArrow)
		{
			finish();
		}
		

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_PICK_CONTACT)
		{  
			contactNumber = "";
			ArrayList<TContactItem> contactlist ;
			ContactHandler mContact = new ContactHandler(GiftFriendScreen.this);
			contactlist =	mContact.getSelectedContactArray(data);
			
			if (contactlist != null && contactlist.size() > 0)
			{
				contactNumber = contactlist.get(0).getContact();
			}
			edtMobileNumber.setText(contactNumber);
		}
	}

	
}
