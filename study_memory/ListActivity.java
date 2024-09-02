package com.cookandroid.study_memory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListActivity extends Activity {

    private ListView listView;
    private TextView textViewResult;
    private ArrayList<JSONObject> listItems;
    private ArrayAdapter<String> adapter;
    private Button btnWrite;
    private Button btnHome;
    private Button btnTime;

    private static final String API_URL = "http://10.0.2.2/memory.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        textViewResult = findViewById(R.id.textView_result);
        listView = findViewById(R.id.lv1);
        btnWrite = findViewById(R.id.btnWrite);
        btnHome = findViewById(R.id.btnHome);
        btnTime = findViewById(R.id.btnTime);

        listItems = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(adapter);

        new LoadData().execute(API_URL);

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
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), timeActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject selectedItem = listItems.get(position);
                try {
                    String title = selectedItem.getString("title");
                    String author = selectedItem.getString("author");
                    String memory = selectedItem.optString("memory", "No memory data available");
                    textViewResult.setText("제목: " + title + "\n작성자: " + author + "\n내용: " + memory);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("ITEM_CLICK_ERROR", "Error parsing JSON object: " + selectedItem.toString());
                    Toast.makeText(ListActivity.this, "Error displaying details.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class LoadData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ListActivity.this);
            progressDialog.setMessage("Loading data...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (result != null) {
                try {
                    Log.d("JSON_RESPONSE", result);
                    JSONArray jsonArray = new JSONArray(result);
                    listItems.clear();
                    ArrayList<String> displayList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        listItems.add(jsonObject);
                        String title = jsonObject.getString("title");
                        String author = jsonObject.getString("author");
                        displayList.add("제목 : " + title + "     작성자 : " + author);
                    }
                    adapter.clear();
                    adapter.addAll(displayList);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ListActivity.this, "Error parsing data.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ListActivity.this, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
