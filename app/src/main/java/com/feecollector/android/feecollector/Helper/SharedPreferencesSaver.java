package com.feecollector.android.feecollector.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesSaver {
	private final static String IS_LOGGED = "IS_LOGGED";
	private final static String TOKEN_KEY = "TOKEN_KEY";

	private final static String USER = "user";
	private final static String TOKEN_KEY_USER = "user";

	private final static String LOGIN = "login";
	private final static String TOKEN_KEY_LOGIN = "login";

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
}
