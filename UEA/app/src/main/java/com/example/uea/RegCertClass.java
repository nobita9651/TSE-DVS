package com.example.uea;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegCertClass {
    public void postData(final RegCertAsyncPost callback , RequestQueue requestQueue , byte[] imageData){

        String url = "https://demoid.cognitiveservices.azure.com/formrecognizer/v2.1/custom/models/e00368a3-93ab-4555-b961-0ff787eca0dc/analyze";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Success", "Response: " );
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", "ErrorResponse: " + error.getMessage());
                callback.processFailed(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "image/jpeg");
                params.put("Ocp-Apim-Subscription-Key", "622e575b94bc4eecbd015053d74c117a");
                // change key here
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "image/jpeg";
            }

            @Override
            public byte[] getBody(){
                return imageData;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                String responseString = "",opLoc = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                    opLoc = String.valueOf(response.headers.get("operation-location"));
                    callback.processFinished(opLoc);
                }
                return null;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public void getData(String url , final RegCertAsyncGet callback , RequestQueue requestQueue){
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String res = response.toString();
                        Log.d("json response", "onResponse:"+res);
                        try {
                            JSONObject resp = new JSONObject(res);
                            JSONObject analyzeResult = resp.getJSONObject("analyzeResult");
                            JSONArray documentResults = analyzeResult.getJSONArray("documentResults");
                            JSONObject docRes = documentResults.getJSONObject(0);
                            JSONObject fields = docRes.getJSONObject("fields");
                            String RCExpiry = fields.getJSONObject("expiry").getString("valueString");
                            String Name = fields.getJSONObject("name").getString("valueString");
                            RegCertInfo regCertInfo = new RegCertInfo(Name,RCExpiry);
                            callback.processFinished(regCertInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.processFailed(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Ocp-Apim-Subscription-Key", "622e575b94bc4eecbd015053d74c117a");
                return params;
            }
        };
        requestQueue.add(getRequest);
    }
}
