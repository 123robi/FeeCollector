package com.feecollector.android.feecollector.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.feecollector.android.feecollector.Activity.LoginActivity;
import com.feecollector.android.feecollector.R;
import com.feecollector.android.feecollector.User.Entity.User;


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

public class CreateNewUser extends AsyncTask<String, String, String>{

	private Context context;
	private User user;
	private ProgressBar progressBar;

	public CreateNewUser(Context context, User user, ProgressBar progressBar) {
		this.context = context;
		this.user = user;
		this.progressBar = progressBar;
	}

	@Override
	protected String doInBackground(String... strings) {
		try {
			URL url = new URL("https://feecollector.000webhostapp.com/FeeCollector/addUser.php");
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);

			OutputStream outputStream = httpURLConnection.getOutputStream();
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

			String post_data = URLEncoder.encode("name","UTF-8") + "=" +URLEncoder.encode(user.getName(),"UTF-8")+ "&"
					+URLEncoder.encode("surname","UTF-8") + "=" +URLEncoder.encode(user.getSurname(),"UTF-8");

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
		if (s.equals("true")) {
			Toast.makeText(context, R.string.successful_registration,Toast.LENGTH_LONG).show();
			Activity activity = (Activity)context;
			Intent intent = new Intent(activity, LoginActivity.class);
			activity.startActivity(intent);
			activity.finish();
		}
	}
}
