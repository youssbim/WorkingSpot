package com.unimib.workingspot.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;

/**
 * This class represent a workplace entity with various attributes related to a specific location
 *  such as its name, address, geographic coordinates, and whether it is inside or outside.
 *  The class is designed to be used with Firebase and Room
 */
@Entity
public class WorkPlace {

    // The unique identifier for the workplace
    @PrimaryKey
    @NonNull
    private String firebaseKey;

    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private boolean outside;


    // Flag that indicates if a workplace is saved by a user
    // The @Exclude annotation prevents it from being serialized by Firebase
    @Exclude
    private boolean saved;

    // Base 64 encoding of the workplace photo; saved as a string
    private String b64PhotoEncoding;

    /**
     * Default no-argument constructor
     */
    public WorkPlace() {}

    /**
     * Constructor for initializing a workplace with its name, address, geographic coordinates,
     * and whether it's outside or inside.
     * @param name - the name of the workplace
     * @param address - the address of the workplace
     * @param latitude - the latitude of the workplace
     * @param longitude - the longitude of the workplace
     * @param outside - a boolean indicating if the workplace is outside (true) or inside (false)
     */
    @Ignore
    public WorkPlace(String name, String address, double latitude, double longitude, boolean outside) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.outside = outside;
    }

    /**
     * Gets the unique Firebase key of the workplace.
     * @return the Firebase key
     */
    @NonNull
    public String getFirebaseKey() { return firebaseKey; }

    /**
     * Sets the unique Firebase key of the workplace
     * @param firebaseKey the Firebase key to set
     */
    public void setFirebaseKey(@NonNull String firebaseKey) { this.firebaseKey = firebaseKey; }

    /**
     * Gets the name of the workplace
     * @return the workplace name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the workplace
     * @param name the name of the workplace to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the address of the workplace
     * @return the workplace address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the workplace
     * @param address the address of the workplace to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Checks whether the workplace is outside
     * @return true if the workplace is outside, false if it is inside
     */
    public boolean isOutside() {
        return outside;
    }

    /**
     * Sets whether the workplace is outside or inside
     * @param outside true for outside, false for inside
     */
    public void setOutside(boolean outside) {
        this.outside = outside;
    }

    /**
     * Checks if the workplace is saved by the user.
     * @return true if the workplace is saved, false otherwise.
     */
    @Exclude // Excludes this method from Firebase serialization.
    public boolean isSaved() {
        return saved;
    }

    /**
     * Sets whether the workplace is saved by the user.
     * @param saved - true to mark the workplace as saved, false to un-save it.
     */
    @Exclude // Excludes this method from Firebase serialization.
    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    /**
     * Gets the latitude of the workplace.
     * @return the latitude of the workplace.
     */
    public double getLatitude() { return this.latitude; }

    /**
     * Sets the latitude of the workplace.
     * @param latitude - the latitude to set.
     */
    public void setLatitude(double latitude) { this.latitude = latitude; }

    /**
     * Gets the longitude of the workplace.
     * @return the longitude of the workplace.
     */
    public double getLongitude() { return this.longitude; }

    /**
     * Sets the longitude of the workplace.
     * @param longitude - the longitude to set.
     */
    public void setLongitude(double longitude) { this.longitude = longitude; }

    /**
     * Gets the Base64-encoded photo of the workplace.
     * @return the Base64 photo encoding string
     */
    public String getB64PhotoEncoding() { return b64PhotoEncoding; }

    /**
     * Sets the Base64-encoded photo of the workplace.
     *
     * @param b64PhotoEncoding - the Base64 photo encoding string to set.
     */
    public void setB64PhotoEncoding(String b64PhotoEncoding) { this.b64PhotoEncoding = b64PhotoEncoding; }

    /**
     * Compares this workplace to another object.
     * Two workplaces are considered equal if they have the same latitude and longitude
     * @param obj - the object to compare with.
     * @return true if the objects are the same workplace, false otherwise.
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == this)
            return true;
        if(!(obj instanceof WorkPlace workPlace))
            return false;
        return workPlace.getLatitude() == this.getLatitude() &&
                workPlace.getLongitude() == this.getLongitude();
    }
}