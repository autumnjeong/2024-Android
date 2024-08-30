package com.cookandroid.study_memory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private EditText etName, etPass;
    private Button btnLogin, btnJoin;
    private TextView tvInfor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        etName = findViewById(R.id.et_name);
        etPass = findViewById(R.id.et_pass);
        btnLogin = findViewById(R.id.btn_login);
        btnJoin = findViewById(R.id.btn_join);
        tvInfor = findViewById(R.id.tv_infor);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
            }
        });
    }

    private void performLogin() {
        final String name = etName.getText().toString();
        final String pass = etPass.getText().toString();

        // Check if name or password fields are empty
        if (name.isEmpty() || pass.isEmpty()) {
            tvInfor.setText("이름과 비밀번호를 입력해 주세요.");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2/Login.php");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    // Send POST data
                    String postData = "userName=" + name + "&userPassword=" + pass;
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(postData);
                    writer.flush();
                    writer.close();

                    // Get response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    final String result = response.toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleLoginResponse(result);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void handleLoginResponse(String response) {
        if (response.contains("no name OR wrong password")) {
            tvInfor.setText("로그인에 실패하였습니다");
        } else {
            try {
                // Parse JSON and extract user data
                JSONObject jsonResponse = new JSONObject(response);
                JSONArray userArray = jsonResponse.getJSONArray("user");
                JSONObject user = userArray.getJSONObject(0);

                String userName = user.getString("userName");
                String userAge = user.getString("userAge");
                String userEmail = user.getString("userEmail");
                String userNum = user.getString("userNum");
                String userPhone = user.getString("userPhone");

                tvInfor.setText("로그인에 성공하셨습니다\n2초뒤 홈 화면으로 이동 됩니다");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("userName", userName);
                        intent.putExtra("userAge", userAge);
                        intent.putExtra("userEmail", userEmail);
                        intent.putExtra("userNum", userNum);
                        intent.putExtra("userPhone", userPhone);
                        startActivity(intent);
                        finish();  // Close LoginActivity
                    }
                }, 2000);  // 2 seconds delay

            } catch (Exception e) {
                e.printStackTrace();
                tvInfor.setText("로그인에 실패하였습니다");
            }
        }
    }
}
