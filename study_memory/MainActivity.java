package com.cookandroid.study_memory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button btnWrite;
    private Button btnList;
    private Button btnTime;

    // 추가된 TextView 변수들
    private TextView tvUserName, tvUserAge, tvUserEmail, tvUserNum, tvUserPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnWrite = findViewById(R.id.btnWrite);
        btnList = findViewById(R.id.btnList);
        btnTime = findViewById(R.id.btnTime);

        // 추가된 TextView 초기화
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserAge = findViewById(R.id.tv_user_age);
        tvUserEmail = findViewById(R.id.tv_user_email);
        tvUserNum = findViewById(R.id.tv_user_num);
        tvUserPhone = findViewById(R.id.tv_user_phone);

        // LoginActivity로부터 전달받은 사용자 정보 추출 및 설정
        String userName = getIntent().getStringExtra("userName");
        String userAge = getIntent().getStringExtra("userAge");
        String userEmail = getIntent().getStringExtra("userEmail");
        String userNum = getIntent().getStringExtra("userNum");
        String userPhone = getIntent().getStringExtra("userPhone");

        tvUserName.setText("Name: " + userName);
        tvUserAge.setText("Age: " + userAge);
        tvUserEmail.setText("Email: " + userEmail);
        tvUserNum.setText("Number: " + userNum);
        tvUserPhone.setText("Phone: " + userPhone);

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriteActivity.class);
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
}
