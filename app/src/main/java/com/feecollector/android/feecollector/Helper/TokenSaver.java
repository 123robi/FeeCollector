package com.feecollector.android.feecollector.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenSaver {
	private final static String IS_LOGGED = "IS_LOGGED";
	private final static String TOKEN_KEY = "TOKEN_KEY";

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
}
