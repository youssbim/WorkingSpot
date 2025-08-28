package com.unimib.workingspot.source.user.concretes;

import static com.unimib.workingspot.ui.main.MainActivity.TAG;
import static com.unimib.workingspot.util.constants.AccountConstants.*;
import static com.unimib.workingspot.util.constants.Constants.*;
import static com.unimib.workingspot.util.constants.DataStoreConstants.*;
import static com.unimib.workingspot.util.constants.AuthenticationConstants.*;

import android.util.Log;

import com.unimib.workingspot.model.User;
import com.unimib.workingspot.source.user.abstracts.BaseUserLocalDataSource;
import com.unimib.workingspot.util.data_store.DataStoreManagerSingleton;

/**
 * Concrete implementation of BaseUserLocalDataSource.
 * Handles local caching of user-related data (profile picture, username, general key-value resources)
 * using a DataStoreManagerSingleton.
 * Uses callback interface to notify success/failure asynchronously.
 */
public class UserLocalDataSource extends BaseUserLocalDataSource {

    /**
     Singleton instance to manage DataStore operations (local key-value storage)
     */
    private final DataStoreManagerSingleton dataStoreManager;

    /**
     * Constructor takes DataStoreManagerSingleton instance for dependency injection.
     */
    public UserLocalDataSource(DataStoreManagerSingleton dataStoreManager) {
        super();
        this.dataStoreManager = dataStoreManager;
    }

    /**
     * Saves a resource (key-value pair) to local cache.
     * Calls userCallback.onSuccessFromLocal if successful,
     * otherwise logs and calls onFailureFromLocal with error message.
     */
    @Override
    public void setCacheResource(String key, String value) {
        dataStoreManager.createDataStoreResource(key, value,
                () -> userCallback.onSuccessFromLocal(key),
                throwable -> {
                    userCallback.onFailureFromLocal(throwable.getMessage() + key);
                    Log.e(TAG, RESOURCE_ERROR_SAVE, throwable);
                }
        );
    }

    /**
     * Retrieves a cached resource by key.
     * If empty string, calls onFailureFromLocal, otherwise onSuccessFromLocal.
     * Logs errors if retrieval fails.
     */
    @Override
    public void getCacheResource(String key) {
        dataStoreManager.getResource(key,
                resource -> {
                    if(resource.equals(EMPTY)){
                        userCallback.onFailureFromLocal(EMPTY);
                    } else {
                        userCallback.onSuccessFromLocal(resource);
                    }
                },
                throwable -> {
                    Log.e(TAG, DATASTORE_SAVING_ERROR, throwable);
                    userCallback.onFailureFromLocal(throwable.getMessage() + key);
                }
        );
    }

    /**
     * Updates the cached profile picture within the stored User object.
     * Reads the User from cache, updates the profile picture, then writes back.
     * Notifies success or failure via callback.
     */
    @Override
    public void updateProfilePhotoInCache(String newEncodedProfilePicture) {
        dataStoreManager.getResource(USER,
                resource -> {
                    if (!resource.equals(EMPTY)) {
                        User user = User.userFromString(resource); // Deserialize user string
                        user.setProfilePicture(newEncodedProfilePicture); // Update profile photo
                        dataStoreManager.createDataStoreResource(USER, user.toString(),
                                () -> userCallback.onSuccessFromLocal(PROFILE_PHOTO),
                                throwable -> userCallback.onFailureFromLocal(PROFILE_PICTURE_CACHE_UPDATE_ERROR + throwable.getMessage())
                        );
                    }
                },
                throwable -> userCallback.onFailureFromLocal(CACHE_READING_ERROR + throwable.getMessage())
        );
    }

    /**
     * Updates the cached username within the stored User object.
     * Reads the User from cache, updates the username, then writes back.
     * Notifies success or failure via callback.
     */
    @Override
    public void updateUsernameInCache(String newUsername) {
        dataStoreManager.getResource(USER,
                resource -> {
                    if (!resource.equals(EMPTY)) {
                        User user = User.userFromString(resource); // Deserialize user string
                        user.setUsername(newUsername); // Update username
                        dataStoreManager.createDataStoreResource(USER, user.toString(),
                                () -> userCallback.onSuccessFromLocal(USERNAME),
                                throwable -> userCallback.onFailureFromLocal(USERNAME_CACHE_UPDATE_ERROR + throwable.getMessage())
                        );
                    }
                },
                throwable -> userCallback.onFailureFromLocal(CACHE_READING_ERROR + throwable.getMessage())
        );
    }

    /**
     * Deletes a cached resource by key.
     * Notifies success or failure and logs appropriately.
     */
    @Override
    public void deleteCacheResource(String key) {
        dataStoreManager.clearDataStoreResource(key,
                resourceKey -> {
                    userCallback.onSuccessFromLocal(EMPTY);
                    Log.d(TAG, resourceKey + DATASTORE_RESOURCE_INITIALIZATION_CORRECTLY_CLEARED + key);
                },
                throwable -> {
                    userCallback.onFailureFromLocal(throwable.getMessage() + key);
                    Log.e(TAG, DATASTORE_RESOURCE_CLEAR_ERROR, throwable);
                }
        );
    }

    /**
     * Clears all disposables or active processes related to data store management.
     * Typically called to clean up resources.
     */
    @Override
    public void clearCache() {
        dataStoreManager.clearDisposable();
    }
}
