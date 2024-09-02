package com.cookandroid.study_memory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class timeActivity extends AppCompatActivity {

    private TextView timerTextView;
    private Button startButton, stopButton;
    private Button btnHome, btnWrite, btnList;
    private TextView recordsTextView;
    private long startTime = 0L;
    private boolean isRunning = false;
    private ArrayList<String> timeRecords = new ArrayList<>();
    private Handler handler = new Handler();
    private Runnable updateTimerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time);

        timerTextView = findViewById(R.id.timerTextView);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        recordsTextView = findViewById(R.id.recordsTextView);
        btnHome = findViewById(R.id.btnHome);
        btnList = findViewById(R.id.btnList);
        btnWrite = findViewById(R.id.btnWrite);

        // 시작 버튼 클릭 리스너
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning) {
                    startTimer();
                }
            }
        });

        // 종료 버튼 클릭 리스너
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning) {
                    stopTimer();
                }
            }
        });

        // 다른 액티비티로 이동하는 버튼들
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), WriteActivity.class);
                startActivity(intent);
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
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
            }
        });

        // 타이머 업데이트를 위한 Runnable 정의
        updateTimerRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    long elapsedTime = SystemClock.elapsedRealtime() - startTime;
                    updateTimer(elapsedTime);
                    handler.postDelayed(this, 100);  // 100ms마다 업데이트
                }
            }
        };
    }

    // 시간 측정을 시작하는 메서드
    private void startTimer() {
        startTime = SystemClock.elapsedRealtime();  // 현재 시간을 기록
        isRunning = true;
        handler.post(updateTimerRunnable);  // 타이머 업데이트 시작
    }

    // 시간 측정을 종료하고 결과를 기록하는 메서드
    private void stopTimer() {
        long elapsedTime = SystemClock.elapsedRealtime() - startTime;
        isRunning = false;
        handler.removeCallbacks(updateTimerRunnable);  // 타이머 업데이트 중지
        addTimeRecord(elapsedTime);
    }

    // 경과 시간을 TextView에 업데이트하는 메서드
    private void updateTimer(long elapsedTime) {
        int minutes = (int) (elapsedTime / 1000) / 60;
        int seconds = (int) (elapsedTime / 1000) % 60;

        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeFormatted);
    }

    // 경과 시간을 기록하는 메서드
    private void addTimeRecord(long elapsedTime) {
        int minutes = (int) (elapsedTime / 1000) / 60;
        int seconds = (int) (elapsedTime / 1000) % 60;

        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        timeRecords.add(timeFormatted);
        updateRecordsView();
    }

    // 기록된 시간을 TextView에 표시하는 메서드
    private void updateRecordsView() {
        StringBuilder records = new StringBuilder();
        for (String record : timeRecords) {
            records.append(record).append("\n");
        }
        recordsTextView.setText(records.toString());
    }
}