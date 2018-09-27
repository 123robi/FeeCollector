package eu.rkosir.feecollector.helper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import eu.rkosir.feecollector.AppConfig;

public class HttpRequest {

	private  String httpMethod;
	private String postData;
	private String url;

	public HttpRequest(String httpMethod, String postData, String url) {
		this.httpMethod = httpMethod;
		this.postData = postData;
		this.url = url;
	}

	public String getResult() {
		if (this.postData == null) {
			return null;
		}
		try {
			URL url = new URL(this.url);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod(this.httpMethod);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);

			OutputStream outputStream = httpURLConnection.getOutputStream();
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

			bufferedWriter.write(this.postData);
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

}
