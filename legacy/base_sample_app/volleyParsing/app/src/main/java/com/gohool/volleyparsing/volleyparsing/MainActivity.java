package com.gohool.volleyparsing.volleyparsing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String URL = "https://netflixroulette.net/api/api.php?director=Quentin";
    public static final String GEO_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2014-01-01&endtime=2014-01-02";

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queue = Volley.newRequestQueue(this);

        // Get JSON Object GEO
        getJsonObject(GEO_URL);

        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
//                Log.d("Reponse: ", response.toString());
                // Traverse the array
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject movieObject = response.getJSONObject(i);
                        Log.d("Items: ", movieObject.getString("show_title") + "/ Released: " + movieObject.getString("release_year"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", error.getMessage());
            }
        });

        queue.add(arrayRequest);
    }

    private void getJsonObject(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Metadata
                    JSONObject metadata = response.getJSONObject("metadata");

                    // jsonArray
                    JSONArray features = response.getJSONArray("features");

                    // Iterate through array and get json object
                    for (int i = 0; i < features.length(); i++) {
                        // Get objects
                        JSONObject propertiesObject = features.getJSONObject(i).getJSONObject("properties");
                        JSONObject geometryObject = features.getJSONObject(i).getJSONObject("geometry");

                        // After we get the object access what we want
                        Log.d("Place: ", propertiesObject.getString("place"));
                        Log.d("Coordinates: ", geometryObject.getString("coordinates"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error ", error.getMessage());
            }
        });

        queue.add(jsonObjectRequest);
    }

}
