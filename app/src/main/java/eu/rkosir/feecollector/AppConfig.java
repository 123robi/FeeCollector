package eu.rkosir.feecollector;

public class AppConfig {
	public static final String URL_LOGIN = "http://rkosir.eu/Cake/usersApi/login";
	public static final String URL_REGISTER = "http://rkosir.eu/Cake/usersApi/add";
	public static final String URL_CHANGE_PASSWORD = "http://rkosir.eu/Cake/usersApi/changePassword";
	public static final String URL_CHANGE_PASSWORD_FACEBOOK = "http://rkosir.eu/Cake/usersApi/changePasswordFacebook";

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
