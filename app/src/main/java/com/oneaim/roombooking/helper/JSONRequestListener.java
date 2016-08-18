package com.oneaim.roombooking.helper;

import com.android.volley.NetworkResponse;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by carloscorreia on 18/08/16.
 */
public interface JSONRequestListener {

     //Usually we'd also set a method for request headers, i.e Map<String,String> getHeaders();

     JSONObject getRequestBody();
     void successResponse(String response);
     void errorResponse();
}
