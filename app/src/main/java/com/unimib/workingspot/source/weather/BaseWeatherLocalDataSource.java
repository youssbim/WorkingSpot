package com.unimib.workingspot.source.weather;

import android.app.Application;

import com.unimib.workingspot.model.weather.Weather;
import com.unimib.workingspot.repository.weather.IWeatherResponseCallback;

/**
 * Abstract base class that represents the local data source for the weather information.
 */
public abstract class BaseWeatherLocalDataSource {

    protected IWeatherResponseCallback weatherResponseCallback;
    protected Application application;
    /**
     * Sets the weather response callback which will be used to notify the repository
     * @param weatherResponseCallback The callback interface
     */
    public void setWeatherResponseCallback(IWeatherResponseCallback weatherResponseCallback) {
        this.weatherResponseCallback = weatherResponseCallback;
    }
    /**
     * Sets the application context for the data source. This is needed to access the device cache
     * @param application The application context
     */
    public void setApplication(Application application) {
        this.application = application;
    }
    /**
     * Abstract method for fetching weather data from the local cache
     */
    public abstract void getWeather();
    /**
     * Abstract method for saving weather data to the local cache
     * @param weather The weather data to be saved
     */
    public abstract void saveWeatherInfo(Weather weather);
}
