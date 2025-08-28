package com.unimib.workingspot.ui.main;

import static com.unimib.workingspot.util.network.ui_methods.activities_methods.ApplyLayout.applyLayout;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unimib.workingspot.R;
import com.unimib.workingspot.util.network.NetworkManagerSingleton;
import com.unimib.workingspot.util.network.NetworkState;

/**
 * Main activity of the application. This activity hosts the following fragments:
 * <ul>
 *     <li>{@link com.unimib.workingspot.ui.main.fragments.HomeFragment HomeFragment}</li>
 *     <li>{@link com.unimib.workingspot.ui.main.fragments.SavedWorkPlaceFragment SavedWorkPlaceFragment}</li>
 *     <li>{@link com.unimib.workingspot.ui.main.fragments.MapFragment MapFragment}</li>
 *     <li>{@code Profile fragments:}
 *      <ul>
 *          <li>{@link com.unimib.workingspot.ui.main.fragments.ProfileFragment ProfileFragment}</li>
 *          <li>{@link com.unimib.workingspot.ui.main.fragments.GuestProfileFragment GuestProfileFragment}</li>
 *          <li>{@link com.unimib.workingspot.ui.main.fragments.LoggedProfileFragment LoggedProfileFragment}</li>
 *      </ul>
 * </ul>
 */
public class MainActivity extends AppCompatActivity {
    // Tag for debug purposes
    public static String TAG = MainActivity.class.getSimpleName();
    private TextView statusText;
    private NetworkManagerSingleton networkManagerSingleton;

    /**
     * Called when the activity is created
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Connects the navbar with the navigation controller
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerViewHome);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView navbar = findViewById(R.id.bottomNavigation);
        NavigationUI.setupWithNavController(navbar, navController);

        navbar.setOnItemSelectedListener(item -> {
            NavigationUI.onNavDestinationSelected(item, navController);
            navController.popBackStack(item.getItemId(), false);
            return true;
        });

        // Setups the network manager and listens for network changes
        statusText = findViewById(R.id.offline_view);



        networkManagerSingleton = NetworkManagerSingleton.getInstance(this.getApplication());
        networkManagerSingleton.registerNetworkCallback();
        networkManagerSingleton.getConnectionStatusLiveData()
                .observe(this, networkState -> {
                    if (networkState == NetworkState.ONLINE) {
                        applyLayout(this, statusText, Boolean.TRUE);
                    } else if(networkState == NetworkState.OFFLINE){
                        applyLayout(this, statusText, Boolean.FALSE);
                    }
                });

    }

    /**
     * Called when the activity is destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkManagerSingleton.unregisterNetworkCallback();
    }

}