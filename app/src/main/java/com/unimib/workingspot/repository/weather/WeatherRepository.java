package com.unimib.workingspot.repository.weather;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unimib.workingspot.model.Result;
import com.unimib.workingspot.model.weather.Weather;
import com.unimib.workingspot.model.weather.WeatherAPIResponse;
import com.unimib.workingspot.source.weather.BaseWeatherLocalDataSource;
import com.unimib.workingspot.source.weather.BaseWeatherRemoteDataSource;
import static com.unimib.workingspot.util.constants.WeatherConstants.WEATHER_API_EXPIRED_THRESHOLD;

/**
 * This class is responsible for managing the retrieval of weather data. It first attempts to fetch the data
 * from the local cache (database) if available and fresh. If the cached data is expired or unavailable,
 * it fetches the weather data from the remote Weather API. This class implements a repository pattern
 * and coordinates the two Weather data sources (local and remote)
 * (<a href="https://www.weatherapi.com/docs/">Weather API docs</a>)
 */
public class WeatherRepository implements IWeatherResponseCallback {
    // LiveData object to post the weather data or errors
    private final MutableLiveData<Result> weatherLiveData;

    // Data sources for fetching weather data
    private final BaseWeatherRemoteDataSource weatherRemoteDataSource;
    private final BaseWeatherLocalDataSource weatherLocalDataSource;

    // Latitude and Longitude to request weather data for a specific location
    private double latitude;
    private double longitude;

    /**
     * Constructor for initializing the WeatherRepository with the remote and local data sources
     * @param weatherRemoteDataSource  Data source for fetching weather from Weather API
     * @param weatherLocalDataSource Data source for fetching weather from cache
     */
    public WeatherRepository(BaseWeatherRemoteDataSource weatherRemoteDataSource,
                             BaseWeatherLocalDataSource weatherLocalDataSource) {
        weatherLiveData = new MutableLiveData<>();
        this.weatherRemoteDataSource = weatherRemoteDataSource;
        this.weatherLocalDataSource = weatherLocalDataSource;
        weatherRemoteDataSource.setWeatherResponseCallback(this);
        weatherLocalDataSource.setWeatherResponseCallback(this);
    }

    /**
     * Fetches the weather data either from local or remote sources depending on the last update time
     * @param latitude Current latitude of the device
     * @param longitude Current longitude of the device
     * @param lastUpdated The date and time of the last weather update
     * @return The {@link LiveData} containing the {@link Result} of the fetch operation
     */
        public LiveData<Result> fetchWeather(double latitude, double longitude, long lastUpdated) {
        this.latitude = latitude;
        this.longitude = longitude;
        long currentTime = System.currentTimeMillis();

        if(currentTime - lastUpdated > WEATHER_API_EXPIRED_THRESHOLD) {
            weatherRemoteDataSource.getWeather(latitude, longitude);
        } else {
            weatherLocalDataSource.getWeather();
        }

        return weatherLiveData;
    }

    @Override
    public void onSuccessFromRemote(WeatherAPIResponse weatherAPIResponse) {
        weatherLocalDataSource.saveWeatherInfo(weatherAPIResponse.getCurrentWeather());
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        weatherLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromLocal(Weather weather) {
        Result.WeatherSuccess result = new Result.WeatherSuccess(new WeatherAPIResponse(weather));
        weatherLiveData.postValue(result);
    }

    @Override
    public void onErrorFromLocal(Exception exception) {
        Result.Error result = new Result.Error(exception.getMessage());
        weatherLiveData.postValue(result);
    }
    // Callback method when there is no cached weather data; this is used in case the app cache was
    // deleted to prevent the application from displaying an error. It is
    @Override
    public void onNoWeatherDataFromCache() {
        weatherRemoteDataSource.getWeather(latitude, longitude);
    }
}
