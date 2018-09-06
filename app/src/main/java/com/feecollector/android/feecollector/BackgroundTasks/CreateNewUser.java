package com.feecollector.android.feecollector.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.feecollector.android.feecollector.Activity.LoginActivity;
import com.feecollector.android.feecollector.AppConfig;
import com.feecollector.android.feecollector.R;
import com.feecollector.android.feecollector.User.Entity.User;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class CreateNewUser extends AsyncTask<String, String, String>{

	private final WeakReference<Context> context;
	private final WeakReference<ProgressBar> progressBar;
	private User user;
	private boolean facebookLogin;

	public CreateNewUser(Context context, User user, ProgressBar progressBar, boolean facebookLogin) {
		this.context = new WeakReference<>(context);
		this.user = user;
		this.progressBar = new WeakReference<>(progressBar);
		this.facebookLogin = facebookLogin;
	}
	@Override
	protected String doInBackground(String... strings) {
		try {
			URL url = new URL(AppConfig.URL_REGISTER);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);

			OutputStream outputStream = httpURLConnection.getOutputStream();
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

			String post_data = URLEncoder.encode("name","UTF-8") + "=" +URLEncoder.encode(user.getName(),"UTF-8")+ "&"
					+URLEncoder.encode("email","UTF-8") + "=" +URLEncoder.encode(user.getEmail(),"UTF-8")+ "&"
					+URLEncoder.encode("password","UTF-8") + "=" +URLEncoder.encode(user.getPassword(),"UTF-8")+ "&"
					+URLEncoder.encode("facebook_json","UTF-8") + "=" +URLEncoder.encode("default","UTF-8");

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
		progressBar.get().setVisibility(View.VISIBLE);
	}

	@Override
	protected void onPostExecute(String s) {
		progressBar.get().setVisibility(View.INVISIBLE);
		JSONObject object = null;
		try {
			object = new JSONObject(s);
			if(!object.getBoolean("error")) {
				Toast.makeText(context.get(), R.string.successful_registration,Toast.LENGTH_LONG).show();
				Activity activity = (Activity)context.get();
				Intent intent = new Intent(activity, LoginActivity.class);
				activity.startActivity(intent);
				activity.finish();
			} else {
				if(!facebookLogin) {
					Toast.makeText(context.get(), object.getString("error_msg"),Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(context.get(), R.string.successful_login,Toast.LENGTH_LONG).show();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
