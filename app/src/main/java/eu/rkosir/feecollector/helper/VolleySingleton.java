package eu.rkosir.feecollector.helper;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
	private static VolleySingleton instance;
	private RequestQueue requestQueue;
	private static Context contextInstance;

	private VolleySingleton(Context context) {
		contextInstance = context;
		requestQueue = getRequestQueue();
	}

	private RequestQueue getRequestQueue() {
		if (requestQueue == null) {
			requestQueue = Volley.newRequestQueue(contextInstance.getApplicationContext());
		}
		return requestQueue;
	}

	public static synchronized VolleySingleton getInstance(Context context) {
		if (instance == null) {
			instance = new VolleySingleton(context);
		}
		return instance;
	}

	public <T> void addToRequestQueue(Request<T> request) {
		getRequestQueue().add(request);
	}
}
