package com.example.finaleapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ResetPassword extends AppCompatActivity {

    EditText username, newPassword;
    Button resetBtn;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        username = findViewById(R.id.resetUsername);
        newPassword = findViewById(R.id.resetNewPassword);
        resetBtn = findViewById(R.id.resetButton);

        dbHelper = new DatabaseHelper(this);

        resetBtn.setOnClickListener(v -> {
            String user = username.getText().toString();
            String pass = newPassword.getText().toString();

            boolean success = dbHelper.updatePassword(user, pass);
            if (success) {
                Toast.makeText(this, "Password Reset Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
