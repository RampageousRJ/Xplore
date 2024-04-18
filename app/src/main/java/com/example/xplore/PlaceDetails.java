package com.example.xplore;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaceDetails extends AppCompatActivity {

    ImageView icon;
    ListView reviews_list;
    TextView place_name,place_category;
    TextView place_location,place_distance;
    String distance;
    String email;

    ImageButton home,search,profile;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        place_name = findViewById(R.id.place_name);
        place_location = findViewById(R.id.place_location);
        reviews_list = findViewById(R.id.reviews_list);
        place_distance = findViewById(R.id.place_distance);
        place_category = findViewById(R.id.place_category);
        email = getIntent().getStringExtra("email");

        home = findViewById(R.id.homeButton);
        home.setOnClickListener(v -> {
            Intent intent = new Intent(PlaceDetails.this, landingPage.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        search = findViewById(R.id.searchButton);
        search.setOnClickListener(v -> {
            Intent intent = new Intent(PlaceDetails.this, SearchPlaces.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        profile = findViewById(R.id.profileButton);
        profile.setOnClickListener(v -> {
            Intent intent = new Intent(PlaceDetails.this, Profile.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        Intent intent  = getIntent();
        String fsq_id = intent.getStringExtra("fsq_id");
        distance = intent.getStringExtra("distance");
        assert distance != null;
        place_distance.setText("Distance: "+Double.parseDouble(distance)/1000+" km");

        populateDetails(fsq_id);
        populateReviews(fsq_id);
    }

    private void populateDetails(String fsqId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://api.foursquare.com/v3/places/"+fsqId, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String name_of_place = response.getString("name");
                            if(name_of_place.split(" ").length>5){
                                place_name.setTextSize(25);
                            }
                            else{
                                place_name.setTextSize(30);
                            }
                            place_name.setText(name_of_place);
                            JSONObject location = response.getJSONObject("location");
                            place_location.setText(location.getString("formatted_address"));
                            ArrayList<JSONObject> categories = new ArrayList<>();
                            JSONArray categoriesArray = response.getJSONArray("categories");
                            place_category.setText(categoriesArray.getJSONObject(0).getString("name"));

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
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

    private void populateReviews(String fsq_id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "https://api.foursquare.com/v3/places/"+fsq_id+"/tips?limit=50", null,
                new Response.Listener<JSONArray>() { // Changed to JsonArrayRequest
                    @Override
                    public void onResponse(JSONArray response) { // Changed parameter to JSONArray
                        try {
                            ArrayList<String> reviewList = new ArrayList<>(); // Create ArrayList to store review text
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String text = jsonObject.getString("text"); // Get text field from each JSON object
                                reviewList.add(text); // Add text to reviewList
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(PlaceDetails.this, android.R.layout.simple_list_item_1, reviewList);
                            reviews_list.setAdapter(adapter); // Set adapter to ListView
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error: ", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "fsq3vZ2E1pK5yNw9IgJ+cCRG54nbxUT5xu/3lz1Ubh4ldoE=");
                return headers;
            }
        };
        queue.add(request);
    }
}
