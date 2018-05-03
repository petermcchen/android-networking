package com.example.android.networking.asynctask1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
{

    private ScrollView mScrollView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.networking.asynctask1.R.layout.activity_main);

        mScrollView = (ScrollView) findViewById(R.id.scroll_view);
        mTextView = (TextView) findViewById(R.id.web_content);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (!isNetworkConnected())
        {
            new AlertDialog.Builder(this)
                    .setTitle("No Internet Connection")
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert).show();

            return;
        }

        new ConnectNetworkTask().execute("http://httpbin.org/stream/50");
    }

    private boolean isNetworkConnected()
    {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private class ConnectNetworkTask extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute()                           // before running doInBackground
        {
        }

        @Override
        protected String doInBackground(String... params)       // runs in worker thread
        {
            try
            {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                final StringBuilder builder = new StringBuilder();
                String line;

                while ((line = r.readLine()) != null)
                {
                    builder.append(line);
                    builder.append("\n");

                    publishProgress(builder.toString());

                    Thread.sleep(100);
                }

                urlConnection.disconnect();

                return builder.toString();
            }
            catch (IOException | InterruptedException e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... progress)   // runs in UI thread and get progress update
        {
            mTextView.setText(progress[0]);

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }

        @Override
        protected void onPostExecute(String result)         // runs in UI thread after doInBackground is done
        {
            mTextView.setText(result);

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
}
