package com.unimib.workingspot.model.weather;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import static com.unimib.workingspot.util.constants.Constants.PIPE_REGEX;
import static com.unimib.workingspot.util.constants.WeatherConstants.DAY_FLAG_SERIALIZED_NAME;
import static com.unimib.workingspot.util.constants.WeatherConstants.TEMPERATURE_CELSIUS_SERIALIZED_NAME;
import static com.unimib.workingspot.util.constants.Constants.PIPE;

/**
 * This class represents the current weather condition fetched from WeatherAPI.
 * It contains the temperature, the weather condition (code and description),
 * whether it is day or night, and the last time the data was updated
 * (<a href="https://www.weatherapi.com/docs/">Weather API docs</a>)
 */
public class Weather {
    @SerializedName(TEMPERATURE_CELSIUS_SERIALIZED_NAME)
    private double temperature;
    @SerializedName(DAY_FLAG_SERIALIZED_NAME)
    private int isDay;
    private WeatherCondition condition; // Weather condition details

    /**
     * Default public constructor required for proper serialization and deserialization with Gson
     */
    public Weather() {

    }

    /**
     * Constructor used to create a Weather object with parameters
     * @param code The weather condition code
     * @param condition The weather condition description
     * @param temperature The temperature in Celsius degrees
     * @param isDay A flag representing if it is day (1) or if it is night (0)
     */
    public Weather(int code, String condition, double temperature, int isDay) {
        setCondition(new WeatherCondition(condition, code));
        setTemperature(temperature);
        setIsDay(isDay);
    }
    /**
     * Gets the weather temperature
     * @return The weather temperature in Celsius
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Sets the weather temperature
     * @param temperature The temperature value in Celsius
     */
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    /**
     * Returns whether it is currently day or night
     * @return An integer; 1 if it is day, 0 otherwise
     */
    public int isDay() {
        return isDay;
    }

    /**
     * Sets the day/night flag
     * @param day 1 if it is day, 0 otherwise
     */
    public void setIsDay(int day) {
        isDay = day;
    }

    /**
     * Gets the current weather condition
     * @return A {@link WeatherCondition} object
     */
    public WeatherCondition getWeatherCondition() {
        return condition;
    }


    /**
     * Sets the current weather condition
     * @param condition A {@link WeatherCondition} object
     */
    public void setCondition(WeatherCondition condition) {
        this.condition = condition;
    }

    /**
     * Returns a string representation of the weather object in the format:
     * "code|condition|temperature|isDay"
     * @return A pipe-separated string of weather data
     */
    @NonNull
    @Override
    public String toString() {
        return getWeatherCondition().getCode() + PIPE
                + getWeatherCondition().getCondition() + PIPE
                + getTemperature() + PIPE
                + isDay();
    }

    /**
     * Static utility method to parse a Weather object from a pipe-separated string.
     * Expected format: "code|condition|temperature|isDay"
     *
     * @param weatherInfo The string containing weather information
     * @return A new {@link Weather} object
     */
    public static Weather parseFromWeatherInfo(String weatherInfo) {
        String[] info = weatherInfo.split(PIPE_REGEX);
        return new Weather(Integer.parseInt(info[0]),
                info[1], Double.parseDouble(info[2]), Integer.parseInt(info[3]));
    }

}
