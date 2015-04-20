package it.telecomitalia.timcoupon.service;

import it.telecomitalia.timcoupon.data.DealScreenDTO;

import java.util.List;


public interface DealIOperationListener
{
	public void onOperationCompleted(boolean p_OperationComplitionStatus, String p_MessageFromServer, List<DealScreenDTO> dtos);
}
