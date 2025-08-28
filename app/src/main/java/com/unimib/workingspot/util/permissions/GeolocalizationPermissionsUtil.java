package com.unimib.workingspot.util.permissions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

/**
 * Utility class for handling geolocation permissions.
 * This class provides methods for requesting location permissions and checking
 * whether permissions have already been granted
 */
public class GeolocalizationPermissionsUtil {


    /**
     * Registers an activity result launcher to request location permissions
     * @param fragment The fragment from which the request is initiated
     * @param callback Callback interface to handle permission request results
     * @return An {@link ActivityResultLauncher} instance for requesting permissions
     */
    public static ActivityResultLauncher<String[]> registerForLocationPermissionRequest(Fragment fragment, IPermissionCallback callback) {
        return fragment.registerForActivityResult(new ActivityResultContracts
                .RequestMultiplePermissions(),
                result -> {
                    Boolean fineLocationGranted = result
                            .getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION,
                                    false);
                    Boolean coarseLocationGranted = result
                            .getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION,
                                    false);
                    if(Boolean.TRUE.equals(fineLocationGranted)
                            || Boolean.TRUE.equals(coarseLocationGranted)) {
                        callback.onPermissionRequestSuccess();
                    } else {
                        callback.onPermissionRequestFailure();
                    }
        });
    }
    /**
     * Checks if a specific permission has been granted
     * @param context The application context.
     * @param permission The permission {@link Manifest.permission} to check
     * @return {@code true} if the permission is granted, {@code false} otherwise
     */
    public static boolean hasPermission(Context context, String permission) {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }
    /**
     * Checks if the application has either fine or coarse location permissions.
     * @param context The application context
     * @return {@code true} if either fine or coarse location permission is granted, {@code false} otherwise
     */
    public static boolean hasLocationPermissions(Context context) {
        return hasPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
                hasPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
    }
}
