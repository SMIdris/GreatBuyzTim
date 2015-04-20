package com.turacomobile.greatbuyz.service;


import java.util.List;

import com.turacomobile.greatbuyz.data.DealScreenDTO;


public interface DealIOperationListener
{
	public void onOperationCompleted(boolean p_OperationComplitionStatus, String p_MessageFromServer, List<DealScreenDTO> dtos);
}
