package com.example.android.networking.rxjava1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
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
        setContentView(com.example.android.networking.rxjava1.R.layout.activity_main);

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
        Observable<List<User>> getUsers();

        @GET("/users/{username}")
        Observable<String> getUser(@Path("username") String userName);

        @POST("/users")
        Observable<String> createUser(@Body User user);
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
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

        Retrofit retrofit = retrofitBuilder.client(httpClient.build())
                .build();

        SampleClient client = retrofit.create(SampleClient.class);

        client.getUsers()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> failure(throwable))
                .subscribe(response -> listUsers(response));

    }

    private void listUsers(List<User> users)
    {
        StringBuilder builder = new StringBuilder();

        for (User user : users)
        {
            builder.append("username: ");
            builder.append(user.userName);
            builder.append("\n\n");
        }

        mTextView.setText(builder.toString());

        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    private void failure(Throwable t)
    {
        t.printStackTrace();
    }
}
