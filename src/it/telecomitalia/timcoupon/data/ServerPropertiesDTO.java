package it.telecomitalia.timcoupon.data;

public class ServerPropertiesDTO
{
	private boolean _giftAFriend;
	private boolean _googleAnalytics;
	private boolean _buyConfirmation;
	private boolean _emailConfiguration;
	private boolean _emailSettingInDealDetails;
	private boolean _giftHistory;
	private boolean _dealsNearMe;

	public ServerPropertiesDTO(boolean giftAFriend, boolean googleAnalytics, boolean buyConfirmation, boolean emailConfiguration,
			boolean emailSettingInDealDetails, boolean giftHistory, boolean dealsNearMe)
	{
		_giftAFriend = giftAFriend;
		_googleAnalytics = googleAnalytics;
		_buyConfirmation = buyConfirmation;
		_emailConfiguration = emailConfiguration;
		_emailSettingInDealDetails = emailSettingInDealDetails;
		_giftHistory = giftHistory;
		_dealsNearMe = dealsNearMe;
	}

	public final boolean isGiftAFriend()
	{
		return _giftAFriend;
	}

	public final boolean isGoogleAnalytics()
	{
		return _googleAnalytics;
	}

	public final boolean isBuyConfirmation()
	{
		return _buyConfirmation;
	}

	public final boolean isEmailConfiguration()
	{
		return _emailConfiguration;
	}

	public final boolean isEmailSettingInDealDetails()
	{
		return _emailSettingInDealDetails;
	}

	public final boolean isGiftHistory()
	{
		return _giftHistory;
	}

	public final boolean isDealsNearMe()
	{
		return _dealsNearMe;
	}
}
