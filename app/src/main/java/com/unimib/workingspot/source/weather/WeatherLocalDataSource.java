package com.unimib.workingspot.source.weather;

import android.app.Application;

import com.unimib.workingspot.model.weather.Weather;
import com.unimib.workingspot.util.data_store.DataStoreManagerSingleton;
import static com.unimib.workingspot.util.constants.Constants.DATASTORE;
import static com.unimib.workingspot.util.constants.DataStoreConstants.DATASTORE_RESOURCE_NOT_CORRECTLY_RETRIEVED;
import static com.unimib.workingspot.util.constants.DataStoreConstants.DATASTORE_RESOURCE_NOT_FOUND;
import static com.unimib.workingspot.util.constants.DataStoreConstants.DATASTORE_SAVING_ERROR;
import static com.unimib.workingspot.util.constants.DataStoreConstants.DATASTORE_WEATHER_KEY;
import static com.unimib.workingspot.util.constants.DataStoreConstants.DATASTORE_WEATHER_LAST_UPDATED_KEY;

/**
 * Concrete implementation of the {@link BaseWeatherLocalDataSource} for interacting with
 * the weather data saved in cache.
 */
public class WeatherLocalDataSource extends BaseWeatherLocalDataSource {

    // Instance of the DataStoreManager used to interact with the local cache
    private final DataStoreManagerSingleton dataStoreManager;

    /**
     * Constructor for initializing the WeatherLocalDataSource
     * @param application The application context
     */
    public WeatherLocalDataSource(Application application) {
        setApplication(application);
        dataStoreManager = DataStoreManagerSingleton.getInstance(application, DATASTORE);
    }
    /**
     * Fetches weather data from the local cache. Concrete implementation of
     * {@link  BaseWeatherLocalDataSource#getWeather()}
     */
    @Override
    public void getWeather() {
        dataStoreManager.getResource(DATASTORE_WEATHER_KEY, resource -> {
                    if(resource.equals(DATASTORE_RESOURCE_NOT_FOUND)) {
                        weatherResponseCallback.onNoWeatherDataFromCache();
                    } else {
                        weatherResponseCallback.onSuccessFromLocal(Weather.parseFromWeatherInfo(resource));
                    }
                }, throwable -> weatherResponseCallback.onErrorFromLocal(new Exception(DATASTORE_RESOURCE_NOT_CORRECTLY_RETRIEVED)));
    }
    /**
     * Saves weather data to the local cache. Concrete implementation of {@link BaseWeatherLocalDataSource#saveWeatherInfo(Weather)}
     * @param weather The weather data to be saved
     */
    @Override
    public void saveWeatherInfo(Weather weather) {
        dataStoreManager.createDataStoreResource(DATASTORE_WEATHER_LAST_UPDATED_KEY, String.valueOf(System.currentTimeMillis()),
                        () -> onSuccessSaveLastTimeFetched(weather),
                        throwable -> weatherResponseCallback.onErrorFromLocal(new Exception(DATASTORE_SAVING_ERROR)));
    }
    /**
     * Saves the weather data to the local cache.
     * @param weather The weather data to be saved
     */
    private void onSuccessSaveLastTimeFetched(Weather weather) {
        dataStoreManager.createDataStoreResource(DATASTORE_WEATHER_KEY, weather.toString(),
                        () -> weatherResponseCallback.onSuccessFromLocal(weather),
                throwable -> weatherResponseCallback.onErrorFromLocal(new Exception(DATASTORE_SAVING_ERROR)));
    }
}
