package com.example.android.networking.json1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        parseJson();
    }

    private void parseJson()
    {
        String jsonString = "{\"coord\":{\"lon\":121.53,\"lat\":25.05},\"weather\":[{\"id\":803,\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"main\":{\"temp\":24.57,\"pressure\":1009,\"humidity\":78,\"temp_min\":20,\"temp_max\":28},\"id\":1668341,\"name\":\"Taipei\",\"cod\":200}";

        try
        {
            JSONObject jsonObject = new JSONObject(jsonString);

            StringBuilder builder = new StringBuilder();

            JSONArray jsonObjectArray = jsonObject.names();

            for (int i = 0; i < jsonObjectArray.length(); i++)
            {
                String name = jsonObjectArray.getString(i);
                Object value = jsonObject.get(name);

                builder.append("name = ");
                builder.append(name);
                builder.append("\n");

                builder.append("value = ");
                builder.append(value);
                builder.append("\n");

                builder.append("type = ");

                if (value instanceof String)
                {
                    builder.append("String");
                }
                else if (value instanceof Number)
                {
                    builder.append("Number");
                }
                else if (value instanceof Boolean)
                {
                    builder.append("Boolean");
                }
                else if (value instanceof JSONObject)
                {
                    builder.append("JSONObject");
                }
                else if (value instanceof JSONArray)
                {
                    builder.append("JSONArray");
                }
                else
                {
                    builder.append("Other");
                }

                builder.append("\n\n");
            }

            mTextView.setText(builder.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
}
