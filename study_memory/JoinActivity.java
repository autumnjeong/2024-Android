package com.cookandroid.study_memory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JoinActivity extends AppCompatActivity {

    private static final String IP_ADDRESS = "10.0.2.2";
    private static final String TAG = "phpsignup";

    private EditText mEditTextName;
    private EditText mEditTextPassword;
    private EditText mEditTextEmail;
    private EditText mEditTextPhone;
    private EditText mEditTextAge;
    private EditText mEditTextNum;
    private TextView mTextViewResult;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join);

        mEditTextName = findViewById(R.id.et_name);
        mEditTextPassword = findViewById(R.id.et_pass);
        mEditTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        mEditTextEmail = findViewById(R.id.et_email);
        mEditTextPhone = findViewById(R.id.et_phone);
        mEditTextAge = findViewById(R.id.et_age);
        mEditTextNum = findViewById(R.id.et_num);
        mTextViewResult = findViewById(R.id.textView_result);

        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        Button buttonInsert = findViewById(R.id.btn_register);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = mEditTextName.getText().toString();
                String userPassword = mEditTextPassword.getText().toString();
                String userEmail = mEditTextEmail.getText().toString();
                String userPhone = mEditTextPhone.getText().toString();
                String userAge = mEditTextAge.getText().toString();
                String userNum = mEditTextNum.getText().toString();

                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/Signup.php", userName, userPassword, userEmail, userPhone, userAge, userNum);

                // Clear fields after submission
                mEditTextName.setText("");
                mEditTextPassword.setText("");
                mEditTextEmail.setText("");
                mEditTextPhone.setText("");
                mEditTextAge.setText("");
                mEditTextNum.setText("");
            }
        });
    }

    private class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(JoinActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "POST response - " + result);

            // Assuming the result contains "success" on successful signup
            if (result.contains("success")) {
                Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String userName = params[1];
            String userPassword = params[2];
            String userEmail = params[3];
            String userPhone = params[4];
            String userAge = params[5];
            String userNum = params[6];

            String postParameters = "userName=" + userName + "&userPassword=" + userPassword + "&userAge=" + userAge + "&userEmail=" + userEmail + "&userNum=" + userNum + "&userPhone=" + userPhone;

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
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
    }
}
