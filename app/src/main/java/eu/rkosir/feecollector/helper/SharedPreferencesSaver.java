package eu.rkosir.feecollector.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SharedPreferencesSaver {
	private final static String IS_LOGGED = "IS_LOGGED";
	private final static String TOKEN_KEY = "TOKEN_KEY";

	private final static String USER = "user";
	private final static String TOKEN_KEY_USER = "user";

	private final static String LOGIN = "login";
	private final static String TOKEN_KEY_LOGIN = "login";


	private final static String TEAM = "team";
	private final static String TOKEN_KEY_TEAM_NAME = "team_name";
	private final static String TOKEN_KEY_TEAM_ID = "team_id";

	private final static String CURRENCY_SYMBOL = "currency";
	private final static String CURRENCY_CODE = "currency";
	private final static String TOKEN_KEY_CURRENCY_CODE = "currency_code";
	private final static String TOKEN_KEY_CURRENCY_SYMBOL = "currency_symbol";


	private final static String fcmToken = "fcm_token";
	private final static String FCM_TOKEN_KEY = "FCM_TOKEN_KEY";

	public static boolean getToken(Context c) {
		SharedPreferences prefs = c.getSharedPreferences(IS_LOGGED, Context.MODE_PRIVATE);
		return prefs.getBoolean(TOKEN_KEY, false);
	}

	public static void setToken(Context c, Boolean token) {
		SharedPreferences prefs = c.getSharedPreferences(IS_LOGGED, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(TOKEN_KEY, token);
		editor.apply();
	}

	public static String getFcmToken(Context c) {
		SharedPreferences prefs = c.getSharedPreferences(fcmToken, Context.MODE_PRIVATE);
		return prefs.getString(FCM_TOKEN_KEY, "");
	}

	public static void setFcmToken(Context c, String token) {
		SharedPreferences prefs = c.getSharedPreferences(fcmToken, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(FCM_TOKEN_KEY, token);
		editor.apply();
	}

	public static String getUser(Context c) {
		SharedPreferences prefs = c.getSharedPreferences(USER, Context.MODE_PRIVATE);
		return prefs.getString(TOKEN_KEY_USER, null);
	}

	public static void setUser(Context c, String json) {
		SharedPreferences prefs = c.getSharedPreferences(USER, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(TOKEN_KEY_USER, json);
		editor.apply();
	}
	public static void clearUser(Context c) {
		SharedPreferences prefs = c.getSharedPreferences(USER, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();
		editor.apply();
	}

	public static void setLogin(Context c, Boolean isfacebook) {
		SharedPreferences prefs = c.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(TOKEN_KEY_LOGIN, isfacebook);
		editor.apply();
	}

	public static boolean getLogin(Context c) {
		SharedPreferences prefs = c.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);
		return prefs.getBoolean(TOKEN_KEY_LOGIN, false);
	}

	public static void setLastTeamName(Context c, String team_name) {
		SharedPreferences prefs = c.getSharedPreferences(TEAM, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(TOKEN_KEY_TEAM_NAME, team_name);
		editor.apply();
	}

	public static String getLastTeamName(Context c) {
		SharedPreferences prefs = c.getSharedPreferences(TEAM, Context.MODE_PRIVATE);
		return prefs.getString(TOKEN_KEY_TEAM_NAME, null);
	}
	public static void setLastTeamId(Context c, String team_id) {
		SharedPreferences prefs = c.getSharedPreferences(TEAM, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(TOKEN_KEY_TEAM_ID, team_id);
		editor.apply();
	}

	public static String getLastTeamID(Context c) {
		SharedPreferences prefs = c.getSharedPreferences(TEAM, Context.MODE_PRIVATE);
		return prefs.getString(TOKEN_KEY_TEAM_ID, null);
	}
	public static void setCurrencyCode(Context c, String currency_code) {
		SharedPreferences prefs = c.getSharedPreferences(CURRENCY_CODE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(TOKEN_KEY_CURRENCY_CODE, currency_code);
		editor.apply();
	}

	public static String getCurrencyCode(Context c) {
		SharedPreferences prefs = c.getSharedPreferences(CURRENCY_CODE, Context.MODE_PRIVATE);
		return prefs.getString(TOKEN_KEY_CURRENCY_CODE, null);
	}
	public static void setCurrencySymbol(Context c, String currency_symbol) {
		SharedPreferences prefs = c.getSharedPreferences(CURRENCY_SYMBOL, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(TOKEN_KEY_CURRENCY_SYMBOL, currency_symbol);
		editor.apply();
	}

	public static String getCurrencySymbol(Context c) {
		SharedPreferences prefs = c.getSharedPreferences(CURRENCY_SYMBOL, Context.MODE_PRIVATE);
		return prefs.getString(TOKEN_KEY_CURRENCY_SYMBOL, null);
	}
}
