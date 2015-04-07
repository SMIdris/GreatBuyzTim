package com.onmobile.utils;

public interface AppConstants
{
	// app launch type
	public static final String ACTION_SHOW_NOTIFICATION = "showNotification";

	public static final int RESULT_SHOW_REGISTRATION = 1000;
	public static final int RESULT_SET_ALERT_SCREEN = 1001;
	public static final int RESULT_DEAL_DETAIL_SCREEN = 1002;
	public static final int RESULT_CLICK_MYCOUPON = 2001;
	public static final int RESULT_CLICK_SEARCH = 2002;
	public static final int RESULT_SHOW_WEBVIEW = 2003;
	public static final int RESULT_EXIT_APP = 2004;

	public static final String CACHE_DIRECTORY = "MyImages";
	public static final String CACHE_DIRECTORY_ANALYTICS = "VASAnalytics";
	public static final int CACHE_DIRECTORY_TRIM_SIZE = 15; // in MB

	// misc
	public static final int CONNECTION_TIMEOUT_MILLIS = 11000;
	public static final String DEAL_SCREEN = "DealScreen";
	public static final String EMPTY_STRING = "";
	public static final String SPACE_STRING = " ";
	public static final String NULL_IN_STRING = "null";
	public static final String ELLIPSIS_STRING = "..";
	public static final String VERSION_DELIMITER = ":";
	public static final String COMMA = ",";
	public static final String htmlNewLineCharacter = "<br/>";
	public static final String ENCODING_UTF8 = "utf-8";
	public static final String ENCODING_BASE64 = "BASE64";
	public static final String HTMLHEADERCSS = "<html><head><style type=\"text/css\"> @font-face{font-family: \'myFont\'; src: url(\'file:///android_asset/fonts/ARLRDBD.TTF\'); } body {font-family: \'myFont\'; font-size:14px; color: #484747;}</style></head><body>";
	public static final String CONTACTDETAILHEADER1 = "<b>";
	public static final String CONTACTDETAILHEADER2 = "</b><br/>";
	public static final String OFFERHEADER1 = "<br/><br/><b>";
	public static final String OFFERHEADER2 = "</b><br/>";
	public static final String HTMLFOOTER = "<br/><br/><br/><br/><br/><br/>";
	public static final String URL_REDIRECTION_BROWSER_PARAM = "browser=external";
	public static final int DEALS_NEAR_ME_RADIUS = 50; // is in kilometer
	public static final int MDN_MIN_LENGTH = 6;
	public static final int FRACTION_DIGITS = 2;
	public static final int OTP_MIN_LENGTH = 4;
	public static final int MINI_INPUTLIMITFOREXPLORE = 1;
	public static final int CATEGORY_HEADER_LENGTH = 25;

	// For form validation
	public static final int MINNUMBERINPUTLIMIT = 10;
	public static final int MAXNUMBERINPUTLIMIT = 10;
	public static final int MAXINPUTLIMITFOREXPLORE = 25;
	public static final int MAXTEXTINPUTLIMIT = 60;

	public static final int OTP_EXPIRY = 2880; // is in minutes

	// shared prefs
	public static final String SHARE_DATA = "SharedPrefs";

	public interface SharedPrefKeys
	{
		//public static final String city = "city";
		//public static final String categories = "categories";
		//public static final String splitCharacter = ",";

		// version control
		//public static final String terms = "terms";
		public static final String help = "help";
		public static final String about = "about";
		public static final String faq = "faq";
		
		public static final String  SessionId= "SessionId";
		//public static final String  isClientUnsubScribe= "isClientUnsubScribe";
		public static String APP_VERSION_CODE = "APP_VERSION_CODE";
	}

	public interface NotificationType
	{
		public static final int TEXT = 0;
		public static final int URL = 1;
		public static final int DEAL = 2;
	}

	public interface URIParts
	{
		public static final String GET_QUERY_STRING_PARAM = "q";

		// Info controller
		public static final String GET_KEYWORDS = "/info/keywords.json?";
		public static final String GET_CITIES = "/info/cities.json";

		// Subscription controller
		public static final String GET_PURCHASE_HISTORY = "/subscriber/mydeals.json?";
		public static final String USER_INFO = "/subscriber/preference.json?";
		public static final String GET_INFO = "/subscriber/versioninfo?";
		public static final String REGISTER = "/otp.json?";
		public static final String SUBSCRIBE = "/subscription-v2/subscribe_alert.json?";
		public static final String UNSUBSCRIBE = "/subscription-v2/unsubscribe_alert.json?";

