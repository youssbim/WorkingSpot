package com.unimib.workingspot.util.map_managing_utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import com.unimib.workingspot.R;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.unimib.workingspot.model.WorkPlace;

import java.util.Arrays;
import java.util.List;


/**
 * A utility class for common map-related operations, keeping the MapFragment cleaner.
 */
public class MapUtils {

    /**
     * Converts density-independent pixels (dp) to actual pixels (px).
     * Makes UI elements consistent across different screen densities.
     *
     * @param resources The application's {@link Resources} to get display metrics.
     * @param dp The dp value to convert.
     * @return The converted value in pixels.
     */
    public static int dpToPx(Resources resources, int dp) {
        float density = resources.getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    /**
     * Adds a marker to the Google Map.
     * This method encapsulates the marker creation logic.
     *
     * @param googleMap The {@link GoogleMap} instance where the marker will be added.
     * @param workPlace The {@link WorkPlace} object to represent with the marker.
     * @param color The hue for the marker icon (e.g., {@link BitmapDescriptorFactory#HUE_VIOLET}).
     */
    public static void addMapMarker(GoogleMap googleMap, WorkPlace workPlace, float color) {
        LatLng pos = new LatLng(workPlace.getLatitude(), workPlace.getLongitude());
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(pos)
                .title(workPlace.getName())
                .icon(BitmapDescriptorFactory.defaultMarker(color)));
        if (marker != null) {
            marker.setTag(workPlace);
        }
    }

    /**
     * Prepares and launches the Google Places Autocomplete UI.
     *
     * @param context The application {@link Context}.
     * @param autocompleteLauncher The {@link ActivityResultLauncher} used to start the Autocomplete activity.
     * @param googlePlacesApiKey Your Google Places API key.
     */
    public static void prepareAndLaunchPlaceSearch(Context context, ActivityResultLauncher<Intent> autocompleteLauncher, String googlePlacesApiKey) {
        if (!Places.isInitialized()) {
            Places.initialize(context, googlePlacesApiKey);
        }

        // Define which fields we want from the selected place
        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID, Place.Field.DISPLAY_NAME,
                Place.Field.FORMATTED_ADDRESS, Place.Field.LOCATION
        );
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(context);
        autocompleteLauncher.launch(intent);
    }

    /**
     * Displays a confirmation dialog for adding a new location.
     * This keeps the dialog creation logic out of the fragment.
     *
     * @param context The {@link Context} for the dialog.
     * @param initialName The initial name to pre-fill in the dialog.
     * @param initialAddress The initial address to pre-fill in the dialog.
     * @param listener The {@link OnLocationConfirmedListener} to handle the user's confirmation.
     */
    public static void showAddConfirmationDialog(Context context,
                                                 String initialName,
                                                 String initialAddress,
                                                 OnLocationConfirmedListener listener) {
        if (initialName == null || initialAddress == null) {
            Toast.makeText(context, R.string.location_details_incomplete, Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_location, null);
        final EditText titleEditText = dialogView.findViewById(R.id.input_title);
        final EditText subtitleEditText = dialogView.findViewById(R.id.input_subtitle);
        final CheckBox outsideCheckBox = dialogView.findViewById(R.id.input_outside);

        titleEditText.setText(initialName);
        subtitleEditText.setText(initialAddress);

        new AlertDialog.Builder(context)
                .setTitle(R.string.confirm_new_location)
                .setView(dialogView)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    String name = titleEditText.getText().toString().trim();
                    String address = subtitleEditText.getText().toString().trim();
                    boolean outside = outsideCheckBox.isChecked();
                    if (listener != null) {
                        listener.onConfirm(name, address, outside);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * An interface to define a callback for when a location is confirmed in the dialog.
     */
    public interface OnLocationConfirmedListener {
        /**
         * Called when the user confirms the location in the dialog.
         *
         * @param name The confirmed name of the location.
         * @param address The confirmed address of the location.
         * @param outside A boolean indicating if the location is considered "outside".
         */
        void onConfirm(String name, String address, boolean outside);
    }
}
