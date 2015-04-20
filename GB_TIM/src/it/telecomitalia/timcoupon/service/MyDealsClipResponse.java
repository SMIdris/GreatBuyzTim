package it.telecomitalia.timcoupon.service;

import it.telecomitalia.timcoupon.data.Purchase;

import java.util.List;


public class MyDealsClipResponse
{

	private int responceCode;
	private String responceMessage;
	private List<Purchase> listData;

	public MyDealsClipResponse(int responceCode, String message, List<Purchase> data)
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

	public List<Purchase> getListData()
	{
		return listData;
	}

	public void setListData(List<Purchase> listData)
	{
		this.listData = listData;
	}

}
