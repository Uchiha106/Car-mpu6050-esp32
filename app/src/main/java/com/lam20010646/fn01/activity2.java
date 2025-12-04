package com.lam20010646.fn01;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import android.widget.TextView;

import java.io.IOException;


public class activity2 extends AppCompatActivity {
    private OkHttpClient client;
    private String nodeMCUIpAddress = "172.20.10.3"; // Thay bằng địa chỉ IP thực tế của NodeMCU
    private int nodeMCUPort = 80; // Cổng của NodeMCU
    private String getDataUrl = "http://" + nodeMCUIpAddress + "/get_data";
    private String goStranight = "http://" + nodeMCUIpAddress + ":" + nodeMCUPort + "/goStraight";
    private String turnLeft = "http://" + nodeMCUIpAddress + ":" + nodeMCUPort + "/turnLeft";
    private String turnRight = "http://" + nodeMCUIpAddress + ":" + nodeMCUPort + "/turnRight";
    private String turnBack = "http://" + nodeMCUIpAddress + ":" + nodeMCUPort + "/turnBack";
    private String off = "http://" + nodeMCUIpAddress + ":" + nodeMCUPort + "/off";
    private TextView textAX, textGX,textAY, textGY,textAZ, textGZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        client = new OkHttpClient();

        textAX = findViewById(R.id.textA_X);
        textAY = findViewById(R.id.textA_Y);
        textAZ = findViewById(R.id.textA_Z);
        textGX = findViewById(R.id.textG_X);
        textGY = findViewById(R.id.textG_Y);
        textGZ = findViewById(R.id.texG_Z);
        fetchDataFromESP8266(getDataUrl);

        View btnG = findViewById(R.id.btG);
        View btnL = findViewById(R.id.btL);
        View btnR = findViewById(R.id.btR);
        View btnB = findViewById(R.id.btB);
        //--------di_thang
        btnG.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sendRequest(goStranight);
                        return true;
                    case MotionEvent.ACTION_UP:
                        sendRequest(off);
                        return true;
                    default:
                        return false;
                }
            }
        });
        //--------re_trai
        btnL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sendRequest(turnLeft);
                        return true;
                    case MotionEvent.ACTION_UP:
                        sendRequest(off);
                        return true;
                    default:
                        return false;
                }
            }
        });
        //--------re_phai
        btnR.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sendRequest(turnRight);
                        return true;
                    case MotionEvent.ACTION_UP:
                        sendRequest(off);
                        return true;
                    default:
                        return false;
                }
            }
        });
        //--------di_lui
        btnB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        sendRequest(turnBack);
                        return true;
                    case MotionEvent.ACTION_UP:
                        sendRequest(off);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void fetchDataFromESP8266(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showToast("Failed to fetch data");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                String responseData = response.body().string();
                updateUI(responseData);
            }
        });
    }
    private void updateUI(final String responseData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String[] params = responseData.split(",");
                if (params.length == 6) {
                    float AX = Float.parseFloat(params[0]);
                    float AY = Float.parseFloat(params[1]);
                    float AZ = Float.parseFloat(params[2]);
                    float GX = Float.parseFloat(params[3]);
                    float GY = Float.parseFloat(params[4]);
                    float GZ = Float.parseFloat(params[5]);
                    textAX.setText("X: " + AX);
                    textAY.setText("Y: " + AY);
                    textAZ.setText("Z: " + AZ);
                    textGX.setText("X: " + GX);
                    textGY.setText("Y: " + GY);
                    textGZ.setText("Z: " + GZ);
                } else {
                    showToast("Invalid response format");
                }
            }
        });
    }

    private void sendRequest(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showToast("Failed to send request");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                showToast("Request sent successfully");
            }
        });
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity2.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
