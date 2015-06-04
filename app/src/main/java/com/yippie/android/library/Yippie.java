package com.yippie.android.library;

import android.app.Application;

public class Yippie extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        // Initialize the singletons so their instances are bound to the application process.
        initSingletons();
    }

    protected void initSingletons()
    {
        // Initialize the instance of Singleton Class

        Singleton.initInstance(getApplicationContext());
    }
}
