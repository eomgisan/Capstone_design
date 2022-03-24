package com.example.ex1;

import android.app.Application;

public class Variables extends Application {


    public static boolean isNetworkConnected() {
        return NetworkConnected;
    }

    public static void setNetworkConnected(boolean networkConnected) {
        NetworkConnected = networkConnected;
    }

    public static boolean NetworkConnected;


}
