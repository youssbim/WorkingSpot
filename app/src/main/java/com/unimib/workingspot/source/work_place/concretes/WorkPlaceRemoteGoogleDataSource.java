package com.unimib.workingspot.source.work_place.concretes;

import static com.unimib.workingspot.util.constants.WorkPlacesConstants.WORKPLACE_PHOTO_MAX_HEIGHT;
import static com.unimib.workingspot.util.constants.WorkPlacesConstants.WORKPLACE_PHOTO_MAX_WIDTH;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.unimib.workingspot.R;
import com.unimib.workingspot.model.WorkPlace;
import com.unimib.workingspot.source.work_place.abstracts.BaseWorkPlaceGoogleRemoteDataSource;
import com.unimib.workingspot.util.bitmap.BitMapManager;

import java.util.Arrays;
import java.util.List;


/**
 * Implementation of {@link BaseWorkPlaceGoogleRemoteDataSource} that uses the
 * Google Places API to fetch workplace details
 */
public class WorkPlaceRemoteGoogleDataSource extends BaseWorkPlaceGoogleRemoteDataSource{

    PlacesClient client; // Places API client

    /**
     * Constructor for initializing the WorkPlaceRemoteGoogleDataSource
     *
     * @param context The application context
     * @param apiKey  The API key for accessing Google Places services
     */
    public WorkPlaceRemoteGoogleDataSource(Context context, String apiKey, PlacesClient client) {
        if (!Places.isInitialized()) {
            this.client = client;
        }
        setContext(context);
    }


    /**
     * Finds a place based on the workplace given name.
     * @param name      The address or name to search for
     * @param isOutside Flag indicating if the workplace is outdoors
     */
    @Override
    public void findPlaceByName(String name, boolean isOutside) {
        // Initialize session token
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        // Setup the request to Google Places API
        FindAutocompletePredictionsRequest req = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token).setQuery(name).build();
        // Send the request and listen for the result
        client.findAutocompletePredictions(req)
                .addOnSuccessListener(response -> {
                    if(!response.getAutocompletePredictions().isEmpty()) {
                        String placeId = response.getAutocompletePredictions().get(0).getPlaceId();
                        fetchPlaceDetails(placeId, isOutside);
                    }
                })
                .addOnFailureListener(e -> callback.onFailureFromRemote(e));
    }

    /**
     * Fetches detailed information for a place by its ID
     * @param placeID   The place ID
     * @param isOutside Flag indicating if the workplace is outdoors
     */
    private void fetchPlaceDetails(String placeID, boolean isOutside) {
        // Create a list containing which details should be fetch
        List<Place.Field> fields = Arrays.asList(
                Place.Field.DISPLAY_NAME, Place.Field.FORMATTED_ADDRESS,
                Place.Field.LOCATION, Place.Field.PHOTO_METADATAS
        );

        // Send the request to Google Places API and listen for the result
        client.fetchPlace(FetchPlaceRequest.builder(placeID, fields).build())
                .addOnSuccessListener(fetchPlaceResponse -> {
                    if(fetchPlaceResponse != null && fetchPlaceResponse.getPlace().getLocation() != null) {
                        fetchPlaceResponse.getPlace();
                        Place place = fetchPlaceResponse.getPlace();
                        WorkPlace workPlace = new WorkPlace(
                                place.getDisplayName(),
                                place.getFormattedAddress(),
                                place.getLocation().latitude,
                                place.getLocation().longitude,
                                isOutside
                        );

                        // Checks if the response contains any photo
                        List<PhotoMetadata> photos = place.getPhotoMetadatas();
                        if (photos != null && !photos.isEmpty()) {
                            fetchPlacePhoto(photos.get(0), workPlace);
                        } else {
                            // If the response doesn't contain any photo, a fallback one will be used
                            BitMapManager bitMapManager = new BitMapManager();
                            workPlace.setB64PhotoEncoding(bitMapManager.encodeBitmap(context, null, R.drawable.no_image));
                            callback.onSuccessFetchWorkPlaceFromGoogle(workPlace);
                        }
                    }
                })
                .addOnFailureListener(e -> callback.onFailureFromRemote(e));
    }

    /**
     * Fetches the photo for a given place using photo metadata and attaches
     * the encoded bitmap to the provided {@link WorkPlace} object.
     * @param metadata  The photo metadata
     * @param workPlace The workplace to which the photo will be attached
     */
    private void fetchPlacePhoto(PhotoMetadata metadata, WorkPlace workPlace) {
        FetchPhotoRequest req = FetchPhotoRequest.builder(metadata)
                .setMaxWidth(WORKPLACE_PHOTO_MAX_WIDTH).setMaxHeight(WORKPLACE_PHOTO_MAX_HEIGHT).build();
        // Create a bitmap manager to encode the photos into a base64 string
        BitMapManager bitMapManager = new BitMapManager();

        client.fetchPhoto(req)
                .addOnSuccessListener(photoResp -> {
                    Bitmap bitmap = photoResp.getBitmap();
                    String b64String = bitMapManager.encodeBitmap(context, bitmap, R.drawable.no_image);
                    workPlace.setB64PhotoEncoding(b64String);
                    callback.onSuccessFetchWorkPlaceFromGoogle(workPlace);
                })
                .addOnFailureListener(e -> callback.onFailureFromRemote(e));
    }
}
