package com.example.finaleapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class History extends AppCompatActivity {

    HealthDatabaseHelper dbHelper;
    TextView textViewData; // Or use RecyclerView for a better UI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        textViewData = findViewById(R.id.tv_data); // Make sure this TextView exists in layout
        dbHelper = new HealthDatabaseHelper(this);

        Cursor cursor = dbHelper.getAllData();
        StringBuilder data = new StringBuilder();

        if (cursor.getCount() == 0) {
            data.append("No data found");
        } else {
            while (cursor.moveToNext()) {
                data.append("Time: ").append(cursor.getString(6)).append("\n")
                        .append("BPM: ").append(cursor.getInt(1)).append("\n")
                        .append("Temp: ").append(cursor.getFloat(2)).append("°F\n")
                        .append("SpO2: ").append(cursor.getInt(3)).append("%\n")
                        .append("RR: ").append(cursor.getInt(4)).append(" bpm\n")
                        .append("HRV: ").append(cursor.getInt(5)).append(" ms\n\n");
            }
        }

        textViewData.setText(data.toString());
    }
}

