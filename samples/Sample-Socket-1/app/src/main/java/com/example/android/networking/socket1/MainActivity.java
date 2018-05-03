package com.example.android.networking.socket1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

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
        try
        {
            Socket socket = new Socket("ptt.cc", 23);

            InputStream is = socket.getInputStream();

            BufferedInputStream bs = new BufferedInputStream(is);

            String line;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;

            while ((len = bs.read()) > 0)
            {
                baos.write(len);
            }

            line = new String(baos.toByteArray(), "BIG5");
            line = line.replaceAll("\\x1b\\[[0-9;]*[mG]", "");

            is.close();

            socket.close();

            mTextView.setText(line);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
