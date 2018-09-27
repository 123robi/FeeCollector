package eu.rkosir.feecollector.backgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;

import eu.rkosir.feecollector.AppConfig;
import eu.rkosir.feecollector.R;
import eu.rkosir.feecollector.activity.DashboardActivity;
import eu.rkosir.feecollector.helper.HttpRequest;
import eu.rkosir.feecollector.helper.SharedPreferencesSaver;


public class CheckCredentials extends AsyncTask<String, String, String> {
	private WeakReference<Context> context;
	private WeakReference<ProgressBar> progressBar;
	private String email, password;

	public CheckCredentials(Context context, ProgressBar progressBar) {
		this.context = new WeakReference<>(context);
		this.progressBar = new WeakReference<>(progressBar);
	}

	@Override
	protected String doInBackground(String... strings) {
		email = strings[0];
		password = strings[1];
		String post_data = null;

		try {
			post_data = URLEncoder.encode("email","UTF-8") + "=" +URLEncoder.encode(email,"UTF-8")+ "&"
					+URLEncoder.encode("password","UTF-8") + "=" +URLEncoder.encode(password,"UTF-8");

		}  catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		return new HttpRequest(AppConfig.POST,post_data,AppConfig.URL_LOGIN).getResult();
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
			Log.d("JSON", object + "JSON");
			if (!object.getBoolean("error")) {
				Toast.makeText(context.get(), R.string.successful_login,Toast.LENGTH_LONG).show();

				SharedPreferencesSaver.setToken(context.get(),true);

				Activity activity = (Activity)context.get();
				Intent intent = new Intent(activity, DashboardActivity.class);
				SharedPreferencesSaver.setUser(context.get(),object.getString("user"));
				intent.putExtra("email",email);
				activity.startActivity(intent);
				activity.finish();

			} else {
				Toast.makeText(context.get(), object.getString("error_msg"),Toast.LENGTH_LONG).show();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
