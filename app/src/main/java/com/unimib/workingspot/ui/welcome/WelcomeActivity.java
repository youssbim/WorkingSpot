package com.unimib.workingspot.ui.welcome;

import static com.unimib.workingspot.util.constants.Constants.SHOW_DIALOG;
import static com.unimib.workingspot.util.network.ui_methods.activities_methods.ApplyLayout.applyLayout;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unimib.workingspot.R;
import com.unimib.workingspot.util.network.NetworkManagerSingleton;
import com.unimib.workingspot.util.network.NetworkState;

public class WelcomeActivity extends AppCompatActivity {
    public static String TAG = WelcomeActivity.class.getSimpleName();
    private NetworkManagerSingleton networkManagerSingleton;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        showEmailChangeDialog();

        statusText = findViewById(R.id.offline_view);

        networkManagerSingleton = NetworkManagerSingleton.getInstance(this.getApplication());
        networkManagerSingleton.registerNetworkCallback();
        networkManagerSingleton.getConnectionStatusLiveData()
                .observe(this, networkState -> {
                    if (networkState == NetworkState.ONLINE) {
                        applyLayout(this, statusText ,Boolean.TRUE);
                    } else if (networkState == NetworkState.OFFLINE) {
                        applyLayout(this, statusText, Boolean.FALSE);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkManagerSingleton.unregisterNetworkCallback();
    }



    private void showEmailChangeDialog(){
        if (getIntent().getBooleanExtra(SHOW_DIALOG, Boolean.FALSE)) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.email_sent))
                    .setMessage(R.string.email_sent_and_logout)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    }
}