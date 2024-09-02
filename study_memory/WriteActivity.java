package com.cookandroid.study_memory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WriteActivity extends AppCompatActivity {

    private static final String IP_ADDRESS = "10.0.2.2";
    private static final String TAG = "phpsignup";

    private EditText edt1;
    private EditText edt2;
    private EditText edt3;
    private Button btn1;
    private Button btnHome;
    private Button btnList;
    private Button btnTime;
    private ImageView iv1;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write);

        edt1 = findViewById(R.id.Edit1);
        edt2 = findViewById(R.id.Edit2);
        edt3 = findViewById(R.id.Edit3);
        btn1 = findViewById(R.id.btn1);
        btnHome = findViewById(R.id.btnHome);
        btnList = findViewById(R.id.btnList);
        btnTime = findViewById(R.id.btnTime);
        iv1 = findViewById(R.id.iv1);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Title = edt1.getText().toString();
                String Author = edt2.getText().toString();
                String Memory = edt3.getText().toString();

                InsertData task = new InsertData();
                task.execute(Title, Author, Memory);

                edt1.setText("");
                edt2.setText("");
                edt3.setText("");
            }
        });

        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), timeActivity.class);
                startActivity(intent);
            }
        });
    }

    private class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            if (params.length < 3) {
                return "Error: Insufficient parameters";
            }

            String serverURL = "http://" + IP_ADDRESS + "/memory_w.php"; // 서버 주소
            String title = params[0];
            String Author = params[1];
            String memory = params[2];

            // Include 'submit' parameter to match PHP code expectations
            String postParameters = "Title=" + title + "&Author=" + Author + "&Memory=" + memory + "&submit=true";

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();

                return sb.toString();

            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d(TAG, "Server Response: " + result);

            if (result.contains("Data inserted successfully")) {
                Toast.makeText(WriteActivity.this, "저장에 성공했습니다!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(WriteActivity.this, "저장에 실패했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

}