		// Deal controller
		public static final String GET_DEAL_BY_ID = "/deal/";
		public static final String GET_SURPRISE_DEAL = "/random_deal.json?";
		public static final String GET_PURCHASE_DEAL_BY_ID = "/purchasedealbyid/";
		public static final String BROWSE = "/browsedeals.json?";
		public static final String FLAGSHIP_DEALS = "/flagshipdeals.json?";
		public static final String DEALS_BY_CATEGORIES = "/deal_by_category.json?";
		public static final String DEALS_BY_LOCATION = "/dealsbylocation?";
		public static final String RECOMMEND = "/recommend.json?";
		public static final String SEARCH = "/search.json?";
		public static final String GET_MY_DEALS = "/mydealtim.json?";
		public static final String GET_EXCLUSIVE_DEALS = "/exclusivedeals.json?";
		public static final String GET_WELCOME_DEALS = "/welcomedeals.json?";

		// Analytics
		public static final String ANALYTICS_APP_LOG = "/analytics/applog.json";
	}

	public interface UserActivationStatus
	{
		public static final String PENDING = "PENDING";
		public static final String ACTIVE = "ACTIVE";
		public static final String TRIAL = "TRIAL";
		public static final String NOTFOUND = "NOTFOUND";
		public static final String DISCONTINUED = "DISCONTINUED";
		public static final String WAITING = "WAITING";
		public static final String DIRECT_ACTIVATION = "DIRECT_ACTIVATION";
	}

	public interface GCM
	{
		//public static final String GCM_PROJECT_ID = "507363539968"; //395165115367 //786921400297
		public static final String GCM_PROJECT_ID = "786921400297";
		public static final int GCM_REGISTRATION_FAILED = 0;
		public static final int GCM_RECEIVED = 1;
		public static final int GCM_UPDATE_REGISTRATION_ID = 2;
		public static final int GCM_UPDATE_UNREGISTRATION = 3;
	}

	public interface Flurry
	{
		public static final String API_KEY = "BFBTYXGR8CHXQFS7VJT5";
		public static final String GCM_PROJECT_ID = "GcmProjectId";
		public static final String GCM_REG_ERROR = "GcmRegistrationError";
		public static final String CLICK = "Click";
		public static final String VISIT = "Visit";
		public static final String CATEGORY = "Category";
		public static final String CategoryAdd = "CategoryAdd";
		public static final String CategoryRemoved = "CategoryRemoved";
		public static final String REGISTER = "Register";
		public static final String VALIDATEOTP = "ValidateOTP";
		public static final String RESENDOTP = "ResendOTP";
		public static final String DEALID = "DealID";
		public static final String MDN = "MSISDN";
		public static final String USERINPUT = "UserInput";
		public static final String YES = "Yes";
		public static final String NO = "No";
		public static final String STATUS = "Status";
		public static final String SUCCESS = "Success";
		public static final String PENDING = "Pending";
		public static final String Registration = "Registration";
		public static final String DealDetails = "DealDetails";
		public static final String DealsYouMayLike = "DealsYouMayLike";
		public static final String CategoryList = "CategoryList";
		public static final String ChangeReq = "ChangeReq";
		public static final String DealDetail = "DealDetail";
		public static final String GCMDealDetail = "GCMDealDetail";
		
		public static final String WelcomeDeal = "WelcomeDealScreen";
		public static final String ExploreDealResult = "ExploreDealResult";
		public static final String DealItem = "DealItem";
		public static final String Unsubscribe = "Unsubscribe";
		public static final String GCMCount = "GCMCount";
		public static final String NewCity = "NewCity";
		
		

		
	}

