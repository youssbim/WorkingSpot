package com.unimib.workingspot.util.network;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

/**
 * Singleton class that monitors network connectivity status using Android's ConnectivityManager.
 */
public class NetworkManagerSingleton {

    private static volatile NetworkManagerSingleton INSTANCE;

    private final MutableLiveData<NetworkState> connectionStatusLiveData;

    private ConnectivityManager.NetworkCallback networkCallback;

    private final ConnectivityManager connectivityManager;

    /**
     * Returns the singleton instance of NetworkManagerSingleton.
     * Initializes it if not already done.
     */
    public static NetworkManagerSingleton getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (NetworkManagerSingleton.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NetworkManagerSingleton(application);
                }
            }
        }
        return INSTANCE;
    }

    private NetworkManagerSingleton(Application application) {
        connectionStatusLiveData = new MutableLiveData<>();
        connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * Returns LiveData that emits the current network state (ONLINE, OFFLINE, or DEFAULT).
     */
    public MutableLiveData<NetworkState> getConnectionStatusLiveData() {
        return connectionStatusLiveData;
    }

    /**
     * Utility method to check if the device is currently connected to the internet
     * with validated network capabilities.
     */
    private boolean isCurrentlyConnected() {
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
            return capabilities != null &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        }
        return false;
    }

    /**
     * Registers a network callback to listen for changes in network connectivity.
     * Updates the LiveData when connectivity is gained or lost.
     */
    public void registerNetworkCallback() {
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                if (connectionStatusLiveData.getValue() == NetworkState.OFFLINE) {
                    connectionStatusLiveData.postValue(NetworkState.ONLINE);
                }
            }

            @Override
            public void onLost(@NonNull Network network) {
                if (!isCurrentlyConnected()) {
                    connectionStatusLiveData.postValue(NetworkState.OFFLINE);
                }
            }
        };

        connectivityManager.registerDefaultNetworkCallback(networkCallback);

        connectionStatusLiveData.postValue(isCurrentlyConnected() ? NetworkState.DEFAULT : NetworkState.OFFLINE);
    }

    /**
     * Unregisters the previously registered network callback to stop listening for changes.
     */
    public void unregisterNetworkCallback() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        } catch (IllegalArgumentException ignored) {
        }
    }
}
