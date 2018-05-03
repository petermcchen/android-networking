package com.example.android.networking.url1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity
{
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.url_detail);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        try
        {
            String sample = "http://www.example.com:1080/docs/resource1.html?s=test&k=123";
            URL url = new URL(sample);

            StringBuilder builder = new StringBuilder();

            builder.append(sample + "\n");
            builder.append("\n");
            builder.append("protocol: " + url.getProtocol() + "\n");
            builder.append("host: " + url.getHost() + "\n");
            builder.append("port: " + url.getPort() + "\n");
            builder.append("path: " + url.getPath() + "\n");
            builder.append("file: " + url.getFile() + "\n");
            builder.append("query: " + url.getQuery() + "\n");

            mTextView.setText(builder.toString());
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

    }
}