	public interface Constants
	{
		public static final String serverIP = "serverIP";
		public static final String otpExpiry = "otpExpiry";
		public static final String countryCode = "countryCode";
		public static final String getEmailIdFromUser = "getEmailIdFromUser";
		public static final String mobileNumberMinLength = "mobileNumberMinLength";
		public static final String mobileNumberMaxLength = "mobileNumberMaxLength";
		public static final String dealsNearMeRadius = "dealsNearMeRadius";
		public static final String keywordCompletionThreshold = "keywordCompletionThreshold";
		public static final String setAlertsKeywordMaxLength = "setAlertsKeywordMaxLength";
		public static final String useExpandedImageDealLayout = "useExpandedImageDealLayout";
		public static final String needSearchCriteriaValidation = "needSearchCriteriaValidation";
		public static final String gcmProjectId = "gcmProjectId";
		public static final String flurryAPIKey = "flurryAPIKey";
		public static final String otpMaxLength = "otpMaxLength";
		public static final String exploreMaxLimit = "exploreMaxLimit";
		public static final String categoryNameHeaderLength = "categoryNameHeaderLength";
		public static final String getMDNMethod = "getMDNMethod";
		public static final String getMDNUrl = "getMDNUrl";
		public static final String getMDNPostParams = "getMDNPostParams";
		public static final String getMDNHeaderParams = "getMDNHeaderParams";
		public static final String logFileUploadFrequencyInMinutes = "logFileUploadFrequencyInMinutes";
		public static final String logFileUploadMinSizeLimitInKB = "logFileUploadMinSizeLimitInKB";
		public static final String notificationFrequencyMinLimit = "notificationFrequencyMinLimit";
		public static final String notificationFrequencyMaxLimit = "notificationFrequencyMaxLimit";
		public static final String defaultAppNotificationLimit = "defaultAppNotificationLimit";
		public static final String serverTimeZone = "serverTimeZone";
	}

	public interface Messages
	{
		public static final String app_name = "app_name";
		public static final String ok = "ok";
		public static final String yes = "yes";
		public static final String no = "no";
		public static final String cancel = "cancel";
		public static final String upgrade = "upgrade";
		public static final String btnVisitText = "btnVisitText";
		public static final String titleInfo = "titleInfo";
		public static final String error = "error";
		public static final String btnNotifVisitText = "btnNotifVisitText";
		public static final String all = "all";

		public static final String menuAbout = "menuAbout";
		public static final String menuTerms = "menuTerms";
		public static final String menuFAQ = "menuFAQ";
		public static final String menuHelp = "menuHelp";
		public static final String menuSettings = "menuSettings";
		public static final String menuRefresh = "menuRefresh";

		public static final String msgStrSpecial = "timSpecial";
		public static final String dealsOfTheDay = "dealsOfTheDay";
		public static final String surpriseMe = "surpriseMe";
		public static final String dealsByCategories = "dealsByCategories";
		public static final String dealsYouMayLike = "dealsYouMayLike";
		public static final String dealsNearMe = "dealsNearMe";
		public static final String exploreDeals = "exploreDeals";
		public static final String myDeals = "myDeals";

		public static final String val = "val";
		public static final String value = "value";
		public static final String euro = "euro";
		public static final String discount = "discount";
		public static final String percent = "percent";
		public static final String pay = "pay";
		public static final String offerHeaderText = "offerHeaderText";

		public static final String unableToConnect = "unableToConnect";
		public static final String checkInternetConnection = "checkInternetConnection";
		public static final String loading = "loading";
		public static final String emptyDeal = "emptyDeal";
		public static final String networkProblemMessage = "networkProblemMessage";
		public static final String emptyViewBackButton = "emptyViewBackButton";
		public static final String NullFieldMessage = "NullFieldMessage";
		public static final String exitMessage = "exitMessage";
		public static final String inAppNotifyExitDialog = "inAppNotifyExitDialog";
		public static final String NoApplicationFound = "NoApplicationFound";

		public static final String termsTitle = "termsTitle";
		

		public static final String faqTitle = "faqTitle";

		public static final String btnSearchText = "btnSearchText";
		public static final String localityHintText = "localityHintText";
		public static final String keywordHintText = "keywordHintText";

		public static final String btnSurpriseMeText = "btnSurpriseMeText";
		public static final String txtSurpriseMeText = "txtSurpriseMeText";
		public static final String txtOR = "txtOR";

		public static final String issued = "issued";

		public static final String settingItemChangeLocation = "settingItemChangeLocation";
		public static final String settingItemCategoryPreferences = "settingItemCategoryPreferences";
		public static final String settingItemEmail = "settingItemEmail";
		public static final String settingItemUnsubscribe = "settingItemUnsubscribe";
		public static final String settingItemKeywords = "settingItemKeywords";
		public static final String titleChangeLocation = "titleChangeLocation";
		public static final String titleCategoryPreferences = "titleCategoryPreferences";
		public static final String titleUnsubscribeMessage = "titleUnsubscribeMessage";
		public static final String atleastOneCategoryMessage = "atleastOneCategoryMessage";
		public static final String keywordsTextHint = "keywordsTextHint";
		public static final String btnSetAlertsText = "btnSetAlertsText";
		public static final String keywordsSentMessage = "keywordsSentMessage";
		public static final String keywordsFailedMessage = "keywordsFailedMessage";

