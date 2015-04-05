package it.telecomitalia.timcoupon.service;

import it.telecomitalia.timcoupon.data.DealScreenDTO;

import java.util.List;


public class ClipResponse
{

	private int responceCode;
	private String responceMessage;
	private List<DealScreenDTO> listData;

	public ClipResponse(int responceCode, String message, List<DealScreenDTO> data)
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

	public List<DealScreenDTO> getListData()
	{
		return listData;
	}

	public void setListData(List<DealScreenDTO> listData)
	{
		this.listData = listData;
	}

}
