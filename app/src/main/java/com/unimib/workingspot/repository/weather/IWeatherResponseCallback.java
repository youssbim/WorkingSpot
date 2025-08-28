package com.unimib.workingspot.repository.weather;

import com.unimib.workingspot.model.weather.Weather;
import com.unimib.workingspot.model.weather.WeatherAPIResponse;

/**
 * This interface defines the callbacks used to handle various
 * scenarios when fetching weather data from remote and local data sources
 * (<a href="https://www.weatherapi.com/docs/">Weather API docs</a>)
 */
public interface IWeatherResponseCallback {
    /**
     * Callback for when weather data is successfully fetched from the remote source
     * @param weatherAPIResponse The weather response returned by the remote API.
     */
    void onSuccessFromRemote(WeatherAPIResponse weatherAPIResponse);
    /**
     * Callback for when there is an error while fetching weather data from the remote source
     * @param exception The exception detailing the error that occurred
     */
    void onFailureFromRemote(Exception exception);
    /**
     * Callback for when weather data is successfully fetched from the local data source
     * @param weather The weather data retrieved from the local cache or database
     */
    void onSuccessFromLocal(Weather weather);
    /**
     * Callback for when there is an error while fetching weather data from the local data source
     * @param exception The exception detailing the error that occurred
     */
    void onErrorFromLocal(Exception exception);
    /**
     * Called when there is no valid weather data available in the local cache or database. This is a fallback
     * method that informs the system that the local data source does not contain the required weather data,
     * and it may need to fetch data from the remote
     */
    void onNoWeatherDataFromCache();
}
