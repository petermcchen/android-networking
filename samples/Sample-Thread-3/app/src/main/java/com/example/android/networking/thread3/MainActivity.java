package com.example.android.networking.thread3;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity
{

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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

        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                connectNetwork();
            }
        };

        thread.start();
    }

    private boolean isNetworkConnected()
    {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void connectNetwork()
    {
        try
        {
            URL url = new URL("http://httpbin.org/stream/50");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            final StringBuilder builder = new StringBuilder();
            String line;

            while ((line = r.readLine()) != null)
            {
                builder.append(line);
                builder.append("\n");
            }

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mTextView.setText(builder.toString());
                }
            });

            urlConnection.disconnect();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
