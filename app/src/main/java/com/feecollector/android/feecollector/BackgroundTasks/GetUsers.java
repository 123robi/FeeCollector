package com.feecollector.android.feecollector.BackgroundTasks;

import android.content.Context;
import android.os.AsyncTask;

import com.feecollector.android.feecollector.AppConfig;
import com.feecollector.android.feecollector.Helper.AsyncTaskCallBack;

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

public class GetUsers extends AsyncTask<String, String, String> {

    private final WeakReference<Context> context;
    private final AsyncTaskCallBack callBack;

    public GetUsers(Context context, AsyncTaskCallBack callBack) {
        this.context = new WeakReference<>(context);
        this.callBack = callBack;
    }
    @Override
    protected String doInBackground(String... strings) {
        try {
            URL url = new URL(AppConfig.URL_GET_FACEBOOK_JSON);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("email","UTF-8") + "=" +URLEncoder.encode("","UTF-8");

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
    protected void onPostExecute(String s) {
        JSONObject object = null;
        try {
            object = new JSONObject(s);
            callBack.onResult(object.getString("facebook_json"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onPostExecute(s);
    }
}
