package com.example.android.networking.okhttp_async1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
{

    private ScrollView mScrollView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.networking.okhttp_async1.R.layout.activity_main);

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

    private class ConnectNetworkTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute()                           // before running doInBackground
        {
        }

        @Override
        protected String doInBackground(String... params)       // runs in worker thread
        {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(params[0])
                    .build();

            try
            {
                Response response = client.newCall(request).execute();

                StringBuilder builder = new StringBuilder();

                builder.append(response.code()).append(" ").append(response.message()).append("\n");
                builder.append(response.body().string());

                return builder.toString();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Void... progress)   // runs in UI thread and get progress update
        {
        }

        @Override
        protected void onPostExecute(String result)         // runs in UI thread after doInBackground is done
        {
            mTextView.setText(result);

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
}
