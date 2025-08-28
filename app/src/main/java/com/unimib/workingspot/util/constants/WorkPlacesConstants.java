package com.unimib.workingspot.util.constants;

/**
 * Class that defines constants related to the WorkPlaces.
 */
public class WorkPlacesConstants {
    /** API key for authenticating requests to Google Places API */
    public static final String GOOGLE_PLACES_API_KEY = "Insert key here";
    /** Google Maps API link format used for navigation, with latitude and longitude placeholders */
    public static String GOOGLE_MAPS_API_LINK_FORMATTED = "https://www.google.com/maps/dir/?api=1&destination=%f,%f";

    /** Workplace Room Database current version */
    public static final int WORKPLACE_DATABASE_VERSION = 10;
    /** Workplace Room Database name */
    public static final String WORKPLACES_DB_NAME = "saved_work_places_db";

    /** Base URL for Firebase Realtime Database */
    public static final String REALTIME_DATABASE_BASE_URL = "YOUR_FIREBASE_DATABASE_URL";
    /** Root location for saved workplaces in Firebase Realtime Database */
    public static final String FIREBASE_WORKPLACES_SAVED_ROOT_LOCATION = "saved";
    /** Path for user-specific saved workplaces in Firebase Realtime Database */
    public static final String FIREBASE_WORKPLACES_USER_SAVED_LOCATION = "saved-work-places";
    /** Root location for all workplaces in Firebase Realtime Database */
    public static final String FIREBASE_WORKPLACES_ROOT_LOCATION = "workplaces";
    /** Error message used when a Firebase request times out */
    public static final String FIREBASE_TIMEOUT_MESSAGE = "Request timed out!";
    /** Timeout duration (in milliseconds) for fetching workplace data from Firebase (20 seconds) */
    public static final long FIREBASE_WORKPLACE_FETCH_TIME_UNTIL_TIMEOUT = 20000L;
    /** Timeout duration (in milliseconds) for fetching saved workplace data from Firebase (10 seconds) */
    public static final long FIREBASE_SAVED_WORKPLACE_FETCH_TIME_UNTIL_TIMEOUT = 10000L; // 10 seconds
    /** Maximum width (in pixels) for workplace photos */
    public static final int WORKPLACE_PHOTO_MAX_WIDTH = 800;
    /** Maximum height (in pixels) for workplace photos */
    public static final int WORKPLACE_PHOTO_MAX_HEIGHT = 600;


}
