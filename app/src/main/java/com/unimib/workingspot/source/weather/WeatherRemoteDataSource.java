package com.unimib.workingspot.source.weather;

import androidx.annotation.NonNull;

import com.unimib.workingspot.model.weather.WeatherAPIResponse;
import com.unimib.workingspot.service.weather.WeatherAPIService;

import static com.unimib.workingspot.util.constants.Constants.*;
import static com.unimib.workingspot.util.constants.WeatherConstants.WEATHER_API_AQI_PARAMETER_VALUE;

import com.unimib.workingspot.util.constants.WeatherConstants;
import com.unimib.workingspot.util.source.ServiceLocator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Concrete implementation of the {@link BaseWeatherRemoteDataSource} for fetching weather data from WeatherAPI.
 * (<a href="https://www.weatherapi.com/docs/">Weather API docs</a>)
 */
public class WeatherRemoteDataSource extends BaseWeatherRemoteDataSource {

    // Service used to interact with WeatherAPI
    private final WeatherAPIService weatherAPIService;
    // The WeatherAPI key
    private final String apiKey;
    /**
     * Constructor for initializing the WeatherRemoteDataSource
     * @param apiKey The API key
     */
    public WeatherRemoteDataSource(String apiKey) {
        this.apiKey = apiKey;
        this.weatherAPIService = ServiceLocator.getInstance().getWeatherAPIService();
    }
    /**
     * Fetches weather data from WeatherAPI. Concrete implementation of {@link BaseWeatherRemoteDataSource#getWeather(double, double)}
     * @param latitude The latitude of the location for which to fetch weather data
     * @param longitude The longitude of the location for which to fetch weather data
     */
    @Override
    public void getWeather(double latitude, double longitude) {
        // Prepare position query parameter
        String position = latitude + "," + longitude;

        Call<WeatherAPIResponse> weatherResponseCall =
                weatherAPIService.getWeather(apiKey, position, WEATHER_API_AQI_PARAMETER_VALUE);

        // Enqueue the API call and handle the response asynchronously
        weatherResponseCall.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<WeatherAPIResponse> call,
                                   @NonNull Response<WeatherAPIResponse> response) {
                if (response.body() != null && response.isSuccessful()) {
                    weatherResponseCallback
                            .onSuccessFromRemote(response.body());
                } else {
                    weatherResponseCallback
                            .onFailureFromRemote(new Exception(WeatherConstants.WEATHER_API_FAILED_TO_FETCH));
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherAPIResponse> call,
                                  @NonNull Throwable throwable) {
                weatherResponseCallback.onFailureFromRemote(new Exception(RETROFIT_ERROR));
            }
        });
    }
}
