package eu.rkosir.feecollector.helper;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {
	private static VolleySingleton mInstance;
	private RequestQueue mRequestQueue;

	private VolleySingleton(Context mcContext) {
		mRequestQueue = Volley.newRequestQueue(mcContext.getApplicationContext());
	}

	public static synchronized VolleySingleton getInstance(Context context) {
		if(mInstance == null) {
			mInstance = new VolleySingleton(context);
		}
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		return mRequestQueue;
	}
}
