package com.example.xplore;

import static com.example.xplore.PasswordHasher.hashPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class signup extends AppCompatActivity {

    Button btn_signup;

    TextView login;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btn_signup = (Button) findViewById(R.id.button);
        login = (TextView) findViewById(R.id.loginLink);

        EditText name = (EditText) findViewById(R.id.nameET);
        EditText email = (EditText) findViewById(R.id.mailET);
        EditText phone = (EditText) findViewById(R.id.numberET);
        EditText password = (EditText) findViewById(R.id.passET);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateDetails()){
                    dbHandler = new DBHandler(signup.this);
                    String hashedPassword = hashPassword(password.getText().toString());
                    dbHandler.insertUser(name.getText().toString(), email.getText().toString(), phone.getText().toString(), hashedPassword);
                    Toast.makeText(signup.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                    Intent login = new Intent(view.getContext(), login.class);
                    startActivity(login);
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(view.getContext(), login.class);
                startActivity(login);
            }
        });
    }

    private boolean validateDetails(){
        EditText name = (EditText) findViewById(R.id.nameET);
        EditText email = (EditText) findViewById(R.id.mailET);
        EditText phone = (EditText) findViewById(R.id.numberET);
        EditText password = (EditText) findViewById(R.id.passET);
        if(name.getText().toString().isEmpty() || email.getText().toString().isEmpty() || phone.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            Toast.makeText(this, "All fields are mandatory!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!email.getText().toString().contains("@") || !email.getText().toString().contains(".")) {
            Toast.makeText(this, "Enter a valid email!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(phone.getText().toString().length() != 10) {
            Toast.makeText(this, "Enter a valid phone number!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.getText().toString().length() < 6) {
            Toast.makeText(this, "Password should be atleast 6 characters long!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}