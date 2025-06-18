 package com.example.finaleapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.TextureView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


public class HeartRateActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, Camera.PreviewCallback {
    private TextureView textureView;
    private Camera camera;
    private TextView tvHeartRate;
    private TextView tvTemp;
    private List<Integer> redSignal = new ArrayList<>();
    private List<Integer> greenSignal = new ArrayList<>();
    private Button btnStart;
    private Button history;
    int sdnn;
    private TextView tvHRV;
    private TextView tvTime;
    private final Handler timeHandler = new Handler();
    private final ArrayList<Long> rrIntervals = new ArrayList<>();
    private TextView tvRR;
    float temp;
    int finalBPM;
    private boolean measuring = false;
    private long startTime = 0;
    private int beats = 0;
    private final Handler handler = new Handler();

    private static final int MEASUREMENT_TIME_MS = 20000;
    private long lastBeatTime = 0;
    private final ArrayList<Long> beatTimestamps = new ArrayList<>();
    private final LinkedList<Integer> signalWindow = new LinkedList<>();
    private float filteredValue = 0;
    private float previousValue = 0;
    private boolean isRising = false;

    int c1,c2,c3,c4,c5;

    private TextView tvSpO2;
    private final ArrayList<Integer> bpmReadings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);

        textureView = findViewById(R.id.textureView);
        tvHeartRate = findViewById(R.id.tv_heart_rate);
        btnStart = findViewById(R.id.btn_start);
        tvHRV = findViewById(R.id.tv_hrv);
        tvRR = findViewById(R.id.tv_rr);
        tvTime = findViewById(R.id.tv_time);

        updateTime();
        history=findViewById(R.id.tv_data);

        textureView.setSurfaceTextureListener(this);

        btnStart.setOnClickListener(view -> {
            if (camera != null) {
                beats = 0;
                filteredValue = 0;
                signalWindow.clear();
                bpmReadings.clear();
                measuring = true;
                startTime = System.currentTimeMillis();
                camera.setPreviewCallback(this);
                camera.startPreview();
                startMeasurement();
            }
        });

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                100);
    }
    private void updateTime() {
        timeHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new java.util.Date());
                tvTime.setText("Time: " + currentTime);
                timeHandler.postDelayed(this, 1000); // Repeat every second
            }
        }, 0);
    }
    private void startMeasurement() {
        handler.postDelayed(() -> {
            measuring = false;
            camera.setPreviewCallback(null);

            long elapsedTime = System.currentTimeMillis() - startTime;
            int bpm = (int) ((beats * 60000.0) / elapsedTime);

            if (bpm >= 0 && bpm <= 200) {
                bpmReadings.add(bpm);
            }
            HealthDatabaseHelper dbHelper = new HealthDatabaseHelper(this);

            String currentTime1 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new java.util.Date());

            boolean isInserted = dbHelper.insertData(
                    c1,
                    c2,
                    c5,
                    c3, // RR estimate
                    c4,
                    currentTime1
            );

            if (isInserted) {
                Toast.makeText(this, "Saved to history", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
            }
            // }
          if(finalBPM<40&&MEASUREMENT_TIME_MS==20000)
            {
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("Heart Rate too Low .");
                builder.setMessage("Immediate Medical Attention needed");
                builder.setPositiveButton("Open Map", (dialog, which) -> {
                    // Example location: New Delhi
                    String geoUri = "geo:0,0?q=hospitals";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    intent.setPackage("com.google.android.apps.maps"); // Optional: ensure Maps app opens
                    startActivity(intent);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog=builder.create();

                alertDialog.show();
            }
            if(temp>38.0f&&MEASUREMENT_TIME_MS==20000){
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("Fever is detected .");
                builder.setMessage("Hospital Visit is recommended");
                builder.setPositiveButton("Open Map", (dialog, which) -> {
                    // Example location: New Delhi
                    String geoUri = "geo:0,0?q=hospitals";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                    intent.setPackage("com.google.android.apps.maps"); // Optional: ensure Maps app opens
                    startActivity(intent);
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog=builder.create();

                alertDialog.show();}
        }, MEASUREMENT_TIME_MS);


        history.setOnClickListener(view -> {
            String time = new java.text.SimpleDateFormat("yyyy-MM_dd HH:mm:ss", Locale.getDefault()).format(new java.util.Date());
            HealthDatabaseHelper dbHelper1 = new HealthDatabaseHelper(HeartRateActivity.this);
            Intent intent = new Intent(HeartRateActivity.this, History.class);
            startActivity(intent);

        });
    }



    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            camera = Camera.open();
            Camera.Parameters params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            params.setPreviewFormat(ImageFormat.NV21);
            Camera.Size previewSize = params.getSupportedPreviewSizes().get(0);
            params.setPreviewSize(previewSize.width, previewSize.height);
            camera.setParameters(params);
            camera.setPreviewTexture(surface);
            camera.setDisplayOrientation(90);
        } catch (Exception e) {
            Log.e("CAMERA", "Error setting up camera", e);
        }
    }
    private int getGreenFromYUV(byte[] data, int width, int height) {
        int frameSize = width * height;
        long sum = 0;
        int count = 0;

        for (int i = 0; i < frameSize; i += 10) {
            int y = data[i] & 0xff;
            int v = data[frameSize + (i >> 1) & ~1] & 0xff;
            int u = data[frameSize + (i >> 1) | 1] & 0xff;
            int g = (int)(1.164 * (y - 16) - 0.813 * (v - 128) - 0.391 * (u - 128));
            g = Math.max(0, Math.min(255, g));
            sum += g;
            count++;
        }
        return count > 0 ? (int)(sum / count) : 0;
    }
    public int decodeRedFromYUV(byte[] data, int width, int height) {
        int frameSize = width * height;
        long sumRed = 0;
        int count = 0;

        int startX = width / 2 - 5;
        int startY = height / 2 - 5;

        for (int y = startY; y < startY + 10; y++) {
            for (int x = startX; x < startX + 10; x++) {
                int yIndex = y * width + x;

                int yVal = data[yIndex] & 0xFF;

                int uvIndex = frameSize + (y >> 1) * width + (x & ~1);
                int v = data[uvIndex] & 0xFF;
                int u = data[uvIndex + 1] & 0xFF;

                int c = yVal - 16;
                int d = u - 128;
                int e = v - 128;

                int r = (int)(1.164 * c + 1.596 * e);
                r = Math.max(0, Math.min(255, r));

                sumRed += r;
                count++;
            }
        }

        return (int)(sumRed / count);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (!measuring) return;

        Camera.Size size = camera.getParameters().getPreviewSize();
        int width = size.width;
        int height = size.height;

        int redAvg = decodeRedFromYUV(data, width, height);
        if (redAvg < 50) {
            tvHeartRate.setText("Place finger properly");
            return;
        }
        int red = decodeRedFromYUV(data, width, height);
        int green = getGreenFromYUV(data, width, height);

        redSignal.add(red);
        greenSignal.add(green);

        if (redSignal.size() > 200) {
            int spo2 = estimateSpO2(redSignal, greenSignal);
            TextView tvSpO2 = findViewById(R.id.tv_spo2);
            tvSpO2.setText("SpO₂: " + spo2 + "%");
            c5=spo2;
            redSignal.clear();
            greenSignal.clear();

        }

        // Luminance brightness for PPG signal
        long sum = 0;
        for (int i = 0; i < width * height; i++) {
            sum += (data[i] & 0xFF);
        }
        int brightness = (int)(sum / (width * height));

        // Low-pass filter
        filteredValue = (filteredValue == 0) ? brightness : (0.8f * filteredValue + 0.2f * brightness);

        signalWindow.add((int) filteredValue);
        if (signalWindow.size() > 300) signalWindow.removeFirst();

        long currentTime = System.currentTimeMillis();
        float temp=0f;
        // Peak detection logic (slope-based)
        if (filteredValue > previousValue && !isRising) {
            isRising = true;
        } else if (filteredValue < previousValue && isRising) {
            isRising = false;
            if (currentTime - lastBeatTime > 400) {
                beats++;
                if (lastBeatTime > 0) {

                    long interval = currentTime - lastBeatTime;
                    int currentBPM = (int)(60000.0 / interval);
                    if (currentBPM >= 40 && currentBPM <= 200) {
                        tvHeartRate.setText("Heart Rate: " + (int)(0.95*currentBPM-2 ) + " bpm");
                        long rr = currentTime - lastBeatTime;
                        rrIntervals.add(rr);
                        temp = estimateTemperature((int)(0.95*currentBPM-2));
                        TextView tvTemp = findViewById(R.id.tv_temperature);
                        tvTemp.setText("Body Temp: " + String.format(Locale.US, "%.1f°F", temp));
                        tvRR.setText("RR: " + (int)(0.90*((0.85*currentBPM+5.3)/4)+1) + " bpm");
                        finalBPM=(int)(0.95*currentBPM-2);

                        c1=finalBPM;
                        c2=(int)temp;
                        c3=(int)(0.90*((0.85*currentBPM+5.3)/4)+1);






                    }

                }   beatTimestamps.add(currentTime);
                lastBeatTime = currentTime;
                Log.d("PPG", "Beat detected at " + currentTime);




            }
        }

        previousValue = filteredValue;

        Log.d("PPG", "Brightness=" + brightness + ", Filtered=" + filteredValue);
        if (!rrIntervals.isEmpty()) {
            double sum1 = 0;
            double mean = 0;
            for (long rr : rrIntervals) mean += rr;
            mean /= rrIntervals.size();

            for (long rr : rrIntervals) {
                double diff = rr - mean;
                sum1 += diff * diff;
            }
            double sdnn1 = Math.sqrt(sum1 / rrIntervals.size());  // HRV in ms
            sdnn=(int)sdnn1;
            tvHRV.setText("HRV: " + (int) sdnn1 + " ms");
            c4=(int)sdnn1;
        } else {
            tvHRV.setText("HRV: -- ms");
        }
           // if(MEASUREMENT_TIME_MS>=20000) {
               /* HealthDatabaseHelper dbHelper = new HealthDatabaseHelper(this);

                String currentTime1 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new java.util.Date());

                boolean isInserted = dbHelper.insertData(
                        c1,
                        c2,
                        c5,
                        c3, // RR estimate
                        c4,
                        currentTime1
                );

                if (isInserted) {
                    Toast.makeText(this, "Saved to history", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
                }
           // }*/
       // dbHelper.clearDatabase();
    }

    private int estimateSpO2(List<Integer> red, List<Integer> green) {
        if (red.size() < 100 || green.size() < 100) return 0;

        double redAC = Collections.max(red) - Collections.min(red);
        double greenAC = Collections.max(green) - Collections.min(green);
        double redDC = red.stream().mapToInt(Integer::intValue).average().orElse(1);
        double greenDC = green.stream().mapToInt(Integer::intValue).average().orElse(1);

        double ratio = (redAC / redDC) / (greenAC / greenDC);
        int spo2 = (int)(110 - 25 * ratio);
        return Math.max(70, Math.min(100, spo2));
    }




    private float estimateTemperature(int bpm) {
        float baseTemp = 36.5f; // average resting temperature
        if (bpm < 90) return (baseTemp*1.8f+32);
        if (bpm < 110) return (baseTemp + 0.3f)*1.8f+32;
        if (bpm < 130) return (baseTemp + 0.6f)*1.8f+32;
        return (baseTemp + 1.0f)*1.8f+32;
    }



    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
}
