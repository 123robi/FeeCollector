package com.feecollector.android.feecollector.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class FacebookJsonSaver {
	private final static String FACEBOOK_JSON = "facebook_json";
	private final static String TOKEN_KEY = "json";

	public static String getJson(Context c) {
		SharedPreferences prefs = c.getSharedPreferences(FACEBOOK_JSON, Context.MODE_PRIVATE);
		return prefs.getString(TOKEN_KEY, "null");
	}

	public static void setJson(Context c, String json) {
		SharedPreferences prefs = c.getSharedPreferences(FACEBOOK_JSON, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(TOKEN_KEY, json);
		editor.apply();
	}
	public static void clear(Context c) {
		SharedPreferences prefs = c.getSharedPreferences(FACEBOOK_JSON, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.clear();
		editor.apply();
	}
}
