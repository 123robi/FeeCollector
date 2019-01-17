package eu.rkosir.feecollector;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AppConfig {
	public static final String URL_LOGIN = "http://rkosir.eu/FeeCollector/usersApi/login";
	public static final String URL_REGISTER = "http://rkosir.eu/FeeCollector/usersApi/add";
	public static final String URL_EXISTS = "http://rkosir.eu/FeeCollector/usersApi/exists";
	public static final String URL_CHANGE_PASSWORD = "http://rkosir.eu/FeeCollector/usersApi/changePassword";
	public static final String URL_CHANGE_PASSWORD_FACEBOOK = "http://rkosir.eu/FeeCollector/usersApi/changePasswordFacebook";
	public static final String URL_CHANGE_DETAILS = "http://rkosir.eu/FeeCollector/usersApi/changeUserDetails";
	public static final String URL_SAVE_TEAM = "http://rkosir.eu/FeeCollector/teamsApi/add";
	public static final String URL_JOIN_TEAM = "http://rkosir.eu/FeeCollector/teamMembersApi/update";
	public static final String URL_ADD_FEE = "http://rkosir.eu/FeeCollector/FeesApi/add";
	public static final String URL_GET_TEAMS = "http://rkosir.eu/FeeCollector/teamsApi/getTeamsOfUser?email=%1$s";
	public static final String URL_GET_TEAM_MEMEBERS_AND_FEES = "http://rkosir.eu/FeeCollector/teamsApi/getUsersInTeam?connection_number=%1$s";
	public static final String URL_DELETE_TEAM = "http://rkosir.eu/FeeCollector/teamsApi/deleteTeam";
	public static final String URL_ADD_MEMBER_TO_TEAM = "http://rkosir.eu/FeeCollector/teamMembersApi/add";
	public static final String URL_GET_MEMBERS = "http://rkosir.eu/FeeCollector/teamMembersApi/getUsersInTeam?connection_number=%1$s&email=%2$s";
	public static final String URL_GET_All_MEMBERS = "http://rkosir.eu/FeeCollector/teamMembersApi/getAllUsers?connection_number=%1$s";
	public static final String URL_GET_EVENTS = "http://rkosir.eu/FeeCollector/eventsApi/get?connection_number=%1$s";
	public static final String URL_GET_NEXT_EVENT = "http://rkosir.eu/FeeCollector/eventsApi/getNextEvent?connection_number=%1$s";
	public static final String URL_GET_LOCATIONS = "http://rkosir.eu/FeeCollector/placesApi/getPlaces?connection_number=%1$s";
	public static final String URL_GET_PLACE_ID = "http://rkosir.eu/FeeCollector/placesApi/getPlaceById?id=%1$s";
	public static final String ULR_SAVE_IAMGE = "http://rkosir.eu/FeeCollector/usersApi/insertImage";
	public static final String URL_SEND_MESSAGE_NOTIFICATION = "http://rkosir.eu/FeeCollector/notificationApi/sendMessage";
	public static final String URL_SAVE_FEE_TO_USER = "http://rkosir.eu/FeeCollector/usersFeesApi/addFee";
	public static final String URL_SAVE_EVENT = "http://rkosir.eu/FeeCollector/eventsApi/add";
	public static final String URL_GET_FEES_OF_USER = "http://rkosir.eu/FeeCollector/FeesApi/getFeeOfUserByEmail?connection_number=%1$s&email=%2$s";
	public static final String URL_UPDATE_FEES_OF_USER = "http://rkosir.eu/FeeCollector/FeesApi/updateFeeOfUserByEmail";
	public static final String URL_UPDATE_USER = "http://rkosir.eu/FeeCollector/usersApi/update";
	public static final String URL_GET_TOP_3_FINED_USERS = "http://rkosir.eu/FeeCollector/FeesApi/getTop3FinedUsers?connection_number=%1$s";
	public static final String URL_PLACES_ADD ="http://rkosir.eu/FeeCollector/placesApi/add";

	public static final String ALPHA_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";
	public static final String NUMERIC = "0123456789";
	public static final String SPECIAL_CHARS = "!@#$%^&*_=+-/";

	public static final int PASSWORD_LENGTH = 5;

	public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat parse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public static SimpleDateFormat parseDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat dayFormat = new SimpleDateFormat("E", Locale.getDefault());
	public static SimpleDateFormat notificationEventFormat = new SimpleDateFormat("dd.MM.YYYY HH:mm");
	public static SimpleDateFormat membersFeeFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
	public static SimpleDateFormat nextEventFormat = new SimpleDateFormat("d", Locale.getDefault());
	public static SimpleDateFormat nextEventTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
	public static SimpleDateFormat parseIcal = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");

	//HTTP METHODS
	public static final String POST = "POST";
	public static final String GET = "GET";
	public static final String PUT = "PUT";
}