		public static final String enable = "enable";
		public static final String retry = "retry";
		public static final String gpsNeededMessage = "gpsNeededMessage";

		public static final String register = "register";
		public static final String validate = "validate";
		public static final String registrationFailMessage = "registrationFailMessage";
		public static final String otpExpiredMessage = "otpExpiredMessage";
		public static final String agree = "agree";
		public static final String enterMobileNumber = "enterMobileNumber";
		public static final String enterOTP = "enterOTP";
		public static final String otpResendText = "otpResendText";
		public static final String otpResendButtonText = "otpResendButtonText";
		public static final String registerMobileNumber = "registerMobileNumber";
		public static final String welcomeText = "welcomeText";
		public static final String welcomeText_DiscontinuedUser = "welcomeTextDiscontinuedUser";
		public static final String welcomeOfferText = "welcomeOfferText";
		public static final String welcomeDealContinueButtonText = "welcomeDealContinueButtonText";
		public static final String subscription_status_pending_message = "subscription_status_pending_message";
		public static final String subscription_failed_message = "subscription_failed_message";
		public static final String incorrectOTP = "incorrectOTP";
		public static final String freeDealMessage = "freeDealMessage";
		public static final String locationNotAvailables = "locationNotAvailables";
		public static final String zeroDiscountText = "zeroDiscountText";
		public static final String txtTnCAndInfo = "txtTnCAndInfo";
		public static final String registerMarketingTitle = "regMarketingTitle";
		public static final String registerMarketingText = "regMarketingInfo";
		public static final String registerMarketingInfo= "regMarketingCost";
		public static final String regAcceptMsg= "regAcceptMsg";
		public static final String postPinMsg= "postPinMsg";
		public static final String infoPrivacyTitle= "infoPrivacyTitle";
		public static final String infoCostiTitle= "infoCostiTitle";
		public static final String infoTermsTitle = "infoTermsTitle";
		public static final String regBonusImageUrl = "regBonusImageUrl";
	}

	public interface JSONKeys
	{
		public static final String MDN = "mdn";
		public static final String MSISDN = "msisdn";
		public static final String IMEI = "imei";
		public static final String IMSI = "imsi";
		public static final String CLIENT = "client";
		public static final String isDailyMsgEnabled = "isDailyMsgEnabled";
		public static final String RESPONSE = "response";
		public static final String CODE = "code";
		public static final String RESULT = "result";
		public static final String USER = "user";
		public static final String UPGRADE = "upgrade";
		public static final String CLIENT_UPGRADE_PROPERTIES = "clientUpgradeProperties";
		public static final String FORCE = "force";
		public static final String CITIES = "cities";
		public static final String LOCATIONS = "locations";
		public static final String LOCATION_MAPPING = "locationMapping";
		public static final String OM_LOCATION = "omLocation";
		public static final String DISTRICTS = "districts";
		public static final String CATEGORIES = "categories";
		public static final String KEYWORDS = "keywords";
		public static final String KEYWORD = "keyword";
		public static final String ENTRY = "entry";
		public static final String MESSAGES = "messages";
		public static final String CONSTANTS = "constants";
		public static final String WELCOMEDEAL = "welcome_deal";
		public static final String APP_CONFIG_MESSAGE = "appConfigMessage";
		public static final String APP_CONSTANT = "appConstant";
		public static final String OTP = "OTP";
		//public static final String DEAL_ID = "dealid";
		public static final String ALERT_DEAL_ID = "_id";
		public static final String ID = "id";
		public static final String MSG = "msg";
		public static final String MESSAGE = "message";
		public static final String SENDER_NAME = "senderName";
		public static final String REF_ID = "refId";
		public static final String PROMOTION_PRIORITY = "promotionPriority";
		public static final String NAME = "name";
		public static final String VALUE = "value";
		public static final String DESCRIPTION = "description";
		public static final String LONG_DESCRIPTION = "longDescription";
		public static final String IMAGE = "image";
		public static final String IMAGES = "images";
		public static final String IMAGEURL = "imageUrl";
		public static final String IMGURL = "imgurl";
		public static final String IMGURL_SMALL = "imgurlsmall";
		public static final String IMGURL_MEDIUM = "imgurlmedium";
		public static final String IMGURL_LARGE = "imgurllarge";
		public static final String COUPON_PRICE = "couponPrice";
		public static final String SORT = "sort";
		public static final String PRICE = "price";
		public static final String ORDER = "order";
		public static final String ASC = "asc";
		public static final String DISCOUNT = "discount";
		public static final String CONTACT = "contact";
		public static final String PHONE_LINES = "phoneLines";
		public static final String PHONE_NUMBER = "phoneNumber";
		public static final String AVAILABLE_FROM = "availableFrom";
		public static final String AVAILABLE_UPTO = "availableUpto";
		public static final String EMAIL = "email";
		public static final String EMAILID = "emailid";
		public static final String NOTIFICATION_ENABLED = "notificationEnabled";
		public static final String NOTIFICATION_FREQUENCY = "appNotificationLimit";
		public static final String PREFERENCE = "preference";
		public static final String URL = "url";
		public static final String DETAILS = "details";
		public static final String FIRST_LINE = "firstLine";
		public static final String SECOND_LINE = "secondLine";
		public static final String DISTRICT = "district";
		public static final String STATE = "state";
		public static final String COUNTRY = "country";
		public static final String PIN = "pin";
		public static final String LONGITUDE = "longitude";
		public static final String LONG = "long";
		public static final String LATITUDE = "latitude";
		public static final String LAT = "lat";
		public static final String RADIUS = "radius";
		public static final String LANGUAGE = "language";
		public static final String CATEGORY = "category";
		public static final String CATEGORIES_BROWSE = "categories";
		
