package com.turacomobile.greatbuyz.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.turacomobile.greatbuyz.GreatBuyzApplication;
import com.turacomobile.greatbuyz.R;
import com.turacomobile.greatbuyz.service.DataController;
import com.turacomobile.greatbuyz.utils.AppConstants;
import com.turacomobile.greatbuyz.utils.Utils;

public final class ExploreDealsFragment extends Fragment
{

	EditText edtLocation;
	EditText edtSearchItem;
	Spinner sprCategory;
	ExplorePageFragmentListener fm;
	static Activity activity;
	boolean validate = false;
	DataController _data = GreatBuyzApplication.getDataController();

	public static ExploreDealsFragment newInstance(String content, Activity screen, ExplorePageFragmentListener f)
	{
		ExploreDealsFragment fragment = new ExploreDealsFragment();
		fragment.fm = f;
		activity = screen;
		GreatBuyzApplication.isCouponClicked = false;
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		activity = getActivity();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		if ((savedInstanceState != null))
		{

		}
	}

	@Override
	public void onStart()
 {
		super.onStart();
		// FlurryAgent.onPageView();
		// GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit("ExploreDeals");
		// Kiran // we are taking page name from message table for language
		// support //
		String name = Utils.getMessageString(AppConstants.Messages.exploreDeals, R.string.exploreDeals);
		GreatBuyzApplication.getApplication().getAnalyticsAgent().onPageVisit(name);
	}

	public void hideKeyboard()
	{
		if (activity != null)
		{
			// InputMethodManager imm = (InputMethodManager)
			// activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edtLocation.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(edtSearchItem.getWindowToken(), 0);
		}
	}

	public void hideErrorPopUp()
	{
		if (activity != null)
		{
			edtLocation.setError(null);
			edtSearchItem.setError(null);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View layout = null;
		layout = inflater.inflate(R.layout.exploredeal, null);
		List<String> categories;
		sprCategory = (Spinner) layout.findViewById(R.id.sprExploreCategories);

		categories = GreatBuyzApplication.getDataController().getCategoriesList();
		// activity = getActivity();
		// CityAdapter mySpinnerArrayAdapter = new CityAdapter(activity,
		// R.layout.spinneritem, R.id.spinnerTarget, categories, true);
		CityAdapter mySpinnerArrayAdapter = new CityAdapter(getActivity(), R.layout.spinneritem, R.id.spinnerTarget, categories, true);

		mySpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sprCategory.setAdapter(mySpinnerArrayAdapter);

		edtLocation = (EditText) layout.findViewById(R.id.edtLocation);
		edtLocation.setHint(Utils.getMessageString(AppConstants.Messages.localityHintText, R.string.localityHintText));
		edtLocation.setOnFocusChangeListener(new OnFocusChangeListener()
		{

			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				edtLocation.setHint("");
				if (edtSearchItem.getText().toString().equalsIgnoreCase(AppConstants.EMPTY_STRING))
					edtSearchItem.setHint(Utils.getMessageString(AppConstants.Messages.keywordHintText, R.string.keywordHintText));
			}
		});
		edtLocation.setTypeface(GreatBuyzApplication.getApplication().getFont());
		String strLength = _data.getConstant(AppConstants.Constants.exploreMaxLimit);
		if (!Utils.isNothing(strLength))
		{
			try
			{
				int length = Integer.parseInt(strLength);
				edtLocation.setFilters(new InputFilter[] { new InputFilter.LengthFilter(length) });
			}
			catch (Exception e)
			{
			}
		}
		edtLocation.setOnKeyListener(new OnKeyListener()
		{

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (event.getAction() == KeyEvent.ACTION_DOWN)
				{
					// check if the right key was pressed
					if (keyCode == KeyEvent.KEYCODE_BACK)
					{
						edtLocation.clearFocus();

						return true;
					}
				}
				return false;
			}
		});

		// edtLocation.setFilters(new InputFilter[] { new
		// InputFilter.LengthFilter(AppConstants.MAXINPUTLIMITFOREXPLORE) });

		edtSearchItem = (EditText) layout.findViewById(R.id.edtItemName);
		edtSearchItem.setHint(Utils.getMessageString(AppConstants.Messages.keywordHintText, R.string.keywordHintText));

