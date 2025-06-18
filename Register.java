package com.example.finaleapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity {

    EditText regUsername, regPassword;
    Button registerButton;
    DatabaseHelper dbHelper;

    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regUsername = findViewById(R.id.regUsername);
        regPassword = findViewById(R.id.regPassword);
        registerButton = findViewById(R.id.registerButton);
        dbHelper = new DatabaseHelper(this);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = regUsername.getText().toString();
                String password = regPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Register.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Save to SharedPreferences
                    boolean success = dbHelper.registerUser(username, password);
                    if (success) {
                        Toast.makeText(Register.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();

                        Toast.makeText(Register.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();

                        // Go back to login screen
                        Intent intent = new Intent(Register.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(Register.this, "User already exists!", Toast.LENGTH_SHORT).show();
                    }

            }

}
});
    }
}