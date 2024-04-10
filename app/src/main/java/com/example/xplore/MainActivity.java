package com.example.xplore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    TextView txt_login;
    Button btn_get_started;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_login = (TextView) findViewById(R.id.registerLink);
        btn_get_started = (Button) findViewById(R.id.get_started);

        txt_login.setOnClickListener(view -> {
            Intent Login = new Intent(view.getContext(), login.class);
            startActivity(Login);
        });

        btn_get_started.setOnClickListener(view -> {
            Intent Signup = new Intent(view.getContext(), signup.class);
            startActivity(Signup);
        });
    }
}