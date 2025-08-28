package com.unimib.workingspot.util.constants;

/**
 * Class that defines constants related to WeatherAPI.
 * (<a href="https://www.weatherapi.com/docs/">Weather API docs</a>)
 */
public class WeatherConstants {

    /** API key for authenticating requests to WeatherAPI */
    public static String WEATHER_API_KEY = "Insert key here";

    /** Serialized name for temperature in Celsius in API response */
    public final static String TEMPERATURE_CELSIUS_SERIALIZED_NAME = "temp_c";
    /** Serialized name indicating whether it is day or night in API response */
    public final static String DAY_FLAG_SERIALIZED_NAME = "is_day";
    /** Serialized name for the current weather object in API response */
    public final static String CURRENT_WEATHER_SERIALIZED_NAME = "current";
    /** Threshold time in milliseconds after which cached weather data expires (10 minutes) */
    public static final long WEATHER_API_EXPIRED_THRESHOLD = 600000; // 10 minutes in milliseconds
    /** Default value for Air Quality Index (AQI) parameter in API request */
    public static final String WEATHER_API_AQI_PARAMETER_VALUE = "no";
    /** Base URL for WeatherAPI service */
    public static final String WEATHER_API_BASE_URL = "https://api.weatherapi.com/v1/";
    /** Query parameter key for API authentication */
    public static final String WEATHER_API_KEY_QUERY = "key";
    /** Endpoint for fetching current weather data */
    public static final String WEATHER_API_CURRENT_WEATHER_ENDPOINT = "current.json";
    /** Query parameter for specifying location in API requests */
    public static final String WEATHER_API_POSITION_PARAMETER = "q";
    /** Query parameter for Air Quality Index (AQI) in API requests */
    public static final String WEATHER_API_AQI_PARAMETER = "aqi";
    /** Message used when weather data fetch operation fails */
    public static final String WEATHER_API_FAILED_TO_FETCH = "FAILED TO FETCH WEATHER DATA";
    /** Serialized name for weather condition */
    public static final String WEATHER_API_CONDITION_SERIALIZED_NAME = "text";

}
