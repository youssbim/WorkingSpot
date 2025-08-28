package com.unimib.workingspot.model.weather;

import static com.unimib.workingspot.util.constants.WeatherConstants.CURRENT_WEATHER_SERIALIZED_NAME;

import com.google.gson.annotations.SerializedName;

/**
 * This class represent the response structure returned by the Weather API; it is designed to work
 * with Gson for automatic serialization and deserialization.
 * (<a href="https://www.weatherapi.com/docs/">Weather API docs</a>)
 */
public class WeatherAPIResponse {
    @SerializedName(CURRENT_WEATHER_SERIALIZED_NAME)
    private Weather currentWeather; // Current weather returned by Weather API

    /**
     * Default no-argument constructor required for Gson deserialization
     */
    public WeatherAPIResponse() {}

    /**
     * Constructs a WeatherAPIResponse containing the specified Weather object
     * @param currentWeather The current weather data
     */
    public WeatherAPIResponse(Weather currentWeather) {
        this.currentWeather = currentWeather;
    }

    /**
     * Returns the current weather data
     * @return A {@link Weather} object representing current conditions
     */
    public Weather getCurrentWeather() {
        return currentWeather;
    }

    /**
     * Sets the current weather data.
     *
     * @param currentWeather A {@link Weather} object to set
     */
    public void setCurrentWeather(Weather currentWeather) {
        this.currentWeather = currentWeather;
    }
}
