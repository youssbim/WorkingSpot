package com.unimib.workingspot.util.data_store;

import static com.unimib.workingspot.util.constants.DataStoreConstants.*;

import android.app.Application;
import android.util.Log;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * Singleton manager for handling data storage using Jetpack DataStore (RxJava3 version).
 * Provides basic CRUD operations and manages disposable resources.
 */
public class DataStoreManagerSingleton {

    private static volatile DataStoreManagerSingleton INSTANCE;
    private final CompositeDisposable dataStoreDisposable;

    private final RxDataStore<Preferences> dataStore;

    private static final String TAG = DataStoreManagerSingleton.class.getSimpleName();

    /**
     * Private constructor to enforce singleton pattern.
     *
     * @param application The application context used to create the data store.
     * @param name        The name of the data store file.
     */
    private DataStoreManagerSingleton(Application application, String name) {
        dataStore = new RxPreferenceDataStoreBuilder(application, name).build();
        dataStoreDisposable = new CompositeDisposable();
    }

    /**
     * Retrieves the singleton instance of DataStoreManagerSingleton.
     *
     * @param application Application context.
     * @param name        Name of the DataStore file.
     * @return Singleton instance.
     */
    public static DataStoreManagerSingleton getInstance(Application application, String name) {
        if (INSTANCE == null) {
            synchronized (DataStoreManagerSingleton.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DataStoreManagerSingleton(application, name);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Clears all active disposables to avoid memory leaks.
     */
    public void clearDisposable() {
        dataStoreDisposable.clear();
    }

    /**
     * Saves a resource (key-value pair) to the DataStore.
     *
     * @param key       The key under which the resource is saved.
     * @param resource  The string value to save.
     * @param onSuccess Callback for successful save.
     * @param onFailure Callback for failure.
     */
    public void createDataStoreResource(final String key, final String resource,
                                        DataStoreCallbacks.OnSuccessCreation onSuccess,
                                        DataStoreCallbacks.OnFailure onFailure) {
        Preferences.Key<String> userkey = PreferencesKeys.stringKey(key);

        Disposable disposable = dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(userkey, resource);
            return Single.just(mutablePreferences);
        }).subscribe(
                preferences -> {
                    Log.d(TAG, DATASTORE_RESOURCE_SUCCESSFULLY_SAVED + resource);
                    if (onSuccess != null) onSuccess.onSuccess();
                },
                throwable -> {
                    Log.e(TAG, DATASTORE_SAVING_ERROR, throwable);
                    if (onFailure != null) onFailure.onError(throwable);
                }
        );

        dataStoreDisposable.add(disposable);
    }

    /**
     * Retrieves a value from the DataStore for the given key.
     *
     * @param key       The key whose value is to be retrieved.
     * @param onSuccess Callback with the retrieved value.
     * @param onFailure Callback if retrieval fails.
     */
    public void getResource(final String key,
                            DataStoreCallbacks.OnSuccessRetrieve onSuccess,
                            DataStoreCallbacks.OnFailure onFailure) {
        Preferences.Key<String> userKey = PreferencesKeys.stringKey(key);

        Disposable disposable = dataStore.data()
                .map(prefs -> {
                    String resource = prefs.get(userKey);
                    return resource != null ? resource : DATASTORE_RESOURCE_NOT_FOUND;
                })
                .firstOrError()
                .subscribe(
                        resource -> {
                            Log.d(TAG, DATASTORE_RESOURCE_CORRECTLY_RETRIEVED + resource);
                            if (onSuccess != null) onSuccess.onRetrieve(resource);
                        },
                        throwable -> {
                            Log.e(TAG, DATASTORE_RESOURCE_NOT_CORRECTLY_RETRIEVED, throwable);
                            if (onFailure != null) onFailure.onError(throwable);
                        }
                );

        dataStoreDisposable.add(disposable);
    }

    /**
     * Deletes a value from the DataStore by key.
     *
     * @param key       The key to remove.
     * @param onSuccess Callback if deletion succeeds.
     * @param onFailure Callback if deletion fails.
     */
    public void clearDataStoreResource(final String key,
                                       DataStoreCallbacks.OnSuccessDelete onSuccess,
                                       DataStoreCallbacks.OnFailure onFailure) {
        Preferences.Key<String> userKey = PreferencesKeys.stringKey(key);

        Disposable disposable = dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.remove(userKey);
            return Single.just(mutablePreferences);
        }).subscribe(
                preferences -> {
                    Log.d(TAG, DATASTORE_RESOURCE_CORRECTLY_CLEARED + key);
                    if (onSuccess != null) onSuccess.onDelete(key);
                },
                throwable -> {
                    Log.e(TAG, DATASTORE_RESOURCE_NOT_CORRECTLY_CLEARED + key, throwable);
                    if (onFailure != null) onFailure.onError(throwable);
                }
        );

        dataStoreDisposable.add(disposable);
    }
}
