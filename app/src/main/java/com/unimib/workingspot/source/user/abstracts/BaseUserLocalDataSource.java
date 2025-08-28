package com.unimib.workingspot.source.user.abstracts;

import com.unimib.workingspot.repository.user.IUserResponseCallback;

/**
 * Abstract class representing a local data source for user-related operations.
 * This class defines methods to manage user data caching locally on the device,
 * such as saving, retrieving, updating, and clearing cached resources.
 */
public abstract class BaseUserLocalDataSource {

    /**
     Callback interface to handle responses related to user data operations
     */
    protected IUserResponseCallback userCallback;

    /**
     * Sets the callback to receive responses from user data operations.
     * @param userCallback The callback instance
     */
    public void setUserCallback(IUserResponseCallback userCallback){
        this.userCallback = userCallback;
    }

    /**
     * Abstract method to save a key-value pair in the local cache.
     * @param key The cache key identifying the resource
     * @param value The value to be cached
     */
    public abstract void setCacheResource(String key, String value);

    /**
     * Abstract method to retrieve a cached value by its key.
     * @param key The cache key identifying the resource
     */
    public abstract void getCacheResource(String key);

    /**
     * Abstract method to delete a cached resource by its key.
     * @param key The cache key identifying the resource to be deleted
     */
    public abstract void deleteCacheResource(String key);

    /**
     * Abstract method to update the cached profile photo with a new encoded image.
     * @param newEncodedProfilePicture The new profile picture encoded as a String
     */
    public abstract void updateProfilePhotoInCache(String newEncodedProfilePicture);

    /**
     * Abstract method to update the cached username with a new value.
     * @param newUsername The new username to be cached
     */
    public abstract void updateUsernameInCache(String newUsername);

    /**
     * Abstract method to clear all cached user data.
     */
    public abstract void clearCache();
}
