package com.example.android.networking.retrofit1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class MainActivity extends AppCompatActivity
{

    private ScrollView mScrollView;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.networking.retrofit1.R.layout.activity_main);

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

        retrofitTesting();
    }

    private boolean isNetworkConnected()
    {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public class User
    {
        @SerializedName("username")
        public String userName;

        @SerializedName("age")
        public int age;

        public User(String name, int age)
        {
            this.userName = name;
            this.age = age;
        }
    }

    private interface SampleClient
    {
        @GET("/users")
        Call<List<User>> getUsers();

        @GET("/users/{username}")
        Call<String> getUser(@Path("username") String userName);

        @POST("/users")
        Call<String> createUser(@Body User user);
    }

    private void retrofitTesting()
    {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();

        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                                    .baseUrl("http://android-networking.getsandbox.com")
                                    .addConverterFactory(ScalarsConverterFactory.create())
                                    .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = retrofitBuilder.client(httpClient.build())
                            .build();

        SampleClient client =  retrofit.create(SampleClient.class);

        final StringBuilder builder = new StringBuilder();

        //
        // GET demo from http://android-networking.getsandbox.com/users
        //
        Call<List<User>> getCall = client.getUsers();

        getCall.enqueue(new Callback<List<User>>()
        {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response)
            {
                List<User> userList = response.body();

                for (User user : userList)
                {
                    builder.append("username: ");
                    builder.append(user.userName);
                    builder.append("\n\n");
                }

                runOnUiThread(new Runnable()
                {
                    @Override public void run()
                    {
                        mTextView.setText(builder.toString());

                        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t)
            {
                t.printStackTrace();
            }
        });

        //
        // POST demo
        //
        User newUser = new User("Sample User " + System.currentTimeMillis()/1000, 40);

        Call<String> postCall = client.createUser(newUser);

        postCall.enqueue(new Callback<String>()
        {
            @Override
            public void onResponse(Call<String> call, Response<String> response)
            {
                String responseString = response.body();

                builder.append("createUser response: ");
                builder.append(responseString);
                builder.append("\n\n");

                runOnUiThread(new Runnable()
                {
                    @Override public void run()
                    {
                        mTextView.setText(builder.toString());

                        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t)
            {
                t.printStackTrace();
            }
        });
    }
}
