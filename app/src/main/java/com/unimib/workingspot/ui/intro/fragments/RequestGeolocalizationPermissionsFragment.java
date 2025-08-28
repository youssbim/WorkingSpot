package com.unimib.workingspot.ui.intro.fragments;

import android.Manifest;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unimib.workingspot.R;
import com.unimib.workingspot.ui.main.MainActivity;
import com.unimib.workingspot.util.activity_launch_utils.LaunchUtils;
import com.unimib.workingspot.util.data_store.DataStoreManagerSingleton;
import com.unimib.workingspot.util.permissions.IPermissionCallback;
import com.unimib.workingspot.util.permissions.GeolocalizationPermissionsUtil;

import static com.unimib.workingspot.util.constants.Constants.DATASTORE;
import static com.unimib.workingspot.util.constants.DataStoreConstants.DATASTORE_HAS_SEEN_INTRO_KEY;
import static com.unimib.workingspot.util.constants.DataStoreConstants.DATASTORE_SAVING_ERROR;

/**
 * {@link Fragment} that represents a screen to request geo-localization permissions.
 * This fragment is displayed only when the application is started for the first time.
 */
public class RequestGeolocalizationPermissionsFragment extends Fragment
        implements IPermissionCallback{

    // Activity result launcher for requesting geo-localization permissions
    private ActivityResultLauncher<String[]> activityResultLauncher;

    // Counter to prevent the OS from blocking the user in this fragment if the permission have been
    // request too many times
    private int timesAskedForPermissions;

    // TAG for debug purposes
    public static String TAG = RequestGeolocalizationPermissionsFragment.class.getName();

    public RequestGeolocalizationPermissionsFragment() {
        // Required empty public constructor
    }

    /**
     * Called when the fragment is created
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityResultLauncher = GeolocalizationPermissionsUtil
                .registerForLocationPermissionRequest(this, this);
        timesAskedForPermissions = 0;
    }

    /**
     * Inflates the layout for this fragment
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return The root view of this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_geolocalization_permissions, container,
                false);
    }

    /**
     * Called when the view for this fragment is created
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton requestLocationPermissionsButton = view.
                findViewById(R.id.request_location_permission_button_intro);
        requestLocationPermissionsButton.setOnClickListener(v -> {
            if(!GeolocalizationPermissionsUtil.hasLocationPermissions(requireContext()))
                activityResultLauncher.launch(new String[] {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                });
        });
    }

    /** {@inheritDoc }*/
    @Override
    public void onPermissionRequestSuccess() {
        saveHasSeenIntroFlag();
    }
    /** {@inheritDoc }*/
    @Override
    public void onPermissionRequestFailure() {
        // The OS limits the times an application can ask for permissions to 3 times.
        // This check prevents the user from being stuck in this fragment
        if(timesAskedForPermissions >= 2) {
            saveHasSeenIntroFlag();
        } else {
            // If permissions have not been granted, inform again the user that some functionalities
            // may not be available
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.geo_localization_permission_denied_dialogue_title)
                    .setMessage(R.string.geo_localization_permission_denied_dialogue_message)
                    .setPositiveButton(R.string.geo_localization_permission_denied_dialogue_positive_button_text,
                            (dialog, which) -> {
                                dialog.dismiss();
                                saveHasSeenIntroFlag();
                            })
                    .setNegativeButton(R.string.geo_localization_permission_denied_dialogue_negative_button_text,
                            (dialog, which) -> {
                                timesAskedForPermissions++;
                                dialog.dismiss();
                            })
                    .setCancelable(false)
                    .show();
        }
    }

    /**
     * Saves a flag indicating that the user has seen the intro screen.
     */
    private void saveHasSeenIntroFlag() {
        DataStoreManagerSingleton.getInstance(requireActivity().getApplication(), DATASTORE)
                .createDataStoreResource(DATASTORE_HAS_SEEN_INTRO_KEY, Boolean.TRUE.toString(),
                        () -> LaunchUtils.launchActivity(requireActivity(), requireContext(),
                                MainActivity.class),
                        throwable -> Log.e(TAG, DATASTORE_SAVING_ERROR, throwable));
    }


}