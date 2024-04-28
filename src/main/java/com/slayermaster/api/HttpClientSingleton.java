package com.slayermaster.api;

import okhttp3.OkHttpClient;

public class HttpClientSingleton
{
    private static OkHttpClient instance;

    private HttpClientSingleton()
    {
        // Private constructor to prevent instantiation
    }

    public static synchronized OkHttpClient getInstance()
    {
        if (instance == null)
        {
            instance = new OkHttpClient.Builder()
                    // Customize your client here
                    .build();
        }
        return instance;
    }
}

