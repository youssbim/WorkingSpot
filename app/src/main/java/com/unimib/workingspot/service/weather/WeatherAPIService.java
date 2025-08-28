package com.unimib.workingspot.service.weather;

import com.unimib.workingspot.model.weather.WeatherAPIResponse;
import com.unimib.workingspot.util.constants.WeatherConstants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
/**
 * Service interface for fetching weather data from WeatherAPI.
 * This interface defines the endpoint for getting current weather information
 * (<a href="https://www.weatherapi.com/docs/">Weather API docs</a>)
 * */
public interface WeatherAPIService {
    /**
     * Fetches current weather data for a given position
     * @param apiKey The WeatherAPI key
     * @param position The location for which to retrieve the weather data
     * @param aqi The Air Quality Index (AQI) data parameter; can be set to "yes" or "no" depending if
     *            air quality data is required
     * @return A {@link Call} object that can be used to asynchronously request the weather data
     * and receive a {@link WeatherAPIResponse}, which will contain the current weather data
     */
    @GET(WeatherConstants.WEATHER_API_CURRENT_WEATHER_ENDPOINT)
    Call<WeatherAPIResponse> getWeather(
            @Query(WeatherConstants.WEATHER_API_KEY_QUERY) String apiKey,
            @Query(WeatherConstants.WEATHER_API_POSITION_PARAMETER) String position,
            @Query(WeatherConstants.WEATHER_API_AQI_PARAMETER) String aqi
    );
}
