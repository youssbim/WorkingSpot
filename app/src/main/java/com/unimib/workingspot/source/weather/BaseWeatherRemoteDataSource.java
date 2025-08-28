package com.unimib.workingspot.source.weather;

import com.unimib.workingspot.repository.weather.IWeatherResponseCallback;

/**
 * Abstract base class that represents the remote data source for the weather
 * information fetched from WeatherAPI.
 * (<a href="https://www.weatherapi.com/docs/">Weather API docs</a>)
 * */
public abstract class BaseWeatherRemoteDataSource {
    protected IWeatherResponseCallback weatherResponseCallback;
    /**
     * Sets the weather response callback which will be used to notify the repository
     * @param weatherResponseCallback The callback interface
     */
    public void setWeatherResponseCallback(IWeatherResponseCallback weatherResponseCallback) {
        this.weatherResponseCallback = weatherResponseCallback;
    }
    /**
     * Abstract method for fetching weather data from the remote data source
     * @param latitude The latitude of the location for which to fetch weather data
     * @param longitude The longitude of the location for which to fetch weather data
     */
    public abstract void getWeather(double latitude, double longitude);
}
