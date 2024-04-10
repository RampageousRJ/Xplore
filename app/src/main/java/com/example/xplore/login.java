package com.example.xplore;

import static com.example.xplore.PasswordHasher.hashPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

public class login extends AppCompatActivity {

    Button btn_mainPage;
    TextView register;
    EditText email, password;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_mainPage = (Button) findViewById(R.id.button2);
        register = (TextView) findViewById(R.id.registerLink);
        email = (EditText) findViewById(R.id.emailField);
        password = (EditText) findViewById(R.id.passwordField);


        btn_mainPage.setOnClickListener(view -> {
            if(validateEmail(email.getText().toString()) && validatePassword(password.getText().toString())) {
                dbHandler = new DBHandler(login.this);
                String password_to_check = hashPassword(password.getText().toString());
                if(dbHandler.validateUser(getApplicationContext(),email.getText().toString(), password_to_check)) {
                    Intent mainPage = new Intent(view.getContext(), landingPage.class);
                    startActivity(mainPage);
                }
            }
        });

        register.setOnClickListener(view -> {
            Intent Register = new Intent(view.getContext(), signup.class);
            startActivity(Register);
        });
    }

    private boolean validateEmail(String email) {
        if(email.isEmpty()) {
            Toast.makeText(this, "Email cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!email.contains("@") || !email.contains(".")) {
            Toast.makeText(this, "Enter a valid email!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        if(password.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}