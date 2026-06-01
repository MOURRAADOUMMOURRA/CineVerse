package com.example.cineverse.network;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyClient {

    private static VolleyClient instance;
    private RequestQueue requestQueue;

    private VolleyClient(Context context) {
        requestQueue = Volley.newRequestQueue(
                context.getApplicationContext()
        );
    }

    public static synchronized VolleyClient getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyClient(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