		public static final String OM_CATEGORY = "omCategory";
		public static final String CATEGORY_MAPPING = "categoryMapping";
		public static final String CATEGORY_IMAGE_URL_SMALL = "categoryImageUrlSmall";
		public static final String CATEGORY_IMAGE_URL_MEDIUM = "categoryImageUrlMedium";
		public static final String CATEGORY_IMAGE_URL_BIG = "categoryImageUrlBig";
		public static final String TNC = "tnc";
		public static final String MAX = "max";
		public static final String MIN = "min";
		public static final String END = "end";
		public static final String ENDDATE = "endDate";
		public static final String OFFER = "offer";
		public static final String HOW_TO_REDEEM = "howToRedeem";
		public static final String MERCHANT = "merchant";
		public static final String OPERATORS = "operators";
		public static final String TAGS = "tags";
		public static final String EXCLUSIVE_DEALS = "exclusiveDeals";
		public static final String REVIEWS = "reviews";
		public static final String SOURCE = "source";
		public static final String RETRIEVED_ON = "retrievedOn";
		public static final String RETRIEVEDON = "retrievedon";
		public static final String LIMIT = "limit";
		public static final String SKIP = "skip";
		public static final String CHARGING_MODE = "chargingMode";
		public static final String SERVICE_KEY = "serviceKey";
		public static final String SERVICE_KEY_VALUE = "rs14";
		public static final String CHANNEL = "channel";
		public static final String GCM = "GCM";
		public static final String WAP = "WAP";
		public static final String REGISTRATION = "registration";
		public static final String REGISTRATION_ID = "registrationId";
		public static final String GOOGLE_REG_ID = "googleRegistrationId";
		public static final String GCM_ID = "gcmid";
		public static final String SEND_COUPON = "sendcoupon";
		public static final String COUPON_ID = "couponId";
		public static final String ISSUE_DATE = "issueDate";
		public static final String EXPIRY_DATE = "expiryDate";
		public static final String PURCHASE_TIMESTAMP = "purchaseTimeStamp";
		public static final String COUPON = "coupon";
		public static final String GIFT = "gift";
		public static final String DEAL_NAME = "dealName";
		public static final String DEAL_ID_PURCHASE = "dealId";
		public static final String ACTIVATE = "activate";
		public static final String DEAL = "deal";
		public static final String DEAL_DTO = "dealDto";
		public static final String VERSIONS = "versions";
		public static final String DATA_VERSION = "dataVersion";
		public static final String CURRENT_VERSION = "CURRENT_VERSION";
		public static final String PREFERRED = "preferred";
		public static final String DEFAULT = "default";
		//public static final String PREFERRED_LOCATION = "preferedLocation";
		public static final String DEFAULT_LOCATION = "defaultLocation";
		public static final String VISIT_URL = "redemptionUrl";
		public static final String SUBSCRIBE = "subscribe";
		public static final String SUBSCRIBED = "subscribed";
		public static final String INDEX = "index";
		public static final String START_HOME_SCREEN = "starthomescreen";
		public static final String SUCCESS = "SUCCESS";
		public static final String SUGGESTED = "suggested";

