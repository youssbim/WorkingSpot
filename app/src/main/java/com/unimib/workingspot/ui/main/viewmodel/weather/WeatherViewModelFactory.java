package com.unimib.workingspot.ui.main.viewmodel.weather;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.workingspot.repository.weather.WeatherRepository;

/**
 * Factory for creating instances of {@link WeatherViewModel}.
 */
public class WeatherViewModelFactory implements ViewModelProvider.Factory {
    private final WeatherRepository weatherRepository;

    /**
     * Constructor for initializing WeatherViewModelFactory
     * @param weatherRepository The weather repository
     */
    public WeatherViewModelFactory(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    /**
     * Creates a new instance of the specified {@link ViewModel} class
     * @param modelClass The {@code Class} object for the ViewModel to be created
     * @return An instance of {@link WeatherViewModel}
     * @param <T> The type of the ViewModel
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new WeatherViewModel(weatherRepository);
    }


}