		edtSearchItem.setOnFocusChangeListener(new OnFocusChangeListener()
		{

			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				edtSearchItem.setHint("");
				if (edtLocation.getText().toString().equalsIgnoreCase(AppConstants.EMPTY_STRING))
				{
					edtLocation.setHint(Utils.getMessageString(AppConstants.Messages.localityHintText, R.string.localityHintText));
				}
			}
		});
		edtSearchItem.setTypeface(GreatBuyzApplication.getApplication().getFont());

		if (!Utils.isNothing(strLength))
		{
			try
			{
				int length = Integer.parseInt(strLength);
				edtSearchItem.setFilters(new InputFilter[] { new InputFilter.LengthFilter(length) });
			}
			catch (Exception e)
			{
			}
		}

		edtSearchItem.setOnKeyListener(new OnKeyListener()
		{

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (event.getAction() == KeyEvent.ACTION_DOWN)
				{
					// check if the right key was pressed
					if (keyCode == KeyEvent.KEYCODE_BACK)
					{
						edtSearchItem.clearFocus();

						return true;
					}
				}
				return false;
			}
		});

		// edtSearchItem.setFilters(new InputFilter[] { new
		// InputFilter.LengthFilter(AppConstants.MAXINPUTLIMITFOREXPLORE) });

		String strValidate = GreatBuyzApplication.getDataController().getConstant(AppConstants.Constants.needSearchCriteriaValidation);
		if (!Utils.isNothing(strValidate))
		{
			try
			{
				validate = Boolean.parseBoolean(strValidate);
			}
			catch (Exception e)
			{
			}
		}

		Button btnSend = (Button) layout.findViewById(R.id.btnExploreSend);
		btnSend.setTypeface(GreatBuyzApplication.getApplication().getFont());
		Utils.setMessageToButton(btnSend, AppConstants.Messages.btnSearchText);
		btnSend.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				hideKeyboard();

				String err = Utils.getMessageString(AppConstants.Messages.NullFieldMessage, R.string.NullFieldMessage);

				if (validate && Utils.isNothing(edtLocation.getText().toString())
						&& edtLocation.getText().toString().length() < AppConstants.MINI_INPUTLIMITFOREXPLORE)
				{
					Utils.setError(edtLocation, err);
					return;
				}

				if (validate && Utils.isNothing(edtSearchItem.getText().toString())
						&& edtSearchItem.getText().toString().length() < AppConstants.MINI_INPUTLIMITFOREXPLORE)
				{
					Utils.setError(edtSearchItem, err);
					return;
				}

				String selectedCategory = sprCategory.getSelectedItem().toString();
				String strAll = Utils.getMessageString(AppConstants.Messages.all, R.string.all);
				if (selectedCategory.equalsIgnoreCase(strAll))
					selectedCategory = AppConstants.EMPTY_STRING;

				String locality = edtLocation.getText().toString();
				String key = edtSearchItem.getText().toString();

				// IP change screen
				if (locality.equals("ABC@123") && key.equals("ABC@123"))
				{
					Intent changeIPIntent = new Intent(getActivity(), IPChangeActivity.class);
					startActivityForResult(changeIPIntent, AppConstants.RESULT_EXIT_APP);
					return;
				}
				fm.onSwitchToNextFragment(selectedCategory, GreatBuyzApplication.getDataController().getUserCity(), edtLocation.getText()
						.toString(), edtSearchItem.getText().toString());
			}
		});

		edtLocation.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {
	            String locationEntered = edtLocation.getText().toString().trim();
	        	if(!locationEntered.equalsIgnoreCase("") && locationEntered.length()>0){
	        		edtLocation.setError(null);
	            }
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
	    }); 
		edtSearchItem.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {
	            String locationEntered = edtSearchItem.getText().toString().trim();
	        	if(!locationEntered.equalsIgnoreCase("") && locationEntered.length()>0){
	        		edtSearchItem.setError(null);
	            }
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
	    }); 
		
		return layout;
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	public void refreshFragment()
	{
		edtLocation.setText(AppConstants.EMPTY_STRING);
		edtSearchItem.setText(AppConstants.EMPTY_STRING);
		sprCategory.setSelection(0);
	}

}
