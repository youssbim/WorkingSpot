package com.unimib.workingspot.ui.main.viewmodel.weather;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.workingspot.model.Result;
import com.unimib.workingspot.repository.weather.WeatherRepository;

/**
 * ViewModel for managing and providing weather data to the UI.
 * This class acts as a bridge between the UI and the {@link WeatherRepository}
 * via {@link LiveData}.
 */
public class WeatherViewModel extends ViewModel {

    private final WeatherRepository weatherRepository;

    /**
     * Constructor for initializing WeatherViewModel.
     * @param weatherRepository The {@link WeatherRepository} instance.
     */
    public WeatherViewModel(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    /**
     * Initiates the fetching of weather data for a specified location.
     * @param latitude    The latitude of the location for which to fetch weather.
     * @param longitude   The longitude of the location for which to fetch weather.
     * @param lastUpdated A timestamp indicating the last time weather data was updated.
     * @return A {@link LiveData} object containing the weather data or an error state.
     */
    public LiveData<Result> getWeather(double latitude, double longitude, long lastUpdated) {
        return weatherRepository.fetchWeather(latitude, longitude, lastUpdated);
    }
}
