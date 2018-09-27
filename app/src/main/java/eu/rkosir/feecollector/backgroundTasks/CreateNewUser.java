package eu.rkosir.feecollector.backgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.User.entity.User;
import eu.rkosir.feecollector.activity.ChangePasswordActivity;
import eu.rkosir.feecollector.activity.DashboardActivity;
import eu.rkosir.feecollector.activity.LoginActivity;
import eu.rkosir.feecollector.helper.HttpRequest;
import eu.rkosir.feecollector.helper.JsonObjectConverter;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;

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
		String post_data = null;
		try {
			if(user.getFacebook_json() != null) {
				post_data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(user.getName(), "UTF-8") + "&"
						+ URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(user.getEmail(), "UTF-8") + "&"
						+ URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(user.getPassword(), "UTF-8") + "&"
						+ URLEncoder.encode("facebook_json", "UTF-8") + "=" + URLEncoder.encode(user.getFacebook_json(), "UTF-8");
			} else {
				post_data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(user.getName(), "UTF-8") + "&"
						+ URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(user.getEmail(), "UTF-8") + "&"
						+ URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(user.getPassword(), "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		return new HttpRequest(AppConfig.POST, post_data, AppConfig.URL_REGISTER).getResult();
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
			JsonObjectConverter converter = new JsonObjectConverter(object.getString("user"));
			if(!object.getBoolean("error")) {
				Toast.makeText(context.get(), R.string.successful_registration,Toast.LENGTH_LONG).show();
				SharedPreferencesSaver.setUser(context.get(), object.getString("user"));
				if(object.getJSONObject("user").isNull("facebook_json")) {
					Activity activity = (Activity)context.get();
					Intent intent = new Intent(activity, LoginActivity.class);
					activity.startActivity(intent);
					activity.finish();
				} else {
					Activity activity = (Activity)context.get();
					Intent intent = new Intent(activity, ChangePasswordActivity.class);
					SharedPreferencesSaver.setLogin(context.get(),true);
					activity.startActivity(intent);
					activity.finish();
				}

			} else {
				if(!facebookLogin) {
					Toast.makeText(context.get(), object.getString("error_msg"),Toast.LENGTH_LONG).show();
				} else {
					Intent intent = new Intent(context.get(), DashboardActivity.class);
					SharedPreferencesSaver.setUser(context.get(), object.getString("user"));
					context.get().startActivity(intent);
					Toast.makeText(context.get(), R.string.successful_login,Toast.LENGTH_LONG).show();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
