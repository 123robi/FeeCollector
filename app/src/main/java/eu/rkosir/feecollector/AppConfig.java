package eu.rkosir.feecollector;

public class AppConfig {
	public static final String URL_LOGIN = "http://rkosir.eu/usersApi/login";
	public static final String URL_REGISTER = "http://rkosir.eu/usersApi/add";
	public static final String URL_CHANGE_PASSWORD = "http://rkosir.eu/usersApi/changePassword";
	public static final String URL_CHANGE_PASSWORD_FACEBOOK = "http://rkosir.eu/usersApi/changePasswordFacebook";;
	public static final String URL_SAVE_TEAM = "http://rkosir.eu/teamsApi/add";
	public static final String URL_JOIN_TEAM = "http://rkosir.eu/teamsApi/join";
	public static final String URL_ADD_FEE = "http://rkosir.eu/FeesApi/add";
	public static final String URL_GET_TEAMS = "http://rkosir.eu/teamsApi/get?email=%1$s";
	public static final String URL_GET_TEAM_MEMEBERS = "http://rkosir.eu/teamsApi/getUsersInTeam?connection_number=%1$s";


	public static final String ALPHA_CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";
	public static final String NUMERIC = "0123456789";
	public static final String SPECIAL_CHARS = "!@#$%^&*_=+-/";

	public static final int PASSWORD_LENGTH = 5;

	//HTTP METHODS
	public static final String POST = "POST";
	public static final String GET = "GET";
	public static final String PUT = "PUT";
}
