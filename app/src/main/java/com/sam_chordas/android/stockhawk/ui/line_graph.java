package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class line_graph extends ActionBarActivity {

    LineChart lineChart;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph2);
        Intent i = getIntent();
        String sym = i.getStringExtra("symbol");

        String query = "select * from yahoo.finance.historicaldata where symbol ='" + sym + "' and startDate = '2016-01-01' and endDate = '2016-01-24'";

        uri = Uri.parse("https://query.yahooapis.com/v1/public/yql").buildUpon().appendQueryParameter("q", query).appendQueryParameter("format","json").
                appendQueryParameter("diagnostics","true").appendQueryParameter("env","store://datatables.org/alltableswithkeys").appendQueryParameter("callback","").build();
        Log.v("url", uri.toString());
        lineChart = (LineChart) findViewById(R.id.linechart);

        AsyncTaskForLineGraph asyncTaskForLineGraph = new AsyncTaskForLineGraph();
        asyncTaskForLineGraph.execute(uri.toString());
    }
    class AsyncTaskForLineGraph extends AsyncTask<String,String,String> {

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();
        @Override
        protected String doInBackground(String... params) {

            String response = getResponseFromApi(params[0]);
            int val = 1;
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject jsonObject1 = jsonObject.getJSONObject("query");
                JSONObject jsonObject3 = jsonObject1.getJSONObject("results");
                JSONArray jsonArray = jsonObject3.getJSONArray("quote");
                Log.v("array",jsonArray.length()+"");
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    entries.add(new Entry((int) Float.parseFloat(jsonObject2.getString("Adj_Close")),val));
                    val++;

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            labels.add("1");
            labels.add("2");
            labels.add("3");
            labels.add("4");
            labels.add("5");
            labels.add("6");
            labels.add("7");
            labels.add("8");
            labels.add("9");
            labels.add("10");
            labels.add("11");
            labels.add("12");
            labels.add("13");
            labels.add("14");

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            int animateSeconds = 1000;
            super.onPostExecute(s);
            LineDataSet dataset = new LineDataSet(entries, "Stock Values over time");
            dataset.setDrawCircles(true);
            dataset.setDrawValues(true);
            LineData data = new LineData(labels,dataset);
            lineChart.setDescription("Graph for Stock Values");
            lineChart.setData(data);
            lineChart.animateY(animateSeconds);

        }

        public String getResponseFromApi(String url) {
            String response="";

            HttpGet httpGet = new HttpGet(url);
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity);

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (httpClient != null) {
                    httpClient.getConnectionManager().shutdown();
                }
            }
            return response;
        }
    }
}
