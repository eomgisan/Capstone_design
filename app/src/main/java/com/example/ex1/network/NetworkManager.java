package com.example.ex1.network;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;

import com.example.ex1.Variables;

public class NetworkManager {
    Context context;
    public NetworkManager(Context context) {
        this.context = context;
    }

    // Network Check
    public void registerNetworkCallback()
    {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();

            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
                                                                   @Override
                                                                   public void onAvailable(Network network) {
                                                                       Variables.setNetworkConnected(true);
                                                                   }
                                                                   @Override
                                                                   public void onLost(Network network) {
                                                                       Variables.setNetworkConnected(false);
                                                                   }
                                                               }

            );
            Variables.setNetworkConnected(false);
        }catch (Exception e){
            Variables.setNetworkConnected(false);
        }
    }

}

