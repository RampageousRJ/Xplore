package com.example.xplore;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchPlaces extends AppCompatActivity {
    ImageButton home;
    double latitude, longitude;
    ListView places_list;
    HashMap<String,String> place_map;
    Spinner selector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_places);
        home = findViewById(R.id.homeButton);
        home.setOnClickListener(v -> {
            Intent intent = new Intent(SearchPlaces.this, landingPage.class);
            startActivity(intent);
        });

        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0);
        longitude = intent.getDoubleExtra("longitude", 0);

        getDefaultPlaces(latitude, longitude);
        places_list = findViewById(R.id.places_list);
        selector = findViewById(R.id.selector);
        selector.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"Temples", "Hotels", "Cafe", "Parks", "Shopping", "Hospitals"}));
        selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getDefaultPlaces(latitude, longitude, adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                getDefaultPlaces(latitude, longitude);
            }
        });

        places_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchPlaces.this, PlaceDetails.class);
                intent.putExtra("fsq_id", place_map.get(adapterView.getItemAtPosition(i).toString()));
                intent.putExtra("distance", place_map.get(adapterView.getItemAtPosition(i).toString()+"_distance"));
                startActivity(intent);
            }
        });
    }

    private void getDefaultPlaces(double latitude, double longitude,String query) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.foursquare.com/v3/places/search?query="+query+"&ll="+latitude+"%2C"+longitude+"&radius=5000&limit=50&sort=POPULARITY", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray results = null;
                        HashMap<String,String> place_map_temp = new HashMap<>();
                        ArrayList<String> list_elements = new ArrayList<>();
                        try {
                            results = response.getJSONArray("results");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = null;
                            try {
                                place = results.getJSONObject(i);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                list_elements.add(place.getString("name"));
                                place_map_temp.put(place.getString("name"), place.getString("fsq_id"));
                                place_map_temp.put(place.getString("name")+"_distance", place.getString("distance"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchPlaces.this, android.R.layout.simple_list_item_1, list_elements);
                        places_list.setAdapter(adapter);
                        place_map = place_map_temp;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchPlaces.this, "Unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "fsq3vZ2E1pK5yNw9IgJ+cCRG54nbxUT5xu/3lz1Ubh4ldoE="); // Adjust header name and format based on API requirements
                return headers;
            }
        };
        queue.add(request);
    }

    private void getDefaultPlaces(double latitude, double longitude) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.foursquare.com/v3/places/search?ll="+latitude+"%2C"+longitude+"&radius=5000&limit=50&sort=POPULARITY", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray results = null;
                        HashMap<String,String> place_map_temp = new HashMap<>();
                        ArrayList<String> list_elements = new ArrayList<>();
                        try {
                            results = response.getJSONArray("results");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = null;
                            try {
                                place = results.getJSONObject(i);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                list_elements.add(place.getString("name"));
                                place_map_temp.put(place.getString("name"), place.getString("fsq_id"));
                                place_map_temp.put(place.getString("distance"), place.getString("distance"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchPlaces.this, android.R.layout.simple_list_item_1, list_elements);
                        places_list.setAdapter(adapter);
                        place_map = place_map_temp;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchPlaces.this, "UnSuccessful", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "fsq3vZ2E1pK5yNw9IgJ+cCRG54nbxUT5xu/3lz1Ubh4ldoE="); // Adjust header name and format based on API requirements
                return headers;
            }
        };
        queue.add(request);
    }


}