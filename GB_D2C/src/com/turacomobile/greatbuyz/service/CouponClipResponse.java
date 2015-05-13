package com.turacomobile.greatbuyz.service;


import java.util.List;

import com.turacomobile.greatbuyz.data.CouponScreenDTO;


public class CouponClipResponse
{

	private int responceCode;
	private String responceMessage;
	private List<CouponScreenDTO> listData;

	public CouponClipResponse(int responceCode, String message, List<CouponScreenDTO> data)
	{
		this.responceCode = responceCode;
		this.responceMessage = message;
		this.listData = data;
	}

	public int getResponceCode()
	{
		return responceCode;
	}

	public void setResponceCode(int responceCode)
	{
		this.responceCode = responceCode;
	}

	public String getResponceMessage()
	{
		return responceMessage;
	}

	public void setResponceMessage(String responceMessage)
	{
		this.responceMessage = responceMessage;
	}

	public List<CouponScreenDTO> getListData()
	{
		return listData;
	}
	
	public void setListData(List<CouponScreenDTO> listData)
	{
		this.listData = listData;
	}
	


}
