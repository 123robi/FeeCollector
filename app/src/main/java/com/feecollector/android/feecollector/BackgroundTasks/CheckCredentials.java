package com.feecollector.android.feecollector.BackgroundTasks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.feecollector.android.feecollector.Activity.DashboardActivity;
import com.feecollector.android.feecollector.AppConfig;
import com.feecollector.android.feecollector.Helper.TokenSaver;
import com.feecollector.android.feecollector.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class CheckCredentials extends AsyncTask<String, String, String> {
	@SuppressLint("StaticFieldLeak")
	private Context context;
	@SuppressLint("StaticFieldLeak")
	private ProgressBar progressBar;

	private SharedPreferences sp;
	public CheckCredentials(Context context, ProgressBar progressBar) {
		this.context = context;
		this.progressBar = progressBar;
		sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
	}

	@Override
	protected String doInBackground(String... strings) {
		try {
			URL url = new URL(AppConfig.URL_LOGIN);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);

			OutputStream outputStream = httpURLConnection.getOutputStream();
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

			String post_data = URLEncoder.encode("email","UTF-8") + "=" +URLEncoder.encode(strings[0],"UTF-8")+ "&"
					+URLEncoder.encode("password","UTF-8") + "=" +URLEncoder.encode(strings[1],"UTF-8");

			bufferedWriter.write(post_data);
			bufferedWriter.flush();
			bufferedWriter.close();
			outputStream.close();

			InputStream inputStream = httpURLConnection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
			String result = "";
			String line = "";
			while((line = bufferedReader.readLine()) != null){
				result += line;
			}
			bufferedReader.close();
			inputStream.close();
			httpURLConnection.disconnect();

			return result;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPreExecute() {
		progressBar.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onPostExecute(String s) {
		progressBar.setVisibility(View.INVISIBLE);
		JSONObject object = null;
		try {
			object = new JSONObject(s);
			if(!object.getBoolean("error")) {
				sp.edit().putBoolean(AppConfig.IS_LOGGED,true).apply();
				Toast.makeText(context, R.string.successful_login,Toast.LENGTH_LONG).show();
				TokenSaver.setToken(context,true);
				Activity activity = (Activity)context;
				Intent intent = new Intent(activity, DashboardActivity.class);
				activity.startActivity(intent);
				activity.finish();
			} else {
				Toast.makeText(context, object.getString("error_msg"),Toast.LENGTH_LONG).show();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
