package com.oneaim.roombooking.helper;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by carloscorreia on 18/08/16.
 */
public class NetworkHelper {

    public enum ListenerTypes {
        JSONRequestListener, SimpleListener
    }

    private int method = Request.Method.GET;
    private String url;

    private ListenerTypes type;
    private JSONRequestListener jsonListener = null;

    public NetworkHelper(String url) {
        this.url = url;
    }

    public NetworkHelper(String url, int method) {
        this(url);
        this.method = method;
    }

    public void setListener(JSONRequestListener listener) {
        this.jsonListener = listener;
        type = ListenerTypes.JSONRequestListener;
    }


    public void process() {
        if(type.equals(ListenerTypes.JSONRequestListener))
            processJSON();
    }

    /**
     *
     * This is the only type of request processing we're going to use throughout the app.
     */
    private void processJSON() {
        final String TAG = type + ":" + url.replace(APIEndpoints.API_URL, "");

        Response.Listener<String> successListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                jsonListener.successResponse(response);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                jsonListener.errorResponse();
            }
        };

        StringRequest request = new StringRequest(method,url,successListener,errorListener) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonListener.getRequestBody().toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        MainController.getInstance().cancelPendingRequests(TAG);
        MainController.getInstance().addToRequestQueue(request, TAG);
    }
}