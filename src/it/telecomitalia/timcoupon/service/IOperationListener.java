package it.telecomitalia.timcoupon.service;

public interface IOperationListener
{
	public void onOperationCompleted(boolean p_OperationComplitionStatus, String p_MessageFromServer);
}
