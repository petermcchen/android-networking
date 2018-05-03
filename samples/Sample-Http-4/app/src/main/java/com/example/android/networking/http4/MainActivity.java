package com.example.android.networking.http4;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                            .add("Http4 POST ", String.valueOf(Math.random()*10000+1))
                            .build();

        Request request = new Request.Builder()
                .url("http://httpbin.org/post")
                .post(body)
                .build();

        try
        {
            Response response = client.newCall(request).execute();

            StringBuilder builder = new StringBuilder();

            builder.append(response.code()).append(" ").append(response.message()).append("\n");
            builder.append(response.body().string());

            mTextView.setText(builder.toString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
