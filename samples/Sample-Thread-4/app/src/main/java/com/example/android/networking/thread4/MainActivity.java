package com.example.android.networking.thread4;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
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

    private static final int MSG_DOWNLOAD = 1;

    private ScrollView mScrollView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                mNetworkHandler.obtainMessage(MSG_DOWNLOAD, builder.toString()).sendToTarget();

                Thread.sleep(100);
            }

            urlConnection.disconnect();
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private Handler mNetworkHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_DOWNLOAD:
                {
                    final String result = (String) msg.obj;

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mTextView.setText(result);
                            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });

                    break;
                }

                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };
}
