package eu.rkosir.feecollector;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AppConfig {
	public static final String URL_LOGIN = "https://rkosir.eu/FeeCollector/usersApi/login";
	public static final String URL_REGISTER = "https://rkosir.eu/FeeCollector/usersApi/add";
	public static final String URL_EXISTS = "https://rkosir.eu/FeeCollector/usersApi/exists";
	public static final String URL_CHANGE_PASSWORD = "https://rkosir.eu/FeeCollector/usersApi/changePassword";
	public static final String URL_CHANGE_PASSWORD_FACEBOOK = "https://rkosir.eu/FeeCollector/usersApi/changePasswordFacebook";
	public static final String URL_CHANGE_DETAILS = "https://rkosir.eu/FeeCollector/usersApi/changeUserDetails";
	public static final String URL_SAVE_TEAM = "https://rkosir.eu/FeeCollector/teamsApi/add";
	public static final String URL_JOIN_TEAM = "https://rkosir.eu/FeeCollector/teamMembersApi/update";
	public static final String URL_ADD_FEE = "https://rkosir.eu/FeeCollector/FeesApi/add";
	public static final String URL_GET_TEAMS = "https://rkosir.eu/FeeCollector/teamsApi/getTeamsOfUser?email=%1$s";
	public static final String URL_GET_TEAM_MEMEBERS_AND_FEES = "https://rkosir.eu/FeeCollector/teamsApi/getUsersInTeam?connection_number=%1$s";
	public static final String URL_DELETE_TEAM = "https://rkosir.eu/FeeCollector/teamsApi/deleteTeam";
	public static final String URL_ADD_MEMBER_TO_TEAM = "https://rkosir.eu/FeeCollector/teamMembersApi/add";
	public static final String URL_GET_MEMBERS = "https://rkosir.eu/FeeCollector/teamMembersApi/getUsersInTeam?connection_number=%1$s&email=%2$s";
	public static final String URL_GET_All_MEMBERS = "https://rkosir.eu/FeeCollector/teamMembersApi/getAllUsers?connection_number=%1$s";
	public static final String URL_GET_EVENTS = "https://rkosir.eu/FeeCollector/eventsApi/get?connection_number=%1$s";
	public static final String URL_GET_NEXT_EVENT = "https://rkosir.eu/FeeCollector/eventsApi/getNextEvent?connection_number=%1$s";
	public static final String URL_GET_LOCATIONS = "https://rkosir.eu/FeeCollector/placesApi/getPlaces?connection_number=%1$s";
	public static final String URL_GET_PLACE_ID = "https://rkosir.eu/FeeCollector/placesApi/getPlaceById?id=%1$s";
	public static final String ULR_SAVE_IAMGE = "https://rkosir.eu/FeeCollector/usersApi/insertImage";
	public static final String URL_SEND_MESSAGE_NOTIFICATION = "https://rkosir.eu/FeeCollector/notificationApi/sendMessage";
	public static final String URL_SAVE_FEE_TO_USER = "https://rkosir.eu/FeeCollector/usersFeesApi/addFee";
	public static final String URL_SAVE_EVENT = "https://rkosir.eu/FeeCollector/eventsApi/add";
	public static final String URL_GET_FEES_OF_USER = "https://rkosir.eu/FeeCollector/FeesApi/getFeeOfUserByEmail?connection_number=%1$s&email=%2$s";
	public static final String URL_UPDATE_FEES_OF_USER = "https://rkosir.eu/FeeCollector/FeesApi/updateFeeOfUserByEmail";
	public static final String URL_UPDATE_USER = "https://rkosir.eu/FeeCollector/usersApi/update";
	public static final String URL_GET_TOP_3_FINED_USERS = "https://rkosir.eu/FeeCollector/FeesApi/getTop3FinedUsers?connection_number=%1$s";
	public static final String URL_PLACES_ADD ="https://rkosir.eu/FeeCollector/placesApi/add";
	public static final String URL_SUMMARY = "https://rkosir.eu/FeeCollector/FeesApi/getSummaryFee?connection_number=%1$s";

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
	public static SimpleDateFormat dateOfFeeConverter = new SimpleDateFormat("yyyy-MM-dd");

	public static final String POST = "POST";
	public static final String GET = "GET";
	public static final String PUT = "PUT";
}
