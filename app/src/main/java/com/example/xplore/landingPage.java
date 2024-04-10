package com.example.xplore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.Manifest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import androidx.annotation.NonNull;
import androidx.activity.OnBackPressedCallback;

public class landingPage extends AppCompatActivity {
    Button find,clear;
    EditText loc;
    ListView list;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1001;
    ArrayList<String> list_values = new ArrayList<>();
    ArrayList<String> temp_values = new ArrayList<>();
    private boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        loc = (EditText)findViewById(R.id.location);
        list = (ListView)findViewById(R.id.list);
        list.setVisibility(View.INVISIBLE);

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finishAffinity();
                    return;
                }
                doubleBackToExitPressedOnce = true;
                Toast.makeText(landingPage.this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 1000);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);

        find = (Button)findViewById(R.id.find_values);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loc.getText().toString().isEmpty()) {
                    Toast.makeText(landingPage.this, "Please enter a filter!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ArrayList<String> filteredValues = filterValues(list_values, loc.getText().toString());
                ArrayAdapter<String> adapter = new ArrayAdapter<>(landingPage.this, android.R.layout.simple_list_item_1, filteredValues);
                list.setAdapter(adapter);
            }
        });

        clear = (Button)findViewById(R.id.clear_filters);
        clear.setOnClickListener(view -> {
            loc.setText("");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(landingPage.this, android.R.layout.simple_list_item_1, list_values);
            list.setAdapter(adapter);
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String number = (String) adapterView.getItemAtPosition(i);
                String[] parts = number.split(" : ");
                String phone = parts[1].replaceAll("\\s","");
                Toast.makeText(landingPage.this, "Calling " + phone, Toast.LENGTH_SHORT).show();
                dialPhoneNumber(phone);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            getLocation();
        }
    }

    public ArrayList<String> filterValues(ArrayList<String> values, String searchText) {
        ArrayList<String> filteredValues = new ArrayList<>();
        for (String value : values) {
            if (value.toLowerCase().contains(searchText.toLowerCase())) {
                filteredValues.add(value);
            }
        }
        return filteredValues;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Unsuccessful...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String state_val = getStateFromCoordinates(landingPage.this,latitude, longitude);
                    Toast.makeText(landingPage.this, "State: " + state_val, Toast.LENGTH_SHORT).show();
                    postData(state_val);
                }
            }
        });
    }

    public static String getStateFromCoordinates(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String state_val = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                state_val = address.getAdminArea();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return state_val;
    }

    private void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void postData(String location) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://xplore-oy2j.onrender.com/";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("location", location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Iterator<String> keys = response.keys();
                            while (keys.hasNext()) {
                                String agency = keys.next();
                                String phone = response.getString(agency);
                                String item = agency + " : " + phone;
                                list_values.add(item);
                            }
                            list.setVisibility(View.VISIBLE);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(landingPage.this, android.R.layout.simple_list_item_1, list_values);
                            list.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(landingPage.this,"Error: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }
}
