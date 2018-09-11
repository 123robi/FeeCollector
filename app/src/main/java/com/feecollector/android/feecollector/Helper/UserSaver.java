package com.feecollector.android.feecollector.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSaver {

	private final static String USER = "user";
	private final static String TOKEN_KEY = "user";

	public static String getUser(Context c) {
		SharedPreferences prefs = c.getSharedPreferences(USER, Context.MODE_PRIVATE);
		return prefs.getString(TOKEN_KEY, null);
	}

	public static void setUser(Context c, String json) {
		SharedPreferences prefs = c.getSharedPreferences(USER, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(TOKEN_KEY, json);
		editor.apply();
	}
	public static void clearUser(Context c) {
		SharedPreferences prefs = c.getSharedPreferences(USER, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();
		editor.apply();
	}
}
