package com.example.android.networking.okhttp_async2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
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
        setContentView(com.example.android.networking.okhttp_async2.R.layout.activity_main);

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
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://httpbin.org/stream/50")
                .build();

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final StringBuilder builder = new StringBuilder();

                builder.append(response.code()).append(" ").append(response.message()).append("\n");
                builder.append(response.body().string());

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mTextView.setText(builder.toString());
                    }
                });
            }
        });
    }
}
