package com.thisisnotajoke.lockitron;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class User {

    private static final String TAG = "User";
    private final String token;
    private final Context context;
    private ArrayList<Lock> locks;
    private ArrayAdapter<Lock> lockAdapter;

    public User(Context c, String token){
        this.token = token;
        this.context = c;
        locks = new ArrayList<Lock>();
        if(token != null && !token.isEmpty()){
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(buildLocksRequest());
        }
    }

    private JsonArrayRequest buildLocksRequest(){
        //curl -i -F "access_token=STUFF" https://api.lockitron.com/v1/locks/UUID/unlock
        String url = Lockitron.ENDPOINT + "locks?access_token="+token;

        JsonArrayRequest jsObjRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                locks.clear();
                for(int i = 0; i < response.length(); i++){
                    JSONObject obj = null;
                    try {
                        obj = response.getJSONObject(i).getJSONObject("lock");
                        locks.add(gson.fromJson(obj.toString(), Lock.class));
                    } catch (JSONException e) {
                        Log.e(TAG, "JSONException: " + e.getMessage());
                    }
                }
                if(lockAdapter != null)
                    lockAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Log.e(TAG, "Error: " + error.getStackTrace().toString());
                NetworkResponse response = error.networkResponse;

                if (response != null) {
                    Log.e(TAG, "Network Response: " + response.toString());
                }
            }
        });
        Log.v(TAG, url);
        return jsObjRequest;
    }

    public ArrayList<Lock> getLocks() {
        return locks;
    }

    public void setLocksAdapter(ArrayAdapter<Lock> adapter) {
        this.lockAdapter = adapter;
    }
}
