package com.example.android.networking.http1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
{
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // enable StrictMode policy to allow network operations in all threads
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        connectNetwork();
    }

    private boolean isNetworkConnected()
    {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void connectNetwork()
    {
        HttpURLConnection urlConnection = null;

        try
        {
            URL url = new URL("http://httpbin.org/stream/50");
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setConnectTimeout(60*1000);
            urlConnection.setReadTimeout(60*1000);

            urlConnection.connect();

            int response = urlConnection.getResponseCode();

            StringBuilder builder = new StringBuilder();

            builder.append(response).append(" ").append(urlConnection.getResponseMessage()).append("\n");

            if (response == 200)
            {
                InputStream is = urlConnection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(is));

                String line;

                while ((line = r.readLine()) != null)
                {
                    builder.append(line);
                    builder.append("\n");
                }

                is.close();
            }

            mTextView.setText(builder.toString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }
}