		// get MDN object
		public static final String GET_MDN = "getmdn";
		public static final String METHOD = "method";
		public static final String HEADER_PARAMS = "headerParams";
		public static final String POST_PARAMS = "postParam";
		public static final String MSISDN_TAG_START = "<msisdn>";
		public static final String MSISDN_TAG_END = "</msisdn>";

		// Search deals
		public static final String START = "start";
		public static final String QUERY = "query";
		public static final String LOCALITY = "locality";
		public static final String RESPONSE_HEADER = "responseHeader";
		public static final String STATUS = "status";
		public static final String NUM_FOUND = "numFound";
		public static final String DOCS = "docs";
		public static final String ROWS = "rows";

		// Server properties
		public static final String GIFT_A_FRIEND = "giftAFriend";
		public static final String GOOGLE_ANALYTICS = "googleAnalytics";
		public static final String BUY_CONFIRMATION = "buyConfirmation";
		public static final String EMAIL_CONFIGURATION = "emailConfiguration";
		public static final String EMAIL_SETTING_IN_DEAL_DETAIL = "emailSettingInDealDetails";
		public static final String GIFT_HISTORY = "giftHistory";
		public static final String DEALS_NEAR_ME = "dealsNearMe";

		// Notification
		public static final String TYPE = "type";
		public static final String TITLE = "title";
		public static final String DESC = "desc";
		public static final String TEXT = "text";
	}

	public interface Names
	{
	}

	public interface DialogConstants
	{
		public static final int ERROR_DIALOG = 100;
		public static final int MESSAGE_DIALOG = 101;
		public static final int LOADING_DIALOG = 102;
		public static final int EXIT_DIALOG = 103;
		public static final int REG_UNSUCCESSFUL_DIALOG = 104;
		public static final int FREE_DEAL_MESSAGE_DIALOG = 105;
		public static final int NETWORK_ERROR_DIALOG = 106;
		public static final int WAIT_FOR_REG_DIALOG = 107;
		public static final int UPGRADE_DIALOG = 108;
		public static final int FAILED_SEARCH_DIALOG = 109;
		public static final int REG_MESSAGE_DIALOG_WITH_EXIT = 110;
		public static final int IN_APP_EXIT_DIALOG = 111;
		public static final int MESSAGE_DIALOG_NODEAL = 112;

		// Settings
		public static final int CHANGE_LOCATION_DIALOG = 202;
		public static final int CHANGE_SETTINGS_LOCATION_DIALOG = 205;
		public static final int CATEGORY_PREF_DIALOG = 206;
		public static final int UNSUBSCRIBE_DIALOG = 207;
		public static final int UNSUBSCRIBE_PENDING_DIALOG = 208;
		public static final int EMAIL_DIALOG = 209;
		public static final int KEYWORDS_DIALOG = 210;
	}

	public interface FramentConstants
	{
		public static final int EXCLUSIVE_DEALS = 1;
		public static final int DEALS_OF_THE_DAY = 2;
		public static final int SURPRISE_ME = 3;
		public static final int CATEGORY_FRAGMENT = 4;
		public static final int DEALS_NEAR_ME = 5;	
		public static final int EXPLORE_DEALS = 6;
		public static final int MY_DEALS = 7;

		public static final int DEAL_BY_CATEGORY = 8;
		public static final int DEALS_YOU_MAY_LIKE = 9;
		//public static final int MY_DEALS = 10;
		public static final int DEAL_BY_ID = 100;
		public static final int FREE_DEAL = 101;
	}

	public interface SettingItems
	{
		public static final int TITLE = 0;
		public static final int LOCATION = 1;
		public static final int CATEGORIES = 2;
		public static final int UNSUBSCRIBE = 3;
		public static final int EMAIL = 4;
		public static final int KEYWORDS = 5;

		public static final int ABOUT = 6;
		public static final int TNC = 7;
		public static final int HELP = 8;
		public static final int FAQ = 9;
		public static final int REFRESH = 10;

		public static final int PERSONAL_PROFILE = 11;
		public static final int MY_COUPON = 12;
		public static final int SEARCH = 13;
		public static final int NOTIFICATIONS = 14;
		public static final int INFO = 15;
		public static final int VERSION = 16;
		
		public static final int ALL_TNC = 17;
		public static final int INFO_PRIVACY = 18;
		public static final int INFO_COSTI = 19;
	}
}
