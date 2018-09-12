package com.feecollector.android.feecollector.BackgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.feecollector.android.feecollector.Activity.DashboardActivity;
import com.feecollector.android.feecollector.AppConfig;
import com.feecollector.android.feecollector.Helper.JsonObjectConverter;
import com.feecollector.android.feecollector.Helper.SharedPreferencesSaver;
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
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ChangePassword  extends AsyncTask<String, String, String> {

	private WeakReference<Context> context;
	private WeakReference<ProgressBar> progressBar;
	private String email, password;
	private JsonObjectConverter converter;
	private String currentPassword;
	private WeakReference<EditText> current_password_input;
	private boolean facebookChange;

	public ChangePassword(Context context, ProgressBar progressBar, EditText current_password_input, boolean facebookChange) {
		this.context = new WeakReference<>(context);
		this.progressBar = new WeakReference<>(progressBar);
		this.converter = new JsonObjectConverter(SharedPreferencesSaver.getUser(context));
		this.current_password_input = new WeakReference<>(current_password_input);
		this.facebookChange = facebookChange;
	}

	@Override
	protected String doInBackground(String... strings) {
		email = converter.getString("email");
		password = strings[0];
		if (!facebookChange) {
			currentPassword = strings[1];
		}
		try {
			URL url = new URL(AppConfig.URL_CHANGE_PASSWORD);
			if(facebookChange) {
				url = new URL(AppConfig.URL_CHANGE_PASSWORD_FACEBOOK);
			}
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);

			OutputStream outputStream = httpURLConnection.getOutputStream();
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
			String post_data;

			if(facebookChange) {
				post_data = URLEncoder.encode("email","UTF-8") + "=" +URLEncoder.encode(email,"UTF-8")+ "&"
						+URLEncoder.encode("password","UTF-8") + "=" +URLEncoder.encode(password,"UTF-8");
			} else {
				post_data = URLEncoder.encode("email","UTF-8") + "=" +URLEncoder.encode(email,"UTF-8")+ "&"
						+URLEncoder.encode("password","UTF-8") + "=" +URLEncoder.encode(password,"UTF-8")+ "&"
						+URLEncoder.encode("current_password","UTF-8") + "=" +URLEncoder.encode(currentPassword,"UTF-8");
			}

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

			if (!object.getBoolean("error")) {
				Toast.makeText(context.get(), R.string.successfull_change_of_password,Toast.LENGTH_LONG).show();
				if (facebookChange) {
					SharedPreferencesSaver.setLogin(context.get(),false);
					Activity activity = (Activity)context.get();
					Intent intent = new Intent(activity, DashboardActivity.class);
					activity.startActivity(intent);
					activity.finish();
				}
			} else {
				current_password_input.get().setError(context.get().getString(R.string.error_current_password_not_match), null);
				View focusView = current_password_input.get();
				focusView.requestFocus();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
